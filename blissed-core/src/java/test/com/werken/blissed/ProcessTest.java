package com.werken.blissed;

import junit.framework.TestCase;

public class ProcessTest extends TestCase
{
    private Process process;
    private State state1;
    private Transition transition_1_finish;

    public ProcessTest(String name)
    {
        super( name );
    }

    public void setUp()
    {
        this.process = new Process( "test.process",
                                    "test process" );

        try
        {
            this.state1 = this.process.addState( "state.1",
                                                 "state one" );
            
            this.transition_1_finish = this.state1.addTransition( null,
                                                                  new BooleanGuard( false ),
                                                                  "1 to finish" );
            this.process.setStartState( this.state1 );
        }
        catch (DuplicateStateException e)
        {
            fail( e.getLocalizedMessage() );
        }
    }

    public void tearDown()
    {
        this.process = null;
    }

    public void testAddState()
    {
        try
        {
            State state2 = this.process.addState( "state.2",
                                                  "state two" );
            
            assertSame( state2,
                        this.process.getState( "state.2" ) );
        }
        catch (DuplicateStateException e)
        {
            fail( e.getLocalizedMessage() );
        }
    }
    
    public void testRemoveState()
    {
        try
        {
            State state2 = this.process.addState( "state.2",
                                                  "state two" );
            
            assertSame( state2,
                        this.process.getState( "state.2" ) );
            
            this.process.removeState( state2 );
            
            assertNull( this.process.getState( "state.2" ) );
        }
        catch (DuplicateStateException e)
        {
            fail( e.getLocalizedMessage() );
        }
    }

    public void testSpawn_Root()
    {
        try
        {
            Context context = this.process.spawn();

            this.process.accept( context );
            
            assertSame( this.process,
                        context.getCurrentProcess() );
            
            System.err.println( "current state: " + context.getCurrentState().getName() );
            assertSame( this.state1,
                        context.getCurrentState() );
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

    public void testSpawn_Child()
    {
        try
        {
            Context context = this.process.spawn();

            this.process.accept( context );
            
            assertSame( this.process,
                        context.getCurrentProcess() );
            
            assertSame( this.state1,
                        context.getCurrentState() );

            Context child = this.process.spawn( context );

            this.process.accept( child );

            assertSame( this.process,
                        child.getCurrentProcess() );

            assertSame( this.state1,
                        child.getCurrentState() );

            assertSame( context,
                        child.getParent() );
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