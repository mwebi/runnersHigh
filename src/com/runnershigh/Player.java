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
	private int jumpHeight = 75;
	private int jumpStartY;
	private boolean onGround = false;
	private int refrenzY;

	public Player(Context context, int ScreenHeight) {
		
		posX = 70;
		posY = 150;
		refrenzY=ScreenHeight;
		
		//Queue<Block> blocks;
		
		playerImg = BitmapFactory.decodeResource(context.getResources(), R.drawable.playerimg);
		width=playerImg.getWidth();
		height=playerImg.getHeight();
	}
	
	public void jump() {
		if(!jumping) {
			jumping = true;
			jumpStartY = posY;
			
		}
	}
	public void update() {
		if(!jumping) {
			if(!onGround)
				posY-=2;
		}
		
		//TODO
		//if(poxY<=ScreenHeight)
			//gameover
	}	
	public void doJump() {
		if(jumping == true){
			if(!reachedPeak) {
				if(posY < jumpStartY + jumpHeight) {
					posY += 3;
				}
				else if (posY >= jumpStartY + jumpHeight){
					reachedPeak = true;
				}
			}
			if(reachedPeak) {
				if(posY > jumpStartY)
					posY -= 3;
				else if (posY <= jumpStartY){
					reachedPeak =false;
					jumping = false;
				}
			}						
		}
	}
	
	public void checkCollision(Vector<Rect> blocks) {
		for(Rect currentBlock : blocks){
			//Rect currentBlock = blocks.get(0);
			Rect playerRect = new Rect(posX,posY,posX+width,posY+height);
			boolean inside=false;
			/*if (currentBlock.top > playerRect.top)
				if (currentBlock.left < playerRect.right)
					if (currentBlock.right > playerRect.right){
						Log.d("contains", "true");
						inside=true;
					} else {inside=false;}
				 else {inside=false;}
			 else {inside=false;}*/
			
			
			if(playerRect.left > currentBlock.left && playerRect.right < currentBlock.right){
				if(currentBlock.top!=0){
					if(playerRect.top <= currentBlock.top){ //TODO wieso playerRect.top??
						onGround=true;
						break;
					}
					else {
						onGround=false;
					}
				}
				else {
					onGround=false; //weil bodenlos
				}
			}
			//currentBlock.contains(playerRect)
			if (inside){
				Log.d("block", Integer.toString(currentBlock.top));
				Log.d("block", Integer.toString(currentBlock.left));
				Log.d("block", Integer.toString(currentBlock.right));
				Log.d("block", Integer.toString(currentBlock.bottom));
				
				Log.d("player", Integer.toString(playerRect.top));
				Log.d("player", Integer.toString(playerRect.left));
				Log.d("player", Integer.toString(playerRect.right));
				Log.d("player", Integer.toString(playerRect.bottom));	
			}
		}
	}
	public void draw(Canvas canvas, int referenzX, int referenzY) {
		canvas.drawBitmap(playerImg, posX, referenzY-posY-height, null);
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
