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
/*
 * Author: Pierre Lindenbaum PhD
 * Contact: plindenbaum@yahoo.fr
 * Created on Jan 4, 2005
 * 
 * For condition of distribution and use, see the accompanying README file.
 *
 * $Id: $
 * $Author: $
 * $Revision: $
 * $Date: $
 * $Source: $
 * $Log: $
 * 
 */
package org.lindenb.lib.xml;


import java.io.Writer;
import java.util.Collection;
import java.util.Vector;


import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.*;

/**
 * @author lindenb
 *
 * static utilities for XML
 */
public class XMLUtilities
    {
    static public final String XML_NS_URI="http://www.w3.org/XML/1998/namespace";
    
    /** count number of child elements with defined uri and localname */
    static public int coutElementNS(Node parent,String uri,String localName)
    	{
    	int n=0;
        for(Node c=parent.getFirstChild();c!=null;c=c.getNextSibling())
        	{
        	n+=(isA(c,uri,localName)?1:0);
        	}
        return n;
    	}
    
    static public Element findFirstChildElement(Node parent,String name)
    	{
        if(parent==null) return null;
        return findChildElement(parent.getFirstChild(),null,name);
    	}
    
    
    
    static public Element findFirstChildElementNS(Node parent,String namespaceuri,String qname)
		{
	    if(parent==null) return null;
	    return findChildElementNS(parent.getFirstChild(),null,namespaceuri,qname);
		}
    
    
    static public Element findChildElement(Node first,Node last,String name)
    	{
    	while(first!=last)
    		{
    		if(first.getNodeType()==Node.ELEMENT_NODE)
    			{
    			if(first.getNodeName().equals(name)) return (Element)first;
    			}
    		first=first.getNextSibling();
    		}
    	return null;
    	}
    
    static public Element[] findChildElements(Node first,Node last,String name)
	{
        Vector v= new Vector();
    	while(first!=last)
    		{
    		if(first.getNodeType()==Node.ELEMENT_NODE)
    			{
    			if(first.getNodeName().equals(name)) v.addElement(first);
    			}
    		first=first.getNextSibling();
    		}
    	Element array[]=new Element[v.size()];
    	for(int i=0;i< array.length;++i)
    	{
    	    array[i]=(Element)v.elementAt(i);
    	}
    	return array;
    	}
   

    
    static public Element findChildElementNS(Node first,Node last,String namespaceuri,String qname)
	{
	while(first!=last)
		{
		if(first.getNodeType()==Node.ELEMENT_NODE)
			{
			if(isA(first,namespaceuri,qname))
			    	{
			        return (Element)first;
			    	}
			 
			}
		first=first.getNextSibling();
		}
	return null;
	}
    
    static public Element[] findAllDescendantElements(Node e,String qname)
    {
    	Vector v= new Vector();
    	findAllDescendantElements(e,qname,v);
    	Element array[]=new Element[v.size()];
    	for(int i=0;i< array.length;++i)
    	{
    	    array[i]=(Element)v.elementAt(i);
    	}
    	return array;
    }
    
    static public int findAllDescendantElements(Node e,String qname,Collection vector)
    	{
    	int n=0;
    	if(e==null || qname==null) return 0;
    	for(Node c=e.getFirstChild();c!=null;c=c.getNextSibling())
			{
			if(c.getNodeType()==Node.ELEMENT_NODE && c.getNodeName().equals(qname))
				{
				++n;
				vector.add((Element)c);
				}
			n+=findAllDescendantElements(c,qname,vector);
			}
    	return n;
    	}
    
    
    static public String textContent(Node node)
    	{
        return textContent(node,new StringBuffer()).toString();
    	}
    
    static StringBuffer textContent(Node node,StringBuffer s)
    {
        if(node==null) return s;
        for(Node c= node.getFirstChild();c!=null;c=c.getNextSibling())
            {
                if(c.getNodeType()==Node.CDATA_SECTION_NODE)
                {
                 s.append(((CDATASection)c).getNodeValue());
                }
                else if(c.getNodeType()==Node.TEXT_NODE)
                {
                 s.append(((Text)c).getNodeValue());
                }
                else
                {
                 textContent(c,s);
                }
            }
       return s;
    }
    
    /**
     * recursively transform the prefix and the namespace of a node where getNamespaceURI is NULL
     * @param node the node to transform
     * @param prefix the new prefix
     * @param namespaceuri the new namespace uri
     * @return the new node with NS and prefix changed
     */
    static public Node setPrefixNamespace(Node node,String prefix, String namespaceuri)
        {
        Node dest=null;
        if(node.getNodeType()==Node.ELEMENT_NODE)
            {
            Element e= (Element)node;
            if(e.getNamespaceURI()==null)
                {
                
                Element e2= node.getOwnerDocument().createElementNS(
                        namespaceuri,
                        (prefix!=null? prefix+':'+e.getLocalName() : e.getLocalName() )
                        );
                NamedNodeMap nodes= e.getAttributes();
                for(int i=0;i< nodes.getLength();++i)
                    {
                    Attr att= (Attr) (node.getOwnerDocument().importNode(nodes.item(i),true));
                    e2.setAttributeNode(att);
                    }
                dest=e2;
                }
            else
                {
                dest= node.getOwnerDocument().importNode(node,false);
                }
            }
        else
            {
            dest= node.getOwnerDocument().importNode(node,false);
            }
        
        for(Node c= node.getFirstChild();c!=null; c=c.getNextSibling())
            {
            dest.appendChild(setPrefixNamespace(c,prefix,namespaceuri));
            }
        return dest;
        }
    

    

    	
    public static String escape(String s)
        {
        if(s==null) return s;
        StringBuffer buff= new StringBuffer();
        for(int i=0;i< s.length();++i)
            {
            switch(s.charAt(i))
	            {
	            case('\"') : buff.append("&quot;"); break;
	            case('\'') : buff.append("&apos;"); break;
	            case('&') : buff.append("&amp;"); break;
	            case('<') : buff.append("&lt;"); break;
	            case('>') : buff.append("&gt;"); break;
	            default: buff.append(s.charAt(i)); break;
	            }            
            }
        return buff.toString();
        }
    
    /**
     * write this as an XML document in a stream
     * @param out the stream
     * @param omitdecl should we omit the declaration
     */
    static public void print(Node node,Writer out,boolean omitdecl)
        {
        try {
            if(node==null) return;
            
             TransformerFactory tFactory = TransformerFactory.newInstance();
             Transformer   transformer = tFactory.newTransformer();
             transformer.setOutputProperty("omit-xml-declaration",(omitdecl?"yes":"no")); 
            DOMSource source = new DOMSource(node);
            StreamResult result = new StreamResult(out);
            transformer.transform(source, result);
        } catch (Exception e) {
        throw new RuntimeException("Cannot print Node",e);
        }     
           
        }
    
    static public boolean isA(Node e,String ns, String localname)
    	{
    	if(e==null) return false;
    	//if(e.getNodeType()!=Node.ELEMENT_NODE) return false;
    	return ns.equals(e.getNamespaceURI()) && e.getLocalName().equals(localname);
    	}
    
    
    public static boolean hasParent(Node e,String ns,String name)
		{
		if(e==null) return false;
		return isA(e.getParentNode(),ns,name);
		}
    
    
    public static boolean hasChildNS(Node parent,String ns,String name)
    	{
    	if(parent==null) return false;
    	for(Node c=parent.getFirstChild();c!=null;c=c.getNextSibling())
    		{
    		if(isA(c,ns,name)) return true;
    		}
    	return false;
    	}
    
    public static boolean hasDescendantNS(Node parent,String ns,String name)
    	{
    	if(parent==null) return false;
    	for(Node c=parent.getFirstChild();c!=null;c=c.getNextSibling())
    		{
    		if(isA(c,ns,name)) return true;
    		if(hasDescendantNS(c,ns,name)) return true;
    		}
    	return false;
    	}
    
    }

