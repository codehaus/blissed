package com.werken.blissed;

import junit.framework.TestCase;

public class ProcessContextTest extends TestCase
{
    private ProcessEngine engine;
    private Process process;
    private State state;

    public ProcessContextTest(String name)
    {
        super( name );
    }

    public void setUp() throws Exception
    {
        this.engine = new ProcessEngine();

        this.process = new Process( "test.process",
                                    "test process" );

        this.state = process.addState( "state.1",
                                       "state one" );

        this.process.setStartState( this.state );
    }

    public void tearDown() throws Exception
    {
        this.engine = null;
        this.process = null;
        this.state = null;
    }

    public void testGetProcessEngine() throws Exception
    {
        ProcessContext context = this.engine.spawn( this.process );

        assertSame( this.engine,
                    context.getProcessEngine() );
    }

    public void testGetProcess() throws Exception
    {
        ProcessContext context = this.engine.spawn( this.process );

        assertSame( this.process,
                    context.getProcess() );
    }

    public void testGetParent_None() throws Exception
    {
        ProcessContext context = this.engine.spawn( this.process );

        assertNull( context.getParent() );
    }
     
    public void testGetParent_Nested() throws Exception
    {
        ProcessContext context = this.engine.spawn( this.process );

        ProcessContext child = this.engine.spawn( this.process,
                                                  context );

        assertSame( context,
                    child.getParent() );
    }
}
