package com.werken.blissed;

/*
 $Id: Process.java,v 1.19 2002-07-18 05:22:50 bob Exp $

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

import com.werken.blissed.event.ProcessListener;
import com.werken.blissed.event.ProcessStartedEvent;
import com.werken.blissed.event.ProcessFinishedEvent;

import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Collections;

/** A graph of nodes and transitions in the form of a
 *  state machine.
 *
 *  @see ProcessStartedEvent
 *  @see ProcessFinishedEvent
 *  @see ProcessListener
 *
 *  @author <a href="mailto:bob@eng.werken.com">bob mcwhirter</a>
 */
public class Process implements Named, Described, Activity
{
    // ------------------------------------------------------------
    //     Instance members
    // ------------------------------------------------------------

    /** Name of this process. */
    private String name;

    /** Description of this process. */
    private String description;

    /** States in this process, indexed by <code>name</code>. */
    private Map states;

    /** Start node. */
    private Start start;

    /** Finish node. */
    private Finish finish;

    /** All active contexts in this process. */
    private Set activeContexts;

    /** Process listeners. */
    private List listeners;

    // ------------------------------------------------------------
    //     Constructors
    // ------------------------------------------------------------

    /** Construct a new process.
     *
     *  @param name The name of the process.
     *  @param description A description of the process.
     */
    public Process(String name,
                   String description)
    {
        this.name        = name;
        this.description = description;

        this.states = new HashMap();

        this.finish = new Finish( this );
        this.start  = new Start( this );

        this.activeContexts = new HashSet();
        this.listeners = Collections.EMPTY_LIST;
    }

    // ------------------------------------------------------------
    //     Instance methods
    // ------------------------------------------------------------

    /** Retrieve the entry-point start node of this process.
     *
     *  @return The entry-point start node.
     */
    public Start getStart()
    {
        return this.start;
    }

    /** Set the start state.
     *
     *  @param start The start state, or <code>null</code> to
     *         remove the start state.
     */
    public void setStartState(State start)
    {
        if ( start == null )
        {
            getStart().setDestination( getFinish() );
        }
        else
        {
            getStart().setDestination( start );
        }
    }

    /** Retrieve the exit-point finish node of this process.
     *
     *  @return The exit-point finish node.
     */
    public Finish getFinish()
    {
        return this.finish;
    }

    /** Add a state to this process.
     *
     *  @param state The state to add.
     */
    protected void addState(State state)
    {
        this.states.put( state.getName(),
                         state );
    }

    /** Retrieve a stae in this process, by name.
     *
     *  @param name The name of the stae to retrieve.
     *
     *  @return The named stae, or <code>null</code> of no stae
     *          with the given name is known to this process.
     */
    public State getState(String name)
    {
        return (State) this.states.get( name );
    }

    /** Remove a state from this process.
     *
     *  @param state The state to remove.
     */
    protected void removeState(State state)
    {
        this.states.remove( state.getName() );
    }

    /** Create a new state for this process.
     *
     *  @param name The name of the state.
     *  @param description The description of the state.
     *
     *  @return The newly added <code>State</code>.
     *
     *  @throws DuplicateStateException If a state already exists within
     *          this process with the specified name.
     */
    public State addState(String name,
                          String description) throws DuplicateStateException
    {
        if ( this.states.containsKey( name ) )
        {
            throw new DuplicateStateException( this,
                                               name );
        }

        State state = new State( this,
                                 name,
                                 description );
        
        addState( state );

        return state;
    }

    /** Spawn a new instance of this process.
     *
     *  @return A new <code>Context</code> representing the state
     *          for the new instance of this process.
     */
    public Context spawn() 
    {
        Context context = new Context( this );

        // accept( context );

        return context;
    }

    /** Spawn a new instance of this process.
     *
     *  @param parent The parent Context.
     *
     *  @return A new <code>Context</code> representing the state
     *          for the new instance of this process.
     */
    public Context spawn(Context parent) 
    {
        Context context = new Context( this,
                                       parent );

        // accept( context );

        return context;
    }

