package com.werken.blissed;

import junit.framework.TestCase;

public class FalsePredicateTest extends TestCase
{
    public FalsePredicateTest(String name)
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
        assertTrue( ! FalsePredicate.INSTANCE.test( null ) );
    }
}
