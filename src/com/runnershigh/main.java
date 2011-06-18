package com.runnershigh;

import com.runnershigh.OpenGLRenderer;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.media.MediaPlayer;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;

public class main extends Activity {
		PowerManager.WakeLock wakeLock ;
		//MediaPlayer musicPlayerIntro;
		MediaPlayer musicPlayerLoop;
		boolean MusicLoopStartedForFirstTime = false;
		boolean paused = false;

		boolean isRunning = false;
		
		/** Called when the activity is first created. */
	    @Override
		public void onCreate(Bundle savedInstanceState) {
	    	super.onCreate(savedInstanceState);
	    	//setContentView(R.layout.main);	 
	    	paused=false;
	    	
	    	PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
			wakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "tag");
			wakeLock.acquire();	    	
			
			SoundManager.getInstance();
	        SoundManager.initSounds(this);
	        SoundManager.loadSounds();
	        
	        //musicPlayerIntro = MediaPlayer.create(getApplicationContext(), R.raw.nyanintro);
	        //musicPlayerIntro.start();
	        //musicPlayerIntro.setVolume(0.5f, 0.5f);
	        //musicPlayerIntro.setLooping(false);
	        
	        musicPlayerLoop = MediaPlayer.create(getApplicationContext(), R.raw.track);
	        musicPlayerLoop.setLooping(true);
			musicPlayerLoop.seekTo(0);
			musicPlayerLoop.setVolume(0.5f, 0.5f);
	        
			requestWindowFeature(Window.FEATURE_NO_TITLE);  
			getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
			
