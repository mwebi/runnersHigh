package com.runnershigh;

import android.content.Context;

import java.util.TimerTask;
import java.util.Vector;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;

public class Player{
	private static float MAX_JUMP_HEIGHT = 100;  //TODO: make MAX_JUMP_HEIGHT < 100 possible
	private static float MIN_JUMP_HEIGHT = 10;
	public Bitmap playerImg;
	private float lastPosY;
	static public int width;
	private int height;
	private float x;
	private float y;
	private boolean jumping = false;
	private boolean jumpingsoundplayed = true;
	private boolean reachedPeak = false;
	private boolean slowSoundplayed = false;
	private float jumpStartY;
	private float velocity = 0;
	private Rect playerRect;
	private float speedoffsetX = 0;
	private Bitmap playerSpriteImg; 
	public PlayerSprite playerSprite;
	

	public Player(Context context, OpenGLRenderer glrenderer, int ScreenHeight) {
		x = 70; 
		y = 200;
		
		width = 40; //60; nyan cat pre minimalize //62; playersprite settings
		height = 30; //42; nyan cat pre minimalize //63; playersprite settings
		
		playerSpriteImg = BitmapFactory.decodeResource(context.getResources(), R.drawable.nyansprite);
		playerSprite = new PlayerSprite(x, y, 1, width, height, 25, 6); 
		playerSprite.loadBitmap(playerSpriteImg); 
		glrenderer.addMesh(playerSprite);
	}
	
	public void setJump(boolean jump) {
		if(!jump)
			reachedPeak = true;
		
		if(reachedPeak) return;
		
		jumpStartY = y;
		jumping = true;
		if(jump)
			jumpingsoundplayed = false;
	}
	
	public boolean update() {
		playerSprite.updatePosition(x, y);
		playerSprite.tryToSetNextFrame();
		
		if(jumpingsoundplayed==false){
			SoundManager.playSound(3, 1);
			jumpingsoundplayed = true;
		}
		if (jumping && velocity >= 0) {
			if(y - jumpStartY < MIN_JUMP_HEIGHT || !reachedPeak) {
				float modifier = (MAX_JUMP_HEIGHT - (y - jumpStartY))/30; //TODO: make MAX_JUMP_HEIGHT < 100 possible
				
				velocity += 0.4981f * modifier;
			}
			if(y - jumpStartY >= MAX_JUMP_HEIGHT) {
				reachedPeak = true;			
			}
		}
		
		velocity -= 0.4981f;
		
		if (velocity < -9)
			velocity = -9;
		else if (velocity > 9)
			velocity = 9;
		
		y += velocity;
		
		playerRect = new Rect((int)x,(int)y+height,(int)x+width,(int)y);
		
		for (int i = 0; i < Level.maxBlocks; i++)
		{
			if( checkIntersect(playerRect, Level.blockData[i].getRect()) ){
				if(lastPosY >= Level.blockData[i].mHeight)
				{
					y=Level.blockData[i].mHeight;
					velocity = 0;
					reachedPeak = false;
					jumping = false;
				}
				else{
					// false -> player stops at left -> block mode
					// true -> player goes through left side -> platform mode
					return false;
				}
			}
		}
		lastPosY = y;
		
		if(speedoffsetX<50)
			speedoffsetX+=0.01;
		
		x=70+speedoffsetX;
		
		if(y + height < 0){
			y = -height;
			return false;
		}
		
		return true;
	}	
	
	public boolean collidedWithObstacle(int levelPosition) {
		
		Rect ObstacleRect = new Rect();
		
		for(int i = 0; i < Level.maxObstaclesJumper; i++)
		{
			ObstacleRect.left =  (int)Level.obstacleDataJumper[i].x;
			ObstacleRect.top = (int)Level.obstacleDataJumper[i].y+(int)Level.obstacleDataJumper[i].height; 
			ObstacleRect.right = (int)Level.obstacleDataJumper[i].x+(int)Level.obstacleDataJumper[i].width;
			ObstacleRect.bottom = (int)Level.obstacleDataJumper[i].y;
			
			if( checkIntersect(playerRect, ObstacleRect) && !Level.obstacleDataJumper[i].didTrigger)
			{
				Level.obstacleDataJumper[i].didTrigger=true;
				
				SoundManager.playSound(6, 1);
				velocity = 6; //katapultiert den player wie ein trampolin nach oben
				
			}
		}
		
		for(int i = 0; i < Level.maxObstaclesSlower; i++)
		{
			ObstacleRect.left =  (int)Level.obstacleDataSlower[i].x;
			ObstacleRect.top = (int)Level.obstacleDataSlower[i].y+(int)Level.obstacleDataSlower[i].height; 
			ObstacleRect.right = (int)Level.obstacleDataSlower[i].x+(int)Level.obstacleDataSlower[i].width;
			ObstacleRect.bottom = (int)Level.obstacleDataSlower[i].y;

			if( checkIntersect(playerRect, ObstacleRect) && !Level.obstacleDataSlower[i].didTrigger)
			{
				Level.obstacleDataSlower[i].didTrigger=true;
				
				//TODO: prevent playing sound 2x or more 
				if(!slowSoundplayed){    
					SoundManager.playSound(5, 1);
					slowSoundplayed=true;
				}
				return true; //slow down player fast
			}
		}
		
		for(int i = 0; i < Level.maxObstaclesBonus; i++)
		{
			ObstacleRect.left =  (int)Level.obstacleDataBonus[i].x;
			ObstacleRect.top = (int)Level.obstacleDataBonus[i].y+(int)Level.obstacleDataBonus[i].height; 
			ObstacleRect.right = (int)Level.obstacleDataBonus[i].x+(int)Level.obstacleDataBonus[i].width;
			ObstacleRect.bottom = (int)Level.obstacleDataBonus[i].y;

			if( checkIntersect(playerRect, ObstacleRect) && !Level.obstacleDataBonus[i].didTrigger)
			{
				Level.obstacleDataBonus[i].didTrigger=true;

				SoundManager.playSound(8, 1);
				Level.scoreCounter+=200;
				Level.obstacleDataBonus[i].z= -1;
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
	
	public void reset() {
		x = 70; // x/y is bottom left corner of picture
		y = 200;
		velocity = 0;
		speedoffsetX = 0;
	}
	
	public int getPosX() {
		return (int)x;
	}

	public void setPosX(int posX) {
		this.x = posX;
	}

	public int getPosY() {
		return (int)y;
	}

	public void setPosY(int posY) {
		this.y = posY;
	}

}
