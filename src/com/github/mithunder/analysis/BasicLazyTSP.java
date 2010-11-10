package com.github.mithunder.analysis;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.github.mithunder.node.Graph;
import com.github.mithunder.node.Node;

public class BasicLazyTSP implements LazyTSP {

	@Override
	public List<Node> getLazyTSPSequence(Graph g, int m, int k, int B,
			Collection<String> properties) {
		
		Checker.checkArguments(g, m, k, B);
		
		//First, visit all nodes except the m+1 first nodes.
		boolean[] visited = new boolean[g.getNodeCount()];
		for (int i = 0; i < m+1; i++) {
			visited[i] = true;
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
	
	private List<Node> getSequence(boolean[] visited, Graph g, int m, int k, int B, double weightSum, Node routeStart) {
		
		for (Node node : g.getNodes()) {
			if (node.getId() != routeStart.getId() && !visited[node.getId()-1]) {
				//A candidate for a new path.
				List<Node> sequence = new ArrayList<Node>();
				sequence.add(routeStart);
				sequence.add(node);
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
		
		Node routeCurrent = currentSequence.get(currentSequence.size()-1);
		for (Node node : g.getNodes()) {
			if (node.getId() != routeCurrent.getId() && !visited[node.getId()-1]) {
				//A candidate for a new path.
				currentSequence.add(node);
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

	@Override
	public List<Node> getBestLazyTSPSequence(Graph g, int m, int k,
			Collection<String> properties) {
		throw new UnsupportedOperationException("This class does not support" +
				" the optimization version.");
	}
}
