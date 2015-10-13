/*
 * Dialog which is shown in case of having chosen pictures without gps-information.
 * Called from Anzeigen.java class which creates the map with the pictures on it
 * Dialog consist mainly of one ListView which is filled by myAdapter2
 * 
 * called from: Anzeigen.java
 * data-input: pathes of picture, orientation of pictures
 * data-output: none
 * layout: layout_full2
 * 
 * author: shuewe87@gmail.com
 */
package com.shuewe.picturemap;

import java.util.ArrayList;
import java.util.List;

import com.shuewe.picturemap.R;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.LinearLayout.LayoutParams;

public class DialogUnset extends Dialog {

	List<String> path=new ArrayList<String>();
	List<Integer> posi= new ArrayList<Integer>();
	List<Integer> ori = new ArrayList<Integer>();
	Context c;
	Boolean zumersten=true;
	Boolean zumzweiten=true;
	
	 public DialogUnset(Context context, List<Integer> pos, List<String> path, List<Integer> or) {
		super(context, R.style.DiaTheme);
		this.c=context;
		this.path=path;
		this.ori=or;
		this.posi=pos;
	}

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
	    setContentView(R.layout.layout_full2);
	    android.view.WindowManager.LayoutParams params = getWindow().getAttributes();
	    params.height = LayoutParams.MATCH_PARENT;
	    getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);
	    final ListView listview = (ListView) findViewById(R.id.piclist2);
	    listview.setEmptyView(findViewById(R.id.empty));
	    //Button to close Dialog
	    Button but= (Button)findViewById(R.id.clos);
	    but.setOnClickListener(new View.OnClickListener(){
				@Override
				public void onClick(View v) {
					unsetList();
					DialogUnset.this.cancel();
				}
	        	
	    });
	    //wait until Views are loaded..
	    ViewTreeObserver vto = listview.getViewTreeObserver();
	    vto.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
				@Override
				public void onGlobalLayout() {
					LayoutParams para = (LayoutParams) listview.getLayoutParams();
			        if(!zumersten && zumzweiten){
			        	listview.setEmptyView(findViewById(R.id.empty));
			        	Handler handler = new Handler();
			        	handler.postDelayed(new Runnable(){

							@Override
							public void run() {
								// TODO Auto-generated method stub
								listview.setAdapter(new myAdapter2(c,posi,path,ori));
							}
			        		
			        	}, 500);
			        	zumzweiten=false;
			        }else{
			        	if(zumersten){
			        			int heightLV= (int) Math.ceil(listview.getHeight()/c.getResources().getDisplayMetrics().density);
			        			if (heightLV>200){para.setMargins(0, 50, 0, 50);}
			        	}
			        	zumersten=false;
			        }
				}
	    });
	}
//free memory	
	private void unsetList(){
		ListView listview = (ListView) findViewById(R.id.piclist2);
		listview.setAdapter(null);
		System.gc();
	}
}
