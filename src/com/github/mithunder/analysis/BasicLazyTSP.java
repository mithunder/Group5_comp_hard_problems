package com.github.mithunder.analysis;

import java.util.Collection;

import com.github.mithunder.node.Graph;
import com.github.mithunder.node.Node;

public class BasicLazyTSP implements LazyTSP {

	@Override
	public Collection<Node> getLazyTSPSequence(Graph g, int m, int k, int B) {
		
		
		
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<Node> getLazyTSPSequence(Graph g, int m, int k, int B,
			Collection<String> properties) {
		return getLazyTSPSequence(g, m, k, B);
	}
}
