package com.runnershigh;

import android.R.drawable;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;

public class Obstacle extends Mesh {
	private int ObstacleX = 0;
	private int ObstacleY = 0;
	private int ObstacleWidth = 0;
	private int ObstacleHeight = 0;
	private Rect ObstacleRect;
	private char ObstacleType; //s=slow, 
	
	
	public Obstacle(int x, int y, int w, int h, char type){
		ObstacleX=x;
		ObstacleY=y;
		ObstacleWidth=w;
		ObstacleHeight=h;
		ObstacleType=type;
		ObstacleRect = new Rect (ObstacleX, ObstacleY, ObstacleX+ObstacleWidth, ObstacleY+ObstacleHeight);
		
		float textureCoordinates[] = { 0.0f, 1.0f, //
				1.0f, 1.0f, //
				0.0f, 0.0f, //
				1.0f, 0.0f, //
		};

		short[] indices = new short[] { 0, 1, 2, 1, 3, 2 };

		float[] vertices = new float[] { 0, 0, 0, w, 0, 0.0f, 0, h,
				0.0f, w, h, 0.0f };

		setIndices(indices);
		setVertices(vertices);
		setTextureCoordinates(textureCoordinates);
	}
	public Obstacle(Obstacle GivenObstacle){
		ObstacleX = GivenObstacle.getX();
		ObstacleY = GivenObstacle.getY();
		ObstacleWidth = GivenObstacle.getWidth();
		ObstacleHeight = GivenObstacle.getHeight();
		ObstacleRect = GivenObstacle.getObstacleRect();
		ObstacleType = GivenObstacle.getType();
		
		float textureCoordinates[] = { 0.0f, 1.0f, //
				1.0f, 1.0f, //
				0.0f, 0.0f, //
				1.0f, 0.0f, //
		};

		short[] indices = new short[] { 0, 1, 2, 1, 3, 2 };

		float[] vertices = new float[] { 0, 0, 0, ObstacleWidth, 0, 0.0f, 0, ObstacleHeight,
				0.0f, ObstacleWidth, ObstacleHeight, 0.0f };

		setIndices(indices);
		setVertices(vertices);
		setTextureCoordinates(textureCoordinates);
	}
	public int getWidth(){
		return ObstacleWidth;
	}
	public int getHeight(){
		return ObstacleHeight;
	}
	public char getType(){
		return ObstacleType;
	}
	public void setType(char type){
		ObstacleType = type;
	}
	
	public int getX(){
		return ObstacleX;
	}
	public int getY(){
		return ObstacleY;
	}
	public void setX(int x){
		ObstacleX=x;
	}
	public void setY(int y){
		ObstacleY=y;
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
		if(clickX <= ObstacleX+ObstacleWidth && clickX > ObstacleX){
			if(clickY <= ObstacleY+ObstacleHeight && clickY > ObstacleY){
				return true;
			}
		}
		return false;
	}
	public void drawObstacle(Canvas canvas, Bitmap ObstacleImage){
		canvas.drawBitmap(ObstacleImage, ObstacleRect.left, ObstacleRect.top, null);
	}
}
