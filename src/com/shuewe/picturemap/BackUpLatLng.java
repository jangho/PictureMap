/*Class to store old coordinates in case of adding new ones.
 * author: shuewe87@gmail.com
 * 
 * */
package com.shuewe.picturemap;

import java.util.ArrayList;
import java.util.List;

import com.google.android.gms.maps.model.LatLng;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class BackUpLatLng {

	int id;
	List<Double> lat= new ArrayList<Double>();
	List<Double> lng= new ArrayList<Double>();
	String path; 
	/*
	 * Checks if given path is stored in database and gives results for old coordinates to lat and lng 
	 */
	public BackUpLatLng(Context con, String pa){
		this.id=-1;
		DatabaseHandler handler= new DatabaseHandler(con);
		SQLiteDatabase db = handler.getWritableDatabase();
		Cursor cur = db.rawQuery("SELECT * FROM BackUP WHERE pfad='" + pa +"' ORDER BY id ASC",null);
		while(cur.moveToNext()){
			this.id=cur.getInt(0);
			lat.add(cur.getDouble(2));
			lng.add(cur.getDouble(3));
		}
		while(lat.size()>3){
			lat.remove(1);
			lng.remove(1);
		}
		cur.close();
		db.close();
		handler.close();
	}
	/*
	 * writes new coordinates to db
	 */
	public static void writeDb(Context c,String pa,double lat, double lng){
		DatabaseHandler handler= new DatabaseHandler(c);
		SQLiteDatabase db = handler.getWritableDatabase();
		ContentValues val = new ContentValues();
		val.put("lat", lat);
		val.put("lng", lng);
		val.put("pfad", pa);
		db.insert("BackUP", null, val);
		db.close();
		handler.close();
	}
	public List<Double> getLat(){
		return lat;
	}
	public List<Double> getLng(){
		return lng;
	}
	/*Checks if new coordinates are equivalent to old ones. If yes, the old one will be removed
	 */
	public List<Double> getLat(LatLng a){
		List<Double> resLat= new ArrayList<Double>();
		resLat.addAll(lat);
		for(int i=lat.size()-1;i>=0;i+=-1){
			if(new LatLng(lat.get(i),lng.get(i)).equals(a)){
				resLat.remove(i);
			}
		}
		return resLat;
	}
	public List<Double> getLng(LatLng a){
		List<Double> resLng= new ArrayList<Double>();
		resLng.addAll(lng);
		for(int i=lng.size()-1;i>=0;i+=-1){
			if(new LatLng(lat.get(i),lng.get(i)).equals(a)){
				resLng.remove(i);
			}
		}
		return resLng;
	}
}
