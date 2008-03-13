/*
 * Created on 27-Apr-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.lindenb.scifoaf;


import java.util.Collections;
import java.util.Vector;

/**
 * @author pierre
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class LaboratoryList extends Vector
{


/**
 * serialVersionUID
 */
private static final long serialVersionUID = 1L;

public LaboratoryList()
	{
	super(0,1);
	}

public int getLabCount() { return super.size();}
public Laboratory getLabAt(int i) { return (Laboratory)super.elementAt(i);}

public boolean contains(Laboratory lab)
{
    return super.contains(lab);
}

public boolean contains(String labName)
    {
    return contains(new Laboratory(labName));
    }


public Laboratory add(Laboratory laboratory)
	{
    for(int i=0;i< getLabCount();++i)
        {
        if(getLabAt(i).equals(laboratory)) return(getLabAt(i));
        }
	super.addElement(laboratory);
    return laboratory;
	}

public LaboratoryList getMemberIs(Author author)
	{
	LaboratoryList list= new LaboratoryList();
	for(int i=0;i< getLabCount();++i)
		{
		if(getLabAt(i).contains(author)) list.add(getLabAt(i));
		}
	return list;
	}

public void sort()
    {
    Collections.sort(this);
    }

}
