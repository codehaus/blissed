package com.werken.blissed;

/*
 $Id: State.java,v 1.18 2002-08-14 20:22:29 bob Exp $

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

import org.apache.commons.graph.Vertex;

import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.util.Collections;
import java.util.Iterator;

/** A <code>Activity</code>-bearing state in the process graph.
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
public class State implements Named, Described, Vertex
{
    // ------------------------------------------------------------
    //     Instance members
    // ------------------------------------------------------------

    /** The process to which this state belongs. */
    private Process process;

    /** The name of this. */
    private String name;

    /** The description of this state. */
    private String description;

    /** The activity. */
    private Activity activity;

    /** The exit-path transitions. */
    private List outbound;

    /** Entry-path transitions */
    private Set inbound;

    /** State-event listeners. */
    private List listeners;

    // ------------------------------------------------------------
    //     Constructors
    // ------------------------------------------------------------

    /** Construct.
     *
     *  @param process The process to which this state belongs.
     *  @param name The name of this state.
     *  @param description The description of this state.
     */
    State(Process process,
          String name,
          String description)
    {
        this.process     = process;
        this.name        = name;
        this.description = description;

        this.activity    = NoOpActivity.INSTANCE;
        this.outbound    = new ArrayList();
        this.inbound     = new HashSet();
        this.listeners   = Collections.EMPTY_LIST;
    }

    // ------------------------------------------------------------
    //     Instance methods
    // ------------------------------------------------------------

    /** Retrieve the process to which this state belongs.
     *
     *  @return The process.
     */
    public Process getProcess()
    {
        return this.process;
    }

    /** Add an exit path transition.
     *
     *  @param transition The transition to add.
     */
    void addTransition(Transition transition)
    {
        this.outbound.add( transition );

        State destination = transition.getDestination();

        if ( destination != null )
        {
            transition.getDestination().addInboundTransition( transition );
        }
    }

    /** Add an inbound path transition.
     *
     *  @param transition The transition to add.
     */
    void addInboundTransition(Transition transition)
    {
        this.inbound.add( transition );
    }

    /** Remove an exit path transition.
     *
     *  @param transition The transition to remove.
     */
    public void removeTransition(Transition transition)
    {
        this.outbound.remove( transition );
        transition.getDestination().removeInboundTransition( transition );
    }

    /** Remove an inbound path transition.
     *
     *  @param transition The transition to remove.
     */
    void removeInboundTransition(Transition transition)
    {
        this.inbound.remove( transition );
    }

    /** Create a transition.
     *
     *  @param destination The destination of the transition.
     *  @param description Description of the transition.
     *
     *  @return The added transition.
     */
    public Transition addTransition(State destination,
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
    public Transition addTransition(State destination,
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

    /** Retrieve the <b>live</b> list of outbound.
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
        return this.outbound;
    }

    /** Retrieve the set of inbound transitions
     *
     *  @return The <code>Set</code> of inbound <code>Transitions</code>.
     */
    Set getInboundTransitions()
    {
        return this.inbound;
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

    /** Accept a procession into this state.
     *
     *  @param procession The procession to accept.
     *
     *  @throws InvalidMotionException If an invalid motion occurs.
     *  @throws ActivityException If an error occurs while performing an activity.
     */
    public void accept(Procession procession) throws InvalidMotionException, ActivityException
    {
        procession.enterState( this );

        fireStateEntered( procession );

        getActivity().perform( procession );

        check( procession );
    }

    /** Release a procession from this state.
     *
     *  @param procession The procession to release.
     *
     *  @throws InvalidMotionException If an invalid motion occurs.
     */
    public void release(Procession procession) throws InvalidMotionException
    {
        procession.exitState( this );

        fireStateExited( procession );
    }

    /** Check the status of the procession within this
     *  state, with a goal towards making progress.
     *
     *  @param procession The procession to check.
     *
     *  @throws InvalidMotionException If an invalid motion occurs.
     *  @throws ActivityException If an error occurs while performing an activity.
     */
    public void check(Procession procession) throws InvalidMotionException, ActivityException
    {
        attemptTransition( procession );
    }

    /** Attempt to perform some transition within the
     *  procession of this state and a procession.
     *
     *  @param procession The Procession to attempt transitioning.
     *
     *  @return <code>true</code> if a transition was followed
     *          moving the procession to a new state, otherwise
     *          <code>false</code>.
     *
     *  @throws InvalidMotionException If an invalid motion occurs.
     *  @throws ActivityException If an error occurs while performing an activity.
     */
    boolean attemptTransition(Procession procession) throws InvalidMotionException, ActivityException
    {
        List outbound = getTransitions();

        if ( outbound.isEmpty() )
        {
            throw new NoTransitionException( this );
        }

        Iterator   transIter = outbound.iterator();
        Transition eachTrans = null;

        boolean result = false;

        while ( transIter.hasNext() )
        {
            eachTrans = (Transition) transIter.next();

            result = eachTrans.accept( procession );

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

    /** Fire an event indicating that a procession has entered 
     *  this state.
     *
     *  @param procession The procession entering this state.
     */
    void fireStateEntered(Procession procession)
    {
        StateEnteredEvent event = new StateEnteredEvent( this,
                                                         procession );
        
        Iterator listenerIter = getStateListeners().iterator();
        StateListener eachListener = null;
        
        while ( listenerIter.hasNext() )
        {
            eachListener = (StateListener) listenerIter.next();
            
            eachListener.stateEntered( event );
        }

        procession.fireStateEntered( event );
    }

    /** Fire an event indicating that a procession has exited 
     *  this state.
     *
     *  @param procession The finished process instance procession.
     */
    void fireStateExited(Procession procession)
    {
        StateExitedEvent event = new StateExitedEvent( this,
                                                       procession );
        
        Iterator listenerIter = getStateListeners().iterator();
        StateListener eachListener = null;

        while ( listenerIter.hasNext() )
        {
            eachListener = (StateListener) listenerIter.next();
            
            eachListener.stateExited( event );
        }

        procession.fireStateExited( event );
    }

    /** Fire an event indicating that a procession has started 
     *  this state's activity.
     *
     *  @param activity The activity.
     *  @param procession The procession.
     */
    void fireActivityStarted(Activity activity,
                             Procession procession)
    {
        ActivityStartedEvent event = new ActivityStartedEvent( this,
                                                               activity,
                                                               procession );
        
        Iterator listenerIter = getStateListeners().iterator();
        StateListener eachListener = null;
        
        while ( listenerIter.hasNext() )
        {
            eachListener = (StateListener) listenerIter.next();
            
            eachListener.activityStarted( event );
        }

        procession.fireActivityStarted( event );
    }

    /** Fire an event indicating that a procession has finished
     *  this state's activity.
     *
     *  @param activity The activity.
     *  @param procession The procession.
     */
    void fireActivityFinished(Activity activity,
                              Procession procession)
    {
        ActivityFinishedEvent event = new ActivityFinishedEvent( this,
                                                                 activity,
                                                                 procession );
        
        Iterator listenerIter = getStateListeners().iterator();
        StateListener eachListener = null;

        while ( listenerIter.hasNext() )
        {
            eachListener = (StateListener) listenerIter.next();
            
            eachListener.activityFinished( event );
        }

        procession.fireActivityFinished( event );
    }

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
    //     com.werken.blissed.Named
    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 

    /** Retrieve the name of this state.
     *
     *  @return The name.
     */
    public String getName()
    {
        return this.name;
    }

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
    //     com.werken.blissed.Described 
    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 

    /** Retrieve the description of this state.
     *
     *  @return The description.
     */
    public String getDescription()
    {
        return this.description;
    }

    /** Set the description
     *
     *  @param description The description.
     */
    public void setDescription(String description)
    {
        this.description = description;
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
            + "; outbound=" + getTransitions()
            + "]";
    }

}
