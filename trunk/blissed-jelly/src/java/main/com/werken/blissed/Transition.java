package com.werken.blissed;

/*
 $Id: Transition.java,v 1.4 2002-07-03 03:10:37 werken Exp $

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

/** A guarded arc between two <code>Node</code>s.
 *
 *  @see Node
 *  @see Predicate
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

    /** Predicate guarding this transition. */
    private Predicate predicate;

    /** Description of this transition. */
    private String description;

    /** Transition event listeners. */
    private List listeners;

    // ------------------------------------------------------------
    //     Constructors
    // ------------------------------------------------------------

    /** Construct.
     *
     *  @param origin The origin of this transitional arc.
     *  @param destination The destination of this transitional arc.
     *  @param predicate Predicate guard on this transition.
     *  @param description The description of this transition.
     */
    public Transition(Node origin,
                      Node destination,
                      Predicate predicate,
                      String description)
    {
        this.origin      = origin;
        this.destination = destination;
        this.predicate   = predicate;
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

    /** Retrieve the <code>Predicate</code> which guards this transition.
     *
     *  @return The predicate.
     */
    public Predicate getPredicate()
    {
        return this.predicate;
    }

    /** Set the <code>Predicate</code> which guards this transition.
     *
     *  @param The predicate.
     */
    public void setPredicate(Predicate predicate)
    {
        this.predicate = predicate;
    }

    /** Test and optionally accept this transition against a context.
     *
     *  @param workSlip The context against which to
     *         evaluate this transition.
     *
     *  @return <code>true</code> if this transition was successful
     *          within the context.
     */
    boolean accept(WorkSlip workSlip)
    {
        boolean result = getPredicate().performTest( workSlip );

        if ( ! result )
        {
            return false;
        }

        getOrigin().release( workSlip );

        fireTransitionFollowed( workSlip );

        getDestination().accept( workSlip );

        return true;
    }
    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
    //     Event-listener management
    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 

    public void addTransitionListener(TransitionListener listener)
    {
        if ( this.listeners == Collections.EMPTY_LIST )
        {
            this.listeners = new ArrayList();
        }

        this.listeners.add( listener );
    }

    public void removeTransitionListener(TransitionListener listener)
    {
        this.listeners.remove( listener );
    }

    public List getTransitionListeners()
    {
        return this.listeners;
    }

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
    //     Event firing
    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 

    void fireTransitionFollowed(WorkSlip workSlip)
    {
        TransitionFollowedEvent event = new TransitionFollowedEvent( this,
                                                                     workSlip );

        Iterator listenIter = getTransitionListeners().iterator();
        TransitionListener eachListen = null;

        while ( listenIter.hasNext() )
        {
            eachListen = (TransitionListener) listenIter.next();

            eachListen.transitionFollowed( event );
        }
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

}
