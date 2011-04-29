package com.runnershigh;

import java.util.Random;
import java.util.Vector;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;


public class Level {
	private int width;
	private int height;
	private int levelPosition;
	private int lastLevelPosition;
	private float deltaLevelPosition;
	public static float scoreCounter;
	private boolean threeKwasplayed;
	public float baseSpeed;
	public float baseSpeedMax;
	public float baseSpeedStart;
	public float extraSpeed;
	public float extraSpeedMax;
	private Vector<Block> blockData;
	private Vector<Block> unusedBlocks;
	private Vector<Obstacle> obstacleData;
	private Vector<Obstacle> unusedObstacles;
	private Bitmap obstacleSlowImg;
	private Bitmap obstacleJumpImg;
	private Bitmap bonusImg;
	private boolean slowDown;
	Paint paint;
	Rect blockRect;
	private int BlockCounter;
	private OpenGLRenderer renderer;
	
	public Level(Context context, OpenGLRenderer glrenderer, int _width, int _heigth) {
		//Log.d("debug", "in Level constructor");
		width = _width;
		height = _heigth;
		levelPosition = 0;
		lastLevelPosition = 0;
		deltaLevelPosition = 0;
		scoreCounter = 0;
		baseSpeedStart = 2;
		baseSpeed = baseSpeedStart;
		baseSpeedMax = 5;
		extraSpeed = 0;
		extraSpeedMax = 5;
		threeKwasplayed = false;
		renderer = glrenderer;
		
		paint = new Paint();
		paint.setColor(Color.RED);
		paint.setStyle(Paint.Style.FILL);
		
		blockData = new Vector<Block>();
		unusedBlocks = new Vector<Block>();
		obstacleData = new Vector<Obstacle>();
		unusedObstacles = new Vector<Obstacle>();
		obstacleSlowImg = BitmapFactory.decodeResource(context.getResources(), R.drawable.obstacleslow );
		obstacleJumpImg = BitmapFactory.decodeResource(context.getResources(), R.drawable.obstaclejump );
		Block.setTextureLeft(
				BitmapFactory.decodeResource(
						context.getResources(), R.drawable.blockleft ));
		Block.setTextureMiddle(
				BitmapFactory.decodeResource(
						context.getResources(), R.drawable.blockmiddle ));
		Block.setTextureRight(
				BitmapFactory.decodeResource(
						context.getResources(), R.drawable.blockright ));
		

		bonusImg = BitmapFactory.decodeResource(context.getResources(), R.drawable.bonusimage);

		slowDown = false;
		
		generateAndAddBlock();
	}
	
	public void update() {
		
		synchronized (blockData) {
			//Log.d("debug", "in update");
			
			if (blockData.size() == 0)
			{
				levelPosition = 0;
				generateAndAddBlock();
			}
			// Log.d("debug", "in update after == 0");
			
			if (0 > blockData.get(0).getRect().right) {
				unusedBlocks.add(blockData.firstElement());
				blockData.remove(0);
			}
			synchronized (obstacleData) {
				if (obstacleData.size()>0){
					if (0 > obstacleData.get(0).x+obstacleData.get(0).width) {
						unusedObstacles.add(obstacleData.firstElement());
						obstacleData.remove(0);	
					}
				}
			}
			
			//Log.d("debug", "in update after > right; blockData.size() -> " + Integer.toString(blockData.size()) );
			
			// Log.d("debug", "left:" + blockData.get(blockData.size() -1).left);
			//Log.d("debug", "lp + width:" + (levelPosition + width));
			
			
			if (blockData.get(blockData.size() -1).getRect().left < width)
				generateAndAddBlock();
			
			// Log.d("debug", "in update after < levelPosition + width");
			
			if(baseSpeed < baseSpeedMax)
				baseSpeed+=0.025; //baseSpeed+=0.025;
			
			if(extraSpeed < extraSpeedMax)
				extraSpeed+=0.001; //extraSpeed+=0.001;
			if(slowDown){
				//extraSpeed=0;
				baseSpeed=1;
				slowDown=false;
			}
			
			
			deltaLevelPosition = baseSpeed + extraSpeed;
			levelPosition += deltaLevelPosition;


			Log.d("debug", "deltaLevelPosition/10: " + deltaLevelPosition/10);
			scoreCounter += deltaLevelPosition/10;
						
			for (Block block : blockData) {
				block.x -= deltaLevelPosition;
			}
			
			synchronized (obstacleData) {
				for (Obstacle obstacle : obstacleData) {
					
					if(obstacle.ObstacleType == 'b'){
						obstacle.updateObstacleCircleMovement();
						obstacle.centerX -= deltaLevelPosition;
					}
					else
						obstacle.x -= deltaLevelPosition;
				}
			}
			
			
			if(scoreCounter>=2000 && threeKwasplayed==false){
				threeKwasplayed=true;
				SoundManager.playSound(2, 1);
			}
			
			lastLevelPosition=levelPosition;
			//Log.d("debug", "in update after value mod");
		}	
	}
	
