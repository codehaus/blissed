package com.werken.blissed;

import junit.framework.TestCase;

public class ContextTest extends TestCase
{
    private Process process;
    private Context context;
    private State   state1;

    public ContextTest(String name)
    {
        super( name );
    }

    public void setUp() throws Exception
    {
        this.process = new Process( "test.process",
                                    "test process" );

        this.state1 = this.process.addState( "state.1",
                                             "state one" );

        this.process.getStart().setDestination( this.state1 );

        this.state1.addTransition( this.process.getFinish(),
                                   new BooleanGuard( false ),
                                   "1 to finish" );

        this.context = new Context( this.process );
    }

    public void tearDown()
    {
        this.context = null;
        this.process = null;
    }

    public void testVariableManagement()
    {
        Object obj1 = new Object();
        Object obj2 = new Object();

        assertNull( this.context.getVariable( "one" ) );

        assertNull( this.context.getVariable( "two" ) );

        this.context.setVariable( "one",
                                  obj1 );

        this.context.setVariable( "two",
                                  obj2 );

        assertSame( obj1,
                    this.context.getVariable( "one" ) );

        assertSame( obj2,
                    this.context.getVariable( "two" ) );

        this.context.clearVariable( "one" );

        assertNull( this.context.getVariable( "one" ) );

        assertSame( obj2,
                    this.context.getVariable( "two" ) );

        this.context.clearVariable( "two" );

        assertNull( this.context.getVariable( "two" ) );
    }

    public void testSpawn()
    {
        try
        {
            Context spawned = this.context.spawn( this.process );

            this.process.accept( spawned );
            
            assertSame( this.process,
                        spawned.getCurrentProcess() );
            
            assertSame( this.state1,
                        spawned.getCurrentNode() );

            assertSame( this.context,
                        spawned.getParent() );
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
