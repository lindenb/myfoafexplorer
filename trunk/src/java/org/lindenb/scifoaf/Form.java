/*
 * Author: Pierre Lindenbaum PhD
 * Contact: plindenbaum@yahoo.fr
 * Created on 1:17:23 PM
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
package org.lindenb.scifoaf;

import java.awt.BorderLayout;
import java.awt.Color;
import java.util.HashMap;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * @author lindenb
 *
 * <code>Form</code>
 */
public class Form {
    static class Item
        {
        String label;
        Pattern pattern;
        JTextField textField;
        }
    
    private JPanel panel;
    public Vector items;
    public HashMap hash;

public Form()
{
    this.panel=null;
    this.items=new Vector();
    this.hash= new HashMap();
}
    
public void add(
        String id,
        String label,
        String pattern
        )
    {
    Item item= new Item();
    
    item.label= label;
    item.pattern=null;
    if(pattern!=null)
        {
        item.pattern=Pattern.compile(pattern);
        }
    items.addElement(item);
    this.hash.put(id,item);
    }

public JPanel realise(String name)
    {
    if(this.panel!=null) return this.panel;
    this.panel= new JPanel(new BorderLayout());
    
    this.panel.setBorder(
            BorderFactory.createTitledBorder(
                    BorderFactory.createCompoundBorder(
                            BorderFactory.createEmptyBorder(10,10,10,10),
                            BorderFactory.createLineBorder(Color.BLACK)
                            ),
                    name));
    
    JPanel pane1 = new JPanel();
    pane1.setLayout(new BoxLayout(pane1,BoxLayout.PAGE_AXIS));
    
    for(int i=0;i< items.size();++i)
        {
        Item item=(Item)items.elementAt(i);
        pane1.add(new JLabel(item.label));
        item.textField= new JTextField("");
        pane1.add(item.textField);
        }
    this.panel.add(pane1,BorderLayout.CENTER);
    
    return this.panel;
    }

public String getText(String id)
    {
    Item item=(Item)this.hash.get(id);
    if(item==null) return null;
    return item.textField.getText();
    }

public void setText(String id,String text)
    {
    Item item=(Item)this.hash.get(id);
    if(item==null) return;
    if(item.textField==null) return;
    item.textField.setBackground(Color.WHITE);
    item.textField.setText(text);
    }

public String validate()
    {
    //boolean ok=true;
    StringBuffer msg=new StringBuffer();
    for(int i=0;i< items.size();++i)
        {
        Item item=(Item)items.elementAt(i);
        String text= item.textField.getText().trim();
        if(item.pattern!=null)
            {
            Matcher match= item.pattern.matcher(text);
            if(!match.matches())
                {
                item.textField.setBackground(Color.RED);
                msg.append(" "+text+" doesnt match");
                //ok=false;
                }
            else
                {
                item.textField.setBackground(Color.WHITE);
                }
            }
        }
    return (msg.length()==0?null:msg.toString());
    }

}
