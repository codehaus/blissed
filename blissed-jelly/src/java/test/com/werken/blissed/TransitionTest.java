package com.werken.blissed;

import junit.framework.TestCase;

public class TransitionTest extends TestCase
{
    private Process process;

    private State state1;
    private State state2;

    public TransitionTest(String name)
    {
        super( name );
    }

    public void setUp()
    {
        this.process = new Process( "process.test",
                                    "test process" );

        this.state1 = this.process.addState( "state.1",
                                             "state one" );

        this.state2 = this.process.addState( "state.2",
                                             "state two" );
    }

    public void tearDown()
    {
        this.state1 = null;
        this.state2 = null;
    }

    public void testNullGuard()
    {
        Transition transition = new Transition( this.state1,
                                                this.state2,
                                                "bare transition" );

        assertNull( transition.getGuard() );

        assertTrue( transition.testGuard( null ) );
    }
}
