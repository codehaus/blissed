package org.codehaus.blissed;

import junit.framework.TestCase;

public class BlissedExceptionTest extends TestCase
{
    public BlissedExceptionTest( String name )
    {
        super( name );
    }

    public void setUp()
    {
    }

    public void tearDown()
    {
    }

    public void testConstruct_NoArg()
    {
        BlissedException e = new BlissedException();

        assertNull( e.getMessage() );
        assertNull( e.getLocalizedMessage() );

        assertNull( e.getCause() );
    }

    public void testConstruct_WithMessage()
    {
        BlissedException e = new BlissedException( "The message" );

        assertEquals( "The message",
                      e.getMessage() );

        assertEquals( "The message",
                      e.getLocalizedMessage() );

        assertNull( e.getCause() );
    }

    public void testConstruct_WithRootCause()
    {
        Exception rootCause = new Exception( "Root cause" );

        BlissedException e = new BlissedException( rootCause );

        assertNotNull( e.getCause() );

        assertSame( rootCause,
                    e.getCause() );
    }
}
