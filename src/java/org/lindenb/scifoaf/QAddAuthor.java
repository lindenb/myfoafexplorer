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
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;

import org.lindenb.lib.debug.Debug;
import org.lindenb.lib.ncbi.EUtilities;
import org.lindenb.lib.ncbi.pubmed.PubmedRecord;
import org.lindenb.lib.ncbi.pubmed.PubmedRecordSet;
import org.lindenb.lib.xml.XMLUtilities;




/**
 * @author lindenb
 *
 * <code>QAnalysePaper</code>
 */
public class QAddAuthor extends Question {
    static public final String KEY="add Author";
    static private final int ITEM_ADD_RELATIONSHIP=1;
    static private final int ITEM_ADD_PUBLICATION=2;
    //static private final int ITEM_ADD_LAB=3;
    
    
    static class MyCellRenderer extends JLabel
		implements ListCellRenderer
		{
        /**
         * serialVersionUID
         */
        private static final long serialVersionUID = 1L;
        public MyCellRenderer() {
            setOpaque(true);
        }
        public Component getListCellRendererComponent(
            JList list,
            Object value,
            int index,
            boolean isSelected,
            boolean cellHasFocus)
        	{
        	String s=value.toString();
        	if(s.length()>90) s=s.substring(0,90)+"...";
            setText(s);
            setToolTipText(value.toString());
            setBackground(isSelected ? Color.red : Color.white);
            setForeground(isSelected ? Color.white : Color.black);
            return this;
        	}
    }
    
    
    static private class Item
	{
    	int type;
    	Author author=null;
        Laboratory laboratory=null;
    	PubmedRecord pubMedRecord=null;
        Item laboratoryItem=null;
    	JCheckBox checkBox=null;
        JComboBox labComboBox=null;
        Object  checkBoxLinked[]=null;
	}
    
    JCheckBox   setAsMainAuthor;
    JButton     select_papers_ok_button;
    JPanel      my_papers_pane;
    Vector		items;
    JList       laboratories;
    JList       knowList;
    Form        form;
    
    
    /**
     * Constructor for <code>QAnalysePaper</code>
     * @param sciFOAF
     */
    public QAddAuthor(SciFOAF sciFOAF) {
        super(sciFOAF);
        this.setAsMainAuthor=null;
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
     * createUseAtMainAuthor
     * @param author
     * @return
     */
    private JPanel createUseAtMainAuthor(Author author)
        {
        JPanel pane= new JPanel(new FlowLayout());
        this.setAsMainAuthor= foaf().createCheckBox("Use "+author+" as main author.");
        if(foaf().mainAuthor==null)
            {
            this.setAsMainAuthor.setToolTipText("Use "+author+" as main author.");
            this.setAsMainAuthor.setSelected(true);
            }
        else
            {
            this.setAsMainAuthor.setToolTipText(""+foaf().mainAuthor+" already used as main Author.");
            this.setAsMainAuthor.setSelected(false);
            }
        pane.add(this.setAsMainAuthor);
        return pane;
        }

    private JPanel createAffiliedLaboratories(Author author,PubmedRecord recs[])
    {
        JPanel pane= new JPanel(new BorderLayout());
        pane.setBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createCompoundBorder(
                        BorderFactory.createEmptyBorder(10,10,10,10),
                        BorderFactory.createLineBorder(Color.BLACK)
                        ),
                        "Laboratories"));
       
