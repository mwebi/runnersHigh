package com.runnershigh;

public class Block extends Mesh {
	public int left;
	public int right;
	public int bottom;
	public int top;
	
	public int getLeft() {
		return left;
	}

	public void setLeft(int left) {
		this.left = left;
		
		float[] vertices = new float[] { 0, 0, 0, right-left, 0, 0.0f, 0, top-bottom,
				0.0f, right-left, top-bottom, 0.0f };

		setVertices(vertices);
	}

	public int getRight() {
		return right;
	}

	public void setRight(int right) {
		this.right = right;
		
		float[] vertices = new float[] { 0, 0, 0, right-left, 0, 0.0f, 0, top-bottom,
				0.0f, right-left, top-bottom, 0.0f };

		setVertices(vertices);
	}

	public int getBottom() {
		return bottom;
	}

	public void setBottom(int bottom) {
		this.bottom = bottom;
		float[] vertices = new float[] { 0, 0, 0, right-left, 0, 0.0f, 0, top-bottom,
				0.0f, right-left, top-bottom, 0.0f };

		setVertices(vertices);
	}

	public int getTop() {
		return top;
	}

	public void setTop(int top) {
		this.top = top;
		float[] vertices = new float[] { 0, 0, 0, right-left, 0, 0.0f, 0, top-bottom,
				0.0f, right-left, top-bottom, 0.0f };

		setVertices(vertices);
	}

	public Block(int _left, int _top, int _right, int _bottom) {
		left = _left;
		top = _top;
		right = _right;
		bottom = _bottom;
		
		float textureCoordinates[] = { 0.0f, 1.0f, //
				1.0f, 1.0f, //
				0.0f, 0.0f, //
				1.0f, 0.0f, //
		};

		short[] indices = new short[] { 0, 1, 2, 1, 3, 2 };

		float[] vertices = new float[] { 0, 0, 0, right-left, 0, 0.0f, 0, top-bottom,
				0.0f, right-left, top-bottom, 0.0f };

		setIndices(indices);
		setVertices(vertices);
		setTextureCoordinates(textureCoordinates);
	}
	
}


