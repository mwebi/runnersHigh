package com.runnershigh;

import javax.microedition.khronos.opengles.GL10;

import com.runnershigh.OpenGLRenderer;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.opengl.GLSurfaceView;
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
	        musicPlayer.setVolume(0.5f, 0.5f);
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
			wakeLock.release();
			musicPlayer.release();
			SoundManager.cleanup();
			super.onDestroy();
		}
		@Override
		public void onResume() {
			wakeLock.acquire();
			musicPlayer.start();
			super.onResume();			
		}
		@Override
		public void onPause() {
			wakeLock.release();
			musicPlayer.pause();
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
		private RHDrawable background;  
		private RHDrawable Counter;
		private Bitmap BGImg;
		private Bitmap CounterBitmap;
		private Canvas CounterCanvas;
		private Drawable CounterBackground;
		private int width;
		private int height;
		private Button resetButton;
		private Bitmap resetButtonImg;
		private Button saveButton;
		private Bitmap saveButtonImg;
		private boolean scoreWasSaved = false;
		private boolean deathSoundPlayed = false;
		Paint paint = new Paint();
		private OpenGLRenderer mRenderer;
		private Context mContext;

		
		public RunnersHighView(Context context) {
			super(context);
			Log.d("debug", "in RunnersHighView constructor");
			Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
			width= display.getWidth(); 
			height= display.getHeight();
			Util.getInstance().setScreenHeight(height);
			
			mContext = context;
			
			paint.setARGB(0xff, 0x00, 0x00, 0x00);;
			paint.setAntiAlias(true);
			paint.setTextSize(16);
			
			mRenderer = new OpenGLRenderer();
			this.setRenderer(mRenderer);
			
			BGImg = BitmapFactory.decodeResource(context.getResources(), R.drawable.background);
			background = new RHDrawable(0, 0, -1, width, height);
			background.loadBitmap(BGImg); 
			mRenderer.addMesh(background);
			
			resetButtonImg = BitmapFactory.decodeResource(context.getResources(), R.drawable.resetbutton);
			resetButton = new Button(350, height-50-10, -2, 100, 50);
			resetButton.loadBitmap(resetButtonImg);
			mRenderer.addMesh(resetButton);			
			
			saveButtonImg = BitmapFactory.decodeResource(context.getResources(), R.drawable.savebutton);
			saveButton = new Button(200, height-50-10, -2, 100, 50);
			saveButton.loadBitmap(saveButtonImg);
			mRenderer.addMesh(saveButton);
			
			player = new Player(getApplicationContext(), mRenderer, height);
			mRenderer.addMesh(player);
			
			level = new Level(context, mRenderer, width, height);
			
			CounterBackground = context.getResources().getDrawable(R.drawable.counterbg);
			CounterBackground.setBounds(0, 0, 128, 16);
			
			Counter = new RHDrawable(20, height-20-20, 1, 140, 20); //upscaling from 128/16 texture
			updateCounterTexture(mContext);
			mRenderer.addMesh(Counter);
			
			
			Thread rHThread = new Thread(this);
			rHThread.start();
		}

		public void run() {
			// wait a bit for everything to load
			try{ Thread.sleep(750); }
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
			int RunUnitCounterUpdate=20;
			while(true){
				if (player.update(level.getBlockData())) {
						level.update();
				} else {
					if(player.getPosY() < 0){
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
				if(player.collidedWithObstacle(level.getObstacleData(),level.getLevelPosition()) ){
					level.lowerSpeed();
				}

				if(RunUnitCounterUpdate==0){
					updateCounterTexture(mContext);
					RunUnitCounterUpdate=20;
				}
				RunUnitCounterUpdate--;
				
				//postInvalidate();
				try{ Thread.sleep(10); }
				catch (InterruptedException e)
				{
					e.printStackTrace();
				}
			}
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
		public void updateCounterTexture(Context context){
			// Create an empty, mutable bitmap
			CounterBitmap = Bitmap.createBitmap(128, 16, Bitmap.Config.ARGB_4444);
			// get a canvas to paint over the bitmap
			CounterCanvas = new Canvas(CounterBitmap);
			CounterBitmap.eraseColor(0);

			// get a background image from resources
			// note the image format must match the bitmap format
			CounterBackground.draw(CounterCanvas); // draw the background to our bitmap

			// draw the text centered
			CounterCanvas.drawText("Your Score: " + Integer.toString(level.getScoreCounter()), 5, 14, paint);
			
			Counter.loadBitmap(CounterBitmap);
		}
		
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
						scoreWasSaved=false;
						deathSoundPlayed=false;
						SoundManager.playSound(1, 1);
					}
					else if(saveButton.isClicked( event.getX(), Util.getInstance().toScreenY((int)event.getY())  ) && !scoreWasSaved){
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
