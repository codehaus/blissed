package com.werken.blissed;

import com.werken.blissed.activity.NoOpActivity;
import com.werken.blissed.guard.BooleanGuard;

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

    public void testRemoveTransition_ToState()
    {
        State state1 = new State( "state.1",
                                  "state one" );

        State state2 = new State( "state.2",
                                  "state two" );

        Transition origTrans = state1.addTransition( state2,
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

        state1.removeTransition( origTrans );

        assertTrue( state1.getTransitions().isEmpty() );
        assertTrue( state2.getTransitions().isEmpty() );
    }

    public void testRemoveTransition_ToFinish()
    {
        State state1 = new State( "state.1",
                                  "state one" );

        Transition origTrans = state1.addTransition( null,
                                                     "transition" );

        List transitions = state1.getTransitions();

        assertEquals( 1,
                      transitions.size() );

        Transition transition = (Transition) transitions.get( 0 );

        assertSame( state1,
                    transition.getOrigin() );

        state1.removeTransition( origTrans );

        assertTrue( state1.getTransitions().isEmpty() );
    }

    public void testClearTransitions()
    {
        State state1 = new State( "state.1",
                                  "state one" );

        State state2 = new State( "state.2",
                                  "state two" );

        Transition origTrans = state1.addTransition( state2,
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

        state1.clearTransitions();

        assertTrue( state1.getTransitions().isEmpty() );
        assertTrue( state2.getTransitions().isEmpty() );
    }

    public void testActivity_SetGet()
    {
        State state1 = new State( "state.1",
                                  "state one" );

        assertSame( NoOpActivity.INSTANCE,
                    state1.getActivity() );

        Activity newActivity = new NoOpActivity();

        state1.setActivity( newActivity );

        assertSame( newActivity,
                    state1.getActivity() );
    }

    public void testDescription_SetGet()
    {
        State state1 = new State( "state.1",
                                  "state one" );

        assertEquals( "state one",
                      state1.getDescription() );

        state1.setDescription( "new description" );

        assertEquals( "new description",
                      state1.getDescription() );
    }

    public void testTerminalState_Constructor()
    {
        State state = new State.TerminalState();

        assertEquals( "state.terminal",
                      state.getName() );

        assertEquals( "terminal state",
                      state.getDescription() );

        assertSame( NoOpActivity.INSTANCE,
                    state.getActivity() );
    }

    public void testTerminalState_SetDescription()
    {
        State state = new State.TerminalState();

        assertEquals( "terminal state",
                      state.getDescription() );

        state.setDescription( "new description" );

        assertEquals( "terminal state",
                      state.getDescription() );
    }

    public void testTerminalState_SetActivity()
    {
        State state = new State.TerminalState();

        assertSame( NoOpActivity.INSTANCE,
                    state.getActivity() );

        state.setActivity( new NoOpActivity() );

        assertSame( NoOpActivity.INSTANCE,
                    state.getActivity() );
    }

    public void testTerminalState_AddTransition()
    {
        State state = new State.TerminalState();

        State destination = new State( "dest.state",
                                       "destination state" );
        
        assertNull( state.addTransition( destination,
                                         "destination transition" ) );

        assertEquals( 0,
                      state.getTransitions().size() );

        assertEquals( 0,
                      destination.getInboundTransitions().size() );
                               
    }

    public void testTerminalState_AddTransitionWithGuard()
    {
        State state = new State.TerminalState();

        State destination = new State( "dest.state",
                                       "destination state" );
        
        assertNull( state.addTransition( destination,
                                         new BooleanGuard( false ),
                                         "destination transition" ) );
        
        assertEquals( 0,
                      state.getTransitions().size() );

        assertEquals( 0,
                      destination.getInboundTransitions().size() );
                               
    }
}
