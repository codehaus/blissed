package com.werken.blissed;

/*
 $Id: Context.java,v 1.8 2002-07-06 21:23:38 werken Exp $

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

import com.werken.blissed.event.ContextListener;
import com.werken.blissed.event.ProcessStartedEvent;
import com.werken.blissed.event.ProcessFinishedEvent;
import com.werken.blissed.event.StateEnteredEvent;
import com.werken.blissed.event.StateExitedEvent;
import com.werken.blissed.event.TransitionFollowedEvent;

import java.util.Date;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

/** Data for an instance of a <code>Process</code>.
 *
 *  @see Process#spawn
 * 
 *  @author <a href="mailto:bob@eng.werken.com">bob mcwhirter</a>
 */
public class Context implements Named
{
    // ------------------------------------------------------------
    //     Instance members
    // ------------------------------------------------------------

    /** Name of this Context. */
    private String name;

    /** Process of which this Context is an instance. */
    private Process process;

    /** Parent Context, if this is spawned. */
    private Context parent;

    /** Children Context, if any. */
    private Set children;

    /** Instance variables. */
    private Map variables;

    /** Location tracking. */
    private Location location;

    /** Context listeners. */
    private List listeners;

    // ------------------------------------------------------------
    //     Constructors
    // ------------------------------------------------------------

    /** Construct a root <code>Context</code>.
     *
     *  @param process The process of which this Context is an instance. 
     */
    Context(Process process)
    {
        this( process,
              null );
    }

    /** Construct a nested <code>Context</code>.
     *
     *  @param process The process of which this Context is an instance. 
     *  @param parent The parent of this Context.
     */
    Context(Process process,
            Context parent)
    {
        String name = null;

        if ( name == null )
        {
            name = "";
        }
        else
        {
            name = parent.getName() + ".";
        }

        name += process.getName() + "-" + ( new Date().getTime() );

        this.name    = name;
        this.process = process;
        this.parent  = parent;

        this.variables  = new HashMap();

        this.children   = Collections.EMPTY_SET;
        this.listeners  = Collections.EMPTY_LIST;

        this.location = new Location( this );
    }

    // ------------------------------------------------------------
    //     Instance methods
    // ------------------------------------------------------------

    /** Retrieve the name of this Context.
     *
     *  @return The name of this Context.
     */
    public String getName()
    {
        return this.name;
    }

    /** Retrieve the Process of this Context.
     *
     *  @return The process of this Context.
     */
    public Process getProcess()
    {
        return this.process;
    }

    /** Retrieve the parent of this Context.
     *
     *  @return The parent of this Context, or <code>null</code>
     *          if this is a root-level Context.
     */
    public Context getParent()
    {
        return this.parent;
    }

    /** Retrieve an variable.
     *
     *  @param name The name of the variable.
     *
     *  @return The variable's value, or <code>null</code>
     *          if the variable has not been set.
     */
    public Object getVariable(String name)
    {
        return this.variables.get( name );
    }

    /** Set an variable.
     *
     *  @param name The name of the variable.
     *  @param value The value of the variable.
     */
    public void setVariable(String name,
                             Object value)
    {
        this.variables.put( name,
                             value );
    }

    /** Clean an variable.
     *
     *  @param name The name of the variable.
     */
    public void clearVariable(String name)
    {
        this.variables.remove( name );
    }

    /** Signal that this context has started a process.
     *
     *  @param process The process.
     */
    void startProcess(Process process)
    {
        this.location.startProcess( process );
    }

    /** Signal that this context has finished a process.
     *
     *  @param process The process.
     *
     *  @throws InvalidMotionException If an invalid motion occurs.
     */
    void finishProcess(Process process) throws InvalidMotionException
    {
        this.location.finishProcess( process );
    }

    /** Signal that this context has entered a node.
     *
     *  @param node The node.
     *
     *  @throws InvalidMotionException If an invalid motion occurs.
     */
    void enterNode(Node node) throws InvalidMotionException
    {
        this.location.enterNode( node );
    }

    /** Signal that this context has exited a node.
     *
     *  @param node The node.
     *
     *  @throws InvalidMotionException If an invalid motion occurs.
     */
    void exitNode(Node node) throws InvalidMotionException
    {
        this.location.exitNode( node );
    }

    /** Spawn a nested process.
     *
     *  @param process The process to spawn.
     *
     *  @return The spawned nested Context.
     *
     *  @throws InvalidMotionException If an invalid motion occurs.
     *  @throws ActivityException If an error occurs while performing an activity.
     */
    public Context spawn(Process process) throws InvalidMotionException, ActivityException
    {
        Context spawned = process.spawn( this );

        addChild( spawned );

        return spawned;
    }

