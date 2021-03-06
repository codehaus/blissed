package org.codehaus.blissed;

/*
 $Id: ProcessEngine.java,v 1.5 2005-07-01 16:10:29 proyal Exp $

 Copyright 2002 (C) The Werken Company. All Rights Reserved.

 Redistribution and use of this software and associated documentation
 ("Software"), with or without modification, are permitted provided
 that the following conditions are met:

 1. Redistributions of source code must retain copyright
    statements and notices.  Redistributions must also contain a
    copy of this document.

 2. Redistributions in binary form must reproduce the
    above copyright notice, this list of conditions and the
    following disclaimer in the documentation and/or other
    materials provided with the distribution.

 3. The name "blissed" must not be used to endorse or promote
    products derived from this Software without prior written
    permission of The Werken Company.  For written permission,
    please contact bob@werken.com.

 4. Products derived from this Software may not be called "blissed"
    nor may "blissed" appear in their names without prior written
    permission of The Werken Company. blissed is a registered
    trademark of The Werken Company.

 5. Due credit should be given to the blissed Project
    (http://blissed.werken.com/).

 THIS SOFTWARE IS PROVIDED BY THE WERKEN COMPANY AND CONTRIBUTORS
 ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES, INCLUDING, BUT
 NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL
 THE WERKEN COMPANY OR ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 OF THE POSSIBILITY OF SUCH DAMAGE.

 */

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/** Process controller engine.
 *
 *  @author <a href="mailto:bob@eng.werken.com">bob mcwhirter</a>
 *
 *  @version $Id: ProcessEngine.java,v 1.5 2005-07-01 16:10:29 proyal Exp $
 */
public class ProcessEngine implements Runnable
{
    /** The Log to which logging calls will be made. */
    private static final Log log = LogFactory.getLog( ProcessEngine.class );

    // ------------------------------------------------------------
    //     Instance members
    // ------------------------------------------------------------

    /** Queue of <code>ProcessContext</code>s requiring checking. */
    private final LinkedList queue;

    /** Number of service threads. */
    private int numThreads;

    /** Runnable flag. */
    private boolean shouldRun;

    /** Service threads. */
    private ThreadGroup threads;

    // ------------------------------------------------------------
    //     Constructors
    // ------------------------------------------------------------

    /** Construct.
     */
    public ProcessEngine()
    {
        this.queue = new LinkedList();
        this.numThreads = 1;
        this.shouldRun = false;
    }

    // ------------------------------------------------------------
    //     Instance methods
    // ------------------------------------------------------------

    /** Set the number of service threads.
     *
     *  @param numThreads Number of service threads.
     */
    public void setThreads( int numThreads )
    {
        this.numThreads = numThreads;
    }

    /** Retrieve the number of service threads.
     *
     *  @return The number of service threads.
     */
    public int getThreads()
    {
        return this.numThreads;
    }

    /** Start the service threads.
     */
    public synchronized void start()
    {
        if( this.threads != null )
        {
            return;
        }

        this.shouldRun = true;

        this.threads = new ThreadGroup( "org.codehaus.blissed.ProcessEngine" );

        Thread thread = null;

        for( int i = 0; i < this.numThreads; ++i )
        {
            thread = new Thread( this.threads,
                                 this,
                                 "org.codehaus.blissed.ProcessEngine[" + i + "]" );

            thread.start();
        }
    }

    /** Stop the service threads.
     *
     *  @throws InterruptedException If the calling thread is interrupted
     *          while waiting for service threads to terminate.
     */
    public synchronized void stop() throws InterruptedException
    {
        if( this.threads == null )
        {
            return;
        }

        this.shouldRun = false;

        Thread[] activeThreads = new Thread[this.threads.activeCount() + 10];

        int numThreads = this.threads.enumerate( activeThreads );

        this.threads.interrupt();

        for( int i = 0; i < numThreads; ++i )
        {
            activeThreads[i].join();
        }

        this.threads = null;
    }

    /** Determine if this <code>ProcessEngine</code> is started.
     *
     *  @return <code>true</code> if this process engine is started
     *          and servicing the queue, otherwise <code>false</code>.
     */
    public boolean isStarted()
    {
        return ( this.shouldRun );
    }

    /** Run the service thread loop.
     */
    public void run()
    {
        QueueEntry entry = null;

        while( this.shouldRun )
        {
            try
            {
                entry = getNextToService();
            }
            catch( InterruptedException e )
            {
                break;
            }

            try
            {
                entry.service( this );
            }
            catch( Exception e )
            {
                final ProcessContext context = entry.getProcessContext();
                final ServiceFailedListener listener = context.getServiceFailedListener();

                if( null == listener )
                {
                    log.error( "Exception servicing entry", e );
                }
                else
                {
                    listener.serviceFailed( context, e );
                }
            }
        }
    }

