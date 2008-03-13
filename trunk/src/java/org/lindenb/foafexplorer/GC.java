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


import java.awt.FontMetrics;
import java.awt.Graphics2D;

import javax.swing.JComponent;

/**
 * @author lindenb
 *
 */
public class GC
	{

	private Graphics2D g;
	int step;
	FontMetrics fm;
	JComponent observer;
	boolean paintRelationships;
	
	GC(Graphics2D g,JComponent observer)
		{
		this.g=g;
		this.observer= observer;
		this.fm= g.getFontMetrics();
		this.paintRelationships=true;
		}

	Graphics2D g()
		{
		return this.g;
		}
	}
