package com.werken.blissed;

import junit.framework.TestCase;

public class StateTest extends TestCase
{
    private Process process;
    private State   state1;
    private State   state2;
    private Context context;

    public StateTest(String name)
    {
        super( name );
    }

    public void setUp()
    {
        this.process = new Process( "test.process",
                                    "test process" );

        this.state1 = this.process.addState( "state.1",
                                             "state one" );

        this.state2 = this.process.addState( "state.2",
                                             "state two" );

        this.context = new Context( this.process );
    }

    public void tearDown()
    {
        this.process = null;
        this.state1  = null;
    }

    public void testAttemptTransition_NoTransition()
    {
        try
        {
            this.context.startProcess( this.process );
            this.context.enterNode( this.state1 );

            try
            {
                this.state1.attemptTransition( this.context );
                fail( "Should have thrown NoTransitionException" );
            }
            catch (NoTransitionException e)
            {
                // expected and correct
            }
            catch (ActivityException e)
            {
                fail( e.getLocalizedMessage() );
            }
        }
        catch (InvalidMotionException e)
        {
            fail( e.getLocalizedMessage() );
        }
    }

    public void testAttemptTransition_Blocked()
    {
        this.state1.addTransition( this.state2,
                                   new BooleanGuard( false ),
                                   "1 to 2" );
        
        try
        {
            this.context.startProcess( this.process );
            this.context.enterNode( this.state1 );

            this.state1.attemptTransition( this.context );

            assertSame( this.state1,
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
}
