package com.werken.blissed;

import junit.framework.TestCase;

public class StartTest extends TestCase
{
    private Process process;

    public StartTest(String name)
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

    public void testInitialDestination()
    {
        Start start = this.process.getStart();

        assertSame( this.process.getFinish(),
                    start.getDestination() );
    }
}
