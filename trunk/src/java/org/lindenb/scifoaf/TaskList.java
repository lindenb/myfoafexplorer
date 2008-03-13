/*
 * Author: Pierre Lindenbaum PhD
 * Contact: plindenbaum@yahoo.fr
 * Created on 10:57:28 AM
 * 
 * For condition of distribution and use, see the accompanying README file.
 *
 * $Id: $
 * $Author: $
 * $Revision: $
 * $Date: $
 * $Source: $
 * $Log: $
 * 
 */
package org.lindenb.scifoaf;


import java.util.Vector;

import javax.swing.ListModel;
import javax.swing.event.ListDataListener;

/**
 * @author lindenb
 *
 * <code>TaskList</code>
 */
public class TaskList implements ListModel {
    Vector datas;
    Vector listeners;
    
    public TaskList()
    {
        this.datas= new Vector();
        this.listeners= new Vector();
    }
   
    public Task removeLastTask()
    {
        Task t= getTopTask();
        this.datas.removeElement(t);
        return t;
    }
    
    public Task addTask(Task task)
    {
        for(int i=0;i< getTaskCount();++i)
        {
            if(getTaskAt(i).equals(task)) return getTaskAt(i);
        }
        this.datas.addElement(task);
        return task;
    }
    
    public Task getTopTask()
    {
        return (Task)this.datas.lastElement();
    }
    
   
    public void removeTask(Task task)
    {
        this.datas.remove(task);
    }
    
    
    public Task getTaskAt(int index)
    {
        return (Task)this.datas.elementAt(index);
    }

    public int getTaskCount()
    {
        return this.datas.size();
    }
    
    /**
     * overides/implements parent
     * @see javax.swing.ListModel#getSize()
     */
    public int getSize() {
        return getTaskCount();
    }

    /**
     * overides/implements parent
     * @see javax.swing.ListModel#getElementAt(int)
     */
    public Object getElementAt(int index) {
       return getTaskAt(index);
    }

    /**
     * overides/implements parent
     * @see javax.swing.ListModel#addListDataListener(javax.swing.event.ListDataListener)
     */
    public void addListDataListener(ListDataListener l) {
       this.listeners.addElement(l);

    }

    /**
     * overides/implements parent
     * @see javax.swing.ListModel#removeListDataListener(javax.swing.event.ListDataListener)
     */
    public void removeListDataListener(ListDataListener l) {
        this.listeners.remove(l);
    }

}
