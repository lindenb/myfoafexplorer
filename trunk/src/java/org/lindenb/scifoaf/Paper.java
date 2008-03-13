/*
 * Author: Pierre Lindenbaum PhD
 * Contact: plindenbaum@yahoo.fr
 * Created on 1:17:05 PM
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

import java.util.HashSet;
import java.util.Iterator;

import org.lindenb.lib.ncbi.pubmed.PubmedRecord;
import org.lindenb.lib.xml.XMLUtilities;
import org.w3c.dom.Element;



/**
 * @author lindenb
 *
 * <code>Paper</code>
 */
public class Paper
    {
    Integer pmid;
    HashSet authors;
    HashSet meshTerms;
    String title;
    
    public Paper(PubmedRecord rec)
    {
    	this.pmid = rec.getPMID();
        this.title= rec.getTitle();
    	this.authors= new HashSet();
        this.meshTerms= new HashSet();
        Element mesh[]= rec.getMeshDescriptorNameElements();
        for(int i=0;i< mesh.length;++i)
        {
        	this.meshTerms.add(new MeshTerm(XMLUtilities.textContent(mesh[i])));
        }
        
    }
    
    public boolean contains(Author author)
    {
    	return this.authors.contains(author);
    }

    public Author addAuthor(Author author)
    {
        if(contains(author))
        {
            for (Iterator iter = authors.iterator(); iter.hasNext();) {
                Author trueauthor = (Author) iter.next();
                if(trueauthor.equals(author)) return trueauthor;
            }
        }
    	this.authors.add(author);
        return author;
    }
    
   
    
    
    /**
     * @return Returns the pmid.
     */
    public Integer getPMID() {
        return pmid;
    }
    
    public String getTitle()
        {
        return this.title;
        }

    
    
    /**
     * overides/implements parent
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        return pmid.hashCode();
        }
    
    /**
     * overides/implements parent
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object obj) {
        if(obj==null) return false;
        if(!(obj instanceof Paper)) return false;
        return getPMID().equals(((Paper)obj).getPMID());
    }


}
