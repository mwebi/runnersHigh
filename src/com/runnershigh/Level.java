package com.runnershigh;

import java.util.Vector;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.Log;


public class Level {
	
	private Vector<LevelSection> sections; 
	private int width;
	private int height;
	private int sectionPosition;
	private int sectionsWidth;
	private Context context;
	
	public Level(Context context, int width, int heigth) {
		this.width = width;
		this.height = heigth;
		this.sectionPosition = 0;
		this.sectionsWidth = 0;
		this.context = context;
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
			Log.d("Level", "removing a section");
			synchronized (sections) {
				sections.remove(0);	
			}
			
		}
		else
			sectionPosition -= 1;
	}
	
	public void draw(Canvas canvas, int referenceX, int referenceY) {
		int relativeX = sectionPosition;
		synchronized (sections) {
			for (LevelSection section : sections) {
				section.draw(canvas, referenceX + relativeX, referenceY);
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
				newHeight = (int)(Math.random()*height);
			else
				newHeight = oldHeight;
			
			LevelSection newSection = new LevelSection(newHeight);
			sections.add(newSection);
			sectionsWidth += newSection.getWidth();
		}
		
	}
	
	private class LevelSection {
		private Vector<Block> blocks;
		private int width;
		private int height;
		
		public LevelSection(int height) {
			blocks = new Vector<Block>();
			this.height = height;
			
			while(height > 0) {
				Block newBlock = new Block(height);
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

		public void draw(Canvas canvas, int referenceX, int referenceY) {
			for (Block block : blocks) {
				block.draw(canvas, referenceX, referenceY);
			}
		}
		
		private class Block {
			private Bitmap image;
			private int posY;
			private int width;
			private int height;

			public Block(int posY) {
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
			
			public void draw(Canvas canvas, int referenceX, int referenceY) {
				canvas.drawBitmap(image, referenceX, referenceY-posY, null);
			}
		}
		
	}
}
