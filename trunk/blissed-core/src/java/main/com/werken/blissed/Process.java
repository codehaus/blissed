package com.werken.blissed;

/*
 $Id: Process.java,v 1.10 2002-07-05 03:57:12 werken Exp $

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

    /** Nodes in this process, indexed by <code>name</code>. */
    private Map nodes;

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
    Process(String name,
            String description)
    {
        this.name        = name;
        this.description = description;

        this.nodes = new HashMap();

        this.start  = new Start( this );
        this.finish = new Finish( this );

        this.start.setTransition( new Transition( this.start,
                                                  this.finish,
                                                  "default start-finish transition" ) );
        
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

    /** Retrieve the exit-point finish node of this process.
     *
     *  @return The exit-point finish node.
     */
    public Finish getFinish()
    {
        return this.finish;
    }

    /** Add a node to this process.
     *
     *  @param node The node to add.
     */
    protected void addNode(Node node)
    {
        this.nodes.put( node.getName(),
                        node );
    }

    /** Retrieve a node in this process, by name.
     *
     *  @param name The name of the node to retrieve.
     *
     *  @return The named node, or <code>null</code> of no node
     *          with the given name is known to this process.
     */
    Node getNode(String name)
    {
        return (Node) this.nodes.get( name );
    }

    /** Remove a node from this process.
     *
     *  @param node The node to remove.
     */
    void removeNode(Node node)
    {
        this.nodes.remove( node.getName() );
    }

    /** Create a new state for this process.
     *
     *  @param name The name of the state.
     *  @param description The description of the state.
     */
    public State addState(String name,
                          String description)
    {
        State state = new State( this,
                                 name,
                                 description );
        
        addNode( state );

        return state;
    }

    /** Spawn a new instance of this process.
     *
     *  @return A new <code>Context</code> representing the state
     *          for the new instance of this process.
     */
    public Context spawn() throws InvalidMotionException, ActivityException
    {
        Context context = new Context( this );

        accept( context );

        return context;
    }

    /** Spawn a new instance of this process.
     *
     *  @param parent The parent Context.
     *
     *  @return A new <code>Context</code> representing the state
     *          for the new instance of this process.
     */
    Context spawn(Context parent) throws InvalidMotionException, ActivityException
    {
        Context context = new Context( this,
                                       parent );

        accept( context );

        return context;
    }

    void accept(Context context) throws InvalidMotionException, ActivityException
    {
        this.activeContexts.add( context );

        fireProcessStarted( context );

        getStart().accept( context );
        
    }

    void release(Context context) throws InvalidMotionException
    {
        getFinish().release( context );

        fireProcessFinished( context );

        this.activeContexts.remove( context );
    }

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

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
    //     com.werken.blissed.Activity
    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 

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
