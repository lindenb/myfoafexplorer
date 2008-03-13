/*
 * Author: Pierre Lindenbaum PhD
 * Contact: plindenbaum@yahoo.fr
 * Created on 2:48:10 PM
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
import java.text.Collator;
import java.util.Properties;


import org.lindenb.lib.xml.XMLUtilities;
import org.w3c.dom.Element;



/**
 * @author lindenb
 *
 * <code>Author</code>
 */
public class Author implements Comparable
    {
    static private Collator collator= null;
	private String lastName,foreName,initials,suffix,collectiveName;
	private Properties properties;
    
    
    /**
     * Constructor for <code>Author</code>
     */
    public Author(Element e)
        {
        if(collator==null)
            {
            collator = Collator.getInstance();
            collator.setStrength(Collator.PRIMARY); 
            }
        lastName= nullIfEmpty(XMLUtilities.textContent(XMLUtilities.findFirstChildElement(e,"LastName")));
        foreName= nullIfEmpty(XMLUtilities.textContent(XMLUtilities.findFirstChildElement(e,"ForeName")));
        initials= nullIfEmpty(XMLUtilities.textContent(XMLUtilities.findFirstChildElement(e,"Initials")));
        suffix= nullIfEmpty(XMLUtilities.textContent(XMLUtilities.findFirstChildElement(e,"Suffix")));
        collectiveName= nullIfEmpty(XMLUtilities.textContent(XMLUtilities.findFirstChildElement(e,"CollectiveName")));
        properties= new Properties();

        
        if(this.initials==null && this.foreName!=null)
            {
            //Debug.trace("ah oui !");
            this.initials= ""+this.foreName.toUpperCase().charAt(0);
            }
        if(this.initials!=null && this.foreName==null)
            {
            //Debug.trace("ah oui !");
            this.foreName= ""+this.initials;
            }
        
      
        
        if(collectiveName!=null)
            {
            collectiveName=collectiveName.trim();
            if(collectiveName.length()==0) collectiveName=null;
            this.foreName=collectiveName;
            this.lastName=collectiveName;
            }

        }
    
    
    
    /**
     * @return Returns the foreName.
     */
    public String getForeName() {
        return foreName;
    }

    /**
     * @return Returns the collectiveName.
     */
    public String getCollectiveName() {
        return collectiveName;
    }

    /**
     * @return Returns the initials.
     */
    public String getInitials() {
        return initials;
    }



    /**
     * @return Returns the lastName.
     */
    public String getLastName() {
        return lastName;
    }



    /**
     * @return Returns the suffix.
     */
    public String getSuffix() {
        return suffix;
    }



    public void setProperty(String key,String val)
    {
        this.properties.setProperty(key,val);
    }
    
    public String getProperty(String key)
    {
        return this.properties.getProperty(key);
    }
    
    
    private static String nullIfEmpty(String s)
    {
        if(s==null) return s;
        if(s.trim().length()==0) return null;
        return s;
    }
    
    /**
     * @return RDF identifier
     */
    public String getID()
    {
    
   	 	String s1= this.lastName+"_"+this.foreName;
   	 	String s2="";
   	 	for(int i=0;i< s1.length();++i)
   	 	{
   	 		if(  'A' <= Character.toUpperCase(s1.charAt(i)) ||
                 'Z' >=Character.toUpperCase(s1.charAt(i)))
   	 		{
   	 			s2+= s1.charAt(i);
   	 		}
            else if(i>0)
                {
                s2+=(""+(int)s1.charAt(i));
                }
		 }
   	return XMLUtilities.escape(s2);
    }
    
    
    public boolean isCollective()
    {
    	return this.collectiveName!=null;
    }
    
    /*
     *  NCBI say: The syntax used to search for an author uses
     *  his last name followed by his initials
     *  followed by a space and a suffix abbreviation,
     *  if applicable, all without periods or a comma
     *  after the last name (e.g., fauci as or o'brien jc jr).
     *  Initials and suffixes may be omitted when searching.
     *  PubMed automatically truncates on an author's name
     *  to account for varying initials, e.g., o'brien j [au]
     *  will retrieve o'brien ja, o'brien jb, o'brien jc jr,
     *  as well as o'brien j. To turn off this automatic truncation,
     *  enclose the author's name in double quotes and the search tag [au],
     *   e.g., "o'brien j" [au] to retrieve just o'brien j
     */
    public String getTerm()
    {
    	 try
		 {
             if(this.foreName!=null && this.foreName.length()>0)
             {
                 return URLEncoder.encode("\""+
                         this.lastName+", "+
                         this.foreName+" "+
                         (this.suffix!=null?" "+suffix:"")+
                         "\"[fau]","UTF-8");
             }
             
             
    	 	return URLEncoder.encode("\""+
                    this.lastName+" "+
                    this.initials+
                    (this.suffix!=null?" "+suffix:"")+
                    "\"[au]","UTF-8");
		 }
    	 catch(Exception e)
		 {
    	 	e.printStackTrace();
    	 	return null;
		 }
    }
    

    
    public String getName()
    {
        return (suffix!=null?""+suffix+" ":"")+lastName+" "+foreName;
    }
    
    /**
     * overides/implements parent
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        String c=""+lastName+" "+foreName+" "+initials+" "+suffix;
        return c.hashCode();
        }
    
    public boolean isHomonyme(Author other)
        {
        if(equals(other)) return false;//it's the same
        Author cp=(Author)other;
        if(     lastName!=null &&
                cp.lastName!=null &&
                collator.compare(lastName,cp.lastName)==0) return true;
        //Debug.trace(""+getName()+" not homonyme of "+other.getName()+" "+Debug.getStackTrace());
        return false;
        }
    
    /**
     * overides/implements parent
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public int compareTo(Object obj)
        {
        if(obj==null) return -1;
        if(!(obj instanceof Author)) return -1;
        return (""+lastName).compareTo(""+((Author)obj).lastName);
        }
    
    /**
     * overides/implements parent
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object obj) {
        if(obj==null) return false;
        if(!(obj instanceof Author)) return false;
        if(isCollective()) return false;
        Author cp=(Author)obj;
        if(cp.isCollective()) return false;
        if(     lastName!=null &&
                cp.lastName!=null &&
                collator.compare(lastName,cp.lastName)!=0) return false;
        
        if(     foreName!=null &&
                cp.foreName!=null &&
                /* foreName may have been initialized from some inital.
                 * And initials may have two letters. So me must look beyond 
                 * the two first characters
                 */
                foreName.length() > 2 && 
                cp.foreName.length() > 2 &&
                collator.compare(foreName,cp.foreName)!=0
                )
            {
            return false;
            }
            
        
        boolean b= collator.compare(getTerm(),cp.getTerm())==0;
        return b;
        }
    
    public String toString()
    	{
        return getName();
    	}
    
    
   
}
