package org.codehaus.blissed.jelly;

/*
 $Id: DefinitionTagSupport.java,v 1.1 2003-06-04 15:15:04 proyal Exp $

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

import org.codehaus.blissed.Described;
import org.codehaus.blissed.Process;
import org.codehaus.blissed.State;

import org.apache.commons.jelly.JellyException;
import org.apache.commons.jelly.JellyTagException;

/** Base of process definition jelly tags.
 *
 *  @author <a href="mailto:bob@eng.werken.com">bob mcwhirter</a>
 *
 *  @version $Id: DefinitionTagSupport.java,v 1.1 2003-06-04 15:15:04 proyal Exp $
 */
public abstract class DefinitionTagSupport extends BlissedTagSupport
{
    // ------------------------------------------------------------
    //     Constructors
    // ------------------------------------------------------------

    /** Construct.
     */
    protected DefinitionTagSupport()
    {
        // intentionally left blank
    }

    // ------------------------------------------------------------
    //     Instance methods
    // ------------------------------------------------------------

    /** Retrieve the currently in-scope <code>Process</code>.
     *
     *  @return The currently in-scope process.
     *
     *  @throws JellyTagException If an in-scope process does not exist.
     */
    protected Process getCurrentProcess() throws JellyTagException
    {
        ProcessTag processTag = (ProcessTag) findAncestorWithClass( ProcessTag.class );

        if ( processTag == null )
        {
            throw new JellyTagException( "Not within a process element" );
        }
        
        Process process = processTag.getProcess();

        return process;
    }

    /** Retrieve the currently in-scope <code>State</code>.
     *
     *  @return The currently in-scope state.
     *
     *  @throws JellyException If an in-scope state does not exist.
     */
    protected State getCurrentState() throws JellyException
    {
        StateTag stateTag = (StateTag) findAncestorWithClass( StateTag.class );

        if ( stateTag == null )
        {
            throw new JellyException( "Not within a state element" );
        }
        
        State state = stateTag.getState();

        return state;
    }

    /** Retrieve the currently in-scope <code>Described</code>.
     *
     *  @return The currently in-scope described object.
     *
     *  @throws JellyTagException If an in-scope described object does not exist.
     */
    protected Described getCurrentDescribed() throws JellyTagException
    {
        DescribedTag describedTag = (DescribedTag) findAncestorWithClass( DescribedTag.class );

        if ( describedTag == null )
        {
            throw new JellyTagException( "Unable to locate an element to describe" );
        }

        Described described = describedTag.getDescribed();

        return described;
    }
}
