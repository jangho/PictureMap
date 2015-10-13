/*Activity to show a map from google with the chosen pictures
 * Author: shuewe87@gmail.com
 * 
 */
package com.shuewe.picturemap;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.shuewe.picturemap.R;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Display;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;

public class Anzeigen extends AppCompatActivity implements OnMarkerClickListener  {
	GoogleMap supportMap;
	SupportMapFragment supportmapfragment;
	Fragment fragment;
	private ImageView iv;
	List<Double> listelat=null;
	List<Double> listelng=null;
	List<String> pathlist=null;
	List<Integer> ori=null;
	SlidingMenu menu;
	Marker[] marker;
	Boolean hist=false;
	Boolean shown=false;
	Boolean swit=false;
	String curpath="";
	List<LatLng> posmarker=null;
	FullDialog dia;
	int curori,counter;
	private InterstitialAd interstitial;
 
    @SuppressWarnings({ "deprecation", "deprecation" })
	@SuppressLint("NewApi")
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        
        //Check for Android Version
        if(android.os.Build.VERSION.SDK_INT< 11){
			this.requestWindowFeature(Window.FEATURE_NO_TITLE);
			
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anzeigen);
        // Create the interstitial.
        interstitial = new InterstitialAd(this);
        interstitial.setAdUnitId("ca-app-pub-6437137290907604/7239922178");

        // Create ad request.
        AdRequest adRequest = new AdRequest.Builder().addTestDevice("D0579B02FB9B4FF5434915798CEFB644").build();

        // Begin loading your interstitial.
        interstitial.loadAd(adRequest);

        iv = (ImageView) findViewById(R.id.imView);
        List<Integer> notSet=new ArrayList<Integer>();
    	List<String> notSetpath=new ArrayList<String>();
    	List<Integer> notSetori = new ArrayList<Integer>();
		ori=new ArrayList<Integer>();
        listelat= new ArrayList<Double>();
        listelng=new ArrayList<Double>();
        posmarker= new ArrayList<LatLng>();
        pathlist=new ArrayList<String>();
        FragmentManager fmanager = getSupportFragmentManager();
        fragment = fmanager.findFragmentById(R.id.map);
        supportmapfragment = (SupportMapFragment)fragment;
        supportMap = supportmapfragment.getMap();
        supportMap.setOnMarkerClickListener(this);
        
        //Read intent
        Intent i= getIntent();
        ArrayList<String> lat= new ArrayList<String>(); 
        ArrayList<String> lng = new ArrayList<String>();
        Resources res= getResources();
        ArrayList<String> oristring = new ArrayList<String>();
        lat=i.getStringArrayListExtra("lat");
        lng= i.getStringArrayListExtra("lng");
        oristring = i.getStringArrayListExtra("ori");
        pathlist=i.getStringArrayListExtra("path");
        
