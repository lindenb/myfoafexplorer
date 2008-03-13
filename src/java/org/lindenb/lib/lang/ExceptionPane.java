/*
 * Author: Pierre Lindenbaum PhD
 * Contact: lindenb@integragen.com
 * Created on 12:22:13 PM
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
package org.lindenb.lib.lang;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;


import javax.swing.BorderFactory;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.table.AbstractTableModel;




public class ExceptionPane extends JPanel
    {
    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 1L;
    private Throwable throwable;
    private AbstractTableModel tableModel;
    
    public ExceptionPane(Throwable throwable)
    	{
        super(new BorderLayout());
        Dimension screen= Toolkit.getDefaultToolkit().getScreenSize();
        setPreferredSize(new Dimension(screen.width/2,screen.height/2));
        setBorder(BorderFactory.createLineBorder(Color.RED,2));
        this.throwable= throwable;

        this.tableModel = new AbstractTableModel()
			{
			private static final long serialVersionUID = 1L;


			public int getColumnCount() {
                 return 4;
            }

            public String getColumnName(int columnIndex) {
                switch(columnIndex)
                {
                case 0: return "Class";
                case 1: return "Method";
                case 2: return "File";
                case 3: return "Line";
                }
                return null;
            }

            public int getRowCount()
                {
                return ExceptionPane.this.throwable.getStackTrace().length;
                }


            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return false;
            }


            public Class getColumnClass(int columnIndex) {
                switch(columnIndex)
                {
                case 0:case 1:case 2: return String.class;
                case 3: return Integer.class;
                }
                return null;
            }


            public Object getValueAt(int rowIndex, int columnIndex) {
                StackTraceElement e= ExceptionPane.this.throwable.getStackTrace()[rowIndex];
                switch(columnIndex)
                {
                case 0: return e.getClassName();
                case 1: return e.getMethodName();
                case 2: return e.getFileName();
                case 3: return new Integer(e.getLineNumber());
                }
                return null;
            }
			};
        JPanel pane= new JPanel(new GridLayout(0,1));
        this.add(pane,BorderLayout.CENTER);
        JTextArea msg= new JTextArea(throwable.getClass().getName()+
                "\n"+throwable.getMessage());
        msg.setEditable(false);
        msg.setBackground(Color.WHITE);
        msg.setLineWrap(true);
        JScrollPane scroll=new JScrollPane(msg);
        scroll.setBorder(BorderFactory.createTitledBorder("An exception of type "+
                throwable.getClass().getName() +
                " has occured"));
        scroll.setPreferredSize(new Dimension(200,200));
        pane.add(scroll);
        
        JTable table = new JTable(this.tableModel);
        
        table.setBackground(Color.WHITE);
        scroll=new JScrollPane(table);
        scroll.setBorder(BorderFactory.createTitledBorder("Stack Trace"));
        pane.add(scroll);
        setBackground(Color.RED);
        pane.setOpaque(true);
        pane.setBackground(Color.RED);
        }

    /** display an alert via OptionPane.showMessageDialog */
    static public void show(Throwable t)
    	{
    	JOptionPane.showMessageDialog(null,new ExceptionPane(t),"Error",JOptionPane.ERROR_MESSAGE,null);
    	}
    
    
}
