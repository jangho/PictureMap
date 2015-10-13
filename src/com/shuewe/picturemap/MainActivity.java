package com.shuewe.picturemap;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.sanselan.ImageReadException;
import org.apache.sanselan.Sanselan;
import org.apache.sanselan.common.IImageMetadata;
import org.apache.sanselan.formats.jpeg.JpegImageMetadata;
import org.apache.sanselan.formats.jpeg.exifRewrite.ExifRewriter;
import org.apache.sanselan.formats.tiff.TiffImageMetadata;
import org.apache.sanselan.formats.tiff.write.TiffOutputSet;


import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.shuewe.picturemap.R;
import com.shuewe.picturemap.DatePickerFragment.DatePickedListener;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements DatePickedListener{
	int pos,changeOrt;
	Calendar dat1,dat2;
	View layout;
	Resources res;
	EreignisClass Ere;
	private AdView adview;
	//CheckBox onlyNot;
	ImageButton b1,b2,b3;
	List<Double> nullList;
	List<String> plist;
	List<String> plistOhne;
	List<Integer> posOhne;
	List<Double> latlist;
	boolean wrOk,internet;
	List<Double> lnglist;
	List<Integer> poslist;
	List<Integer> ori;
	List<Integer> oriOhne;
 	TextView dp1,dp2;
	private Toolbar toolbar;
	ImageButton selectedHelp;
 	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		if(android.os.Build.VERSION.SDK_INT< 11){
			this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		}
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		toolbar = (Toolbar)findViewById(R.id.tool_bar);
		toolbar.setLogo(R.drawable.ic_launcher);
		setSupportActionBar(toolbar);
		nullList = new ArrayList<Double>();
		plist= new ArrayList<String>();
		plistOhne = new ArrayList<String>();
		posOhne = new ArrayList<Integer>();
		latlist= new ArrayList<Double>();
		lnglist= new ArrayList<Double>();
		poslist=new ArrayList<Integer>();
		ori = new ArrayList<Integer>();
		oriOhne= new ArrayList<Integer>();
		if(getResources().getBoolean(R.bool.portrait_only)){
	        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
	    }
		pos=0;
		//Adding adview to the layout
		LinearLayout l = (LinearLayout) findViewById(R.id.container);
		adview = new AdView(this);
	    adview.setAdUnitId("ca-app-pub-6437137290907604/4323481777");
	    adview.setAdSize(AdSize.BANNER);
	    l.addView(adview); 
	    AdRequest adRequest = new AdRequest.Builder().addTestDevice("D0579B02FB9B4FF5434915798CEFB644").build();
	    adview.loadAd(adRequest);
		res=getResources();
		//Check if user has an id, if not send request to server
		Ere= new EreignisClass(this,EreignisClass.Eid);
	    if(Ere.getID()==-1){
	    	Thread thread = new Thread(new Runnable(){

			@Override
				public void run() {
					URL url;
					try {
						url = new URL("http://www.shuewe.de/picmap_make_id.php");					
						HttpURLConnection conn = (HttpURLConnection) url.openConnection();
						InputStream response = new BufferedInputStream(conn.getInputStream());
						StringBuilder builder = new StringBuilder();
						BufferedReader r = new BufferedReader(new InputStreamReader(response));
						String s= r.readLine();
						builder.append(s);
						makeToast(s);
						Util.sendDB("reg", Integer.valueOf(s)); 
					} catch (MalformedURLException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
			}});
	    	thread.start();
	    	//Make dialog to introduce the user
	    	AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
					MainActivity.this);
				alertDialogBuilder.setTitle(getResources().getString(R.string.helloTitel));
				alertDialogBuilder
					.setMessage(getResources().getString(R.string.helloMess))
					.setCancelable(true).setPositiveButton("OK", new DialogInterface.OnClickListener(){

						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							dialog.cancel();
						}
						
					});
				
					
					
					AlertDialog alertDialog = alertDialogBuilder.create();
					alertDialog.show();
	    }
	    internet=isNetworkAvailable();
	    b1=(ImageButton) findViewById(R.id.b1);
	    b2=(ImageButton) findViewById(R.id.b2);
	    b3=(ImageButton) findViewById(R.id.b3);
	    b3.setEnabled(false);
	   // onlyNot=(CheckBox) findViewById(R.id.onlyNot);
	   // onlyNot.setVisibility(View.GONE);
	    if(savedInstanceState!=null){
	    	if(savedInstanceState.getStringArrayList("plist")!=null){
	    		pos=savedInstanceState.getInt("pos");
	    		changeOrt=savedInstanceState.getInt("changeOrt");
	    		plist=savedInstanceState.getStringArrayList("plist");
	    		plistOhne=savedInstanceState.getStringArrayList("plistOhne");
	    		List<String> lnglistH = savedInstanceState.getStringArrayList("lnglist");
	    		List<String> latlistH = savedInstanceState.getStringArrayList("latlist");
	    		List<String> oriH=savedInstanceState.getStringArrayList("ori");
	    		List<String> oriOhneH=savedInstanceState.getStringArrayList("oriOhne");
	    		List<String> poslistH=savedInstanceState.getStringArrayList("poslist");
	    		List<String> posOhneH=savedInstanceState.getStringArrayList("posOhne");
	    		List<String> nulllistH=savedInstanceState.getStringArrayList("nulllist");
	    		if(latlistH!=null){
	    			for(int i=0;i<latlistH.size();i++){
	    				latlist.add(Double.valueOf(latlistH.get(i)));
	    				lnglist.add(Double.valueOf(lnglistH.get(i)));
	    				ori.add(Integer.valueOf(oriH.get(i)));
	    			}
	    		}
	    		if(oriOhneH!=null){
	    			for(int i=0;i<oriOhneH.size();i++){
	    				oriOhne.add(Integer.valueOf(oriOhneH.get(i)));
	    			}
	    		}
	    		if(posOhneH!=null){
	    			for(int i=0;i<posOhneH.size();i++){
	    				posOhne.add(Integer.valueOf(posOhneH.get(i)));
	    			}
	    		}
	    		if(poslistH!=null){
	    			for(int i=0;i<poslistH.size();i++){
	    				poslist.add(Integer.valueOf(poslistH.get(i)));
	    			}
	    		}
	    		if(nulllistH!=null){
	    			for(int i=0;i<nulllistH.size();i++){
	    				nullList.add(Double.valueOf(oriOhneH.get(i)));
	    			}
	    		}
	    		if(savedInstanceState.getBoolean("only")){addList2();}else{addList();}
	    	}
	    }
    	ListView listview = (ListView) findViewById(R.id.piclist);
    	listview.setEmptyView(findViewById(R.id.empty)); 
    	
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. 
		int id = item.getItemId();
		//Opens the tutorial
		if (id == R.id.helpMode){
			if(findViewById(R.id.help).getVisibility()==View.VISIBLE){
				unsetHelp();
				selectedHelp= null;
				findViewById(R.id.help).setVisibility(View.GONE);
				if(plist.size()==0){
					b3.setEnabled(false);
				}
			}else{
				findViewById(R.id.help).setVisibility(View.VISIBLE);
				selectedHelp=b1;
				b3.setEnabled(true);
				Util.setDrawable(Util.getDrawable(getResources(), R.drawable.selectedhelp),selectedHelp);
				ImageView iv=(ImageView) (findViewById(R.id.helpimage)); 
				iv.setImageDrawable(Util.getDrawable(getResources(), R.drawable.lupe));
				Util.setDrawable(Util.getDrawable(getResources(), R.drawable.ibutton_enabled),iv);
				((TextView) findViewById(R.id.helptext)).setText(getResources().getString(R.string.helplupe));
			}
			
		}
		//Shows information about the app
		if (id == R.id.information) {
			if(findViewById(R.id.help).getVisibility()==View.VISIBLE){
				unsetHelp();
				selectedHelp= null;
				ImageView iv=(ImageView) (findViewById(R.id.helpimage)); 
				iv.setImageDrawable(Util.getDrawable(getResources(), R.drawable.frag));
				Util.setDrawable(null,iv);
				((TextView) findViewById(R.id.helptext)).setText(getResources().getString(R.string.helpcontact));
				
			}else{
			final Dialog dialog = new Dialog(this);
			dialog.setContentView(R.layout.rate);
			dialog.setTitle((res.getString(R.string.titKon)));
			ImageButton btnAdd1 = (ImageButton) dialog.findViewById(R.id.mail);

			ImageButton btnAdd2 = (ImageButton) dialog.findViewById(R.id.giverate);

			btnAdd1.setOnClickListener(new OnClickListener() {
			    public void onClick(View v) {
			    	Intent intent = new Intent(Intent.ACTION_SEND);
			    	intent.setType("text/html");
			    	intent.putExtra(Intent.EXTRA_EMAIL, "shuewe87@gmail.com");
			    	intent.putExtra(Intent.EXTRA_SUBJECT, "Feedback to Geo Foto Map");

			    	startActivity(Intent.createChooser(intent, "Send Email"));

			    }
			});

			btnAdd2.setOnClickListener(new OnClickListener() {
			    public void onClick(View v) {
			    	Intent intent = new Intent(Intent.ACTION_VIEW);
			    	intent.setData(Uri.parse("market://details?id=com.shuewe.picturemap"));
			       // intent.setData(Uri.parse("http://play.google.com/store/apps/details?id=com.shuwe.picturemap"));
			        // btnAdd2 has been clicked
			        startActivity(intent);
			    }
			});

			dialog.show();	
			return true;
		}
		}
		return super.onOptionsItemSelected(item);
	}
	//Import of single pictures by the user
	public void imp(View v){
		if(findViewById(R.id.help).getVisibility()==View.VISIBLE){
			unsetHelp();
			selectedHelp= (ImageButton) v;
			Util.setDrawable(Util.getDrawable(getResources(), R.drawable.selectedhelp),selectedHelp);
			ImageView iv=(ImageView) (findViewById(R.id.helpimage)); 
			iv.setImageDrawable(Util.getDrawable(getResources(), R.drawable.pic));
			Util.setDrawable(Util.getDrawable(getResources(), R.drawable.ibutton_enabled),iv);
			((TextView) findViewById(R.id.helptext)).setText(getResources().getString(R.string.helplupe));
			}else{	
		buttonset(false);
		if (android.os.Build.VERSION.SDK_INT>= 19){
			Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
		    intent.addCategory(Intent.CATEGORY_OPENABLE);
		    intent.setType("image/jpeg");
		    intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
		    startActivityForResult(intent, 1);
		}else{
			Intent intent = new Intent();
			intent.setType("image/*");
			intent.setAction(Intent.ACTION_GET_CONTENT);
			startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);	
		}
		}
	}
	
	//Handle of results from Handler. Used to handle click events from listview adapter
	@SuppressLint("NewApi")
	protected void onActivityResult(int reqcode,int rescode,Intent data ){
		ClipData clipdata; 
		if (reqcode == 1 && rescode == RESULT_OK) {
			if(data!=null){
				if (android.os.Build.VERSION.SDK_INT>= 19){
					clipdata = data.getClipData();
				}else{
					clipdata=null;
				}
				if(clipdata!=null){
					for(int i=0;i<clipdata.getItemCount();i++){
						Uri selectedImage = clipdata.getItemAt(i).getUri();
						String s=getPath(this,selectedImage);
						try {
							addPic(s);
						} catch (ImageReadException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}else{
					Uri selectedImage=data.getData();
					String s=getPath(this,selectedImage);
					try {
						addPic(s);
					} catch (ImageReadException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				buttonset(true);
				//if(latlist.size()==0){
				//	b3.setEnabled(false);
				//}
			}
			addList();
		}else if(reqcode!=1111 & reqcode!= 11& data!=null){
			if(data.getBooleanExtra("changeMulti", false)){
				List<Integer> changedPos= (List<Integer>) data.getIntegerArrayListExtra("changes");
				for(int i=0;i<changedPos.size();i++){
					if(changedPos.get(i)!=changeOrt && (latlist.get(changeOrt)!=latlist.get(changedPos.get(i)) || lnglist.get(changeOrt)!=lnglist.get(changedPos.get(i)))){
						try {
							changeOrt(changedPos.get(i),latlist.get(changeOrt),lnglist.get(changeOrt));
						} catch (ImageReadException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
				EreignisClass E = new EreignisClass(this,EreignisClass.Eid);
				Util.sendDB("addInfoMulti", E.getVal().intValue());  
			}else{
			//	if(onlyNot.isChecked()){
			//		plistOhne.remove(changeOrt);
			//		int a=changeOrt;
			//		nullList.remove(0);
			//		changeOrt=posOhne.get(changeOrt);
			//		posOhne.remove(a);
			//	}
				EreignisClass E = new EreignisClass(this,EreignisClass.Eid);
				Util.sendDB("addInfo", E.getVal().intValue());
				try {
					changeOrt(changeOrt,data.getDoubleExtra("lat",0),data.getDoubleExtra("long",0));
				} catch (ImageReadException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			buttonset(true); 
		//	if(onlyNot.isChecked()){addList2();}else{
			addList();
		//	}
		}else if(reqcode==11){
		}else{
			buttonset(true);
			if(latlist.size()==0){
			//	b3.setEnabled(false);
			}else{
				//hier backhandle
				ListView listview = (ListView) findViewById(R.id.piclist);
			//	if(!onlyNot.isChecked()){
				    listview.setAdapter(new myAdapter(this,handler2, latlist,lnglist,plist,ori));
			//	}else{
			//	    listview.setAdapter(new myAdapter(this,handler2, nullList,nullList,plistOhne,oriOhne));	
			//	}
			}
		} 
	}
	
	//get the absolut pathes of the pictures
	@SuppressLint("NewApi")
	private String getPath(Context context,Uri uri){
		if (android.os.Build.VERSION.SDK_INT< 19){
			if( uri == null ) {
             return null;
			}
			// try to retrieve the image from the media store first
			// this will only work for images selected from gallery
			String[] projection = { MediaStore.Images.Media.DATA };
			@SuppressWarnings("deprecation")
			String[] column = { MediaStore.Images.Media.DATA, MediaStore.Images.Media.ORIENTATION};
			Cursor cursor = managedQuery(uri, column, null, null, MediaStore.Images.ImageColumns.DATE_TAKEN + " DESC"  );
			if( cursor != null ){
				int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
				cursor.moveToFirst();
				ori.add(cursor.getInt(1));
				return cursor.getString(column_index);
			}
			return uri.getPath();
		}else{
			String filePath = "";
			String wholeID = DocumentsContract.getDocumentId(uri);
			String id;
			if(wholeID.split(":").length==2){
			// Split at colon, use second item in the array
				id = wholeID.split(":")[1];
			}else{
				id=wholeID;
			}
			String[] column = { MediaStore.Images.Media.DATA, MediaStore.Images.Media.ORIENTATION, MediaStore.Images.ImageColumns.DATE_TAKEN};     
			// where id is equal to             
			String sel = MediaStore.Images.Media._ID + "=?";
			Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, column, sel, new String[]{ id },  MediaStore.Images.ImageColumns.DATE_TAKEN + " DESC");
			int columnIndex = cursor.getColumnIndex(column[0]);
			if (cursor.moveToFirst()) {
				filePath = cursor.getString(columnIndex);
				ori.add(cursor.getInt(1));
			}   
			cursor.close();
			return filePath;
		}
	}
	//sets the Listview with the chosen pictures
	private void addList(){ 
		
		ListView listview = (ListView) findViewById(R.id.piclist);
		if(latlist.size()>0){
			if(!b3.isEnabled()){
				b3.setEnabled(true);
			}
			listview.setAdapter(new myAdapter(this,handler2, latlist,lnglist,plist,ori));
		}else{
			listview.setAdapter(null);
		}
	}
	//Listview for mode "only pictures without gps-data". Not used at the moment
private void addList2(){ //setzt Path Liste und Lat Lng fort
	ListView listview = (ListView) findViewById(R.id.piclist);
	if(latlist.size()>0){
		if(!b3.isEnabled()){
			b3.setEnabled(true);
		}
		listview.setAdapter(new myAdapter(this,handler2, nullList,nullList,plistOhne,oriOhne));		
	}else{
		listview.setAdapter(null);
	}
}
//Calls the Anzeigen Activity which shows the pictures on a map
	public void anzeigen(View v){
		if(findViewById(R.id.help).getVisibility()==View.VISIBLE){
			unsetHelp();
			//v.setBackground(getResources().getDrawable(R.drawable.selectedhelp, null));
			selectedHelp= (ImageButton) v;
			Util.setDrawable(Util.getDrawable(getResources(), R.drawable.selectedhelp),selectedHelp);
			ImageView iv=(ImageView) (findViewById(R.id.helpimage)); 
			iv.setImageDrawable(Util.getDrawable(getResources(), R.drawable.map));
			Util.setDrawable(Util.getDrawable(getResources(), R.drawable.ibutton_enabled),iv);
			((TextView) findViewById(R.id.helptext)).setText(getResources().getString(R.string.helpmap));
		}else{
		if(isNetworkAvailable()){
		ListView listview = (ListView) findViewById(R.id.piclist);
		listview.destroyDrawingCache();
		listview.setAdapter(null);
		System.gc();
		Intent z =new Intent(this,Anzeigen.class);
		ArrayList<String> listlatlist = new ArrayList<String>();
		for (Double s: latlist){
			listlatlist.add(s.toString());	 
			 }
		ArrayList<String> listlnglist = new ArrayList<String>();
		for (Double s: lnglist){
			listlnglist.add(s.toString());	 
			 }
		ArrayList<String> orilist = new ArrayList<String>();
		for (int s: ori){
			orilist.add(String.valueOf(s));	 
			 }
		z.putStringArrayListExtra("lat", listlatlist);
		z.putStringArrayListExtra("lng",listlnglist);
		z.putStringArrayListExtra("path", (ArrayList<String>) plist);
		z.putStringArrayListExtra("ori", orilist);
		startActivityForResult(z,1111);
		makeOhne();
		EreignisClass E = new EreignisClass(this,EreignisClass.Eid);
		Util.sendDB("showPics:_"+latlist.size()+",ohneOrt:_"+plistOhne.size(), E.getVal().intValue());
		b3.setEnabled(false);	
		}else{
			Toast.makeText(this, res.getString(R.string.nointernet), Toast.LENGTH_LONG).show();
		}
		}
	}
	
	//Opens a dialog to allow the user to search images by capture date
	public void nachZeit(View v){
if(findViewById(R.id.help).getVisibility()==View.VISIBLE){
	unsetHelp();
	selectedHelp= (ImageButton) v;
	Util.setDrawable(Util.getDrawable(getResources(), R.drawable.selectedhelp),selectedHelp);
	ImageView iv=(ImageView) (findViewById(R.id.helpimage)); 
	iv.setImageDrawable(Util.getDrawable(getResources(), R.drawable.lupe));
	Util.setDrawable(Util.getDrawable(res, R.drawable.ibutton_enabled),iv);
	((TextView) findViewById(R.id.helptext)).setText(getResources().getString(R.string.helplupe));
}else{
		
		buttonset(false);
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
        LayoutInflater inflater=this.getLayoutInflater();
        //this is what I did to added the layout to the alert dialog
        layout=inflater.inflate(R.layout.datepick,null);       
        alert.setView(layout);
        alert.setTitle(R.string.titledate);
        alert.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog,int id) {
			// if this button is clicked, close
			// current activity
			if(plist!=null){
			plist.removeAll(plist);
			latlist.removeAll(latlist);
			lnglist.removeAll(lnglist);
			ListView listview = (ListView) findViewById(R.id.piclist);
	    	listview.setEmptyView(findViewById(R.id.empty2));
	    	findViewById(R.id.empty).setVisibility(View.GONE);
			addList();}
			dialog.cancel();
			makequery();
			if(latlist.size()==0){
				b3.setEnabled(false);
			}
			
		}});
        alert.setNegativeButton(R.string.abbruch, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				buttonset(true);
				if(latlist.size()==0){
					b3.setEnabled(false);
				}
				dialog.cancel();
				
			}
		});
		dp1 = (TextView)layout.findViewById(R.id.dat1);
		dp2 = (TextView)layout.findViewById(R.id.dat2);
		dp1.setTag(1);
		dp2.setTag(2);
		dat1=Calendar.getInstance();
		dat1.add(Calendar.MONTH, -6);
		dat2=Calendar.getInstance();
		SimpleDateFormat s= new SimpleDateFormat("dd/MM/yyyy");
		dp1.setText(s.format(dat1.getTime()));
		dp2.setText(s.format(dat2.getTime()));
		
		
		resetCol();
		Button bMonth = (Button) layout.findViewById(R.id.month);
		bMonth.setBackgroundColor(Color.DKGRAY);
		alert.show();
		
	    // which image properties are we querying
	   
	    //rewList();
	}
	}
	//Checks if pictures are ok and have gps-data. Then they will be added to the lists
	private void addPic(String s) throws ImageReadException, IOException{
    	if(!s.equals("")){
    		IImageMetadata sanselanmetadata = Sanselan.getMetadata(new File(s));
    		 if (sanselanmetadata instanceof JpegImageMetadata) {
 	            JpegImageMetadata jpegMetadata = (JpegImageMetadata) sanselanmetadata;
 	            TiffImageMetadata tiffImageMetadata = jpegMetadata.getExif();
 	            
 	            

 	            // print all GPS
 	            if(tiffImageMetadata!=null){
 	            TiffImageMetadata.GPSInfo gpsInfo = tiffImageMetadata.getGPS();
 	            
 	            if(gpsInfo!=null){
 	            
 	           plist.add(s);
 	  	    latlist.add((double) gpsInfo.getLatitudeAsDegreesNorth());
 	  	    lnglist.add((double) gpsInfo.getLongitudeAsDegreesEast());
 	            }else{
 	            	plist.add(s);
 	    	  	    latlist.add(0.0);
 	    	  	    lnglist.add(0.0);
 	            }
 	            }
    		 }
    		
    		
    		
    		 }else{
	    	ori.remove(ori.size()-1);
	    	Toast.makeText(this, res.getString(R.string.wrongformat), Toast.LENGTH_LONG).show();
	    	if(latlist.size()==0){
				b3.setEnabled(false);
			}
    		 
    	else{
    		Toast.makeText(this, res.getString(R.string.wrongformat), Toast.LENGTH_LONG).show();
    		if(latlist.size()==0){
    			b3.setEnabled(false);
			}
    	}
    		 }
    	
	}
	//Handler for Listview adapter and import thread
	public Handler handler2=new Handler(){
		public void handleMessage(Message msg){
			if(msg.obj.equals("ende")){
				ListView listview = (ListView) findViewById(R.id.piclist);
		    	listview.setEmptyView(findViewById(R.id.empty));
		    	findViewById(R.id.empty2).setVisibility(View.GONE);
				addList();
				buttonset(true);
				if(latlist.size()==0){
					
					b3.setEnabled(false);
				}
				
				}else if(msg.arg1==2){
					latlist.add((Double.valueOf(((String[])msg.obj)[0])));
					lnglist.add((Double.valueOf(((String[])msg.obj)[1])));
					plist.add(((String[])msg.obj)[2]);
					ori.add((Integer.valueOf(((String[])msg.obj)[3])));
				}else if(msg.arg1==101 || msg.arg1==102){
			if(isNetworkAvailable()){
			try {
				if(testWrite(plist.get(msg.arg2))){
					Intent z = new Intent(MainActivity.this,MapSelect.class);
					EreignisClass Ere = new EreignisClass(MainActivity.this,EreignisClass.writeOK);
				if(Ere.getID()>-1){
					if(Ere.getValString().equals("no")){
						Ere.setValString("ok");
						Ere.inDB();
						EreignisClass Ere2 = new EreignisClass(MainActivity.this,EreignisClass.Eid);
						Util.sendDB("write_ok_"+plist.get(msg.arg2), Ere2.getVal().intValue());
					}
				}
					if(msg.arg1==102){
//						if(onlyNot.getVisibility()==View.VISIBLE && onlyNot.isChecked()){
//							z.putExtra("Lat", 0);
//							z.putExtra("Lng", 0);
//							z.putStringArrayListExtra("plist", (ArrayList<String>) plist);
//							ArrayList<String> orilist = new ArrayList<String>();
//							for (int s: ori){
//								orilist.add(String.valueOf(s));	 
//							 }
//							z.putStringArrayListExtra("ori", orilist);
//						}else{
							z.putExtra("Lat", latlist.get(msg.arg2));
							z.putExtra("Lng", lnglist.get(msg.arg2));
							z.putExtra("path", plist.get(msg.arg2));
							z.putStringArrayListExtra("plist", (ArrayList<String>) plist);
							ArrayList<String> orilist = new ArrayList<String>();
							for (int s: ori){
								orilist.add(String.valueOf(s));	 
							}
							z.putStringArrayListExtra("ori", orilist);
							ArrayList<String> latllist = new ArrayList<String>();
							for (Double s: latlist){
								latllist.add(String.valueOf(s));	 
							}
							z.putStringArrayListExtra("latlist", latllist);
							ArrayList<String> lngllist = new ArrayList<String>();
							for (Double s: lnglist){
								lngllist.add(String.valueOf(s));	 
							}
							z.putStringArrayListExtra("lnglist", lngllist);
							
			//			}
					}
	
					startActivityForResult(z,55);
					changeOrt=(int) msg.arg2;	
				}else{
					EreignisClass Ere = new EreignisClass(MainActivity.this,EreignisClass.writeOK);
					if(Ere.getID()>-1){
					if(Ere.getValString().equals("ok")||Ere.getID()==-1){
						Ere.setValString("no");
						Ere.inDB();
						EreignisClass Ere2 = new EreignisClass(MainActivity.this,EreignisClass.Eid);
						Util.sendDB("write_not_ok_"+plist.get(msg.arg2), Ere2.getVal().intValue());
					}
					}else{
						Ere.setValString("no");
						Ere.inDB();
						EreignisClass Ere2 = new EreignisClass(MainActivity.this,EreignisClass.Eid);
						Util.sendDB("write_not_ok_"+plist.get(msg.arg2), Ere2.getVal().intValue());
					}
					Toast.makeText(MainActivity.this, res.getString(R.string.sorry), Toast.LENGTH_LONG).show();
				}

				
			} catch (ImageReadException e) {
				EreignisClass Ere = new EreignisClass(MainActivity.this,EreignisClass.writeOK);
				if(Ere.getID()>-1){
					if(Ere.getValString().equals("ok")||Ere.getID()==-1){
						Ere.setValString("no");
						Ere.inDB();
						EreignisClass Ere2 = new EreignisClass(MainActivity.this,EreignisClass.Eid);
						Util.sendDB("write_not_ok_"+plist.get(msg.arg2), Ere2.getVal().intValue());
					}
					}else{
						Ere.setValString("no");
						Ere.inDB();
						EreignisClass Ere2 = new EreignisClass(MainActivity.this,EreignisClass.Eid);
						Util.sendDB("write_not_ok_"+plist.get(msg.arg2), Ere2.getVal().intValue());
					}
					Toast.makeText(MainActivity.this, res.getString(R.string.sorry), Toast.LENGTH_LONG).show();
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				EreignisClass Ere = new EreignisClass(MainActivity.this,EreignisClass.writeOK);
				if(Ere.getID()>-1){
					if(Ere.getValString().equals("ok")){
						Ere.setValString("no");
						Ere.inDB();
						EreignisClass Ere2 = new EreignisClass(MainActivity.this,EreignisClass.Eid);
						Util.sendDB("write_not_ok_"+plist.get(msg.arg2), Ere2.getVal().intValue());
					}
					}else{
						Ere.setValString("no");
						Ere.inDB();
						EreignisClass Ere2 = new EreignisClass(MainActivity.this,EreignisClass.Eid);
						Util.sendDB("write_not_ok_"+plist.get(msg.arg2), Ere2.getVal().intValue());
					}
					Toast.makeText(MainActivity.this, res.getString(R.string.sorry), Toast.LENGTH_LONG).show();
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
				
			
			}else{
					Toast.makeText(MainActivity.this, res.getString(R.string.nointernet), Toast.LENGTH_LONG).show();
				}
		}else{
			
			//if(onlyNot.isChecked()){
			//	latlist.remove((int)posOhne.get(msg.arg2));
			//	lnglist.remove((int)posOhne.get(msg.arg2));
			//	plist.remove((int)posOhne.get(msg.arg2));
			//	ori.remove((int)posOhne.get(msg.arg2));
			//	buttonset(true);
			//	makeOhne();
			//	addList2();
			//}else{
		latlist=(List<Double>)(((Object[])(msg.obj))[0]);	
		lnglist=(List<Double>)(((Object[])(msg.obj))[1]);	
		plist=(List<String>)(((Object[])(msg.obj))[2]);	
		ori=(List<Integer>)(((Object[])(msg.obj))[3]);	
		
		addList();
		buttonset(true);
	//		}
		}
			}
	};

	//makes the query to the mediastore with the time period, the user has chosen
	private void makequery(){
		dat2.set(Calendar.HOUR_OF_DAY, 23);
		dat2.set(Calendar.MINUTE, 59);
		 String[] projection = new String[]{
		            MediaStore.Images.ImageColumns.DATA,
		            MediaStore.Images.ImageColumns.DATE_TAKEN,
		            MediaStore.Images.ImageColumns.ORIENTATION,
		            MediaStore.Images.ImageColumns.DISPLAY_NAME
		    };

		    // Get the base URI for the People table in the Contacts content provider.
		    Uri images = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
		    
		    String selection=MediaStore.Files.FileColumns.MIME_TYPE + "=? AND "+MediaStore.Images.ImageColumns.DATE_TAKEN+" > ? AND "+MediaStore.Images.ImageColumns.DATE_TAKEN+" < ?";
		    String[] selectionarg=new String[3];
		    selectionarg[0]=MimeTypeMap.getSingleton().getMimeTypeFromExtension("jpg");
		    selectionarg[1]=String.valueOf(dat1.getTimeInMillis());
		    selectionarg[2]=String.valueOf(dat2.getTimeInMillis());
		    // Make the query.
		    Cursor cur;
		    if (android.os.Build.VERSION.SDK_INT< 11){
		    cur = managedQuery(images, projection, selection, selectionarg, MediaStore.Images.ImageColumns.DATE_TAKEN +" DESC;");
		    }else{
		    
		    cur =getContentResolver().query(images,
		            projection, // Which columns to return
		            selection,       // Which rows to return (all rows)
		            selectionarg,       // Selection arguments (none)
		            MediaStore.Images.ImageColumns.DATE_TAKEN + " DESC"        // Ordering
		            );}
		    List<String> pa = new ArrayList<String>();
		    List<Integer> o = new ArrayList<Integer>();
		    
		    if(cur.moveToFirst()){
		    	pa.add(cur.getString(0));
		    	o.add(cur.getInt(2));
					}
		    		
		    	while(cur.moveToNext()){
		    		pa.add(cur.getString(0));
		    		o.add(cur.getInt(2));
							//addPic(cur.getString(0));
						}
			    		
		    
		   cur.close();
		   Importer imp= new Importer(handler2,pa,o);
		   imp.start();
	}
	//Methods to handle the responses from the Dialog to search pictures by date
	public void dat(View view){
		DialogFragment newFragment = new DatePickerFragment(Integer.valueOf(view.getTag().toString()));
		if(view.getTag().toString().equals("1")){
			((DatePickerFragment) newFragment).setYear(dat1.get(Calendar.YEAR),dat1.get(Calendar.MONTH),dat1.get(Calendar.DAY_OF_MONTH));
		}else{
			((DatePickerFragment) newFragment).setYear(dat2.get(Calendar.YEAR),dat2.get(Calendar.MONTH),dat2.get(Calendar.DAY_OF_MONTH));
				
		}
		
	    
	   newFragment.show(getSupportFragmentManager(), "timePicker");
	}

	@Override
	public void onDatePicked(Calendar c, int viewnumber) {
		// TODO Auto-generated method stub
		SimpleDateFormat s= new SimpleDateFormat("dd/MM/yyyy");
		if(viewnumber==1){
			dp1.setText(s.format(c.getTime()));
			dat1=c;
		}else{
			dp2.setText(s.format(c.getTime()));
			dat2=c;
		}
	}	
	public void alltime(View view){
		resetCol();
		view.setBackgroundColor(Color.DKGRAY);
		Calendar c=Calendar.getInstance();
		c.set(Calendar.YEAR, 1970);
		c.set(Calendar.MONTH, 0);
		c.set(Calendar.DAY_OF_MONTH, 1);
		dat1=c;
		SimpleDateFormat s= new SimpleDateFormat("dd/MM/yyyy");
		dp1.setText(s.format(c.getTime()));
	}
	public void year(View view){
		resetCol();
		view.setBackgroundColor(Color.DKGRAY);
		Calendar c= Calendar.getInstance();
		c.add(Calendar.YEAR, -1);
		dat1=c;
		SimpleDateFormat s= new SimpleDateFormat("dd/MM/yyyy");
		dp1.setText(s.format(c.getTime()));
	}
	public void month(View view){
		resetCol();
		view.setBackgroundColor(Color.DKGRAY);
		Calendar c= Calendar.getInstance();
		c.add(Calendar.MONTH, -6);
		dat1=c;
		SimpleDateFormat s= new SimpleDateFormat("dd/MM/yyyy");
		dp1.setText(s.format(c.getTime()));
	}
	public void day(View view){
		resetCol();
		view.setBackgroundColor(Color.DKGRAY);
		Calendar c= Calendar.getInstance();
		c.add(Calendar.DAY_OF_MONTH, -7);
		dat1=c;
		SimpleDateFormat s= new SimpleDateFormat("dd/MM/yyyy");
		dp1.setText(s.format(c.getTime()));
	}
	private void resetCol(){
		Button [] b= new Button[4];
		b[0]= (Button) layout.findViewById(R.id.day);
		b[1]= (Button) layout.findViewById(R.id.month);
		b[2]= (Button) layout.findViewById(R.id.year);
		b[3]= (Button) layout.findViewById(R.id.all);
		for(int i=0;i<b.length;i++){
			b[i].setBackgroundColor(Color.GRAY);
		}
	}
	//Insert the user id into a local database
	private void makeToast(String s){
		final String rausdamit=s;
		runOnUiThread(new Runnable() {
	        public void run()
	        {
	        
	        	Ere.val=Double.parseDouble(rausdamit);
	            
	            Ere.inDB();
	        }
	    });
	}
	//Enables/Disables the ImageButtons
	private void buttonset(Boolean en){
		
		b1.setEnabled(en);
		b2.setEnabled(en);
		b3.setEnabled(en);
		if(latlist.size()==0){
			b3.setEnabled(false);
		}else{
			if(en){
				b3.setEnabled(true);		
			}
		}
		if(en){
			if(latlist.contains(0.0)){
	//			onlyNot.setVisibility(View.VISIBLE);
			}else{
	//			onlyNot.setChecked(false);
	//			onlyNot.setVisibility(View.GONE);
			}
		}else{
	//		onlyNot.setChecked(false);
	//		onlyNot.setVisibility(View.GONE);
		}
	}
	//Method not used at the moment
	public void onlyNot(View view){
	//	CheckBox cb= (CheckBox) view;
	//	if(cb.isChecked()){
	//			makeOhne();
	//		addList2();
	//	}else{
	//		addList();
	//	}
	}
	private void makeOhne(){
		posOhne.removeAll(posOhne);
		nullList.removeAll(nullList);
		plistOhne.removeAll(plistOhne);
		oriOhne.removeAll(oriOhne);
		for(int i=0;i<latlist.size();i++){
			if(latlist.get(i)==0){
				posOhne.add(i);
				oriOhne.add(ori.get(i));
				plistOhne.add(plist.get(i));
				nullList.add(0.0);
			}
		}
	}


public void onSaveInstanceState(Bundle outState) {
	if(plist!=null){
	if(!plist.isEmpty()){
	List<String> latlistH = new ArrayList<String>();
	List<String> lnglistH = new ArrayList<String>();
	List<String> oriH = new ArrayList<String>();
	List<String> oriOhneH = new ArrayList<String>();
	List<String> nullListH = new ArrayList<String>();
	List<String> poslistH = new ArrayList<String>();
	List<String> posOhneH = new ArrayList<String>();
	for(int i=0;i<latlist.size();i++){
		latlistH.add(latlist.get(i).toString());
		lnglistH.add(lnglist.get(i).toString());
		oriH.add(ori.get(i).toString());
	}
	for(int i=0;i<nullList.size();i++){
	nullListH.add(nullList.get(i).toString());
	}
	for(int i=0;i<oriOhne.size();i++){
		oriOhneH.add(oriOhne.get(i).toString());
		}
	for(int i=0;i<poslist.size();i++){
		poslistH.add(poslist.get(i).toString());
		}
	for(int i=0;i<posOhne.size();i++){
		posOhneH.add(posOhne.get(i).toString());
		}
	outState.putStringArrayList("plist", (ArrayList<String>) plist);
	outState.putStringArrayList("plistOhne", (ArrayList<String>) plistOhne);
	outState.putInt("pos", pos);
	outState.putInt("changeOrt", changeOrt);
	outState.putStringArrayList("lnglist", (ArrayList<String>)lnglistH);
	outState.putStringArrayList("latlist", (ArrayList<String>)latlistH);
	outState.putStringArrayList("nullList", (ArrayList<String>)nullListH);
	outState.putStringArrayList("ori", (ArrayList<String>)oriH);
	outState.putStringArrayList("oriOhne", (ArrayList<String>)oriOhneH);
	outState.putStringArrayList("poslist", (ArrayList<String>)poslistH);
	outState.putStringArrayList("posOhne", (ArrayList<String>)posOhneH);
	//outState.putBoolean("only", onlyNot.isChecked());
	outState.putBoolean("only", false);
	
	}
	}
}
//Handles results from Activity MapSelect to edit gps-data
private void changeOrt(int pos,double lat,double lng) throws ImageReadException, IOException{
  wrOk=writeFile(plist.get(pos),lat,lng); 
	if(!wrOk){
		Toast.makeText(this, res.getString(R.string.writeerror), Toast.LENGTH_LONG).show();
		EreignisClass Ere = new EreignisClass(MainActivity.this,EreignisClass.Eid);
		Util.sendDB("change_write_not_ok", Ere.getVal().intValue());
		Ere= new EreignisClass(MainActivity.this,EreignisClass.writeOK);
		Ere.setValString("no");
		Ere.inDB();
	}else{
		BackUpLatLng.writeDb(this, plist.get(pos), latlist.get(pos), lnglist.get(pos));
		 latlist.set(pos, lat);
		 lnglist.set(pos, lng);
		 
	}
}
//Checks if picture is writeable 
private Boolean testWrite(String testPath) throws ImageReadException, IOException{
	Boolean res=false;
	double[] BackLatLng= Util.getLatLng(testPath);
//	try {
		if(writeFile(testPath,0.0,0.0)){
	    	res=true; 
	    	writeFile(testPath,BackLatLng[0],BackLatLng[1]);
	   }  
	return res;
}

private boolean isNetworkAvailable() {
	Boolean res=false;
    ConnectivityManager connectivityManager 
          = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
    res= activeNetworkInfo != null && activeNetworkInfo.isConnected();
    internet=res;
    return res;
}
//Writes the files with the updated gps-data. Therefore a temp file gets created and will be compared to the old file.
//If everything is ok, the old file gets deleted and the temp file will be renamed to the deleted one and the function returns true
private boolean writeFile(String path,double lat, double lng) throws ImageReadException, IOException{
	boolean ok=true;
	final String p=path;
	final double latitude=lat;
	final double longitude=lng;
	OutputStream os = null;
	File file = new File(p);
	String [] out = p.split("\\.");
	String outpath= out[0]+"temp.jpg";
	try {
		TiffOutputSet outputSet = null;
		IImageMetadata metadata = Sanselan.getMetadata(file);
		JpegImageMetadata jpegMetadata = (JpegImageMetadata) metadata;
		if (null != jpegMetadata){
			TiffImageMetadata exif = jpegMetadata.getExif();
			if (null != exif){
						outputSet = exif.getOutputSet();
			}
		}
		if (null == outputSet){outputSet = new TiffOutputSet();}
		outputSet.setGPSInDegrees(longitude, latitude);
		os = new FileOutputStream(outpath);
		os = new BufferedOutputStream(os);		
		new ExifRewriter().updateExifMetadataLossless(file, os, outputSet);		
		os.close();
		os = null;
	} catch (Exception e) {
		e.printStackTrace();
	}
	File file2 = new File(outpath);
	double[] latlng = Util.getLatLng(outpath);
	if(Math.abs(latlng[0]-lat)<0.0001 && Math.abs(latlng[1]-lng)<0.0001){
		ok=true;
		if(file2!=null){
			file.delete();
			file2.renameTo(new File(p));
		}
	}else{
		ok=false;
		if(file2!=null){
			file2.delete();
		}
	}
	return ok;
}

//Sets the Images of the Imagebuttons in the tutorial mode
private void unsetHelp(){
	if(selectedHelp!=null){
		if(selectedHelp==b1 |selectedHelp==b2 | selectedHelp==b3){
			Util.setDrawable(Util.getDrawable(getResources(), R.drawable.ibutton),selectedHelp);
		}else{
			Util.setDrawable(null,selectedHelp);
		}
	}
}
//In the tutorial mode, back button leads to close of tutorial mode, otherwise the activity will be closed
@Override
public void onBackPressed(){
	if(findViewById(R.id.help).getVisibility()==View.VISIBLE){
		findViewById(R.id.help).setVisibility(View.GONE);
		unsetHelp();
		selectedHelp=null;
		if(plist.size()==0){
			b3.setEnabled(false);
		}
	}else{
		super.onBackPressed();
	}
}



}