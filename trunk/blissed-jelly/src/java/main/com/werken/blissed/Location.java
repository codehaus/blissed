package com.werken.blissed;

/*
  $Id: Location.java,v 1.4 2002-07-06 03:49:01 werken Exp $

  Copyright 2001 (C) The Werken Company. All Rights Reserved.
 
  Redistribution and use of this software and associated documentation
  ("Software"), with or without modification, are permitted provided
  that the following conditions are met:

  1. Redistributions of source code must retain copyright
  statements and notices.  Redistributions must also contain a
  copy of this document.
 
  2. Redistributions in binary form must reproduce the
  above copyright notice, this list of conditions and the
  following disclaimer in the documentation and/or other
  materials provided with the distribution.
 
  3. The name "blissed" must not be used to endorse or promote
  products derived from this Software without prior written
  permission of The Werken Company.  For written permission,
  please contact bob@werken.com.
 
  4. Products derived from this Software may not be called "blissed"
  nor may "blissed" appear in their names without prior written
  permission of The Werken Company. blissed is a registered
  trademark of The Werken Company.
 
  5. Due credit should be given to the blissed Project
  (http://blissed.werken.com/).
 
  THIS SOFTWARE IS PROVIDED BY THE WERKEN COMPANY AND CONTRIBUTORS
  ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES, INCLUDING, BUT
  NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
  FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL
  THE WERKEN COMPANY OR ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
  INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
  SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
  HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
  STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
  ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
  OF THE POSSIBILITY OF SUCH DAMAGE.
 
*/

import java.util.Stack;

class Location 
{
    private Context context;

    private Stack locationStack;

    Location(Context context)
    {
        this.context = context;
        this.locationStack = new Stack();
    }

    Context getContext()
    {
        return this.context;
    }

    Process getCurrentProcess()
    {
        if ( this.locationStack.isEmpty() )
        {
            return null;
        }

        return ((ProcessEntry)this.locationStack.peek()).getProcess();
    }

    Node getCurrentNode()
    {
        if ( this.locationStack.isEmpty() )
        {
            return null;
        }

        return ((ProcessEntry)this.locationStack.peek()).getCurrentNode();
    }

    void check() throws InvalidMotionException, ActivityException
    {
        if ( this.locationStack.isEmpty() )
        {
            return;
        }

        ProcessEntry entry = (ProcessEntry) this.locationStack.peek();

        Node node = entry.getCurrentNode();

        if ( node == null )
        {
            return;
        }

        node.check( getContext() );
    }

    void startProcess(Process process)
    {
        ProcessEntry entry = new ProcessEntry( process );

        this.locationStack.push( entry );
    }

    void finishProcess(Process process) throws InvalidMotionException
    {
        if ( this.locationStack.isEmpty() )
        {
            throw new InvalidMotionException( "Context not in process \"" + process.getName() + "\"" );
        }

        ProcessEntry entry = (ProcessEntry) this.locationStack.peek();

        if ( ! entry.getProcess().equals( process ) )
        {
            throw new InvalidMotionException( "Context not in process \"" + process.getName() + "\"" );
        }

        if ( entry.getCurrentNode() != null )
        {
            throw new InvalidMotionException( "Process not finished" );
        }

        this.locationStack.pop();
    }

    void enterNode(Node node) throws InvalidMotionException
    {
        if ( this.locationStack.isEmpty() )
        {
            throw new InvalidMotionException( "Context not in any process" );
        }

        ProcessEntry entry = (ProcessEntry) this.locationStack.peek();

        entry.enterNode( node );
    }

    void exitNode(Node node) throws InvalidMotionException
    {
        if ( this.locationStack.isEmpty() )
        {
            throw new InvalidMotionException( "Context not in any process" );
        }

        ProcessEntry entry = (ProcessEntry) this.locationStack.peek();

        entry.exitNode( node );
    }
}

class ProcessEntry
{
    private Process process;
    private Node node;

    ProcessEntry(Process process)
    {
        this.process = process;
        this.node    = null;
    }

    Process getProcess()
    {
        return this.process;
    }

    void enterNode(Node node) throws InvalidMotionException
    {
        if ( this.node != null )
        {
            throw new InvalidMotionException( "Context cannot enter node \"" + node.getName() 
                                              + "\" while still in node \"" + this.node.getName() + "\"" );
        }

        this.node = node;
    }

    void exitNode(Node node) throws InvalidMotionException
    {
        if ( this.node == null
             ||
             ! this.node.equals( node ) )
        {
            throw new InvalidMotionException( "Context not in node \""
                                              + node.getName() + "\" - cannot exit" );
        }

        this.node = null;
    }

    Node getCurrentNode()
    {
        return this.node;
    }
}