        /* already knows panel */
        LaboratoryList hislabs = foaf().laboratories.getMemberIs(author);
        

        
        if(!hislabs.isEmpty())
        {
            //Debug.trace(hislabs);
        	JPanel pane3= new JPanel(new BorderLayout());
        	pane3.add(new JLabel("Belong to the following "+ hislabs.getLabCount()+" lab(s)."),BorderLayout.NORTH);
        	JPanel pane2= new JPanel(new GridLayout(0,2));
        	pane3.add(pane2,BorderLayout.CENTER);
        	for(int i=0;i<hislabs.getLabCount();++i)
        	    {
        		pane2.add(new JLabel(hislabs.getLabAt(i).toHTML()));
                }
        	pane.add(pane3,BorderLayout.NORTH);
        }
        
        
        DefaultListModel model=new DefaultListModel();
        //Debug.trace();
        for(int i=0;i< foaf().laboratories.getLabCount();++i)
            {
            if(!foaf().laboratories.getLabAt(i).contains(author))
                {
                model.addElement(foaf().laboratories.getLabAt(i));
                }
            }
        //Debug.trace();
        for(int i=0;recs!=null && i< recs.length;++i)
            {
            String address=  Laboratory.getPaperAffiliation(recs[i]);
            if(address==null) continue;
            Laboratory lab= new Laboratory(address);
            if(model.contains(lab)) continue;
            model.addElement(lab);
            }
        
        
        this.laboratories= new JList(model);
        this.laboratories.setCellRenderer(new MyCellRenderer());
        this.laboratories.getSelectionModel().setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        JPanel top= new JPanel(new BorderLayout());
        

        
        if(model.isEmpty())
            {
            pane.add(top,BorderLayout.CENTER);
            top.add(new JLabel("No Laboratoy found to affiliate to "+author),BorderLayout.CENTER); 
            }
        else
            {
        	top.add(new JLabel("Select one ore more (using Ctrl) laboratories who is affilated to "+author.getName()),BorderLayout.CENTER);

            
            JPanel flow= new JPanel(new FlowLayout());
            JScrollPane scroll = new JScrollPane(
            		this.laboratories,
            		JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
					JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS	
					);
            Dimension dim = foaf().getSize();
            dim.width*=0.75;
            dim.height*=0.1;
            scroll.setPreferredSize(dim);
            scroll.setMaximumSize(dim);
            flow.add(scroll);
            pane.add(flow,BorderLayout.CENTER);
            }
        
