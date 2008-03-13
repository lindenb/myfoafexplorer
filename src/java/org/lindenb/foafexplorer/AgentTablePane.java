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

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import org.lindenb.lib.swing.RendererAdapter;

/**
 * @author lindenb
 *
 */
public class AgentTablePane extends JPanel
	{
	private static final long serialVersionUID = 1L;
	protected JTable table;
	
	public AgentTablePane(AgentTableModel model)
		{
		super(new BorderLayout());
		this.table= new JTable();
		this.table.setDefaultRenderer(Object.class, new RendererAdapter());
		JScrollPane scroll= new JScrollPane(this.table);
		add(scroll,BorderLayout.CENTER);
		setModel(model);
		}
	
	void setModel(AgentTableModel model)
		{
		this.table.setModel(model);
		}
	
	public AgentTableModel getModel()
		{
		return (AgentTableModel)this.table.getModel();
		}
	
	public JTable getTable()
		{
		return this.table;
		}
	
	}
