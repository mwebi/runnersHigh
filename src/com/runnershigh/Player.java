package com.runnershigh;

import android.content.Context;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.util.Log;

public class Player{
	private static float MAX_JUMP_HEIGHT = Util.getPercentOfScreenHeight(45);  
	private static float MIN_JUMP_HEIGHT = Util.getPercentOfScreenHeight(5);
	public Bitmap playerImg;
	private float lastPosY;
	static public float width;
	static public float height;
	public float x;
	public float y;
	private boolean jumping = false;
	private boolean jumpingsoundplayed = true;
	private boolean onGround = false;
	private boolean reachedPeak = false;
	private boolean slowSoundplayed = false;
	private float jumpStartY;
	private float velocity = 0;
	private Rect playerRect;
	private Rect ObstacleRect;
	private float speedoffsetX = 0;
	private Bitmap playerSpriteImg; 
	public PlayerSprite playerSprite;
	
	public int bonusItems = 0;
	private int bonusScorePerItem = 200;
	

	public Player(Context context, OpenGLRenderer glrenderer, int ScreenHeight) {
		x = Util.getPercentOfScreenWidth(9); //70; 
		y = Settings.FirstBlockHeight+Util.getPercentOfScreenHeight(4);
		
		width = Util.getPercentOfScreenWidth(9); //40; dicker //40; nyan cat //60; nyan cat pre minimalize //62; playersprite settings
		height = width*Util.mWidthHeightRatio; //40; dicker //30;  nyan cat //42; nyan cat pre minimalize //63; playersprite settings
		
		playerSpriteImg = BitmapFactory.decodeResource(context.getResources(), R.drawable.bastardchar512x128);
		playerSprite = new PlayerSprite(x, y, 0.5f, width, height, 25, 8); 
		playerSprite.loadBitmap(playerSpriteImg); 
		glrenderer.addMesh(playerSprite);
		
		playerRect = new Rect();
		playerRect.left =(int)x;
		playerRect.top =(int)(y+height);
		playerRect.right =(int)(x+width);
		playerRect.bottom =(int)y;
		
		ObstacleRect = new Rect();
	}
	
	public void setJump(boolean jump) {
		if(!jump)
		{
			reachedPeak = true;
		}
		
		if(reachedPeak || !onGround) return;
		
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
		
		if (jumping && !reachedPeak) {
			velocity += 1.5f * (MAX_JUMP_HEIGHT - (y - jumpStartY)) / 100.f;


			if(Settings.RHDEBUG){
				Log.d("debug", "y: " + (y));
				Log.d("debug", "y + height: " + (y + height));
				//Log.d("debug", "velocity: " + velocity);
				//Log.d("debug", "modifier: " + (MAX_JUMP_HEIGHT - (y - jumpStartY)) / 100.0f);
				//Log.d("debug", "MAX_JUMP_HEIGHT - (y - jumpStartY): " + (MAX_JUMP_HEIGHT - (y - jumpStartY)));
			}

			if(y - jumpStartY >= MAX_JUMP_HEIGHT)
			{
				reachedPeak = true;
			}
		}
		else
		{
			velocity -= Util.getPercentOfScreenHeight(0.625f); //3
		}
		
		
		if (velocity < -Util.getPercentOfScreenHeight(1.875f)) //9
			velocity = -Util.getPercentOfScreenHeight(1.875f);
		else if (velocity > Util.getPercentOfScreenHeight(1.875f))
			velocity = Util.getPercentOfScreenHeight(1.875f);
		
		y += velocity;
		
		playerRect.left =(int)x;
		playerRect.top =(int)(y+height);
		playerRect.right =(int)(x+width);
		playerRect.bottom =(int)y;
		
		onGround = false;
		
		for (int i = 0; i < Level.maxBlocks; i++)
		{
			if( checkIntersect(playerRect, Level.blockData[i].BlockRect) )
			{
				if(lastPosY >= Level.blockData[i].mHeight && velocity <= 0)
				{
					y=Level.blockData[i].mHeight;
					velocity = 0;
					reachedPeak = false;
					jumping = false;
					onGround = true;
				}
				else{
					// false -> player stops at left -> block mode
					// true -> player goes through left side -> platform mode
					return false;
				}
			}
		}
		lastPosY = y;
		
		if(speedoffsetX<Util.getPercentOfScreenWidth(7) ) //50
			speedoffsetX+=Util.getPercentOfScreenWidth(0.002f); //0.01
		
		x=Util.getPercentOfScreenWidth(9)+speedoffsetX;
		
		if(y + height < 0){
			y = -height;
			return false;
		}
		
		return true;
	}	
	
	public boolean collidedWithObstacle(float levelPosition) {
		
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
				bonusItems++;
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
		velocity = 0;
		x = 70; // x/y is bottom left corner of picture
		y = Settings.FirstBlockHeight+20;
		
		speedoffsetX = 0;
		bonusItems = 0;
	}
	
	public int getBonusScore()
	{
		return bonusItems * bonusScorePerItem;
	}
	


}
