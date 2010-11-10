package com.github.mithunder.analysis;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.mithunder.node.BasicGraph;
import com.github.mithunder.node.Graph;
import com.github.mithunder.node.Node;

public class ImprovedLazyTSP implements LazyTSP {
	
	@Override
	public List<Node> getLazyTSPSequence(Graph g, int m, int k, int B,
			Collection<String> properties) {
		return setupAnalysis(g, m, k, B, properties, false);
	}
	
	@Override
	public List<Node> getBestLazyTSPSequence(Graph g, int m, int k,
			Collection<String> properties) {
		return setupAnalysis(g, m, k, Integer.MAX_VALUE, properties, true);
	}
	
	private List<Node> setupAnalysis(Graph g, int m, int k, int B,
			Collection<String> properties, boolean isOptimization) {
		
		//Check arguments.
		Checker.checkArguments(g, m, k, B);
		
		//Max id.
		int maxId = g.getNodeCount();
		
		//Strip the graph of the 2..m nodes.
		final Pair<Graph, Double> gb = stripGraph(g, m);
		final Graph strippedG = gb.getT1();
		final double bWeightSum = gb.getT2();
		
		//Get neighbour list for each node.
		Map<Node, List<Node>> nodeToNeighbours = getUnsortedNeighbourList(strippedG);

		return calculate(strippedG, k, B-bWeightSum, nodeToNeighbours, maxId, isOptimization);
	}

	private List<Node> calculate(Graph g, int k, double B, Map<Node, List<Node>> nodeToNeighbours,
			int maxId, boolean isOptimization) {
		
		boolean[] visited = new boolean[maxId];
		//Do not try to visit the start node.
		visited[0] = true;
		//First node to visit.
		Node firstNode = g.getMPlus1FirstNodes(2).get(1);
		visited[firstNode.getId()-1] = true;
		List<Node> startList = new ArrayList<Node>();
		startList.add(firstNode);
		
		//Calculating the size of the sequence.
		//Having stripped m-1 nodes from the graph,
		//it now contains n' = n - (m-1) = n - m + 1 nodes.
		//The sequence should hold n - m - k = a nodes,
		//which gives n' = a + k + 1 <=> a = n' - k - 1
		//So, the number of nodes in the sequence is:
		final int sequenceLength = g.getNodeCount() - k - 1;
		
		Pair<List<Node>, Double> sequenceLimit = analyse(g, sequenceLength, B, firstNode, nodeToNeighbours,
				isOptimization, visited, startList, 0.0);
		
		if (sequenceLimit == null) {
			return null;
		}
		
		return sequenceLimit.getT1();
	}
	
	/**
	 * @param g
	 * @param k
	 * @param limit
	 * @param nodeToNeighbours
	 * @param isOptimization 
	 * @param visited 
	 * @return The best sequence found so far, and the best hard limit so far.
	 */
	private Pair<List<Node>, Double> analyse(final Graph g, final int sequenceLength, final double aLimit,
			final Node currentNode,
			final Map<Node, List<Node>> nodeToNeighbours, final boolean isOptimization, final boolean[] visited,
			final List<Node> sequence, final double currentlyUsedWeight) {
		
		double currentLimit = aLimit;
		
		//First, check that the distance to the start node is not too far.
		final Node startNode = g.getNodes().get(0);
		final double currentToStartWeight = g.getWeight(currentNode, startNode);
		if (currentlyUsedWeight + currentToStartWeight > currentLimit) {
			return null;
		}
		
		//Second, check if we are at the last node,
		if (sequence.size() == sequenceLength) {
			//Return the sequence and the new limit.
			return new Pair<List<Node>, Double>(
					new ArrayList<Node>(sequence), currentlyUsedWeight + currentToStartWeight
			);
		}
		
		Pair<List<Node>, Double> bestSequenceLimit = null;
		
		//Go through each neighbour, and try to find a sequence based on them.
		for (Node neighbour : nodeToNeighbours.get(currentNode)) {
			//First, check that we haven't visited this neighbour,
			//or that we aren't at the current node.
			if (visited[neighbour.getId()-1] || neighbour.getId() == currentNode.getId()) {
				continue;
			}
			
			//Second, check that we won't breach the current limit.
			double testingWeight = g.getWeight(currentNode, neighbour);
			if (currentlyUsedWeight + testingWeight + g.getWeight(neighbour, startNode) < currentLimit) {
				//This is a potential candidate.
				//Estimate a lower bound.
				
				
				//Try it out.
				visited[neighbour.getId()-1] = true;
				sequence.add(neighbour);
				Pair<List<Node>, Double> newSequenceLimit = analyse(
						g, sequenceLength, currentLimit, neighbour,
						nodeToNeighbours, isOptimization, visited, sequence,
						currentlyUsedWeight + testingWeight
				);
				
				//Was it usable?
				if (newSequenceLimit != null) {
					if (!isOptimization) {
						//A solution has been found!
						return newSequenceLimit;
					}
					else {
						//A potential solution has been found.
						//Check if it is better than the current solution.
						if (bestSequenceLimit == null
								|| bestSequenceLimit.getT2() > newSequenceLimit.getT2()) {
							bestSequenceLimit = newSequenceLimit;
							currentLimit = newSequenceLimit.getT2();
						}
						//Else we don't care.
					}
				}
				//Else we don't care.

				//Remember to clean up.
				sequence.remove(sequence.size()-1);
				visited[neighbour.getId()-1] = false;
			}
		}
		
		if (bestSequenceLimit == null) {
			return null;
		}
		
		return new Pair<List<Node>, Double>(
			bestSequenceLimit.getT1(), bestSequenceLimit.getT2()
		);
	}

	private final Pair<Graph, Double> stripGraph(Graph g, int m) {

		List<Node> newNodes = new ArrayList<Node>();
		int i = 1;
		double bWeightSum = 0.0;
		Node previousNode = null;
		for (Node currentNode : g.getNodes()) {
			if (i < 2) {
				newNodes.add(currentNode);
			}
			else if (i < m+2) {
				bWeightSum += g.getWeight(previousNode, currentNode);
				if (i == m+1) {
					newNodes.add(currentNode);
				}
			}
			else {
				newNodes.add(currentNode);
			}
			previousNode = currentNode;
			i++;
		}

		
		return new Pair<Graph, Double>(new BasicGraph(newNodes), bWeightSum);
	}
	
	private Map<Node, List<Node>> getUnsortedNeighbourList(Graph g) {
		ArrayList<Node> generalList = new ArrayList<Node>();
		Map<Node, List<Node>> sortedNeighbourList = new HashMap<Node, List<Node>>();
		//First, add all nodes.
		for (Node node : g.getNodes()) {
			generalList.add(node);
		}
		
		//Then, sort and copy for each node.
		for (Node currentNode : g.getNodes()) {
			sortedNeighbourList.put(currentNode, generalList);
		}
		return sortedNeighbourList;
	}
}
