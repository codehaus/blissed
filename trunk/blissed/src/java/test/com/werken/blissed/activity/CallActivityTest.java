package com.werken.blissed.activity;

import com.werken.blissed.Process;
import com.werken.blissed.ProcessContext;
import com.werken.blissed.ProcessEngine;
import com.werken.blissed.State;
import com.werken.blissed.guard.BooleanGuard;

import junit.framework.TestCase;

public class CallActivityTest extends TestCase
{
    private Process process;
    private State state;

    public CallActivityTest(String name)
    {
        super( name );
    }

    public void setUp() throws Exception
    {
        this.process = new Process( "test.process",
                                    "test process" );

        this.state = this.process.addState( "state.1",
                                            "state one" );

        this.process.setStartState( this.state );
    }

    public void tearDown()
    {
        this.process = null;
        this.state   = null;
    }
    
    public void testConstruct()
    {
        CallActivity activity = new CallActivity( this.process );

        assertSame( this.process,
                    activity.getProcess() );
    }

/*
    public void testPerform() throws Exception
    {
        Process anotherProcess = new Process( "another.process",
                                              "another process" );

        State anotherState1 = anotherProcess.addState( "another.state.1",
                                                      "another state 1" );

        State anotherState2 = anotherProcess.addState( "another.state.2",
                                                       "another state 2" );

        anotherState1.addTransition( anotherState2,
                                     new BooleanGuard( false ),
                                     "trans" );


        anotherProcess.setStartState( anotherState1 );

        ProcessEngine engine = new ProcessEngine();

        ProcessContext context = engine.spawn( this.process );

        assertSame( this.process,
                    context.getCurrentProcess() );

        assertSame( this.state,
                    context.getCurrentState() );

        CallActivity activity = new CallActivity( anotherProcess );

        activity.perform( context );

        assertSame( anotherProcess,
                    context.getCurrentProcess() );

        assertSame( anotherState1,
                    context.getCurrentState() );
    }
*/
}
