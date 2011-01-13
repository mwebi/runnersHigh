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
	private int lastPosY;
	private int width;
	private int height;
	private boolean jumping = false;
	private boolean reachedPeak = false;
	private int fallSpeed = 5;
	private int jumpHeight = 180;
	private int jumpStartY;
	private boolean onGround = false;
	private boolean aboveGround = true;
	private int refrenzY;
	private boolean onZeroBlock = false;
	private boolean jumpenabled=true;
	private boolean startYset = false;
	

	public Player(Context context, int ScreenHeight) {
		
		posX = 70; // x/y ist bottom left corner von picture
		posY = 110;
		refrenzY=ScreenHeight;
		
		//Queue<Block> blocks;
		
		playerImg = BitmapFactory.decodeResource(context.getResources(), R.drawable.playerimg);
		width=playerImg.getWidth();
		height=playerImg.getHeight();
	}
	
	public void jump() {
		if(!jumping && jumpenabled) {
			jumping = true;
			jumpStartY = posY;
			
		}
	}
	public void update() {
		lastPosY=posY;
		
		if(!jumping) {
			//if(aboveGround)
				posY-=fallSpeed;
		}
		//TODO
		//if(poxY<=ScreenHeight)
			//gameover
	}	
	public void doJump() {
		//if(jumpenabled==true){
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
						reachedPeak = false;
						jumping = false;
						//if(onZeroBlock==true)
							//jumpenabled=false;
					}
				}						
			}
		//}
	}
	
	public boolean OLDcheckCollision(Vector<Rect> blocks) {
		int currentBlockID=0;
		for(Rect currentBlock : blocks){
			//Rect currentBlock = blocks.get(0);
			Rect playerRect = new Rect(posX,posY,posX+width,posY-height);
			
			

			if(playerRect.right >= currentBlock.left && playerRect.left <= currentBlock.right){
				//jetzt wissen wir das wir in dem block sind auf dem der spieler ist
				if(currentBlock.top!=0){

					if(playerRect.top >= currentBlock.top){ //TODO wieso playerRect.top??
						//Log.d("coll", "collision greift");
						aboveGround=true;
						if(playerRect.top >= currentBlock.top+5)
							jumpenabled=false;
						break;
					}
					else if(playerRect.top < currentBlock.top){
						//if(!jumping)
							//posY=currentBlock.top;
						if(aboveGround){
							aboveGround=false;
							posY += fallSpeed;
							jumpStartY=currentBlock.top;
							break;
						}
					}
				}else{ //wenn player über block mit height 0
					Log.d("coll", "over zeroblock");
					Log.d("block", Integer.toString(currentBlock.top));
					jumpenabled=false; 
					onZeroBlock=true;
					if(playerRect.right+10 <= currentBlock.left && playerRect.top <= currentBlock.top){
						posX=currentBlock.left;
						Log.d("coll", "boom game over");
					}
					break;
				}
			}
			
			//currentBlock.contains(playerRect)
			if (false){
				Log.d("block", Integer.toString(currentBlock.top));
				Log.d("block", Integer.toString(currentBlock.left));
				Log.d("block", Integer.toString(currentBlock.right));
				Log.d("block", Integer.toString(currentBlock.bottom));
				
				Log.d("player", Integer.toString(playerRect.top));
				Log.d("player", Integer.toString(playerRect.left));
				Log.d("player", Integer.toString(playerRect.right));
				Log.d("player", Integer.toString(playerRect.bottom));	
			}
			
			currentBlockID++;
		}
		return true;
	}
	public boolean checkCollision(Vector<Rect> blocks) {
		Rect playerRect = new Rect(posX,posY,posX+width,posY-height);
		
		/*Rect testRect = new Rect(0,10,10,0);
		Rect testRect2 = new Rect(2,12,12,0);
		if (checkIntersect(testRect, testRect2) )
			Log.d("coll", "geht");*/
		
		for(Rect currentBlock : blocks){
			if(currentBlock.top==0){
				continue;
			}	
			if( checkIntersect(playerRect, currentBlock) ){
				Log.d("coll", "geht");
				if(lastPosY >= currentBlock.top)
					posY=currentBlock.top;

			}
		}
		return true;
	}
	public boolean checkIntersect(Rect playerRect, Rect blockRect) {
		if(playerRect.bottom >= blockRect.bottom && playerRect.bottom <= blockRect.top)
			if(playerRect.right >= blockRect.left && playerRect.right <= blockRect.right )
				return true;
		if(playerRect.bottom >= blockRect.bottom && playerRect.bottom <= blockRect.top)
			if(playerRect.left >= blockRect.left && playerRect.left <= blockRect.right )
				return true;
		if(playerRect.top >= blockRect.bottom && playerRect.top <= blockRect.top)
			if(playerRect.right >= blockRect.left && playerRect.right <= blockRect.right )
				return true;
		if(playerRect.top >= blockRect.bottom && playerRect.top <= blockRect.top)
			if(playerRect.left >= blockRect.left && playerRect.left <= blockRect.right )
				return true;
		//blockrect in playerrect
		if(blockRect.bottom <=playerRect.bottom && blockRect.top <=playerRect.top)
			if(blockRect.left <=playerRect.left && blockRect.right <=playerRect.right )
				return true;
		
		return false;
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
