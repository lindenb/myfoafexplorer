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
package org.lindenb.lib.gis;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.Point2D;
import java.io.InputStream;

import javax.swing.JFrame;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.lindenb.lib.svg.SVGPanel;
import org.w3c.dom.Document;


/**
 * @author lindenb
 *
 */
public class WorldMapPanel extends SVGPanel
	{
	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;
	static private final String FILENAME="worldmap.svg";
	static private Document  WORLDMAP=null;
	static private Dimension WORLDIM=null;
	
	private boolean enabled=true;
	private boolean mouseInMap=false;
	/**
	 * WorldMapPanel
	 */
	public WorldMapPanel()
		{
		super();
		
		if(WORLDMAP==null)
			{
			try
				{
				DocumentBuilderFactory factory= DocumentBuilderFactory.newInstance();
				factory.setCoalescing(true);
				factory.setValidating(false);
				factory.setNamespaceAware(false);
				factory.setIgnoringComments(true);
				factory.setIgnoringElementContentWhitespace(true);
				DocumentBuilder builder= factory.newDocumentBuilder();
				InputStream in=WorldMapPanel.class.getResourceAsStream(FILENAME);
				WORLDMAP=builder.parse(in);
				in.close();
				setSVG(WORLDMAP);
				WORLDIM = getSVGRenderer().getSize();
				}
			catch(Exception err)
				{
				WORLDMAP=null;
				err.printStackTrace();
				}
			}
		
		addMouseListener(new MouseAdapter()
				{
				public void mouseEntered(MouseEvent e)
					{
					if(!isEnabled()) return;
					Rectangle r=getSVGRect();
					if(r==null) return;
					mouseInMap=r.contains(e.getPoint());
					setCursor(Cursor.getPredefinedCursor(mouseInMap?Cursor.CROSSHAIR_CURSOR:Cursor.DEFAULT_CURSOR));
					}
				
				public void mouseExited(MouseEvent e)
					{
					if(!isEnabled()) return;
					mouseInMap=false;
					setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
					}
				
				});
		
		addMouseMotionListener(new MouseMotionAdapter()
				{
				
				public void mouseMoved(MouseEvent e)
					{
					if(!isEnabled()) return;
					Rectangle r=getSVGRect();
					if(r==null) return;
					boolean inmap=r.contains(e.getPoint());
					if(inmap!=mouseInMap)
						{
						mouseInMap=inmap;
						setCursor(Cursor.getPredefinedCursor(mouseInMap?Cursor.CROSSHAIR_CURSOR:Cursor.DEFAULT_CURSOR));
						}

					}
				});
		}

	public boolean isEnabled()
		{
		return enabled;
		}
	
	public void setEnabled(boolean enabled)
		{
		if(this.enabled != enabled)
			{
			if(enabled==false) setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			this.enabled = enabled;
			firePropertyChange("enabled",!this.enabled,enabled);
			repaint();
			}
		}

	/* (non-Javadoc)
	 * @see org.lindenb.lib.svg.SVGPanel#paintComponent(java.awt.Graphics)
	 */
	protected void paintComponent(Graphics g1)
		{
		super.paintComponent(g1);
		if(!isEnabled())
			{
			int h=getHeight();
			int w=getWidth();
			g1.setColor(Color.GRAY);
			for(int y=0;y< h;y+=2)
				{
				g1.drawLine(0,y,w,y);
				}
			}
		}
	
	public Point2D screen2location(Point mouse)
		{
		Rectangle rect= getSVGRect();
		if(rect==null) return null;
		if(rect.contains(mouse)==false) return null;
		double lon= (double)(mouse.getX()-(rect.x+rect.width/2))/((double)rect.width)*360.0;
		double lat= (double)(rect.y+rect.height/2-mouse.getY())/((double)rect.height)*180.0;
		return new Point2D.Double(lon,lat);
		}
	
	
	
	public Point location2screen(Point2D location)
		{
		if(location==null) return null;
		Rectangle r= getSVGRect();
		if(r==null) return null;
		return new Point(
			r.x+r.width/2  + ((int)((location.getX()/360.0)*r.getWidth())),
			r.y+r.height/2 - ((int)((location.getY()/180.0)*r.getHeight()))
			);
		}
	
	private Rectangle getSVGRect()
		{
		if(WORLDMAP==null) return null;
		Insets insets= getInsets();
		Rectangle view= new Rectangle(
				insets.left,
				insets.bottom,
				getWidth()-(insets.left+insets.right),
				getHeight()-(insets.top+insets.bottom)
				);
		
		double ratio= Math.min(
				view.getWidth()/WORLDIM.getWidth(),
				view.getHeight()/WORLDIM.getHeight()
			);
		return new Rectangle(
				(int)((view.getWidth() -WORLDIM.getWidth()*ratio)/2.0),
				(int)((view.getHeight()-WORLDIM.getHeight()*ratio)/2.0),
				(int)(WORLDIM.getWidth()*ratio),
				(int)(WORLDIM.getHeight()*ratio)
				);
				
		}
	

	public static void main(String[] args)
	{
	try
		{
		JFrame frame= new JFrame("test");
		WorldMapPanel w=new WorldMapPanel();
		//w.setEnabled(false);
		frame.getContentPane().add(w);
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
