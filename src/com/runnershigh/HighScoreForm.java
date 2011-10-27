/*
 * HighscoreActivity
 * runnersHigh 1.0
 * 
 * _DESCRIPTION:
 * 	Highscore form Activity - shows input field to save score and name 
 */

package com.runnershigh;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.highscore.HighscoreAdapter;

public class HighScoreForm extends Activity {
	
	private HighscoreAdapter highScoreAdapter = null;
	private EditText nameField;
	private TextView scoreField;
	private Integer score;
	private CheckBox checkboxPushOnline;
	// ---------------------------------------------------
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	requestWindowFeature(Window.FEATURE_NO_TITLE);  
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
        super.onCreate(savedInstanceState);
        setContentView(R.layout.highscoreform);
        
        highScoreAdapter = new HighscoreAdapter(this);
        highScoreAdapter.open();
        
        // Find form elements
        nameField = (EditText) findViewById(R.id.title);
        scoreField = (TextView) findViewById(R.id.score);
        checkboxPushOnline = (CheckBox) findViewById(R.id.postOnline);
        Button confirmButton = (Button) findViewById(R.id.confirm);
     
        // Performe save
        confirmButton.setOnClickListener(new View.OnClickListener() {
        	public void onClick(View view) {
        		saveState();
        	}
        });        
        
        // Set Checkbox true if device is connected to internet
        if(isOnline())
        	checkboxPushOnline.setChecked(true);
        
        // Get Score
        score = (savedInstanceState == null) ? null : (Integer) savedInstanceState.getSerializable("score");
		if (score == null) {
			Bundle extras = getIntent().getExtras();
			score = extras != null ? extras.getInt("score") : null;
		}
		scoreField.setText(score.toString()); 
		
		// Get Last Saved Name
		Cursor cursor = highScoreAdapter.fetchLastEntry();
		startManagingCursor(cursor);
		if(cursor.getCount() > 0) {
			nameField.setText(cursor.getString(cursor.getColumnIndexOrThrow(HighscoreAdapter.KEY_NAME)));
		}
		cursor.close();		
    }
    
    // ---------------------------------------------------
    // Save Entry
    private void saveState() {
    	String name 	=  nameField.getText().toString();
        String score 	=  scoreField.getText().toString();

        int isonline = 0;
        
        if(name.length() > 0) { 
        
            try {
                highScoreAdapter.createHighscore(score, name, isonline);
            } catch (Exception e) {
                Log.w(Settings.LOG_TAG, "create highscore threw an exception");
                Log.w(Settings.LOG_TAG, "Maybe a double attempt? HTC Sensation does that for example");
                return;
            }       	
            
        	// Save score online
        	if(checkboxPushOnline.isChecked()) {        	      		
        		
        		if(!isOnline()) {
        			highScoreAdapter.toastMessage(R.string.hs_error_no_internet);
        		} else {
	        		// Create a new HttpClient and Post Header
	        	    HttpClient httpclient = new DefaultHttpClient();
	        	    HttpPost httppost = new HttpPost(Settings.HIGHSCORE_POST_URL);
	
	        	    try {
	        	        // Add data
	        	        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
	        	        nameValuePairs.add(new BasicNameValuePair("name", name));
	        	        nameValuePairs.add(new BasicNameValuePair("score", score));
	        	        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
	
	        	        httpclient.execute(httppost);
	
	        	    } catch (ClientProtocolException e) {
	        	        // TODO Auto-generated catch block
	        	    } catch (IOException e) {
	        	        // TODO Auto-generated catch block
	        	    }
	        	    
	        	    isonline = 1;
        		}
        	}
        	
        	highScoreAdapter.close();
        	
        	setResult(RESULT_OK);
        	finish();
        } else {
        	highScoreAdapter.toastMessage(R.string.hs_error_name_empty);
        }
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
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        saveState();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override    
    protected void onDestroy() {        
        super.onDestroy();
         
        if (highScoreAdapter != null) {
        	highScoreAdapter.close();
        }
    }
}
