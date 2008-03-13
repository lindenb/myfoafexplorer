/*
 * Created on 10 janv. 2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.lindenb.foafexplorer;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.table.AbstractTableModel;


import org.lindenb.lib.rdf.RDFUtils;

/**
 * @author pierre
 *
 * base class for image, individuals, group, etc...
 */
public abstract class Agent
	{
	public static final int T_INDIVDIDUAL=0;
	public static final int T_GROUP=1;
	public static final int T_PAPER=2;
	public static final int T_IMAGE=3;
	
	private String rdfID;
	private Model model;
	private Relationship relationships[];
	protected String description;
	
	protected Agent(Model model,String rdfID)
		{
		this.model=model;
		this.rdfID=rdfID;
		this.relationships= new Relationship[0];
		if(this.rdfID==null) throw new NullPointerException("rdf:ID not found");
		}
	
	public abstract int getType();
	
	public boolean hasRelationShip(String uri,Agent dest)
		{
		return hasRelationShip(new Relationship(this,dest,uri));
		}
	
	public boolean hasRelationShip(Relationship rel)
		{
		for(int i=0;i< this.relationships.length;++i)
			{
			if(this.relationships[i].equals(rel)) return true;
			}
		return false;
		}
	
	public void addRelationShip(Relationship rel)
		{
		if(rel==null || hasRelationShip(rel)) return;
		if(rel.getAgent(0)!=this) throw new IllegalArgumentException(rel.toString());
		Relationship a[]= new Relationship[this.relationships.length+1];
		System.arraycopy(this.relationships,0,a,0,this.relationships.length);
		a[this.relationships.length]=rel;
		this.relationships=a;
		
		Relationship inverse= rel.getInverseRelationShip();
		if(inverse!=null)
			{
			rel.getAgent(1).addRelationShip(inverse);
			}
		}
	
	public int getRelationCount() { return this.relationships.length;}
	public Relationship getRelationShipAt(int i) { return this.relationships[i];}
	
	public Model getModel()
		{
		return this.model;
		}
	
	public String getID()
		{
		return this.rdfID;
		}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj)
		{
		if(this==obj) return true;
		if(null==obj) return false;
		if(!(obj instanceof Agent)) return false;
		return getID().equals(((Agent)obj). getID()) &&
			   getType()==((Agent)obj). getType();
		}
	
	public int hashCode() {
		return getID().hashCode();
		}
	
	public String toString()
		{
		return getID();
		}
	
	public String getDescription()
		{
		return(this.description==null?getID():this.description);
		}
	
	protected String[] getSearchableStrings()
		{
		return new String[]{getDescription(),getID()};
		}
	
	public boolean match(Pattern regex)
		{		
		String ss[]= getSearchableStrings();
		for(int i=0;ss!=null && i< ss.length;++i)
			{
			if(ss[i]==null) continue;
			Matcher m=regex.matcher(ss[i]);
			if(m.find()) return true;
			}
		return false;
		}
	
	public AbstractTableModel getRelationShips()
		{
		return new AbstractTableModel()
			{
			private static final long serialVersionUID = 1L;

			public int getRowCount()
				{
				return Agent.this.getRelationCount();
				}

			public int getColumnCount()
				{
				return 2;
				}

			/* (non-Javadoc)
			 * @see javax.swing.table.AbstractTableModel#getColumnName(int)
			 */
			public String getColumnName(int column)
				{
				return (column==0?"Relation":"Value");
				}
			
			/* (non-Javadoc)
			 * @see javax.swing.table.AbstractTableModel#getColumnClass(int)
			 */
			public Class getColumnClass(int columnIndex)
				{
				return (columnIndex==0?String.class:Object.class);	
				}
			
			
			public Object getValueAt(int rowIndex, int columnIndex)
				{
				switch(columnIndex)
					{
					case 0: return RDFUtils.localNameOf(getRelationShipAt(rowIndex).getURI());
					case 1: return getRelationShipAt(rowIndex).getAgent(1);
					}
				return null;
				}
			
			public boolean isCellEditable(int rowIndex, int columnIndex)
				{
				return false;
				}
			
			};
		}
	
	}
