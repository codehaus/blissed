package org.codehaus.blissed.jelly;

/*
 $Id: ProcessTag.java,v 1.1 2003-06-04 15:15:04 proyal Exp $

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

import org.apache.commons.jelly.XMLOutput;
import org.apache.commons.jelly.JellyTagException;

/** Create a new process.
 *
 *  @author <a href="mailto:bob@eng.werken.com">bob mcwhirter</a>
 */
public class ProcessTag extends DefinitionTagSupport implements DescribedTag
{
    // ------------------------------------------------------------
    //     Instance members
    // ------------------------------------------------------------

    /** The process name. */
    private String name;

    /** The process. */
    private Process process;

    /** The start state name. */
    private String start;

    /** Storage variable name. */
    private String var;

    // ------------------------------------------------------------
    //     Constructors
    // ------------------------------------------------------------

    /** Construct.
     */
    public ProcessTag()
    {
        // intentionally left blank
    }

    // ------------------------------------------------------------
    //     Instance members
    // ------------------------------------------------------------

    /** Set the process name.
     *
     *  @param name The name.
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /** Retrieve the process name.
     *
     *  @return The name.
     */
    public String getName()
    {
        return this.name;
    }

    /** Set the first state of the process.
     *
     *  @param start The name of the start state.
     */
    public void setStart(String start)
    {
        this.start = start;
    }

    /** Retrieve the name of the first state of the process.
     *
     *  @return The name of the start state.
     */
    public String getStart()
    {
        return this.start;
    }

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
    //     org.codehaus.blissed.jelly.BlissedTag
    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 

    /** Retrieve the current in-scope process.
     *
     *  @return The current in-scope process or <code>null</code>.
     */
    public Process getProcess()
    {
        return this.process;
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
        return getProcess();
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
        checkStringAttribute( "name",
                              getName() );

        checkStringAttribute( "start",
                              getStart() );

        this.process = new Process( getName(),
                                    "" );

        invokeBody( output );

        State startState = this.process.getState( getStart() );

        if ( startState == null )
        {
            throw new JellyTagException( "Start state \"" + getStart() + "\" not found." );
        }

        this.process.setStartState( startState );

        String var = getVar();

        if ( var != null )
        {
            getContext().setVariable( var,
                                      this.process );
        }
    }
}
