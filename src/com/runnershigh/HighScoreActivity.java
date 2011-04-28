/*
 * HighscoreActivity
 * runnersHigh 1.0
 * 
 * _DESCRIPTION:
 * 	Highscore Activity itself - shows highscores of user 
 */

package com.runnershigh;

import android.app.ListActivity;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.SimpleCursorAdapter;

import com.highscore.HighscoreAdapter;

public class HighScoreActivity extends ListActivity {
	
	private HighscoreAdapter highScoreAdapter = null;
	private static final String SHOW_LIMIT = "10";
	
	// ---------------------------------------------------
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	requestWindowFeature(Window.FEATURE_NO_TITLE);  
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
        super.onCreate(savedInstanceState);
        setContentView(R.layout.highscore);
        
        highScoreAdapter = new HighscoreAdapter(this);
        highScoreAdapter.open();
        
        // Clear Button
        Button clearButton = (Button) findViewById(R.id.clearButton);
        clearButton.setOnClickListener(new View.OnClickListener() {
        	public void onClick(View view) {
        		highScoreAdapter.clear();
        		fillData();
        	}
        });  
        
        fillData();
        
        registerForContextMenu(getListView());
    }
    
    // ---------------------------------------------------------
    // Fetch highscore from database table and put it into the listView
    private void fillData() {
        Cursor cursor = highScoreAdapter.fetchScores(SHOW_LIMIT);
        startManagingCursor(cursor);
        Log.i("FILLDATA"," Amount:" + cursor.getCount());
                
        // Create an array to specify the fields we want to display in the list
        String[] from = new String[]{ highScoreAdapter.KEY_SCORE, highScoreAdapter.KEY_NAME };

        // and an array of the fields we want to bind those fields to
        int[] to = new int[]{ R.id.score, R.id.name };
        
        // Creates the backing adapter for the ListView.
        SimpleCursorAdapter adapter
            = new SimpleCursorAdapter(
                      this,                             // The Context for the ListView
                      R.layout.list_row,          		// Points to the XML for a list item
                      cursor,                           // The cursor to get items from
                      from,
                      to
              );

        // Sets the ListView's adapter to be the cursor adapter that was just created.
        setListAdapter(adapter);
    }
    
    // ---------------------------------------------------------
    @Override    
    protected void onDestroy() {        
        super.onDestroy();
         
        if (highScoreAdapter != null) {
        	highScoreAdapter.close();
        }
    }
}
