package org.codehaus.blissed.jelly;

/*
 $Id: JellyGuard.java,v 1.3 2003-06-11 00:24:40 proyal Exp $

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

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.Script;

import org.codehaus.blissed.Guard;
import org.codehaus.blissed.ProcessContext;
import org.codehaus.blissed.Transition;

/**
 *  Transition <code>Guard</code> implemented using a Jelly <code>Script</code>.
 *
 *  <p>
 *  Within the jelly script, the tags &lt;pass&gt; and &lt;fail&gt; can be
 *  used to immediately signal guard passage or failure and circumvent the
 *  evaluation of the remainder of the script.  If the script processes entirely
 *  without encountering an exception, it is considered to have passed, and
 *  {@link #test} returns <code>true</code>.
 *  </p>
 *
 *  <p>
 *  If a ProcessContext has data associated with it, an attempt will be made to treat that
 *  data as a bean, and add all properties to the JellyContext
 *  </p>
 *
 *  @see BeanUtils#describe
 *
 *  @author <a href="mailto:bob@eng.werken.com">bob mcwhirter</a>
 *  @author <a href="mailto:proyal@codehaus.org">peter royal</a>
 *
 *  @version $Id $
 */
public class JellyGuard extends AbstractJellyScript implements Guard
{
    // ------------------------------------------------------------
    //     Constructors
    // ------------------------------------------------------------

    /** Construct.
     *
     *  @param script The jelly guard script.
     */
    public JellyGuard( Script script )
    {
        setScript( script );
    }

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    //     org.codehaus.blissed.Guard
    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

    /** Test this guard against a procession.
     *
     *  @param transition The transition this guard guards.
     *  @param context The process context.
     *
     *  @return <code>true</code> if the procession passes
     *          this guard, otherwise <code>false</code>.
     */
    public boolean test( Transition transition,
                         ProcessContext context )
    {
        try
        {
            runScript( context );
        }
        catch( JellyTagException e )
        {
            final Throwable cause = e.getCause();

            if( cause instanceof PassException )
            {
                return true;
            }
            else if( cause instanceof FailException )
            {
                return false;
            }
            else
            {
                return false;
            }
        }
        catch( UnsupportedEncodingException e )
        {
            throw new RuntimeException( "Unable to create Jelly XMLOutput: " + e.getMessage() );
        }

        return true;
    }
}
