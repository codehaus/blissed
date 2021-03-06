package org.codehaus.blissed;

/*
 $Id: ProcessContext.java,v 1.1 2003-06-04 15:15:04 proyal Exp $

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

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/** Context for an instance of a <code>Process</code>.
 *
 *  @author <a href="mailto:bob@eng.werken.com">bob mcwhirter</a>
 *
 *  @version $Id: ProcessContext.java,v 1.1 2003-06-04 15:15:04 proyal Exp $
 */
public class ProcessContext
{
    // ------------------------------------------------------------
    //     Constants
    // ------------------------------------------------------------

    static final short PROCESS_NOT_STARTED = 0;
    static final short PROCESS_RUNNING     = 1;
    static final short PROCESS_FINISHED    = 2;

    // ------------------------------------------------------------
    //     Instance members
    // ------------------------------------------------------------

    /** Process engine. */
    private ProcessEngine engine;

    /** Parent ProcessContext, if this is spawned. */
    private ProcessContext parent;

    /** Children ProcessContext, if any. */
    private Set children;

    /** Location tracking. */
    private Location location;

    /** Process data. */
    private Object processData;

    // ------------------------------------------------------------
    //     Constructors
    // ------------------------------------------------------------

    /** Construct a root <code>ProcessContext</code>.
     *
     *  @param engine The process engine that instantiated this context.
     */
    ProcessContext( ProcessEngine engine )
    {
        this( engine,
              null );
    }

    /** Construct a nested <code>ProcessContext</code>.
     *
     *  @param engine The process engine that instantiated this context.
     *  @param parent The parent of this ProcessContext.
     */
    ProcessContext( ProcessEngine engine,
                    ProcessContext parent )
    {
        this.engine = engine;
        this.parent = parent;

        this.children = Collections.EMPTY_SET;

        this.location = new Location();
    }

    // ------------------------------------------------------------
    //     Instance methods
    // ------------------------------------------------------------

    /** Retrieve the <code>ProcessEngine</code>.
     *
     *  @return The process engine.
     */
    public ProcessEngine getProcessEngine()
    {
        return this.engine;
    }

    /** Retrieve the parent of this ProcessContext.
     *
     *  @return The parent of this ProcessContext, or <code>null</code>
     *          if this is a root-level ProcessContext.
     */
    public ProcessContext getParent()
    {
        return this.parent;
    }

    /** Retrieve the process-specific data.
     *
     *  @return The process data.
     */
    public Object getProcessData()
    {
        return this.processData;
    }

    /** Set the process-specific data.
     *
     *  @param processData The process data.
     */
    public void setProcessData( Object processData )
    {
        this.processData = processData;
    }

    /** Add a child ProcessContext to this ProcessContext.
     *
     *  @param child The child to add.
     */
    void addChild( ProcessContext child )
    {
        if( this.children == Collections.EMPTY_SET )
        {
            this.children = new HashSet();
        }

        this.children.add( child );
    }

    /** Retrieve an unmodifiable set of all children
     *  ProcessContexts of this ProcessContext.
     *
     *  @return An unmodifiable <code>Set</code> of children
     *          <code>ProcessContext</code>s.
     */
    public Set getChildren()
    {
        return Collections.unmodifiableSet( this.children );
    }

    /** Retrieve the location-management object.
     *
     *  @return The location-management object.
     */
    Location getLocation()
    {
        return this.location;
    }

    /** Retrieve the ProcessContext's current state location.
     *
     *  @return The ProcessContext's current state location.
     */
    public State getCurrentState()
    {
        return getLocation().getCurrentState();
    }

    /** Retrieve the ProcessContext's current process location.
     *
     *  @return The ProcessContext's current process location.
     */
    public Process getCurrentProcess()
    {
        return getLocation().getCurrentProcess();
    }

    public void recheckTransitions()
    {
        getProcessEngine().addToCheckTransitionsQueue( this );
    }

    void startProcess( Process process ) throws InvalidMotionException
    {
        getLocation().startProcess( process );
    }

    void enterState( State state ) throws InvalidMotionException
    {
        getLocation().enterState( state );
    }

    void exitState( State state ) throws InvalidMotionException
    {
        getLocation().exitState( state );
    }

    void finishProcess( Process process ) throws InvalidMotionException
    {
        getLocation().finishProcess( process );
    }

    boolean isStatus( short status )
    {
        return getLocation().isStatus( status );
    }
}


