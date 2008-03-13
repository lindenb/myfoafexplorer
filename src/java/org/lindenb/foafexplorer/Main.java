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

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Stroke;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.net.URL;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.plaf.metal.MetalLookAndFeel;
import javax.swing.table.DefaultTableModel;

import org.lindenb.lib.gis.WorldMapPanel;
import org.lindenb.lib.lang.ExceptionPane;
import org.lindenb.lib.rdf.RDFUtils;
import org.lindenb.lib.swing.RendererAdapter;
import org.lindenb.lib.xml.Namespaces;






/**
 * @author lindenb
 *
 */
public class Main
	extends JPanel
	implements Runnable
	{
	public static boolean DEBUGGING=false;
	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;
	private Model model;
	//private IndividualTableModel selected=new IndividualTableModel();
	private JPanel drawingArea;
	private JTabbedPane tabbedPane;
	private AgentPane individualAGentPane;
	private AgentPane groupAGentPane;
	private AgentPane paperGentPane;
	private JCheckBoxMenuItem isPaintingRelationships;
	/** allow animation */
	private JCheckBoxMenuItem allowDynamicReoganization;
	/** animation thread */
	private Thread relaxer;
	/** current top left corner */
	private Point topLeftcurrent= new Point(0,0);
	/** corner where we go */
	private Point topLeftGoal= new Point(0,0);
	/** offscreen drawing image */
	private BufferedImage offscreenImage=null;
	/** offscreen graphics */
	private Graphics2D offscreenGraphics=null;
	/** last click in drawing Area */
	private Point lastClickInDrawingArea=null;
	/** SVG world map */
	private WorldMapPanel worldMapPane= null;
	/** ListPanes */
	ListPane indiPane;
	ListPane groupPane;
	ListPane paperPane;
	/** backward History Action */
	private AbstractAction backwardHistoryAction;
	/** forward History Action */
	private AbstractAction forwardHistoryAction;
	/** history stack */
	private Vector historyStack;
	/** position in history */
	private int position_in_history=-1;//oui, 'minus'
	
	public Main(Model model)
		{
		super(new BorderLayout());
		
		JPanel main= new JPanel(new BorderLayout(2,2));
		JPanel gridPane= new JPanel(new GridLayout(0,1,5,5));
		main.add(gridPane,BorderLayout.CENTER);
		
		{
		JPanel bottom= new JPanel(new FlowLayout(FlowLayout.LEFT));
		main.add(bottom,BorderLayout.SOUTH);
		bottom.add(new JButton(new AbstractAction("Pierre Lindenbaum PhD. plindenbaum@yahoo.fr")
					{
					private static final long serialVersionUID = 1L;

					public void actionPerformed(ActionEvent e)
						{
						JOptionPane.showMessageDialog(null,
								"Pierre Lindenbaul plindenbaum@yahoo.fr",
								"About Me",JOptionPane.INFORMATION_MESSAGE,null);
						
						}				
				}));
		}
		
		JPanel leftPane= new JPanel(new BorderLayout());
		leftPane.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
		gridPane.add(leftPane);
		
		this.model=model;
		
			{
			JPanel topPane= new JPanel(new FlowLayout(FlowLayout.LEFT));
			main.add(topPane,BorderLayout.NORTH);
			
			JMenuBar bar= new JMenuBar();
			topPane.add(bar);
			JMenu menu= null;
			
			
				menu= new JMenu("File");
				menu.add(new JMenuItem(new AbstractAction("Save Icons")
					{
					private static final long serialVersionUID = 1L;

					public void actionPerformed(java.awt.event.ActionEvent e)
						{
						try
							{
							JFileChooser chooser= new JFileChooser();
							chooser.setDialogTitle("Select the directory where icons will be saved");
							chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
							int choice=chooser.showSaveDialog(null);
							if(choice!=JFileChooser.APPROVE_OPTION) return;
							File dir= chooser.getSelectedFile();
							if(dir==null || !dir.isDirectory()) return;
							
							for(int j=0;j<2;++j)
								{
								AgentTableModel agentTableModel= (j==0?
										(AgentTableModel)Main.this.model.getIndividuals():
										(AgentTableModel)Main.this.model.getGroups());
								for(int i=0;i< agentTableModel.getAgentCount();++i)
									{
									AbstractPeople indi=(AbstractPeople)agentTableModel.getAgentAt(i);
									Icon icon1= indi.getIcon();
									if(icon1==null || !(icon1 instanceof ImageIcon)) continue;
									ImageIcon icon=(ImageIcon)icon1;
									if( !(icon.getImage() instanceof BufferedImage ) ) continue;
									File f= new File(dir,"icon"+indi.getID()+".png");
									if(f.exists())
										{
										System.err.println("error occured (duplicate icon)"+f);
										continue;
										}
									System.out.println("s%"+Main.this.model.getIndividuals().getIndividualAt(i).getImage().getID()+"%icon"+indi.getID()+".png%");
									ImageIO.write((BufferedImage)icon.getImage(),"PNG",f);
									}
								}
							} catch (Exception err)
							{
							JOptionPane.showMessageDialog(
									null,
									new ExceptionPane(err),
									"Error",
									JOptionPane.ERROR_MESSAGE,null
									);
							}
						};
					}));
			menu.add(new JMenuItem(new AbstractAction("Save DOT")
						{
						private static final long serialVersionUID = 1L;

						public void actionPerformed(java.awt.event.ActionEvent e)
							{
							try
							{
							JFileChooser chooser= new JFileChooser();
							chooser.setDialogTitle("Select the File for DOT");
							int choice=chooser.showSaveDialog(null);
							if(choice!=JFileChooser.APPROVE_OPTION) return;
							File dir= chooser.getSelectedFile();
							if(dir==null || !dir.isFile()) return;
							if(dir.exists() && JOptionPane.showConfirmDialog(null,"Already exists, Remove "+dir+" ?")!=JOptionPane.OK_OPTION) return;
							PrintWriter out= new PrintWriter(new FileWriter(dir),true);
							out.println("graph G{");
							for(int i=0;i< Main.this.model.getIndividuals().getIndividualCount();++i)
								{
								Individual indi= Main.this.model.getIndividuals().getIndividualAt(i);
								out.println(indi.getID()+";");
								for(int j=0;j< 2;++j)
									{
									Individual p = indi.getParent(j);
									if(p!=null) out.println(indi.getID()+"->"+p.getID()+";");
									}
								}
							out.println("}");
							out.flush();
							out.close();
							} catch (Exception err)
							{
							JOptionPane.showMessageDialog(null,err.getMessage(),"Error",JOptionPane.ERROR_MESSAGE,null);
							}
							};
						}));
			if(DEBUGGING) bar.add(menu);
			
			
			menu= new JMenu("Options");
			if(DEBUGGING) bar.add(menu);
			menu.add(this.isPaintingRelationships=new JCheckBoxMenuItem(new AbstractAction("Paint Relations")
					{
					private static final long serialVersionUID = 1L;

					public void actionPerformed(java.awt.event.ActionEvent e)
						{
						Main.this.drawingArea.repaint();
						};
					}));
			menu.add(this.allowDynamicReoganization=new JCheckBoxMenuItem(new AbstractAction("Dynamic")
					{
					private static final long serialVersionUID = 1L;

					public void actionPerformed(java.awt.event.ActionEvent e)
						{
						Main.this.drawingArea.repaint();
						};
					}));
			
			this.allowDynamicReoganization.setSelected(true);
			
			menu= new JMenu("Help");
			bar.add(menu);
			menu.add(new JMenuItem(new AbstractAction("About the Author")
						{
						private static final long serialVersionUID = 1L;

						public void actionPerformed(java.awt.event.ActionEvent e)
							{
							JOptionPane.showMessageDialog(null,
									"Pierre Lindenbaum plindenbaum@yahoo.fr",
									"About the Author",JOptionPane.INFORMATION_MESSAGE,null);
							};
						}));
			
			
		
			/** history */
			
			topPane.add(new JButton(this.backwardHistoryAction= new AbstractAction("Back")
				{
				private static final long serialVersionUID = 1L;
	
				public void actionPerformed(ActionEvent e)
					{
					if(Main.this.position_in_history-1>=0 &&
					  !Main.this.historyStack.isEmpty()	)
						{
						--Main.this.position_in_history;
						if(Main.this.position_in_history==0) this.setEnabled(false);
						Main.this.forwardHistoryAction.setEnabled(true);
						Agent agent= (Agent)Main.this.historyStack.elementAt(Main.this.position_in_history);
						setAgentPane(agent);
						setAgentTab(agent);
						focusOnAgent(agent);
						}
					}
				/** @see javax.swing.AbstractAction#getValue(java.lang.String) */
				public Object getValue(String key)
					{
					if(	key.equals(AbstractAction.SHORT_DESCRIPTION) ||
						key.equals(AbstractAction.LONG_DESCRIPTION)
						)
						{
						if(Main.this.position_in_history-1>=0 &&
						  !Main.this.historyStack.isEmpty()	)
							{
							return Main.this.historyStack.elementAt(Main.this.position_in_history-1).toString();
							}
						}
					return super.getValue(key);
					}
				}));
			this.historyStack= new Vector();
			topPane.add(new JButton(this.forwardHistoryAction= new AbstractAction("Forward")
				{
				private static final long serialVersionUID = 1L;
				public void actionPerformed(ActionEvent e)
					{
					if(Main.this.position_in_history+1< Main.this.historyStack.size()	)
							{
							++Main.this.position_in_history;
							if(Main.this.position_in_history+1==Main.this.historyStack.size()) this.setEnabled(false);
							Main.this.backwardHistoryAction.setEnabled(true);
							Agent agent= (Agent)Main.this.historyStack.elementAt(Main.this.position_in_history);
							setAgentPane(agent);
							setAgentTab(agent);
							focusOnAgent(agent);
							}
					}
				/** @see javax.swing.AbstractAction#getValue(java.lang.String) */
				public Object getValue(String key)
					{
					if(	key.equals(AbstractAction.SHORT_DESCRIPTION) ||
						key.equals(AbstractAction.LONG_DESCRIPTION)
						)
						{
						if(Main.this.position_in_history+1< Main.this.historyStack.size()	)
							{
							return Main.this.historyStack.elementAt(Main.this.position_in_history+1).toString();
							}
						}
					return super.getValue(key);
					}
				}));	
		
			this.forwardHistoryAction.setEnabled(false);
			this.backwardHistoryAction.setEnabled(false);
			}
			
			{
			this.drawingArea= new JPanel(true)
				{
				private static final long serialVersionUID = 1L;

				protected void paintComponent(java.awt.Graphics g1d)
					{
					//super.paintComponent(g1d);
					
					if(	Main.this.offscreenImage==null ||
						Main.this.offscreenImage.getWidth(this)!=this.getWidth() ||
						Main.this.offscreenImage.getHeight(this)!=this.getHeight()
						)
						{
						if(Main.this.offscreenGraphics!=null) Main.this.offscreenGraphics.dispose();
						Main.this.offscreenImage= new BufferedImage(this.getWidth(),this.getHeight(),BufferedImage.TYPE_INT_RGB);
						Main.this.offscreenGraphics= Main.this.offscreenImage.createGraphics();
						}
					
					
					Graphics2D g=Main.this.offscreenGraphics;
					
					g.setColor(Color.WHITE);
					g.fillRect(0,0,getWidth(),getHeight());
					
					AffineTransform tr=g.getTransform();
					g.translate(-Main.this.topLeftcurrent.x,-Main.this.topLeftcurrent.y);
					/*g.translate(
						-scrollBars[1].getValue(),
						-scrollBars[0].getValue()
						);*/
					GC gc=new GC(g,this);
					gc.paintRelationships= Main.this.isPaintingRelationships.isSelected();
					Main.this.model.paint(gc);
					int indexes[]=Main.this.indiPane.table.getSelectedRows();
					Stroke stroke= g.getStroke();
					g.setStroke(new BasicStroke(3));
					g.setColor(Color.RED);
					int row =Main.this.groupPane.table.getSelectedRow();
					if(row!=-1)
						{
						g.setColor(Color.GRAY);
						Group group=(Group)Main.this.model.getGroups().getAgentAt(row);
						for(int j=0;j< group.getMemberCount();++j)
							{
							if(group.getMemberAt(j).getType()!=AbstractPeople.T_INDIVDIDUAL) continue;
							((Individual)group.getMemberAt(j)).drawFrame(gc,1);
							
							}
						}

					for(int i=0;i< indexes.length;++i)
						{	
						((Individual)Main.this.model.getIndividuals().getAgentAt(indexes[i])).drawFrame(gc,2);
						}
					
					
					g.setStroke(stroke);
					g.setTransform(tr);
					
					g1d.drawImage(Main.this.offscreenImage,0,0,this);
					
					if(Main.this.tabbedPane.getSelectedIndex()==Main.this.tabbedPane.indexOfTab("GIS"))
						{
						Main.this.worldMapPane.repaint();
						}
					};
					
				
				/* (non-Javadoc)
				 * @see javax.swing.JComponent#getToolTipText(java.awt.event.MouseEvent)
				 */
				public String getToolTipText(MouseEvent e)
					{
					Individual indi= Main.this.model.getIndividuals().findIndividualAt(
							e.getX()+Main.this.topLeftcurrent.x,
							e.getY()+Main.this.topLeftcurrent.y
							);
					if(indi!=null) return indi.getDescription();
					return null;
					}
				};
			this.drawingArea.addMouseWheelListener(new MouseWheelListener()
					{
					/* (non-Javadoc)
					 * @see java.awt.event.MouseWheelListener#mouseWheelMoved(java.awt.event.MouseWheelEvent)
					 */
					public void mouseWheelMoved(MouseWheelEvent mouse)
						{
						
						Main.this.topLeftcurrent.y+=mouse.getUnitsToScroll()*Image.SIZE;
						drawingArea.repaint();
						}
					});	
			
			this.drawingArea.addMouseListener(new MouseAdapter()
				{
				public void mousePressed(MouseEvent e)
					{
					Main.this.lastClickInDrawingArea=e.getPoint();
					Main.this.topLeftGoal.x=Main.this.topLeftcurrent.x;
					Main.this.topLeftGoal.y=Main.this.topLeftcurrent.y;
					}
				public void mouseClicked(MouseEvent e)
					{
					if(e.getClickCount()<2) return;
					int row=Main.this.model.getIndividuals().findIndividualIndexAt(
							e.getX()+Main.this.topLeftcurrent.x,
							e.getY()+Main.this.topLeftcurrent.y
							);
					if(row==-1) return;
					Individual indi=Main.this.model.getIndividuals().getIndividualAt(row);

					setHistory(indi);
					setIndividualPane(indi);
					Main.this.indiPane.table.setRowSelectionInterval(row,row);
					Main.this.tabbedPane.setSelectedIndex(Main.this.tabbedPane.indexOfTab("Individual"));
					
					}
				});
			
			this.drawingArea.addMouseMotionListener(new MouseMotionAdapter()
					{
					public void mouseDragged(MouseEvent e)
						{
						int dx= e.getX()-Main.this.lastClickInDrawingArea.x;
						int dy= e.getY()-Main.this.lastClickInDrawingArea.y;
						Main.this.topLeftcurrent.x-=dx;
						Main.this.topLeftcurrent.y-=dy;
						Main.this.lastClickInDrawingArea.x= e.getX();
						Main.this.lastClickInDrawingArea.y= e.getY();
						Main.this.drawingArea.repaint();
						}
					});
					
			this.drawingArea.setToolTipText("");
			this.drawingArea.setOpaque(true);
			this.drawingArea.setBackground(Color.WHITE);
			}
		this.drawingArea.setBorder(
				BorderFactory.createCompoundBorder(
						BorderFactory.createLineBorder(Color.GRAY),
						BorderFactory.createEmptyBorder(5,5,5,5)
						)
						);
			
		
		leftPane.add(this.drawingArea,BorderLayout.CENTER);
		
		this.tabbedPane= new JTabbedPane()
			{
			private static final long serialVersionUID = 1L;

			public void add(Component component, Object constraints)
				{
				super.add(component, constraints);
				if(component instanceof JComponent)
					{
					((JComponent)component).setBorder(BorderFactory.createLineBorder(
							Color.GRAY
						));
					}
				}
			};
		this.tabbedPane.addChangeListener(new ChangeListener()
				{
				public void stateChanged(ChangeEvent e)
					{
					Main.this.drawingArea.repaint();
					}
				});
		this.tabbedPane.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
		gridPane.add(tabbedPane);
		
			{
			this.indiPane= new ListPane(Main.this.model.getIndividuals());
			this.tabbedPane.add(indiPane,"Individuals");
			this.tabbedPane.add(this.individualAGentPane= new AgentPane(),"Individual");
			this.groupPane= new ListPane(Main.this.model.getGroups());
			this.tabbedPane.add(groupPane,"Groups");
			this.tabbedPane.add(this.groupAGentPane= new AgentPane(),"Group");
			this.paperPane = new ListPane(this.model.getPapers());
			this.tabbedPane.add(this.paperPane,"Papers");	
			this.tabbedPane.add(this.paperGentPane= new AgentPane(),"Paper");
			}
			
			{
			JPanel gispane= new JPanel(new BorderLayout());
			tabbedPane.add(gispane,"GIS");
			gispane.add(this.worldMapPane= new WorldMapPanel()
					{
					private static final long serialVersionUID = 1L;

					protected void paintComponent(Graphics g1)
						{
						if(Main.this.tabbedPane.getSelectedIndex()!=Main.this.tabbedPane.indexOfTab("GIS")) return;
						super.paintComponent(g1);
						
						for(int i=0;i< Main.this.model.getIndividuals().getIndividualCount();++i)
							{
							Individual indi= Main.this.model.getIndividuals().getIndividualAt(i);
							if(!indi.isVisible()) continue;
							Point2D geoloc= indi.getGeoLoc();
							if(geoloc==null) continue;
							
							Point p1 = this.location2screen(geoloc);
							
							g1.setColor(Color.GREEN);
							
							
							int areax= (int)indi.getX()-Main.this.topLeftcurrent.x;
							int areay= (int)indi.getY()-Main.this.topLeftcurrent.y+Image.SIZE/2;
							
							if(areax>0 && areax<= Main.this.drawingArea.getWidth() &&
							   areay>0 && areay<= Main.this.drawingArea.getHeight()	)
								{
								Point p2 = SwingUtilities.convertPoint(
										Main.this.drawingArea,
										areax, areay, this);
								g1.drawLine(p1.x,p1.y,p2.x,p2.y);
								
								p2 = SwingUtilities.convertPoint(
										this,
										p1.x,p1.y, Main.this.drawingArea);
								Graphics2D g2d= (Graphics2D)Main.this.drawingArea.getGraphics();
								g2d.setColor(Color.GREEN);
								g2d.drawLine(areax,areay,p2.x,p2.y);
								}
							}
						}
					});
			}
			
		add(main,BorderLayout.CENTER);
		
		
		
		}
	

	
	
    public void run()
    	{
    	final double step=0.9;
	    Thread me = Thread.currentThread();
		while (relaxer == me)
		   {
		  
		   this.topLeftcurrent.x = this.topLeftGoal.x- (int)(  step* ( this.topLeftGoal.x -  this.topLeftcurrent.x ));
		   this.topLeftcurrent.y = this.topLeftGoal.y- (int)(  step* ( this.topLeftGoal.y -  this.topLeftcurrent.y ));
		   double distance= this.topLeftcurrent.distance(this.topLeftGoal);
		   
		   boolean indimoving=false;
		   for(int i=0;i< this.model.getIndividuals().getIndividualCount();++i)
			   {
			   Individual indi= this.model.getIndividuals().getIndividualAt(i);
			   if(!indi.isMoving()) continue;
			   indimoving=true;
			   indi.setLocation(
					   indi.getGoalX()- (  step* ( indi.getGoalX()-indi.getX() )),
					   indi.getGoalY()- (  step* ( indi.getGoalY()-indi.getY() ))
				   );
			   if(Point2D.distance(indi.getGoalX(),indi.getGoalY(),indi.getX(),indi.getY())<2)
				   {
				   indi.setLocation(indi.getGoalX(),indi.getGoalY());
				   }
			   }
		   
		   if(distance< 2 && indimoving==false)
			   {
			   this.topLeftcurrent.x= this.topLeftGoal.x;
			   this.topLeftcurrent.y= this.topLeftGoal.y;
			   this.drawingArea.repaint();
			   break;
			   }
		   this.drawingArea.repaint();
		   
		   /*
		    if (random && (Math.random() < 0.03))
		    	{
			Node n = node((int)(Math.random() * nnodes()));
			if (!n.isFixed()) {
			    n.setLocation(
			    		 n.getX()+ 100*Math.random() - 50,
			    		 n.getY()+ 100*Math.random() - 50);
				}
			
		    }*/
		    try {
			Thread.sleep(100);
		    } catch (InterruptedException e) {
			break;
		    }
		}
		 for(int i=0;i< this.model.getIndividuals().getIndividualCount();++i)
		   {
		   Individual indi= this.model.getIndividuals().getIndividualAt(i);
		   indi.setLocation(indi.getGoalX(),indi.getGoalY());
		   }
		 this.drawingArea.repaint();
		this.relaxer = null;
		}

	public void start() {
		relaxer = new Thread(this);
		relaxer.start();
	    }
	
	
	private synchronized void regroupPerson(HashSet persons)
		{
//		calcule barycentre
		Point barycentre=new Point(0,0);
		int count=0;
		for(Iterator i=persons.iterator();i.hasNext();)
			{
			Individual indi = (Individual)i.next();
			indi.setVisible(true);
			barycentre.x += indi.getGoalX();
			barycentre.y += indi.getGoalY();
			++count;
			}
		if(count==0)
			{
			this.drawingArea.repaint();
			return;
			}
		barycentre.x/=count;
		barycentre.y/=count;
		regroupPerson(persons,barycentre,null);
		}
	
	private synchronized void regroupPerson(HashSet persons,Point barycentre,Individual center)
		{
		if(this.allowDynamicReoganization.isSelected())
			{
			Vector inSet=new Vector(persons);
			Vector notInSet=new Vector(this.model.getIndividuals().getIndividualCount()-inSet.size(),1);
			
			SortXY sorter = new SortXY(barycentre);
			Collections.sort(inSet,sorter);
			for(int i=0;i< this.model.getIndividuals().getIndividualCount();++i)
				{
				Individual other=this.model.getIndividuals().getIndividualAt(i);
				if(other==center || persons.contains(other)) continue;
				other.setVisible(false);
				notInSet.addElement(other);
				}
			
			Collections.sort(notInSet,sorter);
			
			for(int i=0;i< notInSet.size() && i<inSet.size();++i)
				{
				Individual not = (Individual)notInSet.elementAt(i);
				Individual in = (Individual)inSet.elementAt((inSet.size()-1)-i);
				
				if( barycentre.distance(in.getGoalX() ,in.getGoalY() ) <
					barycentre.distance(not.getGoalX(),not.getGoalY()))
					{
					break;
					}
				not.swapGoal(in);
				}
			}
		
		focusOnPoint(barycentre.x,barycentre.y);
		}
	
	static private class SortXY implements Comparator
		{
		Point barycentre;
		SortXY(Point barycentre){ this.barycentre=barycentre;}
		public int compare(Object arg0, Object arg1)
			{
			double d1= barycentre.distance(((Individual)arg0).getGoalX(),((Individual)arg0).getGoalY());
			double d2= barycentre.distance(((Individual)arg1).getGoalX(),((Individual)arg1).getGoalY());
			if(d1==2) return 0;
			return (d1<d2?-1:1);
			}
		}
	
	public void setHistory(Agent agent)
		{
		++this.position_in_history;
		this.historyStack.setSize(this.position_in_history+1);
		this.historyStack.setElementAt(agent,this.position_in_history);
		this.forwardHistoryAction.setEnabled(false);
		this.backwardHistoryAction.setEnabled(this.position_in_history>0);
		}
	
	
	void setAgentPane(Agent agent)
		{
		if(agent==null) return;
		switch(agent.getType())
			{
			case Agent.T_INDIVDIDUAL: setIndividualPane((Individual)agent); break;
			case Agent.T_GROUP: setGroupPane((Group)agent); break;
			case Agent.T_PAPER: setPaperPane((Paper)agent); break;
			}
		}
	
	void setAgentTab(Agent agent)
		{
		if(agent==null) return;
		switch(agent.getType())
			{
			case Agent.T_INDIVDIDUAL: this.tabbedPane.setSelectedIndex(Main.this.tabbedPane.indexOfTab("Individual")); break;
			case Agent.T_GROUP: this.tabbedPane.setSelectedIndex(Main.this.tabbedPane.indexOfTab("Group"));break;
			case Agent.T_PAPER: this.tabbedPane.setSelectedIndex(Main.this.tabbedPane.indexOfTab("Paper")); break;
			}
		}
	
	
	void focusOnAgent(Agent agent)
		{
		if(agent==null) return;
		switch(agent.getType())
			{
			case Agent.T_INDIVDIDUAL: focusOnIndividual((Individual)agent); break;
			case Agent.T_GROUP: focusOnGroup((Group)agent); break;
			case Agent.T_PAPER: focusOnPaper((Paper)agent); break;
			}
		}
	
	void setIndividualPane(Individual individual)
		{
		this.individualAGentPane.agentOfThePane =individual;
		if(individual==null)
			{
			this.individualAGentPane.setEnabled(false);
			this.individualAGentPane.bioArea.setText("<html></html>");
			this.individualAGentPane.nameLabel.setIcon(null);
			this.individualAGentPane.nameLabel.setText("");
			this.individualAGentPane.relations.setModel(new DefaultTableModel(0,2));
			}
		else
			{
			this.individualAGentPane.setEnabled(true);
			this.individualAGentPane.bioArea.setText(individual.getDescription());
			this.individualAGentPane.nameLabel.setIcon(individual.getIcon());
			this.individualAGentPane.nameLabel.setText(individual.getName());
			this.individualAGentPane.relations.setModel(individual.getRelationShips());
			}
		this.individualAGentPane.bioArea.setCaretPosition(0);
		}
	
	void setGroupPane(Group group)
		{
		this.individualAGentPane.agentOfThePane =group;
		if(group==null)
			{
			this.groupAGentPane.bioArea.setText("<html></html>");
			this.groupAGentPane.nameLabel.setIcon(null);
			this.groupAGentPane.nameLabel.setText("");
			this.groupAGentPane.relations.setModel(new DefaultTableModel(0,2));
			}
		else
			{
			this.groupAGentPane.bioArea.setText(group.getDescription());
			this.groupAGentPane.nameLabel.setIcon(group.getIcon());
			this.groupAGentPane.nameLabel.setText(group.getName());
		    this.groupAGentPane.relations.setModel(group.getRelationShips());
			}
		this.groupAGentPane.bioArea.setCaretPosition(0);
		}
	
	
	void setPaperPane(Paper paper)
		{
		this.individualAGentPane.agentOfThePane =paper;
		if(paper==null)
			{
			this.paperGentPane.bioArea.setText("<html></html>");
			//this.groupAGentPane.nameLabel.setIcon(null);
			this.paperGentPane.nameLabel.setText("");
			this.paperGentPane.relations.setModel(new DefaultTableModel(0,2));
			}
		else
			{
			this.paperGentPane.bioArea.setText(paper.getDescription());
			//this.groupAGentPane.nameLabel.setIcon(paper.getIcon());
			this.paperGentPane.nameLabel.setText(paper.getName());
		    this.paperGentPane.relations.setModel(paper.getRelationShips());
			}
		this.paperGentPane.bioArea.setCaretPosition(0);
		}
	
	/*void focusOnGroup(Group group)
		{
		if(group==null) return;
		
		HashSet set= new HashSet();
		for(int i=0;i< Main.this.groupAGentPane.relations.getModel().getRowCount();++i)
			{
			Agent a= (Agent)Main.this.groupAGentPane.relations.getModel().getValueAt(i,1);
			if(a.getType()!=Agent.T_INDIVDIDUAL) continue;
			set.add(a);
			}
		for(int i=0;i< Main.this.model.getIndividuals().getIndividualCount();++i)
			{
			Individual indi =  Main.this.model.getIndividuals().getIndividualAt(i);
			indi.setVisible(set.contains(indi));
			}
		
		regroupPerson(set);
		}*/
	
	void focusOnPaper(Paper paper)
		{
		if(paper==null) return;
		HashSet knows= new HashSet();
		//NON knows.add(individual);
		for(int i=0;i< paper.getRelationCount();++i)
			{
			Relationship rel= paper.getRelationShipAt(i);
			if(rel.getAgent(1).getType() != Agent.T_INDIVDIDUAL) continue;
			if(!rel.getURI().equals(Namespaces.DC+"author")) continue;
			Individual other= (Individual)rel.getAgent(1);
			other.setVisible(true);
			knows.add(other);
			}
		
		for(int i=0;i< model.getIndividuals().getIndividualCount();++i)
			{
			Individual indi= model.getIndividuals().getIndividualAt(i);
			if(!knows.contains(indi)) indi.setVisible(false);
			}
		regroupPerson(knows);
		}
	
	void focusOnGroup(Group group)
		{
		HashSet knows= new HashSet();
		for(int i=0;i< group.getMemberCount();++i)
			{
			AbstractPeople people= group.getMemberAt(i);
			if(people.getType() != Agent.T_INDIVDIDUAL) continue;
			Individual other= (Individual)people;
			other.setVisible(true);
			knows.add(other);
			}
		for(int i=0;i< model.getIndividuals().getIndividualCount();++i)
			{
			Individual indi= model.getIndividuals().getIndividualAt(i);
			if(!knows.contains(indi)) indi.setVisible(false);
			}
		regroupPerson(knows);
		}
	
	void focusOnIndividual(Individual individual)
		{
		if(individual==null) return;
		individual.setVisible(true);
		HashSet knows= new HashSet();
		//NON knows.add(individual);
		for(int i=0;i< individual.getRelationCount();++i)
			{
			Relationship rel= individual.getRelationShipAt(i);
			if(rel.getAgent(1).getType() != Agent.T_INDIVDIDUAL) continue;
			Individual other= (Individual)rel.getAgent(1);
			other.setVisible(true);
			knows.add(other);
			}
		regroupPerson(knows,new Point((int)individual.getX(),(int)individual.getY()),individual);
		}
	
	synchronized void  focusOnPoint(int x, int y)
		{	
		this.topLeftGoal.x= x-this.drawingArea.getWidth()/2;
		this.topLeftGoal.y= y-this.drawingArea.getHeight()/2;
		if(this.relaxer==null) start();
		}
	
	
	
	
	public void activateURL(URL url)
	{
		
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args)
		{
		DEBUGGING=true;
		if(args.length!=1)
			{
			System.err.println("Expected one foaf file as input");
			System.exit(1);
			}
		try
			{
			UIManager.setLookAndFeel(new MetalLookAndFeel());
			
			JFrame.setDefaultLookAndFeelDecorated(true);
			JDialog.setDefaultLookAndFeelDecorated(true);
			JFrame frame= new JFrame("FOAF");
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			Dimension screen=Toolkit.getDefaultToolkit().getScreenSize();
			frame.setBounds(50,50,screen.width-100,screen.height-100);
			Model m=  Model.load(new File(args[0]));
			Main f= new Main(m);
			frame.setContentPane(f);
			frame.setVisible(true);
			Point c= m.getIndividuals().getCenter();
			f.focusOnPoint(c.x,c.y);
			} catch (Exception e)
			{
			JOptionPane.showMessageDialog(null,
					new ExceptionPane(e),
					"Error",JOptionPane.ERROR_MESSAGE,null);
					
			e.printStackTrace();
			}

		}

	/**
	 * 
	 * PANE FOR LIST
	 *
	 */
	private class ListPane extends JPanel
		{
		/**
		 * serialVersionUID
		 */
		private static final long serialVersionUID = 1L;
		/** search field */
		private JTextField searchField;
		/** table */
		private JTable table;
		/** model */
		private AgentTableModel agentTableModel;
		
		ListPane(AgentTableModel agentTableModel)
		{
		super(new BorderLayout());
		this.agentTableModel= agentTableModel;
		this.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));		
		JPanel topPane= new JPanel(new FlowLayout(FlowLayout.LEFT));
		this.add(topPane,BorderLayout.NORTH);
		topPane.add(new JLabel("Search :",JLabel.RIGHT));
		topPane.add(this.searchField= new JTextField(20));
		AbstractAction searchAction= new AbstractAction("Search")
			{

			private static final long serialVersionUID = 1L;

			public void actionPerformed(java.awt.event.ActionEvent e)
				{
				String s=  ListPane.this.searchField.getText().trim();
				if(s.length()==0 || ListPane.this.agentTableModel.getAgentCount()==0) return;
				try
					{
					Pattern pattern= Pattern.compile(s,Pattern.CASE_INSENSITIVE);
					int start= ListPane.this.table.getSelectedRow();
					if(start==-1) start=0;
					int curr=start+1;
					while(curr!=start)
						{
						if(curr== ListPane.this.table.getRowCount())
							{
							curr=0;
							}
						if(ListPane.this.agentTableModel.getAgentAt(curr).match(pattern))
							{
							ListPane.this.table.getSelectionModel().setSelectionInterval(curr,curr);
							ListPane.this.table.scrollRectToVisible( ListPane.this.table.getCellRect(curr,0,false));
							return;
							}
						if(curr==0 && curr==start) break;
						++curr;
						}
					Toolkit.getDefaultToolkit().beep();
					}
				catch(PatternSyntaxException err)
					{
					JOptionPane.showMessageDialog(null,"Illegal Regular expression "+err.getDescription(),"Error",JOptionPane.ERROR_MESSAGE,null);
					return;
					}
				};
			};
		this.searchField.addActionListener(searchAction);
		topPane.add(new JButton(searchAction));
		
		this.table= new JTable(agentTableModel);
		this.table.setRowHeight(Image.SIZE);
		RendererAdapter renderer = new RendererAdapter()
		{
		private static final long serialVersionUID = 1L;

		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
			{
			Component c= super.getTableCellRendererComponent(table,value,isSelected,hasFocus,row,column);
			int i= table.convertColumnIndexToModel(column);
			if(i==0)
				{
				Agent agent= (Agent)ListPane.this.agentTableModel.getAgentAt(row);
				if(	agent.getType()==Agent.T_INDIVDIDUAL ||
					agent.getType()==Agent.T_GROUP)
					{
					this.setIcon(((AbstractPeople)agent).getIcon());
					}
				else
					{
					this.setIcon(null);
					}
				}
			else
				{
				this.setIcon(null);
				}
			return c;
			}
		};
		this.table.setDefaultRenderer(Object.class,renderer);
		this.table.setDefaultRenderer(Integer.class,renderer);
		this.add(new JScrollPane(table),BorderLayout.CENTER);
		this.table.getSelectionModel().addListSelectionListener(new ListSelectionListener()
				{
				public void valueChanged(ListSelectionEvent e)
					{
					int row= ListPane.this.table.getSelectedRow();
					if(row==-1) return;
					Agent agent=(Agent)ListPane.this.agentTableModel.getAgentAt(row);
					focusOnAgent(agent);
					
					Main.this.drawingArea.repaint();
					}
				});
		this.table.addMouseListener(new MouseAdapter()
				{
				public void mouseClicked(MouseEvent e)
					{
					if(e.getClickCount()<2) return;
					int row=ListPane.this.table.rowAtPoint(e.getPoint());
					if(row==-1) return;
					Agent agent=(Agent)ListPane.this.agentTableModel.getAgentAt(row);
					setHistory(agent);
					if(agent.getType()==Agent.T_INDIVDIDUAL)
						{
						setIndividualPane((Individual)agent);
						focusOnIndividual((Individual)agent);
						Main.this.tabbedPane.setSelectedIndex(Main.this.tabbedPane.indexOfTab("Individual"));
						}
					else if(agent.getType()==Agent.T_GROUP)
						{
						setGroupPane((Group)agent);
						Main.this.tabbedPane.setSelectedIndex(Main.this.tabbedPane.indexOfTab("Group"));
						}
					else if(agent.getType()==Agent.T_PAPER)
						{
						setPaperPane((Paper)agent);
						Main.this.tabbedPane.setSelectedIndex(Main.this.tabbedPane.indexOfTab("Paper"));
						}
					
					}
				});
			
		}
	}
	
	/**
	 * 
	 * Pane for RECORD
	 *
	 */
	private class AgentPane extends JPanel
		{
		/**
		 * serialVersionUID
		 */
		private static final long serialVersionUID = 1L;
		JLabel nameLabel;
		JEditorPane bioArea;
		JTable relations;
		Agent agentOfThePane;
		AgentPane()
			{
			super(new BorderLayout());
			agentOfThePane=null;
			nameLabel = new JLabel("Name");
			nameLabel.setFont(new Font("Helvetica",Font.BOLD,18));
			nameLabel.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
			nameLabel.addMouseListener(new MouseAdapter()
				{
				/* (non-Javadoc)
				 * @see java.awt.event.MouseAdapter#mouseClicked(java.awt.event.MouseEvent)
				 */
				public void mouseClicked(MouseEvent e)
					{
					if(agentOfThePane==null || e.getClickCount()<2) return;
					setHistory(agentOfThePane);
					setAgentPane(agentOfThePane);
					focusOnAgent(agentOfThePane);
					}
				});
			
			this.add(nameLabel,BorderLayout.NORTH);
			JPanel indipane2= new JPanel(new GridLayout(1,0,5,5));
			this.add(indipane2,BorderLayout.CENTER);
			this.bioArea = new JEditorPane("text/html","<html></html>");
			this.bioArea.addHyperlinkListener(new HyperlinkListener()
					{
                public void hyperlinkUpdate(HyperlinkEvent e)
	                {
	                if(e.getEventType()==HyperlinkEvent.EventType.ACTIVATED)
	                        {
	                        String s=e.getDescription();
	                        if(s==null) return;
	                        s=RDFUtils.removeDiez(s);
	                        //Debug.trace(s);
	                        Individual indi=(Individual)Main.this.model.getIndividuals().getAgentByURI(s);
	                        
	                        if(indi!=null)
								{ 
								int row=Main.this.model.getIndividuals().indexOf(indi);
								if(row==-1) return; 
								Main.this.indiPane.table.getSelectionModel().setSelectionInterval(row,row);
								Main.this.indiPane.table.scrollRectToVisible(Main.this.indiPane.table.getCellRect(row, 0,false)); 
								Main.this.tabbedPane.setSelectedIndex(Main.this.tabbedPane.indexOfTab("Individual"));
								return;
								}
	                        
	                        Group g=(Group)Main.this.model.getGroups().getAgentByURI(s);
							 if(g!=null)
								{
								int row=Main.this.model.getGroups().indexOf(g);
								if(row==-1) return;
								Main.this.groupPane.table.getSelectionModel().setSelectionInterval(row,row);
								Main.this.groupPane.table.scrollRectToVisible(Main.this.groupPane.table.getCellRect(row, 0,false)); 
								Main.this.tabbedPane.setSelectedIndex(Main.this.tabbedPane.indexOfTab("Group"));
								return;
								}
							 Paper p=(Paper)Main.this.model.getPapers().getAgentByURI(s);
							 if(p!=null)
								{
								int row=Main.this.model.getPapers().indexOf(p);
								if(row==-1) return;
								Main.this.paperPane.table.getSelectionModel().setSelectionInterval(row,row);
								Main.this.paperPane.table.scrollRectToVisible(Main.this.paperPane.table.getCellRect(row, 0,false)); 
								Main.this.tabbedPane.setSelectedIndex(Main.this.tabbedPane.indexOfTab("Paper"));
								}
							URL url= e.getURL();
	                        if(url!=null) activateURL(url);
	                        }
	
	                }

					});
			this.bioArea.setEditable(false);
			indipane2.add(new JScrollPane(bioArea));
			
		
			this.relations= new JTable(new DefaultTableModel(new String[]{"Relation","With"},0)
					{
					private static final long serialVersionUID = 1L;
					public boolean isCellEditable(int row, int column)
						{
						return false;
						}
					});
			
			this.relations.setRowHeight(Image.SIZE);
			this.relations.setDefaultRenderer(Object.class,new RendererAdapter()
					{
					private static final long serialVersionUID = 1L;
		
					public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
						{
						Component c=super.getTableCellRendererComponent(table, value, isSelected, hasFocus,row, column);
						int i= table.convertColumnIndexToModel(column);
						if(i==1 && value instanceof Agent)
							{
							if( ((Agent)value).getType()==Agent.T_INDIVDIDUAL ||
							    ((Agent)value).getType()==Agent.T_GROUP)
								{
								setIcon(((AbstractPeople)value).getIcon());
								}
							setToolTipText(((Agent)value).getDescription());
							}
						else
							{
							setIcon(null);
							setToolTipText(null);
							}
						return c;
						}
					});
			this.relations.getSelectionModel().addListSelectionListener(new ListSelectionListener()
					{
					public void valueChanged(ListSelectionEvent e)
						{
						int row= AgentPane.this.relations.getSelectedRow();
						if(row==-1) return;
						Object value= AgentPane.this.relations.getValueAt(row,1);
						if(value==null) return;
						if(!( value instanceof Agent))
							{
							//
							}
						else if( ((Agent)value).getType()==Agent.T_INDIVDIDUAL)
							{
							row=Main.this.model.getIndividuals().indexOf((Individual)value);
							if(row==-1) return;
							Main.this.indiPane.table.getSelectionModel().setSelectionInterval(row,row);
							Main.this.indiPane.table.scrollRectToVisible(Main.this.indiPane.table.getCellRect(row, 0,false)); 
							}
						else if( ((Agent)value).getType()==Agent.T_GROUP)
							{
							row=Main.this.model.getGroups().indexOf((Group)value);
							if(row==-1) return;
							Main.this.groupPane.table.getSelectionModel().setSelectionInterval(row,row);
							Main.this.groupPane.table.scrollRectToVisible(Main.this.groupPane.table.getCellRect(row, 0,false)); 
							}
						else if( ((Agent)value).getType()==Agent.T_PAPER)
							{
							row=Main.this.model.getPapers().indexOf((Paper)value);
							if(row==-1) return;
							Main.this.paperPane.table.getSelectionModel().setSelectionInterval(row,row);
							Main.this.paperPane.table.scrollRectToVisible(Main.this.paperPane.table.getCellRect(row, 0,false)); 
							}
						}
					});
			
			this.relations.addMouseListener(new MouseAdapter()
					{
					public void mouseClicked(MouseEvent e)
						{
						if(e.getClickCount()<2) return;
						int row=AgentPane.this.relations.rowAtPoint(e.getPoint());
						if(row==-1) return;
						Object value = AgentPane.this.relations.getModel().getValueAt(row,1);
						if(!( value instanceof Agent))
							{
							//
							}
						else if( ((Agent)value).getType()==Agent.T_INDIVDIDUAL)
							{
							setHistory((Agent)value);
							setIndividualPane((Individual)value);
							Main.this.tabbedPane.setSelectedIndex(Main.this.tabbedPane.indexOfTab("Individual"));
							}
						else if( ((Agent)value).getType()==Agent.T_GROUP)
							{
							setHistory((Agent)value);
							setGroupPane((Group)value);
							Main.this.tabbedPane.setSelectedIndex(Main.this.tabbedPane.indexOfTab("Group"));
							}
						else if( ((Agent)value).getType()==Agent.T_PAPER)
							{
							setHistory((Agent)value);
							setPaperPane((Paper)value);
							Main.this.tabbedPane.setSelectedIndex(Main.this.tabbedPane.indexOfTab("Paper"));
							}
						}
					});
			
			indipane2.add(new JScrollPane(this.relations));
			}
	
		}
	}
