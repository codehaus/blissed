package com.werken.blissed;

/*
 $Id: WorkSlip.java,v 1.1.1.1 2002-07-02 14:28:08 werken Exp $

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

import java.util.Date;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

/** Data for an instance of a <code>Process</code>.
 *
 *  @see Process
 *  @see Process#start
 * 
 *  @author <a href="mailto:bob@eng.werken.com">bob mcwhirter</a>
 */
public class WorkSlip implements Named
{
    /** Name of this WorkSlip. */
    private String name;

    /** Process of which this WorkSlip is an instance. */
    private Process process;

    /** Parent WorkSlip, if this is spawned. */
    private WorkSlip parent;

    /** Children WorkSlips, if any. */
    private List children;

    /** Instance attributes. */
    private Map attributes;

    /** Construct a root <code>WorkSlip</code>.
     *
     *  @param process The process of which this WorkSlip is an instance. 
     */
    WorkSlip(Process process)
    {
        this( process,
              null );
    }

    /** Construct a nested <code>WorkSlip</code>.
     *
     *  @param process The process of which this WorkSlip is an instance. 
     *  @param parent The parent of this WorkSlip.
     */
    WorkSlip(Process process,
             WorkSlip parent)
    {
        String name = null;

        if ( name == null )
        {
            name = "";
        }
        else
        {
            name = parent.getName() + ".";
        }

        name += process.getName() + "-" + ( new Date().getTime() );

        this.name    = name;
        this.process = process;
        this.parent  = parent;

        this.attributes = new HashMap();
        this.children = Collections.EMPTY_LIST;
    }

    /** Retrieve the name of this WorkSlip.
     *
     *  @return The name of this WorkSlip.
     */
    public String getName()
    {
        return this.name;
    }

    /** Retrieve the Process of this WorkSlip.
     *
     *  @return The process of this WorkSlip.
     */
    public Process getProcess()
    {
        return this.process;
    }

    /** Retrieve the parent of this WorkSlip.
     *
     *  @return The parent of this WorkSlip, or <code>null</code>
     *          if this is a root-level WorkSlip.
     */
    public WorkSlip getParent()
    {
        return this.parent;
    }

    /** Retrieve an attribute.
     *
     *  @param name The name of the attribute.
     *
     *  @return The attribute's value, or <code>null</code>
     *          if the attribute has not been set.
     */
    public Object getAttribute(String name)
    {
        return this.attributes.get( name );
    }

    /** Set an attribute.
     *
     *  @param name The name of the attribute.
     *  @param value The value of the attribute.
     */
    public void setAttribute(String name,
                             Object value)
    {
        this.attributes.put( name,
                             value );
    }

    /** Clean an attribute.
     *
     *  @param name The name of the attribute.
     */
    public void clearAttribute(String name)
    {
        this.attributes.remove( name );
    }

    /** Spawn a nested process.
     *
     *  @param process The process to spawn.
     *
     *  @return The spawned nested WorkSlip.
     */
    public WorkSlip start(Process process)
    {
        WorkSlip workSlip = new WorkSlip( process,
                                          this );
                                          
        if ( this.children == Collections.EMPTY_LIST )
        {
            this.children = new ArrayList();
        }

        this.children.add( workSlip );

        return workSlip;
    }
}
