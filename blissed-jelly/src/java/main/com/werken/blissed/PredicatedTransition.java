package com.werken.blissed;

/*
 $Id: PredicatedTransition.java,v 1.2 2002-07-04 22:56:53 werken Exp $

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
 *  @see Transition
 *  @see Transition#accept
 *  @see Node
 *  @see Predicate
 *
 *  @author <a href="mailto:bob@eng.werken.com">bob mcwhirter</a>
 */
public class PredicatedTransition extends Transition
{    
    // ------------------------------------------------------------
    //     Instance members
    // ------------------------------------------------------------

    /** Predicate guarding this transition. */
    private Predicate predicate;

    // ------------------------------------------------------------
    //     Constructors
    // ------------------------------------------------------------

    /** Construct.
     *
     *  @param origin The origin of this transitional arc.
     *  @param destination The destination of this transitional arc.
     *  @param description The description of this transition.
     */
    public PredicatedTransition(Node origin,
                                Node destination,
                                String description)
    {
        this( origin,
              destination,
              description,
              null );

        this.predicate = TruePredicate.INSTANCE;
    }

    /** Construct.
     *
     *  @param origin The origin of this transitional arc.
     *  @param destination The destination of this transitional arc.
     *  @param predicate Predicate guard on this transition.
     *  @param description The description of this transition.
     */
    public PredicatedTransition(Node origin,
                                Node destination,
                                String description,
                                Predicate predicate)
    {
        super( origin,
               destination,
               description );

        this.predicate = predicate;
    }

    // ------------------------------------------------------------
    //     Instance methods
    // ------------------------------------------------------------

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
     *  @param predicate The predicate.
     */
    public void setPredicate(Predicate predicate)
    {
        this.predicate = predicate;
    }

    /** Test and optionally accept this transition against a context.
     *
     *  @param context The context against which to
     *         evaluate this transition.
     *
     *  @return <code>true</code> if this transition was successful
     *          within the context.
     */
    boolean accept(Context context) throws InvalidMotionException, ActivityException
    {
        boolean result = getPredicate().performTest( context );

        if ( ! result )
        {
            return false;
        }

        return super.accept( context );
    }

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
    //     java.lang.Object
    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 

    public String toString()
    {
        return "[PredicatedTransition: super=" + super.toString() 
            + "; predicate=" + getPredicate()
            + "]";
    }


}
