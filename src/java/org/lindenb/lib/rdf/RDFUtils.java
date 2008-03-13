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
package org.lindenb.lib.rdf;

import org.lindenb.lib.xml.Namespaces;
import org.lindenb.lib.xml.XMLUtilities;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * @author lindenb
 *
 */
public class RDFUtils
	{
	public static boolean isRDFContainer(Node node)
		{
		return (node!=null &&
				node.getNodeType()==Node.ELEMENT_NODE &&
				node.getNamespaceURI().equals(Namespaces.RDF) &&
				(
				node.getLocalName().equals("Seq") ||
				node.getLocalName().equals("Bag") ||
				node.getLocalName().equals("Alt")
				)
				);
		}
	
	/**	 finds string before '/' or '#' */
	public static String namespaceOf(String uri)
		{
		return uri.substring(0,1+split((uri)));
		}
	
	/**	finds string after '/' or '#' */
	public static String localNameOf(String uri)
		{
		return uri.substring(split((uri))+1);
		}
	
	/**	finds last position of '/' or '#' */
	private static int split(String uri)
		{
		int i= uri.lastIndexOf('#');
		if(i!=-1) return i;
		return uri.lastIndexOf('/');
		}
	
	/**	remove the first '#' at the beginning of a string */
	public static String removeDiez(String s)
		{
		if(s==null || !s.startsWith("#")) return s;
		return s.substring(1);
		}
	
	public static String[] getProperties(Element node,String namespaceuri,String localName)
		{
		String ss[]= getProperties_(node,namespaceuri,localName,Integer.MAX_VALUE);
		if(ss==null) ss=new String[0];
		return ss;
		}
	
	public static String getProperty(Element node,String namespaceuri,String localName)
		{
		String ss[]= getProperties_(node,namespaceuri,localName,0);
		if(ss==null || ss.length==0) return null;
		return ss[0];
		}
	
	private static String[] getProperties_(Element node,String namespaceuri,String localName,int maxCount)
		{
		String ss[]=null;
		if(node.hasAttributes())
			{
			NamedNodeMap atts= node.getAttributes();
			Attr att= (Attr)atts.getNamedItemNS(namespaceuri,localName);
			if(att!=null)
				{
				 if(XMLUtilities.isA(att,namespaceuri,localName))
					 {
					 ss= push_back(ss,att.getValue());
					 if(ss.length> maxCount) return ss;
					 }
				}
				
			}
		
		for(Node c=node.getFirstChild();c!=null;c=c.getNextSibling())
			{
			if(c.getNodeType()!=Node.ELEMENT_NODE) continue;
			String ns= c.getNamespaceURI();
			if(ns==null)
				{
				continue;
				}
			else if(XMLUtilities.isA(c,namespaceuri,localName))
				{
				String parseType=null;
				if(c.hasAttributes())
					{
					Attr att=(Attr)c.getAttributes().getNamedItemNS(Namespaces.RDF,"parseType");
					if(att!=null) parseType=att.getValue();
					}
				if(parseType==null)
					{
					if(c.hasChildNodes())
						{
						ss= push_back(ss,c.getTextContent());
						if(ss.length>maxCount) return ss;
						}
					else
						{
						Attr att=(Attr)c.getAttributes().getNamedItemNS(Namespaces.RDF,"resource");
						if(att!=null)
							{
							ss= push_back(ss,att.getValue());
							if(ss.length>maxCount) return ss;
							}
						else
							{
							throw new RuntimeException("Not Handled !!");
							}
						}
					}
				else
					{
					throw new RuntimeException("Not Handled !!");
					}
				}
			else if(isRDFContainer(c))
				{
				String ss2[]= getProperties_((Element)c,namespaceuri,localName,maxCount);
				for(int i=0;ss2!=null && i< ss2.length;++i)
					{
					ss= push_back(ss,ss2[i]);
					if(ss.length>maxCount) return ss;
					}
				}
			}
		return ss;
		}
	
	private static String[] push_back(String ss[],String s)
		{
		if(s==null) return ss;
		if(ss==null) return new String[]{s};
		String s2[]= new String[ss.length+1];
		System.arraycopy(ss,0,s2,0,ss.length);
		s2[ss.length]=s;
		return s2;
		}
	
	}
