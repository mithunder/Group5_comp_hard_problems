package com.github.mithunder.analysis;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.github.mithunder.node.Graph;
import com.github.mithunder.node.Node;

public class BasicLazyTSP implements LazyTSP {

	@Override
	public List<Node> getLazyTSPSequence(Graph g, int m, int k, int B) {
		
		checkArguments(g, m, k, B);
		
		//First, visit all nodes except the m+1 first nodes.
		boolean[] visited = new boolean[g.getNodeCount()];
		for (int i = m+1; i < visited.length; i++) {
			visited[i] = false;
		}
		
		//Calcuate the weight for the m+1 first nodes.
		double weightSum = 0;
		
		Node previousNode = null;
		for (Node node : g.getMPlus1FirstNodes(m)) {
			if (previousNode != null) {
				weightSum += g.getWeight(previousNode, node);
			}
			previousNode = node;
		}
		
		final Node routeStart = previousNode;
		
		//Try every single combination in existence, until a good one has been found,
		//or nothing was found.
		return getSequence(visited, g, m, k, B, weightSum, routeStart);
	}

	@Override
	public List<Node> getLazyTSPSequence(Graph g, int m, int k, int B,
			Collection<String> properties) {
		return getLazyTSPSequence(g, m, k, B);
	}
	
	private void checkArguments(Graph g, int m, int k, int B) {
		if (k < 1 || m < 1 || B < 1) {
			throw new IllegalArgumentException("(k, m, B) must be natural numbers (1+): ("
					+ k + ", " + m + ", " + B + ").");
		}
		
		final int invarNKM2 = g.getNodeCount() - k - m;
		if (invarNKM2 < 2) {
			throw new IllegalArgumentException(
					"The invariant n - k - m >= 2 must hold: " + invarNKM2
			);
		}
	}
	
	private List<Node> getSequence(boolean[] visited, Graph g, int m, int k, int B, double weightSum, Node routeStart) {
		
		Iterator<Node> ite = g.getNeighbourIterator(routeStart);
		while (ite.hasNext()) {
			Node node = ite.next();
			if (node != routeStart && !visited[node.getId()-1]) {
				//A candidate for a new path.
				List<Node> sequence = new ArrayList<Node>();
				sequence.add(routeStart);
				visited[node.getId()-1] = true;
				List<Node> nodes = getSequence(visited, g, m, k, B,
					weightSum + g.getWeight(routeStart, node),
					sequence
				);
				if (nodes != null) {
					return nodes;
				}
				visited[node.getId()-1] = false;
			}
		}
		
		return null;
	}
	
	private List<Node> getSequence(boolean[] visited, Graph g, int m, int k, int B, double weightSum,
			List<Node> currentSequence) {
		
		if (currentSequence.size() == g.getNodeCount() - k - m) {
			if (weightSum +
					g.getWeight(
						currentSequence.get(currentSequence.size()-1),
						g.getMPlus1FirstNodes(m).get(0)
					)
				<= B) {
				return currentSequence;
			}
			return null;
		}
		
		Node routeCurrent = currentSequence.get(0);
		Iterator<Node> ite = g.getNeighbourIterator(routeCurrent);
		while (ite.hasNext()) {
			Node node = ite.next();
			if (node != routeCurrent && !visited[node.getId()-1]) {
				//A candidate for a new path.
				currentSequence.add(routeCurrent);
				visited[node.getId()-1] = true;
				List<Node> nodes = getSequence(visited, g, m, k, B, 
					weightSum + g.getWeight(routeCurrent, node),
					currentSequence
				);
				if (nodes != null) {
					return nodes;
				}
				visited[node.getId()-1] = false;
				currentSequence.remove(currentSequence.size()-1);
			}
		}
		
		return null;
	}
}
