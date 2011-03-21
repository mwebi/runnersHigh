/*
 * HighscoreActivity
 * runnersHigh 1.0
 * 
 * _DESCRIPTION:
 * 	Highscore form Activity - shows input field to save score and name 
 */

package com.runnershigh;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.highscore.HighscoreAdapter;

public class HighScoreForm extends Activity {
	
	private HighscoreAdapter highScoreAdapter = null;
	private EditText nameField;
	private TextView scoreField;
	private Integer score;
	// ---------------------------------------------------
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	requestWindowFeature(Window.FEATURE_NO_TITLE);  
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
        super.onCreate(savedInstanceState);
        setContentView(R.layout.highscoreform);
        
        highScoreAdapter = new HighscoreAdapter(this);
        highScoreAdapter.open();
        
        nameField = (EditText) findViewById(R.id.title);
        scoreField = (TextView) findViewById(R.id.score);
        Button confirmButton = (Button) findViewById(R.id.confirm);

        confirmButton.setOnClickListener(new View.OnClickListener() {
        	public void onClick(View view) {
        		saveState();
        	}
        });        
        
        // Get Score
        Integer score = (savedInstanceState == null) ? null : (Integer) savedInstanceState.getSerializable("score");
		if (score == null) {
			Bundle extras = getIntent().getExtras();
			score = extras != null ? extras.getInt("score") : null;
		}
		
		Log.i("SCORE", score.toString());
		scoreField.setText(score.toString());        
    }
    
    
    // ---------------------------------------------------
    // Save Entry
    private void saveState() {
    	String name 	=  nameField.getText().toString();
        String score 	=  scoreField.getText().toString();       
        
        Log.i("onSAVE", " name: " + name + " Score: " + score);
            
        // Insert into Database
        if(name.length() > 0) {
        	long id = highScoreAdapter.createHighscore(score, name);
            setResult(RESULT_OK);
            finish();
        } else {
        	highScoreAdapter.toastMessage(R.string.hs_error_name_empty);
        }
    }
    
    // ---------------------------------------------------------
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        saveState();
    }

    @Override
    protected void onPause() {
        super.onPause();
       // saveState();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
    
    // Close DatabaseHelper
    @Override    
    protected void onDestroy() {        
        super.onDestroy();
         
        if (highScoreAdapter != null) {
        	highScoreAdapter.close();
        }
    }
}
