package com.werken.blissed;

import junit.framework.TestCase;

public class ProcessContextTest extends TestCase
{
    private ProcessContext  processContext;

    private Process  process;
    private State state1;
    private State state2;

    public ProcessContextTest(String name)
    {
        super( name );
    }

    public void setUp() throws Exception
    {
        this.process  = new Process( "test.process",
                                     "test process" );
                                    
        this.processContext  = new ProcessContext( null,
                                                   this.process );

        this.state1 = this.process.addState( "state.1",
                                             "state one" );

        this.state2 = this.process.addState( "state.2",
                                             "state two" );
    }

    public void tearDown()
    {
        this.processContext  = null;

        this.process = null;
        this.state1  = null;
        this.state2  = null;
    }

    public void testStartProcess()
    {
        this.processContext.startProcess( this.process );
        
        assertSame( process,
                    this.processContext.getCurrentProcess() );
    }

    public void testFinishProcess_Valid()
    {
        this.processContext.startProcess( this.process );

        assertSame( process,
                    this.processContext.getCurrentProcess() );

        try
        {
            this.processContext.finishProcess( this.process );
        }
        catch (InvalidMotionException e)
        {
            fail( e.getLocalizedMessage() );
        }
    }

    public void testFinishProcess_Invalid_WrongProcess()
    {
        this.processContext.startProcess( this.process );
        
        assertSame( process,
                    this.processContext.getCurrentProcess() );

        Process anotherProcess = new Process( "another.process",
                                              "anothe process" );

        try
        {
            this.processContext.finishProcess( anotherProcess );
            fail( "Should have thrown InvalidMotionException" );
        }
        catch (InvalidMotionException e)
        {
            // expected and correct
        }
    }

    public void testFinishProcess_Invalid_NoProcess()
    {
        assertNull( this.processContext.getCurrentProcess() );

        try
        {
            this.processContext.finishProcess( this.process );
            fail( "Should have thrown InvalidMotionException" );
        }
        catch (InvalidMotionException e)
        {
            // expected and correct
        }
    }

    public void testFinishProces_Invalid_ProcessNotFinished()
    {
        this.processContext.startProcess( this.process );
        
        assertSame( process,
                    this.processContext.getCurrentProcess() );


        try
        {
            try
            {
                this.processContext.enterState( this.state1 );
            }
            catch (InvalidMotionException e)
            {
                fail( e.getLocalizedMessage() );
            }

            this.processContext.finishProcess( this.process );
            fail( "Should have thrown InvalidMotionException" );
        }
        catch (InvalidMotionException e)
        {
            // expected and correct
        }
    }

    public void testEnterState_Valid()
    {
        this.processContext.startProcess( this.process );

        assertSame( this.process,
                    this.processContext.getCurrentProcess() );

        assertNull( this.processContext.getCurrentState() );

        try
        {
            this.processContext.enterState( this.state1 );

            assertSame( this.state1,
                        this.processContext.getCurrentState() );
        }
        catch (InvalidMotionException e)
        {
            fail( e.getLocalizedMessage() );
        }
    }

    public void testEnterState_Invalid_NotExitedOtherState()
    {
        this.processContext.startProcess( this.process );

        assertSame( this.process,
                    this.processContext.getCurrentProcess() );

        assertNull( this.processContext.getCurrentState() );

        try
        {
            this.processContext.enterState( this.state1 );

            assertSame( this.state1,
                        this.processContext.getCurrentState() );

            try
            {
                this.processContext.enterState( this.state2 );
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
            this.processContext.enterState( this.state1 );
            fail( "Should have thrown InvalidMotionException" );
        }
        catch (InvalidMotionException e)
        {
            // expected and correct
        }
    }

    public void testExitState_Valid()
    {
        this.processContext.startProcess( this.process );

        assertSame( this.process,
                    this.processContext.getCurrentProcess() );

        assertNull( this.processContext.getCurrentState() );

        try
        {
            this.processContext.enterState( this.state1 );

            assertSame( this.state1,
                        this.processContext.getCurrentState() );

            this.processContext.exitState( this.state1 );

            assertNull( this.processContext.getCurrentState() );
        }
        catch (InvalidMotionException e)
        {
            fail( e.getLocalizedMessage() );
        }
    }

    public void testExitState_Invalid_NoState()
    {
        this.processContext.startProcess( this.process );

        assertSame( this.process,
                    this.processContext.getCurrentProcess() );

        assertNull( this.processContext.getCurrentState() );

        try
        {
            this.processContext.exitState( this.state1 );
            fail( "Should have thrown InvalidMotionException" );
        }
        catch (InvalidMotionException e)
        {
            // expected and correct
        }
    }

    public void testExitState_Invalid_WrongState()
    {
        this.processContext.startProcess( this.process );

        assertSame( this.process,
                    this.processContext.getCurrentProcess() );

        assertNull( this.processContext.getCurrentState() );

        try
        {
            this.processContext.enterState( this.state1 );

            try
            {
                this.processContext.exitState( this.state2 );
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
            this.processContext.exitState( this.state1 );
            fail( "Should have thrown InvalidMotionException" );
        }
        catch (InvalidMotionException e)
        {
            // expected and correct
        }
    } 
}
