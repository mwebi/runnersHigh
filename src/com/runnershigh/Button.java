/* 
 * RunnersHigh Version 1.0
 * 
 * _DESCRIPTION:
 * 		Displays a button ...
 */

package com.runnershigh;

public class Button extends RHDrawable{
	private boolean showButton = false;
	public float lastX;
	
	public Button(int _x, int _y, int _z, int _width, int _height){
		super((int)_x, (int)_y, (int)_z, (int)_width, (int)_height);
		x=lastX=_x;
		y=_y;
		width=_width;
		height=_height;
	}
	public void setShowButton(boolean toSet){
		showButton=toSet;
	}
	public boolean getShowButton(){
		return showButton;
	}
	public int getWidth(){
		return (int)width;
	}
	public int getHeight(){
		return (int)height;
	}
	public int getX(){
		return (int)x;
	}
	public int getY(){
		return (int)y;
	}
	public boolean isClicked(float clickX, float clickY){
		if(clickX <= x+width && clickX > x){
			if(clickY <= y+height && clickY > y){
				return true;
			}
		}
		return false;
	}
}
