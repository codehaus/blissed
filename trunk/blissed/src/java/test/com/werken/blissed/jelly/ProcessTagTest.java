package com.werken.blissed.jelly;

import org.apache.commons.jelly.MissingAttributeException;

import junit.framework.TestCase;

public class ProcessTagTest extends TagTestCase
{
    public ProcessTagTest(String name)
    {
        super( name );
    }

    public void testDoTag_NoName()
    {
        ProcessTag tag = new ProcessTag();

        try
        {
            tag.doTag( getXMLOutput() );
        }
        catch (MissingAttributeException e)
        {
            if ( "name".equals( e.getMissingAttribute() ) )
            {
                // expected and correct
            }
            else
            {
                fail( e.getLocalizedMessage() );
            }
        }
        catch (Exception e)
        {
            fail( e.getLocalizedMessage() );
        }
    }

    public void testDoTag_WithName()
    {
        ProcessTag tag = new ProcessTag();

        tag.setName( "test.process" );

        try
        {
            tag.doTag( getXMLOutput() );

            assertNotNull( tag.getProcess() );

            assertEquals( "test.process",
                          tag.getProcess().getName() );

            assertEquals( "",
                          tag.getProcess().getDescription() );
        }
        catch (Exception e)
        {
            fail( e.getLocalizedMessage() );
        }
    }

    public void testDoTag_WithName_WithDescription()
    {
        ProcessTag tag = new ProcessTag();

        tag.setName( "test.process" );
        tag.setDescription( "A test process" );

        try
        {
            tag.doTag( getXMLOutput() );

            assertNotNull( tag.getProcess() );

            assertEquals( "test.process",
                          tag.getProcess().getName() );

            assertEquals( "A test process",
                          tag.getProcess().getDescription() );
        }
        catch (Exception e)
        {
            fail( e.getLocalizedMessage() );
        }
    }
}
