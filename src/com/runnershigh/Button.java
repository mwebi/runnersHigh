package com.runnershigh;

import android.R.drawable;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;

public class Button {
	private Bitmap ButtonImage;
	private boolean showButton = false;
	private int ButtonX = 350;
	private int ButtonY = 10;
	private int ButtonWidth = 100;
	private int ButtonHeight = 41;
	
	
	public Button(Context context, int ImageID, int x, int y, int w, int h){
		ButtonImage = BitmapFactory.decodeResource(context.getResources(), ImageID); //ImageID=R.drawable.resetbutton
		ButtonX=x;
		ButtonY=y;
		ButtonWidth=w;
		ButtonHeight=h;
	}
	public void setShowButton(boolean toSet){
		showButton=toSet;
	}
	public boolean getShowButton(){
		return showButton;
	}
	public int getWidth(){
		return ButtonWidth;
	}
	public int getHeight(){
		return ButtonHeight;
	}
	public int getX(){
		return ButtonX;
	}
	public int getY(){
		return ButtonY;
	}
	public boolean isClicked(float clickX, float clickY){
		if(clickX <= ButtonX+ButtonWidth && clickX > ButtonX){
			if(clickY <= ButtonY+ButtonHeight && clickY > ButtonY){
				return true;
			}
		}
		return false;
	}
	public void drawButton(Canvas canvas){
		canvas.drawBitmap(ButtonImage, ButtonX, ButtonY, null);
	}
}
