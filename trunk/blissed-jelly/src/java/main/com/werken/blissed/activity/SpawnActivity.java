package com.werken.blissed.activity;

/*
 $Id: SpawnActivity.java,v 1.2 2002-09-16 14:59:51 bob Exp $

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

import com.werken.blissed.Activity;
import com.werken.blissed.Process;
import com.werken.blissed.ProcessContext;
import com.werken.blissed.ProcessEngine;
import com.werken.blissed.ActivityException;
import com.werken.blissed.InvalidMotionException;

/** An <code>Activity</code> that spawns another <code>Process</code>.
 *
 *  @author <a href="mailto:bob@eng.werken.com">bob mcwhirter</a>
 *
 *  @version $Id: SpawnActivity.java,v 1.2 2002-09-16 14:59:51 bob Exp $
 */
public class SpawnActivity implements Activity
{
    // ------------------------------------------------------------
    //     Instance members
    // ------------------------------------------------------------

    /** The process to spawn. */
    private Process process;

    // ------------------------------------------------------------
    //     Constructors
    // ------------------------------------------------------------

    /** Construct.
     *
     *  @param process The process to spawn.
     */
    public SpawnActivity(Process process)
    {
        this.process = process;
    }

    // ------------------------------------------------------------
    //     Instance methods
    // ------------------------------------------------------------

    /** Retrieve the <code>Process</code> to call.
     *
     *  @return The process to call.
     */
    public Process getProcess()
    {
        return this.process;
    }

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
    //     com.werken.blissed.Activity
    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 

    /** Perform this activity within the specified procession.
     *
     *  @param context The process context
     *
     *  @throws ActivityException if an error occurs.
     */
    public void perform(ProcessContext context) throws ActivityException
    {
        ProcessEngine engine = context.getProcessEngine();

        try
        {
            engine.spawn( getProcess(),
                          context );
        }
        catch (InvalidMotionException e)
        {
            throw new ActivityException( e );
        }
    }
}