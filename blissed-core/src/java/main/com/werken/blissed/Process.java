package com.werken.blissed;

/*
 $Id: Process.java,v 1.1.1.1 2002-07-02 14:28:07 werken Exp $

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

import java.util.Map;
import java.util.HashMap;

/** A graph of nodes and transitions in the form of a
 *  state machine.
 *
 *  @author <a href="mailto:bob@eng.werken.com">bob mcwhirter</a>
 */
public class Process implements Named, Described
{
    // ------------------------------------------------------------
    //     Instance members
    // ------------------------------------------------------------

    /** Name of this process. */
    private String name;

    /** Description of this process. */
    private String description;

    /** Nodes in this process, indexed by <code>name</code>. */
    private Map nodes;

    /** Start node. */
    private Start start;

    /** Finish node. */
    private Finish finish;

    // ------------------------------------------------------------
    //     Constructors
    // ------------------------------------------------------------

    /** Construct a new process.
     *
     *  @param name The name of the process.
     *  @param description A description of the process.
     */
    Process(String name,
            String description)
    {
        this.name        = name;
        this.description = description;

        this.nodes = new HashMap();

        this.start  = new Start( this );
        this.finish = new Finish( this );
    }

    // ------------------------------------------------------------
    //     Instance methods
    // ------------------------------------------------------------

    /** Retrieve the entry-point start node of this process.
     *
     *  @return The entry-point start node.
     */
    public Start getStart()
    {
        return this.start;
    }

    /** Retrieve the exit-point finish node of this process.
     *
     *  @return The exit-point finish node.
     */
    public Finish getFinish()
    {
        return this.finish;
    }

    /** Add a node to this process.
     *
     *  @param node The node to add.
     */
    void addNode(Node node)
    {
        this.nodes.put( node.getName(),
                        node );
    }

    /** Retrieve a node in this process, by name.
     *
     *  @param name The name of the node to retrieve.
     *
     *  @return The named node, or <code>null</code> of no node
     *          with the given name is known to this process.
     */
    Node getNode(String name)
    {
        return (Node) this.nodes.get( name );
    }

    /** Remove a node from this process.
     *
     *  @param node The node to remove.
     */
    void removeNode(Node node)
    {
        this.nodes.remove( node.getName() );
    }

    /** Start a new instance of this process.
     *
     *  @return A new <code>WorkSlip</code> representing the state
     *          for the new instance of this process.
     */
    public WorkSlip start()
    {
        WorkSlip workSlip = new WorkSlip( this );

        return workSlip;
    }

    /** Start a new instance of this process.
     *
     *  @param parent The parent WorkSlip.
     *
     *  @return A new <code>WorkSlip</code> representing the state
     *          for the new instance of this process.
     */
    WorkSlip start(WorkSlip parent)
    {
        WorkSlip workSlip = new WorkSlip( this,
                                          parent );

        return workSlip;
    }
 
    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
    //     com.werken.blissed.Named
    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 

    /** Retrieve the name of this process.
     *
     *  @return The name of this process.
     */
    public String getName()
    {
        return this.name;
    }

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
    //     com.werken.blissed.Described
    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 

    /** Retrieve the description of this process.
     *
     *  @return The description of this process.
     */
    public String getDescription()
    {
        return this.description;
    }

}
