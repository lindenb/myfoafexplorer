/*
 * Created on 27-Apr-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.lindenb.scifoaf;

import org.lindenb.lib.debug.Debug;
import org.lindenb.lib.ncbi.pubmed.PubmedRecord;
import org.lindenb.lib.xml.XMLUtilities;
import org.w3c.dom.Element;



/**
 * @author pierre
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class Laboratory extends AuthorList implements Comparable {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 1L;
String id;


public Laboratory()
	{
    super();
	this.id= null;
	}

public Laboratory(String id)
    {
    this();
    Debug.doAssert(id!=null);
    this.id=id.trim();
    }

public boolean equals(Object o)
	{
	if(o==null) return false;
	if(!(o instanceof Laboratory)) return false;
	if(o==this) return true;
	return(id.equals(((Laboratory)o).id));
	}


public int compareTo(Object o)
    {
    if(o==null) return -1;
    if(!(o instanceof Laboratory)) return -1;
    if(o==this) return 0;
    return(id.compareToIgnoreCase(((Laboratory)o).id));
    }


public String toString()
	{
	return  ""+id;
	}

static public String getPaperAffiliation(PubmedRecord rec)
    {
    if(rec==null) return null;
    Element e= rec.getElementNode();
    if(e==null) return null;
    
    if(e.getNodeName().equals("PubmedArticleSet"))
        {
        e= XMLUtilities.findFirstChildElement(e,"PubmedArticle");
        }
    e= XMLUtilities.findFirstChildElement(e,"MedlineCitation");
    e= XMLUtilities.findFirstChildElement(e,"Article");
    e= XMLUtilities.findFirstChildElement(e,"Affiliation");
    if(e==null) return null;
    
    String s= XMLUtilities.textContent(e);
    if(s!=null && s.trim().length()==0) s=null;
    return s;
    }

public String toHTML()
    {
    String tokens[] = this.id.split(",");
    StringBuffer html=new StringBuffer("<html><body><div style=\"margin:1cm;\"><adress>");
    for(int i=0;i< tokens.length;++i)
        {
        if(i>0) html.append("<br>");
        html.append(tokens[i].trim());
        }
    html.append("</adress></div></body></html>");
    return html.toString();
    }

}


