package com.werken.blissed;

import junit.framework.TestCase;

public class StateTest extends TestCase
{
    private Process process;

    private State state1;
    private State state2;
    private State state3;
    private State state4;

    public StateTest(String name)
    {
        super( name );
    }

    public void setUp()
    {
        this.process = new Process( "process",
                                    "a test process" );
        state1 = new State( this.process,
                            "state-1",
                            "state one" );

        state2 = new State( this.process,
                            "state-2",
                            "state two" );

        state3 = new State( this.process,
                            "state-3",
                            "state three" );

        state4 = new State( this.process,
                            "state-4",
                            "state four" );
    }

    public void tearDown()
    {
        this.process = null;

        this.state1 = null;
        this.state2 = null;
        this.state3 = null;
        this.state4 = null;
    }

    public void testDefaultTask()
    {
        assertSame( NoOpTask.INSTANCE,
                    this.state1.getTask() );
    }

    public void testAttemptTransition_OneTransition()
    {
        this.state1.addTransition( this.state2,
                                   TruePredicate.INSTANCE,
                                   "first transition" );

        WorkSlip ws = new WorkSlip( this.process );

        ws.setCurrentNode( this.state1 );

        ws.check();

        assertSame( this.state2,
                    ws.getCurrentNode() );
    }

    public void testAttemptTransition_TwoTransitions_MatchFirst()
    {
        // The first transition should pass immediately.

        this.state1.addTransition( this.state2,
                                   TruePredicate.INSTANCE,
                                   "first transition" );

        this.state1.addTransition( this.state2,
                                   FalsePredicate.INSTANCE,
                                   "second transition" );

        WorkSlip ws = new WorkSlip( this.process );

        ws.setCurrentNode( this.state1 );

        ws.check();

        assertSame( this.state2,
                    ws.getCurrentNode() );
    }

    public void testAttemptTransition_TwoTransitions_MatchSecond()
    {
        // The first transition will fail, and the flow
        // should follow the second transition.

        this.state1.addTransition( this.state2,
                                   FalsePredicate.INSTANCE,
                                   "first transition" );

        this.state1.addTransition( this.state3,
                                   TruePredicate.INSTANCE,
                                   "second transition" );

        WorkSlip ws = new WorkSlip( this.process );

        ws.setCurrentNode( this.state1 );

        ws.check();

        assertSame( this.state3,
                    ws.getCurrentNode() );
    }

    public void testAttemptTransition_TwoTransitions_MatchBoth()
    {
        // Though both transitions match, the first will
        // be followed.

        this.state1.addTransition( this.state2,
                                   TruePredicate.INSTANCE,
                                   "first transition" );

        this.state1.addTransition( this.state3,
                                   TruePredicate.INSTANCE,
                                   "second transition" );

        WorkSlip ws = new WorkSlip( this.process );

        ws.setCurrentNode( this.state1 );

        ws.check();

        assertSame( this.state2,
                    ws.getCurrentNode() );
    }

    public void testAttemptTransition_TwoTransitions_MatchNone()
    {
        // Neither transition matches, so no movement.

        this.state1.addTransition( this.state2,
                                   FalsePredicate.INSTANCE,
                                   "first transition" );

        this.state1.addTransition( this.state3,
                                   FalsePredicate.INSTANCE,
                                   "second transition" );

        WorkSlip ws = new WorkSlip( this.process );

        ws.setCurrentNode( this.state1 );

        ws.check();

        assertSame( this.state1,
                    ws.getCurrentNode() );
    }
}
