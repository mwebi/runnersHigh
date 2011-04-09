package com.runnershigh;

import java.util.Random;
import java.util.Vector;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;


public class Level {
	private int width;
	private int height;
	private int levelPosition;
	private int scoreCounter;
	private boolean threeKwasplayed;
	private float baseSpeed;
	private float extraSpeed;
	private Vector<Block> blockData;
	private Vector<Block> unusedBlocks;
	private Vector<Obstacle> obstacleData;
	private Vector<Obstacle> unusedObstacles;
	private Bitmap obstacleSlowImg;
	private Bitmap obstacleJumpImg;
	private Bitmap blockImg;
	private boolean slowDown;
	Paint paint;
	Rect blockRect;
	private int BlockCounter;
	private OpenGLRenderer renderer;
	
	public Level(Context context, OpenGLRenderer glrenderer, int width, int heigth) {
		Log.d("debug", "in Level constructor");
		this.width = width;
		this.height = heigth;
		this.levelPosition = 0;
		this.scoreCounter = 0;
		this.baseSpeed = 5;
		this.extraSpeed = 0;
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
		blockImg = BitmapFactory.decodeResource(context.getResources(), R.drawable.ground );
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
			
			if (levelPosition > blockData.get(0).right) {
				unusedBlocks.add(blockData.firstElement());
				blockData.remove(0);
			}
			synchronized (obstacleData) {
				if (obstacleData.size()>0){
					if (levelPosition > obstacleData.get(0).getObstacleRect().right) {
						unusedObstacles.add(obstacleData.firstElement());
						obstacleData.remove(0);	
					}
				}
			}
			
			//Log.d("debug", "in update after > right; blockData.size() -> " + Integer.toString(blockData.size()) );
			
			// Log.d("debug", "left:" + blockData.get(blockData.size() -1).left);
			//Log.d("debug", "lp + width:" + (levelPosition + width));
			
			
			if (blockData.get(blockData.size() -1).left < levelPosition + width)
				generateAndAddBlock();
			
			// Log.d("debug", "in update after < levelPosition + width");
			
			if(baseSpeed<5)
				baseSpeed+=0.025;
			
			if(extraSpeed<5)
				extraSpeed+=0.001;
			if(slowDown){
				//extraSpeed=0;
				baseSpeed=1;
				slowDown=false;
			}
			
			levelPosition += baseSpeed + extraSpeed;
			//scoreCounter += 1;
			scoreCounter = levelPosition/10;
			
			for (Block block : blockData) {
				block.x -= (baseSpeed + extraSpeed);
			}
			
			synchronized (obstacleData) {
				for (Obstacle obstacle : obstacleData) {
					obstacle.x -= (baseSpeed + extraSpeed);
				}
			}
			
			
			if(scoreCounter>=3000 && threeKwasplayed==false){
				threeKwasplayed=true;
				SoundManager.playSound(2, 1);
			}
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
		Log.d("debug", "in generate");
		Log.d("debug", "blockData.size() -> " + Integer.toString(blockData.size()) );		
		if (blockData.size() == 0) {
			if (unusedBlocks.size() == 0) {
				Block newBlock = new Block(0, 50, width, 0);
				blockData.add(newBlock);
				newBlock.loadBitmap(blockImg);
				renderer.addMesh(newBlock);
			} else {
				blockData.add(unusedBlocks.firstElement());
				unusedBlocks.remove(0);
			}
		} else {
			Block currentBlock;
			
			int newHeight;
			if (blockData.get(blockData.size() -1 ).top > height/2)
				newHeight = (int)(Math.random()*height/3*2 + height/8);
			else
				newHeight = (int)(Math.random()*height/2 + height/8);
			
			int newWidth = (int)(Math.random()*width/2+width/2);
			int distance = (int)(Math.random()*width/4+width/8);
			Block lastBlock = blockData.get(blockData.size() - 1); 
			int newLeft = lastBlock.right + distance;
			int newRight = newLeft + newWidth;
			
			if (unusedBlocks.size() == 0) {
				Log.d("debug", "new block needed");
				currentBlock = new Block(0, 50, width, 0);
				blockData.add(currentBlock);
				currentBlock.loadBitmap(blockImg);
				renderer.addMesh(currentBlock);
			} else {
				currentBlock = unusedBlocks.firstElement();
				blockData.add(currentBlock);
				unusedBlocks.remove(0);
			}
			
			
			currentBlock.setLeft(newLeft);
			currentBlock.setTop(newHeight);
			currentBlock.setRight(newRight);
			currentBlock.setBottom(0);
			
			currentBlock.x = newLeft - levelPosition;
			currentBlock.y = 0;
			
			//start creating obstacles after the 10th block
			if(BlockCounter>10)
				generateAndAddObstacle(newLeft, newRight, newHeight, newWidth);
		}
		BlockCounter++;
	}
	private void generateAndAddObstacle(int newLeft,int newRight,int newHeight,int newWidth) {
		// Obstacle creation
		Random randomGenerator = new Random();
		int decider = randomGenerator.nextInt(6); //random int 0-4
		if (decider == 3 || decider == 4){ //create either j or s obstacle
			char type;
			Obstacle newObstacle;

			int obstacleLeft;
			//get the range, casting to long to avoid overflow problems
		    long range = (long)newRight - (long)newLeft + 1;
		    // get range to be 1/3 of the block length
		    range*=0.33;
		    // compute a fraction of the range, 0 <= frac < range
		    long fraction = (long)(range * randomGenerator.nextDouble());

		    if (randomGenerator.nextBoolean()){
				type='s'; //make obstacle type slow
				newObstacle = new Obstacle(0, 0, obstacleSlowImg.getWidth(), obstacleSlowImg.getHeight(),type);
			    obstacleLeft =  (int)(newRight - newObstacle.getWidth() - fraction); 
			}else{
				type='j'; //make obstacle type jumping
				newObstacle = new Obstacle(0, 0, obstacleJumpImg.getWidth(), obstacleJumpImg.getHeight(),type);
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
			//get the range, casting to long to avoid overflow problems
		    long range = (long)newRight - (long)newLeft + 1;
		    // get range to be 1/3 of the block length
		    range*=0.33;
		    // compute a fraction of the range, 0 <= frac < range
		    long fraction = (long)(range * randomGenerator.nextDouble());

			type='s'; //make obstacle type slow
			Obstacle newSlowObstacle = new Obstacle(0, 0, obstacleSlowImg.getWidth(), obstacleSlowImg.getHeight(),type);
		    obstacleLeft =  (int)(newRight - newSlowObstacle.getWidth() - fraction); 
		    //set new coordinates
		    newSlowObstacle.setX(obstacleLeft);
		    newSlowObstacle.setY(newHeight);
		    newSlowObstacle.setObstacleRect(obstacleLeft, obstacleLeft+newSlowObstacle.getWidth() ,newHeight, newHeight-newSlowObstacle.getHeight());
			obstacleData.add(newSlowObstacle);
			
			type='j'; //make obstacle type jumping
			Obstacle newJumpObstacle = new Obstacle(0, 0, obstacleJumpImg.getWidth(), obstacleJumpImg.getHeight(),type);
		    obstacleLeft =  (int)(newLeft + newJumpObstacle.getWidth() + fraction); 
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
				
				current.bottom = block.bottom;
				current.left = block.left;
				current.right = block.right;
				current.top = block.top;
				
				current.left -= levelPosition;
				current.right -= levelPosition;
				
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
		return scoreCounter;
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
			blockData.clear();
			obstacleData.clear();
			this.baseSpeed = 5;
			this.extraSpeed = 0;
			BlockCounter=0;
			generateAndAddBlock();
		}
	}
	
	public class Block extends Mesh {
		private int left;
		private int right;
		private int bottom;
		private int top;
		
		public int getLeft() {
			return left;
		}

		public void setLeft(int left) {
			this.left = left;
			
			float[] vertices = new float[] { 0, 0, 0, right-left, 0, 0.0f, 0, top-bottom,
					0.0f, right-left, top-bottom, 0.0f };

			setVertices(vertices);
		}

		public int getRight() {
			return right;
		}

		public void setRight(int right) {
			this.right = right;
			
			float[] vertices = new float[] { 0, 0, 0, right-left, 0, 0.0f, 0, top-bottom,
					0.0f, right-left, top-bottom, 0.0f };

			setVertices(vertices);
		}

		public int getBottom() {
			return bottom;
		}

		public void setBottom(int bottom) {
			this.bottom = bottom;
			float[] vertices = new float[] { 0, 0, 0, right-left, 0, 0.0f, 0, top-bottom,
					0.0f, right-left, top-bottom, 0.0f };

			setVertices(vertices);
		}

		public int getTop() {
			return top;
		}

		public void setTop(int top) {
			this.top = top;
			float[] vertices = new float[] { 0, 0, 0, right-left, 0, 0.0f, 0, top-bottom,
					0.0f, right-left, top-bottom, 0.0f };

			setVertices(vertices);
		}

		public Block(int _left, int _top, int _right, int _bottom) {
			left = _left;
			top = _top;
			right = _right;
			bottom = _bottom;
			
			float textureCoordinates[] = { 0.0f, 1.0f, //
					1.0f, 1.0f, //
					0.0f, 0.0f, //
					1.0f, 0.0f, //
			};

			short[] indices = new short[] { 0, 1, 2, 1, 3, 2 };

			float[] vertices = new float[] { 0, 0, 0, right-left, 0, 0.0f, 0, top-bottom,
					0.0f, right-left, top-bottom, 0.0f };

			setIndices(indices);
			setVertices(vertices);
			setTextureCoordinates(textureCoordinates);
		}
		
	}
	
}