        marker= new Marker[pathlist.size()];
        for(String s: oristring){
        	ori.add(Integer.parseInt(s));
        }
        for (String s:lat){
        	listelat.add(Double.parseDouble(s));
        }
        for (String s:lng){
        	listelng.add(Double.parseDouble(s));
        }
        for (int z=0; z<listelng.size();z++){
        	posmarker.add(new LatLng(listelat.get(z),listelng.get(z)));
        }
        for (int z=0;z<listelng.size();z++){
        	if(listelng.get(z)!=0){
        		marker[z]=supportMap.addMarker(new MarkerOptions().position(new LatLng(listelat.get(z),listelng.get(z))).title(String.valueOf(z+1))); 
        		String newmark="";
        	try {
    			ExifInterface e =new ExifInterface(pathlist.get(z));
    			if(e.getAttribute(ExifInterface.TAG_DATETIME)!=null){newmark=e.getAttribute(ExifInterface.TAG_DATETIME).replaceFirst(":", "/").replaceFirst(":", "/");}
    	    } catch (IOException e) {
    			e.printStackTrace();
    	    }
    		marker[z].setTitle(res.getString(R.string.bildnr)+" "+z);
    		marker[z].setSnippet(res.getString(R.string.taken)+" "+newmark);     	
        	}else{
        	notSet.add(z);
        	notSetpath.add(pathlist.get(z));
        	notSetori.add(ori.get(z));
        	}
        }
        if (savedInstanceState==null){
        	counter=0;
        	if(notSet.size()>0){
        		//Dialog for pictures without gps data
        		DialogUnset dialog = new DialogUnset(this,notSet,notSetpath, notSetori);
        		dialog.setTitle(R.string.titlenotset);
        		dialog.show();}
        		//Calculation of center position for the camera of the map
        		double centerlat=0;
        		double centerlng=0;
        		List<Double> xk,yk,zk;
        		xk= new ArrayList<Double>();
        		yk= new ArrayList<Double>();
        		zk= new ArrayList<Double>();
        		for (int u=0; u<listelng.size();u++){
        			double kuglat=Math.toRadians(90-listelat.get(u));
        			double kuglng=Math.toRadians(listelng.get(u));
        			xk.add(Math.sin(kuglat)*Math.cos(kuglng));	
        			yk.add(Math.sin(kuglat)*Math.sin(kuglng));
        			zk.add(Math.cos(kuglat));
        		}
        		double centerx=0, centery=0, centerz=0;
        		for(int z=0;z<listelng.size();z++){
        			centerx+=xk.get(z);
        			centery+=yk.get(z);
        			centerz+=zk.get(z);
        		}
        		int faksumme=listelng.size();
        		if (faksumme!=0){
        			centerx=centerx/faksumme;
        			centery=centery/faksumme;
        			centerz=centerz/faksumme;
        		}
        		centerlat=90-Math.toDegrees(Math.acos(centerz/Math.sqrt(centerx*centerx+centery*centery+centerz*centerz)));
        		centerlng=Math.toDegrees(Math.atan2(centery,centerx));  
        		CameraUpdate cu= CameraUpdateFactory.newLatLng(new LatLng(centerlat,centerlng));
        		supportMap.moveCamera(cu);
        }else{
        	//Restore stored data
        	shown=savedInstanceState.getBoolean("shown");
        	if(!savedInstanceState.getString("curpath").equals("")){	
        		curpath=savedInstanceState.getString("curpath");
        		curori=savedInstanceState.getInt("ori");
        		counter=savedInstanceState.getInt("counter");
        		Display dis = getWindowManager().getDefaultDisplay();
        		Point size= new Point();
        		if (android.os.Build.VERSION.SDK_INT>= 13){
        			dis.getSize(size);}else{
        		size.x=dis.getWidth();
        		size.y=dis.getHeight();
        		}
        		int sIm;
        		if(size.x>size.y){
        			sIm=size.y/3;
        		}else{
        			sIm=size.x/3;
        		}
        		Bitmap b = Util.decodeSampledBitmapFromFile(curpath,sIm,sIm,curori);
        		iv.setImageBitmap(b);
        		iv.setClickable(true);
        		iv.setTag(curpath);
        		iv.setOnClickListener(new OnClickListener(){
        			@Override
        			public void onClick(View v) {
        				dia = new FullDialog(Anzeigen.this, (String) v.getTag(),ori.get(curori),h);
        				dia.setTitle(R.string.vollbild);
        				dia.requestWindowFeature(Window.FEATURE_NO_TITLE);
        				dia.show();
        			}
        			
        		});
        		iv.bringToFront();
        	}
        	supportMap.setMapType(savedInstanceState.getInt("Map"));
        	CameraPosition cameraPosition = new CameraPosition.Builder()
            	.target(new LatLng(savedInstanceState.getDouble("latMap"),savedInstanceState.getDouble("lngMap")))      // Sets the center of the map to Mountain View
            	.zoom((float) savedInstanceState.getDouble("zoomMap"))                
            	.build();  
        	supportMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        	if(savedInstanceState.getBoolean("dia")){
        		dia = new FullDialog(Anzeigen.this, savedInstanceState.getString("curpath"),0,h);
				dia.setTitle(R.string.vollbild);
				dia.requestWindowFeature(Window.FEATURE_NO_TITLE);
				dia.show();        		
        	}
        	if(savedInstanceState.getBoolean("time")){
        		hist=true;
        		ImageButton li = (ImageButton) findViewById(R.id.links);
        		ImageButton re = (ImageButton) findViewById(R.id.rechts);
        		li.setVisibility(View.VISIBLE);
        		re.setVisibility(View.VISIBLE);
        		int k=getPosi();
        		if(k==pathlist.size()-1){
        			li.setVisibility(View.GONE);
        		}
        		if(k==0){
        			li.setVisibility(View.GONE);	
        		}
        	}
        }
        //Sliding Menue for Options
        menu = new SlidingMenu(this);
        menu.setBehindWidthRes(R.dimen.slidingmenuWidth);
        menu.setFadeDegree(0.35f);
        menu.setMode(SlidingMenu.RIGHT);
        menu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
        menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
        menu.setMenu(R.layout.options);
    }
    
    //Saves runtime variables when display changes orientation or a telephone call comes in
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putDouble("latMap", supportMap.getCameraPosition().target.latitude);
		outState.putDouble("lngMap", supportMap.getCameraPosition().target.longitude);
		outState.putDouble("zoomMap", supportMap.getCameraPosition().zoom);
		outState.putBoolean("shown", shown);
		outState.putInt("counter", counter);
		if(!curpath.equals("")){outState.putInt("ori", ori.get(getPosi()));}
			outState.putString("curpath", curpath);
		if(dia!=null){
			outState.putBoolean("dia", true);	
		}else{
			outState.putBoolean("dia", false);	
		}
		outState.putInt("Map", supportMap.getMapType());
		outState.putBoolean("time", hist);
	}
 
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	getMenuInflater().inflate(R.menu.main, menu);
    return true;
    }

	@Override
	public boolean onMarkerClick(Marker arg0) {
		counter++;
		//Listes to get all pictures with same coordiantes as arg0
		List<Integer> posGleich= new ArrayList<Integer>();
		List<Integer> posGleichPos= new ArrayList<Integer>();
		int numMarker=-1;
		int anz=0;
		int posMarker=-1;
		for(int i=0;i<marker.length;i++){
			if(marker[i]!=null){	
				if(marker[i].getPosition().equals(arg0.getPosition())){
					posGleich.add(i);
					anz++;
				}
			}
		}
		for(int i=0;i<posmarker.size();i++){
			if(marker[posGleich.get(0)].getPosition().equals(posmarker.get(i))){
				posGleichPos.add(i);
			}
		}
		//If there are different images for the selected position, shuffle them 
		for(int i=0;i<posGleich.size();i++){
			if(arg0.equals(marker[posGleich.get(i)])){
				if(posGleich.size()>1){
					if(i<=posGleich.size()-2){
						numMarker=posGleichPos.get(i+1);
						posMarker=i+1;
					}else{
						numMarker=posGleichPos.get(0);
						posMarker=0;
					}
				}else{
					numMarker=posGleichPos.get(0);
					posMarker=0;
				}
			}
		}
		marker[posGleich.get(posMarker)].showInfoWindow();
		curpath=pathlist.get(numMarker);
		setImView(numMarker);
		return false;
	}

	 @Override
	    public void onBackPressed() {
	        this.finish();
	    }
	 
