package com.werken.blissed;

import junit.framework.TestCase;

import com.werken.blissed.guard.BooleanGuard;

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

    public void testAddState() throws Exception
    {
        State state2 = this.process.addState( "state.2",
                                              "state two" );
        
        assertSame( state2,
                    this.process.getState( "state.2" ) );
    }
    
    public void testRemoveState() throws Exception
    {
        State state2 = this.process.addState( "state.2",
                                              "state two" );
        
        assertSame( state2,
                    this.process.getState( "state.2" ) );
        
        this.process.removeState( state2 );
        
        assertNull( this.process.getState( "state.2" ) );
    }
}
