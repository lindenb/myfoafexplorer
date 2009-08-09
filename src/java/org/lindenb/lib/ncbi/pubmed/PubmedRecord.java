package org.lindenb.lib.ncbi.pubmed;

import org.lindenb.lib.xml.DOMNode;
import org.lindenb.lib.xml.XMLUtilities;
import org.lindenb.lib.xml.XPathAPI;
import org.w3c.dom.Element;



public class PubmedRecord extends DOMNode
	{
	private static final String BASE="PubmedArticle/MedlineCitation";
	
	public PubmedRecord(Element elementNode)
		{
		super(elementNode);
		}

	public Element getElementNode()
		{
		return (Element)getNode();
		}
	
	
	 public Integer getPMID()
	    {
	        Element elts[]=XPathAPI.selectElements(
	        		getElementNode(),
	               BASE+"/PMID");
	        if(elts.length!=1) return null;
	        return new Integer(XMLUtilities.textContent(elts[0]));
	    }

	    public String getTitle()
	    {
	        ///PubmedArticleSet/
	        Element elts[]=XPathAPI.selectElements(
	        		getElementNode(),
	        		BASE+"/Article/ArticleTitle"
	                );
	        if(elts.length!=1) return null;
	        return  XMLUtilities.textContent(elts[0]);
	    }
	    
	    public String getAbstract()
	    {
	        ///PubmedArticleSet/
	        Element elts[]=XPathAPI.selectElements(
	        		getElementNode(),
	        		BASE+"/Article/Abstract"
	                );
	        if(elts.length!=1) return null;
	        return  XMLUtilities.textContent(elts[0]);
	    }
	    
	    public Element[] getMeshDescriptorNameElements()
	    	{
	        return XPathAPI.selectElements(
	        		getElementNode(),
	        		BASE+"/MeshHeadingList/MeshHeading/DescriptorName");
	    	}
	    
	    public Element[] getAuthorElements()
	    	{
	        return XPathAPI.selectElements(
	        		getElementNode(),
	        		BASE+"/Article/AuthorList/Author"
	                );
	    	}

	    public String getJournalName()
	    	{
	        ///PubmedArticleSet/
	        Element elts[]=XPathAPI.selectElements(
	        		getElementNode(),
	        		BASE+"/MedlineJournalInfo/MedlineTA"
	                );
	        if(elts.length!=1) return null;
	        return XMLUtilities.textContent(elts[0]);
	    	}

	    public boolean equals(Object o)
	    	{
	        if(o==null ) return false;
	        if(o==this) return true;
	        if(!(o instanceof PubmedRecord)) return false;
	        return getPMID().equals( ((PubmedRecord)o).getPMID());
	    	}

	    
	
}
