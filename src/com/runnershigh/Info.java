package com.runnershigh;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

public class Info extends Activity{
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
		
        super.onCreate(savedInstanceState);
        setContentView(R.layout.info);

        //requestWindowFeature(Window.FEATURE_NO_TITLE);  
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }
    
    public void visitWebsite(View view) {
    	Intent browserIntent = new Intent("android.intent.action.VIEW", Uri.parse(Settings.WEBSITE_URL));
    	startActivity(browserIntent);
    }
}
