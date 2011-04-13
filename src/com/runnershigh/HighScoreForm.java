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
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.database.Cursor;
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
        		
        		// Create a new HttpClient and Post Header
        	    HttpClient httpclient = new DefaultHttpClient();
        	    HttpPost httppost = new HttpPost("http://rh.fidrelity.at/");

        	    try {
        	        // Add your data
        	        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
        	        nameValuePairs.add(new BasicNameValuePair("name", name));
        	        nameValuePairs.add(new BasicNameValuePair("score", score));
        	        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

        	        // Execute HTTP Post Request
        	        HttpResponse response = httpclient.execute(httppost);
        	        Log.i("SEND", httppost.toString());
        	    } catch (ClientProtocolException e) {
        	        // TODO Auto-generated catch block
        	    } catch (IOException e) {
        	        // TODO Auto-generated catch block
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
