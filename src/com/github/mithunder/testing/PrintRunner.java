package com.github.mithunder.testing;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.github.mithunder.analysis.ImprovedLazyTSP;
import com.github.mithunder.analysis.LazyTSP;
import com.github.mithunder.node.BasicGraph;
import com.github.mithunder.node.Graph;
import com.github.mithunder.node.Node;
import com.github.mithunder.parser.Parser;
import com.github.mithunder.viewer.ViewerFrame;

public class PrintRunner {

	public static void main(String[] args) {
		
		System.out.println("Starting...");
		
		Parser parser = new Parser();

		List<Node> nodes = parser.parse("problems/pla85900.tsp");
		final int m = 10, k = 85887;
//		List<Node> nodes = parser.parse("problems/usa13509.tsp");
//		List<Node> nodes = parser.parse("problems/fl3795.tsp");
		
//		List<Node> nodes = parser.parse("problems/vm1084.tsp");
//		final int m = 10, k = 1064;
		
//		List<Node> nodes = parser.parse("problems/eil51.tsp");
//		final int m = 10, k = 10;
		
		Graph g = new BasicGraph(nodes);

		LazyTSP lazyTSP = new ImprovedLazyTSP();
//		LazyTSP lazyTSP = new BranchBoundLazyTSP();
		
		Collection<String> properties = new ArrayList<String>();
		
//		List<Node> sequence = lazyTSP.getLazyTSPSequence(g, m, k, 321, properties);
		List<Node> sequence = lazyTSP.getBestLazyTSPSequence(g, m, k, properties);
		if (sequence != null) {
			System.out.println("Found a sequence!");
			double sum = 0.0;
			Node previous = null;
			for (Node node : sequence) {
				System.out.print(node.getId() + ", ");
				if (previous != null) {
					sum += g.getWeight(previous, node);
				}
				previous = node;
			}
			sum += g.getWeight(previous, g.getNodes().get(0));
			System.out.println("Sequence length: " + sum);
		}
		else {
			System.out.println("Did not find a sequence");
		}
		
		ViewerFrame.viewGraph(g, sequence, m, "");
	}
}
