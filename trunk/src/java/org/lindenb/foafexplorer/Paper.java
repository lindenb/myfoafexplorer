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


import java.io.IOException;



import org.lindenb.lib.rdf.RDFUtils;
import org.lindenb.lib.xml.Namespaces;
import org.w3c.dom.Element;

/**
 * @author lindenb
 *
 */
public class Paper extends Agent
	{
	/** name */
	protected String name;

	
	
	protected Paper(Model model, String rdfID)
		{
		super(model,rdfID);
		}

	/* (non-Javadoc)
	 * @see org.lindenb.genealogy.Agent#getSearchableStrings()
	 */
	protected String[] getSearchableStrings()
		{
		return new String[]{getName(),getDescription(),getID()};
		}
	
	public String getName()
		{
		return this.name==null?getID():this.name;
		}
	

	public String toString()
		{
		return getName();
		}
	
	public int getType()
		{
		return T_PAPER;
		}

	
	/** parse Group from DOM */
	static Paper parse(Element node,Model model) throws IOException
		{
		String rdfID= RDFUtils.getProperty(node,Namespaces.RDF,"ID");
		if(rdfID==null) rdfID= RDFUtils.getProperty(node,Namespaces.RDF,"about");
		
		Paper paper= new Paper(model,rdfID);
		paper.description = RDFUtils.getProperty(node,Namespaces.DC,"description");
		paper.name= RDFUtils.getProperty(node,Namespaces.DC,"title");
		if(paper.name==null ) paper.name= RDFUtils.getProperty(node,Namespaces.FOAF,"name");
		return paper;
		}
	
	}