package org.codehaus.blissed;

/*
 $Id: State.java,v 1.1 2003-06-04 15:15:04 proyal Exp $

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

import org.codehaus.blissed.activity.NoOpActivity;

import org.apache.commons.graph.Vertex;

import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;

/** A <code>Activity</code>-bearing state in the process graph.
 *
 *  <p>
 *  A <code>State</code> contains a <code>Activity</code> and
 *  a sequence of one-or-more <code>Transition</code>s
 *  denoting guarded exit paths.
 *  </p>
 *
 *  @see Activity
 *  @see Transition
 *
 *  @author <a href="mailto:bob@eng.werken.com">bob mcwhirter</a>
 */
public class State implements Named, Described, Vertex
{
    // ------------------------------------------------------------
    //     Instance members
    // ------------------------------------------------------------

    /** The name of this. */
    private String name;

    /** The description of this state. */
    private String description;

    /** The activity. */
    private Activity activity;

    /** The exit-path transitions. */
    private List outbound;

    /** Entry-path transitions */
    private Set inbound;

    // ------------------------------------------------------------
    //     Constructors
    // ------------------------------------------------------------

    /** Construct.
     *
     *  @param name The name of this state.
     *  @param description The description of this state.
     */
    State(String name,
          String description)
    {
        this.name        = name;
        this.description = description;

        this.activity    = NoOpActivity.INSTANCE;
        this.outbound    = new ArrayList();
        this.inbound     = new HashSet();
    }

    // ------------------------------------------------------------
    //     Instance methods
    // ------------------------------------------------------------

    /** Add an exit path transition.
     *
     *  @param transition The transition to add.
     */
    void addTransition(Transition transition)
    {
        this.outbound.add( transition );

        State destination = transition.getDestination();

        if ( destination != null )
        {
            transition.getDestination().addInboundTransition( transition );
        }
    }

    /** Add an inbound path transition.
     *
     *  @param transition The transition to add.
     */
    void addInboundTransition(Transition transition)
    {
        this.inbound.add( transition );
    }

    /** Remove an exit path transition.
     *
     *  @param transition The transition to remove.
     */
    public void removeTransition(Transition transition)
    {
        this.outbound.remove( transition );

        State destination = transition.getDestination();

        if ( destination != null )
        {
            destination.removeInboundTransition( transition );
        }
    }

    /** Remove an inbound path transition.
     *
     *  @param transition The transition to remove.
     */
    void removeInboundTransition(Transition transition)
    {
        this.inbound.remove( transition );
    }

    /** Remove all transitions and clean up both endpoints.
     */
    void clearTransitions()
    {
        List       trans     = new ArrayList( getTransitions() );
        Iterator   transIter = trans.iterator();
        Transition eachTrans = null;

        while ( transIter.hasNext() )
        {
            eachTrans = (Transition) transIter.next();

            removeTransition( eachTrans );
        }
    }

    /** Create a transition.
     *
     *  @param destination The destination of the transition.
     *  @param description Description of the transition.
     *
     *  @return The added transition.
     */
    public Transition addTransition(State destination,
                                    String description)
    {
        return addTransition( destination,
                              null,
                              description );
    }

    /** Create a transition.
     *
     *  @param destination The destination of the transition.
     *  @param guard Guard of the transition.
     *  @param description Description of the transition.
     *
     *  @return The added transition.
     */
    public Transition addTransition(State destination,
                                    Guard guard,
                                    String description)
    {
        Transition transition = new Transition( this,
                                                destination,
                                                guard,
                                                description );
        
        addTransition( transition );
        
        return transition;   
    }

    /** Retrieve the <b>live</b> list of outbound.
     *
     *  <p>
     *  The <b>live</b> list that is returned is backed
     *  directly by the <code>State</code>.  Changes made
     *  to the list are reflected internally within the
     *  <code>State</code>.
     *  </p>
     *
     *  @return The <code>List</code> of <code>Transition</code>s.
     */
    public List getTransitions()
    {
        return this.outbound;
    }

    /** Retrieve the set of inbound transitions
     *
     *  @return The <code>Set</code> of inbound <code>Transitions</code>.
     */
    Set getInboundTransitions()
    {
        return this.inbound;
    }

    /** Set the <code>Activity</code> for this state.
     *
     *  @param activity The activity.
     */
    public void setActivity(Activity activity)
    {
        this.activity = activity;
    }

    /** Retrieve the <code>Activity</code> for this state.
     *
     *  @return The activity.
     */
    public Activity getActivity()
    {
        return this.activity;
    }

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
    //     org.codehaus.blissed.Named
    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 

    /** Retrieve the name of this state.
     *
     *  @return The name.
     */
    public String getName()
    {
        return this.name;
    }

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
    //     org.codehaus.blissed.Described
    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 

    /** Retrieve the description of this state.
     *
     *  @return The description.
     */
    public String getDescription()
    {
        return this.description;
    }

    /** Set the description
     *
     *  @param description The description.
     */
    public void setDescription(String description)
    {
        this.description = description;
    }

    // ------------------------------------------------------------
    //     Inner classes
    // ------------------------------------------------------------

    /** Mostly read-only terminal state class.
     */
    static class TerminalState extends State
    {
        /** Construct.
         */
        TerminalState()
        {
            super( "state.terminal",
                   "terminal state" );
        }

        /** Set description.
         *
         *  @param description The description.
         */
        public void setDescription(String description)
        {
            // intentionally left blank
        }

        /** Set the activity.
         *
         *  @param activity The activity.
         */
        public void setActivity(Activity activity)
        {
            // intentionally left blank
        }

        /** Add a transition.
         *
         *  @param destination The destination.
         *  @param description The description.
         *
         *  @return The new transition.
         */
        public Transition addTransition(State destination,
                                        String description)
        {
            return null;
        };

        /** Add a transition.
         *
         *  @param destination The destination.
         *  @param guard The guard.
         *  @param description The description.
         *
         *  @return The new transition.
         */
        public Transition addTransition(State destination,
                                        Guard guard,
                                        String description)
        {
            return null;
        };
    };
}
