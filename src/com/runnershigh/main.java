package com.runnershigh;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

public class main extends Activity {
		/** Called when the activity is first created. */
	    @Override
		public void onCreate(Bundle savedInstanceState) {
	    	super.onCreate(savedInstanceState);
	    	//setContentView(R.layout.main);	 
				        
			requestWindowFeature(Window.FEATURE_NO_TITLE);  
			getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
			
			//setContentView(R.layout.runnershigh);	        
			//(RunnersHighView) findViewById(R.id.runnersHighViewXML);
			RunnersHighView gameView = new RunnersHighView(getApplicationContext()); 
			setContentView(gameView);	        
			
		}	
				
    
	public class RunnersHighView extends View implements Runnable {
		private Player player;
	
		public RunnersHighView(Context context) {
			super(context);
			
			//setContentView(R.layout.main);	 
			
			//setBackgroundColor(0xFFFFFF);
			player = new Player(getApplicationContext());
			
			Thread rHThread = new Thread(this);
			rHThread.start();
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			while(true){
				player.run();
				postInvalidate();
				try{ Thread.sleep(50); }
				catch (InterruptedException e)
				{
					e.printStackTrace();
				}
			}
			
		}
		
		public void draw(Canvas canvas) {
			
			player.draw(canvas);
			invalidate();
		}

	}
}
