/*
 * Thread used from MainActivity to read out gps information from pathes.
 * Results get passed to MainActivity by a handler 
 */

package com.shuewe.picturemap;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.sanselan.ImageReadException;
import android.media.ExifInterface;
import android.os.Handler;
import android.os.Message;

public class Importer extends Thread {
	
	List<String> path = new ArrayList<String>();
	Handler handler;
	List<Double> latlist=new ArrayList<Double>();
	List<Double> lnglist= new ArrayList<Double>();
	List<Integer> ori = new ArrayList<Integer>();
	
	public Importer(Handler handler,List<String> p,List<Integer> o){
		path=p;
		this.handler=handler;
		this.ori=o;
	}
	
	public void run(){
		for(int i=0;i<path.size();i++){
			//use ExifInterface to read
			//getExifInterface(path.get(i));
			try {
			//use the Sanselan package to read
				getSanselan(path.get(i),ori.get(i));
			} catch (ImageReadException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		Message msg= new Message();
		msg.obj="ende";
		handler.sendMessage(msg);
		
	}
	//Method which uses Sanselan and returns the resutls vie handler to MainActivity
	private void getSanselan(String p, Integer o) throws ImageReadException, IOException{
		double[] latlng = Util.getLatLng(p); 
		Message msg= new Message();
		String[] m = new String[4];
        m[0]=String.valueOf(latlng[0]);
        m[1]=String.valueOf(latlng[1]);
        m[2]=p;
        m[3]=String.valueOf(o);
        msg.obj=m;
        msg.arg1=2;
        handler.sendMessage(msg);
	}
	
	//Method which uses ExifInterface and returns the resutls vie handler to MainActivity
	private void getExifInterface(String p){
		Boolean ok=true;
		try {
			ExifInterface e =new ExifInterface(p);
			if(e.getAttribute(ExifInterface.TAG_IMAGE_LENGTH).equals("0")){
				ok=false;
			}
			float[] he= new float[2];
			e.getLatLong(he);			   
		if(ok){
			 String[] m = new String[3];
				m[0]=String.valueOf(he[0]);
				m[1]=String.valueOf(he[1]);
				m[2]=p;
				Message msg= new Message();
				msg.obj=m;
				msg.arg1=2;
				handler.sendMessage(msg);
		}
		} catch (IOException e) {
			e.printStackTrace();
	    }
	}
}
