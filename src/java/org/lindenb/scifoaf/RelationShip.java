/*
 * Author: Pierre Lindenbaum PhD
 * Contact: plindenbaum@yahoo.fr
 * Created on 10:38:50 AM
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

/**
 * @author lindenb
 *
 * <code>RelationShip</code>
 */
public class RelationShip {
    private Author agent1;
    private Author agent2;
    
    public RelationShip(Author agent1, Author agent2)
    {
        this.agent1= agent1;
        this.agent2= agent2;
    }
    
    public boolean contains(Author agent)
    {
        return getFirstAgent().equals(agent) ||
                getSecondAgent().equals(agent);
    }
    
    public Author getComplementary(Author agent)
    {
        if(getFirstAgent().equals(agent))
        {
            return  getSecondAgent();
        }
        else if(getSecondAgent().equals(agent))
        {
            return  getFirstAgent();
        }
        return null;
        
    }
    
    public Author getFirstAgent()
    {
        return agent1;
    }
    
    public Author getSecondAgent()
    {
        return agent2;
    }
    
    public boolean equals(Object o)
    {
        if(o==null || !(o instanceof RelationShip)) return false;
        RelationShip r=(RelationShip)o;
        return  (getFirstAgent().equals(r.getFirstAgent()) &&
                getSecondAgent().equals(r.getSecondAgent())) ||
                (getFirstAgent().equals(r.getSecondAgent()) &&
                getSecondAgent().equals(r.getFirstAgent()));
                
    }
    
    public int hashCode()
    {
        return agent1.hashCode();
    }
    
    public String toString()
    {
        return ""+getFirstAgent()+" -> "+getSecondAgent();
    }
    
}
    
