package com.werken.blissed;

/*
 $Id: Transition.java,v 1.13 2002-07-17 17:11:07 bob Exp $

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

import com.werken.blissed.event.TransitionFollowedEvent;
import com.werken.blissed.event.TransitionListener;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Collections;

/** An arc between two <code>Node</code>s.
 *
 *  @see Node
 *
 *  @author <a href="mailto:bob@eng.werken.com">bob mcwhirter</a>
 */
public class Transition implements Described
{    
    // ------------------------------------------------------------
    //     Instance members
    // ------------------------------------------------------------

    /** Origin end of this transitional arc. */
    private Node origin;

    /** Destination end of this transitional arc. */
    private Node destination;

    /** Description of this transition. */
    private String description;

    /** Transition event listeners. */
    private List listeners;

    /** Guard which restricts transitions. */
    private Guard guard;

    // ------------------------------------------------------------
    //     Constructors
    // ------------------------------------------------------------

    /** Construct.
     *
     *  @param origin The origin of this transitional arc.
     *  @param destination The destination of this transitional arc.
     *  @param description The description of this transition.
     */
    Transition(Node origin,
               Node destination,
               String description)
    {
        this( origin,
              destination,
              null,
              description );
    }

    /** Construct.
     *
     *  @param origin The origin of this transitional arc.
     *  @param destination The destination of this transitional arc.
     *  @param guard The guard for this transition.
     *  @param description The description of this transition.
     */
    Transition(Node origin,
               Node destination,
               Guard guard,
               String description)
    {
        this.origin      = origin;
        this.destination = destination;
        this.guard       = guard;
        this.description = description;
        this.listeners   = Collections.EMPTY_LIST;
    }

    // ------------------------------------------------------------
    //     Instance methods
    // ------------------------------------------------------------

    /** Retrieve the origin of this transition.
     *
     *  @return The origin <code>Node</code>.
     */
    public Node getOrigin()
    {
        return this.origin;
    }

    /** Retrieve the destination of this transition.
     *
     *  @return The destination <code>Node</code>.
     */
    public Node getDestination()
    {
        return this.destination;
    }

    /** Set the guard of this transition.
     *
     *  @param guard The guard.
     */
    public void setGuard(Guard guard)
    {
        this.guard = guard;
    }

    /** Retrieve the guard of this transition.
     *
     *  @return The guard.
     */
    public Guard getGuard()
    {
        return this.guard;
    }

    /** Test the guard on this transition.
     *
     *  <p>
     *  If this transition contains no guard, then
     *  the test always evaluates to <code>true</code>.
     *  </p>
     *
     *  @param context The context.
     *
     *  @return <code>true</code> if the context passes the guard,
     *          otherwise <code>false</code>.
     */
    boolean testGuard(Context context)
    {
        if ( getGuard() == null )
        {
            return true;
        }

        return getGuard().test( context );
    }

    /** Test and optionally accept this transition against a context.
     *
     *  @param context The context against which to
     *         evaluate this transition.
     *
     *  @return <code>true</code> if this transition was successful
     *          within the context.
     *
     *  @throws InvalidMotionException If an invalid motion occurs.
     *  @throws ActivityException If an error occurs while performing an activity.
     */
    boolean accept(Context context) throws InvalidMotionException, ActivityException
    {
        if ( ! testGuard( context ) )
        {
            return false;
        }

        getOrigin().release( context );

        fireTransitionFollowed( context );

        getDestination().accept( context );

        return true;
    }

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
    //     Event-listener management
    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 

    /** Add a <code>TransitionListener</code> to this transition.
     *
     *  @param listener The listener to add.
     */
    public void addTransitionListener(TransitionListener listener)
    {
        if ( this.listeners == Collections.EMPTY_LIST )
        {
            this.listeners = new ArrayList();
        }

        this.listeners.add( listener );
    }

    /** Remove a <code>TransitionListener</code> from this state.
     *
     *  @param listener The listener to remove.
     */
    public void removeTransitionListener(TransitionListener listener)
    {
        this.listeners.remove( listener );
    }

    /** Retrieve the <b>live</b> list of <code>TransitionListener</code>s
     *  for this transition.
     *
     *  <p>
     *  The returned <b>live</b> list is directly backed by the transition.
     *  Change made to the list are immediately reflected internally
     *  within the transition.
     *  </p>
     *
     *  @return The <code>List</code> of <code>TransitionListener</code>s.
     */
    public List getTransitionListeners()
    {
        return this.listeners;
    }

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
    //     Event firing
    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 

    /** Fire an event indicating that a context has followed
     *  this transition.
     *
     *  @param context The context following this transition.
     */
    void fireTransitionFollowed(Context context)
    {
        TransitionFollowedEvent event = new TransitionFollowedEvent( this,
                                                                     context );

        Iterator listenIter = getTransitionListeners().iterator();
        TransitionListener eachListen = null;

        while ( listenIter.hasNext() )
        {
            eachListen = (TransitionListener) listenIter.next();

            eachListen.transitionFollowed( event );
        }

        context.fireTransitionFollowed( event );
    }

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
    //     com.werken.blissed.Described
    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 

    /** Retrieve the description of this transition.
     *
     *  @return The description of this transition.
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
        return "[Transition: origin=" + getOrigin()
            + "; destination=" + getDestination()
            + "; guard=" + getGuard() 
            + "; description=" + getDescription()
            + "]";
    }


}