//Handler for the FullScreen Mode made by FullDialog. Here the positon in the list of pictures is stored and 
//when the user leaves the FullDialog the Interstitial gets shown
Handler h = new Handler(){
	public void handleMessage(Message msg){
		if(msg.arg1==1){
			zurueck();
		}else if(msg.arg1==2){
			vor();
		}else if(msg.arg1==3){
			int posi= getPosi();
			dia=null;
			displayInterstitial();
			setImView(posi);	
		}
	}
};
//Get the picture which was taken before the current picture
private void zurueck(){
	int posi=getPosi();	
	if(posi>0){
		dia.setImage(pathlist.get(posi-1),ori.get(posi-1));
		curpath=pathlist.get(posi-1);
	}
}
//Get the picture which was taken after the current picture
private void vor(){
	int posi=getPosi();	
	if(posi>-1 & posi<(pathlist.size()-1)){
		dia.setImage(pathlist.get(posi+1),ori.get(posi+1));
		curpath=pathlist.get(posi+1);
	}
}

//Finds the current position of the shown picture in the list of pictures
private int getPosi(){
	int posi=-1;
	for(int i=0;i<pathlist.size();i++){
		if(pathlist.get(i).equals(curpath)){
			posi=i;
		}
	}
	return posi;
}

protected void onPause(){
	super.onPause();
	if(dia!=null){
		dia.cancel();
	}
}

