package com.runnershigh;

import android.content.Context;
import android.graphics.Bitmap;


public class Util {
	private static Util mInstance =  null;
	public static int mScreenHeight = 0;
	public static int mScreenWidth = 0;
	private Context mContext = null;
	
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
	
	public Context getAppContext()
	{
		assert(mContext != null);
		return mContext;
	}
}