        return pane;
        }
    
    /**
     * createKnowsPanel
     * @param author
     * @return
     */
    private JPanel createKnowsPanel(Author author)
    {
        this.knowList=null;
        JPanel pane= new JPanel(new BorderLayout());
        pane.setBorder(
                BorderFactory.createTitledBorder(
                                BorderFactory.createCompoundBorder(
                                BorderFactory.createEmptyBorder(10,10,10,10),
                                BorderFactory.createLineBorder(Color.BLACK)
                                )
                        ,"Knows"));
       
        /* already knows panel */
        AuthorList know = foaf().whoknowwho.getKnown(author);
        if(!know.isEmpty())
            {
        	JPanel pane3= new JPanel(new BorderLayout());
        	pane3.add(new JLabel("Knows the following "+ know.getAuthorCount()+" person(s)."),BorderLayout.NORTH);
        	JPanel pane2= new JPanel(new GridLayout(0,6));
        	pane3.add(pane2,BorderLayout.CENTER);
        	for(int i=0;i<know.getAuthorCount();++i)
        	    {
        		pane2.add(new JLabel(know.getAuthorAt(i).getName()));
                }
        	pane.add(pane3,BorderLayout.NORTH);
            }
        
        
        DefaultListModel model=new DefaultListModel();
        
        AuthorList complement= foaf().authors.complement(know);
        complement.sort();
        for(int i=0;i< complement.getAuthorCount();++i)
            {
            if(complement.equals(author)) continue;
            if(know.contains(complement.getAuthorAt(i))) continue;
            model.addElement(complement.getAuthorAt(i));   
            }
        this.knowList = new JList(model);
        this.knowList.setCellRenderer(new MyCellRenderer());
        this.knowList.getSelectionModel().setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        
        JPanel top= new JPanel(new BorderLayout());
        
        
        if(model.isEmpty())
            {
            pane.add(top,BorderLayout.CENTER);
            top.add(new JLabel("No person found to affiliate to "+author),BorderLayout.CENTER); 
            }
        else
            {
        	top.add(new JLabel("Select one ore more (using Ctrl) persons knowing "+author.getName()),BorderLayout.NORTH);
            JPanel listpane= new JPanel();
            listpane.setLayout(new BoxLayout(listpane,BoxLayout.PAGE_AXIS));
            listpane.add(top);
            JPanel flow= new JPanel(new FlowLayout());
            JScrollPane scroll=new JScrollPane(this.knowList,
            		JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
					JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS		
            	);
            Dimension dim = foaf().getSize();
            
            dim.width*=0.75;
            dim.height*=0.1;
            scroll.setPreferredSize(dim);
            scroll.setMaximumSize(dim);
            flow.add(scroll);
            listpane.add(flow);
            pane.add(listpane,BorderLayout.CENTER);
            }
        return pane;
    }
    
    public  JPanel createTitlePane(Author author)
    {
    	JPanel pane0= new JPanel(new BorderLayout());
    	pane0.add(new JLabel(foaf().getAuthorIcon()),BorderLayout.WEST);
    	pane0.add(new JLabel("<html><body><h1 align=\"center\">"+XMLUtilities.escape(author.getName())+"</h1></body></html>"),BorderLayout.CENTER);
    	pane0.setBorder(BorderFactory.createLineBorder(Color.BLUE));
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
        
        Author author= topTask.author;
        PaperList publications= foaf().papers.getAuthorPublications(author);
        AuthorList hisRelations = foaf().whoknowwho.getKnown(author);
        //Debug.trace("n relation:"+hisRelations.size());
        
        JPanel pane0= new JPanel();
        pane0.setLayout(new BoxLayout(pane0,BoxLayout.PAGE_AXIS));
        pane0.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        pane0.add(createTitlePane(author));
        
        this.form= new Form();

        form.add("foaf:nick","NickName (multiple are space separated)",null);
        form.add("foaf:mbox","E-Mail (multiple are space separated)",null);
        form.add("foaf:homepage","WWW homepage (multiple are space separated)",null);
        form.add("foaf:img","URL to a picture  (multiple are space separated)",null);
        
        pane0.add(form.realise("Informations"));
        
        form.setText("foaf:mbox","mailto:");
        form.setText("foaf:homepage","http://");
        form.setText("foaf:img","http://");
       
        

        
        pane0.add(createUseAtMainAuthor(author));

        
        

       
            
            JPanel pane3= new JPanel();
            pane3.setLayout(new BoxLayout(pane3, BoxLayout.PAGE_AXIS));
           
            
            PubmedRecord recs[]= null;
            
            try
            	{
                foaf().setInformation("Fetching Paper for ="+author);
                EUtilities eutilities= new EUtilities(EUtilities.PUBMED);
                eutilities.setReturnCount(Integer.parseInt((String)(foaf().getFetchingNCBIProperties().getProperty("retmax"))));
                PubmedRecordSet set=new PubmedRecordSet(eutilities.searchAndFetch(
						author.getTerm()));
                
                recs=new PubmedRecord[set.getSize()];
                for(int i=0;i< recs.length;++i)
                	{ 	
	                recs[i]=set.getRecordAt(i);
                	}
            }
            catch(Exception err)
            {

                pane0.add(new JLabel("Cannot fetch article written by "+author+" "+err.toString()));
                recs=null;
                Debug.trace();
            }

            
        pane0.add(createAffiliedLaboratories(author,recs));
        pane0.add(createKnowsPanel(author));     
        pane0.add(pane3);  
            
           
        for(int i=0;recs!=null && i< recs.length;++i)
            {
        	if(recs[i]==null) continue;
        	JPanel paperPane= new JPanel();
        	paperPane.setLayout(new BoxLayout(paperPane, BoxLayout.PAGE_AXIS));
        	paperPane.setBorder( BorderFactory.createTitledBorder(
                    BorderFactory.createCompoundBorder(
                    BorderFactory.createEmptyBorder(10,10,10,10),
        			BorderFactory.createLineBorder(Color.BLUE)),
					"PMID:"+recs[i].getPMID()
        			));
            pane3.add(paperPane);
            
            paperPane.add(foaf().article2panel(recs[i],this));
            
            
            /*****************************************************
             * 
             * Laboratory
             * 
             */
            String affiliation=Laboratory.getPaperAffiliation(recs[i]);
            Item laboratoryItem=null;
            if(affiliation!=null)
            {
                JPanel labPanel= new JPanel(new BorderLayout());
                labPanel.add(new JLabel(foaf().getIcon("erlen.gif")),BorderLayout.WEST);
                labPanel.add(new JLabel(new Laboratory(affiliation).toHTML()),BorderLayout.CENTER);
                paperPane.add(labPanel);
               
                }
            
            Item paperCheckBox = null;
            
        	if(publications.contains(recs[i].getPMID()))
             	{
                paperCheckBox=null;
             	paperPane.add(new JLabel("Already know that he/she wrote this paper"));
             	}
            else
            	{
                paperCheckBox= new Item();
                paperCheckBox.type= ITEM_ADD_PUBLICATION;
                paperCheckBox.author=null;
                paperCheckBox.pubMedRecord= recs[i];
                paperCheckBox.laboratory=null;
                if(affiliation!=null)
                    {
                    paperCheckBox.laboratory=new Laboratory(affiliation);
                    }
                paperCheckBox.checkBox=foaf().createCheckBox(author.getName()+" wrote this paper");
            	items.addElement(paperCheckBox);
                paperPane.add(paperCheckBox.checkBox);
                
                paperCheckBox.checkBox.addActionListener(new ActionListener()
                        {
                        public void actionPerformed(ActionEvent e)
                            {
                            for(int i=0;i< items.size();++i)
                                {
                                Item item= (Item)items.elementAt(i);
                                if(item.type!=ITEM_ADD_PUBLICATION) continue;
                                if(item.checkBox!=e.getSource()) continue;
                                if(item.checkBoxLinked==null) continue;//pas d'autres auteurs
                                for(int j=0;j< item.checkBoxLinked.length;++j)
                                    {
                                    JCheckBox cb=(JCheckBox)item.checkBoxLinked[j];
                                    cb.setEnabled(item.checkBox.isSelected());
                                    }
                                }
                            }
                        });
            	}
        	
           JPanel pane2= new JPanel(new GridLayout(0,2));
           pane2.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
           paperPane.add(pane2);   
           
            AuthorList authorList= new AuthorList(recs[i]);
            for(int j=0;j< authorList.getAuthorCount();++j)
                {
            	JPanel pane4= new JPanel(new FlowLayout(FlowLayout.LEFT));
            	Author other=authorList.getAuthorAt(j);
            	if(other.equals(author)) continue;
                
            	if(hisRelations.contains(other))
            		{
            		pane4.add(new JLabel(author+" knows "+other.getName()));
            		}
            	else
            		{
            		Item item= new Item();
                	item.type= ITEM_ADD_RELATIONSHIP;
                	item.author=other;
                	item.pubMedRecord =  recs[i];
                    
                    
                    Author homonyme=null;
                    
                    for(int t=0;t<foaf().authors.getAuthorCount();++t)
                        {
                        if(foaf().authors.getAuthorAt(t).isHomonyme(item.author))
                            {
                            homonyme= foaf().authors.getAuthorAt(t);
                            break;
                            }
                        }
                    
                    for(int t=0;homonyme==null && t< foaf().taskList.getTaskCount();++t)
                        {
                        if(!foaf().taskList.getTaskAt(t).tasktype.equals(QAddAuthor.KEY)) continue;
                        if(foaf().taskList.getTaskAt(t).author.isHomonyme(item.author))
                            {
                            homonyme= foaf().taskList.getTaskAt(t).author;
                            break;
                            }
                        }
                    
                    
                    
                    
                    
                    
                	item.checkBox=foaf().createCheckBox(
                            homonyme== null ?
                            "Knows "+other.getName()   
                            :
                            "<html><body>Knows "+other.getName()+"<br><i>May be same as "+homonyme.getName()+"</i></body></html>");
                    item.checkBox.setHorizontalAlignment(JCheckBox.CENTER);
                    item.checkBox.setToolTipText("check this item if "+author.getName()+" knows "+other.getName());
                	item.laboratoryItem = laboratoryItem;
                    items.addElement(item);
            		pane4.add(item.checkBox);
                    
                    if(paperCheckBox!=null)
                        {
                        if(paperCheckBox.checkBoxLinked==null)
                            {
                            paperCheckBox.checkBoxLinked= new Object[0];
                            }
                        item.checkBox.setEnabled(false);
                        Object array[]=new Object[ paperCheckBox.checkBoxLinked.length+1];
                        System.arraycopy(paperCheckBox.checkBoxLinked,0,array,0,paperCheckBox.checkBoxLinked.length);
                        array[paperCheckBox.checkBoxLinked.length]=item.checkBox;
                        paperCheckBox.checkBoxLinked= array;
                        }
                    
            		}
            	pane2.add(pane4);
                }
            
            }
        /* 
         * OK Button
         */
        this.select_papers_ok_button=  createOKButton();
        this.select_papers_ok_button.setBorderPainted(false);
        this.select_papers_ok_button.setText("<html><body>Fill-in and select the information about this author<br>"+
                "and then validate it by pressing this button</body></html>");
        
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
    	}
    
    
    
    /**
     * overides/implements parent
     * @see org.lindenb.scifoaf.Question#activate()
     */
    public void activate() {
    	 
    	Task me= foaf().taskList.getTopTask();
    	foaf().taskList.removeTask(me);
    	Author author=me.author;
        foaf().authors.add(me.author);
        
        if(this.setAsMainAuthor.isSelected())
            {
            foaf().mainAuthor= author;
            }
        
        
        /*
         * properties
         */
        String property=this.form.getText("foaf:nick");
        author.setProperty("foaf:nick",property);
        property=this.form.getText("foaf:mbox");
        author.setProperty("foaf:mbox",property);
        property=this.form.getText("foaf:homepage");
        author.setProperty("foaf:homepage",property);
        property=this.form.getText("foaf:img");
        author.setProperty("foaf:img",property);
        
        /*
         * laboratories
         */
        if(this.laboratories!=null)
        	{
	       Object selectedlabs[]=this.laboratories.getSelectedValues();
	       for(int i=0;selectedlabs!=null && i< selectedlabs.length;++i)
		       	{
                Laboratory lab= foaf().laboratories.add(((Laboratory)selectedlabs[i]));
                lab.add(author);
		       	}
        	}
       
       /*
        * knows
        */
        if(this.knowList!=null)
        {
	       Object selectedKnown[]=this.knowList.getSelectedValues();
	       for(int i=0;selectedKnown!=null && i< selectedKnown.length;++i)
	       {
	       foaf().whoknowwho.add(new  RelationShip((Author)selectedKnown[i],author));
	       }
        }
        
        for (Iterator iter=  this.items.iterator(); iter.hasNext();)
            {
            Item item = (Item) iter.next(); 

        	switch(item.type)
    			{
        		case ITEM_ADD_PUBLICATION:
        			{
        			if(!item.checkBox.isSelected()) break;
        			Paper paper= foaf().papers.add(new Paper(item.pubMedRecord));
        			paper.addAuthor(author);
                    item.laboratoryItem=item;
                    item.author=author;
                    //addLab(item);
        			break;
        			}
            	case ITEM_ADD_RELATIONSHIP:
                    {
                    if(!item.checkBox.isSelected()) break;
                    foaf().setInformation("Should I Add Author "+item.author);
                    
                    foaf().whoknowwho.add(new RelationShip(author,item.author));
                    Paper paper= foaf().papers.add(new Paper(item.pubMedRecord));
                    paper.addAuthor(item.author);
                    
                    //addLab(item);
                   
                    /* check if the author is already in the author list */
                    boolean found=foaf().authors.contains(item.author);
                    /* check if there is already a task to insert the new author */
                    for(int j=0;
                        found==false &&  j<foaf().taskList.getTaskCount();
                        ++j)
                        {
                        if(!foaf().taskList.getTaskAt(j).tasktype.equals(QAddAuthor.KEY)) continue;
                        if(!foaf().taskList.getTaskAt(j).author.equals(item.author)) continue;
                        found=true;
                        break;
                        }
                   
                    
                    if(!found)
                        {
                        foaf().setInformation("Adding task \"new Author\":"+item.author);
                        Task newtask= new Task(foaf(),QAddAuthor.KEY);
                        newtask.author= item.author;
                        foaf().taskList.addTask(newtask);
                        }
                    }
                break;
    			}
                
            }
        
        foaf().runTopTask();
    }
    /*
    private void addLab(Item item)
    {
        if(item.laboratoryItem!=null)
        {
        if(item.laboratoryItem.labComboBox!=null)
            {
            item.laboratoryItem.laboratory=(Laboratory)item.laboratoryItem.labComboBox.getSelectedItem();
            }

        if(item.laboratoryItem.laboratory!=null)
            {
            foaf().setInformation("add lab "+ item.laboratoryItem.laboratory);
            Laboratory lab= foaf().laboratories.add(item.laboratoryItem.laboratory);
            if(!lab.contains(item.author))
                {
                lab.add(item.author);
                }
            }
        }  
    } */

    /**
     * overides/implements parent
     * @see org.lindenb.scifoaf.Question#getKey()
     */
    public String getKey() {
        return KEY;
    }

}
