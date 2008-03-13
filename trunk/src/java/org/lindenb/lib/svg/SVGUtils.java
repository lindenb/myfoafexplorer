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

package org.lindenb.lib.svg;

import java.awt.Shape;
import java.awt.geom.GeneralPath;
import java.awt.geom.PathIterator;
import java.util.StringTokenizer;

import org.lindenb.lib.xml.Namespaces;



/**
 * @author lindenb
 * utilities for SVG
 */
public class SVGUtils {
	public static final String SVGNS=Namespaces.SVG;
	
	
	
	static public String shapeToPath(Shape shape)
	    {
	    StringBuffer path=new StringBuffer(""); 
	    
	    double tab[] = new double[6];
	    PathIterator pathiterator = shape.getPathIterator(null);
	       
	        while(!pathiterator.isDone()) {
	          switch(pathiterator.currentSegment(tab)) {
	          case PathIterator.SEG_MOVETO: {
	              path.append( "M " + svground(tab[0]) + " " + svground(tab[1]) + " ");
	              break;
	          }
	          case PathIterator.SEG_LINETO: {
	              path.append( "L " + svground(tab[0]) + " " + svground(tab[1]) + " ");
	              break;
	          }
	          case PathIterator.SEG_CLOSE: {
	              path.append( "Z ");
	              break;
	          }
	          case PathIterator.SEG_QUADTO: {
	              path.append( "Q " + svground(tab[0]) + " " + svground(tab[1]));
	              path.append( " "  + svground(tab[2]) + " " + svground(tab[3]));
	              path.append( " ");
	              break;
	          }
	          case PathIterator.SEG_CUBICTO: {
	              path.append( "C " + svground(tab[0]) + " " + svground(tab[1]));
	              path.append( " "  + svground(tab[2]) + " " + svground(tab[3]));
	              path.append( " "  + svground(tab[4]) + " " + svground(tab[5]));
	              path.append( " ");
	              break;
	          	}
	          }
	          pathiterator.next();
	        }
	     return path.toString();
	    }
	
	 private static float getPathFloat(StringTokenizer t)
	     {
	     float pathFloat;
	     String tempBuffer = t.nextToken();
	     while (tempBuffer.equals(",")|| tempBuffer.equals(" ")){
	      tempBuffer = t.nextToken();
	     }
	     if (tempBuffer.equals("-")){
	       pathFloat =(float) -1.0 * new Float(t.nextToken()).floatValue();
	     }
	     else{
	          pathFloat = (float)new Float(tempBuffer).floatValue();
	     }
	     return(pathFloat);
	  }
	
