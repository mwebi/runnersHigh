package com.runnershigh;


public class Util {
	private static Util mInstance =  null;
	private int mScreenHeight = 0;
	
	public static synchronized Util getInstance() {
		if(mInstance == null)
			mInstance = new Util();
		
		return mInstance;
	}

	public void setScreenHeight(int value) {
		mScreenHeight = value;
	}
	
	public int getScreenHeight() {
		return mScreenHeight;
	}
	
	
	public int toScreenY(int y) {
		y *= -1;
		y += mScreenHeight;
		
		return y;
	}	
}
