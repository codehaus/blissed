package org.codehaus.blissed.jelly;

import org.apache.commons.jelly.tags.junit.JellyTestSuite;

import junit.framework.TestSuite;

public class BlissedTagLibraryTest extends JellyTestSuite
{
    public static TestSuite suite() throws Exception
    {
         return createTestSuite( BlissedTagLibraryTest.class,
                                 "BlissedTagLibraryTest.jelly");        
    }
}
