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
 * Created on 2:11:45 PM
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
package org.lindenb.lib.swing;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.geom.AffineTransform;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.ListCellRenderer;
import javax.swing.plaf.metal.MetalLookAndFeel;
import javax.swing.table.TableCellRenderer;

public class RendererAdapter
extends JLabel
implements ListCellRenderer,TableCellRenderer
	{
	
	

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final int col1 = 224;
	private static final int col2 = 204;
	private Object value;
	private Color selectionColor= new Color(col2,col2,col1);
	private Color rowColors[]= {new Color(col1,col1,col2),new Color(col1,col2,col2)};
	private boolean autoSizeText=false;
	
	public RendererAdapter()
		{
		super();
		this.value=null;
		setOpaque(true);
		setBackground(MetalLookAndFeel.getWhite());
		setBorder(BorderFactory.createLineBorder(selectionColor));
		Font f=super.getFont();
		setFont(new Font(f.getFamily(),Font.PLAIN,f.getSize()));
		}
	
	/**
	 * @param autoSizeText The autoSizeText to set.
	 */
	public void setAutoSizeText(boolean autoSizeText)
		{
		this.autoSizeText = autoSizeText;
		}
	
	/**
	 * @return Returns the autoSizeText.
	 */
	public boolean isAutoSizeText()
		{
		return autoSizeText;
		}
	
	public Object getValue()
		{
		return this.value;
		}
	
	public void setValue(Object value)
		{
		this.value = value;
		if(value==null)
			{
			setText("");
			setHorizontalTextPosition(TRAILING);
			}
		else
			{
			String s=value.toString();
			setText(s);
			if(value instanceof Number)
				{
				setHorizontalTextPosition(LEADING);
				}
			else
				{
				setHorizontalTextPosition(TRAILING);
				}
			}
		}
	
	/* (non-Javadoc)
	 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
	 */
	protected void paintComponent(Graphics g1d)
		{
		Graphics2D g=(Graphics2D)g1d;
		if(!isAutoSizeText())
			{
			super.paintComponent(g1d);
			}
		else
			{
			Insets insets= getInsets();
			g.setColor(getBackground());
			g.fillRect(0,0,getWidth(),getHeight());
			int x= insets.left;
			if(getIcon()!=null)
				{
				getIcon().paintIcon(this,g,insets.left,insets.top);
				x+=getIcon().getIconWidth()+ getIconTextGap();
				}
			String s= getText();
			if(s==null || s.length()==0) return;
			
			double w= (getWidth()-insets.right+5)-x;
			double h= getHeight()-(insets.top+insets.bottom+5);
			if(w<=0 || h<=0) return;
			
			g.setColor(getForeground());
			FontMetrics fm= g.getFontMetrics(getFont());
			double ws = fm.stringWidth(s) ;
			double hs = fm.getHeight();
			double ratio= Math.min(w/ws,h/hs);
			
			int newheight= (int)(hs*ratio);
			//int newwidth= (int)(ws*ratio);
			
			AffineTransform tr= g.getTransform();
			g.translate(x,(getHeight()-newheight)/2);
			g.scale(ratio,ratio);
			g.drawString(s,0,(int)hs);
			g.setTransform(tr);
 			}
		if(getValue()==null)
			{
			g.setColor(MetalLookAndFeel.getBlack());
			g.drawLine(0,0,getWidth(),getHeight());
			}
			
		//int x= getIconTextGap()+(getIcon()==null?0:getIcon().getIconWidth());
		//g.drawLine(x,0,getWidth(),getHeight());
		}
	
	/* (non-Javadoc)
	 * @see javax.swing.ListCellRenderer#getListCellRendererComponent(javax.swing.JList, java.lang.Object, int, boolean, boolean)
	 */
	public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus)
		{
		setValue(value);
		setBackground(isSelected?selectionColor:index%2==0? rowColors[0]: rowColors[1]);
		return this;
		}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableCellRenderer#getTableCellRendererComponent(javax.swing.JTable, java.lang.Object, boolean, boolean, int, int)
	 */
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
		{
		setValue(value);
		setBackground(isSelected?selectionColor:row%2==0? rowColors[0]: rowColors[1]);
		return this;
		}

}
