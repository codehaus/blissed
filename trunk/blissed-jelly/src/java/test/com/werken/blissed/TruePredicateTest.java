package com.werken.blissed;

import junit.framework.TestCase;

public class TruePredicateTest extends TestCase
{
    public TruePredicateTest(String name)
    {
        super( name );
    }

    public void setUp()
    {
    }

    public void tearDown()
    {
    }

    public void testTest()
    {
        assertTrue( TruePredicate.INSTANCE.test( null ) );
    }
}
