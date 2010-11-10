package com.github.mithunder.analysis;

import java.util.Collection;
import java.util.List;

import com.github.mithunder.node.Graph;
import com.github.mithunder.node.Node;

public interface LazyTSP {

	/**
	 * @param g
	 * @param m
	 * @param k
	 * @param B
	 * @return Null if no sequence exists, or a list indicating the sequence (not the cycle).
	 */
	public List<Node> getLazyTSPSequence(Graph g, int m, int k, int B, Collection<String> properties);
	
	/**
	 * The optimizing version. May be unsupported.
	 */
	public List<Node> getBestLazyTSPSequence(Graph g, int m, int k, Collection<String> properties);
}
