package com.runnershigh;

import android.content.Context;
import java.lang.Object;
import java.util.Queue;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.view.View;

public class Player {
	public Bitmap playerImg;
	private int posX;
	private int posY;
	private int width;
	private int height;

	public Player(Context context) {
		
		posX = 70;
		posY = 60;
		
		//Queue<Block> blocks;
		
		playerImg = BitmapFactory.decodeResource(context.getResources(), R.drawable.playerimg);
		playerImg.getWidth();
	}
	
	public void jump() {
		
	}
	public void run() {
		posX=posX+5;
	}
	public void draw(Canvas canvas) {
		canvas.drawBitmap(playerImg, posX-width/2, (posY-height/2)*-1, null);
	}
	
	public int getPosX() {
		return posX;
	}

	public void setPosX(int posX) {
		this.posX = posX;
	}

	public int getPosY() {
		return posY;
	}

	public void setPosY(int posY) {
		this.posY = posY;
	}

}
