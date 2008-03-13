/*
 * Author: Pierre Lindenbaum PhD
 * Contact: plindenbaum@yahoo.fr
 * Created on 3:47:08 PM
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
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * @author lindenb
 *
 * <code>QPromptPMID</code>
 */
public class QPromptPMID extends Question {
    static public final String KEY="prompt_pmid";
    JButton     prompt_pmid_ok_button=null;
    JTextField  prompt_pmid_textfield=null;
    private static final Integer DEFAULT_PMID=new Integer(15047801); 
    
    /**
     * Constructor for <code>QPromptPMID</code>
     * @param sciFOAF
     */
    public QPromptPMID(SciFOAF sciFOAF) {
        super(sciFOAF);
    }
    
    /**
     * overides/implements parent
     * @see org.lindenb.scifoaf.Question#getKey()
     */
    public String getKey() { return KEY;}
    
    /**
     * overides/implements parent
     * @see org.lindenb.scifoaf.Question#create()
     */
    public JPanel create()
        {
        JPanel pane1 = new JPanel(new BorderLayout());
        pane1.setAlignmentX(Component.CENTER_ALIGNMENT);
        pane1.setAlignmentY(Component.TOP_ALIGNMENT);
        
        /* create title */
        pane1.add(createHeader(
        		null,
                foaf().getBundle("enterPMID")),
        	    BorderLayout.NORTH);
        
        
        JPanel pane= new JPanel(new FlowLayout());
        pane.add(new JLabel("Enter a Pubmed Article ID (PMID)"));
        
        this.prompt_pmid_textfield = new JTextField(25);
        this.prompt_pmid_textfield.addActionListener(new ActionListener()
                {
            public void actionPerformed(ActionEvent ae)
                {
                activate();
                }
            });
        pane.add(this.prompt_pmid_textfield);
        this.prompt_pmid_ok_button = createOKButton();
        this.prompt_pmid_ok_button.setToolTipText("look for PMID");
        this.prompt_pmid_ok_button.setBorderPainted(false);
        pane.add(this.prompt_pmid_ok_button);
        this.prompt_pmid_ok_button.addActionListener(new ActionListener()
                {
                public void actionPerformed(ActionEvent ae)
                    {
                    activate();
                    }
                });
        this.prompt_pmid_ok_button.setToolTipText(
                "Enter a Pubmed Article ID and press this button.");
        //pane1.add(Box.createVerticalGlue());
        pane1.add(pane,BorderLayout.CENTER);
        
        pane= new JPanel(new FlowLayout());
        pane.add(new JLabel(foaf().getBundle("aboutFoaf")));
        pane1.add(pane,BorderLayout.SOUTH);
        

        return pane1;
        }

    /**
     * overides/implements parent
     * @see org.lindenb.scifoaf.Question#update()
     */
    public void show()
        {
        Task topTask= foaf().taskList.getTopTask();
        if(foaf().authors.getAuthorCount()==0)
            {
            if(topTask.pmid==null) topTask.pmid= DEFAULT_PMID;
            this.prompt_pmid_textfield.setText(""+topTask.pmid);
            }
        else
            {
            this.prompt_pmid_textfield.setText("");
            }
        foaf().cardLayout.show(foaf().cardPanel,KEY);
        }

    /**
     * overides/implements parent
     * @see org.lindenb.scifoaf.Question#activate()
     */
    public void activate()
            {
            //Task top=foaf().taskList.getTopTask();
            //assert(top.getType().equals(KEY));
            Task task= new Task(foaf(),QAnalysePaper.KEY);
            String text=this.prompt_pmid_textfield.getText();
            if(text.trim().length()==0) return;
            try
                {
                task.pmid=new Integer(text);
                }
            catch(Exception err)
                {
                err.printStackTrace();
                JOptionPane.showMessageDialog(foaf(),
                        "Error",
                        err.toString(),
                        JOptionPane.WARNING_MESSAGE);
                return;
                }
            foaf().taskList.addTask(task);
            this.prompt_pmid_textfield.setText("");
            foaf().runTopTask();
            }

}