    /** Add a child context to this context.
     *
     *  @param child The child to add.
     */
    void addChild(Context child)
    {
        if ( this.children == Collections.EMPTY_SET )
        {
            this.children = new HashSet();
        }

        this.children.add( child );
    }

    /** Retrieve an unmodifiable set of all children
     *  contexts of this context.
     *
     *  @return An unmodifiable <code>Set</code> of children
     *          <code>Context</code>s.
     */
    public Set getChildren()
    {
        return Collections.unmodifiableSet( this.children );
    }

    /** Perform a liveness check on this context.
     *
     *  @throws InvalidMotionException If an invalid motion occurs.
     *  @throws ActivityException If an error occurs while performing an activity.
     */
    void check() throws InvalidMotionException, ActivityException
    {
        this.location.check();
    }

    /** Retrieve the location-management object.
     *
     *  @return The location-management object.
     */
    Location getLocation()
    {
        return this.location;
    }

    /** Retrieve the context's current node location.
     *
     *  @return The context's current node location.
     */
    Node getCurrentNode()
    {
        return getLocation().getCurrentNode();
    }

    /** Retrieve the context's current process location.
     *
     *  @return The context's current process location.
     */
    Process getCurrentProcess()
    {
        return getLocation().getCurrentProcess();
    }

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
    //     Event-listener management
    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 

    /** Add a <code>ContextListener</code> to this context.
     *
     *  @param listener The listener to add.
     */
    public void addContextListener(ContextListener listener)
    {
        if ( this.listeners == Collections.EMPTY_LIST )
        {
            this.listeners = new ArrayList();
        }

        this.listeners.add( listener );
    }

    /** Remove a <code>ContextListener</code> from this context.
     *
     *  @param listener The listener to remove.
     */
    public void removeContextListener(ContextListener listener)
    {
        this.listeners.remove( listener );
    }

    /** Retrieve the <b>live</b> list of <code>ContextListener</code>s
     *  for this context.
     *
     *  <p>
     *  The returned <b>live</b> list is directly backed by the context.
     *  Change made to the list are immediately reflected internally
     *  within the context.
     *  </p>
     *
     *  @return The <code>List</code> of <code>ContextListeners</code>s.
     */
    public List getContextListeners()
    {
        return this.listeners;
    }

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
    //     Event firing
    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 

    // -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  
    //         Process
    // -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  

    /** Fire an event indicating that this context has started a process.
     *
     *  @param event The event.
     */
    void fireProcessStarted(ProcessStartedEvent event)
    {
        Iterator listenerIter = getContextListeners().iterator();
        ContextListener eachListener = null;
        
        while ( listenerIter.hasNext() )
        {
            eachListener = (ContextListener) listenerIter.next();
            
            eachListener.processStarted( event );
        }
    }

    /** Fire an event indicating that this context has finished a process.
     *
     *  @param event The event.
     */
    void fireProcessFinished(ProcessFinishedEvent event)
    {
        Iterator listenerIter = getContextListeners().iterator();
        ContextListener eachListener = null;

        while ( listenerIter.hasNext() )
        {
            eachListener = (ContextListener) listenerIter.next();
            
            eachListener.processFinished( event );
        }
    }

    // -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  
    //         State
    // -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  

    /** Fire an event indicating that this context has entered a state.
     *
     *  @param event The event.
     */
    void fireStateEntered(StateEnteredEvent event)
    {
        Iterator listenerIter = getContextListeners().iterator();
        ContextListener eachListener = null;
        
        while ( listenerIter.hasNext() )
        {
            eachListener = (ContextListener) listenerIter.next();
            
            eachListener.stateEntered( event );
        }
    }

    /** Fire an event indicating that this context has exited a state.
     *  
     *  @param event The event.
     */
    void fireStateExited(StateExitedEvent event)
    {
        Iterator listenerIter = getContextListeners().iterator();
        ContextListener eachListener = null;

        while ( listenerIter.hasNext() )
        {
            eachListener = (ContextListener) listenerIter.next();
            
            eachListener.stateExited( event );
        }
    }

    // -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  
    //         Transition
    // -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  

    /** Fire an event indicating that this context has followed a transition.
     *
     *  @param event The event.
     */
    void fireTransitionFollowed(TransitionFollowedEvent event)
    {
        Iterator listenIter = getContextListeners().iterator();
        ContextListener eachListen = null;

        while ( listenIter.hasNext() )
        {
            eachListen = (ContextListener) listenIter.next();

            eachListen.transitionFollowed( event );
        }
    }
}
