package org.codehaus.blissed.jelly;

/*
 $Id: BlissedTagSupport.java,v 1.1 2003-06-04 15:15:04 proyal Exp $

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

import org.apache.commons.jelly.TagSupport;
import org.apache.commons.jelly.MissingAttributeException;

/** Base of all blissed jelly tags.
 *
 *  @author <a href="mailto:bob@eng.werken.com">bob mcwhirter</a>
 *
 *  @version $Id: BlissedTagSupport.java,v 1.1 2003-06-04 15:15:04 proyal Exp $
 */
public abstract class BlissedTagSupport extends TagSupport
{
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

    /** Check a string attribute to ensure it is not <code>null</code>
     *  or the empty string.
     *
     *  @param name The name of the attribute.
     *  @param value The value of the attribute.
     *
     *  @throws MissingAttributeException If the value is either <code>null</code>
     *          or the empty string.
     */
    protected void checkStringAttribute(String name,
                                        String value) throws MissingAttributeException
    {
        checkObjectAttribute( name,
                              value );

        if ( value.trim().equals( "" ) )
        {
            throw new MissingAttributeException( name );
        }
    }

    /** Check an object attribute to ensure that it is not <code>null</code>.
     *
     *  @param name The attribute name.
     *  @param value The attribute value.
     *
     *  @throws MissingAttributeException If the value is <code>null</code>.
     */
    protected void checkObjectAttribute(String name,
                                        Object value) throws MissingAttributeException
    {
        if ( value == null )
        {
            throw new MissingAttributeException( name );
        }
    }
}
