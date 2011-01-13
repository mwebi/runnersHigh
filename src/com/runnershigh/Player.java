package com.runnershigh;

import android.content.Context;
import java.lang.Object;
import java.util.Queue;
import java.util.Vector;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.Log;
import android.view.View;

public class Player {
	public Bitmap playerImg;
	private int posX;
	private int posY;
	private int width;
	private int height;
	private boolean jumping = false;
	private boolean reachedPeak = false;
	private int jumpHeight = 50;
	private int jumpStartY;

	public Player(Context context) {
		
		posX = 70;
		posY = -150;
		
		//Queue<Block> blocks;
		
		playerImg = BitmapFactory.decodeResource(context.getResources(), R.drawable.playerimg);
		playerImg.getWidth();
	}
	
	public void jump() {
		if(!jumping) {
			jumping = true;
			jumpStartY = posY;
		}
	}
	
	public void doJump() {
		if(jumping == true){
			if(!reachedPeak) {
				if(posY < jumpStartY + jumpHeight) {
					posY += 5;
				}
				else if (posY >= jumpStartY + jumpHeight){
					reachedPeak = true;
				}
			}
			if(reachedPeak) {
				if(posY > jumpStartY)
					posY -= 5;
				else if (posY <= jumpStartY){
					reachedPeak =false;
					jumping = false;
				}
			}						
		}
	}
	
	public boolean checkCollision(Vector<Rect>  blockVector) {
		Rect currentBlock = blockVector.get(0);
		Log.v("left:", Integer.toString(currentBlock.left));
		Log.v("top:", Integer.toString(currentBlock.top));
		Log.v("right:", Integer.toString(currentBlock.right));
		Log.v("bottom:", Integer.toString(currentBlock.bottom));
		
		if(currentBlock.contains(posX, posY))
			Log.v("contains", "true");
		
		else Log.v("contains", "false");
		
		return true;
	}
	
	public void run() {
		//posX=posX+5;
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
