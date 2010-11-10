package com.github.mithunder.node;

import java.awt.Rectangle;
import java.util.List;

public interface Graph {
	
	public List<Node> getMPlus1FirstNodes(int m);
	
	public int getNodeCount();
	
	public double getWeight(Node node1, Node node2);
	
	public List<Node> getNodes();
	
	public Rectangle getBounds();
}
