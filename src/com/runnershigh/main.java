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
				        
			requestWindowFeature(Window.FEATURE_NO_TITLE);  
			getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
				        
			RunnersHighView gameView = (RunnersHighView) findViewById(R.id.runnersHighView);	        
			setContentView(gameView);	        
		}	
				
    
	public class RunnersHighView extends View implements Runnable {
		private Player player;
	
		public RunnersHighView(Context context) {
			super(context);
			
			//setContentView(R.layout.)
			
			//this.setBackgroundColor(0xFFFFFF);
			player = new Player(getApplicationContext());
			
			// TODO Auto-generated constructor stub
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
		}
		
		public void draw(Canvas canvas) {
			player.draw(canvas);
		}

	}
}
