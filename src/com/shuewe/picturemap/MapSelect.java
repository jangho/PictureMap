/*
 * Map which is called from MainActivity to edit the gps position of a selected picture
 */

package com.shuewe.picturemap;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.shuewe.picturemap.R;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

public class MapSelect  extends AppCompatActivity implements OnMapClickListener {

	GoogleMap supportMap;
	SupportMapFragment supportmapfragment;
	Fragment fragment;
	Boolean ready;
	List<Double> la,lo;
	Marker marker;
	Resources res;
	List<Integer> ori;
	List<Double> latlist=new ArrayList<Double>();
	List<Double> lnglist=new ArrayList<Double>();
	List<Integer> outCh;
	LatLng koor;
	LatLng koorAlt;
	BackUpLatLng back;
	DialogSharePosition dia;
	Marker[] m=null;
	String citString;
	String path;
	List<String> plist;
	Toolbar toolbar;
	AdView adview;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		if(android.os.Build.VERSION.SDK_INT< 11){
			this.requestWindowFeature(Window.FEATURE_NO_TITLE);	
		}
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map_select);
		toolbar = (Toolbar)findViewById(R.id.tool_bar);
		setSupportActionBar(toolbar);
		ori=new ArrayList<Integer>();
		if(getResources().getBoolean(R.bool.portrait_only)){
	        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
	    }
		LinearLayout l = (LinearLayout) findViewById(R.id.cont);
		adview = new AdView(this);
	    adview.setAdUnitId("ca-app-pub-6437137290907604/2369244572");
	    adview.setAdSize(AdSize.BANNER);
	    l.addView(adview); 
	    AdRequest adRequest = new AdRequest.Builder().addTestDevice("D0579B02FB9B4FF5434915798CEFB644").build();
	    adview.loadAd(adRequest);
		res=getResources();
		FragmentManager fmanager = getSupportFragmentManager();
        fragment = fmanager.findFragmentById(R.id.map);
        supportmapfragment = (SupportMapFragment)fragment;
        supportMap = supportmapfragment.getMap();
        supportMap.setOnMapClickListener(this);
        ready=false;
       //Read intents
        Intent intent= getIntent();
		Bundle extras = intent.getExtras();
		if(extras!=null){
			List<String>  oristring = extras.getStringArrayList("ori");
		      for(String s: oristring){
		    	  ori.add(Integer.parseInt(s));
		      }
		      List<String> latstring = extras.getStringArrayList("latlist");
		      for(String s: latstring){
		    	  latlist.add(Double.parseDouble(s));
		      }
		      List<String> lngstring = extras.getStringArrayList("lnglist");
		      for(String s: lngstring){
		    	  lnglist.add(Double.parseDouble(s));
		      }
			koor=new LatLng(extras.getDouble("Lat"),extras.getDouble("Lng"));
			koorAlt=new LatLng(extras.getDouble("Lat"),extras.getDouble("Lng"));
			if(koor.latitude!=0 || koor.longitude!=0){
			Marker marker2;
			back = new BackUpLatLng(this,extras.getString("path"));
			path=extras.getString("path");
			if(back.id!=-1){
			 CheckBox b = (CheckBox) findViewById(R.id.alte);
		        b.setVisibility(View.VISIBLE);}
			
		marker2=supportMap.addMarker(new MarkerOptions().position(koor).title("Auswahl").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
			Geocoder geocoder;
			List<Address> addresses;
			geocoder = new Geocoder(this, Locale.getDefault());
			try {
				addresses = geocoder.getFromLocation(koor.latitude, koor.longitude, 1);
				if(addresses.size()>0){
					String address = addresses.get(0).getAddressLine(0);
					String city = addresses.get(0).getAddressLine(1);
					String country = addresses.get(0).getAddressLine(2);
					marker2.setTitle(res.getString(R.string.aktuelP)+city);
					 TextView t= (TextView) findViewById(R.id.tview);
					 t.setText(city);
					 citString=city;}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			CameraUpdate cu= CameraUpdateFactory.newLatLng(new LatLng(koor.latitude,koor.longitude));
		    supportMap.moveCamera(cu);
		    
			}
			plist= extras.getStringArrayList("plist");
		
			}
		if(savedInstanceState!=null){
			if(savedInstanceState.getBoolean("alte")){
		    	Spinner sp= (Spinner) findViewById(R.id.old);
		    	makeMarkers(sp);
		    }
			if(savedInstanceState.getDouble("Lat")!=0){
			koor=new LatLng(savedInstanceState.getDouble("Lat"),savedInstanceState.getDouble("Lng"));
			citString=savedInstanceState.getString("citString");
			marker=supportMap.addMarker(new MarkerOptions().position(koor).title("Auswahl").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
			marker.setTitle(citString);
			TextView t= (TextView) findViewById(R.id.tview);
			 t.setText(citString);
			 ready=true;
			}
			if(savedInstanceState.getBoolean("dia")){
        		dia = new DialogSharePosition(MapSelect.this,path,plist,latlist,lnglist,ori,h);
        		dia.setTitle(res.getString(R.string.titsharepos));
        		dia.show();		
        	}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		// The copy item to use the position of the selected image to others is only visible if the current picture has a gps position
		getMenuInflater().inflate(R.menu.mapselect,menu);
		if(koor==null || koor.equals(new LatLng(0,0))){
			menu.findItem(R.id.copy).setVisible(false);
			}
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.save) {
			if(koorAlt!=null){
				if(ready && !koorAlt.equals(koor)){
					makeAlert("single");
			}else{
				this.finish();
			}
				}else{
					if(ready){
						makeAlert("single");
				}else{
					this.finish();
				}
				}
			return true;
		}
		if(id==R.id.copy){
			dia = new DialogSharePosition(this,path,plist,latlist,lnglist,ori,h);
			dia.setTitle(res.getString(R.string.titsharepos));
			dia.show();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onMapClick(LatLng arg0) {
		// TODO Auto-generated method stub
		//Sets the marker to the clicked position
		setMarker(arg0);
		Spinner sp= (Spinner) findViewById(R.id.old);
		//Change Spinner to "new position"
		if(sp.getVisibility()==View.VISIBLE){
			sp.setSelection(0);
		}
		
	}
	//Search for position by name of location
	public void ortsuchen(View view){
		try {
            InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        } catch (Exception e) {
            // TODO: handle exception
        }
		Geocoder geoc= new Geocoder(this);
		final double lat;
		final double lng;
		EditText ed= (EditText) findViewById(R.id.editsuche);
		List<Address> listad;
		try{
			listad= geoc.getFromLocationName(ed.getText().toString(),1);
			if (listad.size()!=0){
			lat= listad.get(0).getLatitude();
			lng= listad.get(0).getLongitude();
			//If marker was set, delete it
			if(marker!=null){
				marker.remove();
			}
			//set new marker
			koor=new LatLng(lat,lng);
			setMarker(koor);
			marker=supportMap.addMarker(new MarkerOptions().position(koor).title("Auswahl").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
			 CameraUpdate cu= CameraUpdateFactory.newLatLng(koor);
		       supportMap.moveCamera(cu);
			}
			
			}
		catch(IOException e){
			 
		}
		
	}
//method called from dialog to save and quit Activity
	private void realout(){
		Intent inte= new Intent();
		inte.putExtra("changeMulti", false);
		inte.putExtra("lat", koor.latitude);
		inte.putExtra("long", koor.longitude);
		inte.putExtra("city", citString);
		setResult(Activity.RESULT_OK,inte);
		MapSelect.this.finish();
	}
	public void onSaveInstanceState(Bundle outState) {
		outState.putString("citString", citString);
		if(koor!=null){
			outState.putDouble("Lat", koor.latitude);
			outState.putDouble("Lng", koor.longitude);
		}
		CheckBox b = (CheckBox) findViewById(R.id.alte);
		if(b.getVisibility()!=View.GONE){
			outState.putBoolean("alte", true);
		}else{
			outState.putBoolean("alte", false);
		}
		if(dia!=null){
			outState.putBoolean("dia", true);	
		}else{
			outState.putBoolean("dia", false);	
		}
	}
	//If old gps-data to the current picture are stored, show a spinner with them
	public void alte(View view){
		Spinner sp= (Spinner) findViewById(R.id.old);
		if(((CheckBox) view).isChecked()){
			makeMarkers(sp);  //Sets the marker for old positon on the map
		}else{
			sp.setVisibility(View.GONE);
			for(int i=0;i<m.length;i++){
				m[i].remove();
			}
			setMarker(koor);
		}
	}
	
	private void setKoor(int i){
		ready=true;
		koor=new LatLng(la.get(i-1),lo.get(i-1));
	}
	//Set marker to new position stored in LatLng o
	private void setMarker(LatLng o){
		if(marker!=null){
			marker.remove();
		}
		koor=o;
		if(!koor.equals(koorAlt)){
			marker=supportMap.addMarker(new MarkerOptions().position(o).title("Auswahl").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
		}
		Geocoder geocoder;
		List<Address> addresses;
		geocoder = new Geocoder(this, Locale.getDefault());
		try {
			addresses = geocoder.getFromLocation(o.latitude, o.longitude, 1);
			if(addresses.size()>0){
				String address = addresses.get(0).getAddressLine(0);
				String city = addresses.get(0).getAddressLine(1);
				String country = addresses.get(0).getAddressLine(2);
				if(!koor.equals(koorAlt)){
					marker.setTitle(city);
				}
				TextView t= (TextView) findViewById(R.id.tview);
				t.setText(city);
				citString=city;
				ready=true;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	private void makeMarkers(Spinner sp){
		sp.setVisibility(View.VISIBLE);
		List<String> bez = new ArrayList<String>();
		bez.add(res.getString(R.string.keine));
		// back is Instance of BackUpLatLng, back.getLat(LatLng) returns a List of stored Latitudes to the picture which are not equal to the current position  
		la = back.getLat(koorAlt);
		lo = back.getLng(koorAlt);
		m= new Marker[lo.size()];
		for(int i=0;i<la.size();i++){
			Geocoder geocoder;
			List<Address> addresses;
			geocoder = new Geocoder(this, Locale.getDefault());
			try {
				addresses = geocoder.getFromLocation(la.get(i), lo.get(i), 1);
				if(addresses.size()>0){
					String address = addresses.get(0).getAddressLine(0);
					String city = addresses.get(0).getAddressLine(1);
					String country = addresses.get(0).getAddressLine(2);
					m[i]=supportMap.addMarker(new MarkerOptions().position(new LatLng(la.get(i),lo.get(i))).title(city).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
					bez.add(city);	
					TextView t= (TextView) findViewById(R.id.tview);
					t.setText(city);
					citString=city;
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}}
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, bez);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		sp.setAdapter(adapter);
		sp.setOnItemSelectedListener(new  AdapterView.OnItemSelectedListener() { 

			public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
				if(i>0){
					setKoor(i);
					for(int z=0;z<m.length;z++){
						m[z].setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
					}
					setMarker(koor);  
      			}
			}
                // If no option selected 

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
		});
	}
	
	//Makes Alert to ask if new positions should be saved
	private void makeAlert(final String multi){
		AlertDialog.Builder dia = new AlertDialog.Builder(this);
		dia.setTitle(res.getString(R.string.titwirklich));
		
		 
		// set dialog message
		dia
			.setMessage(res.getString(R.string.txtwirklich))
			.setCancelable(false)
			.setPositiveButton(res.getString(R.string.yeswirklich),new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,int id) {
					// if this button is clicked, close
					// current activity
					if(!multi.equals("multi")){
						realout();
					}else{
						realout2();
					}
				}
			  })
			.setNegativeButton(res.getString(R.string.nowirklich),new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,int id) {
					// if this button is clicked, just close
					// the dialog box and do nothing
					dialog.cancel();
				}
			});

			// create alert dialog
			AlertDialog alertDialog = dia.create();

			// show it
			alertDialog.show();
	}
	//Handler to get selected pictures which should become the same position like the current one 
	Handler h = new Handler(){
		public void handleMessage(Message msg){
			if(((ArrayList<Integer>) msg.obj).size()>0){
				outCh= (ArrayList<Integer>) msg.obj;
				makeAlert("multi");
			}
			
	}};
	//Quits the Activity and sets the intent
	private void realout2(){
		Intent inte= new Intent();
		inte.putExtra("changeMulti", true);
		inte.putIntegerArrayListExtra("changes", (ArrayList<Integer>) outCh);
		setResult(Activity.RESULT_OK,inte);
		MapSelect.this.finish();
	}
}
