package com.github.mithunder.node;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

public class BasicGraph implements Graph {
	
	private final List<Node> nodes = new ArrayList<Node>();
	private final Rectangle bounds;
	
	/**
	 * @param nodes Nodes in the correct order.
	 */
	public BasicGraph(List<Node> someNodes) {
		double minX = 0.0, minY = 0.0, maxX = 0.0, maxY = 0.0;
		for (Node node : someNodes) {
			nodes.add(node);
			if (node.getX() < minX) {minX = node.getX();}
			if (node.getY() < minY) {minY = node.getY();}
			if (node.getX() > maxX) {maxX = node.getX();}
			if (node.getY() > maxY) {maxY = node.getY();}
		}
		bounds = new Rectangle((int)minX, (int)minY, (int)(maxX-minX), (int)(maxY-minY));
	}

	@Override
	public List<Node> getMPlus1FirstNodes(int m) {
		return nodes.subList(0, m+1);
	}

	@Override
	public int getNodeCount() {
		return nodes.size(); 
	}

	@Override
	public final double getWeight(Node node1, Node node2) {
		return Math.sqrt(square(node1.getX() - node2.getX()) + square(node1.getY() - node2.getY()));
	}
	
	private final double square(double x) {
		return x*x;
	}

	@Override
	public List<Node> getNodes() {
		return nodes;
	}

	@Override
	public Rectangle getBounds() {
		return bounds;
	}
}
