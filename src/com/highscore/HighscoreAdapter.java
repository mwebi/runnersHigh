/*
 * HighScoreAdapater
 * runnersHigh 1.0
 * 
 * _DESCRIPTION:
 * 	Table operations for 'rh_highscore'
 * 	Saves and returns highscores from database
 * 
 * 	Child class of DbAdapter
 * 
 * 	TODO: Delete entries, if there are more than SHOW_LIMIT
 */

package com.highscore;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

public class HighscoreAdapter extends DbAdapter {
	
	private static final String DATABASE_TABLE 	= "rh_highscore";
	
	public static final String KEY_ROWID 		= "_id";
    public static final String KEY_NAME 		= "name";
    public static final String KEY_SCORE 		= "score";

	public HighscoreAdapter(Context ctx) {
    	super(ctx);
        this.mCtx = ctx;
	}
	
	// -------------------------------------------------------
    // Create
    public long createHighscore(String score, String name) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_NAME, name);
        initialValues.put(KEY_SCORE, score);
                
        return mDb.insert(DATABASE_TABLE, null, initialValues);
    }

    // -------------------------------------------------------
    // Delete
    public boolean delete(long rowId) {
        return mDb.delete(DATABASE_TABLE, KEY_ROWID + "=" + rowId, null) > 0;
    }

    // -------------------------------------------------------
    // Show
    public Cursor fetchScores(String limit) {
    	return mDb.query(DATABASE_TABLE, 
        				new String[] { 	KEY_ROWID,
    									KEY_NAME,
    									KEY_SCORE
    								  },
    					null, null, null, null,
        				KEY_SCORE + " DESC", limit);
    }
}
