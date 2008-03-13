/**
 * 
 */
package org.lindenb.lib.svg;


import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.io.File;


import javax.swing.JFrame;

import javax.swing.JPanel;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * @author lindenb
 *
 */
public class SVGPanel extends JPanel
{
/**
 * serialVersionUID
 */
private static final long serialVersionUID = 1L;
private SVGRenderer renderer;
private Dimension dim;

public SVGPanel()
	{
	this(null);
	}

public SVGPanel(Node svg)
	{
	super(false);
	this.renderer = new SVGRenderer();
	this.dim=null;
	setSVG(svg);
	}

protected SVGRenderer getSVGRenderer()
	{
	return this.renderer;
	}

public void setSVG(Node svg)
	{
	Node old= getSVGRenderer().getNode();
	if(old==svg) return;
	
	 getSVGRenderer().setNode(svg);
	this.dim=  getSVGRenderer().getSize();
	firePropertyChange("SVG",old,svg);
	
	if(this.dim!=null)
		{
		setPreferredSize(this.dim);
		}
	else
		{
		setPreferredSize(new Dimension(5,5));
		}
	//repaint();
	}

public Node getSVG()
	{
	return this.renderer.getNode();
	}

protected void paintComponent(Graphics g1)
	{
	super.paintComponent(g1);
	if(getSVG()==null || this.dim==null) return;
	if(this.dim.width==0 || this.dim.height==0) return;
	Insets insets= getInsets();
	Dimension size= getSize();
	size.width-= (insets.left+insets.right);
	size.height-= (insets.top+insets.bottom);
	
	if(size.width<=0 || size.height<=0) return;
	
	 getSVGRenderer().setViewRect(new Rectangle2D.Double(
			insets.left,
			insets.top,
			size.getWidth()-(insets.left+insets.right),
			size.getHeight()-(insets.top+insets.bottom)
		));
	
	
	 getSVGRenderer().setGraphics((Graphics2D)g1);
	 getSVGRenderer().paint();
	

	}


public static void main(String[] args)
{
try
	{
	DocumentBuilderFactory f= DocumentBuilderFactory.newInstance();
	f.setNamespaceAware(true);
	f.setValidating(false);
	DocumentBuilder b= f.newDocumentBuilder();
	Document doc=b.parse(new File("/env/islande/home/lindenb/jeter.svg"));
	JFrame frame= new JFrame("test");
	frame.getContentPane().add(new SVGPanel(doc));
	frame.setBounds(50,50,100,100);
	frame.pack();
	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	frame.setVisible(true);
}
catch(Exception err)
{
	err.printStackTrace();
}
}


}