	/*
	public void draw(Canvas canvas) {
		synchronized (blockData) {
			//Log.d("debug", "in draw");
			
			canvas.save();
			
			canvas.translate(-levelPosition, 0);
			canvas.scale(1, -1);
			//canvas.translate(0, height);
			canvas.translate(0, -height+1);
			
		
			for (Rect block : blockData) {				
				canvas.drawRect(block, paint);
			}
			for (Obstacle obstacle : obstacleData) {				
				if(obstacle.getType()=='s')
					obstacle.drawObstacle(canvas, obstacleSlowImg);
				else if(obstacle.getType()=='j')
					obstacle.drawObstacle(canvas, obstacleJumpImg);
			}
			canvas.restore();
			
		}
	}
	*/
	
	private void generateAndAddBlock() {
		//Log.d("debug", "in generate");
		//Log.d("debug", "blockData.size() -> " + Integer.toString(blockData.size()) );		
		if (blockData.size() == 0) {
			if (unusedBlocks.size() == 0) {
				Block newBlock = new Block();
				
				newBlock.x = 0;
				newBlock.setWidth(width);
				newBlock.setHeight(50);
				
				blockData.add(newBlock);
				renderer.addMesh(newBlock);
			} else {
				Block currentBlock = unusedBlocks.firstElement();
				blockData.add(currentBlock);
				
				currentBlock.x = 0;
				currentBlock.setWidth(width);
				currentBlock.setHeight(50);
				
				unusedBlocks.remove(0);
			}
		} else {
			Block currentBlock;
			
			int newHeight;
			int oldHeight = blockData.get(blockData.size() -1 ).getRect().top;
			
			if (oldHeight > height/2)
				newHeight = (int)(Math.random()*height/3*2 + height/8);
			else
				newHeight = (int)(Math.random()*height/2 + height/8);
			
			/*if (Math.abs(oldHeight - newHeight) < 20)
			{
				newHeight -= (oldHeight - newHeight) * 4;
			}
			*/
			
			int newWidth = (int)(Math.random()*width/2+width/2);
			newWidth -= (newWidth - Block.getTextureLeftWidth() - Block.getTextureRightWidth()) % (Block.getTextureMiddleWidth());
			
			// use this for original distance
			int distance = (int)(Math.random()*width/4+width/8);
			
			// or use this for variable distance
			//int distance;
			//if (levelPosition < 2000)
				//distance = (int)(Math.random()*width/4-width/4+levelPosition/40.0f);
			//else
				//distance = (int)(Math.random()*width/4-width/4+50);
			
			
			Block lastBlock = blockData.get(blockData.size() - 1); 
			int newLeft = lastBlock.getRect().right + distance;
			int newRight = newLeft + newWidth;
			
			if (unusedBlocks.size() == 0) {
				currentBlock = new Block();
				blockData.add(currentBlock);
				renderer.addMesh(currentBlock);
			} else {
				currentBlock = unusedBlocks.firstElement();
				blockData.add(currentBlock);
				unusedBlocks.remove(0);
			}
			
			currentBlock.setHeight(newHeight);
			currentBlock.setWidth(newWidth);
			
			currentBlock.x = newLeft;
			
			//start creating obstacles after the 10th block
			if(BlockCounter>10)
				generateAndAddObstacle(newLeft, newRight, newHeight, newWidth);
		}
		BlockCounter++;
	}
	private void generateAndAddObstacle(int newLeft,int newRight,int newHeight,int newWidth) {
		// bonus Obstacle creation
		Random randomGenerator = new Random();
		//get the range, casting to long to avoid overflow problems
		int range = newRight - newLeft + 1;
		// get range to be 1/3 of the block length
		double limitLeft=newLeft+range*0.20;
		//range*=0.60;
	    
		
		int Bonusdecider = randomGenerator.nextInt(5);
		Obstacle newBonus = null;
		//if (0 == 0){
		if (Bonusdecider == 3){
			int bonusLeft;

		    // compute a fraction of the range, 0 <= frac < range
		    double fraction = range * randomGenerator.nextDouble();
		    
		    int newBonusWidth= 50;
		    int newBonusHeight= 50;
		    
		    if (unusedObstacles.size() == 0) {
		    	newBonus = new Obstacle(0.0f, 0.0f, 0.9f, newBonusWidth, newBonusHeight,'b');
				renderer.addMesh(newBonus);
			} else {
				newBonus = unusedObstacles.firstElement();
				unusedObstacles.remove(0);
				newBonus.setWidth(newBonusWidth );
			    newBonus.setHeight(newBonusHeight );
			    newBonus.setType('b');
			    newBonus.didTrigger=false;
			    newBonus.z=1;
			}
		    newBonus.loadBitmap(bonusImg); //TODO: way to not load bitmap for every object at runtime
		    bonusLeft = (int)(newLeft + fraction ); 
		    //set new coordinates
		    
		    newBonus.setX(bonusLeft);
		    newBonus.setY(newHeight+50+randomGenerator.nextInt(75));
		    newBonus.setObstacleRect(bonusLeft, bonusLeft+newBonus.getWidth() ,newHeight, newHeight-newBonus.getHeight());
			obstacleData.add(newBonus);
		}
		
		//get the range, casting to long to avoid overflow problems
		range = newRight - newLeft + 1;
		// get range to be 1/3 of the block length
	    range*=0.33;
		
		// Obstacle creation
		int decider = randomGenerator.nextInt(6); //random int von-bis
		if (decider == 3 || decider == 4){ //create either j or s obstacle
			char type;
			Obstacle newObstacle = null;

			int obstacleLeft;

		    // compute a fraction of the range, 0 <= frac < range
		    long fraction = (long)(range * randomGenerator.nextDouble());

		    if (randomGenerator.nextBoolean()){
				type='s'; //make obstacle type slow
				if (unusedObstacles.size() == 0) {
					newObstacle = new Obstacle(0.0f, 0.0f, 1.0f, (float)obstacleSlowImg.getWidth(), (float)obstacleSlowImg.getHeight(),type);
					renderer.addMesh(newObstacle);
				/*} else {
					newObstacle = unusedObstacles.firstElement();
					unusedObstacles.remove(0);
				}*/
				} else {
					newObstacle=checkForExistingObstacle(type);
					newObstacle.didTrigger=false;
				}
				newObstacle.loadBitmap(obstacleSlowImg);
			    obstacleLeft =  (int)(newRight - newObstacle.getWidth() - fraction); 
			}else{
				type='j'; //make obstacle type jumping
				if (unusedObstacles.size() == 0) {
					newObstacle = new Obstacle(0.0f, 0.0f, 1.0f, (float)obstacleJumpImg.getWidth(), (float)obstacleJumpImg.getHeight(),type);
					renderer.addMesh(newObstacle);
				/*} else {
					newObstacle = unusedObstacles.firstElement();
					unusedObstacles.remove(0);
				}*/
				} else {
					newObstacle=checkForExistingObstacle(type);
					newObstacle.didTrigger=false;
				}
				newObstacle.loadBitmap(obstacleJumpImg);
			    obstacleLeft =  (int)(newLeft + newObstacle.getWidth() + fraction); 
			}

		    //set new coordinates
		    newObstacle.setX(obstacleLeft);
		    newObstacle.setY(newHeight);
		    newObstacle.setObstacleRect(obstacleLeft, obstacleLeft+newObstacle.getWidth() ,newHeight, newHeight-newObstacle.getHeight());
			obstacleData.add(newObstacle);
			
		}else if (decider == 5){ //create two obstacles
			char type;
			int obstacleLeft;

		    // compute a fraction of the range, 0 <= frac < range
		    long fraction = (long)(range * randomGenerator.nextDouble());

		    Obstacle newSlowObstacle = null;
			type='s'; //make obstacle type slow
			if (unusedObstacles.size() == 0) {
				newSlowObstacle  = new Obstacle(0.0f, 0.0f, 1.0f, (float)obstacleSlowImg.getWidth(), (float)obstacleSlowImg.getHeight(),type);
				renderer.addMesh(newSlowObstacle);
			} else {
				newSlowObstacle=checkForExistingObstacle(type);
				newSlowObstacle.didTrigger=false;
			}
			
			newSlowObstacle.loadBitmap(obstacleSlowImg);
		    obstacleLeft =  (int)(newRight - newSlowObstacle.getWidth() - fraction); 
		    newSlowObstacle.setType(type);
		    //set new coordinates
		    newSlowObstacle.setX(obstacleLeft);
		    newSlowObstacle.setY(newHeight);
		    newSlowObstacle.setObstacleRect(obstacleLeft, obstacleLeft+newSlowObstacle.getWidth() ,newHeight, newHeight-newSlowObstacle.getHeight());
			obstacleData.add(newSlowObstacle);
			
			Obstacle newJumpObstacle = null;
			type='j'; //make obstacle type jumping
			if (unusedObstacles.size() == 0) {
				newJumpObstacle = new Obstacle(0.0f, 0.0f, 1.0f, (float)obstacleJumpImg.getWidth(), (float)obstacleJumpImg.getHeight(),type);
				renderer.addMesh(newJumpObstacle);
			} else {
				newJumpObstacle=checkForExistingObstacle(type);
				newJumpObstacle.didTrigger=false;
			}
			
			newJumpObstacle.loadBitmap(obstacleJumpImg);
		    obstacleLeft =  (int)(newLeft + newJumpObstacle.getWidth() + fraction);
		    newJumpObstacle.setType(type);
		    //set new coordinates
		    newJumpObstacle.setX(obstacleLeft);
		    newJumpObstacle.setY(newHeight);
		    newJumpObstacle.setObstacleRect(obstacleLeft, obstacleLeft+newJumpObstacle.getWidth() ,newHeight, newHeight-newJumpObstacle.getHeight());
			obstacleData.add(newJumpObstacle);
		}
	}
	//TODO refactor without temporary vector
	public Vector<Rect> getBlockData() {
		synchronized (blockData) {
			//Log.d("debug", "in getBlockData");
			Vector<Rect> modifiedBlockData = new Vector<Rect>();
			
			for (Block block : blockData) {				
				Rect current = new Rect();
				
				current.bottom = block.getRect().bottom;
				current.left = block.getRect().left;
				current.right = block.getRect().right;
				current.top = block.getRect().top;
				
				// current.left -= levelPosition;
				// current.right -= levelPosition;
				
				modifiedBlockData.add(current);
			}
			return modifiedBlockData;
		}
	}
	
