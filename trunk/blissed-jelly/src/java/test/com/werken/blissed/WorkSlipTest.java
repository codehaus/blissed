package com.werken.blissed;

import junit.framework.TestCase;

public class WorkSlipTest extends TestCase
{
    private Process process;

    public WorkSlipTest(String name)
    {
        super( name );
    }

    public void setUp()
    {
        this.process = new Process( "process",
                                    "a test process" );
    }

    public void tearDown()
    {
        this.process = null;
    }

    public void testStartProcess_DefaultTransition()
    {
        WorkSlip ws = this.process.start();

        assertNotNull( ws.getCurrentNode() );

        // Starting an otherwise empty process immediately
        // transitions across the default start-finish
        // path, leaving us at the finish line.

        assertSame( process.getFinish(),
                    ws.getCurrentNode() );
    }

    public void testStartProcess_Blocked()
    {
        this.process.getStart().getTransition().setPredicate( FalsePredicate.INSTANCE );

        WorkSlip ws = this.process.start();

        assertNotNull( ws.getCurrentNode() );

        // Since we've installed a FalsePredicate in the
        // start node's transition, the workslip should
        // still be in the start node, waiting for something
        // to happen.

        assertSame( this.process.getStart(),
                    ws.getCurrentNode() );
    }

    public void testStartSubProcess()
    {
        WorkSlip parent = this.process.start();

        assertNull( parent.getParent() );

        assertTrue( parent.getChildren().isEmpty() );

        Process subProcess = new Process( "sub-process",
                                          "a test sub-process" );

        WorkSlip child = parent.start( subProcess );

        assertSame( parent,
                    child.getParent() );

        assertEquals( 1,
                      parent.getChildren().size() );

        assertTrue( parent.getChildren().contains( child ) );
    }

    public void testAttributes()
    {
        WorkSlip ws = this.process.start();

        Object objA = new Object();
        Object objB = new Object();

        ws.setAttribute( "a",
                         objA );

        ws.setAttribute( "b",
                         objB );

        assertSame( objA,
                    ws.getAttribute( "a" ) );

        assertSame( objB,
                    ws.getAttribute( "b" ) );

        ws.clearAttribute( "a" );

        assertNull( ws.getAttribute( "a" ) );

        assertSame( objB,
                    ws.getAttribute( "b" ) );

        ws.clearAttribute( "b" );

        assertNull( ws.getAttribute( "b" ) );
    }
}
