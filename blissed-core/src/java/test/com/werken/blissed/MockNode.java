
package com.werken.blissed;

import java.util.Set;
import java.util.HashSet;

public class MockNode extends Node
{
    private Set accepted;

    MockNode(Process process,
             String name,
             String description)
    {
        super( process,
               name,
               description );

        this.accepted = new HashSet();
    }

    public void accept(WorkSlip workSlip)
    {
        this.accepted.add( workSlip );
    }

    public Set getAccepted()
    {
        return this.accepted;
    }
}

