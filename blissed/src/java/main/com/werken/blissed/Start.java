package com.werken.blissed;

/*
 $Id: Start.java,v 1.6 2002-07-04 19:40:07 werken Exp $

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

/** Entry-point start node of a <code>Process</code>.
 *
 *  @see Process#getStart
 *  @see Process#start
 *
 *  @author <a href="mailto:bob@eng.werken.com">bob mcwhirter</a>
 */
public class Start extends Node
{

    // ------------------------------------------------------------
    //     Instance members
    // ------------------------------------------------------------

    /** The initial transition. */
    private Transition transition;

    // ------------------------------------------------------------
    //     Constructors
    // ------------------------------------------------------------

    /** Construct.
     *
     *  @param process The process.
     */
    Start(Process process)
    {
        super( process,
               "blissed.start",
               "start for " + process.getName() );
               
    }

    // ------------------------------------------------------------
    //     Instance methods
    // ------------------------------------------------------------

    /** Set the initial transition.
     *
     *  @param transition The transition.
     */
    public void setTransition(Transition transition)
    {
        this.transition  = transition;
    }

    /** Retrieve the initial transition.
     *
     *  @return The transition.
     */
    public Transition getTransition()
    {
        return this.transition;
    }

    /** Attempt to transition the context out of this node.
     *
     *  @param context The context to attempt transitioning.
     */
    void attemptTransition(Context context) throws InvalidMotionException
    {
        getTransition().accept( context );
    }

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
    //     com.werken.blissed.Node
    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 

    /** Accept a context into this node.
     *
     *  @param context The context to accept.
     */
    public void accept(Context context) throws InvalidMotionException
    {
        context.startProcess( getProcess() );

        super.accept( context );

        getProcess().fireProcessStarted( context );

        attemptTransition( context );
    }
}
