package com.werken.blissed;

/*
 $Id: Procession.java,v 1.3 2002-08-15 17:37:23 bob Exp $

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

import com.werken.blissed.event.ProcessionListener;
import com.werken.blissed.event.ProcessStartedEvent;
import com.werken.blissed.event.ProcessFinishedEvent;
import com.werken.blissed.event.StateEnteredEvent;
import com.werken.blissed.event.StateExitedEvent;
import com.werken.blissed.event.TransitionFollowedEvent;
import com.werken.blissed.event.ActivityStartedEvent;
import com.werken.blissed.event.ActivityFinishedEvent;

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
 *
 *  @version $Id: Procession.java,v 1.3 2002-08-15 17:37:23 bob Exp $
 */
public class Procession implements Named
{
    // ------------------------------------------------------------
    //     Instance members
    // ------------------------------------------------------------

    /** Name of this Procession. */
    private String name;

    /** Process of which this Procession is an instance. */
    private Process process;

    /** Parent Procession, if this is spawned. */
    private Procession parent;

    /** Children Procession, if any. */
    private Set children;

    /** Location tracking. */
    private Location location;

    /** Process data. */
    private Object processData;

    /** Procession listeners. */
    private List listeners;

    // ------------------------------------------------------------
    //     Constructors
    // ------------------------------------------------------------

    /** Construct a root <code>Procession</code>.
     *
     *  @param process The process of which this Procession is an instance. 
     */
    Procession(Process process)
    {
        this( process,
              null );
    }

    /** Construct a nested <code>Procession</code>.
     *
     *  @param process The process of which this Procession is an instance. 
     *  @param parent The parent of this Procession.
     */
    Procession(Process process,
               Procession parent)
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

        this.children   = Collections.EMPTY_SET;
        this.listeners  = Collections.EMPTY_LIST;

        this.location = new Location( this );

