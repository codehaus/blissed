package org.codehaus.blissed.guard;

/*
 $Id: BooleanGuard.java,v 1.1 2003-06-04 15:15:04 proyal Exp $

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

import org.codehaus.blissed.Guard;
import org.codehaus.blissed.ProcessContext;
import org.codehaus.blissed.Transition;

/** Simple flag-based boolean <code>Guard</code>.
 *
 *  @see Guard
 *
 *  @author <a href="mailto:bob@eng.werken.com">bob mcwhirter</a>
 */
public class BooleanGuard implements Guard
{
    // ------------------------------------------------------------
    //     Instance members
    // ------------------------------------------------------------

    /** The boolean guard value. */
    private boolean guard;

    // ------------------------------------------------------------
    //     Constructors
    // ------------------------------------------------------------

    /** Construct.
     *
     *  @param guard Initial guard value.
     */
    public BooleanGuard(boolean guard)
    {
        this.guard = guard;
    }

    // ------------------------------------------------------------
    //     Instance methods
    // ------------------------------------------------------------

    /** Set the guard value.
     *
     *  @param guard The guard value.
     */
    public void setGuard(boolean guard)
    {
        this.guard = guard;
    }

    /** Retrieve the guard value.
     *
     *  @return The guard value.
     */
    public boolean getGuard()
    {
        return this.guard;
    }

    /** Test this guard against a process context.
     *
     *  <p>
     *  <b>implementation note:</b> This method
     *  ignores the process context itself, and returns
     *  the same value as returned from
     *  {@link #getGuard}.
     *  </p>
     *
     *  @see #setGuard
     *  @see #getGuard
     *
     *  @param transition The transition this guard guards.
     *  @param context The process context.
     *
     *  @return <code>true</code> if the process context passes
     *          this guard, otherwise <code>false</code>.
     */
    public boolean test(Transition transition,
                        ProcessContext context)
    {
        return getGuard();
    }
}

