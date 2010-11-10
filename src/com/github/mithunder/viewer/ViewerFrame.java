package com.github.mithunder.viewer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.Iterator;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.github.mithunder.node.Graph;
import com.github.mithunder.node.Node;


public class ViewerFrame {
	
	public static void viewGraph(Graph graph, List<Node> nodes, int m, String info) {
		JFrame jFrame = new JFrame();
		
		final GraphComponent gC = new GraphComponent(graph, nodes, m);
		gC.addComponentListener(gC);
		gC.addMouseMotionListener(gC);
		jFrame.add(gC);
		jFrame.setLocationByPlatform(true);
		
		jFrame.setSize(new Dimension(400, 400));
		jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jFrame.setTitle(info);
		
		//Set size.
		final JTextField te = new JTextField();
		te.setSize(new Dimension(100, 20));
		te.setMinimumSize(new Dimension(100, 20));
		final JButton button = new JButton("Set node size");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				try {
					gC.setNodeSize(Integer.parseInt(te.getText()));
				} catch (NumberFormatException e) {
					e.printStackTrace();
				}
			}
		});
		
		//See id.
		JLabel idLabel = new JLabel();
		gC.setLabel(idLabel);
		idLabel.setMinimumSize(new Dimension(40, 20));
		
		JPanel southP = new JPanel();
		southP.setLayout(new BoxLayout(southP, BoxLayout.X_AXIS));
		southP.add(te);
		southP.add(button);
		southP.add(idLabel);
		jFrame.add(southP, BorderLayout.SOUTH);
		
		jFrame.setVisible(true);
	}
}

class GraphComponent extends JComponent implements ComponentListener, MouseMotionListener {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4779743845607251731L;
	private Graph graph;
	private double zoom = 1.0;
	private final int startX, startY;
	private final int gWidth, gHeight;
	private int nodeSize = 3;
	private final List<Node> nodes;
	private final int m;
	private Node selectedNode = null;
	private double mCoorX, mCoorY;
	private JLabel idLabel;
	
	public GraphComponent(Graph graph, List<Node> nodes, int m) {
		this.graph = graph;
		Rectangle bu = graph.getBounds();
		startX = bu.x;
		startY = bu.y;
		gWidth = bu.width;
		gHeight = bu.height;
		this.nodes = nodes;
		this.m = m;
	}
	
	public void setLabel(JLabel idLabel) {
		this.idLabel = idLabel;
	}

	public Node getSelectedNode() {
		return selectedNode;
	}
	
	public void setNodeSize(int size) {
		this.nodeSize = size;
		repaint();
	}
	
	public void paintComponent(Graphics gr) {
		super.paintComponent(gr);
		
		Color c = gr.getColor();
		
		Graphics2D g2 = (Graphics2D) gr;
		g2.scale(zoom, zoom);
		g2.translate(-startX, -startY);
		
		//Draw nodes.
		g2.setColor(Color.blue);
		{
			for (Node n : graph.getNodes()) {
				g2.fillOval((int)n.getX() - nodeSize/2, (int)n.getY() - nodeSize/2, nodeSize, nodeSize);
			}
		}
		
		//Draw chosen path edges.
		g2.setColor(Color.green);
		if (nodes != null) {
			Iterator<Node> ite = nodes.iterator();
			if (ite.hasNext()) {
				Node previous = ite.next();
				while (ite.hasNext()) {
					Node n = ite.next();
					g2.drawLine((int)previous.getX(), (int)previous.getY(), (int)n.getX(), (int)n.getY());
					previous = n;
				}
			}
		}
		
		//Draw m+1 edges.
		g2.setColor(Color.red);
		if (nodes != null) {
			
			Node previous = null;
			int i = 0;
			for (Node n : graph.getNodes()) {
				if (previous != null) {
					g2.drawLine((int)previous.getX(), (int)previous.getY(), (int)n.getX(), (int)n.getY());
				}
				previous = n;
				i++;
				if (i == m+1) {
					break;
				}
			}
		}
		
		g2.setColor(Color.black);
		if (selectedNode != null) {
			g2.drawLine((int)mCoorX, (int)mCoorY, (int)selectedNode.getX(), (int)selectedNode.getY());
		}
		
		gr.setColor(c);
	}
	
	public void componentResized(ComponentEvent ce) {
		zoom = Math.min(1.0*getSize().width/gWidth, 1.0*getSize().height/gHeight)*0.9;
	}

	//Do nothing.
	public void componentShown(ComponentEvent e) {}
	public void componentHidden(ComponentEvent e) {}
	public void componentMoved(ComponentEvent e) {}

	@Override
	public void mouseDragged(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseMoved(MouseEvent me) {
		mCoorX = me.getX()/zoom + startX;
		mCoorY = me.getY()/zoom + startY;
		double dist = Double.MAX_VALUE;
		double testDist = 0.0;
		Node closest = null;
		
		for (Node n : graph.getNodes()) {
			testDist = getDist(mCoorX, mCoorY, n.getX(), n.getY());
			if (testDist < dist) {
				dist = testDist;
				closest = n;
			}
		}
		selectedNode = closest;
		idLabel.setText("" + selectedNode.getId());
		repaint();
	}
	
	private final double getDist(double x, double y, double xE, double yE) {
		return Math.sqrt(square(x-xE) + square(y-yE));
	}
	
	private final double square(double x) {
		return x*x;
	}
}
