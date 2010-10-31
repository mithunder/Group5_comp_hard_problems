package com.github.mithunder.node;

import java.util.Iterator;
import java.util.List;

public interface Graph {
	
	public List<Node> getMPlus1FirstNodes(int m);

	public Iterator<Node> getNeighbourIterator(Node node);
}