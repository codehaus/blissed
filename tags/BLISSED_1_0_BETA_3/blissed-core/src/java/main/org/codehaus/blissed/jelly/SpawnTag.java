package org.codehaus.blissed.jelly;

/*
 $Id: SpawnTag.java,v 1.2 2003-06-05 19:56:56 proyal Exp $

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
import org.apache.commons.jelly.MissingAttributeException;
import org.apache.commons.jelly.XMLOutput;

import org.codehaus.blissed.BlissedException;
import org.codehaus.blissed.Process;
import org.codehaus.blissed.ProcessContext;
import org.codehaus.blissed.ProcessEngine;

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
 *  @version $Id: SpawnTag.java,v 1.2 2003-06-05 19:56:56 proyal Exp $
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

    /** Async spawn flag. */
    private Boolean async;

    // ------------------------------------------------------------
    //     Constructors
    // ------------------------------------------------------------

    /** Construct.
     */
    public SpawnTag()
    {
        this.async = null;
    }

    // ------------------------------------------------------------
    //     Instance methods
    // ------------------------------------------------------------

    /** Set the process to perform.
     *
     *  @param process The process.
     */
    public void setProcess( Process process )
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

    /** Set the <code>ProcessEngine</code> to use if spawning
     *  a non-nested process.
     *
     *  @param engine The process engine.
     */
    public void setEngine( ProcessEngine engine )
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

    /** Set the variable name in which to store the <code>Activity</code>.
     *
     *  @param var The variable name.
     */
    public void setVar( String var )
    {
        this.var = var;
    }

    /** Retrieve the variable name in which to store the <code>Activity</code>.
     *
     *  @return The variable name.
     */
    public String getVar()
    {
        return this.var;
    }

    /** Set the async flag.
     *
     *  <p>
     *  For top-level, non-nested spawned processes, the <code>async</code>
     *  attribute may be specified in order to signal if the spawned process
     *  should attempt to use the caller's thread or if it should instead
     *  register with the process engine to operate asynchronously.
     *  </p>
     *
     *  <p>
     *  Even if spawned non-asyncly, once the process blocks, any future
     *  work <b>will</b> occur asynchronously within the process engine's
     *  thread.
     *  </p>
     *
     *  <p>
     *  For any process that is spawned as a child of another process,
     *  the <code>async</code> attribute is invalid and will throw an
     *  exception.  All nested processes are spawned asynchronously.
     *  </p>
     *
     *  @param async The async flag.
     */
    public void setAsync( boolean async )
    {
        if( async )
        {
            this.async = Boolean.TRUE;
        }
        else
        {
            this.async = Boolean.FALSE;
        }
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
    public void doTag( XMLOutput output ) throws JellyTagException
    {
        checkObjectAttribute( "process",
                              this.process );

        ProcessContext context = getProcessContext();

        ProcessEngine engine = null;
        ProcessContext spawned = null;

        if( context == null )
        {
            engine = getEngine();

            if( engine == null )
            {
                throw new MissingAttributeException( "engine" );
            }

            boolean async = false;

            if( this.async != null )
            {
                async = this.async.booleanValue();
            }

            try
            {
                spawned = engine.spawn( getProcess(),
                                        null,
                                        async );
            }
            catch( BlissedException e )
            {
                throw new JellyTagException( "Unable to spawn process: " + getProcess().getName(), e );
            }
        }
        else
        {
            engine = context.getProcessEngine();

            if( this.async != null )
            {
                throw new JellyTagException( "async attribute only via for non-nested process" );
            }


            try
            {
                spawned = engine.spawn( getProcess(),
                                        context,
                                        null );
            }
            catch( BlissedException e )
            {
                throw new JellyTagException( "Unable to spawn process: " + getProcess().getName(), e );
            }
        }

        if( getVar() != null )
        {
            getContext().setVariable( getVar(),
                                      spawned );
        }
    }
}
