/*
 * Author: Pierre Lindenbaum PhD
 * Contact: plindenbaum@yahoo.fr
 * Created on 4:29:34 PM
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


import java.net.URLEncoder;

/**
 * @author lindenb
 *
 * <code>MeshTerm</code>
 */
public class MeshTerm {
    private String term;
    
    public MeshTerm(String term)
    {
        this.term=term.trim();
    }
    
    public boolean equals(Object obj) {
        if(obj==null) return false;
        if(obj==this) return true;
        if(!(obj instanceof MeshTerm)) return false;
        return term.equals(((MeshTerm)obj).term);
    }
    
    /**
     * @return getURL
     */
    public String getURL()
    {
    	String s=null;
    	 try
		 {
    	 	s= URLEncoder.encode(this.term,"UTF-8");
		 }
    	 catch(Exception e)
		 {
    	 	return null;
		 }
    	
    return "http://www.nlm.nih.gov/cgi/mesh/2005/MB_cgi?mode=&amp;term="+
			s+ "&amp;field=entry";
    }
    

    
    
    public int hashCode() {
        return term.hashCode();
    }
    
    public String toString()
    {
        return term;
    }
    
}