        this.processData = process.createProcessData();
    }

    // ------------------------------------------------------------
    //     Instance methods
    // ------------------------------------------------------------

    /** Retrieve the name of this Procession.
     *
     *  @return The name of this Procession.
     */
    public String getName()
    {
        return this.name;
    }

    /** Retrieve the Process of this Procession.
     *
     *  @return The process of this Procession.
     */
    public Process getProcess()
    {
        return this.process;
    }

    /** Retrieve the parent of this Procession.
     *
     *  @return The parent of this Procession, or <code>null</code>
     *          if this is a root-level Procession.
     */
    public Procession getParent()
    {
        return this.parent;
    }

    /** Set process-specific data.
     *
     *  @param processData The process data.
     */
    public void setProcessData(Object processData)
    {
        this.processData = processData;
    }

    /** Retrieve the process-specific data.
     *
     *  @return The process data.
     */
    public Object getProcessData()
    {
        return this.processData;
    }

    /** Signal that this procession has started a process.
     *
     *  @param process The process.
     */
    void startProcess(Process process)
    {
        this.location.startProcess( process );
    }

    /** Signal that this procession has finished a process.
     *
     *  @param process The process.
     *
     *  @throws InvalidMotionException If an invalid motion occurs.
     */
    void finishProcess(Process process) throws InvalidMotionException
    {
        this.location.finishProcess( process );
    }

    /** Signal that this procession has entered a state.
     *
     *  @param state The state.
     *
     *  @throws InvalidMotionException If an invalid motion occurs.
     */
    void enterState(State state) throws InvalidMotionException
    {
        this.location.enterState( state );
    }

    /** Signal that this procession has exited a state.
     *
     *  @param state The state.
     *
     *  @throws InvalidMotionException If an invalid motion occurs.
     */
    void exitState(State state) throws InvalidMotionException
    {
        this.location.exitState( state );
    }

    /** Spawn a nested process.
     *
     *  @param process The process to spawn.
     *
     *  @return The spawned nested Procession.
     *
     *  @throws InvalidMotionException If an invalid motion occurs.
     *  @throws ActivityException If an error occurs while performing an activity.
     */
    public Procession spawn(Process process) throws InvalidMotionException, ActivityException
    {
        Procession spawned = process.spawn( this );

        addChild( spawned );

        return spawned;
    }

    /** Add a child procession to this procession.
     *
     *  @param child The child to add.
     */
    void addChild(Procession child)
    {
        if ( this.children == Collections.EMPTY_SET )
        {
            this.children = new HashSet();
        }

        this.children.add( child );
    }

    /** Retrieve an unmodifiable set of all children
     *  processions of this procession.
     *
     *  @return An unmodifiable <code>Set</code> of children
     *          <code>Procession</code>s.
     */
    public Set getChildren()
    {
        return Collections.unmodifiableSet( this.children );
    }

    /** Perform a liveness check on this procession.
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

    /** Retrieve the procession's current state location.
     *
     *  @return The procession's current state location.
     */
    State getCurrentState()
    {
        return getLocation().getCurrentState();
    }

    /** Retrieve the procession's current process location.
     *
     *  @return The procession's current process location.
     */
    Process getCurrentProcess()
    {
        return getLocation().getCurrentProcess();
    }

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
    //     Event-listener management
    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 

    /** Add a <code>ProcessionListener</code> to this procession.
     *
     *  @param listener The listener to add.
     */
    public void addProcessionListener(ProcessionListener listener)
    {
        if ( this.listeners == Collections.EMPTY_LIST )
        {
            this.listeners = new ArrayList();
        }

        this.listeners.add( listener );
    }

    /** Remove a <code>ProcessionListener</code> from this procession.
     *
     *  @param listener The listener to remove.
     */
    public void removeProcessionListener(ProcessionListener listener)
    {
        this.listeners.remove( listener );
    }

    /** Retrieve the <b>live</b> list of <code>ProcessionListener</code>s
     *  for this procession.
     *
     *  <p>
     *  The returned <b>live</b> list is directly backed by the procession.
     *  Change made to the list are immediately reflected internally
     *  within the procession.
     *  </p>
     *
     *  @return The <code>List</code> of <code>ProcessionListeners</code>s.
     */
    public List getProcessionListeners()
    {
        return this.listeners;
    }

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
    //     Event firing
    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 

    // -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  
    //         Process
    // -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  

    /** Fire an event indicating that this procession has started a process.
     *
     *  @param event The event.
     */
    void fireProcessStarted(ProcessStartedEvent event)
    {
        Iterator listenerIter = getProcessionListeners().iterator();
        ProcessionListener eachListener = null;
        
        while ( listenerIter.hasNext() )
        {
            eachListener = (ProcessionListener) listenerIter.next();
            
            eachListener.processStarted( event );
        }
    }

    /** Fire an event indicating that this procession has finished a process.
     *
     *  @param event The event.
     */
    void fireProcessFinished(ProcessFinishedEvent event)
    {
        Iterator listenerIter = getProcessionListeners().iterator();
        ProcessionListener eachListener = null;

        while ( listenerIter.hasNext() )
        {
            eachListener = (ProcessionListener) listenerIter.next();
            
            eachListener.processFinished( event );
        }
    }

    // -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  
    //         State
    // -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  

    /** Fire an event indicating that this procession has entered a state.
     *
     *  @param event The event.
     */
    void fireStateEntered(StateEnteredEvent event)
    {
        Iterator listenerIter = getProcessionListeners().iterator();
        ProcessionListener eachListener = null;
        
        while ( listenerIter.hasNext() )
        {
            eachListener = (ProcessionListener) listenerIter.next();
            
            eachListener.stateEntered( event );
        }
    }

    /** Fire an event indicating that this procession has exited a state.
     *  
     *  @param event The event.
     */
    void fireStateExited(StateExitedEvent event)
    {
        Iterator listenerIter = getProcessionListeners().iterator();
        ProcessionListener eachListener = null;

        while ( listenerIter.hasNext() )
        {
            eachListener = (ProcessionListener) listenerIter.next();
            
            eachListener.stateExited( event );
        }
    }

    /** Fire an event indicating that this procession has started an activity.
     *
     *  @param event The event.
     */
    void fireActivityStarted(ActivityStartedEvent event)
    {
        Iterator listenIter = getProcessionListeners().iterator();
        ProcessionListener eachListen = null;

        while ( listenIter.hasNext() )
        {
            eachListen = (ProcessionListener) listenIter.next();

            eachListen.activityStarted( event );
        }
    }

    /** Fire an event indicating that this procession has started an activity.
     *
     *  @param event The event.
     */
    void fireActivityFinished(ActivityFinishedEvent event)
    {
        Iterator listenIter = getProcessionListeners().iterator();
        ProcessionListener eachListen = null;

        while ( listenIter.hasNext() )
        {
            eachListen = (ProcessionListener) listenIter.next();

            eachListen.activityFinished( event );
        }
    }

    // -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  
    //         Transition
    // -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  

    /** Fire an event indicating that this procession has followed a transition.
     *
     *  @param event The event.
     */
    void fireTransitionFollowed(TransitionFollowedEvent event)
    {
        Iterator listenIter = getProcessionListeners().iterator();
        ProcessionListener eachListen = null;

        while ( listenIter.hasNext() )
        {
            eachListen = (ProcessionListener) listenIter.next();

            eachListen.transitionFollowed( event );
        }
    }
}
