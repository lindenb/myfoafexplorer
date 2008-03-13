/*
 * Created on 10 janv. 2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.lindenb.foafexplorer;



/**
 * @author pierre
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class GroupTableModel extends AgentTableModel
	{
	private static final long serialVersionUID = 1L;
	private static String COLS[]={"Name","Count"};
	
	public GroupTableModel()
		{
		
		}
	
	public Group addGroup(Group g)
		{
		return (Group)super.addAgent(g);
		}
	

	
	
	public int getGroupCount()
		{
		return super.getRowCount();
		}
	
	/* (non-Javadoc)
	 * @see javax.swing.table.AbstractTableModel#getColumnClass(int)
	 */
	public Class getColumnClass(int columnIndex)
		{
		switch(columnIndex)
			{
			case 0: return String.class;
			case 1: return Integer.class;
			}
		return null;
		}
	

	public Group getGroupAt(int index)
		{
		return (Group)getAgentAt(index);
		}

	public Group getGroupbyURI(String rdfID)
		{
		return (Group)getAgentByURI(rdfID);
		}
	
	
	public int getColumnCount() {
		return COLS.length;
		}

	
	public String getColumnName(int column)
		{
		return COLS[column];
		}
	
	public Object getValueAt(int row, int column)
		{
		Group i=getGroupAt(row);
		switch(column)
			{
			case 0: return i.getName();
			case 1: return new Integer(i.getMemberCount());
			}
		return null;
		}
}
