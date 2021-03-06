package org.codehaus.blissed.jelly;

/*
 $Id: CallTag.java,v 1.1 2003-06-04 15:15:04 proyal Exp $

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

import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.XMLOutput;

import org.codehaus.blissed.BlissedException;
import org.codehaus.blissed.Process;
import org.codehaus.blissed.ProcessContext;
import org.codehaus.blissed.ProcessEngine;

/** Call a process.
 *
 *  <p>
 *  This tag does <b>not</b> spawn a new process but simply
 *  considers another process to be the activity for the state.
 *  <p>
 *
 *  @author <a href="mailto:bob@eng.werken.com">bob mcwhirter</a>
 */
public class CallTag extends RuntimeTagSupport 
{
    // ------------------------------------------------------------
    //     Instance members
    // ------------------------------------------------------------

    /** The process to call. */
    private Process process;

    // ------------------------------------------------------------
    //     Constructors
    // ------------------------------------------------------------

    /** Construct.
     */
    public CallTag()
    {
        // intentionally left blank
    }

    // ------------------------------------------------------------
    //     Instance methods
    // ------------------------------------------------------------

    /** Set the process to perform.
     *
     *  @param process The process.
     */
    public void setProcess(Process process)
    {
        this.process = process;
    }

    /** Retrieve the process.
     *
     *  @return The process.
     */
    public Process getProcess() 
    {
        return this.process;
    }

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
    //     org.apache.commons.jelly.Tag
    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 

    /** Evaluates this tag after all the tags properties
     *  have been initialized.
     *
     *  @param output The output sink.
     *
     *  @throws JellyTagException if an error occurs.
     */
    public void doTag(XMLOutput output) throws JellyTagException
    {
        checkObjectAttribute( "process",
                              getProcess() );

        ProcessContext context = getProcessContext();

        if ( context == null )
        {
            throw new JellyTagException( "No process context" );
        }

        ProcessEngine  engine  = context.getProcessEngine();

        try
        {
            engine.call( getProcess(),
                         context );
        }
        catch( BlissedException e )
        {
            throw new JellyTagException("Unable to call process: " + getProcess().getName(), e );
        }
    }
}

