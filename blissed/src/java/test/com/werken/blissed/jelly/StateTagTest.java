package com.werken.blissed.jelly;

import org.apache.commons.jelly.JellyException;

public class StateTagTest extends TagTestCase
{
    public StateTagTest(String name)
    {
        super( name );
    }

    public void testDoTag_NoProcess()
    {
        StateTag tag = new StateTag();

        try
        {
            tag.doTag( getXMLOutput() );
        }
        catch (JellyException e)
        {
            // expected and correct
        }
        catch (Exception e)
        {
            fail( e.getLocalizedMessage() );
        }
    }
}
