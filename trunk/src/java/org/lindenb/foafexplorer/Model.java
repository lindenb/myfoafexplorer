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
package org.lindenb.foafexplorer;


import java.awt.geom.Point2D;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Vector;


import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;


import org.lindenb.lib.rdf.RDFUtils;
import org.lindenb.lib.xml.Namespaces;
import org.lindenb.lib.xml.XMLUtilities;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * @author lindenb
 *
 */
public class Model
	{
	public static String NS="http://genealogy.lindenb.org/";
	private static final long serialVersionUID = 1L;
	

	private IndividualTableModel individuals;
	private GroupTableModel groups;
	private PaperTableModel papers;
	
	public Model()
		{
		this.individuals= new IndividualTableModel();
		this.groups= new GroupTableModel();
		this.papers= new PaperTableModel();
		}
	
	public IndividualTableModel getIndividuals()
		{
		return this.individuals;
		}
	
	public GroupTableModel getGroups()
		{
		return this.groups;
		}
	
	public PaperTableModel getPapers()
		{
		return this.papers;
		}
	
	
	
	
	
	public void paint(GC gc)
		{
		for(int step=0;step<2;++step)
			{
			gc.step=step;
			for(int i=0;i< getIndividuals().getIndividualCount();++i)
				{
				getIndividuals().getIndividualAt(i).paint(gc);
				}
			}
		}
	
	
	static Model load(File in) throws IOException
		{
		return Model.load(new FileInputStream(in));
		}
	
	static Model load(InputStream in) throws IOException
		{
		Model model= new Model();
		int row=0;int col=0;
		
		try
			{
			DocumentBuilderFactory factory=DocumentBuilderFactory.newInstance();
			factory.setNamespaceAware(true);
			factory.setCoalescing(true);
			factory.setExpandEntityReferences(true);
			factory.setIgnoringComments(true);
			factory.setIgnoringElementContentWhitespace(true);
			factory.setValidating(false);
			DocumentBuilder builder= factory.newDocumentBuilder();
			Document doc= builder.parse(in);
			Element root = doc.getDocumentElement();
			if(root==null || !XMLUtilities.isA(root,Namespaces.RDF,"RDF"))
				{
				throw new IOException("Not a rdf:RDF root");
				}
			HashMap uri2agent= new HashMap();
			Vector agentRel= new Vector();
			Vector indiImg= new Vector();
			
			int squaresize=XMLUtilities.coutElementNS(root,Namespaces.FOAF,"Person");
			if(squaresize==0) squaresize=1;
			squaresize= (int)Math.floor(Math.sqrt(squaresize));
			
			
			for(Node c1= root.getFirstChild();c1!=null;c1=c1.getNextSibling())
				{
				if(c1.getNodeType()!=Node.ELEMENT_NODE) continue;
				Element e1=(Element)c1;
				//Debug.trace(e1.getNodeName());
				if(XMLUtilities.isA(e1,Namespaces.FOAF,"Person"))
					{
					Individual indi= Individual.parse((Element)e1,model);
					
					String pointStr= RDFUtils.getProperty(e1,Model.NS,"location");
					if(pointStr!=null)
						{
						int loc= pointStr.indexOf(',');
						indi.setLocation(
								2*Integer.parseInt(pointStr.substring(0,loc)),
								2*Integer.parseInt(pointStr.substring(loc+1))
								);
						}
					
					uri2agent.put(indi.getID(),indi);
					model.getIndividuals().addIndividual(indi);
					
					findRelationships(e1,indi,agentRel);
					
					String imageid = RDFUtils.getProperty(e1,Namespaces.FOAF,"img");
					if(imageid!=null)
						{
						indiImg.addElement(indi);
						indiImg.addElement(imageid);
						}
					}
				else if(XMLUtilities.isA(e1,Namespaces.FOAF,"Group"))
					{
					Group group= Group.parse((Element)e1,model);
					
					uri2agent.put(group.getID(),group);
					model.getGroups().addGroup(group);
					findRelationships(e1,group,agentRel);
					
					String imageid = RDFUtils.getProperty(e1,Namespaces.FOAF,"img");
					if(imageid!=null)
						{
						indiImg.addElement(group);
						indiImg.addElement(imageid);
						}
					}
				else if(XMLUtilities.isA(e1,Namespaces.FOAF,"Document"))
					{
					Paper paper= Paper.parse((Element)e1,model);
					
					uri2agent.put(paper.getID(),paper);
					model.getPapers().addPaper(paper);

					findRelationships(e1,paper,agentRel);
					
					}
				else if(XMLUtilities.isA(e1,Namespaces.FOAF,"Image"))
					{
					Image img= Image.parse((Element)e1,model);
					uri2agent.put(img.getID(),img);
					}
				else
					{
					System.err.println("Cannot handle "+e1.getNodeName());
					}
				}
			
	
			
			for(int i=0;i< model.getIndividuals().getIndividualCount();++i)
				{
				Individual indi=  model.getIndividuals().getIndividualAt(i);
				if(indi.getLocation()==null)
					{
					indi.setLocation(
							Image.SIZE+row*(Image.SIZE+Image.SIZE/2),
							Image.SIZE+col*(Image.SIZE+Image.SIZE/2)
							);
			
					if((++col+1)==squaresize) { col=0;++row;}
					}
				}
			
			
			for(int i=0;i< indiImg.size();i+=2)
				{
				AbstractPeople indi=(AbstractPeople)indiImg.elementAt(i+0);
				
				String s =RDFUtils.removeDiez((String)indiImg.elementAt(i+1));
				if(s!=null)
					{
					Image img = (Image)uri2agent.get(RDFUtils.removeDiez(s));
					if(img!=null)
						{
						indi.setImage(img);
						}
					else
						{
						try
							{
							img = new Image(model,s);
							indi.setImage(img);
							}
						catch (Exception e) {
							e.printStackTrace();
						System.err.println("Cannot read image s="+s);
						}
						}
					}
				}
			
			

			for(int i=0;i< agentRel.size();i+=3)
				{
				Agent indi=(Agent)agentRel.elementAt(i+0);
				String uri=(String)agentRel.elementAt(i+1);
				String dest=(String)agentRel.elementAt(i+2);
				if(dest==null)  continue;
				Agent agent2= (Agent)uri2agent.get(dest);
				if(agent2==null)
					{
					System.err.println("Cannot find resource "+dest+
							" ("+indi+" is "+uri+")"
							);
					continue;
					}
				indi.addRelationShip(new Relationship(indi,agent2,uri));
				}
			
			for(int i=0;i< model.getGroups().getGroupCount();++i)
				{
				Group g= model.getGroups().getGroupAt(i);
				Point2D geoloc= g.getGeoLoc();
				if(geoloc==null) continue;
				for(int j=0;j< g.getMemberCount();++j)
					{
					AbstractPeople p=g.getMemberAt(i);
					if(p.getType()!=Agent.T_INDIVDIDUAL) continue;
					if(p.getGeoLoc()==null)
						{
						((Individual)p).setLocation(geoloc.getX(),geoloc.getY());
						}
					}
				}
			
			}
		catch (Exception e)
			{
			e.printStackTrace();
			throw new IOException(e.getMessage());
			}
		return model;
		}
	
	
	private static void findRelationships(Element root, Agent agent, Vector agentRel)
		{
		for(int i=0;i< Relationship.URIs.length;i+=3)
			{
			String s[]= RDFUtils.getProperties(root,
					RDFUtils.namespaceOf(Relationship.URIs[i]),
					RDFUtils.localNameOf(Relationship.URIs[i])
					);
			//System.err.println(agent+" "+RDFUtils.localNameOf(Relationship.URIs[i])+ " "+s.length);
			for(int j=0;j < s.length;++j)
				{
				agentRel.addElement(agent);
				agentRel.addElement(Relationship.URIs[i]);
				agentRel.addElement(RDFUtils.removeDiez(s[j]));
				}
			}
		}
	
	}
