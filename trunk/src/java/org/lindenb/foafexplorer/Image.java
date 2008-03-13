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

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.imageio.ImageIO;

import org.lindenb.lib.rdf.RDFUtils;
import org.lindenb.lib.xml.Namespaces;
import org.w3c.dom.Element;

/**
 * @author lindenb
 *
 */
public class Image extends Agent
	{
	public static  final int SIZE=64;
	private URI uri= null;
	private BufferedImage image;
	private boolean failure=false;
	
	public Image(Model model, String rdfID) throws URISyntaxException
		{
		super(model,rdfID);
		this.image=null;
		this.failure=false;
		if(rdfID!=null)
			{
			this.uri=new URI(rdfID);
			}
		//force load image now
		getImage();
		}
	
	public URI getURI()
		{
		return this.uri;
		}
	
	public BufferedImage getImage()
		{
		if(this.failure) return null;
		if(this.image!=null) return image;
		
		try
			{
			BufferedImage img=null;

			try
			{
			if(uri.isAbsolute())
				{
				img= ImageIO.read(getURI().toURL());
				}
			else
				{
				String s="/"+getURI().toString();
				img= ImageIO.read(Image.class.getResourceAsStream(s));
				}
			}
			catch (Exception e)
				{
				img= ImageIO.read(new File(getURI().toASCIIString()));
				}
			
			if(img==null)
				{
				System.err.println("ImageIO returned null");
				this.failure=true;
				return this.image;
				}
			if(img.getWidth()==SIZE && img.getHeight()==SIZE)
				{
				this.image= img;
				return this.image;
				}
			
			int x0=0;
			int y0=0;
			int len=0;
			if(img.getWidth()< img.getHeight())
				{
				x0=0;
				len= img.getWidth();
				y0= (img.getHeight()-len)/2;
				}
			else
				{
				y0=0;
				len= img.getHeight();
				x0= (img.getWidth()-len)/2;
				}
			this.image= new BufferedImage(SIZE,SIZE,BufferedImage.TYPE_INT_RGB);
			Graphics2D g= this.image.createGraphics();
			g.setColor(Color.WHITE);
			g.fillRect(0,0,SIZE,SIZE);
			g.drawImage(img,
					0,0,SIZE,SIZE,
					x0,y0,
					len,
					len,null);
			g.setColor(Color.WHITE);
			g.drawRect(0,0,SIZE-1,SIZE-1);
			
			g.dispose();
			return this.image;
			} catch (Exception e)
			{
			System.err.println("cannot get url="+getURI());
			//e.printStackTrace();
			this.failure=true;
			this.image=null;
			}
		return this.image;
		}
	
	public void paint(GC gc,int x, int y)
		{
		if(this.failure==true) return;
		Graphics2D g=gc.g();
		g.drawImage(
				getImage(),x,y,
				getImage().getWidth(gc.observer),
				getImage().getHeight(gc.observer),
				gc.observer
				);
		}
	
	public int getType()
		{
		return T_IMAGE;
		}
	
	static Image parse(Element node,Model model) throws IOException
		{
		String url= RDFUtils.getProperty(node,Namespaces.RDF,"resource");
		Image img;
		try {
			img = new Image(model,url);
			}
		catch (URISyntaxException e)
			{
			e.printStackTrace();
			throw new IOException(e.getMessage());
			}
		img.description = RDFUtils.getProperty(node,Namespaces.DC,"description");
		
		return img;
		}
	}
