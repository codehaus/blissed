package com.werken.blissed;

import junit.framework.TestCase;

import com.werken.blissed.guard.BooleanGuard;

import java.util.Set;

public class ProcessTest extends TestCase
{
    private ProcessEngine engine;
    private Process process;
    private State state1;
    private State state2;
    private State state3;
    private State state4;

    private Transition transition1_2;
    private Transition transition2_3;
    private Transition transition3_4;
    private Transition transition1_end;

    public ProcessTest(String name)
    {
        super( name );
    }

    public void setUp() throws Exception
    {
        this.engine = new ProcessEngine();

        this.process = new Process( "test.process",
                                    "test process" );

        this.state1 = this.process.addState( "state.1",
                                             "state one" );

        this.state2 = this.process.addState( "state.2",
                                             "state two" );

        this.state3 = this.process.addState( "state.3",
                                             "state three" );

        this.state4 = this.process.addState( "state.4",
                                             "state four" );

        this.transition1_2 = new Transition( this.state1,
                                             this.state2,
                                             new BooleanGuard( false ),
                                             "transition.1.2" );

        this.state1.addTransition( this.transition1_2 );

        this.transition2_3 = new Transition( this.state2,
                                             this.state3,
                                             new BooleanGuard( false ),
                                             "transition.2.3" );

        this.state2.addTransition( this.transition2_3 );

        this.transition3_4 = new Transition( this.state3,
                                             this.state4,
                                             new BooleanGuard( true ),
                                             "transition.3.4" );

        this.state3.addTransition( this.transition3_4 );

        this.transition1_end = new Transition( this.state1,
                                               null,
                                               new BooleanGuard( false ),
                                               "transition.1.end" );

        this.state1.addTransition( this.transition1_end );

        this.process.setStartState( this.state1 );
    }

    public void tearDown()
    {
        this.engine  = null;
        this.process = null;
        this.state1  = null;
        this.state2  = null;
        this.state3  = null;
        this.state4  = null;
        this.transition1_2 = null;
        this.transition2_3 = null;
        this.transition3_4 = null;
        this.transition1_end = null;
    }

    public void testAddState_Valid() throws Exception
    {
        State state5 = this.process.addState( "state.5",
                                              "state five" );
        
        assertSame( state5,
                    this.process.getState( "state.5" ) );
    }
    

    public void testAddState_Duplicate() throws Exception
    {
        try
        {
            State state1 = this.process.addState( "state.1",
                                                  "state one" );
            
            fail( "Should have thrown DuplicateStateException" );
        }
        catch (DuplicateStateException e)
        {
            // expected and correct
            assertEquals( "test.process",
                          e.getProcess().getName() );

            assertEquals( "state.1",
                          e.getStateName() );
        }
    }

    public void testRemoveState_Valid() throws Exception
    {
        assertTrue( this.process.removeState( state2 ) );
        assertNull( this.process.getState( "state.2" ) );
    }

    public void testRemoveState_Invalid() throws Exception
    {
        Process otherProcess = new Process( "other.process",
                                            "other process" );

        State otherState = otherProcess.addState( "state.1",
                                                  "state one" );

        assertTrue( ! this.process.removeState( otherState ) );

        assertSame( state1,
                    this.process.getState( "state.1" ) );
    }

    public void testName_GetSet()
    {
        assertEquals( "test.process",
                      this.process.getName() );

        this.process.setName( "new.name" );

        assertEquals( "new.name",
                      this.process.getName() );
    }

    public void testDescription_GetSet()
    {
        assertEquals( "test process",
                      this.process.getDescription() );

        this.process.setDescription( "new description" );

        assertEquals( "new description",
                      this.process.getDescription() );
    }

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
    //     org.apache.commons.graph.Graph
    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 

    public void testGetVertices_ForEdge()
    {
        Set vertices = this.process.getVertices( this.transition2_3 );

        assertEquals( 2,
                      vertices.size() );

        assertTrue( vertices.contains( this.state2 ) );
        assertTrue( vertices.contains( this.state3 ) );
    }

    public void testGetVertices_All()
    {
        Set vertices = this.process.getVertices();

        assertEquals( 4,
                      vertices.size() );

        assertTrue( vertices.contains( this.state1 ) );
        assertTrue( vertices.contains( this.state2 ) );
        assertTrue( vertices.contains( this.state3 ) );
        assertTrue( vertices.contains( this.state4 ) );
    }

    public void testGetEdges_ForVertex()
    {
        Set edges = this.process.getEdges( this.state2 );

        assertEquals( 2,
                      edges.size() );

        assertTrue( edges.contains( this.transition1_2 ) );
        assertTrue( edges.contains( this.transition2_3 ) );
    }

    public void testGetEdges_All()
    {
        Set edges = this.process.getEdges();

        assertEquals( 4,
                      edges.size() );

        assertTrue( edges.contains( this.transition1_2 ) );
        assertTrue( edges.contains( this.transition2_3 ) );
        assertTrue( edges.contains( this.transition3_4 ) );
        assertTrue( edges.contains( this.transition1_end ) );
    }

    public void testGetSource()
    {
        Object source = this.process.getSource( this.transition2_3 );

        assertSame( this.state2,
                    source );
    }

    public void testGetTarget()
    {
        Object target = this.process.getTarget( this.transition2_3 );

        assertSame( this.state3,
                    target );
    }
}
