package com.werken.blissed.jelly;

import com.werken.blissed.Process;

public interface ProcessLibrary
{
    void addProcess(Process process);
    Process getProcess(String name);
}
