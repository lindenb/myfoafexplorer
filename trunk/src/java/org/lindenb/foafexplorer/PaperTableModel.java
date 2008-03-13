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
public class PaperTableModel extends AgentTableModel
	{
	private static final long serialVersionUID = 1L;
	private static String COLS[]={"Title"};
	
	public PaperTableModel()
		{
		
		}
	
	public Paper addPaper(Paper g)
		{
		return (Paper)super.addAgent(g);
		}
	

	

	public int getPaperCountt()
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
			}
		return null;
		}
	
	public String getColumnName(int column)
		{
		return COLS[column];
		}

	public Paper getPaperAt(int index)
		{
		return (Paper)getAgentAt(index);
		}

	public Paper getPaperbyURI(String rdfID)
		{
		return (Paper)getAgentByURI(rdfID);
		}
	
	
	public int getColumnCount() {
		return COLS.length;
		}

	public Object getValueAt(int row, int column)
		{
		Paper i=getPaperAt(row);
		switch(column)
			{
			case 0: return i.getName();
			}
		return null;
		}
}