			//setContentView(R.layout.runnershigh);	        
			//(RunnersHighView) findViewById(R.id.runnersHighViewXML);
			isRunning = true;
			RunnersHighView gameView = new RunnersHighView(getApplicationContext()); 
			setContentView(gameView);	     
		}	
		
	    @Override
	    protected void onDestroy() {
	    	if(Settings.RHDEBUG)
	    		Log.d("debug", "onDestroy main");
	    	isRunning = false;
			wakeLock.release();
			musicPlayerLoop.release();
			SoundManager.cleanup();
			super.onDestroy();
		}
		@Override
		public void onResume() {
			if(Settings.RHDEBUG)
				Log.d("debug", "onResume");
			wakeLock.acquire();
			if(MusicLoopStartedForFirstTime)
				musicPlayerLoop.start();
			super.onResume();
			paused=false;
		}
		@Override
		public void onStop() {
			if(Settings.RHDEBUG)
				Log.d("debug", "onStop");
			super.onStop();
		}
		@Override
		public void onRestart() {
			if(Settings.RHDEBUG)
				Log.d("debug", "onRestart");
			paused=false;
			super.onRestart();
		}
		@Override
		public void onPause() {
			if(Settings.RHDEBUG)
				Log.d("debug", "onPause");
			paused=true;
			wakeLock.release();
			musicPlayerLoop.pause();
			super.onPause();
		}
	    
		public void saveScore(int score) {			
			Intent myIntent = new Intent (this, HighScoreForm.class);
			myIntent.putExtra("score", score);			
			startActivity (myIntent);
		}
    
	public class RunnersHighView extends GLSurfaceView implements Runnable {
		private Player player;

		private Level level;
		private ParalaxBackground background;
		private int width;
		private int height;
		private Button resetButton;
		private Bitmap resetButtonImg;
		private Button saveButton;
		private Bitmap saveButtonImg;
		private RHDrawable blackRHD;
		private Bitmap blackImg;
		private float blackImgAlpha;
		private boolean scoreWasSaved = false;
		private boolean deathSoundPlayed = false;
		Paint paint = new Paint();
		private OpenGLRenderer mRenderer;
		private CounterGroup mCounterGroup;
		private CounterDigit mCounterDigit1;
		private CounterDigit mCounterDigit2;
		private CounterDigit mCounterDigit3;
		private CounterDigit mCounterDigit4;
		private Bitmap CounterFont; 
		private Bitmap CounterYourScoreImg;
		private RHDrawable CounterYourScoreDrawable;
		public  boolean doUpdateCounter = true;
		private long timeAtLastSecond;
		private int runCycleCounter;
		
		public RunnersHighView(Context context) {
			super(context);
			
			Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
			width= display.getWidth(); 
			height= display.getHeight();
			Util.getInstance().setScreenHeight(height);
			Util.getInstance().setAppContext(context);
			
			
			paint.setARGB(0xff, 0x00, 0x00, 0x00);;
			paint.setAntiAlias(true);
			paint.setTextSize(16);
			
			mRenderer = new OpenGLRenderer();
			this.setRenderer(mRenderer);
			
			background = new ParalaxBackground(width, height);

			background.loadLayerFar(BitmapFactory.decodeResource(context.getResources(),
					R.drawable.backgroundlayer3_compr));
			background.loadLayerMiddle(BitmapFactory.decodeResource(context.getResources(),
					R.drawable.backgroundlayer2_compr));
			background.loadLayerNear(BitmapFactory.decodeResource(context.getResources(),
					R.drawable.backgroundlayer1_compr));

			if(Settings.RHDEBUG)
				Log.d("debug", "before addMesh");
			mRenderer.addMesh(background);
			
			resetButtonImg = BitmapFactory.decodeResource(context.getResources(),R.drawable.resetbutton);
			resetButton = new Button(350, height-50-10, -2, 100, 50);
			resetButton.loadBitmap(resetButtonImg);
			mRenderer.addMesh(resetButton);			
			
			saveButtonImg = BitmapFactory.decodeResource(context.getResources(), R.drawable.savebutton);
			saveButton = new Button(200, height-50-10, -2, 100, 50);
			saveButton.loadBitmap(saveButtonImg);
			mRenderer.addMesh(saveButton);
			
			player = new Player(getApplicationContext(), mRenderer, height);
			
			level = new Level(context, mRenderer, width, height);
			
			
			//new counter
			CounterYourScoreImg = BitmapFactory.decodeResource(context.getResources(), R.drawable.yourscore);
			CounterYourScoreDrawable = new RHDrawable(20, height-16-20, 1, CounterYourScoreImg.getWidth(), CounterYourScoreImg.getHeight());
			CounterYourScoreDrawable.loadBitmap(CounterYourScoreImg); 
			mRenderer.addMesh(CounterYourScoreDrawable);
			
			CounterFont = BitmapFactory.decodeResource(context.getResources(), R.drawable.numberfont);
			mCounterGroup = new CounterGroup(70, height-20-20, 1, 128*4, 20, 25);
			
			for(int i=70; i<130; i+=15){
				if(i==115){
					mCounterDigit1 = new CounterDigit(i, height-20-20, 1, 16, 20);
					mCounterDigit1.loadBitmap(CounterFont); 
					mCounterGroup.add(mCounterDigit1);
				}
				if(i==100){
					mCounterDigit2 = new CounterDigit(i, height-20-20, 1, 16, 20);
					mCounterDigit2.loadBitmap(CounterFont); 
					mCounterGroup.add(mCounterDigit2);
				}
				if(i==85){
					mCounterDigit3 = new CounterDigit(i, height-20-20, 1, 16, 20);
					mCounterDigit3.loadBitmap(CounterFont); 
					mCounterGroup.add(mCounterDigit3);
				}
				if(i==70){
					mCounterDigit4 = new CounterDigit(i, height-20-20, 1, 16, 20);
					mCounterDigit4.loadBitmap(CounterFont); 
					mCounterGroup.add(mCounterDigit4);
				}
			}
			mRenderer.addMesh(mCounterGroup);
			
			blackImg = Bitmap.createBitmap(16, 16, Bitmap.Config.ARGB_4444);
			//blackImg = BitmapFactory.decodeResource(context.getResources(), R.drawable.resetbutton);
			blackRHD = new RHDrawable(0, 0, 1, width, height);
			blackImg.eraseColor(-16777216);
			blackImgAlpha=1;
			blackRHD.setColor(0, 0, 0, blackImgAlpha);
			blackRHD.loadBitmap(blackImg);
			mRenderer.addMesh(blackRHD);
			
			timeAtLastSecond = System.currentTimeMillis();
	        runCycleCounter=0;
			
	        Thread rHThread = new Thread(this);
			rHThread.start();
			
			if(Settings.RHDEBUG)
				Log.d("debug", "RunnersHighView constructor ended");
		}
		
		public void run() {

			if(Settings.RHDEBUG)
				Log.d("debug", "run method started");
	    	
			// wait until the intro is over
			// this gives the app enough time to load
			try{
				while(!mRenderer.firstFrameDone)
					Thread.sleep(10);

				if(Settings.RHDEBUG)
					Log.d("debug", "first frame done");

				if(!musicPlayerLoop.isPlaying())
					musicPlayerLoop.start();
				MusicLoopStartedForFirstTime=true;
				
				long timeAtStart = System.currentTimeMillis();
				while (System.currentTimeMillis() < timeAtStart + 2000)
				{
					blackImgAlpha-=0.005; 
					blackRHD.setColor(0, 0, 0, blackImgAlpha);
					Thread.sleep(10);
				}
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
			
			blackRHD.setColor(0, 0, 0, 0);
			blackRHD.z=-1.0f;
			//mRenderer.removeMesh(blackRHD); //TODO: find a way to remove mesh without runtime errors

			long timeForOneCycle=0;
			long currentTimeTaken=0;
			long starttime = 0;
			
			while(isRunning){
				starttime= System.currentTimeMillis();

				player.playerSprite.setFrameUpdateTime( (level.baseSpeedMax+level.extraSpeedMax)*10 -((level.baseSpeed+level.extraSpeed)*10) );
				if (player.update()) {
						if(Settings.RHDEBUG){
							currentTimeTaken = System.currentTimeMillis()- starttime;
							Log.d("runtime", "time after player update: " + Integer.toString((int)currentTimeTaken));
						}
						level.update();
						if(Settings.RHDEBUG){
							currentTimeTaken = System.currentTimeMillis()- starttime;
							Log.d("runtime", "time after level update: " + Integer.toString((int)currentTimeTaken));
						}
						background.update();
						if(Settings.RHDEBUG){
							currentTimeTaken = System.currentTimeMillis()- starttime;
							Log.d("runtime", "time after background update: " + Integer.toString((int)currentTimeTaken));
						}
				} else {
					if(player.getPosY() < 0){
						doUpdateCounter=false;
						resetButton.setShowButton(true);
						resetButton.z = 1.0f;
						saveButton.setShowButton(true);
						saveButton.z = 1.0f;
						if(!deathSoundPlayed){
							SoundManager.playSound(7, 1);
							deathSoundPlayed=true;
						}
					}
				}
				
				if(player.collidedWithObstacle(level.getLevelPosition()) ){
					level.lowerSpeed();
				}
				
				
				if(doUpdateCounter)
					mCounterGroup.tryToSetCounterTo(level.getScoreCounter());

				if(Settings.RHDEBUG){				
					timeForOneCycle= System.currentTimeMillis()- starttime;
					Log.d("runtime", "time after counter update: " + Integer.toString((int)timeForOneCycle));
				}
				timeForOneCycle= System.currentTimeMillis()- starttime;
				//postInvalidate();
				if(timeForOneCycle>9)
					timeForOneCycle=9;
				
				try{ Thread.sleep(10-timeForOneCycle); }
				catch (InterruptedException e)
				{
					e.printStackTrace();
				}
				runCycleCounter++;
				
				if(Settings.RHDEBUG){
					currentTimeTaken = System.currentTimeMillis()- starttime;
					Log.d("runtime", "time after thread sleep : " + Integer.toString((int)currentTimeTaken));
				}
				
				timeForOneCycle= System.currentTimeMillis()- starttime;
				if((System.currentTimeMillis() - timeAtLastSecond) > 1000 && Settings.RHDEBUG){
					timeAtLastSecond = System.currentTimeMillis();
					Log.d("runtime", "run cycles per second: " + Integer.toString(runCycleCounter));
					runCycleCounter=0;
				}
				if(Settings.RHDEBUG){
					timeForOneCycle= System.currentTimeMillis()- starttime;
					Log.d("runtime", "overall time for this run: " + Integer.toString((int)timeForOneCycle));
				}
			}
			
			if(Settings.RHDEBUG)
				Log.d("debug", "run method ended");
			
		}
		
		/*public void draw(Canvas canvas) {
			

			canvas.drawText("Your Score: " + Integer.toString(level.getScoreCounter()), 20, 20, paint);
			
			if (resetButton.getShowButton())
				resetButton.drawButton(canvas);
			if (saveButton.getShowButton() && !scoreWasSaved)
				saveButton.drawButton(canvas);

			level.draw(canvas);
			player.draw(canvas);
			
		}
		*/


		public boolean onTouchEvent(MotionEvent event) {
			if(event.getAction() == MotionEvent.ACTION_UP)
				player.setJump(false);
			
			else if(event.getAction() == MotionEvent.ACTION_DOWN){
				if (resetButton.getShowButton() || saveButton.getShowButton()) {
					if(resetButton.isClicked( event.getX(), Util.getInstance().toScreenY((int)event.getY()) ) ){
						System.gc(); //do garbage collection
						player.reset();
						level.reset();
						resetButton.setShowButton(false);
						resetButton.z = -2.0f;
						saveButton.setShowButton(false);
						saveButton.z = -2.0f;
						saveButton.x = saveButton.lastX; 
						mCounterGroup.resetCounter();
						scoreWasSaved=false;
						deathSoundPlayed=false;
						SoundManager.playSound(1, 1);
						doUpdateCounter=true;
					}
					else if(saveButton.isClicked( event.getX(), Util.getInstance().toScreenY((int)event.getY())  ) && !scoreWasSaved){
						//save score
						saveButton.setShowButton(false);
						saveButton.z = -2.0f;
						saveButton.lastX = saveButton.x;
						saveButton.x = -5000;
						
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
