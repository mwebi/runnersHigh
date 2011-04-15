package com.runnershigh;

public class Block extends RHDrawable {
	
	public Block(int left, int top, int right, int bottom) {
		super(left, bottom, 0, right-left, top-bottom);
		
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


