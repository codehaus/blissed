package com.werken.blissed;

import junit.framework.TestCase;

public class FinishTest extends TestCase
{
    private Process process;
    private Context context;

    public FinishTest(String name)
    {
        super( name );
    }

    public void setUp()
    {
        this.process = new Process( "test.process",
                                    "test process" );

        this.context = new Context( this.process );
    }

    public void tearDown()
    {
        this.process = null;
        this.context = null;
    }

    public void testAccept()
    {
        try
        {
            this.context.startProcess( this.process );
            
            this.process.getFinish().accept( this.context );
            
            assertNull( this.context.getCurrentNode() );
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
