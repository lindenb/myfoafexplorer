/*
 * Created on 10 janv. 2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.lindenb.foafexplorer;

import java.awt.Point;


/**
 * @author pierre
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class IndividualTableModel extends AgentTableModel
	{
	private static final long serialVersionUID = 1L;
	private static String COLS[]={"Name"};
	//private Rectangle rect;
	//private Point barycentre=null;
	
	public IndividualTableModel()
		{

		}
	
	public Individual addIndividual(Individual individual)
		{
		return (Individual)super.addAgent(individual);
		}
	
	/* (non-Javadoc)
	 * @see javax.swing.table.AbstractTableModel#getColumnName(int)
	 */
	public String getColumnName(int column)
		{
		return COLS[column];
		}
	
	/** returns barycentre of individuals */
	public Point getCenter()
		{
		Point c=new Point(0,0);
		if(getIndividualCount()==0) return c;
		for(int i=0;i< getIndividualCount();++i)
			{
			c.x+= (int)getIndividualAt(i).getX();
			c.y+= (int)getIndividualAt(i).getY();
			}
		c.x/=getIndividualCount();
		c.y/=getIndividualCount();
		return c;
		}
	
	
	public int getIndividualCount()
		{
		return super.getRowCount();
		}
	
	/* (non-Javadoc)
	 * @see javax.swing.table.AbstractTableModel#getColumnClass(int)
	 */
	public Class getColumnClass(int columnIndex)
		{
		return String.class;
		}
	
	public Individual findIndividualAt(int x, int y)
		{
		int i= findIndividualIndexAt(x,y);
		if(i==-1) return null;
		return getIndividualAt(i);
		}

	public int findIndividualIndexAt(int x, int y)
		{
		for(int i=0;i< getIndividualCount();++i)
			{
			Individual indi= getIndividualAt(i);
			if(indi.contains(x,y)) return i;
			}
		return -1;
		}
	
	
	
	public Individual getIndividualAt(int index)
		{
		return (Individual)getAgentAt(index);
		}

	public Individual getIndividualbyURI(String rdfID)
		{
		return (Individual)getAgentByURI(rdfID);
		}
	
	
	public int getColumnCount() {
		return COLS.length;
		}

	public Object getValueAt(int row, int column)
		{
		Individual i=getIndividualAt(row);
		switch(column)
			{
			case 0: return i.getName();
			}
		return null;
		}

	
}
