package com.werken.blissed;

/*
 $Id: Split.java,v 1.2 2002-07-02 15:40:12 werken Exp $

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

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

/** A node that splits the flow into parallel flows.
 *
 *  @author <a href="mailto:bob@eng.werken.com">bob mcwhirter</a>
 */
public class Split extends Node
{
    // ------------------------------------------------------------
    //     Instance members
    // ------------------------------------------------------------

    /** Destinations after the split. */
    private List destinations;

    // ------------------------------------------------------------
    //     Constructors
    // ------------------------------------------------------------

    /** Construct.
     *
     *  @param process The process to which this node belongs.
     *  @param name The name of this node.
     *  @param description The description of this node.
     */
    Split(Process process,
          String name,
          String description)
    {
        super( process,
               name,
               description );

        this.destinations = new ArrayList();
    }

    // ------------------------------------------------------------
    //     Instance methods
    // ------------------------------------------------------------

    /** Add a destination to this split.
     *
     *  @param destination The destination to add.
     */
    public void addDestination(Node destination)
    {
        this.destinations.add( destination );
    }

    /** Remove a destination from this split.
     *
     *  @param destination The destination to remove.
     */
    public void removeDestination(Node destination)
    {
        this.destinations.remove( destination );
    }

    /** Retrieve a <b>live</b> list of destinations.
     *
     *  <p>
     *  The <b>live</b> list returned by this method
     *  is backed by the <code>Split</code>.  Any changes
     *  made to the list are immediately reflected
     *  internally to the <code>Split</code>.
     *  </p>
     *
     *  @return The list of destinations.
     */
    public List getDestinations()
    {
        return this.destinations;
    }

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
    //     com.werken.blissed.Node
    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 

    public void accept(WorkSlip workSlip)
    {
        Iterator destIter = getDestinations().iterator();
        Node     eachDest = null;

        while ( destIter.hasNext() )
        {
            eachDest = (Node) destIter.next();

            eachDest.accept( workSlip.createSplitWorkSlip() );
        }
    }
}
