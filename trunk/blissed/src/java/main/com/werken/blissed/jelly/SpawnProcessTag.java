package com.werken.blissed.jelly;

/*
 $Id: SpawnProcessTag.java,v 1.2 2002-07-18 18:32:58 bob Exp $

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
import com.werken.blissed.Context;
import com.werken.blissed.ActivityException;
import com.werken.blissed.InvalidMotionException;

import org.apache.commons.jelly.XMLOutput;
import org.apache.commons.jelly.JellyContext;
import org.apache.commons.jelly.JellyException;
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
 */
public class SpawnProcessTag extends BlissedTagSupport 
{
    // ------------------------------------------------------------
    //     Instance members
    // ------------------------------------------------------------

    /** Name of the process to spawn. */
    private String name;

    // ------------------------------------------------------------
    //     Constructors
    // ------------------------------------------------------------

    /** Construct.
     */
    public SpawnProcessTag()
    {
        // intentionally left blank
    }

    // ------------------------------------------------------------
    //     Instance methods
    // ------------------------------------------------------------

    /** Set the name of the process to spawn.
     *
     *  @param name The name of the process.
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /** Accept the context into the process.
     *
     *  @param process The process.
     *  @param context The context.
     *
     *  @throws InvalidMotionException If an invalid motion occurs.
     *  @throws ActivityException If an error occurs while performing an activity.
     */
    void accept(Process process,
                Context blissedContext) throws InvalidMotionException, ActivityException
    {
        JellyContext jellyContext = null;

        if (blissedContext.getParent() == null)
        {
            jellyContext = getContext();
        }
        else
        {
            jellyContext = new JellyContext( getContext() );

            jellyContext.setExport( false );
            jellyContext.setInherit( true );
        }

        jellyContext.setVariable( "blissed.context",
                                  blissedContext );

        blissedContext.setVariable( "jelly.context",
                                    jellyContext);

        process.accept( blissedContext );
          
    }

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
    //     org.apache.commons.jelly.Tag
    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 

    /** Evaluates this tag after all the tags properties
     *  have been initialized.
     *
     *  @task This should handle spawning children flows.
     *
     *  @param output The output sink.
     *
     *  @throws Exception if an error occurs.
     */
    public void doTag(XMLOutput output) throws Exception
    {
        if ( this.name == null )
        {
            throw new MissingAttributeException( "name" );
        }

        invokeBody( output );

        final Process process = getProcess( this.name );

        if ( process == null )
        {
            throw new JellyException( "No such process \"" + this.name + "\"" );
        }

        Context parent = (Context) getContext().getVariable( "blissed.context" );

        Context tmpContext = null;

        if ( parent == null )
        {
            tmpContext = process.spawn();
        }
        else
        {
            tmpContext = process.spawn( parent );
        }

        final Context context = tmpContext;

        Thread thread = new Thread() {
                public void run()
                {
                    try
                    {
                        accept( process,
                                context );
                    }
                    catch (InvalidMotionException e)
                    {
                        e.printStackTrace();
                    }
                    catch (ActivityException e)
                    {
                        e.printStackTrace();
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            };
        
        thread.start();
    }

}
