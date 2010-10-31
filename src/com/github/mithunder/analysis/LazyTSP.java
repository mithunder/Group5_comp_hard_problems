package com.github.mithunder.analysis;

import java.util.Collection;

import com.github.mithunder.node.Graph;
import com.github.mithunder.node.Node;

public interface LazyTSP {

	public Collection<Node> getLazyTSPSequence(Graph g, int m, int k, int B);
	

	public Collection<Node> getLazyTSPSequence(Graph g, int m, int k, int B, Collection<String> properties);
}
