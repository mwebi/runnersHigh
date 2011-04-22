package com.runnershigh;

import android.graphics.Rect;
import android.util.Log;

public class PlayerSprite extends Mesh {
	private float width;
	private float height;
	private int currentFrame;
	private long LastFrameChangeTime;
	private int FrameUpdateTime;
	private float numberOfFrames;
	private float textureWidthOfOneFrame;
	
	public PlayerSprite(float _x, float _y, float _z, int _width, int _height, int _FrameUpdateTime, int _numberOfFrames) {
		x = _x;
		y = _y;
		z = _z;
		
		width= _width;
		height= _height;
		
		currentFrame=0;
		FrameUpdateTime = _FrameUpdateTime;
		numberOfFrames = _numberOfFrames;
		textureWidthOfOneFrame = 1 / numberOfFrames;
		
		float textureCoordinates[] = { 0.0f, 1.0f, //
				textureWidthOfOneFrame, 1.0f, //
				0.0f, 0.0f, //
				textureWidthOfOneFrame, 0.0f, //
		};

		short[] indices = new short[] { 0, 1, 2, 1, 3, 2 };

		float[] vertices = new float[] { 0, 0, 0, width, 0, 0.0f, 0, height,
				0.0f, width, height, 0.0f };

		setIndices(indices);
		setVertices(vertices);
		setTextureCoordinates(textureCoordinates);
		
		LastFrameChangeTime = System.currentTimeMillis();
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
	public void updatePosition(float _x, float _y)
	{
		x = _x;
		y = _y;
	}
	public void setFrameUpdateTime(float _FrameUpdateTime)
	{
		FrameUpdateTime = (int)_FrameUpdateTime;
	}
	public void tryToSetNextFrame() {
		if(  System.currentTimeMillis() > (LastFrameChangeTime+FrameUpdateTime) ){
			LastFrameChangeTime=System.currentTimeMillis();
			currentFrame++;
			if(currentFrame==numberOfFrames)
				currentFrame=0;
			float textureCoordinates[] = {textureWidthOfOneFrame*currentFrame, 1.0f, //
					textureWidthOfOneFrame*currentFrame+textureWidthOfOneFrame, 1.0f, //
					textureWidthOfOneFrame*currentFrame, 0.0f, //
					textureWidthOfOneFrame*currentFrame+textureWidthOfOneFrame, 0.0f, //
			};
			setTextureCoordinates(textureCoordinates);
		}
	}
}