	public Vector<Obstacle> getObstacleData() {
		synchronized (obstacleData) {
			return obstacleData;
		}
	}
	
	public int getScoreCounter() {
		return (int)scoreCounter;
	}
	public void lowerSpeed() {
		slowDown = true;
	}
	public int getLevelPosition(){
		return levelPosition;
	}
	public void reset() {
		scoreCounter=0;
		synchronized (blockData) {
			//Log.d("debug", "in reset");
			levelPosition = 0;
			while(blockData.size() > 0)
			{
				blockData.get(0).x = -1000;
				unusedBlocks.add(blockData.firstElement());
				blockData.remove(0);
			}
			blockData.clear();
			while(obstacleData.size() > 0)
			{
				//TODO obstacles get set outside of screen but continue to exist
				obstacleData.get(0).x = -100;
				unusedObstacles.add(obstacleData.firstElement());
				obstacleData.remove(0);
			}
			obstacleData.clear();
			
			this.baseSpeed = baseSpeedStart;
			this.extraSpeed = 0;
			BlockCounter=0;
			generateAndAddBlock();
		}
	}
	private Obstacle checkForExistingObstacle(char type){
		Obstacle ObstacleToReturn = null;
		boolean foundObstacle=false;
		int foundObstaclePosition=0;
		for(int i=0; i<unusedObstacles.size(); i++){
			if(unusedObstacles.get(i).ObstacleType == type){
				foundObstacle=true;
				foundObstaclePosition=i;
				break;
			}
		}
		if(!foundObstacle){
			ObstacleToReturn = new Obstacle(0.0f, 0.0f, 1.0f, (float)obstacleSlowImg.getWidth(), (float)obstacleSlowImg.getHeight(),type);
			renderer.addMesh(ObstacleToReturn);
		}
		else{
			ObstacleToReturn = unusedObstacles.get(foundObstaclePosition);
			unusedObstacles.remove(foundObstaclePosition);
		}
		return ObstacleToReturn;
	}

}

