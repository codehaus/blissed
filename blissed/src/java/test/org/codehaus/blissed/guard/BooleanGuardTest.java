package org.codehaus.blissed.guard;

import junit.framework.TestCase;

public class BooleanGuardTest extends TestCase
{
    public BooleanGuardTest(String name)
    {
        super( name );
    }

    public void setUp()
    {
    }

    public void tearDown()
    {
    }

    public void testTrue()
    {
        BooleanGuard guard = new BooleanGuard( true );

        assertTrue( guard.test( null,
                                null ) );
    }

    public void testFalse()
    {
        BooleanGuard guard = new BooleanGuard( false );

        assertTrue( ! guard.test( null,
                                  null ) );
    }

    public void testChange()
    {
        BooleanGuard guard = new BooleanGuard( false );

        assertTrue( ! guard.test( null,
                                  null ) );

        guard.setGuard( true );

        assertTrue( guard.test( null,
                                null ) );
    }

    public void testAccessors()
    {
        BooleanGuard guard = new BooleanGuard( true );

        assertTrue( guard.getGuard() );

        guard.setGuard( false );

        assertTrue( ! guard.getGuard() );
    }
}
