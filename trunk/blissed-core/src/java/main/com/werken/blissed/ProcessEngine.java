package com.werken.blissed;

import java.util.LinkedList;
import java.util.List;
import java.util.Iterator;

public class ProcessEngine 
{
    private LinkedList queue;

    public ProcessEngine()
    {
        // intentionally left blank
    }

    public void start()
    {

    }

    public void stop()
    {

    }

    public void addToCheckQueue(ProcessContext context)
    {
        synchronized ( this.queue )
        {
            this.queue.addLast( context );
            this.queue.notifyAll();
        }
    }

    protected ProcessContext getNextToCheck() throws InterruptedException
    {
        ProcessContext context = null;

        synchronized( this.queue )
        {
            while ( this.queue.isEmpty() )
            {
                this.queue.wait();

                if ( ! this.queue.isEmpty() )
                {
                    context = (ProcessContext) this.queue.getFirst();
                    break;
                }
            }
        }

        return context;
    }

    public ProcessContext spawn(Process process)
    {
        ProcessContext context = new ProcessContext( process );

        return context;
    }

    public ProcessContext spawn(Process process,
                                ProcessContext parent)
    {
        ProcessContext context = new ProcessContext( process,
                                                     parent );
        addToCheckQueue( context );

        return context;
    }

    public boolean check(ProcessContext context) throws InvalidMotionException
    {
        boolean transitioned = false;

      OUTTER:
        while ( true )
        {
            State currentState = context.getCurrentState();

            if ( currentState == null )
            {
                break OUTTER;
            }
            
            List       trans     = currentState.getTransitions();
            Iterator   transIter = trans.iterator();
            Transition eachTrans = null;
            
          INNER:
            while ( transIter.hasNext() )
            {
                eachTrans = (Transition) transIter.next();
                
                if ( eachTrans.testGuard( context ) )
                {
                    followTransition( context,
                                      eachTrans );
                    
                    transitioned = true;
                    continue OUTTER;
                }
            }

            break OUTTER;
        }

        return transitioned;
    }

    protected void followTransition(ProcessContext context,
                                    Transition transition) throws InvalidMotionException
    {
        State destination = transition.getDestination();

        context.exitState( context.getCurrentState() );

        if ( destination == null )
        {
            context.finishProcess( context.getCurrentProcess() );
        }
        else
        {
            context.enterState( destination );
        }
    }
}
