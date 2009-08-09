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
 * Contact: lindenb@integragen.com
 * Created on 10:00:32 AM
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

import java.io.File;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;


import org.w3c.dom.*;

/**
 * @author lindenb
 *
 * <code>XPathAPI</code>
 */
public class XPathAPI {
    

    
static public Element[] selectElements(Node node,String xpathExpression)
    {
    if(xpathExpression.startsWith("/"))
        {
        while(node.getParentNode()!=null)
            {
            node=node.getParentNode();
            }
        if(node.getNodeType()==Node.DOCUMENT_NODE)
            {
            node=((Document)node).getDocumentElement();
            }
        xpathExpression=xpathExpression.substring(1);
        }
    Vector nodes= new Vector();
    selectElements(node,xpathExpression,nodes,null);
    Element array[]= new Element[nodes.size()];
    for(int i=0;i< nodes.size();++i) array[i]=(Element)nodes.elementAt(i);
    return array;
    }

static private void selectElements(
        Node node,
        String xpathExpression,
        Vector nodes,
        int minmax[]
        )
    {

    int index= xpathExpression.indexOf('/');
    if(index==-1)
        {
        if( node.getNodeType()==Node.ELEMENT_NODE &&
            (node.getNodeName().equals(xpathExpression) ||
             xpathExpression.equals("*")
             ))
            {
            if(minmax!=null)
                {
                int position=0;
                if(position< minmax[0] || position> minmax[1]) return;
                }
            nodes.addElement((Element)node);
            }
        }
    else
        {
        String left = xpathExpression.substring(0,index);
        
        if(left.equals("*")==false &&
           node.getNodeName().equals(left)==false) return;
        
        String right = xpathExpression.substring(index+1);
        
        for(Node c= node.getFirstChild();c!=null;c=c.getNextSibling())
            {
            if(c.getNodeType()!=Node.ELEMENT_NODE) continue;
            selectElements(c,right,nodes,null);
            }
        
        }
    }


public static void main(String[] args) {
    if(args.length!=2) return;
    try {
        DocumentBuilderFactory factory= DocumentBuilderFactory.newInstance();
        DocumentBuilder builder= factory.newDocumentBuilder();
        System.out.println("Parsing "+args[0]);
        Document doc= builder.parse(new File(args[0]));
        System.out.println("Eval "+args[1]);
        Element element[] = XPathAPI.selectElements(doc.getDocumentElement(),args[1]);
        for(int i=0;i< element.length;++i)
        {
            Node e= element[i];
            while(e!=null)
                {
                System.out.print(e.getNodeName());
                e=e.getParentNode();
                if(e==null) break;
                System.out.print( " <- ");
                }
            System.out.println("");
        }
    } catch (Exception e) {
        e.printStackTrace();

    }
}

}
