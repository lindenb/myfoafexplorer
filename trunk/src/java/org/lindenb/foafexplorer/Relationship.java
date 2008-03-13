/*
Copyright (c) 2005 Pierre Lindenbaum PhD

Permission is hereby granted, free of charge, to any person obtaining
a copy of this software and associated documentation files (the
``Software''), to deal in the Software without restriction, including
without limitation the rights to use, copy, modify, merge, publish,
distribute, sublicense, and/or sell copies of the Software, and to
permit persons to whom the Software is furnished to do so, subject to
the following conditions:

The above copyright notice and this permission notice shall be included
in all copies or substantial portions of the Software.

The name of the authors when specified in the source files shall be 
kept unmodified.

THE SOFTWARE IS PROVIDED ``AS IS'', WITHOUT WARRANTY OF ANY KIND, EXPRESS
OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
IN NO EVENT SHALL 4XT.ORG BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE
USE OR OTHER DEALINGS IN THE SOFTWARE.


$Id: $
$Author: $
$Revision: $
$Date: $
$Locker: $
$RCSfile: $
$Source: $
$State: $
$Name: $
$Log: $


*************************************************************************/
package org.lindenb.foafexplorer;

import org.lindenb.lib.rdf.RDFUtils;
import org.lindenb.lib.xml.Namespaces;

/**
 * @author lindenb
 *
 */
public class Relationship
	{
	public static final String URIs[]=
		{
		Namespaces.REL + "acquaintanceOf",null,null,
		Namespaces.REL + "ambivalentOf",null,null,
		Namespaces.REL + "ancestorOf",null,null,
		Namespaces.REL + "antagonistOf",null,null,
		Namespaces.REL + "apprenticeTo",null,null,
		Namespaces.REL + "childOf",null,null,
		Namespaces.REL + "closeFriendOf",null,null,
		Namespaces.REL + "collaboratesWith",null,null,
		Namespaces.REL + "colleagueOf",null,null,
		Namespaces.REL + "descendantOf",null,null,
		Namespaces.REL + "employedBy",null,null,
		Namespaces.REL + "employerOf",null,null,
		Namespaces.REL + "enemyOf",null,null,
		Namespaces.REL + "engagedTo",null,null,
		Namespaces.REL + "friendOf",null,null,
		Namespaces.REL + "grandchildOf",null,null,
		Namespaces.REL + "grandparentOf",null,null,
		Namespaces.REL + "hasMet",null,null,
		Namespaces.REL + "knowsByReputation",null,null,
		Namespaces.REL + "knowsInPassing",null,null,
		Namespaces.REL + "knowsOf",null,null,
		Namespaces.REL + "lifePartnerOf",null,null,
		Namespaces.REL + "livesWith",null,null,
		Namespaces.REL + "lostContactWith",null,null,
		Namespaces.REL + "mentorOf",null,null,
		Namespaces.REL + "neighborOf",null,null,
		Namespaces.REL + "parentOf",null,null,
		Namespaces.REL + "participant",null,null,
		Namespaces.REL + "participantIn",null,null,
		Namespaces.REL + "siblingOf",null,null,
		Namespaces.REL + "spouseOf",null,null,
		Namespaces.REL + "worksWith",null,null,
		Namespaces.REL + "wouldLikeToKnow",null,null,
		Namespaces.FOAF + "knows",null,null,
		Namespaces.FOAF + "member",null,null,
		Namespaces.DC + "author",null,null,
		Namespaces.FOAF + "publications",null,null
		};
	
	
	public static final String INVERSE_PROPERTIES[][]=
		{
			{ , }
		};
	private Agent agents[];
	private String uri;
	
	public Relationship(Agent start, Agent end,String uri)
		{
		this.agents=new Agent[]{start,end};
		this.uri=uri;
		if(start==null || end==null || uri==null || start.equals(end)) throw new NullPointerException("Null Parameter");
		}
	
	public Agent getAgent(int index)
		{
		return this.agents[index];
		}
	
	public String getURI()
		{
		return this.uri;
		}
	
	public boolean hasURI(String uri)
		{
		return this.getURI().equals(uri);
		}
	

	public boolean hasURI(String uris[])
		{
		for(int i=0;i< uris.length;++i)
			{
			if(hasURI(uris[i])) return true;
			}
		return false;
		}

	public boolean equals(Object obj)
		{
		if(obj==null) return false;
		if(this==obj) return true;
		Relationship cp=(Relationship)obj;
		return getURI().equals(cp.getURI()) &&
			getAgent(0).equals(cp.getAgent(0)) &&
			getAgent(1).equals(cp.getAgent(1));
		}
	
	public int hashCode()
		{
		return getURI().hashCode();
		}
	
	public static String getInverseURI(String uri)
		{
			 if(uri.equals(Namespaces.REL+"childOf")) { return Namespaces.REL+"parentOf";}
		else if(uri.equals(Namespaces.REL+"parentOf")) { return Namespaces.REL+"childOf";}
		else if(uri.equals(Namespaces.REL+"fatherOf")) { return Namespaces.REL+"childOf";}
		else if(uri.equals(Namespaces.REL+"motherOf")) { return Namespaces.REL+"childOf";}
		else if(uri.equals(Namespaces.FOAF+"knows")) { return Namespaces.FOAF+"knows";}
		else if(uri.equals(Namespaces.FOAF+"publications")) { return Namespaces.DC+"author";}
		else if(uri.equals(Namespaces.DC+"author")) { return Namespaces.FOAF+"publications";}
		else if(uri.equals(Namespaces.REL+"friendOf")) { return Namespaces.REL+"friendOf";} 
		else if(uri.equals(Namespaces.REL+"closeFriendOf")) { return Namespaces.REL+"closeFriendOf";} 
		else if(uri.equals(Namespaces.FOAF+"member")) { return Namespaces.FOAF+"member";}
		return null;
		}
	
	public Relationship getInverseRelationShip()
		{
		String s= getInverseURI(getURI());
		if(s==null) return null;
		return new Relationship(getAgent(1),getAgent(0),s);
		}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString()
		{
		return "{"+ getAgent(0)+ " --"+ RDFUtils.localNameOf(getURI())+ "--> "+ getAgent(1)+"}";
		}
	}
