package com.werken.blissed;

import com.werken.blissed.activity.NoOpActivity;

import junit.framework.TestCase;

import java.util.Set;
import java.util.List;

public class StateTest extends TestCase
{
    public StateTest(String name)
    {
        super( name );
    }

    public void setUp()
    {
    }

    public void tearDown()
    {
    }

    public void testGetActivity_Default()
    {
        State state = new State( "test.state",
                                 "test state" );

        assertSame( NoOpActivity.INSTANCE,
                    state.getActivity() );
    }

    public void testAddTransition_StateDesc()
    {
        State state1 = new State( "state.1",
                                  "state one" );

        State state2 = new State( "state.2",
                                  "state two" );

        state1.addTransition( state2,
                              "transition" );

        List transitions = state1.getTransitions();

        assertEquals( 1,
                      transitions.size() );

        Transition transition = (Transition) transitions.get( 0 );

        assertSame( state1,
                    transition.getOrigin() );

        assertSame( state2,
                    transition.getDestination() );

        Set inboundTransitions = state2.getInboundTransitions();

        assertEquals( 1,
                      inboundTransitions.size() );

        Transition inboundTransition = (Transition) inboundTransitions.iterator().next();

        assertSame( transition,
                    inboundTransition );
    }
}
