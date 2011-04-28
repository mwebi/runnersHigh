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

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
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
        score = (savedInstanceState == null) ? null : (Integer) savedInstanceState.getSerializable("score");
		if (score == null) {
			Bundle extras = getIntent().getExtras();
			score = extras != null ? extras.getInt("score") : null;
		}
		
		// Get Last Saved Name
		Cursor cursor = highScoreAdapter.fetchLastEntry();
		startManagingCursor(cursor);

		if(cursor.getCount() > 0) {
			nameField.setText(cursor.getString(cursor.getColumnIndexOrThrow(highScoreAdapter.KEY_NAME)));
		}
		cursor.close();
		
		scoreField.setText(score.toString()); 
    }
    
    
    // ---------------------------------------------------
    // Save Entry
    private void saveState() {
    	String name 	=  nameField.getText().toString();
        String score 	=  scoreField.getText().toString();   
        CheckBox checkbox = (CheckBox) findViewById(R.id.postOnline);
        
        Log.i("onSAVE", " name: " + name + " Score: " + score);
            
        // Insert into Database
        if(name.length() > 0) {
        	
        	// Save score online
        	if(checkbox.isChecked()) {        	      		
        		
        		if(!isOnline()) {
        			highScoreAdapter.toastMessage(R.string.hs_error_no_internet);
        			Log.i("isOffline", "jo");
        		} else {
	        		// Create a new HttpClient and Post Header
	        	    HttpClient httpclient = new DefaultHttpClient();
	        	    HttpPost httppost = new HttpPost("http://rh.fidrelity.at/post/post_highscore.php");
	
	        	    try {
	        	        // Add your data
	        	        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
	        	        nameValuePairs.add(new BasicNameValuePair("name", name));
	        	        nameValuePairs.add(new BasicNameValuePair("score", score));
	        	        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
	
	        	        // Execute HTTP Post Request
	        	        HttpResponse response = httpclient.execute(httppost);
	        	        getOnlineHighscore();
	
	        	    } catch (ClientProtocolException e) {
	        	        // TODO Auto-generated catch block
	        	    } catch (IOException e) {
	        	        // TODO Auto-generated catch block
	        	    }    		
        		}
        	}
        	
        	// Save score locally
        	long id = highScoreAdapter.createHighscore(score, name);
        	highScoreAdapter.close();
        	
        	setResult(RESULT_OK);
        	finish();
        } else {
        	highScoreAdapter.toastMessage(R.string.hs_error_name_empty);
        }
    }
    
    private void getOnlineHighscore() {
    	HttpParams httpParams = new BasicHttpParams();
    	HttpConnectionParams.setConnectionTimeout(httpParams, 2000);
    	HttpConnectionParams.setSoTimeout(httpParams, 2000);
    	HttpClient client = new DefaultHttpClient(httpParams);

    	HttpGet request = new HttpGet("http://rh.fidrelity.at/best.php?number=10");
    	Log.i("get request", request.toString());
    }
    
	public boolean isOnline() {
		 ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		 return cm.getActiveNetworkInfo().isConnectedOrConnecting();
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