    /** Add a <code>ProcessContext</code> to the check queue.
     *
     *  @param context The process context to add to the queue.
     */
    void addToCheckTransitionsQueue( ProcessContext context )
    {
        synchronized( this.queue )
        {
            this.queue.addLast( new CheckTransitionsEntry( context ) );
            this.queue.notifyAll();
        }
    }

    /** Add a <code>ProcessContext</code> to the process-starting queue.
     *
     *  @param process The process to start.
     *  @param context The process context to start in the process.
     */
    void addToStartProcessQueue( Process process,
                                 ProcessContext context )
    {
        synchronized( this.queue )
        {
            this.queue.addLast( new StartProcessEntry( process,
                                                       context ) );
            this.queue.notifyAll();
        }
    }

    /** Determine if this engine has a <code>ProcessContext</code>
     *  waiting in the queue.
     *
     *  @return <code>true</code> if a process context is available
     *          for servicing, otherwise <code>false</code>.
     */
    public boolean hasContextToService()
    {
        synchronized( this.queue )
        {
            return !this.queue.isEmpty();
        }
    }

    /** Wait for and retrieve the next <code>ProcessContext</code>
     *  from the queue to service.
     *
     *  @return The next <code>ProcessContext</code>.
     *
     *  @throws InterruptedException If the blocking-read of the queue
     *          is interrupted before returning.
     */
    QueueEntry getNextToService() throws InterruptedException
    {
        QueueEntry context = null;

        synchronized( this.queue )
        {
            while( true )
            {
                if( this.queue.isEmpty() )
                {
                    this.queue.wait();
                }

                if( !this.queue.isEmpty() )
                {
                    context = (QueueEntry)this.queue.removeFirst();
                    break;
                }
            }
        }

        return context;
    }

    /** Peek for and retrieve the next <code>ProcessContext</code>
     *  from the queue to service.
     *
     *  @return The next <code>ProcessContext</code> or <code>null</code>
     *          if none is available.
     */
    QueueEntry peekNextToService()
    {
        QueueEntry context = null;

        synchronized( this.queue )
        {
            if( this.queue.isEmpty() )
            {
                return null;
            }

            context = (QueueEntry)this.queue.getFirst();
        }

        return context;
    }

    /** Spawn an instance of a <code>Process</code>.
     *
     *  @param process The process to spawn.
     *  @param processData The data for the new process
     *  @param async Flag indicating if processing of context should
     *               occur asynchronously, or if it should use the
     *               calling thread for motion.
     *
     *  @return The <code>ProcessContext</code> representing the
     *          instance of the newly spawned process.
     *
     *  @throws ActivityException If an activity causes an error.
     *  @throws InvalidMotionException If a motion error occurs while
     *          attempting to spawn the process.
     */
    public ProcessContext spawn( Process process,
                                 Object processData,
                                 boolean async )
        throws ActivityException, InvalidMotionException
    {
        ProcessContext processContext = new ProcessContext( this );

        processContext.setProcessData( processData );

        if( async )
        {
            addToStartProcessQueue( process,
                                    processContext );
        }
        else
        {
            startProcess( process,
                          processContext );

            checkTransitions( processContext );
        }

        return processContext;
    }

    /** Spawn an instance of a <code>Process</code> as a child
     *  of another instance.
     *
     *  @param process The process to spawn.
     *  @param parent The parent context.
     *  @param processData The data to associate with the child process
     *
     *  @return The <code>ProcessContext</code> representing the
     *          instance of the newly spawned process.
     *
     *  @throws ActivityException If an activity causes an error.
     *  @throws InvalidMotionException If a motion error occurs while
     *          attempting to spawn the process.
     */
    public ProcessContext spawn( Process process,
                                 ProcessContext parent,
                                 Object processData )
        throws ActivityException, InvalidMotionException
    {
        if( null == parent )
        {
            throw new NullPointerException( "parent" );
        }

        ProcessContext processContext = new ProcessContext( this, parent );

        parent.addChild( processContext );
        processContext.setProcessData( processData );

        addToStartProcessQueue( process,
                                processContext );

        return processContext;
    }

    /** Call another <code>Process</code> from another instance.
     *
     *  @param process The process to call.
     *  @param context The process context.
     *
     *  @throws ActivityException If an activity causes an error.
     *  @throws InvalidMotionException If a motion error occurs while
     *          attempting to spawn the process.
     */
    public void call( Process process,
                      ProcessContext context ) throws ActivityException, InvalidMotionException
    {
        startProcess( process,
                      context );
    }

