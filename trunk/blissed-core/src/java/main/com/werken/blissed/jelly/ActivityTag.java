package com.werken.blissed.jelly;

/*
 $Id: ActivityTag.java,v 1.10 2002-09-18 06:04:44 bob Exp $

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

import com.werken.blissed.State;
import com.werken.blissed.Activity;
import com.werken.blissed.activity.NoOpActivity;

import org.apache.commons.jelly.Script;
import org.apache.commons.jelly.XMLOutput;
import org.apache.commons.jelly.JellyException;

/** Create an activity.
 *
 *  @author <a href="mailto:bob@eng.werken.com">bob mcwhirter</a>
 *
 *  @version $Id: ActivityTag.java,v 1.10 2002-09-18 06:04:44 bob Exp $
 */
public class ActivityTag extends DefinitionTagSupport
{
    // ------------------------------------------------------------
    //     Instance members
    // ------------------------------------------------------------

    /** Storage variable. */
    private String var;

    // ------------------------------------------------------------
    //     Constructors
    // ------------------------------------------------------------

    /** Construct.
     */
    public ActivityTag()
    {
        // intentionally left blank
    }

    // ------------------------------------------------------------
    //     Instance methods
    // ------------------------------------------------------------

    /** Set the variable name in which to store the <code>Activity</code>.
     *
     *  @param var The variable name.
     */
    public void setVar(String var)
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
        System.err.println("ActivityTag.doTag()" );

        State state = null;

        try
        {     
            state = getCurrentState();
        }
        catch (JellyException e)
        {
            // ignore
        }

        if ( state != null )
        {
            if ( state.getActivity() != null
                 &&
                 ! ( state.getActivity() instanceof NoOpActivity ) )
            {
                throw new JellyException( "Activity already defined for state \""
                                          + state.getName()
                                          + "\"" );
            }
        }

        // - - - - - - - - - - - - - - - 

        Script script = getBody();
        
        Activity activity = new JellyActivity( script );

        if ( getVar() != null )
        {
            getContext().setVariable( getVar(),
                                      activity );
        }

        // - - - - - - - - - - - - - - - 

        if ( state != null )
        {
            state.setActivity( activity );
        }
    }
}
