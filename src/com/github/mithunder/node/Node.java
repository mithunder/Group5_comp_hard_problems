package com.github.mithunder.node;

public class Node {

	private final int ID;
	private final double X_CORD;
	private final double Y_CORD;

	public Node(int id, double x, double y) {
		ID = id;
		X_CORD = x;
		Y_CORD = y;
	}

	public final int getId() {
		return ID;
	}

	public final double getX() {
		return X_CORD;
	}

	public final double getY() {
		return Y_CORD;
	}
}
