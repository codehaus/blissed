package com.werken.blissed;

/*
 $Id: State.java,v 1.3 2002-07-02 16:16:40 werken Exp $

 Copyright 2001 (C) The Werken Company. All Rights Reserved.
 
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

import com.werken.blissed.event.StateListener;
import com.werken.blissed.event.StateEnteredEvent;
import com.werken.blissed.event.StateExitedEvent;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

/** A <code>Task</code>-bearing node in the process graph.
 *
 *  <p>
 *  A <code>State</code> contains a <code>Task</code> and
 *  a sequence of one-or-more <code>Transition</code>s
 *  denoting guarded exit paths.
 *  </p>
 *
 *  @see Task
 *  @see Transition
 *  @see StateEnteredEvent
 *  @see StateExitedEvent
 *  @see StateListener
 *
 *  @author <a href="mailto:bob@eng.werken.com">bob mcwhirter</a>
 */
public class State extends Node
{
    // ------------------------------------------------------------
    //     Instance members
    // ------------------------------------------------------------

    /** The task. */
    private Task task;

    /** The exit-path transitions. */
    private List transitions;

    /** State-event listeners. */
    private List listeners;

    // ------------------------------------------------------------
    //     Constructors
    // ------------------------------------------------------------

    /** Construct.
     *
     *  @param process The process to which this node belongs.
     *  @param name The name of this node.
     *  @param description The description of this node.
     */
    State(Process process,
          String name,
          String description)
    {
        super( process,
               name,
               description );

        this.task = NoOpTask.INSTANCE;
        this.transitions = new ArrayList();
        this.listeners = Collections.EMPTY_LIST;
    }

    // ------------------------------------------------------------
    //     Instance methods
    // ------------------------------------------------------------

    /** Add an exit path transition.
     *
     *  @param transition The transition to add.
     */
    public void addTransition(Transition transition)
    {
        this.transitions.add( transition );
    }

    /** Remove an exit path transition.
     *
     *  @param transition The transition to remove.
     */
    public void removeTransition(Transition transition)
    {
        this.transitions.remove( transition );
    }

    /** Retrieve the <b>live</b> list of transitions.
     *
     *  <p>
     *  The <b>live</b> list that is returned is backed
     *  directly by the <code>State</code>.  Changes made
     *  to the list are reflected internally within the
     *  <code>State</code>.
     *  </p>
     *
     *  @return The <code>List</code> of <code>Transition</code>s.
     */
    public List getTransitions()
    {
        return this.transitions;
    }

    /** Set the <code>Task</code> for this state.
     *
     *  @param task The task.
     */
    public void setTask(Task task)
    {
        this.task = task;
    }

    /** Retrieve the <code>Task</code> for this state.
     *
     *  @return The task.
     */
    public Task getTask()
    {
        return this.task;
    }

    void testTransitions(WorkSlip workSlip)
    {
    }

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
    //     Event-listener management
    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 

    /** Add a <code>StateListener</code> to this state.
     *
     *  @param listern The listenr to add.
     */
    public void addStateListener(StateListener listener)
    {
        if ( this.listeners == Collections.EMPTY_LIST )
        {
            this.listeners = new ArrayList();
        }

        this.listeners.add( listener );
    }

    /** Remove a <code>StateListener</code> from this state.
     *
     *  @param listern The listenr to remove.
     */
    public void removeStateListener(StateListener listener)
    {
        this.listeners.remove( listener );
    }

    /** Retrieve the <b>live</b> list of <code>StateListener</code>s
     *  for this state.
     *
     *  <p>
     *  The returned <b>live</b> list is directly backed by the state.
     *  Change made to the list are immediately reflected internally
     *  within the state.
     *  </p>
     *
     *  @return The <code>List</code> of <code>StateListener</code>s.
     */
    public List getProcessListeners()
    {
        return this.listeners;
    }

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
    //     Event firing
    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 

    /** Fire an event indicating that a workslip has entered 
     *  this state.
     *
     *  @param workSlip The workslip entering this state.
     */
    void fireStateEntered(WorkSlip workSlip)
    {
        StateEnteredEvent event = new StateEnteredEvent( this,
                                                         workSlip );
        
        Iterator listenerIter = getProcessListeners().iterator();
        StateListener eachListener = null;
        
        while ( listenerIter.hasNext() )
        {
            eachListener = (StateListener) listenerIter.next();
            
            eachListener.stateEntered( event );
        }
    }

    /** Fire an event indicating that a workslip has exited 
     *  this state.
     *
     *  @param workSlip The finished process instance context.
     */
    void fireProcessFinished(WorkSlip workSlip)
    {
        StateExitedEvent event = new StateExitedEvent( this,
                                                       workSlip );

        Iterator listenerIter = getProcessListeners().iterator();
        StateListener eachListener = null;

        while ( listenerIter.hasNext() )
        {
            eachListener = (StateListener) listenerIter.next();
            
            eachListener.stateExited( event );
        }
    }

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
    //     com.werken.blissed.Node
    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 

    public void accept(WorkSlip workSlip)
    {

    }

}
