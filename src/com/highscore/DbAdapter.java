/*
 * DbAdapter
 * runnersHigh 1.0
 * 
 * _DESCRIPTION:
 * 	Parent class for DbAdapter classes
 * 	opens or closes db connection
 */
package com.highscore;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

public class DbAdapter {

	protected DBManager mDbHelper;
	protected SQLiteDatabase mDb;
    protected Context mCtx;

    protected DbAdapter(Context ctx) {
        this.mCtx = ctx;
    }

    public DbAdapter open() throws SQLException {
        mDbHelper = new DBManager(mCtx);
        mDb = mDbHelper.getWritableDatabase();
        
        // Enable foreign key constraints
        if (!mDb.isReadOnly()) {
        	mDb.execSQL("PRAGMA foreign_keys=ON;");
        }
        return this;
    }

    public void close() {
        mDbHelper.close();
    }
    
    // -------------------------------------------------------------
    // Toast Message
    public void toastMessage(int msg) {
		Toast.makeText(this.mCtx, msg, Toast.LENGTH_SHORT).show();
    }
    
    // Returns number of table Where row = rowId
    public int getNumber(String table, String row, int rowId) {
    	int number = 0;

    	String queryTable = table.length() == 0 ? "sap_todos" : table ;
    	
    	Cursor mCursor =
            mDb.query(false, queryTable, new String[] { "_id" }, row + " = " + rowId, null,
                    null, null, null, null);
    	number = mCursor.getCount();
        mCursor.close();
        return number;	    	
    }
}