    /** Accept a context into this process.
     *
     *  @param context The context to accept.
     *
     *  @throws InvalidMotionException If an invalid motion occurs.
     *  @throws ActivityException If an error occurs while performing an activity.
     */
    public void accept(Context context) throws InvalidMotionException, ActivityException
    {
        this.activeContexts.add( context );

        context.startProcess( this );

        fireProcessStarted( context );

        getStart().accept( context );
    }

    /** Release a context from this node.
     *
     *  @param context The context to release.
     *
     *  @throws InvalidMotionException If an invalid motion occurs.
     */
    void release(Context context) throws InvalidMotionException
    {
        // getFinish().release( context );

        context.finishProcess( this );

        fireProcessFinished( context );

        this.activeContexts.remove( context );
    }

    /** Retrieve an unmodifiable set of all contexts currently
     *  active within this process.
     *
     *  @return An unmodifiable <code>Set</code> of all <code>Context</code>s
     *          that are currently active within this process.
     */
    public Set getActiveContexts()
    {
        return Collections.unmodifiableSet( this.activeContexts );
    }

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
    //     Event-listener management
    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 

    /** Add a <code>ProcessListener</code> to this process.
     *
     *  @param listener The listenr to add.
     */
    public void addProcessListener(ProcessListener listener)
    {
        if ( this.listeners == Collections.EMPTY_LIST )
        {
            this.listeners = new ArrayList();
        }

        this.listeners.add( listener );
    }

    /** Remove a <code>ProcessListener</code> from this process.
     *
     *  @param listener The listenr to remove.
     */
    public void removeProcessListener(ProcessListener listener)
    {
        this.listeners.remove( listener );
    }

    /** Retrieve the <b>live</b> list of <code>ProcessListeners</code>s
     *  for this process.
     *
     *  <p>
     *  The returned <b>live</b> list is directly backed by the process.
     *  Change made to the list are immediately reflected internally
     *  within the process.
     *  </p>
     *
     *  @return The <code>List</code> of <code>ProcessListener</code>s.
     */
    public List getProcessListeners()
    {
        return this.listeners;
    }

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
    //     Event firing
    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 

    /** Fire an event indicating that an instance of this
     *  process has started.
     *
     *  @param context The started process instance context.
     */
    void fireProcessStarted(Context context)
    {
        ProcessStartedEvent event = new ProcessStartedEvent( this,
                                                             context );
        
        Iterator listenerIter = getProcessListeners().iterator();
        ProcessListener eachListener = null;
        
        while ( listenerIter.hasNext() )
        {
            eachListener = (ProcessListener) listenerIter.next();
            
            eachListener.processStarted( event );
        }

        context.fireProcessStarted( event );
    }

    /** Fire an event indicating that an instance of this
     *  process has finished.
     *
     *  @param context The finished process instance context.
     */
    void fireProcessFinished(Context context)
    {
        ProcessFinishedEvent event = new ProcessFinishedEvent( this,
                                                               context );

        Iterator listenerIter = getProcessListeners().iterator();
        ProcessListener eachListener = null;

        while ( listenerIter.hasNext() )
        {
            eachListener = (ProcessListener) listenerIter.next();
            
            eachListener.processFinished( event );
        }

        context.fireProcessFinished( event );
    }
 
    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
    //     com.werken.blissed.Named
    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 

    /** Retrieve the name of this process.
     *
     *  @return The name of this process.
     */
    public String getName()
    {
        return this.name;
    }

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
    //     com.werken.blissed.Described
    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 

    /** Retrieve the description of this process.
     *
     *  @return The description of this process.
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
    //     com.werken.blissed.Activity
    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 

    /** Perform this activity within the specified context.
     *
     *  @param context The context.
     *
     *  @throws ActivityException if an error occurs.
     */
    public void perform(Context context) throws ActivityException
    {
        try
        {
            accept( context );
        }
        catch (InvalidMotionException e)
        {
            throw new ActivityException( e );
        }
    }

}
