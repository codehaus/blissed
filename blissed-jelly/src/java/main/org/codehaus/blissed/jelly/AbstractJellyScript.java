package org.codehaus.blissed.jelly;

/*
 $Id: AbstractJellyScript.java,v 1.1 2003-06-11 00:24:40 proyal Exp $

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

import java.io.UnsupportedEncodingException;

import org.apache.commons.jelly.Script;
import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.JellyContext;
import org.apache.commons.jelly.XMLOutput;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.beanutils.PropertyUtils;

import org.codehaus.blissed.ProcessContext;

/**
 *  Abstract class to help with running a Jelly <code>Script</code>.
 *
 *  <p>
 *  If a ProcessContext has data associated with it, an attempt will be made to treat that
 *  data as a bean, and add all properties to the JellyContext
 *  </p>
 *
 *  @see PropertyUtils#describe
 *
 *  @author <a href="mailto:proyal@codehaus.org">peter royal</a>
 *
 *  @version $Id $
 */
public abstract class AbstractJellyScript
{
    /** The Log to which logging calls will be made. */
    protected static final Log log = LogFactory.getLog( AbstractJellyScript.class );

    /** The jelly script. */
    private Script script;

    /** The parent JellyContext */
    private JellyContext parentContext;

    public JellyContext getParentContext()
    {
        return parentContext;
    }

    public void setParentContext( JellyContext parentContext )
    {
        this.parentContext = parentContext;
    }

    /** Retrieve the Jelly script.
     *
     *  @return The jelly script.
     */
    public Script getScript()
    {
        return this.script;
    }

    public void setScript( Script script )
    {
        this.script = script;
    }

    protected void runScript( final ProcessContext context )
        throws UnsupportedEncodingException, JellyTagException
    {
        final JellyContext jellyContext = createJellyContext();

        if( null != context )
        {
            final Object processData = context.getProcessData();

            if( null != processData )
            {
                try
                {
                    jellyContext.setVariables( PropertyUtils.describe( processData ) );
                }
                catch( Exception e )
                {
                    log.warn( "Unable to describe process data for JellyContext", e );
                }
            }
        }

        jellyContext.setVariable( RuntimeTagSupport.PROCESS_CONTEXT_KEY, context );

        getScript().run( jellyContext,
                         XMLOutput.createXMLOutput( System.err, false ) );
    }

    private JellyContext createJellyContext()
    {
        if( null == getParentContext() )
        {
            return new JellyContext();
        }
        else
        {
            return new JellyContext( getParentContext() );
        }
    }
}
