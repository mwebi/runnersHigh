package com.runnershigh;

import android.R.bool;
import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.ContactsContract.CommonDataKinds.Event;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

public class main extends Activity {
		PowerManager.WakeLock wakeLock ;
		/** Called when the activity is first created. */
	    @Override
		public void onCreate(Bundle savedInstanceState) {
	    	super.onCreate(savedInstanceState);
	    	//setContentView(R.layout.main);	 
	    	
	    	PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
			wakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "tag");
			wakeLock.acquire();	    	
				        
			requestWindowFeature(Window.FEATURE_NO_TITLE);  
			getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
			
			//setContentView(R.layout.runnershigh);	        
			//(RunnersHighView) findViewById(R.id.runnersHighViewXML);
			RunnersHighView gameView = new RunnersHighView(getApplicationContext()); 
			setContentView(gameView);	        
			
		}	
		
	    @Override
	    protected void onDestroy() {

			super.onDestroy();
			wakeLock.release();
		}
		@Override
		public void onResume() {

			super.onResume();
			wakeLock.acquire();
		}
		@Override
		public void onPause() {

			super.onResume();
			wakeLock.release();
		}
	    
    
	public class RunnersHighView extends View implements Runnable {
		private Player player;
		private Level level;
		private int width;
		private int height;
	
		public RunnersHighView(Context context) {
			super(context);
			
			TextView counter = (TextView) findViewById(R.id.counter);
//			
//			counter.setText("fhhf");
//			counter.setTextColor(0xFF0000);

			Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
			width= display.getWidth(); 
			height= display.getHeight();
			
			player = new Player(getApplicationContext(),height);
			level = new Level(context, width, height);
			
			Thread rHThread = new Thread(this);
			rHThread.start();
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			while(true){
				//REM Player update vor collision
				
				player.doJump();
				player.update();
				player.checkCollision(level.getBlockData());
				level.update();
				
				
				/*if(player.checkCollision(level.getBlockData()) ){
					
				}*/

				
				
				postInvalidate();
				try{ Thread.sleep(25); }
				catch (InterruptedException e)
				{
					e.printStackTrace();
				}
			}
			
		}
		
		public void draw(Canvas canvas) {
			level.draw(canvas, 0, height);
			player.draw(canvas, 0, height);
			invalidate();
		}
		
		public boolean onTouchEvent(MotionEvent event) {
			player.jump();
			return true;
		}

	}
}
