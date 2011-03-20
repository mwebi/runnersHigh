package com.runnershigh;

import android.content.Context;
import java.util.Vector;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.Log;

public class Player {
	private static float MAX_JUMP_HEIGHT = 140;
	private static float MIN_JUMP_HEIGHT = 10;
	public Bitmap playerImg;
	private float posX;
	private int posY;
	private int lastPosY;
	private int width;
	private int height;
	private boolean jumping = false;
	private boolean jumpingsoundplayed = true;
	private boolean reachedPeak = false;
	private boolean slowSoundplayed = false;
	private int jumpStartY;
	private float velocity = 0;
	private Rect playerRect;
	private float speedoffsetX = 0;
	

	public Player(Context context, int ScreenHeight) {
		posX = 70; // x/y is bottom left corner of picture
		posY = 200;
		
		//Queue<Block> blocks;
		
		playerImg = BitmapFactory.decodeResource(context.getResources(), R.drawable.playerimg);
		width=playerImg.getWidth();
		height=playerImg.getHeight();
	}
	
	public void setJump(boolean jump) {
		if(!jump)
			reachedPeak = true;
		
		if(reachedPeak) return;
		
		jumpStartY = posY;
		jumping = true;
		if(jump)
			jumpingsoundplayed = false;
	}
	
	public boolean update(Vector<Rect> blocks) {
		if(jumpingsoundplayed==false){
			SoundManager.playSound(3, 1);
			jumpingsoundplayed = true;
		}
		if (jumping && velocity >= 0) {
			if(posY - jumpStartY < MIN_JUMP_HEIGHT || !reachedPeak) {
				float modifier = (MAX_JUMP_HEIGHT - (posY - jumpStartY))/30;
				velocity += 0.4981 * modifier;
			}
			if(posY - jumpStartY >= MAX_JUMP_HEIGHT) {
				reachedPeak = true;			
			}
		}
		
		velocity -= 0.4981;
		
		if (velocity < -9)
			velocity = -9;
		else if (velocity > 9)
			velocity = 9;
		
		posY += velocity;
		playerRect = new Rect((int)posX,posY,(int)posX+width,posY-height);
		
		for(Rect currentBlock : blocks){
			if(currentBlock.top==0){
				continue;
			}
			if( checkIntersect(playerRect, currentBlock) ){
				if(lastPosY-height >= currentBlock.top)
				{
					posY=currentBlock.top+height;
					velocity = 0;
					reachedPeak = false;
					jumping = false;
				}
				else{
					return false;
				}
					
			}
		}
		
		lastPosY = posY;
		
		
		if(speedoffsetX<5)
			speedoffsetX+=0.001;
		
		posX=70+speedoffsetX*50;
		
		if(posY - height < 0){
			posY = -1;
			return false;
		}
		
		
		return true;
	}	
	public boolean collidedWithObstacle(Vector<Obstacle> obstacles, int levelPosition) {
		for(Obstacle currentObstacle : obstacles){
			Rect modifiedObstacleRect= new Rect(currentObstacle.getObstacleRect());
			modifiedObstacleRect.left -= levelPosition;
			modifiedObstacleRect.right -= levelPosition;
			if( checkIntersect(playerRect, modifiedObstacleRect) ){
				if(currentObstacle.getType()=='s'){
					if(!slowSoundplayed){
						SoundManager.playSound(5, 1);
						slowSoundplayed=true;
					}
					return true; //slow down player fast
				}
				else if(currentObstacle.getType()=='j'){
					SoundManager.playSound(6, 1);
					velocity = 10; //katapultiert den player wie ein trampolin nach oben
				}
			}
		}
		slowSoundplayed=false;
		return false;
	}
	public boolean checkIntersect(Rect playerRect, Rect blockRect) {
		if(playerRect.bottom >= blockRect.bottom && playerRect.bottom <= blockRect.top)
		{
			if(playerRect.right >= blockRect.left && playerRect.right <= blockRect.right )
				return true;
			else if(playerRect.left >= blockRect.left && playerRect.left <= blockRect.right )
				return true;
		}
		else if(playerRect.top >= blockRect.bottom && playerRect.top <= blockRect.top){
			if(playerRect.right >= blockRect.left && playerRect.right <= blockRect.right )
				return true;
			else if(playerRect.left >= blockRect.left && playerRect.left <= blockRect.right )
				return true;
		}
		//blockrect in playerrect
		if(blockRect.bottom >= playerRect.bottom && blockRect.bottom <= playerRect.top)
			if(blockRect.right >= playerRect.left && blockRect.right <= playerRect.right )
				return true;
		
		return false;
	}
	public void draw(Canvas canvas) {
		int y = Util.getInstance().toScreenY(posY);
		canvas.drawBitmap(playerImg, posX, y, null);
	}
	
	public void reset() {
		posX = 70; // x/y is bottom left corner of picture
		posY = 200;
		velocity = 0;
		speedoffsetX =0;
	}
	
	public int getPosX() {
		return (int)posX;
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
