/*
 * Class to put data into a local database which are about user ids, or user behavior
 * to every String (name) a double value and a string can be stored
 * 
 * for example: EreignisClass(...,EreignisClass.Eid)
 * makes an object with id=-1 if no User_id was stored yet.
 * if id>1 EreignisClass.getVal() returns the stored user id
 */
package com.shuewe.picturemap;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class EreignisClass {
	
	Context c;
	String Def, ValString;
	int id;
	double val;
	public static long OneDay=700;
	public static long HalfDay=350;
	public static String  Residual= "Resi";
	public static String  Eid="id";
	public static String writeOK="wOK";

	public EreignisClass(Context con, String s){
	//id=-1 stands for an empty database result
	//s is a String which describes the kind of information to be stored	
		this.id=-1;
		this.c=con;
		this.Def=s;
		DatabaseHandler handler= new DatabaseHandler(c);
		SQLiteDatabase db = handler.getWritableDatabase();
		Cursor cur = db.rawQuery("SELECT * FROM Ereignis WHERE ereignis='" + s +"' ORDER BY id",null);
		//if there is a result set id>-1 and get information
		if(cur.moveToFirst()){
			this.id=cur.getInt(0);
			this.val=cur.getDouble(2);
			this.ValString=cur.getString(3);
		}
		cur.close();
		db.close();
		handler.close();
	}
	//Get and set Val and ValString for chosen entry
	public void setVal(Double v){
		this.val=v;
		
	}
	public void setValString(String s){
		this.ValString=s;
	}
	
	
	public int getID(){
		return this.id;
		
	}
	public String getValString(){
		return this.ValString;
	}
	public Double getVal(){
		return this.val;
	}
	//save to local db
	public void inDB(){
		List<String> multipleItem = new ArrayList<String>();
		DatabaseHandler handler= new DatabaseHandler(c);
		SQLiteDatabase db = handler.getWritableDatabase();
		ContentValues val = new ContentValues();
		val.put("ereignis", this.Def);
		val.put("val", this.val);
		val.put("valString", this.ValString);
		if(this.id==-1){
			db.insert("Ereignis", null, val);
		}else{
			if(multipleItem.contains(this.Def)){
				db.insert("Ereignis", null, val);
			}else{
				db.update("Ereignis", val, "ereignis='"+this.Def+"'", null);
			}
		}
		db.close();
		handler.close();
	}
	
}
