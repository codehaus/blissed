package com.werken.blissed.activity;

import com.werken.blissed.Process;
import com.werken.blissed.ProcessContext;
import com.werken.blissed.ProcessEngine;
import com.werken.blissed.State;
import com.werken.blissed.guard.BooleanGuard;

import junit.framework.TestCase;

import java.util.Set;

public class SpawnActivityTest extends TestCase
{
    private Process process;
    private State state;

    public SpawnActivityTest(String name)
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
        SpawnActivity activity = new SpawnActivity( this.process );

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

        SpawnActivity activity = new SpawnActivity( anotherProcess );

        activity.perform( context );

        assertSame( this.process,
                    context.getCurrentProcess() );

        assertSame( this.state,
                    context.getCurrentState() );

        engine.start();

        Thread.sleep( 1000 );

        Set children = context.getChildren();

        assertEquals( 1,
                      children.size() );

        ProcessContext child = (ProcessContext) children.iterator().next();

        assertSame( anotherProcess,
                    child.getCurrentProcess() );

        assertSame( anotherState1,
                    child.getCurrentState() );
    }
*/
}
