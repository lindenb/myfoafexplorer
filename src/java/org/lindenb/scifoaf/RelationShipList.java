/*
 * Author: Pierre Lindenbaum PhD
 * Contact: plindenbaum@yahoo.fr
 * Created on 10:49:42 AM
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

//import com.integragen.www.util.Debug;


/**
 * @author lindenb
 *
 * <code>RelationShipList</code>
 */
public class RelationShipList {
  private HashSet datas;

 public RelationShipList()
 {
     this.datas=new HashSet();
 }
  
 public boolean contains(Author a, Author b)
 {
 	return contains(new RelationShip(a,b));
 }
 
 public boolean contains(RelationShip r)
 {
 	return this.datas.contains(r);
 }

 public boolean add(RelationShip rel)
 {
     //Debug.trace("Relation add "+rel);
     if(rel==null) return false;
     if(rel.getFirstAgent().equals(rel.getSecondAgent())) return false;
     boolean b=this.datas.add(rel);
     if(!b)
     {
         //Debug.trace("Cannot add "+rel);
     }
     return b;
 }
 
 public boolean remove(RelationShip rel)
 {
    return this.datas.remove(rel);
 }

 public int getRelCount()
 {
     return datas.size();
 }
 
public RelationShipList getRelationShips(Author agent)
{
    RelationShipList list= new RelationShipList();
    for (Iterator iter = datas.iterator(); iter.hasNext();) {
        RelationShip r = (RelationShip) iter.next();
        
        if(r.contains(agent))
        {
            list.add(r);
        }
    }
    return list;
}
 
public AuthorList getKnown(Author agent)
    {
    AuthorList list= new AuthorList();
    for (Iterator iter = datas.iterator(); iter.hasNext();) {
        RelationShip r = (RelationShip) iter.next();
        
        if(r.contains(agent))
        {
            list.add(r.getComplementary(agent));
        }
    }
    return list;
    }




}
