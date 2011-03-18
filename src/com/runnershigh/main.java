package com.runnershigh;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;


public class main extends Activity {
		PowerManager.WakeLock wakeLock ;
		MediaPlayer musicPlayer;
		
		/** Called when the activity is first created. */
	    @Override
		public void onCreate(Bundle savedInstanceState) {
	    	super.onCreate(savedInstanceState);
	    	//setContentView(R.layout.main);	 
	    	
	    	PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
			wakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "tag");
			wakeLock.acquire();	    	
			
			SoundManager.getInstance();
	        SoundManager.initSounds(this);
	        SoundManager.loadSounds();
	        
	        musicPlayer = MediaPlayer.create(getApplicationContext(), R.raw.toughandcool);
	        musicPlayer.start();
	        musicPlayer.setVolume(0.6f, 0.6f);
	        musicPlayer.setLooping(true);


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
			musicPlayer.release();
			SoundManager.cleanup();
		}
		@Override
		public void onResume() {
			super.onResume();
			wakeLock.acquire();
			musicPlayer.start();
		}
		@Override
		public void onPause() {
			super.onPause();
			wakeLock.release();
			musicPlayer.pause();
		}
	    
		public void saveScore(int score) {			
			Intent myIntent = new Intent (this, HighScoreForm.class);
			myIntent.putExtra("score", score);			
			startActivity (myIntent);
		}
    
	public class RunnersHighView extends View implements Runnable {
		private Player player;
		private Level level;
		private int width;
		private int height;
		private Button resetButton;
		private Button saveButton;
		private boolean scoreWasSaved = false;

		
		public RunnersHighView(Context context) {
			super(context);
			
			Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
			width= display.getWidth(); 
			height= display.getHeight();
			Util.getInstance().setScreenHeight(height);
			
			
			player = new Player(getApplicationContext(),height);
			level = new Level(context, width, height);
			
			resetButton = new Button(context, R.drawable.resetbutton, 350, 10, 100, 41);
			saveButton = new Button(context, R.drawable.savebutton, 200, 10, 100, 41);
			
			Thread rHThread = new Thread(this);
			rHThread.start();
		}

		public void run() {
			while(true){
				if (player.update(level.getBlockData())) {
						level.update();
				} else {
					if(player.getPosY() < 0){
						resetButton.setShowButton(true);
						saveButton.setShowButton(true);
					}
				}
				if(player.collidedWithObstacle(level.getObstacleData(),level.getLevelPosition()) ){
					level.lowerSpeed();
				}

				postInvalidate();
				try{ Thread.sleep(10); }
				catch (InterruptedException e)
				{
					e.printStackTrace();
				}
			}
		}
		
		public void draw(Canvas canvas) {
			Paint paint = new Paint();
			paint.setColor(Color.LTGRAY);
			paint.setStyle(Paint.Style.FILL);
			paint.setAntiAlias(true);
			paint.setTextSize(18);

			canvas.drawText("Your Score: " + Integer.toString(level.getScoreCounter()), 20, 20, paint);
			
			if (resetButton.getShowButton())
				resetButton.drawButton(canvas);
			if (saveButton.getShowButton() && !scoreWasSaved)
				saveButton.drawButton(canvas);

			level.draw(canvas);
			player.draw(canvas);
			
			invalidate();
		}
		
		public boolean onTouchEvent(MotionEvent event) {
			if(event.getAction() == MotionEvent.ACTION_UP)
				player.setJump(false);
			
			else if(event.getAction() == MotionEvent.ACTION_DOWN){
				if (resetButton.getShowButton() || saveButton.getShowButton()) {
					if(resetButton.isClicked( event.getX(), event.getY() ) ){
						player.reset();
						level.reset();
						resetButton.setShowButton(false);
						saveButton.setShowButton(false);
						scoreWasSaved=false;
						SoundManager.playSound(1, 1);
					}
					else if(saveButton.isClicked( event.getX(), event.getY() ) && !scoreWasSaved){
						//save score
						saveScore(level.getScoreCounter());
						//play save sound
						SoundManager.playSound(4, 1);
						scoreWasSaved=true;
					}
				}
				else {
					player.setJump(true);
				}
			}
			
			return true;
		}
	}
}
