package com.werken.blissed;

/*
 $Id: ProcessEngine.java,v 1.6 2002-09-18 05:04:58 bob Exp $

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

import java.util.LinkedList;
import java.util.List;
import java.util.Iterator;

/** Process controller engine.
 *
 *  @author <a href="mailto:bob@eng.werken.com">bob mcwhirter</a>
 *
 *  @version $Id: ProcessEngine.java,v 1.6 2002-09-18 05:04:58 bob Exp $
 */
public class ProcessEngine implements Runnable
{
    // ------------------------------------------------------------
    //     Instance members
    // ------------------------------------------------------------

    /** Queue of <code>ProcessContext</code>s requiring checking. */
    private LinkedList queue;

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
        this.queue      = new LinkedList();
        this.numThreads = 1;
        this.shouldRun  = false;
    }

    // ------------------------------------------------------------
    //     Instance methods
    // ------------------------------------------------------------

    public void setThreads(int numThreads)
    {
        this.numThreads = numThreads;
    }

    public int getThreads()
    {
        return this.numThreads;
    }

    public synchronized void start()
    {
        if ( this.threads != null )
        {
            return;
        }

        this.threads = new ThreadGroup( "com.werken.blissed.ProcessEngine" );

        this.shouldRun = true;

        Thread thread = null;

        for ( int i = 0 ; i < this.numThreads; ++i )
        {
            thread = new Thread( this.threads,
                                 this );

            thread.start();
        }
    }

    public synchronized void stop() throws InterruptedException
    {
        if ( this.threads == null )
        {
            return;
        }

        this.shouldRun = false;

        Thread[] activeThreads = new Thread[ this.threads.activeCount() + 10 ];

        int numThreads = this.threads.enumerate( activeThreads );

        this.threads.interrupt();

        for ( int i = 0 ; i < numThreads ; ++i )
        {
            activeThreads[i].join();
        }

        this.threads = null;
    }

    public void run()
    {
        ProcessContext context = null;

        while ( this.shouldRun )
        {
            try
            {
                context = getNextToCheck();
                checkTransitions( context );
            }
            catch (InterruptedException e)
            {
                break;
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    /** Add a <code>ProcessContext</code> to the check queue.
     *
     *  @param context The process context to add to the queue.
     */
    public void addToCheckQueue(ProcessContext context)
    {
        synchronized ( this.queue )
        {
            this.queue.addLast( context );
            this.queue.notifyAll();
        }
    }

    /** Determine if this engine has a <code>ProcessContext</code>
     *  waiting in the queue.
     *
     *  @return <code>true</code> if a process context is available
     *          for transition checking, otherwise <code>false</code>.
     */
    public boolean hasContextToCheck()
    {
        synchronized ( this.queue )
        {
            return ! this.queue.isEmpty();
        }
    }
    
    /** Wait for and retrieve the next <code>ProcessContext</code>
     *  from the queue to check.
     *
     *  @return The next <code>ProcessContext</code>.
     *
     *  @throws InterruptedException If the blocking-read of the queue
     *          is interrupted before returning.
     */
    protected ProcessContext getNextToCheck() throws InterruptedException
    {
        ProcessContext context = null;

        synchronized( this.queue )
        {
            while ( true )
            {
                if ( this.queue.isEmpty() )
                {
                    this.queue.wait();
                }

                if ( ! this.queue.isEmpty() )
                {
                    context = (ProcessContext) this.queue.removeFirst();
                    break;
                }
            }
        }

        return context;
    }

    /** Spawn an instance of a <code>Process</code>.
     *
     *  @param process The process to spawn.
     *
     *  @return The <code>ProcessContext</code> representing the
     *          instance of the newly spawned process.
     *
     *  @throws InvalidMotionException If a motion error occurs while
     *          attempting to spawn the process.
     */
    public ProcessContext spawn(Process process) throws ActivityException, InvalidMotionException
    {
        return spawn( process,
                      false );
    }

    /** Spawn an instance of a <code>Process</code>.
     *
     *  @param process The process to spawn.
     *  @param async Flag indicating if processing of context should
     *               occur asynchronously, or if it should use the
     *               calling thread for motion.
     *
     *  @return The <code>ProcessContext</code> representing the
     *          instance of the newly spawned process.
     *
     *  @throws InvalidMotionException If a motion error occurs while
     *          attempting to spawn the process.
     */
    public ProcessContext spawn(Process process,
                                boolean async) throws ActivityException, InvalidMotionException
    {

        ProcessContext context = new ProcessContext( this,
                                                     process );

        startProcess( process,
                      context );

        if ( async )
        {
            addToCheckQueue( context );
        }
        else
        {
            checkTransitions( context );
        }

        return context;
    }

    /** Spawn an instance of a <code>Process</code> as a child
     *  of another instance.
     *
     *  @param process The process to spawn.
     *  @param parent The parent context.
     *
     *  @return The <code>ProcessContext</code> representing the
     *          instance of the newly spawned process.
     *
     *  @throws InvalidMotionException If a motion error occurs while
     *          attempting to spawn the process.
     */
    public ProcessContext spawn(Process process,
                                ProcessContext parent) throws ActivityException, InvalidMotionException
    {
        ProcessContext context = new ProcessContext( this,
                                                     process,
                                                     parent );

        parent.addChild( context );

        startProcess( process,
                      context );

        addToCheckQueue( context );

        return context;
    }

    /** Call another <code>Process</code> from another instance.
     *
     *  @param process The process to call.
     *  @param context The process context.
     *
     *  @throws InvalidMotionException If a motion error occurs while
     *          attempting to spawn the process.
     */
    public void call(Process process,
                     ProcessContext context) throws ActivityException, InvalidMotionException
    {
        startProcess( process,
                      context );

        checkTransitions( context );
    }

    /** Begin a <code>Process</code> for a particular
     *  <code>ProcessContext</code>.
     *
     *  @param process The process to start.
     *  @param context The process context.
     *
     *  @throws InvalidMotionException If a motion error occurs while
     *          attempting to begin a process.
     */
    protected void startProcess(Process process,
                                ProcessContext context) throws ActivityException, InvalidMotionException
    {
        State startState = process.getStartState();

        Location location = context.getLocation();

        location.startProcess( process );

        enterState( startState,
                    context );
    }

    protected void enterState(State state,
                              ProcessContext context) throws ActivityException, InvalidMotionException
    {
        Location location = context.getLocation();

        location.enterState( state );

        Activity activity = state.getActivity();

        if ( activity == null )
        {
            return;
        }

        activity.perform( context );
    }

    protected void exitState(State state,
                             ProcessContext context) throws InvalidMotionException
    {
        Location location = context.getLocation();

        location.exitState( state );
    }

    protected void finishProcess(Process process,
                                 ProcessContext context) throws InvalidMotionException
    {
        Location location = context.getLocation();

        location.finishProcess( process );
    }

    /** Check a <code>ProcessContext</code> for progress possibilities.
     *
     *  @param context The context to check.
     *
     *  @return <code>true</code> if the <code>ProcessContext</code> performed
     *          motion, otherwise <code>false</code>.
     *
     *  @throws InvalidMotionException If the transitions of the context attempt
     *          an invalid motion.
     */
    public boolean checkTransitions(ProcessContext context) throws ActivityException, InvalidMotionException
    {
        boolean transitioned = false;

      OUTTER:
        while ( true )
        {
            State currentState = context.getCurrentState();

            if ( currentState == null )
            {
                break OUTTER;
            }
            
            List       trans     = currentState.getTransitions();
            Iterator   transIter = trans.iterator();
            Transition eachTrans = null;
            
          INNER:
            while ( transIter.hasNext() )
            {
                eachTrans = (Transition) transIter.next();
                
                if ( eachTrans.testGuard( context ) )
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
     *  @throws InvalidMotionException If the process context may not
     *          transition.
     */ 
    protected void followTransition(ProcessContext context,
                                    Transition transition) throws ActivityException, InvalidMotionException
    {
        State destination = transition.getDestination();
        State origin = transition.getOrigin();

        Location location = context.getLocation();

        if ( location.getCurrentState() != origin )
        {
            throw new InvalidMotionException( "Not in state " + origin.getName() );
        }

        exitState( origin,
                   context );

        if ( destination == null )
        {
            finishProcess( context.getCurrentProcess(),
                           context );
        }
        else
        {
            enterState( destination,
                        context );
        }
    }
}