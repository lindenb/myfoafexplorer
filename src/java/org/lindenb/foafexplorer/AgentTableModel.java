/*
 * Created on 10 janv. 2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.lindenb.foafexplorer;

import java.util.HashMap;
import java.util.Vector;

import javax.swing.table.AbstractTableModel;

/**
 * @author pierre
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public abstract class AgentTableModel extends AbstractTableModel
	{
	/** map URI to agent */
	private HashMap uri2agent;
	private Vector agents;
	
	public AgentTableModel()
		{
		this.uri2agent= new HashMap();
		this.agents= new Vector();
		}
	
	public int getRowCount()
		{
		return getAgentCount();
		}

	boolean isEmpty()
		{
		return this.agents.isEmpty();
		}
	
	public int getAgentCount()
		{
		return this.agents.size();
		}
	
	public Agent getAgentAt(int i)
		{
		return (Agent)this.agents.elementAt(i);
		}
	public boolean contains(Agent agent)
		{
		return this.uri2agent.containsKey(agent.getID());
		}
	
	public int indexOf(Agent agent)
		{
		return this.agents.indexOf(agent);
		}
	
	
	public Agent getAgentByURI(String uri)
		{
		return (Agent)this.uri2agent.get(uri);
		}
	
	public Agent addAgent(Agent agent)
		{
		Agent first= getAgentByURI(agent.getID());
		if(first!=null) return first;
		this.uri2agent.put(agent.getID(),agent);
		this.agents.addElement(agent);
		fireTableRowsInserted(getAgentCount()-1,getAgentCount()-1);
		return agent;
		}
	
	public final Agent removeAgent(Agent agent)
		{
		return removeAgentAt(this.agents.indexOf(agent));
		}
	
	public Agent removeAgentAt(int idx)
		{
		if(idx==-1) return null;
		Agent agent= getAgentAt(idx);
		this.uri2agent.remove(agent.getID());
		this.agents.removeElementAt(idx);
		fireTableRowsDeleted(idx,idx);
		return agent;
		}
	
	public void clear()
		{
		this.agents.clear();
		fireTableDataChanged();
		}
	
	/* (non-Javadoc)
	 * @see javax.swing.table.DefaultTableModel#setValueAt(java.lang.Object, int, int)
	 */
	public void setValueAt(Object aValue, int row, int column)
		{
		}
	
	/* (non-Javadoc)
	 * @see javax.swing.table.DefaultTableModel#isCellEditable(int, int)
	 */
	public boolean isCellEditable(int row, int column)
		{
		// TODO Auto-generated method stub
		return false;
		}
	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getColumnCount()
	 */
	public abstract int getColumnCount();

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getValueAt(int, int)
	 */
	public abstract Object getValueAt(int arg0, int arg1);

}
