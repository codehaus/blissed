package com.werken.blissed;

import junit.framework.TestCase;

public class StartTest extends TestCase
{
    private Process process;
    private Start   start;
    private Context context;

    public StartTest(String name)
    {
        super( name );
    }

    public void setUp()
    {
        this.process = new Process( "test.process",
                                    "test process" );

        this.start   = this.process.getStart();

        this.context = new Context( this.process );
                                    
    }

    public void tearDown()
    {
        this.start   = null;
        this.process = null;
    }

    public void testInitialDestination()
    {
        assertSame( this.process.getFinish(),
                    this.start.getDestination() );
    }

    public void testAccept()
    {
        Start start = this.process.getStart();

        try
        {
            context.startProcess( this.process );
            
            start.accept( this.context );

            assertNull( this.context.getCurrentNode() );

            System.err.println( "--> " + context.getCurrentProcess() );

            assertNull( this.context.getCurrentProcess() );
        }
        catch (InvalidMotionException e)
        {
            fail( e.getLocalizedMessage() );
        }
        catch (ActivityException e)
        {
            fail( e.getLocalizedMessage() );
        }
    }
}
