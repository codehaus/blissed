package com.werken.blissed;

class CheckTransitionsEntry extends QueueEntry
{
    CheckTransitionsEntry(ProcessContext context)
    {
        super( context );
    }

    void service(ProcessEngine engine) throws Exception
    {
        engine.checkTransitions( getProcessContext() );
    }
}
