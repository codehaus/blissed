package com.werken.blissed.jelly;

/*
 $Id: SpawnTag.java,v 1.2 2002-09-17 21:36:43 bob Exp $

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

import com.werken.blissed.Process;
import com.werken.blissed.ProcessEngine;
import com.werken.blissed.ProcessContext;

import org.apache.commons.jelly.XMLOutput;
import org.apache.commons.jelly.MissingAttributeException;

/** Spawn a new process.
 *
 *  <p>
 *  This may be used to spawn both top-level and child processes.
 *  In both cases, a new <code>Thread</code> is created for execution
 *  of the process.
 *  </p>
 *
 *  <p>
 *  The <code>Thread</code> for the process may terminate before the
 *  <code>Process</code> itself, if execution stalls.  
 *  </p>
 *
 *  @author <a href="mailto:bob@eng.werken.com">bob mcwhirter</a>
 *
 *  @version $Id: SpawnTag.java,v 1.2 2002-09-17 21:36:43 bob Exp $
 */
public class SpawnTag extends RuntimeTagSupport 
{
    // ------------------------------------------------------------
    //     Instance members
    // ------------------------------------------------------------

    /** The process engine if non-nested. */
    private ProcessEngine engine;

    /** Name of the process to spawn. */
    private Process process;

    /** Storage variable for spawned process. */
    private String var;

    // ------------------------------------------------------------
    //     Constructors
    // ------------------------------------------------------------

    /** Construct.
     */
    public SpawnTag()
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
     *
     *  @throws Exception If a process library cannot be found.
     */
    public Process getProcess() throws Exception
    {
        return this.process;
    }

    /** Set the <code>ProcessEngine</code> to use if spawning
     *  a non-nested process.
     *
     *  @param engine The process engine.
     */
    public void setEngine(ProcessEngine engine)
    {
        this.engine = engine;
    }

    /** Retrieve the <code>ProcessEngine</code> to use if spawning
     *  a non-nested process.
     *
     *  @return The process engine.
     */
    public ProcessEngine getEngine()
    {
        return this.engine;
    }

    public void setVar(String var)
    {
        this.var = var;
    }

    public String getVar()
    {
        return this.var;
    }

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
    //     org.apache.commons.jelly.Tag
    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 

    /** Evaluates this tag after all the tags properties
     *  have been initialized.
     *
     *  @param output The output sink.
     *
     *  @throws Exception if an error occurs.
     */
    public void doTag(XMLOutput output) throws Exception
    {
        checkObjectAttribute( "process",
                              this.process );

        ProcessContext context = getProcessContext();

        ProcessEngine  engine  = null;
        ProcessContext spawned = null;

        if ( context == null )
        {
            engine = getEngine();

            if ( engine == null )
            {
                throw new MissingAttributeException( "engine" );
            }

            spawned = engine.spawn( getProcess() );
        }
        else
        {
            engine  = context.getProcessEngine();

            spawned = engine.spawn( getProcess(),
                                    context );
        }

        if ( getVar() != null )
        {
            getContext().setVariable( getVar(),
                                      spawned );
        }
    }
}
