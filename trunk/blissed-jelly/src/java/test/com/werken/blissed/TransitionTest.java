package com.werken.blissed;

import com.werken.blissed.guard.BooleanGuard;

import junit.framework.TestCase;

public class TransitionTest extends TestCase
{
    private State state1;
    private State state2;
    private BooleanGuard guard;

    public TransitionTest(String name)
    {
        super( name );
    }

    public void setUp()
    {
        this.state1 = new State( "state.1",
                                 "state one" );

        this.state2 = new State( "state.2",
                                 "state two" );

        this.guard = new BooleanGuard( true );
    }

    public void tearDown()
    {
        this.state1 = null;
        this.state2 = null;

        this.guard = null;
    }

    public void testConstruct_NoGuard()
    {
        Transition trans = new Transition( this.state1,
                                           this.state2,
                                           "no guard" );

        assertSame( this.state1,
                    trans.getOrigin() );

        assertSame( this.state2,
                    trans.getDestination() );

        assertNull( trans.getGuard() );
    }

    public void testConstruct_WithGuard()
    {
        Transition trans = new Transition( this.state1,
                                           this.state2,
                                           this.guard,
                                           "with guard" );

        assertSame( this.state1,
                    trans.getOrigin() );

        assertSame( this.state2,
                    trans.getDestination() );

        assertSame( this.guard,
                    trans.getGuard() );
    }

    public void testTestGuard_Null()
    {
        Transition trans = new Transition( null,
                                           null,
                                           "no guard" );

        assertTrue( trans.testGuard( null ) );
    }

    public void testTestGuard_NonNull()
    {
        Transition trans = new Transition( null,
                                           null,
                                           this.guard,
                                           "with guard" );

        assertTrue( trans.testGuard( null ) );

        this.guard.setGuard( false );

        assertTrue( ! trans.testGuard( null ) );
    }

    public void testGuard_GetSet()
    {
        Transition trans = new Transition( null,
                                           null,
                                           "trans" );

        assertNull( trans.getGuard() );

        trans.setGuard( this.guard );

        assertSame( this.guard,
                    trans.getGuard() );
    }

    public void testDescription_GetSet()
    {
        Transition trans = new Transition( null,
                                           null,
                                           "orig description" );

        assertEquals( "orig description",
                      trans.getDescription() );

        trans.setDescription( "new description" );

        assertEquals( "new description",
                      trans.getDescription() );
    }
}
