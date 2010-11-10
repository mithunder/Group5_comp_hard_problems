package com.github.mithunder.analysis;

import com.github.mithunder.node.Graph;

public abstract class Checker {

	public static void checkArguments(Graph g, int m, int k, int B) {
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
}
