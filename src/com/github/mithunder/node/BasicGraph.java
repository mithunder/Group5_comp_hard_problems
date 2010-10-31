package com.github.mithunder.node;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class BasicGraph implements Graph {
	
	private final List<Node> nodes = new ArrayList<Node>();
	
	/**
	 * @param nodes Nodes in the correct order.
	 */
	public BasicGraph(List<Node> nodes) {
		nodes.addAll(nodes);
	}

	public Iterator<Node> getNeighbourIterator(Node node) {
		return nodes.iterator();
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
	public double getWeight(Node node1, Node node2) {
		return Math.sqrt(square(node1.getX() - node2.getX()) + square(node1.getY() - node2.getY()));
	}
	
	private final int square(int x) {
		return x*x;
	}
}
