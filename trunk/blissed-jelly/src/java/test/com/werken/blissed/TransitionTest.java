package com.werken.blissed;

import junit.framework.TestCase;

public class TransitionTest extends TestCase
{
    private Process process;
    private Context context;

    private State state1;
    private State state2;

    private Transition transition_1_2;
    private Transition transition_2_finish;

    public TransitionTest(String name)
    {
        super( name );
    }

    public void setUp()
    {
        this.process = new Process( "process.test",
                                    "test process" );

        this.state1 = this.process.addState( "state.1",
                                             "state one" );

        this.state2 = this.process.addState( "state.2",
                                             "state two" );

        this.transition_1_2 = this.state1.addTransition( this.state2,
                                                         "transition from 1 to 2" );

        this.transition_2_finish = this.state2.addTransition( this.process.getFinish(),
                                                              "transition from 2 to finish" );

        this.transition_2_finish.setGuard( new BooleanGuard( false ) );

        this.context = new Context( this.process );

        this.context.startProcess( this.process );
    }

    public void tearDown()
    {
        this.state1 = null;
        this.state2 = null;
    }

    public void testNullGuard()
    {
        assertNull( this.transition_1_2.getGuard() );

        assertTrue( this.transition_1_2.testGuard( null ) );
    }

    public void testAccept_Valid()
    {
        try
        {
            this.context.enterNode( this.state1 );
            this.transition_1_2.accept( this.context );

            assertSame( this.state2,
                        this.context.getCurrentNode() );
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

    public void testAccept_Invalid_WrongOrigin()
    {
        try
        {
            try
            {
                this.context.enterNode( this.state2 );
            }
            catch (InvalidMotionException e)
            {
                fail( e.getLocalizedMessage() );
            }
            
            this.transition_1_2.accept( this.context );

            fail( "Should have thrown InvalidMotionException" );
        }
        catch (InvalidMotionException e)
        {
            // expected and correct
        }
        catch (ActivityException e)
        {
            fail( e.getLocalizedMessage() );
        }
    }
}
