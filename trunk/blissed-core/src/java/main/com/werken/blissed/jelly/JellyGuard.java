package com.werken.blissed.jelly;

/*
 $Id: JellyGuard.java,v 1.2 2002-09-17 23:00:05 bob Exp $

 Copyright 2002 (C) The Werken Company. All Rights Reserved.
 
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

import com.werken.blissed.Guard;
import com.werken.blissed.Transition;
import com.werken.blissed.ProcessContext;

import org.apache.commons.jelly.JellyContext;
import org.apache.commons.jelly.Script;
import org.apache.commons.jelly.XMLOutput;

/** Transition <code>Guard</code> implemented using a Jelly <code>Script</code>.
 *
 *  <p>
 *  Within the jelly script, the tags &lt;pass&gt; and &lt;fail&gt; can be
 *  used to immediately signal guard passage or failure and circumvent the
 *  evaluation of the remainder of the script.  If the script processes entirely
 *  without encountering an exception, it is considered to have passed, and
 *  {@link #test} returns <code>true</code>.
 *  </p>
 *
 *  @author <a href="mailto:bob@eng.werken.com">bob mcwhirter</a>
 *
 *  @version $Id $
 */
public class JellyGuard implements Guard
{
    // ------------------------------------------------------------
    //     Instance members
    // ------------------------------------------------------------

    /** The jelly script. */
    private Script script;

    // ------------------------------------------------------------
    //     Constructors
    // ------------------------------------------------------------

    /** Construct.
     * 
     *  @param script The jelly guard script.
     */
    public JellyGuard(Script script)
    {
        this.script = script;
    }

    // ------------------------------------------------------------
    //     Instance methods
    // ------------------------------------------------------------

    /** Retrieve the Jelly script.
     *
     *  @return The jelly script.
     */
    public Script getScript()
    {
        return this.script;
    }

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
    //     com.werken.blissed.Guard
    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 

    /** Test this guard against a procession.
     *
     *  @param transition The transition this guard guards.
     *  @param context The process context.
     *
     *  @return <code>true</code> if the procession passes
     *          this guard, otherwise <code>false</code>.
     */
    public boolean test(Transition transition,
                        ProcessContext context)
    {
        JellyContext jellyContext = new JellyContext();

        jellyContext.setVariable( "blissed_context",
                                  context );

        try
        {
            XMLOutput output = XMLOutput.createXMLOutput( System.err,
                                                          false );

            getScript().run( jellyContext,
                             output );
        }
        catch (Throwable t)
        {
            if ( t instanceof PassException )
            {
                return true;
            } 

            else if ( t instanceof FailException )
            {
                return false;
            }

            return false;
        }

        return true;
    }
}
