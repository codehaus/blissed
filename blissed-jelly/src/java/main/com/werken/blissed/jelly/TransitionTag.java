package com.werken.blissed.jelly;

/*
 $Id: TransitionTag.java,v 1.5 2002-07-18 05:22:50 bob Exp $

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
import com.werken.blissed.State;
import com.werken.blissed.Transition;
import com.werken.blissed.Described;

import org.apache.commons.jelly.XMLOutput;
import org.apache.commons.jelly.JellyException;
import org.apache.commons.jelly.MissingAttributeException;

/** Create a transition.
 *
 *  @author <a href="mailto:bob@eng.werken.com">bob mcwhirter</a>
 */
public class TransitionTag extends BlissedTagSupport implements DescribedTag
{
    // ------------------------------------------------------------
    //     Instance members
    // ------------------------------------------------------------

    /** From state name. */
    private String from;

    /** To state name. */
    private String to;

    /** The transition description. */
    private String description;

    /** The transition. */
    private Transition transition;

    // ------------------------------------------------------------
    //     Constructors
    // ------------------------------------------------------------

    /** Construct.
     */
    public TransitionTag()
    {
        this.description = "";
    }

    // ------------------------------------------------------------
    //     Instance methods
    // ------------------------------------------------------------

    /** Set the origin state name.
     *
     *  @param from The origin state name.
     */
    public void setFrom(String from)
    {
        this.from = from;
    }

    /** Set the destination state name.
     *
     *  @param to The destination state name.
     */
    public void setTo(String to)
    {
        this.to = to;
    }

    /** Retrieve the transition.
     *
     *  @return The transition.
     */
    public Transition getTransition()
    {
        return this.transition;
    }

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
    //     com.werken.blissed.Described
    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 

    /** Retrieve the described object.
     *
     *  @return The described object.
     */
    public Described getDescribed()
    {
        return getTransition();
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
        ProcessTag processTag = (ProcessTag) findAncestorWithClass( ProcessTag.class );

        if ( processTag == null )
        {
            throw new JellyException( "Not within a process element" );
        }
        
        Process process = processTag.getProcess();

        if ( this.from == null )
        {
            throw new MissingAttributeException( "from" );
        }

        if ( this.to == null )
        {
            throw new MissingAttributeException( "to" );
        }

        State fromState = process.getState( this.from );

        if ( fromState == null )
        {
            throw new JellyException( "No such state \"" + this.from  + "\"" );
        }

        State toState = process.getState( this.to );

        if ( toState == null )
        {
            throw new JellyException( "No such state \"" + this.to  + "\"" );
        }

        this.transition = fromState.addTransition( toState,
                                                   this.description );

        invokeBody( output );
    }
}
