package com.werken.blissed;

/*
 $Id: State.java,v 1.16 2002-07-07 22:59:41 bob Exp $

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
import com.werken.blissed.event.ActivityStartedEvent;
import com.werken.blissed.event.ActivityFinishedEvent;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

/** A <code>Activity</code>-bearing node in the process graph.
 *
 *  <p>
 *  A <code>State</code> contains a <code>Activity</code> and
 *  a sequence of one-or-more <code>Transition</code>s
 *  denoting guarded exit paths.
 *  </p>
 *
 *  @see Activity
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

    /** The activity. */
    private Activity activity;

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

        this.activity = NoOpActivity.INSTANCE;
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
    void addTransition(Transition transition)
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

    /** Create a transition.
     *
     *  @param destination The destination of the transition.
     *  @param description Description of the transition.
     *
     *  @return The added transition.
     */
    public Transition addTransition(Node destination,
                                    String description)
    {
        return addTransition( destination,
                              null,
                              description );
    }

    /** Create a transition.
     *
     *  @param destination The destination of the transition.
     *  @param guard Guard of the transition.
     *  @param description Description of the transition.
     *
     *  @return The added transition.
     */
    public Transition addTransition(Node destination,
                                    Guard guard,
                                    String description)
    {
        Transition transition = new Transition( this,
                                                destination,
                                                guard,
                                                description );
        
        addTransition( transition );
        
        return transition;   
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

    /** Set the <code>Activity</code> for this state.
     *
     *  @param activity The activity.
     */
    public void setActivity(Activity activity)
    {
        this.activity = activity;
    }

    /** Retrieve the <code>Activity</code> for this state.
     *
     *  @return The activity.
     */
    public Activity getActivity()
    {
        return this.activity;
    }

    /** Attempt to perform some transition within the
     *  context of this state and a context.
     *
     *  @param context The Context to attempt transitioning.
     *
     *  @return <code>true</code> if a transition was followed
     *          moving the context to a new node, otherwise
     *          <code>false</code>.
     *
     *  @throws InvalidMotionException If an invalid motion occurs.
     *  @throws ActivityException If an error occurs while performing an activity.
     */
    boolean attemptTransition(Context context) throws InvalidMotionException, ActivityException
    {
        List transitions = getTransitions();

        if ( transitions.isEmpty() )
        {
            throw new NoTransitionException( this );
        }

        Iterator   transIter = transitions.iterator();
        Transition eachTrans = null;

        boolean result = false;

        while ( transIter.hasNext() )
        {
            eachTrans = (Transition) transIter.next();

            result = eachTrans.accept( context );

            if ( result )
            {
                return true;
            }
        }

        return false;
    }

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
    //     Event-listener management
    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 

    /** Add a <code>StateListener</code> to this state.
     *
     *  @param listener The listenr to add.
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
     *  @param listener The listenr to remove.
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
    public List getStateListeners()
    {
        return this.listeners;
    }

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
    //     Event firing
    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 

    /** Fire an event indicating that a context has entered 
     *  this state.
     *
     *  @param context The context entering this state.
     */
    void fireStateEntered(Context context)
    {
        StateEnteredEvent event = new StateEnteredEvent( this,
                                                         context );
        
        Iterator listenerIter = getStateListeners().iterator();
        StateListener eachListener = null;
        
        while ( listenerIter.hasNext() )
        {
            eachListener = (StateListener) listenerIter.next();
            
            eachListener.stateEntered( event );
        }

        context.fireStateEntered( event );
    }

    /** Fire an event indicating that a context has exited 
     *  this state.
     *
     *  @param context The finished process instance context.
     */
    void fireStateExited(Context context)
    {
        StateExitedEvent event = new StateExitedEvent( this,
                                                       context );
        
        Iterator listenerIter = getStateListeners().iterator();
        StateListener eachListener = null;

        while ( listenerIter.hasNext() )
        {
            eachListener = (StateListener) listenerIter.next();
            
            eachListener.stateExited( event );
        }

        context.fireStateExited( event );
    }

    /** Fire an event indicating that a context has started 
     *  this state's activity.
     *
     *  @param activity The activity.
     *  @param context The context.
     */
    void fireActivityStarted(Activity activity,
                             Context context)
    {
        ActivityStartedEvent event = new ActivityStartedEvent( this,
                                                               activity,
                                                               context );
        
        Iterator listenerIter = getStateListeners().iterator();
        StateListener eachListener = null;
        
        while ( listenerIter.hasNext() )
        {
            eachListener = (StateListener) listenerIter.next();
            
            eachListener.activityStarted( event );
        }

        context.fireActivityStarted( event );
    }

    /** Fire an event indicating that a context has finished
     *  this state's activity.
     *
     *  @param activity The activity.
     *  @param context The context.
     */
    void fireActivityFinished(Activity activity,
                              Context context)
    {
        ActivityFinishedEvent event = new ActivityFinishedEvent( this,
                                                                 activity,
                                                                 context );
        
        Iterator listenerIter = getStateListeners().iterator();
        StateListener eachListener = null;

        while ( listenerIter.hasNext() )
        {
            eachListener = (StateListener) listenerIter.next();
            
            eachListener.activityFinished( event );
        }

        context.fireActivityFinished( event );
    }
    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
    //     com.werken.blissed.Node
    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 

    /** Accept a context into this node.
     *
     *  @param context The context to accept.
     *
     *  @throws InvalidMotionException If an invalid motion occurs.
     *  @throws ActivityException If an error occurs while performing an activity.
     */
    public void accept(Context context) throws InvalidMotionException, ActivityException
    {
        super.accept( context );

        fireStateEntered( context );

        getActivity().perform( context );

        check( context );
    }

    /** Release a context from this node.
     *
     *  @param context The context to release.
     *
     *  @throws InvalidMotionException If an invalid motion occurs.
     */
    public void release(Context context) throws InvalidMotionException
    {
        super.release( context );
        fireStateExited( context );
    }

    /** Check the status of the context within this
     *  node, with a goal towards making progress.
     *
     *  @param context The context to check.
     *
     *  @throws InvalidMotionException If an invalid motion occurs.
     *  @throws ActivityException If an error occurs while performing an activity.
     */
    public void check(Context context) throws InvalidMotionException, ActivityException
    {
        attemptTransition( context );
    }

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
    //     java.lang.Object
    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 

    /** Produce a string form suitable for debugging.
     *
     *  @return A string form suitable for debugging.
     */
    public String toString()
    {
        return "[State: name=" + getName()
            + "; transitions=" + getTransitions()
            + "]";
    }

}
