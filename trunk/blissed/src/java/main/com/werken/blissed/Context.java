package com.werken.blissed;

/*
  $Id: Context.java,v 1.3 2002-07-05 04:15:04 werken Exp $

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
 *  @see Process#accept
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

    /** Instance attributes. */
    private Map attributes;

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

        this.attributes = new HashMap();

        this.children   = Collections.EMPTY_SET;
        this.listeners  = Collections.EMPTY_LIST;
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

    /** Retrieve an attribute.
     *
     *  @param name The name of the attribute.
     *
     *  @return The attribute's value, or <code>null</code>
     *          if the attribute has not been set.
     */
    public Object getAttribute(String name)
    {
        return this.attributes.get( name );
    }

    /** Set an attribute.
     *
     *  @param name The name of the attribute.
     *  @param value The value of the attribute.
     */
    public void setAttribute(String name,
                             Object value)
    {
        this.attributes.put( name,
                             value );
    }

    /** Clean an attribute.
     *
     *  @param name The name of the attribute.
     */
    public void clearAttribute(String name)
    {
        this.attributes.remove( name );
    }

    void startProcess(Process process)
    {
        this.location.startProcess( process );
    }

    void finishProcess(Process process) throws InvalidMotionException
    {
        this.location.finishProcess( process );
    }

    void enterNode(Node node) throws InvalidMotionException
    {
        this.location.enterNode( node );
    }

    void exitNode(Node node) throws InvalidMotionException
    {
        this.location.exitNode( node );
    }

    /** Spawn a nested process.
     *
     *  @param process The process to spawn.
     *
     *  @return The spawned nested Context.
     */
    public Context start(Process process)
    {
        Context context = new Context( process,
                                       this );
                                          
        if ( this.children == Collections.EMPTY_SET )
        {
            this.children = new HashSet();
        }

        this.children.add( context );

        return context;
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

    /** Create a duplicate child of this context for a split.
     *
     *  @return A duplicate child context for a split.
     */
    Context createSplitContext()
    {
        Context dupe = new Context( getProcess(),
                                    this );

        dupe.attributes.putAll( this.attributes );

        return dupe;
    }

    /** Perform a liveness check on this context.
     */
    void check() throws InvalidMotionException, ActivityException
    {
        this.location.check();
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
