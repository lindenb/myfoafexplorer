/*
 * Author: Pierre Lindenbaum PhD
 * Contact: plindenbaum@yahoo.fr
 * Created on 3:43:49 PM
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

import java.awt.BorderLayout;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;




/**
 * @author lindenb
 *
 * <code>Question</code>
 */
public abstract class Question
    implements HyperlinkListener
    {
    private SciFOAF sciFOAF;
    protected Question(SciFOAF sciFOAF)
        {
        this.sciFOAF =sciFOAF;
        }
    
    public SciFOAF foaf()
        {
        return this.sciFOAF;
        }
    
    public abstract JPanel create();
    /**
     * overides/implements parent
     * @see org.lindenb.scifoaf.Question#update()
     */
    public void show() {
        foaf().cardLayout.show(foaf().cardPanel,getKey());
    }
    
    public JPanel createHeader(String icon,String title)
    {
    /* create title */
    JPanel titlepane= new JPanel(new BorderLayout());
    if(icon!=null)
        {
        titlepane.add(new JLabel(foaf().getIcon(icon)),BorderLayout.WEST);
        }
    titlepane.add(new JLabel(title,JLabel.CENTER),BorderLayout.CENTER);
    return titlepane;
    }
    
    public abstract void activate();
    public abstract String getKey();
    
    
    public void hyperlinkUpdate(HyperlinkEvent e) {
        HyperlinkEvent.EventType etype= e.getEventType();
      if(etype.equals(HyperlinkEvent.EventType.ACTIVATED))
      {
          foaf().setInformation(""+e.getURL());
      }
      else if(etype.equals(HyperlinkEvent.EventType.ENTERED))
      {
          foaf().setInformation(""+e.getSourceElement().getClass());
      }
      else if(etype.equals(HyperlinkEvent.EventType.EXITED))
      {
          foaf().setInformation(""+e.getSourceElement().getClass());
      }
    }
    
    public String toString()
    {
        return getKey();
    }
    

    public JButton createOKButton()
    {
        JButton b= foaf().createButton("finish.png");
        b.setToolTipText("Validate Information");
        return b;
    }

    
}
