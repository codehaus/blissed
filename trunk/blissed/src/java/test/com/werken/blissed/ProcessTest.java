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

        this.state1 = this.process.addState( "state.1",
                                             "state one" );

        this.transition_1_finish = this.state1.addTransition( this.process.getFinish(),
                                                              new BooleanGuard( false ),
                                                              "1 to finish" );

        this.process.getStart().setDestination( this.state1 );
    }

    public void tearDown()
    {
        this.process = null;
    }

    public void testGetStart()
    {
        assertNotNull( this.process.getStart() );
    }

    public void testGetFinish()
    {
        assertNotNull( this.process.getFinish() );
    }

    public void testAddState()
    {
        State state2 = this.process.addState( "state.2",
                                              "state two" );
        
        assertSame( state2,
                    this.process.getNode( "state.2" ) );
    }
    
    public void testRemoveNode()
    {
        State state2 = this.process.addState( "state.2",
                                              "state two" );
        
        assertSame( state2,
                    this.process.getNode( "state.2" ) );

        this.process.removeNode( state2 );

        assertNull( this.process.getNode( "state.2" ) );
    }

    public void testSpawn_Root()
    {
        try
        {
            Context context = this.process.spawn();
            
            assertSame( this.process,
                        context.getCurrentProcess() );
            
            assertSame( this.state1,
                        context.getCurrentNode() );
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
