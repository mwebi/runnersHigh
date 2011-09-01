package com.runnershigh;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

public class Menu extends Activity {
	MediaPlayer menuLoop;
	private Toast loadMessage;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	requestWindowFeature(Window.FEATURE_NO_TITLE);  
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.menu); 
        

		loadMessage = Toast.makeText(getApplicationContext(), "Game loading", 2000 );
		loadMessage.setGravity(Gravity.CENTER|Gravity.CENTER, 0, 0);
        
        /*
        menuLoop = MediaPlayer.create(getApplicationContext(), R.raw.menu);  
        menuLoop.setLooping(true);
        menuLoop.seekTo(0);
        menuLoop.setVolume(0.5f, 0.5f);
        menuLoop.start();
        */
    }
    
    public void playGame(View view) {

		// Loading Toast
		//loadMessage.show();

    	Settings.SHOW_FPS = false;
    	Intent myIntent = new Intent (this, main.class);
    	startActivityForResult(myIntent, 0);
    }
    
    public void playGameWithFPS(View view) {

		// Loading Toast
		//loadMessage.show();
    	Settings.SHOW_FPS = true;
		
    	Intent myIntent = new Intent (this, main.class);
    	startActivityForResult(myIntent, 0);
    }
    
    public void showScore(View view) {
    	Intent myIntent = new Intent (this, HighScoreActivity.class);
    	startActivity (myIntent);
    }
    
    public void showInfo(View view) {
    	Intent myIntent = new Intent (this, Info.class);
    	startActivity (myIntent);
    }
    
    protected void onActivityResult (int requestCode, int resultCode, Intent data) {
    	if (resultCode == 1) {
    		showDialog(1);
    	}
    }
    
    protected Dialog onCreateDialog(int id) {
    	return new AlertDialog.Builder(this)
		  .setTitle("Error while changing view")
		  .setMessage("System needs some time to free memory. Please try again in 10 seconds.")
		  .setCancelable(true)
		  .create();
    }
}
