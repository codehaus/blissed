package com.werken.blissed;

import com.werken.blissed.guard.BooleanGuard;

import junit.framework.TestCase;

import java.util.Set;

public class ProcessEngineTest extends TestCase
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

    public ProcessEngineTest(String name)
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

        this.state1.addTransition( this.transition2_3 );

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

    public void testSpawn_Root() throws Exception
    {
        ProcessContext context = this.engine.spawn( this.process );

        assertNotNull( context );

        assertSame( this.process,
                    context.getCurrentProcess() );

        assertSame( this.state1,
                    context.getCurrentState() );

        assertTrue( ! this.engine.hasContextToCheck() );
    }

    public void testSpawn_Root_Async() throws Exception
    {
        ProcessContext context = this.engine.spawn( this.process,
                                                    true );

        assertSame( this.process,
                    context.getCurrentProcess() );

        assertSame( this.state1,
                    context.getCurrentState() );

        assertTrue( this.engine.hasContextToCheck() );

        assertSame( context,
                    this.engine.getNextToCheck() );
    }

    public void testServiceThread() throws Exception
    {
        ProcessContext context = this.engine.spawn( this.process,
                                                    true );

        assertTrue( this.engine.hasContextToCheck() );

        this.engine.start();

        Thread.sleep( 1000 );

        this.engine.stop();

        assertTrue( ! this.engine.hasContextToCheck() );
    }

    public void testThreads_GetSet()
    {
        assertEquals( 1,
                      this.engine.getThreads() );

        this.engine.setThreads( 5 );

        assertEquals( 5,
                      this.engine.getThreads() );
    }

    public void testStop_AlreadyStopped() throws Exception
    {
        this.engine.stop();
    }

    public void testStart_AlreadyStarted() throws Exception
    {
        this.engine.start();
        this.engine.start();
    }

    public void testSpawn_Nested() throws Exception
    {
        ProcessContext context = this.engine.spawn( this.process );

        ProcessContext nested = this.engine.spawn( this.process,
                                                   context );

        assertSame( this.process,
                    nested.getCurrentProcess() );

        assertSame( this.state1,
                    nested.getCurrentState() );

        assertSame( context,
                    nested.getParent() );

        assertSame( nested,
                    this.engine.getNextToCheck() );

        Set children = context.getChildren();

        assertEquals( 1,
                      children.size() );

        assertSame( nested,
                    children.iterator().next() );
    }

    public void testSpawn_MultipleNested() throws Exception
    {
        ProcessContext context = this.engine.spawn( this.process );

        ProcessContext nested1 = this.engine.spawn( this.process,
                                                    context );

        ProcessContext nested2 = this.engine.spawn( this.process,
                                                    context );

        assertSame( this.process,
                    nested1.getCurrentProcess() );

        assertSame( this.process,
                    nested2.getCurrentProcess() );

        assertSame( this.state1,
                    nested1.getCurrentState() );

        assertSame( this.state1,
                    nested2.getCurrentState() );

        assertSame( context,
                    nested1.getParent() );

        assertSame( context,
                    nested2.getParent() );

        assertSame( nested1,
                    this.engine.getNextToCheck() );

        assertSame( nested2,
                    this.engine.getNextToCheck() );

        Set children = context.getChildren();

        assertEquals( 2,
                      children.size() );

        assertTrue( children.contains( nested1 ) );
        assertTrue( children.contains( nested2 ) );
    }

    public void testCall() throws Exception
    {
        Process anotherProcess = new Process( "another.process",
                                              "another process" );

        State anotherState = anotherProcess.addState( "state.1",
                                                      "state one" );

        anotherProcess.setStartState( anotherState );

        ProcessContext context = this.engine.spawn( this.process );

        this.engine.call( anotherProcess,
                          context );

        assertSame( anotherProcess,
                    context.getCurrentProcess() );

        assertSame( anotherState,
                    context.getCurrentState() );
    }

    public void testFollowTransition_InvalidMotion() throws Exception
    {
        ProcessContext context = this.engine.spawn( this.process );

        try
        {
            this.engine.followTransition( context,
                                          this.transition2_3 );

            fail( "Should have thrown InvalidMotionException" );
        }
        catch (InvalidMotionException e)
        {
            // expected and correct
        }
    }

    public void testFollowTransition_Valid() throws Exception
    {
        ProcessContext context = this.engine.spawn( this.process );

        this.engine.followTransition( context,
                                      this.transition1_2 );

        assertSame( this.process,
                    context.getCurrentProcess() );

        assertSame( this.state2,
                    context.getCurrentState() );
    }

    public void testCheckTransitions_Stagnant() throws Exception
    {
        ProcessContext context = this.engine.spawn( this.process );

        assertSame( this.process,
                    context.getCurrentProcess() );

        assertSame( this.state1,
                    context.getCurrentState() );

        assertTrue( ! this.engine.checkTransitions( context ) );

        assertSame( this.process,
                    context.getCurrentProcess() );

        assertSame( this.state1,
                    context.getCurrentState() );
    }

    public void testCheckTransitions_Mobile() throws Exception
    {
        ProcessContext context = this.engine.spawn( this.process );

        assertSame( this.process,
                    context.getCurrentProcess() );

        assertSame( this.state1,
                    context.getCurrentState() );

        this.engine.followTransition( context,
                                      this.transition1_2 );

        this.engine.followTransition( context,
                                      this.transition2_3 );

        assertSame( this.process,
                    context.getCurrentProcess() );

        assertSame( this.state3,
                    context.getCurrentState() );

        assertTrue( this.engine.checkTransitions( context ) );

        assertSame( this.process,
                    context.getCurrentProcess() );

        assertSame( this.state4,
                    context.getCurrentState() );
    }

    public void testCheckTransitions_NoState() throws Exception
    {
        ProcessContext context = new ProcessContext( this.engine,
                                                     this.process );

        assertTrue( ! this.engine.checkTransitions( context ) );
    }

    public void testCheckTransitions_EndOfProcess() throws Exception
    {
        ProcessContext context = this.engine.spawn( this.process );

        assertSame( this.process,
                    context.getCurrentProcess() );

        assertSame( this.state1,
                    context.getCurrentState() );

        this.engine.followTransition( context,
                                      this.transition1_end );

        assertNull( context.getCurrentProcess() );
        assertNull( context.getCurrentState() );
    }

    public void testEnterState() throws Exception
    {
        ProcessContext context = this.engine.spawn( this.process );

        this.state1.setActivity( new Activity()
            {
                public void perform(ProcessContext context) throws ActivityException
                {
                    throw new ActivityException( "proof.activity.perform" );
                }
            }
                                 );

        this.engine.exitState( this.state1,
                               context );

        try
        {
            this.engine.enterState( this.state1,
                                    context );
        }
        catch (ActivityException e)
        {
            assertEquals( "proof.activity.perform",
                          e.getMessage() );
        }
    }

    public void testEnterState_NullActivity() throws Exception
    {
        ProcessContext context = this.engine.spawn( this.process );

        this.state1.setActivity( null );

        this.engine.exitState( this.state1,
                               context );

        this.engine.enterState( this.state1,
                                context );
    }
}

