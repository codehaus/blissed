package com.werken.blissed;

/*
 $Id: BlissedException.java,v 1.2 2002-07-04 19:40:07 werken Exp $

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

/** Base blissed exception.
 *
 *  @author <a href="mailto:bob@eng.werken.com">bob mcwhirter</a>
 */
public class BlissedException extends Exception
{
    // ------------------------------------------------------------
    //     Instance members
    // ------------------------------------------------------------

    /** Root cause, if any. */
    private Throwable rootCause;

    // ------------------------------------------------------------
    //     Constructors
    // ------------------------------------------------------------

    /** Construct.
     */
    protected BlissedException()
    {
        // intentionally left blank
    }

    /** Construct with a root cause.
     *
     *  @param rootCause The root cause.
     */
    protected BlissedException(Throwable rootCause)
    {
        this.rootCause = rootCause;
    }

    protected BlissedException(String msg)
    {
        super( msg );
    }

    // ------------------------------------------------------------
    //     Instance methods
    // ------------------------------------------------------------

    /** Retrieve the root cause.
     *
     *  @see #hasRootCause
     *
     *  @return The root cause, or <code>null</code> if
     *          this exception does not wrap another.
     */
    public Throwable getRootCause()
    {
        return this.rootCause;
    }

    /** Determine if this exceptions wraps another.
     *
     *  @return <code>true</code> if this exception wraps
     *          another exception, otherwise <code>false</code>.
     */
    public boolean hasRootCause()
    {
        return ( this.rootCause != null );
    }
}

