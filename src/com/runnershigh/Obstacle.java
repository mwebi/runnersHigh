package com.runnershigh;

import android.R.drawable;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;

public class Obstacle extends RHDrawable {
	private Rect ObstacleRect;
	private char ObstacleType; //s=slow, 
	
	
	public Obstacle(float _x, float _y, float _z, float _width, float _height, char type){
		x =_x;
		y =_y;
		width = _width;
		height = _height;
		ObstacleType=type;
		ObstacleRect = new Rect (x, y, x+width, y+height);
		
		float textureCoordinates[] = { 0.0f, 1.0f, //
				1.0f, 1.0f, //
				0.0f, 0.0f, //
				1.0f, 0.0f, //
		};

		short[] indices = new short[] { 0, 1, 2, 1, 3, 2 };

		float[] vertices = new float[] { 0, 0, 0, width, 0, 0.0f, 0, height,
				0.0f, width, height, 0.0f };

		setIndices(indices);
		setVertices(vertices);
		setTextureCoordinates(textureCoordinates);
	}
	public Obstacle(Obstacle GivenObstacle){
		x = GivenObstacle.getX();
		y = GivenObstacle.getY();
		width = GivenObstacle.getWidth();
		height = GivenObstacle.getHeight();
		ObstacleRect = GivenObstacle.getObstacleRect();
		ObstacleType = GivenObstacle.getType();
		
		float textureCoordinates[] = { 0.0f, 1.0f, //
				1.0f, 1.0f, //
				0.0f, 0.0f, //
				1.0f, 0.0f, //
		};

		short[] indices = new short[] { 0, 1, 2, 1, 3, 2 };

		float[] vertices = new float[] { 0, 0, 0, width, 0, 0.0f, 0, height,
				0.0f, width, height, 0.0f };

		setIndices(indices);
		setVertices(vertices);
		setTextureCoordinates(textureCoordinates);
	}
	public int getWidth(){
		return width;
	}
	public int getHeight(){
		return height;
	}
	public char getType(){
		return ObstacleType;
	}
	public void setType(char type){
		ObstacleType = type;
	}
	
	public float getX(){
		return x;
	}
	public float getY(){
		return y;
	}
	public void setX(int _x){
		x=_x;
	}
	public void setY(int _y){
		y=_y;
	}
	public void setObstacleRect(int l, int r, int top, int bottom){
		ObstacleRect.left=l;
		ObstacleRect.right=r;
		ObstacleRect.top=top;
		ObstacleRect.bottom=bottom;
	}
	public void setObstacleRectRight(int r){
		ObstacleRect.right=r;
	}
	public Rect getObstacleRect(){
		return ObstacleRect;
	}
	public void updateObstacleRect(int levelPosition){
		ObstacleRect.left -= levelPosition;
		ObstacleRect.right -= levelPosition;
	}
	public boolean isClicked(float clickX, float clickY){
		if(clickX <= x+width && clickX > x){
			if(clickY <= y+height && clickY > y){
				return true;
			}
		}
		return false;
	}
}
