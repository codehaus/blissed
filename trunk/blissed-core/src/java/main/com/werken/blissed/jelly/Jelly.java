package com.werken.blissed.jelly;

/*
 $Id: Jelly.java,v 1.2 2002-07-18 18:32:58 bob Exp $

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

import org.apache.commons.jelly.JellyContext;
import org.apache.commons.jelly.XMLOutput;
import org.apache.commons.jelly.Script;
import org.apache.commons.jelly.parser.XMLParser;

import java.io.File;
import java.net.URL;

/** Jelly utilities.
 *
 *  @author <a href="mailto:bob@eng.werken.com">bob mcwhirter</a>
 */
public class Jelly
{

    /** Run a jelly script.
     *
     *  @param scriptFile Location of the script to run.
     *  @param rootUrl Root explicit context of the script.
     *  @param context Jelly context.
     *  @param output Output sink.
     *
     *  @throws Exception If an error occurs while locating,
     *          compiling or executing the script.
     */
    public static void runScript(File scriptFile,
                                 URL rootUrl,
                                 JellyContext context,
                                 XMLOutput output) throws Exception
    {
        URL oldRoot    = context.getRootURL();
        URL oldCurrent = context.getCurrentURL();

        if (rootUrl != null)
        {
            context.setRootURL(rootUrl);
            context.setCurrentURL(rootUrl);
        }

        Script script = compileScript(scriptFile,
                                      context);
        script.run(context,
                   output);

        context.setRootURL(oldRoot);
        context.setCurrentURL(oldCurrent);
    }

    /** Compile a jelly script.
     *
     *  @param scriptFile Location of the script to run.
     *  @param context Jelly context.
     *
     *  @throws Exception If an error occurs while locating
     *          or compiling the script.
     *
     *  @return The compiled script.
     */
    public static Script compileScript(File scriptFile,
                                       JellyContext context) throws Exception
    {
        XMLParser parser = new XMLParser();
        parser.setContext(context);

        Script script = parser.parse(scriptFile);

        script = script.compile();

        return script;
    }
}
