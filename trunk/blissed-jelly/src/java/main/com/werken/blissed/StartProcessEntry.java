package com.werken.blissed;

class StartProcessEntry extends QueueEntry
{
    private Process process;

    StartProcessEntry(Process process,
                      ProcessContext context)
    {
        super( context );
        this.process = process;
    }

    Process getProcess()
    {
        return this.process;
    }

    void service(ProcessEngine engine) throws Exception
    {
        engine.startProcess( getProcess(),
                             getProcessContext() );
    }

}
