package com.werken.blissed;

import junit.framework.TestCase;

public class LocationTest extends TestCase
{
    private Process  process;
    private Context  context;
    private Location location;

    private State state1;
    private State state2;

    public LocationTest(String name)
    {
        super( name );
    }

    public void setUp() throws Exception
    {
        this.process  = new Process( "test.process",
                                     "test process" );
                                    
        this.context  = new Context( this.process );

        this.location = this.context.getLocation();
        
        this.state1 = this.process.addState( "state.1",
                                             "state one" );

        this.state2 = this.process.addState( "state.2",
                                             "state two" );
    }

    public void tearDown()
    {
        this.location = null;
        this.context  = null;
    }

    public void testStartProcess()
    {
        this.location.startProcess( this.process );

        assertSame( process,
                    this.location.getCurrentProcess() );
    }

    public void testFinishProcess_Valid()
    {
        this.location.startProcess( this.process );

        assertSame( process,
                    this.location.getCurrentProcess() );

        try
        {
            this.location.finishProcess( this.process );
        }
        catch (InvalidMotionException e)
        {
            fail( e.getLocalizedMessage() );
        }
    }

    public void testFinishProcess_Invalid_WrongProcess()
    {
        this.location.startProcess( this.process );
        
        assertSame( process,
                    this.location.getCurrentProcess() );

        Process anotherProcess = new Process( "another.process",
                                              "anothe process" );

        try
        {
            this.location.finishProcess( anotherProcess );
            fail( "Should have thrown InvalidMotionException" );
        }
        catch (InvalidMotionException e)
        {
            // expected and correct
        }
    }

    public void testFinishProcess_Invalid_NoProcess()
    {
        assertNull( this.location.getCurrentProcess() );

        try
        {
            this.location.finishProcess( this.process );
            fail( "Should have thrown InvalidMotionException" );
        }
        catch (InvalidMotionException e)
        {
            // expected and correct
        }
    }

    public void testFinishProces_Invalid_ProcessNotFinished()
    {
        this.location.startProcess( this.process );
        
        assertSame( process,
                    this.location.getCurrentProcess() );


        try
        {
            try
            {
                this.location.enterState( this.state1 );
            }
            catch (InvalidMotionException e)
            {
                fail( e.getLocalizedMessage() );
            }

            this.location.finishProcess( this.process );
            fail( "Should have thrown InvalidMotionException" );
        }
        catch (InvalidMotionException e)
        {
            // expected and correct
        }
    }

    public void testEnterState_Valid()
    {
        this.location.startProcess( this.process );

        assertSame( this.process,
                    this.location.getCurrentProcess() );

        assertNull( this.location.getCurrentState() );

        try
        {
            this.location.enterState( this.state1 );

            assertSame( this.state1,
                        this.location.getCurrentState() );
        }
        catch (InvalidMotionException e)
        {
            fail( e.getLocalizedMessage() );
        }
    }

    public void testEnterState_Invalid_NotExitedOtherState()
    {
        this.location.startProcess( this.process );

        assertSame( this.process,
                    this.location.getCurrentProcess() );

        assertNull( this.location.getCurrentState() );

        try
        {
            this.location.enterState( this.state1 );

            assertSame( this.state1,
                        this.location.getCurrentState() );

            try
            {
                this.location.enterState( this.state2 );
                fail( "Should have thrown InvalidMotionException" );
            }
            catch (InvalidMotionException e)
            {
                // expected and correct
            }
        }
        catch (InvalidMotionException e)
        {
            fail( e.getLocalizedMessage() );
        }
    }     

    public void testEnterState_Invalid_NoProcess()
    {
        try
        {
            this.location.enterState( this.state1 );
            fail( "Should have thrown InvalidMotionException" );
        }
        catch (InvalidMotionException e)
        {
            // expected and correct
        }
    }

    public void testExitState_Valid()
    {
        this.location.startProcess( this.process );

        assertSame( this.process,
                    this.location.getCurrentProcess() );

        assertNull( this.location.getCurrentState() );

        try
        {
            this.location.enterState( this.state1 );

            assertSame( this.state1,
                        this.location.getCurrentState() );

            this.location.exitState( this.state1 );

            assertNull( this.location.getCurrentState() );
        }
        catch (InvalidMotionException e)
        {
            fail( e.getLocalizedMessage() );
        }
    }

    public void testExitState_Invalid_NoState()
    {
        this.location.startProcess( this.process );

        assertSame( this.process,
                    this.location.getCurrentProcess() );

        assertNull( this.location.getCurrentState() );

        try
        {
            this.location.exitState( this.state1 );
            fail( "Should have thrown InvalidMotionException" );
        }
        catch (InvalidMotionException e)
        {
            // expected and correct
        }
    }

    public void testExitState_Invalid_WrongState()
    {
        this.location.startProcess( this.process );

        assertSame( this.process,
                    this.location.getCurrentProcess() );

        assertNull( this.location.getCurrentState() );

        try
        {
            this.location.enterState( this.state1 );

            try
            {
                this.location.exitState( this.state2 );
                fail( "Should have thrown InvalidMotionException" );
            }
            catch (InvalidMotionException e)
            {
                // expected and correct
            }
        }
        catch (InvalidMotionException e)
        {
            fail( e.getLocalizedMessage() );
        }
    }

    public void testExitState_Invalid_NoProcess()
    {
        try
        {
            this.location.exitState( this.state1 );
            fail( "Should have thrown InvalidMotionException" );
        }
        catch (InvalidMotionException e)
        {
            // expected and correct
        }
    } 
}
