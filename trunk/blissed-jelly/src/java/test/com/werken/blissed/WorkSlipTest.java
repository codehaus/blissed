package com.werken.blissed;

import junit.framework.TestCase;

public class WorkSlipTest extends TestCase
{
    public WorkSlipTest(String name)
    {
        super( name );
    }

    public void setUp()
    {

    }

    public void tearDown()
    {

    }

    public void testStartProcess_DefaultTransition()
    {
        Process process = new Process( "process",
                                       "a test process" );

        WorkSlip ws = process.start();

        assertNotNull( ws.getCurrentNode() );

        // Starting an otherwise empty process immediately
        // transitions across the default start-finish
        // path, leaving us at the finish line.

        assertSame( process.getFinish(),
                    ws.getCurrentNode() );
    }

    public void testStartProcess_Blocked()
    {
        Process process = new Process( "process",
                                       "a test proces" );

        process.getStart().getTransition().setPredicate( FalsePredicate.INSTANCE );

        WorkSlip ws = process.start();

        assertNotNull( ws.getCurrentNode() );

        // Since we've installed a FalsePredicate in the
        // start node's transition, the workslip should
        // still be in the start node, waiting for something
        // to happen.

        assertSame( process.getStart(),
                    ws.getCurrentNode() );
    }
}
