/*
 * Author: Pierre Lindenbaum PhD
 * Contact: plindenbaum@yahoo.fr
 * Created on 5:19:35 PM
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

import javax.swing.JTable;

/**
 * @author lindenb
 *
 * <code>AuthorPanel</code>
 */
public class AuthorPanel {
    Author author;
    JTable papersTable;
    /**
     * Constructor for <code>AuthorPanel</code>
     */
    public AuthorPanel(Author author) {
        super();
        this.author= author;
    }

}
