/*
 * DbManager
 * runnersHigh 1.0
 * 
 * _DESCRIPTION:
 * 	Creates database tables; Updates database
 * 	To update database table changes increase DATABASE_VERSION + 1
 */

package com.highscore;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

class DBManager extends SQLiteOpenHelper {

	private static final String DATABASE_NAME = "SAPdb";
	private static final int DATABASE_VERSION = 1;
	
	/**
	 * Database creation sql statement
	 */
	private static final String DB_CREATE_TERMS =
	    "CREATE TABLE sap_terms " +
	    "(_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
	    "title TEXT NOT NULL," +
	    "isActive INTEGER NOT NULL" +
	    ");";
	
    DBManager(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    	db.execSQL(DB_CREATE_TERMS);
    	
        // Add term on first creation
        db.execSQL("INSERT INTO sap_terms (title, isActive) VALUES('Schuljahr', 1)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w("DBManagerClass", "Upgrading database from version " + oldVersion + " to "
                + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS notes");
        db.execSQL("DROP TABLE IF EXISTS sap_terms");
        onCreate(db);
    }
}
