package com.werken.blissed;

import junit.framework.TestCase;

public class ProcessTest extends TestCase
{
    private static final String PROCESS_NAME        = "test";
    private static final String PROCESS_DESCRIPTION = "A test process.";

    private Process process;
    private Node node1;
    private Node node2;
    private Node node3;

    public ProcessTest(String name)
    {
        super( name );
    }

    public void setUp()
    {
        this.process = new Process( PROCESS_NAME,
                                    PROCESS_DESCRIPTION );

        this.node1 = new Node( this.process,
                               "node-1",
                               "node one" );

        this.node2 = new Node( this.process,
                               "node-2",
                               "node two" );

        this.node3 = new Node( this.process,
                               "node-3",
                               "node three" );
    }

    public void tearDown()
    {
        this.process = null;
    }

    public void testStartCreation()
    {
        assertNotNull( this.process.getStart() );
    }

    public void testFinishCreation()
    {
        assertNotNull( this.process.getFinish() );
    }

    public void testStart()
    {
        WorkSlip ws = this.process.start();

        assertNotNull( ws );
    }

    public void testGetName()
    {
        assertSame( PROCESS_NAME,
                    this.process.getName() );
    }

    public void testGetDescription()
    {
        assertSame( PROCESS_DESCRIPTION,
                    this.process.getDescription() );
    }

    public void testNodeManagement()
    {
        this.process.addNode( this.node1 );

        assertNotNull( this.process.getNode( "node-1" ) );
        assertNull( this.process.getNode( "node-2" ) );
        assertNull( this.process.getNode( "node-3" ) );

        this.process.addNode( this.node2 );

        assertNotNull( this.process.getNode( "node-1" ) );
        assertNotNull( this.process.getNode( "node-2" ) );
        assertNull( this.process.getNode( "node-3" ) );

        this.process.addNode( this.node3 );

        assertNotNull( this.process.getNode( "node-1" ) );
        assertNotNull( this.process.getNode( "node-2" ) );
        assertNotNull( this.process.getNode( "node-3" ) );

        this.process.removeNode( this.node1 );

        assertNull( this.process.getNode( "node-1" ) );
        assertNotNull( this.process.getNode( "node-2" ) );
        assertNotNull( this.process.getNode( "node-3" ) );

        this.process.removeNode( this.node2 );

        assertNull( this.process.getNode( "node-1" ) );
        assertNull( this.process.getNode( "node-2" ) );
        assertNotNull( this.process.getNode( "node-3" ) );

        this.process.removeNode( this.node3 );

        assertNull( this.process.getNode( "node-1" ) );
        assertNull( this.process.getNode( "node-2" ) );
        assertNull( this.process.getNode( "node-3" ) );
    }
}
