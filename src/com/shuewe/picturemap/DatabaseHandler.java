/*Class to setup the internal db to store events for analytic reasons and for gps data*/
package com.shuewe.picturemap;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHandler extends SQLiteOpenHelper {
	
	private static String DB_NAME = "dbPicMap";
    private static int DATABASE_VERSION = 2;
	
    public DatabaseHandler(Context context) {
		super(context, DB_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		 db.execSQL("CREATE TABLE Ereignis(id INTEGER PRIMARY KEY AUTOINCREMENT, ereignis STRING, val DOUBLE, valString STRING)");
		 db.execSQL("CREATE TABLE BackUP(id INTEGER PRIMARY KEY AUTOINCREMENT, pfad STRING, lat DOUBLE, lng DOUBLE)");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.setVersion(newVersion);
		if(oldVersion<2){
			db.execSQL("CREATE TABLE BackUP(id INTEGER PRIMARY KEY AUTOINCREMENT, pfad STRING, lat DOUBLE, lng DOUBLE)");
		}
	} 
}