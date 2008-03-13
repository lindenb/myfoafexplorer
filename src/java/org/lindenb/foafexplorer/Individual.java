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

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.io.IOException;


import org.lindenb.lib.rdf.RDFUtils;
import org.lindenb.lib.xml.Namespaces;
import org.w3c.dom.Element;

/**
 * @author lindenb
 *
 */
public class Individual extends AbstractPeople
	{
	static final int FATHER=0;
	static final int MOTHER=1;
	//static private BufferedImage ALPHAIMAGE= new BufferedImage(Image.SIZE,Image.SIZE,BufferedImage.TYPE_INT_RGB);
	
	
	/** location on screen */
	private Point2D.Double location;
	/** location we wish on screen */
	private Point2D.Double goal=null;
	/** array of parents */
	private Individual parents[];
	/** visible on screen */
	private boolean visible;;
	
	
	private Individual(Model model, String rdfID)
		{
		super(model,rdfID);
		this.parents= new Individual[]{null,null};
		this.location= null;
		this.goal= null;//cf.setLocation
		this.visible=true;
		}
	
	public int getType()
		{
		return T_INDIVDIDUAL;
		}
	
	public Point2D getLocation()
		{
		if(location==null) return null;
		return new Point2D.Double(location.getX(),this.location.getY());
		}
	

	public synchronized void swapGoal(Individual i)
		{
		Point2D.Double p=this.goal;
		this.goal=i.goal;
		i.goal=p;
		}

	
	public void setLocation(double x, double y)
		{
		if(this.location==null) this.location=new Point2D.Double(0,0);
		this.location.x=x;
		this.location.y=y;
		if(this.goal==null) this.goal=new Point2D.Double(x,y);
		}

	public Individual getParent(int index)
		{
		return parents[index];
		}
	
	/**
	 * @return Returns the visible.
	 */
	public boolean isVisible()
		{
		return visible;
		}
	
	/**
	 * @param visible The visible to set.
	 */
	public void setVisible(boolean visible)
		{
		this.visible = visible;
		}
	
	
	public void addRelationShip(Relationship rel)
		{
		if(rel==null || hasRelationShip(rel)) return;
		super.addRelationShip(rel);
		
		if(rel.getURI().equals(Namespaces.REL+"childOf"))
			{
			if(this.parents[0]==null)
				{
				this.parents[0]= (Individual)rel.getAgent(1);
				}
			else if(this.parents[1]==null)
				{
				this.parents[1]= (Individual)rel.getAgent(1);
				}
			else
				{
				throw new IllegalArgumentException("Already 2 parents with "+getName());
				}
			}
		}

	
	public double getX()
		{
		return this.location.x;
		}
	
	public double getY()
		{
		return this.location.y;
		}
	
	public double getGoalX()
		{
		return this.goal.x;
		}
	
	public double getGoalY()
		{
		return this.goal.y;
		}
	
	boolean isMoving()
		{
		return this.goal.x!=this.location.x || this.goal.y!=this.location.y;
		}
	
	public Rectangle getBounds()
		{
		return new Rectangle(
				(int)(getX()-Image.SIZE/2),
				(int)(getY()-Image.SIZE/2),
				Image.SIZE,
				Image.SIZE);
		}
	
	public boolean contains(int x,int y)
		{
		return getBounds().contains(x,y);
		}
	
	protected String[] getSearchableStrings()
		{
		return new String[]{getDescription(),getID(),getName()};
		}
	
	public void drawFrame(GC gc,int pix)
		{
		Graphics2D g=gc.g();
		g.drawRect(
			(int)(getX()-Image.SIZE/2-pix),
			(int)(getY()-Image.SIZE/2-pix),
			-Image.SIZE+(pix*2),-Image.SIZE+(pix*2)
			);
		}
	
	public void paint(GC gc)
		{
		
		Graphics2D g=gc.g();
		if(gc.step==0)
			{
			if(isVisible())
				{
				for(int i=15;i>=0;--i)
					{
					int gray=255-(int)(((15-i)/15.0)*(30));
					g.setColor(new Color(gray,gray,gray));
					g.fillRect(
							(int)(15+getX()-Image.SIZE/2+i),
							(int)(15+getY()-Image.SIZE/2+i),
							Image.SIZE,
							Image.SIZE);
					}
				
			
				g.setColor(Color.GREEN);
				for(int i=0;gc.paintRelationships==true &&
					i< getRelationCount();++i)
					{
					Relationship rel= getRelationShipAt(i);
					if(rel.getAgent(1).getType()!=Agent.T_INDIVDIDUAL) continue;
					
					Individual other=(Individual)rel.getAgent(1);
					if(!other.isVisible()) continue;
					g.drawLine((int)getX(),(int)getY(),(int)other.getX(),(int)other.getY());
					}
			
				}
			
			
			int bottom =(int) getY();
	
			
			Individual father= getParent(FATHER);
			if(isVisible()==false || father!=null && father.isVisible()==false) father=null;
			Individual mother= getParent(MOTHER);
			if(isVisible()==false || mother!=null && mother.isVisible()==false) mother=null;
			
			g.setColor(Color.BLUE);
	        if(father!=null && mother!=null)
	            {
	            int top =  (int)(father.getY()+mother.getY())/2;
	            int y1= top + 1*(bottom-top)/3;
	            int y2= top + 2*(bottom-top)/3;
	
	            g.drawLine(
	                    (int)father.getX(),(int)father.getY(),
	                    (int)father.getX(),y1
	                    );
	            g.drawLine(
	            		(int)mother.getX(),(int)mother.getY(),
	            		(int) mother.getX(),y1
	                    );
	            g.drawLine(
	            		(int)father.getX(),y1,
	            		(int)mother.getX(),y1
	                    );
	            int mid= (int)((father.getX()+mother.getX())/2);
	            g.drawLine(
	                    mid,y1,
	                    mid,y2
	                    );
	
	            g.drawLine(
	                    mid,y2,
	                    (int) this.getX(),y2
	                    );
	
	            g.drawLine(
	            		(int)this.getX(),
						(int)this.getY(),
	            		(int)this.getX(),y2
	                    );
	
	            }
	        else if(!(father==null && mother==null))
	            {
	            Individual p= (father==null?mother:father);
	            g.drawLine(
	            		(int)p.getX(),(int)p.getY(),
	            		(int)this.getX(),(int)this.getY()
	                    );
	            }
			}
		else
			{
			Composite current= null;
			if(!isVisible())
				{
				current= g.getComposite();
				g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.4f));
				}
			
			Image img=getImage();
			if(img!=null)
				{
				img.paint(gc,(int)getX()-Image.SIZE/2,(int)getY()-Image.SIZE/2);
				}
			else
				{
				g.setColor(Color.LIGHT_GRAY);
				g.fillRect((int)getX()-Image.SIZE/2,(int)getY()-Image.SIZE/2,Image.SIZE,Image.SIZE);
				}
			g.setColor(Color.BLACK);
			g.drawRect((int)getX()-Image.SIZE/2,(int)getY()-Image.SIZE/2,Image.SIZE,Image.SIZE);
			
			if(!isVisible())
				{
				g.setComposite(current);
				}
			
			
			if(isVisible())
				{
				String name= getName();
				int w = gc.fm.stringWidth(name) + 10;
				int h = gc.fm.getHeight() + 4;
				g.setColor(Color.YELLOW);
				g.fillRect((int)getX() - w/2, (int)getY()+Image.SIZE/2 - h / 2, w, h);
				g.setColor(Color.black);
				g.drawRect((int)getX() - w/2, (int)getY()+Image.SIZE/2 - h / 2, w-1, h-1);
				g.drawString(name,
						(int)getX() - (w-10)/2, (int)(getY()+Image.SIZE/2 - (h-4)/2) + gc.fm.getAscent());
				}
			}
		}
	
	
	/** parse individual from DOM */
	static Individual parse(Element node,Model model) throws IOException
		{
		String rdfID= RDFUtils.getProperty(node,Namespaces.RDF,"ID");
		Individual individual= new Individual(model,rdfID);
		individual.parse_(node);
		
		

		return individual;
		}
	}
