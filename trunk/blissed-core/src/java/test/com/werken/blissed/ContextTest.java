package com.werken.blissed;

import junit.framework.TestCase;

public class ContextTest extends TestCase
{
    private Process process;
    private Context context;

    public ContextTest(String name)
    {
        super( name );
    }

    public void setUp()
    {
        this.process = new Process( "test.process",
                                    "test process" );

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
}
