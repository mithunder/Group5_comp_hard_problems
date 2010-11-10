package com.github.mithunder.testing;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.github.mithunder.analysis.BranchBoundLazyTSP;
import com.github.mithunder.analysis.ImprovedLazyTSP;
import com.github.mithunder.analysis.LazyTSP;
import com.github.mithunder.node.BasicGraph;
import com.github.mithunder.node.Graph;
import com.github.mithunder.node.Node;
import com.github.mithunder.parser.Parser;
import com.github.mithunder.viewer.ViewerFrame;

public class AssignmentRunner {
	public static void main(String[] args) {
		
		if (args.length != 1) {
			System.out.println("Exactly one filename must be given.");
			System.exit(1);
		}
		
		BufferedReader reader = null;
		String fileName = null;
		int m, k;
		boolean runner;
		try {
			reader = new BufferedReader(new FileReader(new File(args[0])));
			String currentString = reader.readLine();
			while (currentString != null) {
				if (currentString.equals("") || currentString.startsWith("//")) {
					currentString = reader.readLine();
					continue;
				}
				String[] splitString = currentString.split(" ");
				fileName = splitString[0];
				m = Integer.parseInt(splitString[1]);
				k = Integer.parseInt(splitString[2]);
				runner = Integer.parseInt(splitString[3]) != 0;
				
				Parser parser = new Parser();

				List<Node> nodes = parser.parse("problems/" + fileName);
				
				Graph g = new BasicGraph(nodes);

				LazyTSP lazyTSP;
				if (runner) {
					lazyTSP = new ImprovedLazyTSP();
				}
				else {
					lazyTSP = new BranchBoundLazyTSP();
				}
				
				Collection<String> properties = new ArrayList<String>();
				
				long time = System.currentTimeMillis();
				List<Node> sequence = lazyTSP.getBestLazyTSPSequence(g, m, k, properties);
				long timeTaken = System.currentTimeMillis() - time;
				

				double sum = 0.0;
				Node previous = null;
				String info = fileName + "; m=" + m + ", k=" + k + ", al=" +
						(runner ? "depth" : "branch-bound") + ", time(ms): " + timeTaken;
				System.out.println(info);
				System.out.print("   ");
				for (Node node : sequence) {
					System.out.print(node.getId() + ", ");
					if (previous != null) {
						sum += g.getWeight(previous, node);
					}
					previous = node;
				}
				sum += g.getWeight(previous, g.getNodes().get(0));
				for (int i = 0; i < m; i++) {
					sum += g.getWeight(g.getNodes().get(i), g.getNodes().get(i+1));
				}
				System.out.println("Cycle length: " + sum);
//				System.out.println(sum);

				ViewerFrame.viewGraph(g, sequence, m, info);
				currentString = reader.readLine();
			}
		} catch (IOException e) {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
			e.printStackTrace();
			System.exit(1);
		}
		
		System.out.println("\n\nDone.");
	}
}
