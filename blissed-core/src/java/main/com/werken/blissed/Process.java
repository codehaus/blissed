package com.werken.blissed;

/*
 $Id: Process.java,v 1.22 2002-08-15 17:37:23 bob Exp $

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

import org.apache.commons.graph.DirectedGraph;
import org.apache.commons.graph.Vertex;
import org.apache.commons.graph.Edge;

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
public class Process implements Named, Described, Activity, DirectedGraph
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

    /** Start state. */
    private State startState;

    /** All active processions in this process. */
    private Set activeProcessions;

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

        this.startState = null;

        this.activeProcessions = new HashSet();
        this.listeners = Collections.EMPTY_LIST;
    }

    // ------------------------------------------------------------
    //     Instance methods
    // ------------------------------------------------------------

    /** Retrieve the entry-point start state of this process.
     *
     *  @return The entry-point start state.
     */
    public State getStartState()
    {
        return this.startState;
    }

    /** Set the start state.
     *
     *  @param startState The start state, or <code>null</code> to
     *         remove the start state.
     */
    public void setStartState(State startState)
    {
        this.startState = startState;
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

    /** Create the data container for this process.
     *
     *  <p>
     *  The default implementation always returns
     *  <code>null</code>.
     *  </p>
     *
     *  @return The process data object.
     */
    public Object createProcessData()
    {
        return null;
    }

    /** Spawn a new instance of this process.
     *
     *  @return A new <code>Procession</code> representing the state
     *          for the new instance of this process.
     */
    public Procession spawn() 
    {
        Procession procession = new Procession( this );

        // accept( procession );

        return procession;
    }

    /** Spawn a new instance of this process.
     *
     *  @param parent The parent Procession.
     *
     *  @return A new <code>Procession</code> representing the state
     *          for the new instance of this process.
     */
    public Procession spawn(Procession parent) 
    {
        Procession procession = new Procession( this,
                                       parent );

        // accept( procession );

        return procession;
    }

    /** Accept a procession into this process.
     *
     *  @param procession The procession to accept.
     *
     *  @throws InvalidMotionException If an invalid motion occurs.
     *  @throws ActivityException If an error occurs while performing an activity.
     */
    public void accept(Procession procession) throws InvalidMotionException, ActivityException
    {
        this.activeProcessions.add( procession );

        procession.startProcess( this );

        fireProcessStarted( procession );

        getStartState().accept( procession );
    }

    /** Release a procession from this node.
     *
     *  @param procession The procession to release.
     *
     *  @throws InvalidMotionException If an invalid motion occurs.
     */
    void release(Procession procession) throws InvalidMotionException
    {
        // getFinish().release( procession );

        procession.finishProcess( this );

        fireProcessFinished( procession );

        this.activeProcessions.remove( procession );
    }

    /** Retrieve an unmodifiable set of all processions currently
     *  active within this process.
     *
     *  @return An unmodifiable <code>Set</code> of all <code>Procession</code>s
     *          that are currently active within this process.
     */
    public Set getActiveProcessions()
    {
        return Collections.unmodifiableSet( this.activeProcessions );
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
     *  @param procession The started process instance procession.
     */
    void fireProcessStarted(Procession procession)
    {
        ProcessStartedEvent event = new ProcessStartedEvent( this,
                                                             procession );
        
        Iterator listenerIter = getProcessListeners().iterator();
        ProcessListener eachListener = null;
        
        while ( listenerIter.hasNext() )
        {
            eachListener = (ProcessListener) listenerIter.next();
            
            eachListener.processStarted( event );
        }

        procession.fireProcessStarted( event );
    }

    /** Fire an event indicating that an instance of this
     *  process has finished.
     *
     *  @param procession The finished process instance procession.
     */
    void fireProcessFinished(Procession procession)
    {
        ProcessFinishedEvent event = new ProcessFinishedEvent( this,
                                                               procession );

        Iterator listenerIter = getProcessListeners().iterator();
        ProcessListener eachListener = null;

        while ( listenerIter.hasNext() )
        {
            eachListener = (ProcessListener) listenerIter.next();
            
            eachListener.processFinished( event );
        }

        procession.fireProcessFinished( event );
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

    /** Perform this activity within the specified procession.
     *
     *  @param procession The procession.
     *
     *  @throws ActivityException if an error occurs.
     */
    public void perform(Procession procession) throws ActivityException
    {
        try
        {
            accept( procession );
        }
        catch (InvalidMotionException e)
        {
            throw new ActivityException( e );
        }
    }

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
    //     org.apache.commons.graph.DirectedGraph
    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 

    public Set getVertices()
    {
        return new HashSet( this.states.values() );
    }

    public Set getEdges(Vertex vertex)
    {
        Set edges = new HashSet();

        edges.add( getInbound( vertex ) );
        edges.add( getOutbound( vertex ) );

        return edges;
    }

    public Set getVertices(Edge edge)
    {
        Set vertices = new HashSet( 2 );

        vertices.add( ((Transition)edge).getOrigin() );
        vertices.add( ((Transition)edge).getDestination() );

        return vertices;
    }

    public Set getEdges()
    {
        Iterator stateIter = this.states.values().iterator();
        State    eachState = null;

        Set edges = new HashSet();

        while ( stateIter.hasNext() )
        {
            eachState = (State) stateIter.next();

            edges.addAll( getEdges( eachState ) );
        }

        return edges;
    }

    public Set getInbound(Vertex vertex)
    {
        return ((State)vertex).getInboundTransitions();
    }

    public Set getOutbound(Vertex vertex)
    {
        return new HashSet( ((State)vertex).getTransitions() );
    }

    public Vertex getSource(Edge edge)
    {
        return ((Transition)edge).getOrigin();
    }

    public Vertex getTarget(Edge edge)
    {
        return ((Transition)edge).getDestination();
    }

}
