package com.werken.blissed.jelly;

import org.apache.commons.jelly.XMLOutput;

import junit.framework.TestCase;

public abstract class TagTestCase extends TestCase
{
    private XMLOutput output;

    public TagTestCase(String name)
    {
        super( name );    
    }

    public void setUp() throws Exception
    {
        this.output = XMLOutput.createXMLOutput( System.out,
                                                 false );
    }

    public void tearDown()
    {
        this.output = null;
    }

    public XMLOutput getXMLOutput()
    {
        return this.output;
    }
}

