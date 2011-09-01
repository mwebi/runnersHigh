package com.runnershigh;

import android.content.Context;
import android.graphics.Bitmap;


public class Util {
	private static Util mInstance =  null;
	public static int mScreenHeight = 0;
	public static int mScreenWidth = 0;
	public static int mWidthHeightRatio = 0;
	private static Context mContext = null;
	private static OpenGLRenderer mRenderer = null;
	public static long roundStartTime = 0;
	
	public static synchronized Util getInstance() {
		if(mInstance == null)
			mInstance = new Util();
		
		return mInstance;
	}
	
	public static float getPercentOfScreenWidth(float percent) {
		float percentWidth=mScreenWidth/100*percent;
		return percentWidth;
	}
	public static float getPercentOfScreenHeight(float percent) {
		float percentHeight=mScreenHeight/100*percent;
		return percentHeight;
	} 	
	
	public int toScreenY(int y) {
		y *= -1;
		y += mScreenHeight;
		
		return y;
	}
	
	public void setAppContext(Context context)
	{
		mContext = context;
	}
	
	public static Context getAppContext()
	{
		assert(mContext != null);
		return mContext;
	}
	public void setAppRenderer(OpenGLRenderer renderer)
	{
		mRenderer = renderer;
	}
	
	public static OpenGLRenderer getAppRenderer()
	{
		assert(mRenderer != null);
		return mRenderer;
	}
	public static long getTimeSinceRoundStartMillis()
	{
		assert(roundStartTime != 0);
		return System.currentTimeMillis()-roundStartTime;
	}
}
