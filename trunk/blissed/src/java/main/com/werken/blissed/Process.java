package com.werken.blissed;

/*
 $Id: Process.java,v 1.25 2002-09-16 15:39:50 bob Exp $

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

import org.apache.commons.graph.DirectedGraph;
import org.apache.commons.graph.Vertex;
import org.apache.commons.graph.Edge;

import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;

/** A graph of nodes and transitions in the form of a
 *  state machine.
 *
 *  @author <a href="mailto:bob@eng.werken.com">bob mcwhirter</a>
 */
public class Process implements Named, Described, DirectedGraph
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
     *
     *  @return The removed state or <code>null</code>
     *          if the state was not removed.
     */
    protected State removeState(State state)
    {
        // Since we do a name-based lookup, and multiple
        // states from multiple processes might have the
        // same name, we must first ensure that we're truly
        // attempting to remove the same state that's been
        // passed in as a parameter.
        
        State removed = getState( state.getName() );

        if ( removed == state )
        {
            return (State) this.states.remove( removed.getName() );
        }

        return null;
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

        State state = new State( name,
                                 description );
        
        addState( state );

        return state;
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

    /** Set the name of this process.
     *
     *  @param name The name of this process.
     */
    public void setName(String name)
    {
        this.name = name;
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
    //     org.apache.commons.graph.DirectedGraph
    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 

    /** Retrieve all vertices (states).
     *
     *  @return Set of all vertices (states).
     */
    public Set getVertices()
    {
        return new HashSet( this.states.values() );
    }

    /** Retrieve all edges (transitions) from a given vertex (state).
     *
     *  @param vertex The vertex (state).
     *
     *  @return The set of all inbound and outbound edges (transitions)
     *          for the vertex.
     */
    public Set getEdges(Vertex vertex)
    {
        Set edges = new HashSet();

        edges.add( getInbound( vertex ) );
        edges.add( getOutbound( vertex ) );

        return edges;
    }

    /** Retrieve all vertices (states) connected by an edge (transition)
     *
     *  @param edge The edge (transition).
     *
     *  @return The set of vertices (states) connected by the edge (transition).
     */
    public Set getVertices(Edge edge)
    {
        Set vertices = new HashSet( 2 );

        vertices.add( ((Transition)edge).getOrigin() );
        vertices.add( ((Transition)edge).getDestination() );

        return vertices;
    }

    /** Reteieve all edges (transitions).
     *
     *  @return SEt of all edges (transitions).
     */
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

    /** Retrieve all inbound edges (transitions) for a vertex (state).
     *
     *  @param vertex The vertex (state).
     *
     *  @return The set of all inbound edges (transitions).
     */
    public Set getInbound(Vertex vertex)
    {
        return ((State)vertex).getInboundTransitions();
    }

    /** Retrieve all outbound edges (transitions) for a vertex (state).
     *
     *  @param vertex The vertex (state).
     *
     *  @return The set of all outbound edges (transitions).
     */
    public Set getOutbound(Vertex vertex)
    {
        return new HashSet( ((State)vertex).getTransitions() );
    }

    /** Retrieve the source vertex (state) of an edge (transition).
     *
     *  @param edge The edge (transition).
     *
     *  @return The source vertex (state).
     */
    public Vertex getSource(Edge edge)
    {
        return ((Transition)edge).getOrigin();
    }

    /** Retrieve the target vertex (state) of an edge (transition).
     *
     *  @param edge The edge (transition).
     *
     *  @return THe target vertex (state).
     */
    public Vertex getTarget(Edge edge)
    {
        return ((Transition)edge).getDestination();
    }

}
