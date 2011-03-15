package com.runnershigh;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class Menu extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu);   
    }
    
    public void playGame(View view) {
    	Intent myIntent = new Intent (this, main.class);
    	startActivity (myIntent);
    }
    
    public void showScore(View view) {
    	Intent myIntent = new Intent (this, HighScoreActivity.class);
    	startActivity (myIntent);
    }
    
    public void showInfo(View view) {
    	Intent myIntent = new Intent (this, Info.class);
    	startActivity (myIntent);
    }
}