    /** Begin a <code>Process</code> for a particular
     *  <code>ProcessContext</code>.
     *
     *  @param process The process to start.
     *  @param context The process context.
     *
     *  @throws ActivityException If an activity causes an error.
     *  @throws InvalidMotionException If a motion error occurs while
     *          attempting to begin a process.
     */
    protected void startProcess( Process process,
                                 ProcessContext context )
        throws ActivityException, InvalidMotionException
    {
        State startState = process.getStartState();

        Location location = context.getLocation();

        location.startProcess( process );

        enterState( startState,
                    context );

        checkTransitions( context );
    }

    /** Enter a <code>State</code> for a particular
     *  <code>ProcessContext</code>.
     *
     *  @param state The state to enter.
     *  @param context The process context.
     *
     *  @throws ActivityException If an activity causes an error.
     *  @throws InvalidMotionException If a motion error occurs while
     *          attempting to enter a state.
     */
    protected void enterState( State state,
                               ProcessContext context )
        throws ActivityException, InvalidMotionException
    {
        Location location = context.getLocation();

        location.enterState( state );

        if( log.isDebugEnabled() ) log.debug( "Entered state " + state.getName() );

        Activity activity = state.getActivity();

        if( activity == null )
        {
            return;
        }

        if( log.isDebugEnabled() ) log.debug( "Performing activity for " + state.getName() );

        activity.perform( context );

        if( log.isDebugEnabled() ) log.debug( "Performed activity for " + state.getName() );
    }

    /** Exit a <code>State</code> for a particular
     *  <code>ProcessContext</code>.
     *
     *  @param state The state to exit.
     *  @param context The process context.
     *
     *  @throws InvalidMotionException If a motion error occurs while
     *          attempting to exit a state.
     */
    protected void exitState( State state,
                              ProcessContext context ) throws InvalidMotionException
    {
        Location location = context.getLocation();

        location.exitState( state );

        if( log.isDebugEnabled() ) log.debug( "Exited state " + state.getName() );
    }

    /** Finish a <code>Process</code> for a particular
     *  <code>ProcessContext</code>.
     *
     *  @param process The process to finish.
     *  @param context The process context.
     *
     *  @throws InvalidMotionException If a motion error occurs while
     *          attempting to finish a process.
     */
    protected void finishProcess( Process process,
                                  ProcessContext context ) throws InvalidMotionException
    {
        Location location = context.getLocation();

        location.finishProcess( process );

        if( log.isDebugEnabled() ) log.debug( "Finished process " + process.getName() );
    }

    /** Check a <code>ProcessContext</code> for progress possibilities.
     *
     *  @param context The context to check.
     *
     *  @return <code>true</code> if the <code>ProcessContext</code> performed
     *          motion, otherwise <code>false</code>.
     *
     *  @throws ActivityException If an activity causes an error.
     *  @throws InvalidMotionException If the transitions of the context attempt
     *          an invalid motion.
     */
    public boolean checkTransitions( ProcessContext context )
        throws ActivityException, InvalidMotionException
    {
        boolean transitioned = false;

        OUTTER:
          while( true )
          {
              State currentState = context.getCurrentState();

              if( currentState == null )
              {
                  break OUTTER;
              }

              List trans = currentState.getTransitions();

              if( 0 == trans.size() )
              {
                  log.warn( "No transitions from state '" + currentState.getName()
                            + "' in process '" + context.getCurrentProcess().getName() + "'" );
              }

              Iterator transIter = trans.iterator();
              Transition eachTrans = null;

              if( log.isDebugEnabled() )
                  log.debug( "Checking transitions from " + currentState.getName() );

              INNER:
                while( transIter.hasNext() )
                {
                    eachTrans = (Transition)transIter.next();

                    if( eachTrans.testGuard( context ) )
                    {
                        followTransition( context,
                                          eachTrans );

                        transitioned = true;
                        continue OUTTER;
                    }
                }

              break OUTTER;
          }

        return transitioned;
    }

    /** Cause a <code>ProcessContext</code> to follow a passing
     *  <code>Transition</code>.
     *
     *  @param context The process context.
     *  @param transition The passing transition.
     *
     *  @throws ActivityException If an activity causes an error.
     *  @throws InvalidMotionException If the process context may not
     *          transition.
     */
    protected void followTransition( ProcessContext context,
                                     Transition transition )
        throws ActivityException, InvalidMotionException
    {
        State destination = transition.getDestination();
        State origin = transition.getOrigin();

        Location location = context.getLocation();

        if( location.getCurrentState() != origin )
        {
            throw new InvalidMotionException( "Not in state " + origin.getName() );
        }

        exitState( origin,
                   context );

        if( log.isDebugEnabled() )
            log.debug( "Followed transition " + transition.getOrigin().getName()
                       + " -> " + transition.getDestination().getName() );

        Process process = context.getCurrentProcess();

        if( destination == process.getTerminalState() )
        {
            finishProcess( process,
                           context );
        }
        else
        {
            enterState( destination,
                        context );
        }
    }
}
