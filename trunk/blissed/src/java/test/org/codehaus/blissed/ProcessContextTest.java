package org.codehaus.blissed;

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
        ProcessContext context = this.engine.spawn( this.process, null, false );

        assertSame( this.engine,
                    context.getProcessEngine() );
    }

    public void testGetProcess() throws Exception
    {
        ProcessContext context = this.engine.spawn( this.process, null, false );

        assertSame( this.process,
                    context.getCurrentProcess() );
    }

    public void testGetParent_None() throws Exception
    {
        ProcessContext context = this.engine.spawn( this.process, null, false );

        assertNull( context.getParent() );
    }
     
    public void testGetParent_Nested() throws Exception
    {
        ProcessContext context = this.engine.spawn( this.process, null, false );

        ProcessContext child = this.engine.spawn( this.process,
                                                  context,
                                                  null );

        assertSame( context,
                    child.getParent() );
    }

    public void testProcessData_GetSet() throws Exception
    {
        ProcessContext context = this.engine.spawn( this.process, null, false );

        assertNull( context.getProcessData() );

        Object data = new Object();

        context.setProcessData( data );

        assertSame( data,
                    context.getProcessData() );
    }

    public void testProcessData_Spawn() throws Exception
    {
        Object data = new Object();
        ProcessContext context = this.engine.spawn( this.process, data, false );

        assertSame( data,
                    context.getProcessData() );
    }
}
