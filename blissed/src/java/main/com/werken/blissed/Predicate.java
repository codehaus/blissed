package com.werken.blissed;

/*
 $Id: Predicate.java,v 1.4 2002-07-03 04:39:32 werken Exp $

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

import com.werken.blissed.event.PredicatePassedEvent;
import com.werken.blissed.event.PredicateFailedEvent;
import com.werken.blissed.event.PredicateListener;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Collections;

/** Boolean predicate evaluated against a <code>WorkSlip</code> context.
 *
 *  <p>
 *  A <code>Predicate</code> is used to guard <code>Transition</code>s
 *  between <code>State</code>s.  A <code>Predicate</code> is evaluated
 *  against the context of a <code>WorkSlip</code>.
 *  </p>
 *
 *  @see Transition
 *  @see WorkSlip
 *  @see PredicatePassedEvent
 *  @see PredicateFailedEvent
 *  @see PredicateListener
 *
 *  @author <a href="mailto:bob@eng.werken.com">bob mcwhirter</a>
 */
public abstract class Predicate implements Described
{

    // ------------------------------------------------------------
    //     Instance members
    // ------------------------------------------------------------

    /** The description of this predicate. */
    private String description;

    /** List of PredicateTestedListeners. */
    private List listeners;

    // ------------------------------------------------------------
    //     Constructors
    // ------------------------------------------------------------

    /** Construct.
     *
     *  @param description The description of this predicate.
     */
    public Predicate(String description)
    {
        this.description = description;
        this.listeners = Collections.EMPTY_LIST;
    }

    // ------------------------------------------------------------
    //     Instance methods
    // ------------------------------------------------------------

    /** Retrieve the description of this predicate.
     *
     *  @return The description.
     */
    public String getDescription()
    {
        return this.description;
    }

    /** Test this predicate against a <code>WorkSlip</code> context.
     *
     *  @param workSlip The context against which to evaluate.
     *
     *  @return <code>true</code> if this predicate passes within
     *          the context of the <code>WorkSlip</code>, otherwise,
     *          <code>false</code>.
     */
    public abstract boolean test(WorkSlip workSlip);


    /** Perform the prediacte test, firing the appropriate events.
     *
     *  @param workSlip the context against which to evaluate.
     *
     *  @return <code>true</code> if this predicate passes within
     *          the context of the <code>WorkSlip</code>, otherwise,
     *          <code>false</code>.
     */
    boolean performTest(WorkSlip workSlip)
    {
        boolean result = test( workSlip );

        if ( result )
        {
            firePredicatePassed( workSlip );
        }
        else
        {
            firePredicateFailed( workSlip );
        }

        return result;
    }

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
    //     Event-listener management
    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 

    /** Add a <code>PredicateListener</code> to this predicate.
     *
     *  @param listener The listenr to add.
     */
    public void addPredicateListener(PredicateListener listener)
    {
        if ( this.listeners == Collections.EMPTY_LIST )
        {
            this.listeners = new ArrayList();
        }

        this.listeners.add( listener );
    }

    /** Remove a <code>PredicateListener</code> from this predicate.
     *
     *  @param listener The listener to remove.
     */
    public void removePredicateListener(PredicateListener listener)
    {
        if ( this.listeners == Collections.EMPTY_LIST )
        {
            return;
        }

        this.listeners.remove( listener );
    }

    /** Retrieve the <b>live</b> list of <code>PredicateListener</code>s
     *  for this predicate.
     *
     *  <p>
     *  The returned <b>live</b> list is directly backed by the predicate.
     *  Change made to the list are immediately reflected internally
     *  within the predicate.
     *  </p>
     *
     *  @return The <code>List</code> of <code>PredicateListener</code>s.
     */
    public List getPredicateListeners()
    {
        return this.listeners;
    }

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
    //     Event firing 
    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 


    /** Fire an event indicating this predicate passed
     *  within a <code>WorkSlip</code> context.
     *
     *  @param workSlip the context against which this predicate
     *         was evaluated to pass.
     */
    void firePredicatePassed(WorkSlip workSlip)
    {
        PredicatePassedEvent event = new PredicatePassedEvent( this,
                                                                workSlip );

        Iterator listenerIter = getPredicateListeners().iterator();
        PredicateListener eachListener = null;
        
        while ( listenerIter.hasNext() )
        {
            eachListener = (PredicateListener) listenerIter.next();
            
            eachListener.predicatePassed( event );
        }
    }

    /** Fire an event indicating this predicate failed
     *  within a <code>WorkSlip</code> context.
     *
     *  @param workSlip the context against which this predicate
     *         was evaluated to fail.
     */
    void firePredicateFailed(WorkSlip workSlip)
    {
        PredicateFailedEvent event = new PredicateFailedEvent( this,
                                                               workSlip );

        Iterator listenerIter = getPredicateListeners().iterator();
        PredicateListener eachListener = null;
        
        while ( listenerIter.hasNext() )
        {
            eachListener = (PredicateListener) listenerIter.next();
            
            eachListener.predicateFailed( event );
        }
    }
}
