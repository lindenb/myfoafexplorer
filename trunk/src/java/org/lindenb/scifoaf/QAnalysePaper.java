/*
 * Author: Pierre Lindenbaum PhD
 * Contact: plindenbaum@yahoo.fr
 * Created on 4:18:55 PM
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
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.lindenb.lib.ncbi.EUtilities;
import org.lindenb.lib.ncbi.pubmed.PubmedRecord;
import org.lindenb.lib.ncbi.pubmed.PubmedRecordSet;
import org.lindenb.lib.xml.XMLUtilities;


/**
 * @author lindenb
 *
 * <code>QAnalysePaper</code>
 */
public class QAnalysePaper extends Question {
    static public final String KEY="analyse paper";
    static private final int  ADD_AUTHOR=1;
    //static private final int  ADD_LAB =2;
    
    static private class Item
	{
        int type;
    	Author author;
        String labName;
    	JCheckBox checkBox;
	}

    
    JButton     select_papers_ok_button;
    JPanel      my_papers_pane;
    Vector		items;
    PubmedRecord pubMedRecord;
    /**
     * Constructor for <code>QAnalysePaper</code>
     * @param sciFOAF
     */
    public QAnalysePaper(SciFOAF sciFOAF) {
        super(sciFOAF);
        this.pubMedRecord=null;
        this.items= new Vector();
    }

    /**
     * overides/implements parent
     * @see org.lindenb.scifoaf.Question#create()
     */
    public JPanel create() {
        JPanel pane0= new JPanel(new BorderLayout()) ;
        this.my_papers_pane= new JPanel(new BorderLayout());
        pane0.add( this.my_papers_pane, BorderLayout.CENTER);
        
        return pane0;
    }

