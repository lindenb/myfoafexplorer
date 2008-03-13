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

import java.util.HashMap;
import java.util.Iterator;



/**
 * @author lindenb
 *
 * <code>PaperList</code>
 */
public class PaperList 
{
    HashMap datas;

    
    /**
     * Constructor for <code>PaperList</code>
     */
    public PaperList() {
        
        this.datas= new HashMap();
    }
    
    public Paper add(Paper paper)
    {
    	if(contains(paper)) return findPaperByPMID(paper.getPMID());
    	this.datas.put(paper.getPMID(),paper);
    	return paper;
    }

    public boolean isEmpty()
    {
        return this.datas.isEmpty();
    }
    
    public Paper[] toArray()
    {
        Object array[]= this.datas.values().toArray();
        if(array==null) return new Paper[0];
        Paper papers[]= new Paper[array.length];
        for(int i=0;i< array.length;++i)
        {
            papers[i]=(Paper)array[i];
        }
        return papers;
    }
    
    public boolean contains(Integer pmid)
    {
    	return this.datas.containsKey(pmid);
    }
    
    public boolean contains(Paper p)
    {
    	return contains(p.getPMID());
    }
    
    public Paper findPaperByPMID(Integer pmid)
    {
    	return (Paper)this.datas.get(pmid);
    }
    
    public PaperList getAuthorPublications(Author author)
    {
    	PaperList newlist= new PaperList();
    	for (Iterator iter = this.datas.keySet().iterator(); iter.hasNext();) {
			Paper paper = findPaperByPMID((Integer) iter.next());
			if(paper.authors.contains(author))
			{
				newlist.add(paper);
			}
			
		}
    	return newlist;
    }

    


}
