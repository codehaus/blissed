package org.codehaus.blissed.jelly;

/*
 $Id: StateTag.java,v 1.1 2003-06-04 15:15:04 proyal Exp $

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

import org.codehaus.blissed.Process;
import org.codehaus.blissed.State;
import org.codehaus.blissed.Described;
import org.codehaus.blissed.DuplicateStateException;

import org.apache.commons.jelly.XMLOutput;
import org.apache.commons.jelly.JellyTagException;

/** Create a new state.
 *
 *  @author <a href="mailto:bob@eng.werken.com">bob mcwhirter</a>
 */
public class StateTag extends DefinitionTagSupport implements DescribedTag
{
    // ------------------------------------------------------------
    //     Instance members
    // ------------------------------------------------------------

    /** The state name. */
    private String name;

    /** The state description. */
    private String description;

    /** The state. */
    private State state;

    /** Is this a terminal state? */
    private boolean terminal;

    // ------------------------------------------------------------
    //     Constructors
    // ------------------------------------------------------------

    /** Construct.
     */
    public StateTag()
    {
        this.description = "";
        this.terminal = false;
    }

    // ------------------------------------------------------------
    //     Instance methods
    // ------------------------------------------------------------

    /** Set the state name.
     *
     *  @param name The name.
     */
    public void setName( String name )
    {
        this.name = name;
    }

    /** Retrieve the <code>State</code>.
     *
     *  @return The <code>State</code>.
     */
    public State getState()
    {
        return this.state;
    }

    /** Set the flag that indicates if this state is
     *  a terminal state.
     *
     *  @param terminal <code>true</code> to indicate that
     *         this state is a terminal state, otherwise
     *         <code>false</code>.
     */
    public void setTerminal( boolean terminal )
    {
        this.terminal = terminal;
    }

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    //     org.codehaus.blissed.jelly.DescribedTag
    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

    /** Retrieve the current in-scope described object.
     *
     *  @return The in-scope described object.
     */
    public Described getDescribed()
    {
        return getState();
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
        Process process = getCurrentProcess();

        checkStringAttribute( "name",
                              this.name );

        try
        {
            this.state = process.addState( this.name,
                                           this.description );
        }
        catch( DuplicateStateException e )
        {
            throw new JellyTagException( "Unable to add state '" + this.name + "' to process", e );
        }

        invokeBody( output );

        if( this.terminal )
        {
            this.state.addTransition( process.getTerminalState(),
                                      "terminal transition" );
        }
    }
}
