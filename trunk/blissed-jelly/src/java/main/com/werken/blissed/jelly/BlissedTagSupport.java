package com.werken.blissed.jelly;

/*
 $Id: BlissedTagSupport.java,v 1.4 2002-09-17 05:13:34 bob Exp $

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

import com.werken.blissed.ProcessContext;
import com.werken.blissed.ProcessEngine;

import org.apache.commons.jelly.TagSupport;

/** Base of all blissed jelly tags.
 *
 *  @author <a href="mailto:bob@eng.werken.com">bob mcwhirter</a>
 */
public abstract class BlissedTagSupport extends TagSupport
{
    // ------------------------------------------------------------
    //   Constants
    // ------------------------------------------------------------

    /** Key under which the <code>ProcessContext</code> is stored
     *  within the <code>JellyContext</code>.
     */
    public static final String PROCESS_CONTEXT_KEY = "processContext";

    // ------------------------------------------------------------
    //   Constructors
    // ------------------------------------------------------------

    /** Construct.
     */
    protected BlissedTagSupport()
    {
        // intentionally left blank
    }

    // ------------------------------------------------------------
    //   Instance methods
    // ------------------------------------------------------------

    /** Retrieve the <code>ProcessEngine</code> of the current
     *  <code>ProcessContext</code>.
     *
     *  @return The process engine.
     */
    protected ProcessEngine getProcessEngine()
    {
        return getProcessContext().getProcessEngine();
    }

    /** Retrieve the current <code>ProcessContext</code>.
     *
     *  @return The current process context, or <code>null</code>
     *          if no process context is currently in-scope.
     */
    protected ProcessContext getProcessContext()
    {
        return (ProcessContext) getContext().getVariable( PROCESS_CONTEXT_KEY );
    }
}
