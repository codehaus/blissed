package com.werken.blissed;

/*
 $Id: ProcessEngine.java,v 1.3 2002-09-16 14:59:51 bob Exp $

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
 *  @version $Id: ProcessEngine.java,v 1.3 2002-09-16 14:59:51 bob Exp $
 */
public class ProcessEngine 
{
    // ------------------------------------------------------------
    //     Instance members
    // ------------------------------------------------------------

    /** Queue of <code>ProcessContext</code>s requiring checking. */
    private LinkedList queue;

    // ------------------------------------------------------------
    //     Constructors
    // ------------------------------------------------------------

    /** Construct.
     */
    public ProcessEngine()
    {
        // intentionally left blank
    }

    // ------------------------------------------------------------
    //     Instance methods
    // ------------------------------------------------------------

    /** Start.
     */
    public void start()
    {

    }

    /** Stop.
     */
    public void stop()
    {

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
            while ( this.queue.isEmpty() )
            {
                this.queue.wait();

                if ( ! this.queue.isEmpty() )
                {
                    context = (ProcessContext) this.queue.getFirst();
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
    public ProcessContext spawn(Process process) throws InvalidMotionException
    {
        ProcessContext context = new ProcessContext( this,
                                                     process );

        startProcess( process,
                      context );

        check( context );

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
                                ProcessContext parent) throws InvalidMotionException
    {
        ProcessContext context = new ProcessContext( this,
                                                     process,
                                                     parent );

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
                     ProcessContext context) throws InvalidMotionException
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
     *  @throws InvalidMotionException If a motion error occurs while
     *          attempting to begin a process.
     */
    protected void startProcess(Process process,
                                ProcessContext context) throws InvalidMotionException
    {
        State startState = process.getStartState();

        context.startProcess( process );
        context.enterState( startState );
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
    public boolean check(ProcessContext context) throws InvalidMotionException
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
                                    Transition transition) throws InvalidMotionException
    {
        State destination = transition.getDestination();

        context.exitState( context.getCurrentState() );

        if ( destination == null )
        {
            context.finishProcess( context.getCurrentProcess() );
        }
        else
        {
            context.enterState( destination );
        }
    }
}
