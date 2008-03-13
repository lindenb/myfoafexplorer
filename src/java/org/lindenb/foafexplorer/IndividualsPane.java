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


import java.awt.Component;


import javax.swing.JTable;

import org.lindenb.lib.swing.RendererAdapter;

/**
 * @author lindenb
 *
 */
public class IndividualsPane extends AgentTablePane
	{
	private static final long serialVersionUID = 1L;

	public IndividualsPane(IndividualTableModel indis)
		{
		super(indis);
		super.table.setRowHeight(Image.SIZE);
		super.table.setDefaultRenderer(Object.class,new RendererAdapter()
				{
				private static final long serialVersionUID = 1L;

				/* (non-Javadoc)
				 * @see org.lindenb.lib.swing.RendererAdapter#getTableCellRendererComponent(javax.swing.JTable, java.lang.Object, boolean, boolean, int, int)
				 */
				public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
					{
					Component c= super.getTableCellRendererComponent(table,value,isSelected,hasFocus,row,column);
					
					int i= table.convertColumnIndexToModel(column);
					if(i==0)
						{
						Individual indi= (Individual)getModel().getAgentAt(row);
						this.setIcon(indi.getIcon());
						}
					else
						{
						this.setIcon(null);
						}
					return c;
					}
				});
		}
	
	
	
	}
