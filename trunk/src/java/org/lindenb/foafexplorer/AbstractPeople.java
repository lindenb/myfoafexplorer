/*
Copyright (c) 2006 Pierre Lindenbaum PhD

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
import java.io.IOException;

import javax.swing.Icon;
import javax.swing.ImageIcon;


import org.lindenb.lib.rdf.RDFUtils;
import org.lindenb.lib.xml.Namespaces;
import org.lindenb.lib.xml.XMLUtilities;
import org.w3c.dom.Element;
import org.w3c.dom.Node;


/**
 * @author lindenb
 *
 */
public abstract class AbstractPeople extends Agent
	{
	/** name */
	protected String name=null;
	/** picture for this individual */
	private Image image=null;
	/** geographical location */
	private Point2D.Double geoloc;
	
	protected AbstractPeople(Model model, String rdfID)
		{
		super(model,rdfID);
		this.geoloc=null;
		}

	public Point2D getGeoLoc()
		{
		return this.geoloc;
		}
	
	public String getDescription()
		{
		return(this.description==null?getName():this.description);
		}

	public Icon getIcon()
		{
		if(getImage()!=null && getImage().getImage()!=null)
			{
			return new ImageIcon(getImage().getImage(),getDescription());
			}
		return null;
		}
	
	public Image getImage()
		{
		return this.image;
		}
	
	void setImage(Image image)
		{
		this.image=image;
		}
	
	
	
	public String getName()
		{
		return this.name==null?getID():this.name;
		}
	

	public String toString()
		{
		return getName();
		}
	
	
	/** parse individual from DOM */
	protected void parse_(Element node) throws IOException
		{
		this.description= RDFUtils.getProperty(node,Namespaces.DC,"description");
		this.name= RDFUtils.getProperty(node,Namespaces.FOAF,"name");
		if(getType()==T_INDIVDIDUAL)
			{
			if(this.name!=null)
				{
				String title= RDFUtils.getProperty(node,Namespaces.FOAF,"title");
				if(title!=null) this.name=title+" "+this.name;
				}
			}
		for(Node c1=node.getFirstChild();c1!=null;c1=c1.getNextSibling())
			{
			if(XMLUtilities.isA(c1,Namespaces.FOAF,"based_near"))
				{
				for(Node c2=c1.getFirstChild();c2!=null;c2=c2.getNextSibling())
					{
					if(XMLUtilities.isA(c2,Namespaces.GEO,"Point"))
						{
						try
							{
							this.geoloc=new Point2D.Double(
									Double.parseDouble(RDFUtils.getProperty((Element)c2,Namespaces.GEO,"long")),
									Double.parseDouble(RDFUtils.getProperty((Element)c2,Namespaces.GEO,"lat"))
								);
							//Debug.trace(""+this.name+" "+this.geoloc);
							}
						catch (Exception e) {
							this.geoloc=null;
							e.printStackTrace();
							}
						break;
						}
					}
				}
			}
		}
	}