package com.werken.blissed;

import junit.framework.TestCase;

public class ProcessTest extends TestCase
{
    private Process process;

    public ProcessTest(String name)
    {
        super( name );
    }

    public void setUp()
    {
        this.process = new Process( "test.process",
                                    "test process" );
    }

    public void tearDown()
    {
        this.process = null;
    }

    public void testGetStart()
    {
        assertNotNull( this.process.getStart() );
    }

    public void testGetFinish()
    {
        assertNotNull( this.process.getFinish() );
    }

    public void testAddState()
    {
        State state = this.process.addState( "state.1",
                                             "state one" );

        assertSame( state,
                    this.process.getNode( "state.1" ) );
    }

    public void testRemoveNode()
    {
        State state = this.process.addState( "state.1",
                                             "state one" );

        assertSame( state,
                    this.process.getNode( "state.1" ) );

        this.process.removeNode( state );

        assertNull( this.process.getNode( "state.1" ) );
    }
}
