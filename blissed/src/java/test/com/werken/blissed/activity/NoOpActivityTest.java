package com.werken.blissed.activity;

import junit.framework.TestCase;

public class NoOpActivityTest extends TestCase
{
    public NoOpActivityTest(String name)
    {
        super( name );
    }

    public void setUp() throws Exception
    {
    }

    public void tearDown() throws Exception
    {
    }

    public void testDescription_GetSet() throws Exception
    {
        assertEquals( "no-op",
                      NoOpActivity.INSTANCE.getDescription() );

        NoOpActivity.INSTANCE.setDescription( "foo" );

        assertEquals( "no-op",
                      NoOpActivity.INSTANCE.getDescription() );
    }

    public void testPerform() throws Exception
    {
        NoOpActivity.INSTANCE.perform( null );
    }
}