protected void onDestroy(){
	super.onDestroy();
	if(counter>0){
	EreignisClass E = new EreignisClass(this,EreignisClass.Eid);
	Util.sendDB("MarkerClick:_"+counter, E.getVal().intValue());
	if(isFinishing()){displayInterstitial();}
	}
}

public void displayInterstitial() {	
    if (interstitial.isLoaded() && !shown) {
      interstitial.show();
      shown=true;
    }  
}
//Methods to handle the SlidingMenu clicks
public void MapHyb(View view){
	EreignisClass E = new EreignisClass(this,EreignisClass.Eid);
	Util.sendDB("Map:_Hybrid", E.getVal().intValue());
	supportMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
}

public void MapNorm(View view){
	EreignisClass E = new EreignisClass(this,EreignisClass.Eid);
	Util.sendDB("Map:_Normal", E.getVal().intValue());
	supportMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
}

public void MapSat(View view){
	EreignisClass E = new EreignisClass(this,EreignisClass.Eid);
	Util.sendDB("Map:_Sat", E.getVal().intValue());
	supportMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
}
//Methods for the chronoligical mode
public void rechts(View view){
	ImageButton li = (ImageButton) findViewById(R.id.links);
	li.setVisibility(View.VISIBLE);
	int i=getPosi();
	if(i>=1){
		setImView(i-1);
		curpath=pathlist.get(i-1);
	}
	if(i==1){
		ImageButton link = (ImageButton) findViewById(R.id.rechts);
		link.setVisibility(View.GONE);	
	}
}

public void links(View view){
	ImageButton li = (ImageButton) findViewById(R.id.rechts);
	li.setVisibility(View.VISIBLE);
	int i=getPosi();
	if(i<pathlist.size()-1){
		setImView(i+1);
		curpath=pathlist.get(i+1);
	}
	if(i==pathlist.size()-2){
		ImageButton link = (ImageButton) findViewById(R.id.links);
		link.setVisibility(View.GONE);	
	}
}

public void Navi(View view){
	EreignisClass E = new EreignisClass(this,EreignisClass.Eid);
	Util.sendDB("Map:_Chronologisch", E.getVal().intValue());
	ImageButton li = (ImageButton) findViewById(R.id.links);
	ImageButton re = (ImageButton) findViewById(R.id.rechts);
	if(((CheckBox) view).isChecked()){
		hist=true;
		menu.toggle();
		re.setVisibility(View.VISIBLE);
		setImView(pathlist.size()-1);
		curpath=pathlist.get(pathlist.size()-1);
	}else{
		hist=false;
		li.setVisibility(View.GONE);
		re.setVisibility(View.GONE);
	}
}

private void setImView(int posi){
	if(listelat.get(posi)!=0){
		CameraUpdate cu= CameraUpdateFactory.newLatLng(new LatLng(listelat.get(posi),listelng.get(posi)));
		supportMap.animateCamera(cu);
	}
    Display dis = getWindowManager().getDefaultDisplay();
	Point size= new Point();
	if (android.os.Build.VERSION.SDK_INT>= 13){
		dis.getSize(size);
	}else{
		size.x=dis.getWidth();
		size.y=dis.getHeight();
	}
	int sIm;
	if(size.x>size.y){
		sIm=size.y/3;
	}else{
		sIm=size.x/3;
	}
	Bitmap b = Util.decodeSampledBitmapFromFile(pathlist.get(posi),sIm,sIm,ori.get(posi));
	iv.setImageBitmap(b);
	iv.invalidate();
	iv.setTag(pathlist.get(posi).toString()+","+ori.get(posi));
	iv.setClickable(true);
	iv.setOnClickListener(new OnClickListener(){
		@Override
		public void onClick(View v) {
			dia = new FullDialog(Anzeigen.this, ((String) v.getTag()).split(",")[0],Integer.valueOf(((String) v.getTag()).split(",")[1]),h);
			dia.setTitle(R.string.vollbild);
			dia.requestWindowFeature(Window.FEATURE_NO_TITLE);
			dia.show();
			((BitmapDrawable) iv.getDrawable()).getBitmap().recycle();
			iv.setImageBitmap(null);
		}
		
	});
	iv.bringToFront();
}

public void set(View view){
	menu.toggle();
}
}