    /**
     * overides/implements parent
     * @see org.lindenb.scifoaf.Question#show()
     */
    public void show()
    	{
        this.my_papers_pane.removeAll();
        this.items.clear();
        Task topTask= foaf().taskList.getTopTask();
        
        Integer pmid= topTask.pmid;
        JPanel pane0= new JPanel();
        pane0.setLayout(new BoxLayout(pane0,BoxLayout.PAGE_AXIS));
        pane0.add(createHeader(
        		"foaf.png",
				"<html><body><h1>Process Pubmed Article PMID "+
                pmid+"</h1></body></h1>"));
      

        try
        	{
            foaf().setInformation("Fetching Paper PMID="+pmid);
            PubmedRecordSet set= new PubmedRecordSet(new EUtilities(EUtilities.PUBMED).fetch(pmid.intValue()));
            if(set.getSize()==0) throw new IOException("error no article was found");
            this.pubMedRecord = set.getRecordAt(0);
            /*this.pubMedRecord = PubmedRecordS.fetch(
            		pmid.intValue(),
					foaf().getFetchingNCBIProperties()
            		);*/
        	}
        catch(Exception err)
			{
			err.printStackTrace();
            pane0.add(new JLabel(
                    "<html><body>Cannot fetch article PMID "+pmid+
                    " <pre>"+XMLUtilities.escape(err.toString())+
                    "</pre></body></html>"));
            this.pubMedRecord = null;
			}

        if(this.pubMedRecord!=null)
        {
        JPanel pane3= new JPanel();
        pane3.setBorder(BorderFactory.createLineBorder(Color.BLUE));
        pane3.setLayout(new BoxLayout(pane3, BoxLayout.PAGE_AXIS));
        pane0.add(pane3);
        
        	{
            JPanel pane2= new JPanel(new BorderLayout());
            pane2.add(foaf().article2panel(this.pubMedRecord,this),BorderLayout.CENTER);
            pane3.add(pane2);
            
            /*****************************************************
             * 
             * Laboratory
             * 
             */
            String affiliation=Laboratory.getPaperAffiliation(this.pubMedRecord);
            
            if(affiliation!=null)
            {
                JPanel labPanel= new JPanel(new BorderLayout());
                labPanel.add(new JLabel(foaf().getIcon("erlen.gif")),BorderLayout.WEST);
                pane3.add(labPanel);
                labPanel.add(new JLabel(new Laboratory(affiliation).toHTML()),BorderLayout.CENTER);
                }
                
            
            
            
            
            
            AuthorList authors= new AuthorList(this.pubMedRecord);
            JPanel authorspane= new JPanel(new GridLayout(0,3));
            pane3.add(authorspane);
            for(int i=0;i< authors.getAuthorCount();++i)
                {
            	Item item= new Item();
                item.type= ADD_AUTHOR;
            	item.author = authors.getAuthorAt(i);
 
            	
            	//check wether we need to add this author
            	boolean found=false;
            	for(int j=0;j<items.size();++j)
            		{
            		if(((Item)items.elementAt(j)).type== ADD_AUTHOR &&
                       ((Item)items.elementAt(j)).author.equals(item.author))
                		{
                		found=true;
                        authorspane.add(new JLabel(item.author.getName()+" already selected"));
                		break;
                		}
            		}
            	if(found) continue;
            	Author homonyme=null;
            	for(int j=0;j<foaf().taskList.getTaskCount();++j)
            	    {
            		if(!foaf().taskList.getTaskAt(j).tasktype.equals(QAddAuthor.KEY)) continue;
            		if(!foaf().taskList.getTaskAt(j).author.equals(item.author))
                        {
                        if(foaf().taskList.getTaskAt(j).author.isHomonyme(item.author))
                            {
                            homonyme= foaf().taskList.getTaskAt(j).author;
                            }
                        continue;
                        }
                    authorspane.add(new JLabel(item.author.getName()+" already selected in task List"));
            		found=true;
            		break;
                    }
            	if(found) continue;
            	
            	if(foaf().authors.contains(item.author))
            		{
                    authorspane.add(new JLabel(item.author.getName()+" already inserted in the Author List"));
            		continue;
            		}
            	
                item.checkBox = foaf().createCheckBox("Add "+item.author.getName() +(homonyme!=null?" (has homonyme ?:"+homonyme.getName()+")":""));
                item.checkBox.setHorizontalAlignment(JCheckBox.CENTER);
                item.checkBox.setToolTipText("Process the bibliography of this individual as a new Person in the FOAF file");
                items.addElement(item);
                
                authorspane.add(item.checkBox);
                }
            }
           }
        /*
         * OK Button
         */
        this.select_papers_ok_button= createOKButton();
        this.select_papers_ok_button.setText("Continue");
        this.select_papers_ok_button.setBorderPainted(false);
        this.select_papers_ok_button.addActionListener(new ActionListener()
                {
                public void actionPerformed(ActionEvent ae)
                    {
                    activate();
                    }
                });
        pane0.add( this.select_papers_ok_button);
        
        this.my_papers_pane.add(new JScrollPane(pane0));
        this.my_papers_pane.validate();
        foaf().cardLayout.show(foaf().cardPanel,KEY);
        super.show();
    }
    
    
    
    /**
     * overides/implements parent
     * @see org.lindenb.scifoaf.Question#activate()
     */
    public void activate() {
    	 Task me= foaf().taskList.getTopTask();
         foaf().taskList.removeTask(me);
         
         
         for (Iterator iter=  this.items.iterator(); iter.hasNext();)
             {
         	Item item = (Item) iter.next(); 
             switch(item.type)
                 {
                 case ADD_AUTHOR:
                     {
                     if(item.checkBox.isSelected())
                         {
                         foaf().setInformation("Add Author "+item.author);
                         Task newtask= new Task(foaf(),QAddAuthor.KEY);
                         newtask.author= item.author;
                         Paper paper= foaf().papers.add(new Paper(this.pubMedRecord));
                         item.author=paper.addAuthor(item.author);
                         
                         foaf().taskList.addTask(newtask);
                         }
                     break;
                     }
                 }
             }
         
         foaf().runTopTask();

    }

    /**
     * overides/implements parent
     * @see org.lindenb.scifoaf.Question#getKey()
     */
    public String getKey() {
        return KEY;
    }

}
