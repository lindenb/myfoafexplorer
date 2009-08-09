/*
Copyright (c) 2005 Pierre Lindenbaum PhD

Permission is hereby granted, free of charge, to any person obtaining
a copy of this software and associated documentation files (the
``Software''), to deal in the Software without restriction, including
without limitation the rights to use, copy, modify, merge, publish,
distribute, sublicense, and/or sell copies of the Software, and to
permit persons to whom the Software is furnished to do so, subject to
the following conditions:

The above copyright notice and this permission notice shall be included
in all copies or substantial portions of the Software.

The name of the authors when specified in the source files shall be 
kept unmodified.

THE SOFTWARE IS PROVIDED ``AS IS'', WITHOUT WARRANTY OF ANY KIND, EXPRESS
OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
IN NO EVENT SHALL 4XT.ORG BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE
USE OR OTHER DEALINGS IN THE SOFTWARE.


$Id: $
$Author: $
$Revision: $
$Date: $
$Locker: $
$RCSfile: $
$Source: $
$State: $
$Name: $
$Log: $


*************************************************************************/
package org.lindenb.lib.debug;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * @author pierre
 *
 */
public class Debug
    {
    static private PrintStream err=System.err;
    static private int level=1;
    
    public static PrintStream getStream()
        {
        return err;
        }
    
    public static void setStream(PrintStream errstream)
    {
        if(err!=null) err.flush();
        err= errstream;   
        if(errstream==null) err=System.err;
     
    }
    
    public static void setDebugging(boolean b)
        {
        setDebugging(b==true?1:0);
        }
 
    public static void setDebugging(int newlevel)
    {
    level=newlevel;
    }
    
    
    public static boolean isDebugging()
        {
        return level!=0;
        }
    
    public static void doAssert(boolean b)
    {
    	if(b) return;
    	//if(!isDebugging()) return; NON toujours vérifier
    	try
	    	{
	        throw new Throwable();
	    	}
	    catch(Throwable t)
	    	{
	    	getStream().println("#Assertion failed");
	        t.printStackTrace(getStream());
	        System.exit(-1);
	    	}
    }
    
    public static String getStackTrace()
        {
        StringWriter writer= new StringWriter();
        PrintWriter p= new PrintWriter(writer,true);
        try
            {
            throw new Throwable();
            }
        catch(Throwable t)
             {
             t.printStackTrace(p);
             p.flush();
             }
        return writer.toString();
        }
    
    public static void traceStack(Object msg)
        {
        if(!isDebugging()) return;
        try
            {
            throw new Throwable();
            }
        catch(Throwable t)
            {
            getStream().println("#"+msg);
            t.printStackTrace(getStream());
            }
        }
    
    /** return the traceStack.length */
    public static int depth()
    	{
    	 if(!isDebugging()) return -1;
         try
             {
             throw new Throwable();
             }
         catch(Throwable t)
             {
             
             return t.getStackTrace().length-1;
             }
    	}
    
    public static void traceStack()
        {
        traceStack("");
        }
    
    public static void trace()
        {
        if(!isDebugging()) return;
        getStream().println(where(2));
        }
  
    public static void trace(Object o)
        {
        if(!isDebugging()) return;
        getStream().println(where(2)+o);
        }
   
    public static void trace(int i )
        {
        if(!isDebugging()) return;
        getStream().println(where(2)+i);
        }
    
    public static void trace(double i )
        {
        if(!isDebugging()) return;
        getStream().println(where(2)+i);
        }
    
    private static String where(int n)
        {
        try
            {
            throw new Throwable();
            }
        catch(Throwable t)
             {
             StackTraceElement stack[]=t.getStackTrace();
             return "#"+   stack[n].getClassName()+":"+
                           stack[n].getMethodName()+":"+
                           stack[n].getLineNumber()+": ";
             }
        }
    
    public static String where()
        {
        return where(2);
        }
    
    }
