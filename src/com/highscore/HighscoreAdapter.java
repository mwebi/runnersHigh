/*
 * DbManager
 * runnersHigh 1.0
 * 
 * _DESCRIPTION:
 * 	Creates database tables; Updates database
 * 	To update database table changes increase DATABASE_VERSION + 1
 * 
 * 	Child class of DbAdapter
 */

package com.highscore;

import android.content.Context;

public class HighscoreAdapter extends DbAdapter {
	
	private static final String DATABASE_TABLE 	= "sap_grades";
	
	public static final String KEY_ROWID 		= "_id";
    public static final String KEY_GRADE 		= "grade";
    public static final String KEY_DATE 		= "date";

	protected HighscoreAdapter(Context ctx) {
    	super(ctx);
        this.mCtx = ctx;
	}
}
