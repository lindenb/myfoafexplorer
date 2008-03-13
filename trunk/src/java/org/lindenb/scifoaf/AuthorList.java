/*
 * Author: Pierre Lindenbaum PhD
 * Contact: plindenbaum@yahoo.fr
 * Created on 2:40:53 PM
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

import java.util.Collections;
import java.util.Vector;


import org.lindenb.lib.ncbi.pubmed.PubmedRecord;
import org.lindenb.lib.xml.XMLUtilities;
import org.w3c.dom.Element;
import org.w3c.dom.Node;



/**
 * @author lindenb
 *
 * <code>PaperList</code>
 */
public class AuthorList extends Vector
	{
    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 1L;

    /**
     * Constructor for <code>AuthorList</code>
     */
    public AuthorList()
    	{
        super(0,5);
    	}
    
    public AuthorList( PubmedRecord rec)
    	{
    	this();
    	if(rec==null) return;
    	Element e= rec.getElementNode();
        if(e==null) return;
        
        if(e.getNodeName().equals("PubmedArticleSet"))
            {
            e= XMLUtilities.findFirstChildElement(e,"PubmedArticle");
            }
        e= XMLUtilities.findFirstChildElement(e,"MedlineCitation");
        e= XMLUtilities.findFirstChildElement(e,"Article");
        e= XMLUtilities.findFirstChildElement(e,"AuthorList");
        if(e==null) return ;
        

	    for(Node c= e.getFirstChild();c!=null;c=c.getNextSibling())
	        {
	        if(c.getNodeType()!=Node.ELEMENT_NODE) continue;
	        if(c.getNodeName().equals("Author"))
	            {
	        	Author au= new Author((Element)c);
	        	if(au.getCollectiveName()!=null) continue;
	        	add(au);
	            }
	        }
    	}
    
    
    public int getAuthorCount() { return super.size();}
    public Author getAuthorAt(int i) { return (Author)super.elementAt(i);}
    
    public void add(Author author)
    {
        if(contains(author))
        {
            //Debug.traceStack("Houla !!!"+author);
        }
        else
        {
    	super.add(author);
        }
    }
    

    public boolean contains(Author author)
    {
    	return super.contains(author);
    }
    
    /**
     * return all elements of this object whose
     * authors are not in list
     * @param list
     * @return
     */
    public AuthorList complement(AuthorList list)
    {
        AuthorList complementList = new AuthorList();
        for(int i=0;i< getAuthorCount();++i)
        {
            if( !list.contains(getAuthorAt(i)))
            {
                complementList.add(getAuthorAt(i));
            }
        }
        return complementList;
    }

    public void sort()
        {
        Collections.sort(this);
        }
}
