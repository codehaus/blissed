package com.werken.blissed;

import com.werken.blissed.guard.BooleanGuard;

import junit.framework.TestCase;

public class ProcessEngineTest extends TestCase
{
    private ProcessEngine engine;
    private Process process;
    private State state1;
    private State state2;

    public ProcessEngineTest(String name)
    {
        super( name );
    }

    public void setUp() throws Exception
    {
        this.engine = new ProcessEngine();

        this.process = new Process( "test.process",
                                    "test process" );

        this.state1 = this.process.addState( "state.1",
                                             "state one" );

        this.state2 = this.process.addState( "state.2",
                                             "state two" );

        this.state1.addTransition( state2,
                                   new BooleanGuard( false ),
                                   "transition.1" );

        this.process.setStartState( this.state1 );
    }

    public void tearDown()
    {
        this.engine  = null;
        this.process = null;
        this.state1  = null;
        this.state2  = null;
    }

    public void testSpawn_Root() throws Exception
    {
        ProcessContext context = this.engine.spawn( this.process );

        assertNotNull( context );

        assertSame( this.process,
                    context.getCurrentProcess() );

        assertSame( this.state1,
                    context.getCurrentState() );
    }
}
