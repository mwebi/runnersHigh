/*
 * HighscoreActivity
 * runnersHigh 1.0
 * 
 * _DESCRIPTION:
 * 	Highscore Activity itself - shows highscores of user 
 */

package com.runnershigh;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
	private static final String POST_HIGHSCORE_URL = "http://rh.fidrelity.at/post/post_highscore.php";
	
	private boolean isOnlineView = false;
	
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
        		clearHighscore();        		
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
    		
	        Cursor cursor = highScoreAdapter.fetchScores(SHOW_LIMIT);
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
    	
    	if(!isOnline()) {
    		Log.i("", "not online");
    		highScoreAdapter.toastMessage(R.string.hs_error_no_internet);
    	} else {
    	
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
					String score;
					
	    			for(int i = 0; i < jArray.length(); i++) {
	    				name = jArray.getJSONObject(i).getString("name");
	    				score = jArray.getJSONObject(i).getString("score");
	    				
	    				onlineData[i] = score + "   " + name;
	    			}             
	
	    			fillData(onlineData);
	    		}
	    	} catch (Exception e) {
	    		e.printStackTrace();
	    	}
    	}
    }
    
    // ---------------------------------------------------------
    // Switch Views
    private void switchHighScoreButton(String state) {
    	
    	final Button getOnlineHS = (Button) findViewById(R.id.onlineHSButton);
    	
    	if(state == "ONLINE") {
    		
    		getOnlineHS.setText(R.string.hs_bttn_getonline);
	        getOnlineHS.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					loadOnlineHighscore(10);
				}
			});
	        isOnlineView = true;
	        
    	} else {    		

    		getOnlineHS.setText(R.string.hs_bttn_getlocal);			
            getOnlineHS.setOnClickListener(new View.OnClickListener() {
    			public void onClick(View v) {
    				fillData(empty);
    			}
    		});
            isOnlineView = false;
    	}
    }
    
    // ---------------------------------------------------------
    // Truncate HighScore Database Table
    private void clearHighscore() {
    	
    	AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle("Clear Highscore");
        alert.setMessage("Do you really want to delete all local Highscores ?");        
  
        alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int whichButton) {          
        	highScoreAdapter.clear();
        	fillData(empty);
        	}
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialog, int whichButton) {
            // Canceled.
          }
        });               	
    }
    
    // ---------------------------------------------------------
    // onClick Item list element
    @Override
    protected void onListItemClick(ListView l, View v, int position, final long id) {
        super.onListItemClick(l, v, position, id);
       
        if(isOnlineView == true)
        	return;    
        
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle("Highscore");
        alert.setMessage("Push this score online ?");

        // Fetch data from database
        Cursor cursor = highScoreAdapter.fetchSingleScore(id);
        final String name = cursor.getString(cursor.getColumnIndex(HighscoreAdapter.KEY_NAME));
        final String score = cursor.getString(cursor.getColumnIndex(HighscoreAdapter.KEY_SCORE));
        final int isonline = cursor.getInt(cursor.getColumnIndex(HighscoreAdapter.KEY_ISONLINE));
       
        final TextView input = new TextView(this);
        input.setText("Name: " + name + " Score: " + score + " isOnline: " + isonline);
        alert.setView(input);

        // OK
        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int whichButton) {          
        	// Push score online
        	if(isonline == 1) {
        		highScoreAdapter.toastMessage(R.string.hs_already_pushed);
        	} else {
        		
        		/**TODO: CHECK IF USER IS ONLINE **/
        		
        		// Create a new HttpClient and Post Header
        	    HttpClient httpclient = new DefaultHttpClient();
        	    HttpPost httppost = new HttpPost(POST_HIGHSCORE_URL);

        	    try {
        	        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
        	        nameValuePairs.add(new BasicNameValuePair("name", name));
        	        nameValuePairs.add(new BasicNameValuePair("score", score));
        	        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

        	        HttpResponse response = httpclient.execute(httppost);        	       
        	        highScoreAdapter.updateScore(id, 1);
        	        highScoreAdapter.toastMessage(R.string.hs_pushed_online);
        	    } catch (ClientProtocolException e) {
        	        // TODO Auto-generated catch block
        	    } catch (IOException e) {
        	        // TODO Auto-generated catch block
        	    }        		
        	}        	
          }
        });
        
        // CANCEL
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialog, int whichButton) {
            // Canceled.
          }
        });
        cursor.close();
        alert.show();        
    }
    
    // ---------------------------------------------------------
	// Check if user is connected to the internet
	public boolean isOnline() {		
	    ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo ni = cm.getActiveNetworkInfo();
	    if (ni != null && ni.isAvailable() && ni.isConnected()) {
	        return true;
	    } else {
	        return false; 
	    }
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
