package com.github.mithunder.node;

public class Node {

	private final int ID;
	private final int X_CORD;
	private final int Y_CORD;

	public Node(int id, int x, int y) {
		ID = id;
		X_CORD = x;
		Y_CORD = y;
	}

	public int getId() {
		return ID;
	}

	public int getX() {
		return X_CORD;
	}

	public int getY() {
		return Y_CORD;
	}
}
