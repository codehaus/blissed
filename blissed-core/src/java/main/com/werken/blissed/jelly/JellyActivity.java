package com.werken.blissed.jelly;

/*
 $Id: JellyActivity.java,v 1.3 2002-09-17 05:13:34 bob Exp $

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

import com.werken.blissed.Activity;
import com.werken.blissed.ProcessContext;
import com.werken.blissed.ActivityException;

import org.apache.commons.jelly.JellyContext;
import org.apache.commons.jelly.Script;
import org.apache.commons.jelly.XMLOutput;

/** State activity based upon a Jelly script.
 *
 *  @author <a href="mailto:bob@eng.werken.com">bob mcwhirter</a>
 *
 *  @version $Id: JellyActivity.java,v 1.3 2002-09-17 05:13:34 bob Exp $
 */
public class JellyActivity implements Activity
{
    // ------------------------------------------------------------
    //     Instance members
    // ------------------------------------------------------------

    /** Jellyscript. */
    private Script script;

    // ------------------------------------------------------------
    //     Constructors
    // ------------------------------------------------------------

    /** Construct.
     *
     *  @param script The jelly script.
     */
    public JellyActivity(Script script)
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
    //     com.werken.blissed.Activity
    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 

    /** Perform this activity within the specified procession.
     *
     *  @param context The process context
     *
     *  @throws ActivityException if an error occurs.
     */
    public void perform(ProcessContext context) throws ActivityException
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
        catch (Exception e)
        {
            throw new ActivityException( e );
        }
    }
   
}
