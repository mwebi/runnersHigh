/*
 * HighscoreActivity
 * runnersHigh 1.0
 * 
 * _DESCRIPTION:
 * 	Highscore Activity itself - shows highscores of user 
 */

package com.runnershigh;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.highscore.HighscoreAdapter;

public class HighScoreActivity extends ListActivity {
	
	private HighscoreAdapter highScoreAdapter = null;
	private static final String SHOW_LIMIT = "10";
	private final String[] empty = new String[0];	
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
        		fillData(empty);
        	}
        });        
       
        switchHighScoreButton("ONLINE");        
        registerForContextMenu(getListView());
        
        fillData(empty);      
    }
    
    // ---------------------------------------------------------
    // Fetch highscore from database table and put it into the listView
    private void fillData(String[] onlineData) {
    	
    	// Online List
    	if(onlineData.length > 0) {
    		setListAdapter(new ArrayAdapter<String>(this, R.layout.list_single_row, onlineData));
    		switchHighScoreButton("LOCAL");
    	         		
    	// Local List
    	} else {   
    		
	        Cursor cursor = highScoreAdapter.fetchScores(SHOW_LIMIT, 0);
	        startManagingCursor(cursor);
	       	                
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
	        switchHighScoreButton("ONLINE");
    	}
    }
       
    // ---------------------------------------------------------
    // Load online Highscore
    public void loadOnlineHighscore(int size) {
    	
    	final String[] onlineData = new String[size]; // {}; 
    	
    	try {
    		HttpClient client = new DefaultHttpClient();  
    		String getURL = "http://rh.fidrelity.at/best.php?size=" + Integer.toString(size);
    		HttpGet get = new HttpGet(getURL);
    		// query data from server
    		HttpResponse responseGet = client.execute(get); 
    		HttpEntity resEntityGet = responseGet.getEntity();  
    		if (resEntityGet != null) {
    			JSONArray jArray = new JSONArray(EntityUtils.toString(resEntityGet));
    			
				String name;
				int score;
				
    			for(int i = 0; i < jArray.length(); i++) {
    				name = jArray.getJSONObject(i).getString("name");
    				score = Integer.parseInt(jArray.getJSONObject(i).getString("score"));
    				
    				onlineData[i] = jArray.getJSONObject(i).getString("score") + " " + name;
    				
    				Log.i("GET ONLINE HS", name + " with the score " + score);	
    			}             

    			fillData(onlineData);
    		}
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    }
    
    // ---------------------------------------------------------
    // HELPER METHODS
    private void switchHighScoreButton(String state) {
    	
    	final Button getOnlineHS = (Button) findViewById(R.id.onlineHSButton);
    	
    	if(state == "ONLINE") {
    		
    		getOnlineHS.setText(R.string.hs_bttn_getonline);
	        getOnlineHS.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					loadOnlineHighscore(10);
				}
			});
	        
    	} else {    		

    		getOnlineHS.setText(R.string.hs_bttn_getlocal);			
            getOnlineHS.setOnClickListener(new View.OnClickListener() {
    			public void onClick(View v) {
    				fillData(empty);
    			}
    		});
    	}
    }
    
    // ---------------------------------------------------------
    // onClick Item list element
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        //i.putExtra(GradeDbAdapter.KEY_ROWID, id);
        //startActivityForResult(i, ACTIVITY_EDIT);
        
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle("Highscore");
        alert.setMessage("Push this score online ?");

        Cursor cursor = highScoreAdapter.fetchSingleScore(id);
        String ID = cursor.getString(cursor.getColumnIndex(HighscoreAdapter.KEY_ROWID));
        String name = cursor.getString(cursor.getColumnIndex(HighscoreAdapter.KEY_NAME));
        String score = cursor.getString(cursor.getColumnIndex(HighscoreAdapter.KEY_SCORE));
       
        final TextView input = new TextView(this);
        input.setText("Name: " + name + " Score: " + score);
        alert.setView(input);

        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int whichButton) {          
        	// Push score online
          }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialog, int whichButton) {
            // Canceled.
          }
        });

        alert.show();
        
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
