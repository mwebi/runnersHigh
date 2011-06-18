package com.runnershigh;

import android.graphics.Rect;
import android.util.Log;

public class CounterDigit extends Mesh {
	protected float width;
	protected float height;
	protected int digitValue;
	protected float textureCoordinates[] = {0.0f,0.0f,0.0f,0.0f,0.0f,0.0f,0.0f,0.0f};
	
	public CounterDigit(int _x, int _y, int _z, int _width, int _height) {
		x = _x;
		y = _y;
		z = _z;
		
		width= _width;
		height= _height;
		
		digitValue=0;
		
		float textureCoordinates[] = { 0.0f, 1.0f, //
				0.0625f, 1.0f, //
				0.0f, 0.0f, //
				0.0625f, 0.0f, //
		};

		short[] indices = new short[] { 0, 1, 2, 1, 3, 2 };

		float[] vertices = new float[] { 0, 0, 0, width, 0, 0.0f, 0, height,
				0.0f, width, height, 0.0f };

		setIndices(indices);
		setVertices(vertices);
		setTextureCoordinates(textureCoordinates);
	}
	
	public Rect getRect() {
		return new Rect((int)x,(int)(y+height),(int)(x+width),(int)y);
	}
	
	public void setWidth(int width)
	{
		this.width = width;
		
		float[] vertices = new float[] { 0, 0, 0, width, 0, 0.0f, 0, height,
				0.0f, width, height, 0.0f };

		setVertices(vertices);
	}
	
	public void setHeight(int height)
	{
		this.height = height;
		float[] vertices = new float[] { 0, 0, 0, width, 0, 0.0f, 0, height,
				0.0f, width, height, 0.0f };

		setVertices(vertices);
	}
	public void incrementDigit() {
		digitValue++;
		if(digitValue==10)
			digitValue=0;
		
		float textureCoordinates[] = {0.0625f*digitValue, 1.0f, //
				0.0625f*digitValue+0.0625f, 1.0f, //
				0.0625f*digitValue, 0.0f, //
				0.0625f*digitValue+0.0625f, 0.0f, //
		};
		setTextureCoordinates(textureCoordinates);
	}
	public void setDigitToZero() {
		digitValue=0;
		float textureCoordinates[] = { 0.0f, 1.0f, //
				0.0625f, 1.0f, //
				0.0f, 0.0f, //
				0.0625f, 0.0f, //
		};
		setTextureCoordinates(textureCoordinates);
	}
	public void setDigitTo(int value) {
		digitValue=value;
		
		textureCoordinates[0] = 0.0625f*digitValue;
		textureCoordinates[1] = 1.0f;
		textureCoordinates[2] = 0.0625f*digitValue+0.0625f;
		textureCoordinates[3] = 1.0f;
		textureCoordinates[4] = 0.0625f*digitValue;
		textureCoordinates[5] = 0.0f;
		textureCoordinates[6] = 0.0625f*digitValue+0.0625f;
		textureCoordinates[7] = 0.0f;
		
		setTextureCoordinates(textureCoordinates);
		
	}
}
