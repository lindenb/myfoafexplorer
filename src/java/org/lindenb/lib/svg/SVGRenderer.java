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
package org.lindenb.lib.svg;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

import org.lindenb.lib.awt.ColorUtils;
import org.w3c.dom.*;

/**
 * @author pierre
 *
 * SVGRenderer
 */
public class SVGRenderer
{
private Graphics2D g2d;
private Node root;
private boolean namespaceAware;
private Rectangle2D viewRect;
public SVGRenderer()
	{
	this.namespaceAware=false;
	}

public void setNamespaceAware(boolean namespaceAware)
	{
	this.namespaceAware = namespaceAware;
	}



public boolean isNamespaceAware()
	{
	return namespaceAware;
	}



public Rectangle2D getViewRect()
	{
	if(this.viewRect==null) return null; 
	return (Rectangle2D)this.viewRect.clone();
	}

public void setViewRect(Rectangle2D viewRect)
	{
	this.viewRect = viewRect;
	}

public Graphics2D getGraphics()
	{
	return this.g2d;
	}

public  void setGraphics(Graphics2D g)
	{
	this.g2d=g;
	}

public void setNode(Node node)
	{
	this.root=node;
	}

public Node getNode()
	{
	return this.root;
	}

private String getLocalName(Element e)
	{
	String s= e.getLocalName();
	if(s!=null) return s;
	return e.getNodeName();
	}

private Attr getAttributeNodeNS(Element e,String name)
	{
	if(e==null) return null;
	if(isNamespaceAware())
		{
		Attr att= e.getAttributeNodeNS(SVGUtils.SVGNS,name);
		if(att==null) att=  e.getAttributeNodeNS(null,name);
		return att;
		}
	else
		{
		return e.getAttributeNode(name);
		}
	}

public Dimension getSize()
	{
	if(this.root==null) return null;
	Element e=null;
	if(this.root.getNodeType()==Node.DOCUMENT_NODE)
		{
		e=( ((Document)this.root).getDocumentElement());
		}
	else if(this.root.getNodeType()==Node.ELEMENT_NODE)
		{
		e=((Element)this.root);
		}
	if(e==null) return null;
	Attr width= getAttributeNodeNS(e,"width");
	Attr height= getAttributeNodeNS(e,"height");
	if(width==null || height==null) return null;
	try {
		return new Dimension(
			Integer.parseInt(width.getValue()),
			Integer.parseInt(height.getValue())
			);
		}
	catch (Exception err)
		{
		return null;
		}
	}

public void paint()
	{
	if(this.root==null) return;
	Graphics2D g=getGraphics();
	if(g==null) return;
	
	Rectangle2D view= getViewRect();
	AffineTransform originalTr=null;
	if(view!=null)
		{
		Dimension dimSVG=getSize();
		if(dimSVG!=null && dimSVG.getWidth()>0 && dimSVG.getHeight()>0)
			{
			originalTr= g.getTransform();
			
			double ratio= Math.min(
					view.getWidth()/dimSVG.getWidth(),
					view.getHeight()/dimSVG.getHeight()
				);
			
			
			g.translate(
					(view.getWidth() -dimSVG.getWidth()*ratio)/2.0,
					(view.getHeight()-dimSVG.getHeight()*ratio)/2.0
					);
			g.scale(ratio,ratio);
			}
		}
	
	if(this.root.getNodeType()==Node.DOCUMENT_NODE)
		{
		paint( ((Document)this.root).getDocumentElement());
		}
	else if(this.root.getNodeType()==Node.ELEMENT_NODE)
		{
		paint((Element)this.root);
		}
	
	if(originalTr!=null)
		{
		g.setTransform(originalTr);
		}
	
	}

private void paint(Element e)
	{
	if(e==null) return;
	
	if(isNamespaceAware() && !SVGUtils.SVGNS.equals(e.getNamespaceURI())) return;
	Graphics2D g=getGraphics();
		
	Color strokeColor = findColor(e,"stroke");
	Color fillColor = findColor(e,"fill");
	warning(""+e.getLocalName()+" fill="+fillColor+" "+getAttributeNodeNS(e,"fill"));
	Shape shape=null;
	String shapeName= getLocalName(e);
	
	if(shapeName==null)
		{
		warning("shapeName is null");
		}
	else if(shapeName.equals("path"))
		{
		Attr d= getAttributeNodeNS(e,"d");
		if(d!=null)
			{
			shape = SVGUtils.Path2Shape(d.getValue());
			}
		}
	else if(shapeName.equals("rect"))
		{
		
		Attr x= getAttributeNodeNS(e,"x");
		Attr y= getAttributeNodeNS(e,"y");
		Attr w= getAttributeNodeNS(e,"width");
		Attr h= getAttributeNodeNS(e,"height");
		if(x!=null && y!=null && w!=null && h!=null)
			{
			shape =new Rectangle2D.Double(
				Double.parseDouble(x.getValue()),
				Double.parseDouble(y.getValue()),	
				Double.parseDouble(w.getValue()),	
				Double.parseDouble(h.getValue())
				);
			}
		}
	else if(shapeName.equals("line"))
		{
		
		Attr x1= getAttributeNodeNS(e,"x1");
		Attr y1= getAttributeNodeNS(e,"y1");
		Attr x2= getAttributeNodeNS(e,"x2");
		Attr y2= getAttributeNodeNS(e,"y2");
		if(x1!=null && y1!=null && x2!=null && y2!=null)
			{
			shape =new Line2D.Double(
				Double.parseDouble(x1.getValue()),
				Double.parseDouble(y1.getValue()),	
				Double.parseDouble(x2.getValue()),	
				Double.parseDouble(y2.getValue())
				);
			}
		}
	else
		{
		warning("cannot display <"+e.getLocalName()+">");
		}
	
	if(shape!=null)
		{
		warning(strokeColor);
		warning(fillColor);
		if(fillColor!=null)
			{
			
			g.setColor(fillColor);
			g.fill(shape);
			}
		if(strokeColor!=null)
			{
			
			g.setColor(strokeColor);
			g.draw(shape);
			}
		}
	else
		{
		warning("No shape with <"+e.getLocalName()+">"+fillColor+" "+strokeColor);
		}
		
	for(Node c=e.getFirstChild();c!=null;c=c.getNextSibling())
		{
		if(c.getNodeType()!=Node.ELEMENT_NODE) continue;
		paint((Element)c);
		}
	
	}

private Color findColor(Element e,String name)
	{
	String value = findStyle(e,name,"none");
	//System.err.print(""+value+" "+ColorUtils.parseColor(value));
	if(value!=null && value.equals("none")) return null;
	return ColorUtils.parseColor(value);
	}

	private String findStyle(Element e,String name,String defaultValue)
		{
		String v= findStyle(e,name);
		if(v==null) return defaultValue;
		return v;
		}

	private String findStyle(Element e,String name)
	{
		
	Attr att = getAttributeNodeNS(e,"style");
	if(att!=null)
		{
		
		String tokens[]=att.getValue().split("[;]");
		for(int i=0;i< tokens.length;++i)
			{
			String tok=tokens[i].trim();
			
			int loc= tok.indexOf(':');
			if(loc<=0 || loc+1==tok.length() ) continue;
			
			if(tok.substring(0,loc).trim().equals(name))
				{
				
				return tok.substring(loc+1).trim();
				}
			}
		}
	
	att = getAttributeNodeNS(e,name);

	if(att!=null) return att.getValue();
	
	if( e.getParentNode()!=null &&
		e.getParentNode().getNodeType()==Node.ELEMENT_NODE)
		{
		return findStyle((Element)e.getParentNode(),name);
		}
	return null;
	}
	
public void warning(Object o)
	{
	
	//System.err.println("#"+o);
	}

}

