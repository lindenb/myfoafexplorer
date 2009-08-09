package org.lindenb.lib.ncbi.pubmed;

import java.io.File;
import java.io.IOException;
import java.util.Vector;

import javax.swing.ListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.lindenb.lib.ncbi.EUtilities;
import org.lindenb.lib.xml.DOMNode;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

public class PubmedRecordSet
extends DOMNode
implements ListModel
{
private Vector datas;
private Vector listeners;


public PubmedRecordSet()
	{
	super(emptyDoc_());
	this.datas= new Vector();
	this.listeners= new Vector();
	}


public PubmedRecordSet(File xmlFile) throws IOException,SAXException
	{
	this();
	this.merge(xmlFile);
	}

static private DocumentBuilder getDocumentBuilder_()
	{
	try
		{
		DocumentBuilderFactory factory= DocumentBuilderFactory.newInstance();
		factory.setCoalescing(true);
		factory.setNamespaceAware(false);
		factory.setIgnoringComments(true);
		factory.setIgnoringElementContentWhitespace(true);
		factory.setValidating(false);
		DocumentBuilder builder= factory.newDocumentBuilder();
		return builder;
		}
	catch(Exception err)
		{
		throw new RuntimeException(err);
		}
	}

static private Document emptyDoc_()
	{
	try
		{
		DocumentBuilder builder=getDocumentBuilder_();
		Document doc= builder.newDocument();
		doc.appendChild(doc.createElement("PubmedArticleSet"));
		return doc;
		}
	catch(Exception err)
		{
		throw new RuntimeException(err);
		}
	}


public PubmedRecordSet(Document doc)
	{
	super(doc);
	this.datas= new Vector();
	this.listeners= new Vector();
	if(doc==null) return;
	Element root= doc.getDocumentElement();
	if(root==null) return;
	for(Node c=root.getFirstChild();c!=null;c=c.getNextSibling())
		{
		if(c.getNodeType()!=Node.ELEMENT_NODE) continue;
		this.datas.addElement(new PubmedRecord((Element)c));
		}
	}

public boolean remove(PubmedRecord rec)
	{
	for(int i=0;i< getSize();++i)
		{
		PubmedRecord pr = getRecordAt(i);
		if(pr.equals(rec))
			{
			Element e= pr.getElementNode();
			if(e.getParentNode()!=null)
				{
				e.getParentNode().removeChild(e);
				}
			this.datas.removeElementAt(i);
			fireIntervalRemoved(i,i);
			return true;
			}
		}
	return false;
	}
/**
 * merge records from another source
 * @param records the other source
 * @return the number of new records added
 */
public int merge(PubmedRecordSet records)
	{
	int size_begin=getSize();
	for(int i=0;i< records.getSize();++i)
		{
		PubmedRecord rec= records.getRecordAt(i);
		if(this.contains(rec)) continue;
		Element imported= (Element)((Document)getNode()).importNode(rec.getNode(),true);
		Element root= ((Document)getNode()).getDocumentElement();
		root.appendChild(imported);
		this.datas.addElement(new PubmedRecord(imported));
		}
	if(getSize()!=size_begin)
		{
		fireIntervalAdded(size_begin,getSize()-1);
		}
	return getSize()-size_begin;
	}

/**
 * read and merge records from another file
 * @param xmlfile the source file
 * @return the number of new records added
 */
public int merge(File xmlfile) throws SAXException,IOException
	{
	DocumentBuilder builder=getDocumentBuilder_();
	return this.merge(new PubmedRecordSet(builder.parse(xmlfile)));
	}


protected void fireIntervalAdded(int beg,int end)
	{
	ListDataEvent e= new ListDataEvent(this,ListDataEvent.INTERVAL_ADDED,beg,end);
	for(int i=this.listeners.size()-1;i>=0;--i)
		{
		ListDataListener l=(ListDataListener)this.listeners.elementAt(i);
		l.intervalAdded(e);
		}
	}

protected void fireIntervalRemoved(int beg,int end)
	{
	ListDataEvent e= new ListDataEvent(this,ListDataEvent.INTERVAL_REMOVED,beg,end);
	for(int i=this.listeners.size()-1;i>=0;--i)
		{
		ListDataListener l=(ListDataListener)this.listeners.elementAt(i);
		l.intervalRemoved(e);
		}
	}

public boolean contains(PubmedRecord rec)
	{
	for(int i=0;i< getSize();++i)
		{
		PubmedRecord pr = getRecordAt(i);
		if(pr.equals(rec)) return true;
		}
	return false;
	}

public int getSize()
	{
	return datas.size();
	}

public PubmedRecord getRecordAt(int index)
	{
	return (PubmedRecord)this.datas.elementAt(index);
	}


public Object getElementAt(int index)
	{
	return getRecordAt(index);
	}

public void addListDataListener(ListDataListener l)
	{
	listeners.add(l);
	}

public void removeListDataListener(ListDataListener l) {
	listeners.remove(l);
	}

static PubmedRecordSet search(String terms) throws SAXException,IOException
	{
	EUtilities utilities= new EUtilities(EUtilities.PUBMED);
	return new PubmedRecordSet(utilities.searchAndFetch(terms));
	}
}
