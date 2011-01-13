package com.runnershigh;

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
	
	private Vector<LevelSection> sections; 
	private int width;
	private int height;
	private int sectionPosition;
	private int sectionsWidth;
	private int scoreCounter;
	private Context context;
	
	public Level(Context context, int width, int heigth) {
		this.width = width;
		this.height = heigth;
		this.sectionPosition = 0;
		this.sectionsWidth = 0;
		this.context = context;
		this.scoreCounter = 0;
		
		sections = new Vector<LevelSection>();
		
		while(this.sectionsWidth < this.width)
			generateAndAddSection();
		
		generateAndAddSection();
		generateAndAddSection();
		
	}
	
	public void update() {
		
		if (sectionPosition + sections.get(0).getWidth() < 0) {
		 
			generateAndAddSection();
			sectionsWidth -= sections.get(0).getWidth();
			sectionPosition += sections.get(0).getWidth();
			//Log.d("Level", "removing a section");
			synchronized (sections) {
				sections.remove(0);	
			}
		}
		else {
			sectionPosition -= 20;
			scoreCounter += 10;
		}
			
	}
	
	public void draw(Canvas canvas) {

		int relativeX = sectionPosition;
		synchronized (sections) {
			for (LevelSection section : sections) {
				section.draw(canvas, relativeX);
				relativeX += section.getWidth();
			}
		}

	}
	
	private void generateAndAddSection() {
		if (sectionsWidth < width) {
			LevelSection newSection = new LevelSection(50);
			sections.add(newSection);
			sectionsWidth += newSection.getWidth();
		} else {
			int index = sections.size()-1;
			int counter = (int)(Math.random()*3)+20;
			boolean setNewHeight = true;
			
			int oldHeight = sections.get(index).getHeight();
			index--;
			while(counter > 0) {
				if (sections.get(index).getHeight() != oldHeight)
					setNewHeight = false;
				index--;
				counter--;
			}
			
			int newHeight;
			
			if (setNewHeight && oldHeight != 0)
				newHeight = 0;
			else if (setNewHeight && oldHeight == 0)
				newHeight = (int)(Math.random()*height/2);
			else
				newHeight = oldHeight;
			
			LevelSection newSection = new LevelSection(newHeight);
			sections.add(newSection);
			sectionsWidth += newSection.getWidth();
		}
		
	}
	
	public Vector<Rect> getBlockData() {
		Vector<Rect> blockData = new Vector<Rect>();
		
		int currentX = sectionPosition;
		int startX = currentX;
		
		int currentY = sections.firstElement().getHeight();
		for (LevelSection section : sections) {
			if (section.getHeight() != currentY) {
				Rect currentRect = new Rect();
				currentRect.left = startX;
				currentRect.right = currentX;
				currentRect.bottom = 0;
				currentRect.top = currentY;
				blockData.add(currentRect);
				
				startX = currentX;
				currentY = section.getHeight();
			}
			currentX += section.getWidth();
		}
		return blockData;
	}

	public int getScoreCounter() {
		return scoreCounter;
	}

	private class LevelSection {
		private Vector<Tile> blocks;
		private int width;
		private int height;
		
		public LevelSection(int height) {
			blocks = new Vector<Tile>();
			this.height = height;
			
			while(height > 0) {
				Tile newBlock = new Tile(height);
				blocks.add(newBlock);
				height -= newBlock.getHeight();
			}
			
			
			if (this.height == 0)
				this.width = 10;
			else
				this.width = blocks.firstElement().getWidth();
		}
		
		public int getWidth() {
			return width;
		}

		public int getHeight() {
			return height;
		}

		public void draw(Canvas canvas, int referenceX) {
			for (Tile block : blocks) {
				block.draw(canvas, referenceX);
			}
		}
		
		private class Tile {
			private Bitmap image;
			private int posY;
			private int width;
			private int height;

			public Tile(int posY) {
				this.posY = posY;
				image = BitmapFactory.decodeResource(context.getResources(), R.drawable.blockpart);
				this.width = image.getWidth();
				this.height = image.getHeight();
			}
			
			public int getWidth() {
				return width;
			}

			public int getHeight() {
				return height;
			}
			
			public void draw(Canvas canvas, int referenceX) {
				canvas.drawBitmap(image, referenceX, Util.getInstance().toScreenY(posY), null);
			}
		}
		
	}
}
