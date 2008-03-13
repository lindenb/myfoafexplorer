/*
 * Created on 10 janv. 2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.lindenb.foafexplorer;

import java.io.IOException;

import org.lindenb.lib.rdf.RDFUtils;
import org.lindenb.lib.xml.Namespaces;
import org.w3c.dom.Element;

/**
 * @author pierre
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class Group extends AbstractPeople
	{
	//private IndividualTableModel individuals;
	private AbstractPeople members[];
	/**
	 * @param model
	 * @param rdfID
	 */
	public Group(Model model, String rdfID)
		{
		super(model, rdfID);
		members=new AbstractPeople[0];
		//this.individuals= new IndividualTableModel();
		}
	
	public int getType()
		{
		return T_GROUP;
		}
	
	/*public IndividualTableModel getIndividuals()
		{
		//return this.individuals;
		}*/
	
	/* (non-Javadoc)
	 * @see org.lindenb.genealogy.Agent#addRelationShip(org.lindenb.genealogy.Relationship)
	 */
	public void addRelationShip(Relationship rel)
		{
		if(rel==null ) return;
		if(hasRelationShip(rel)) return;
		
		if(rel.hasURI(Namespaces.FOAF + "member") &&
				(rel.getAgent(1).getType()==T_INDIVDIDUAL) ||
				(rel.getAgent(1).getType()==T_GROUP)
				)
			{
			AbstractPeople a[]= new Individual[this.members.length+1];
			System.arraycopy(this.members,0,a,0,this.members.length);
			a[this.members.length]=(AbstractPeople)rel.getAgent(1);
			this.members=a;
			}
		
		super.addRelationShip(rel);
		}
	
	public int getMemberCount()
		{
		return members.length;
		}
	
	public AbstractPeople getMemberAt(int i)
		{
		return members[i];
		}
	
	/** parse Group from DOM */
	static Group parse(Element node,Model model) throws IOException
		{
		String rdfID= RDFUtils.getProperty(node,Namespaces.RDF,"ID");
		Group group= new Group(model,rdfID);
		group.parse_(node);
		return group;
		}
	
	}