	public static GeneralPath Path2Shape(String pathString )
		{

		float fx=0,fy=0,fx1=0,fx2=0,fy1=0,fy2=0,oldfx=0,oldfy=0;
        GeneralPath p = new GeneralPath(GeneralPath.WIND_EVEN_ODD);
        
        StringTokenizer t = new StringTokenizer(pathString," ,MmLlCczArSsHhVvDdEeFfGgJjQqTtz-",true);
        while(t.hasMoreElements()){
            String tempBuffer = t.nextToken();
            //boolean negate=false;
            switch (tempBuffer.charAt(0)){
              case 'M': //Move To
                       fx = getPathFloat(t);
                       fy = getPathFloat(t);
                       oldfx=fx;
                       oldfy=fy;
                       p.moveTo(fx, fy);
                       break;
              case 'm':
                       fx = getPathFloat(t);
                       fy = getPathFloat(t);
                       fx+=oldfx;
                       fy+=oldfy;
                       oldfx=fx;
                       oldfy=fy;
                       p.moveTo(fx, fy);
                       break;

              case 'L': // Line to
                       fx = getPathFloat(t);
                       fy = getPathFloat(t);
                       oldfx=fx;
                       oldfy=fy;
                       p.lineTo(fx, fy);
                       break;
              case 'l':
                       fx = getPathFloat(t);
                       fy = getPathFloat(t);
                       fx+=oldfx;
                       fy+=oldfy;
                       oldfx=fx;
                       oldfy=fy;
                       p.lineTo(fx, fy);
                       break;
              case 'C':

                       fx = getPathFloat(t);
                       fy = getPathFloat(t);
                       fx1 = getPathFloat(t);
                       fy1 = getPathFloat(t);
                       fx2 = getPathFloat(t);
                       fy2 = getPathFloat(t);
                       p.curveTo(fx,fy,fx1,fy1,fx2,fy2);
                       oldfx= fx2;
                       oldfy= fy2;



                       break;
              case 'c':

                       fx = getPathFloat(t);
                       fy = getPathFloat(t);
                       fx1 = getPathFloat(t);
                       fy1 = getPathFloat(t);
                       fx2 = getPathFloat(t);
                       fy2 = getPathFloat(t);
                       fx+=oldfx;
                       fy+=oldfy;
                       fx1+=oldfx;
                       fy1=oldfy;
                       fx2+=oldfx;
                       fy2+=oldfy;
                       p.curveTo(fx,fy,fx1,fy1,fx2,fy2);
                       oldfx= fx2;
                       oldfy= fy2;

                       break;

              case 'z':
//                       System.out.println("closepath");
                       break;

              case 'A':
                       System.out.println("Absolute");
                       break;
              case 'r':
                       System.out.println("relative");
                       break;

              case 'S':
                       System.out.println("Smooth curve");
                       break;

              case 's':
                       System.out.println("relative smooth curve");
                       break;


              case 'H':
                       fy = getPathFloat(t);
                       oldfy=fy;
                       p.lineTo(oldfx, fy);


                       break;

              case 'h':
                       fy = getPathFloat(t);
                       fy+=oldfy;
                       oldfy=fy;
                       p.lineTo(oldfx, fy);

                       break;

              case 'V':
                       fx = getPathFloat(t);
                       oldfx=fx;
                       p.lineTo(fx, oldfy);

                       break;

              case 'v':
                       fx = getPathFloat(t);
                       fx+=oldfx;
                       oldfx=fx;
                       p.lineTo(fx, oldfy);

                       break;

              case 'D':
                       System.out.println("arc 1 - see spec");
                       break;

              case 'd':
                       System.out.println("relative arc 1");
                       break;
              case 'E':
                       System.out.println("arc 2 - with line");
                       break;

              case 'e':
                       System.out.println("relative arc 2");
                       break;
              case 'F':
                       System.out.println("arc 3");
                       break;

              case 'f':
                       System.out.println("relative arc 3");
                       break;
              case 'G':
                       System.out.println("arc 4");
                       break;

              case 'g':
                       System.out.println("relative arc 4");
                       break;


              case 'J':
                       System.out.println("elliptical quadrant");
                       break;

              case 'j':
                       System.out.println("relative elliptical quadrant");
                       break;
              case 'Q':
                       fx = getPathFloat(t);
                       fy = getPathFloat(t);
                       fx1 = getPathFloat(t);
                       fy1 = getPathFloat(t);
                       p.quadTo(fx,fy,fx1,fy1);
                       oldfx= fx2;
                       oldfy= fy2;


/*

        quadTo(float x1, float y1, float x2, float y2) 
                  Adds a curved segment, defined by two new points, to the path by drawing a Quadratic curve that
        intersects both the current coordinates and the coordinates (x2, y2), using the specified point (x1, y1) as a
        quadratic parametric control point.

*/

                       break;

              case 'q':
                       System.out.println("relative quadratic bezier curve to");
                       break;
              case 'T':
                       System.out.println("True Type quadratic bezier curve ");
                       break;

              case 't':
                       System.out.println("relative True Type quadratic bezier curve");
                       break;

              case '-':
                       System.out.println("Negative value");
                       break;

            }


          }

        return p;
		}
	
	
    private static String svground(double n)
	    {
	    return ""+n;
	    }
	
}
