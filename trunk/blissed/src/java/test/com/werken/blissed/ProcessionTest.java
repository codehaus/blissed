package com.werken.blissed;

import junit.framework.TestCase;

public class ProcessionTest extends TestCase
{
    private Process process;
    private Procession procession;
    private State   state1;

    public ProcessionTest(String name)
    {
        super( name );
    }

    public void setUp() throws Exception
    {
        this.process = new Process( "test.process",
                                    "test process" );

        this.state1 = this.process.addState( "state.1",
                                             "state one" );

        this.state1.addTransition( null,
                                   new BooleanGuard( false ),
                                   "1 to finish" );


        this.process.setStartState( this.state1 );

        this.procession = new Procession( this.process );
    }

    public void tearDown()
    {
        this.procession = null;
        this.process = null;
    }

    public void testVariableManagement()
    {
        Object obj1 = new Object();
        Object obj2 = new Object();

        assertNull( this.procession.getVariable( "one" ) );

        assertNull( this.procession.getVariable( "two" ) );

        this.procession.setVariable( "one",
                                     obj1 );

        this.procession.setVariable( "two",
                                     obj2 );

        assertSame( obj1,
                    this.procession.getVariable( "one" ) );

        assertSame( obj2,
                    this.procession.getVariable( "two" ) );

        this.procession.clearVariable( "one" );

        assertNull( this.procession.getVariable( "one" ) );

        assertSame( obj2,
                    this.procession.getVariable( "two" ) );

        this.procession.clearVariable( "two" );

        assertNull( this.procession.getVariable( "two" ) );
    }

    public void testSpawn()
    {
        try
        {
            Procession spawned = this.procession.spawn( this.process );

            this.process.accept( spawned );
            
            assertSame( this.process,
                        spawned.getCurrentProcess() );
            
            assertSame( this.state1,
                        spawned.getCurrentState() );

            assertSame( this.procession,
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
