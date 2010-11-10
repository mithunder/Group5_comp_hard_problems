package com.github.mithunder.analysis;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

import com.github.mithunder.node.BasicGraph;
import com.github.mithunder.node.Graph;
import com.github.mithunder.node.Node;

public class BranchBoundLazyTSP implements LazyTSP {
	
	private double upperBound;
	private int sequenceLength;
	private List<Pair<Double, Node>> node2LeastEdgesHalfsum;
	private int maxId;
	private boolean quickBound;

	public void clear() {
		upperBound = Double.MAX_VALUE;
		sequenceLength = 0;
		node2LeastEdgesHalfsum = null;
		maxId = -1;
		quickBound = false;
	}

	public BranchBoundLazyTSP() {
		clear();
	}

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
	
	private List<Node> setupAnalysis(Graph g1, int m, int k, int B,
			Collection<String> properties, boolean isOptimization) {
		
		for (String str : properties) {
			if (str.equals("quickBound")) {
				quickBound = true;
			}
		}
		
		//Clear.
		clear();
		
		//Check arguments.
		Checker.checkArguments(g1, m, k, B);
		
		//Max id.
		maxId = g1.getNodeCount();
		
		//Strip the graph of the 2..m nodes.
		final Pair<Graph, Double> gb = stripGraph(g1, m);
		final Graph strippedG = gb.getT1();
		final double bWeightSum = gb.getT2();

		
		//Calculating the size of the sequence.
		//Having stripped m-1 nodes from the graph,
		//it now contains n' = n - (m-1) = n - m + 1 nodes.
		//The sequence should hold n - m - k = a nodes,
		//which gives n' = a + k + 1 <=> a = n' - k - 1
		//So, the number of nodes in the sequence is:
		sequenceLength = strippedG.getNodeCount() - k - 1;
		
		calculateNode2LeastEdgesHalfsum(strippedG);
		
		if (!isOptimization) {
			upperBound = B-bWeightSum;
		}

		return analyse(strippedG, isOptimization);
	}
	
	private void calculateNode2LeastEdgesHalfsum(Graph g) {
		
		//Go through each node, and calculate the sum.
		node2LeastEdgesHalfsum = new ArrayList<Pair<Double,Node>>();
		for (Node node : g.getNodes()) {
			Node n1 = null, n2 = null;
			double d1 = Double.MAX_VALUE, d2 = Double.MAX_VALUE;
			
			for (Node neighbour : g.getNodes()) {
				if (node == neighbour) {
					continue;
				}
				if (n1 == null) {n1 = neighbour; d1 = g.getWeight(node, neighbour); continue;}
				if (n2 == null) {n2 = neighbour; d2 = g.getWeight(node, neighbour); continue;}
				final double weight = g.getWeight(node, neighbour);
				if (d1 > weight) {n1 = neighbour; d1 = weight; continue;}
				if (d2 > weight) {n2 = neighbour; d2 = weight; continue;}
			}
			
			node2LeastEdgesHalfsum.add(new Pair<Double, Node>((d1 + d2)/2.0, node));
		}
		
		//Sort them, so those with the lowest bounds are tried first.
		Collections.sort(node2LeastEdgesHalfsum, new Comparator<Pair<Double,Node>>() {
			public int compare(Pair<Double,Node> o1, Pair<Double,Node> o2) {
				if (o1.getT1() < o2.getT1()) {
					return -1;
				}
				if (o1.getT1() > o2.getT1()) {
					return 1;
				}
				return 0;
			}
		});
	}

	private double computeLowerbound(Graph g, List<Node> path, double length) {
		
		//For the number of nodes required in the sequence, minus those in the path,
		//add those nodes with the least 2-least-edge-halfsum which does not appear
		//in the path.
		
		if (quickBound) {
			return length + g.getWeight(g.getNodes().get(0), path.get(path.size()-1));
		}
		
		boolean[] insideThePath = new boolean[maxId];
		//Do not care about the nodes in the path.
		for (Node node : path) {
			insideThePath[node.getId()-1] = true;
		}

		double sum = 0.0;
		int count = 0;
		for (Pair<Double, Node> halfsumNode : node2LeastEdgesHalfsum) {
			if (!insideThePath[halfsumNode.getT2().getId()-1]) {
				sum += halfsumNode.getT1();
				count++;
				if (count == sequenceLength - path.size()+1) {
					break;
				}
			}
		}
		
		return length + Math.max(sum, g.getWeight(g.getNodes().get(0), path.get(path.size()-1)));
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
	private List<Node> analyse(final Graph g,
			final boolean isOptimization) {

		final Node startNode = g.getNodes().get(0);
		final Node firstPathNode = g.getNodes().get(1);
		Path bestPath = null;
		
		PriorityQueue<Path> pathQueue = new PriorityQueue<Path>();
		
		//We start with the first node.
		pathQueue.add(new Path(g, firstPathNode));
		
		while (!pathQueue.isEmpty()) {
			//Poll the most promising path.
			Path currentPath = pathQueue.poll();
			
			//Now, for each path popped, check that the lower bound is less than the global upper bound.
			if (currentPath.getLowerBound() > upperBound) {
				continue;
			}
			
			//Go through each node, and consider it as a candidate for the next path.
			for (Node neighbour : g.getNodes()) {
				//We don't want to add nodes we already have looked at.
				if (currentPath.hasNode(neighbour) || startNode == neighbour) {
					continue;
				}
				
				//Add a new path, based on this neighbour.
				Path newPath = new Path(g, currentPath, neighbour);
				
				//Check for end.
				if (newPath.getPathCount() == sequenceLength) {
					//Check for getting a new upper bound and a new best path candidate.
					final double pathLength = newPath.getLength() + g.getWeight(newPath.getLast(), startNode);
					if (pathLength <= upperBound) {
						if (!isOptimization) {
							return newPath.getPath();
						}
						bestPath = newPath;
						upperBound = pathLength;
					}
				}
				//If we aren't at the end, see if the new path is promising.
				else if (newPath.getLowerBound() <= upperBound) {
					pathQueue.add(newPath);
				}
			}
		}
		
		if (bestPath != null && isOptimization) {
			return bestPath.getPath();
		}
		return null;
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
	
	private class Path implements Comparable<Path> {
		private final List<Node> path;
		private final double lowerBound;
		private final double length;
		
		public Path(Graph g, Node node) {
			path = new ArrayList<Node>();
			path.add(node);
			length = 0.0;
			lowerBound = computeLowerbound(g, path, length);
		}
		
		public Node getLast() {
			return path.get(path.size()-1);
		}

		public Path(Graph g, Path otherPath, Node node) {
			path = new ArrayList<Node>(otherPath.getPath());
			path.add(node);
			length = otherPath.getLength() + g.getWeight(path.get(path.size()-2), node);
			lowerBound = computeLowerbound(g, path, length);
		}

		public boolean hasNode(Node someNode) {
			for (Node node : path) {
				if (node == someNode) {
					return true;
				}
			}
			return false;
		}

		public int compareTo(Path path) {
			
			if (lowerBound < path.getLowerBound()) {
				return -1;
			}
			else if (lowerBound > path.getLowerBound()) {
				return 1;
			}
			return 0;
		}
		
		public final double getLowerBound() {
			return lowerBound;
		}
		
		public List<Node> getPath() {
			return path;
		}
		
		public int getPathCount() {
			return path.size();
		}
		
		public double getLength() {
			return length;
		}
	}
}
