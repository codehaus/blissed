package org.codehaus.blissed;

/*
 $Id: ProcessEntry.java,v 1.1 2003-06-04 15:15:04 proyal Exp $

 Copyright 2003 (C) The Werken Company. All Rights Reserved.

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

/** Entry in the location stack.
 *
 *  @author <a href="mailto:bob@eng.werken.com">bob mcwhirter</a>
 */
class ProcessEntry
{
    // ------------------------------------------------------------
    //     Instance members
    // ------------------------------------------------------------

    /** The process. */
    private Process process;

    /** The current state within the process. */
    private State state;

    // ------------------------------------------------------------
    //     Constructors
    // ------------------------------------------------------------

    /** Construct.
     *
     *  @param process The process.
     */
    ProcessEntry(Process process)
    {
        this.process = process;
        this.state    = null;
    }

    // ------------------------------------------------------------
    //     Instance methods
    // ------------------------------------------------------------

    /** Retrieve the process.
     *
     *  @return The process.
     */
    Process getProcess()
    {
        return this.process;
    }

    /** Signal that this procession has entered a state.
     *
     *  @param state The state.
     *
     *  @throws InvalidMotionException If an invalid motion occurs.
     */
    void enterState(State state) throws InvalidMotionException
    {
        if ( this.state != null )
        {
            throw new InvalidMotionException( "Procession cannot enter state \"" + state.getName()
                                              + "\" while still in state \"" + this.state.getName() + "\"" );
        }

        this.state = state;
    }

    /** Signal that this procession has exited a state.
     *
     *  @param state The state.
     *
     *  @throws InvalidMotionException If an invalid motion occurs.
     */
    void exitState(State state) throws InvalidMotionException
    {
        if ( this.state == null
             ||
             ! this.state.equals( state ) )
        {
            throw new InvalidMotionException( "Procession not in state \""
                                              + state.getName() + "\" - cannot exit" );
        }

        this.state = null;
    }

    /** Retrieve the current state.
     *
     *  @return The current state, or <code>null</code> if the
     *          procession is in no state.
     */
    State getCurrentState()
    {
        return this.state;
    }
}
