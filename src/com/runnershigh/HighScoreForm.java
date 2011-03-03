/*
 * HighscoreActivity
 * runnersHigh 1.0
 * 
 * _DESCRIPTION:
 * 	Highscore Activity itself - shows highscores of user 
 */

package com.runnershigh;

import org.w3c.dom.Text;

import android.app.Activity;
import android.app.ListActivity;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.highscore.HighscoreAdapter;

public class HighScoreForm extends Activity {
	
	private HighscoreAdapter highScoreAdapter = null;
	private EditText nameField;
	private TextView scoreField;	
	
	// ---------------------------------------------------
    @Override
    public void onCreate(Bundle savedInstanceState) {
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
                setResult(RESULT_OK);
                finish();
        	}
        });        
    }
    
    // ---------------------------------------------------
    // Edit Form -> get data of the entry
    private void populateFields() {
    	/*
        if (mRowId != null && mRowId > 0) {        	
            Cursor note = highScoreAdapter.fetchScores(limit).fetchSingle(mRowId);
            startManagingCursor(note);           
            
            String getTitleValue = note.getString(note.getColumnIndexOrThrow(highScoreAdapter.KEY_TITLE));
            nameForm.setText(getTitleValue);
        } */
    }
    
    // ---------------------------------------------------
    // Save Entry
    private void saveState() {

    	String name 	=  nameField.getText().toString();
        String score 	=  scoreField.getText().toString();       
        
        Log.i("onSAVE", " name: " + name + " Score: " + score);
            
        // Insert into Database	    
	    long id = highScoreAdapter.createHighscore(score, name);	    
    }
    

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
        //populateFields();
    }
    
    // ---------------------------------------------------------
    // Close DatabaseHelper
    @Override    
    protected void onDestroy() {        
        super.onDestroy();
         
        if (highScoreAdapter != null) {
        	highScoreAdapter.close();
        }
    }
}
