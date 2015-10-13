/*Makes the dialog to copy gps data from one picture to others*/
package com.shuewe.picturemap;

import java.util.ArrayList;
import java.util.List;

import com.shuewe.picturemap.R;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.LinearLayout.LayoutParams;

public class DialogSharePosition extends Dialog {
	List<String> path=new ArrayList<String>();
	String pathcur;
	List<Integer> ori = new ArrayList<Integer>();
	Context c;
	Boolean zumersten=true;
	Handler h;
	Boolean zumzweiten=true;
	List<Double> latlist;
	List<Double> lnglist;
	List<Integer> selected= new ArrayList<Integer>();
	
	 public DialogSharePosition(Context context, String pcur, List<String> path, List<Double> la, List<Double> ln, List<Integer> or,Handler ha) {
		super(context, R.style.DiaTheme);
		this.c=context;
		this.path=path;
		this.latlist=la;
		this.lnglist=ln;
		this.h=ha;
		this.ori=or;
		this.pathcur=pcur;
	}

	@Override
	    public void onCreate(Bundle savedInstanceState)
	    {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.layout_full3);
	        android.view.WindowManager.LayoutParams params = getWindow().getAttributes();
	        params.height = LayoutParams.MATCH_PARENT;
	        getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);
	        final ListView listview = (ListView) findViewById(R.id.piclist2);
	        //Button which closes the dialog and returns results via a handler to the acticity (MapSelect.java)
	        Button but= (Button)findViewById(R.id.clos);
	        but.setOnClickListener(new View.OnClickListener(){

				@Override
				public void onClick(View v) {
					List<Integer> sel=unsetList();
					Message msg= new Message();
					msg.obj=sel;
					h.sendMessage(msg);
					DialogSharePosition.this.cancel();
				}
	        	
	        });
	        ViewTreeObserver vto = listview.getViewTreeObserver();
	        vto.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {

				@Override
				public void onGlobalLayout() {
					LayoutParams para = (LayoutParams) listview.getLayoutParams();
			        if(!zumersten && zumzweiten){
			        	 int pos=-1;
			        	for(int i=0;i<path.size();i++){
			        		if(path.get(i).equals(pathcur)){
			        			pos=i;
			        		}
			        	}
			        	//make pos final to give it to a second thread
			        	final int p=pos;
			        	listview.setEmptyView(findViewById(R.id.empty));
			        	listview.setAdapter(null);
			        	final myAdapter3 mA = new myAdapter3(c,pos,path,latlist,lnglist,ori,selected);
			        	//Use a handler to allow Android to load the empty view for the Listview before loading its content
			        	//Therefore the thread which loads the content starts about 500 ms after the main thread
			        	Handler handler = new Handler();
			        	handler.postDelayed(new Runnable(){

							@Override
							public void run() {
								// TODO Auto-generated method stub
								listview.setAdapter(mA);
								listview.setSelection(p);
							}
			        		
			        	}, 500);
			        	zumzweiten=false;
			        	}else{
			        	//Sets a margin for the listview depending on the screen size	
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
	private List<Integer> unsetList(){
		List<Integer> res;
		ListView listview = (ListView) findViewById(R.id.piclist2);
		res=((myAdapter3) listview.getAdapter()).getSelected();
		listview.setAdapter(null);
		System.gc();
		return res;
	}
}
