package com.werken.blissed;

abstract class QueueEntry
{
    private ProcessContext context;

    QueueEntry(ProcessContext context)
    {
        this.context = context;
    }

    ProcessContext getProcessContext()
    {
        return this.context;
    }

    abstract void service(ProcessEngine engine) throws Exception;
}
