package com.werken.blissed;

import com.werken.blissed.jelly.Jelly;
import com.werken.blissed.jelly.BlissedTagLibrary;

import org.apache.commons.jelly.JellyContext;
import org.apache.commons.jelly.XMLOutput;

import java.io.File;

public class Main
{
    public static void main(String[] args)
    {
        if ( args.length != 1 )
        {
            System.err.println( "usage: blissed <script-xml>" );
            return;
        }

        File script = new File( args[0] );

        JellyContext context = new JellyContext();

        context.registerTagLibrary( "",
                                    new BlissedTagLibrary() );

        try
        {
            Jelly.runScript( script,
                             script.getParentFile().toURL(),
                             context,
                             XMLOutput.createXMLOutput( System.out,
                                                        false ) );
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
