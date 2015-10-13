/*
 * Dialog which shows the pictures in full-screen mode. Gets called from Anzeigen.java in case of touching the image.
 * 
 * called from: Anzeigen.java
 * data-input: pathes of pictures, orientation of pictures, current picture showed
 * data-output: current position of regarded picture (by Handler)
 * layout: layout_full
 * 
 * author: shuewe87@gmail.com
 */
package com.shuewe.picturemap;

import com.shuewe.picturemap.R;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;

public class FullDialog extends Dialog {

	Handler handler;
	String path;
	int ori;
	Context c;
	private ImageView iv;
	 
	public FullDialog(Context context, String s, int o, Handler h){
		super(context,R.style.DiaThemeFull);
		this.path=s;
		this.c=context;
		this.ori=o;
		this.handler=h;
	}

	@Override
	public void onCreate(Bundle savedInstanceState){
	       super.onCreate(savedInstanceState);
	       setContentView(R.layout.layout_full);
	       iv= (ImageView) findViewById(R.id.imview); 
	       ViewTreeObserver vto = iv.getViewTreeObserver();
	       vto.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {

	            @SuppressWarnings("deprecation")
				@Override
	            public void onGlobalLayout() {
	            	
	            	//Calculate the sampled picture and show it in ImageView iv
	            	final BitmapFactory.Options options = new BitmapFactory.Options();
	        	    options.inJustDecodeBounds = true;
	        	    BitmapFactory.decodeFile(path, options);
	     	        int heiIm= options.outHeight;
	     	        int widIm= options.outWidth;
	     	        int heiSc= iv.getHeight();
	     	        int widSc= iv.getWidth();
	     	        double heiRat=(double) heiIm/heiSc;
	     	        double widRat=(double) widIm/widSc;
	     	        if(heiRat<1){
	     	        	heiRat=1;
	     	        }
	     	        if(widRat<1){
	     	        	widRat=1;
	     	        }
	       	        Bitmap bm;
	     	    	iv.setImageBitmap(null);
	     	    	if(widRat>heiRat){
	     	    		bm=Util.decodeSampledBitmapFromFile(path,widSc,(int) (heiIm/widRat),ori);
	     	    	}else{
	     	    		bm=Util.decodeSampledBitmapFromFile(path,(int)(widIm/heiRat),heiSc,ori);
	     	    	}
	     	    	iv.setImageBitmap(bm);
	     	        ViewTreeObserver obs = iv.getViewTreeObserver();
	                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
	                    obs.removeOnGlobalLayoutListener(this);
	                } else{
	                	obs.removeGlobalOnLayoutListener(this);
	                }
	            }
	       });
	       android.view.WindowManager.LayoutParams params = getWindow().getAttributes();
	       params.height = LayoutParams.MATCH_PARENT;
	       params.width=LayoutParams.MATCH_PARENT;
	       getWindow().setAttributes((android.view.WindowManager.LayoutParams) params); 
	       //Button to return to Activity. Msg.arg1=3 means the the full mode is ended
	       Button but= (Button)findViewById(R.id.clos);
	       but.setOnClickListener(new View.OnClickListener(){

				@Override
				public void onClick(View v) {
					if(handler!=null){
						Message Msg = new Message();
						Msg.arg1=3;
						handler.sendMessage(Msg);
					}
					FullDialog.this.cancel();
				}
	        	
	       });
	    
	       //Buttons for going to next/previous picture. Handler gives 1 for the previous picture and 2 for the next one
	       ImageButton links = (ImageButton) findViewById(R.id.links);
	       ImageButton rechts = (ImageButton) findViewById(R.id.rechts);
	       if(handler!=null){ 
	    	   links.setOnClickListener(new View.OnClickListener(){

	    		   @Override
	    		   public void onClick(View v) {
	    			   Message msg = new Message();
	    			   msg.arg1=1;
	    			   handler.sendMessage(msg);
	    		   } 	
	    	   });
	    	   rechts.setOnClickListener(new View.OnClickListener(){

	    		   @Override
	    		   public void onClick(View v) {
	    			   Message msg = new Message();
	    			   msg.arg1=2;
	    			   handler.sendMessage(msg);
	    		   }
	    	   });
	       }else{
	    	   links.setVisibility(View.GONE);
	    	   rechts.setVisibility(View.GONE);
	       }
	}
	//gets called from Anzeigen after pressed previous or next in this Dialog. 
	//So, this Dialog doesn't need all pathes of the pictures
	public void setImage(String s, int o){
		Bitmap temp=((BitmapDrawable) iv.getDrawable()).getBitmap();
		iv.setImageBitmap(null);
		temp.recycle();
		iv.setImageResource(0);
		temp=null;
		final BitmapFactory.Options options = new BitmapFactory.Options();
	    options.inJustDecodeBounds = true;
	    options.inPreferredConfig=Config.RGB_565;
	    BitmapFactory.decodeFile(s, options);
	    int heiIm,widIm;
	    if(o==90 || o==270){
	    	widIm= options.outHeight;
	    	heiIm= options.outWidth;
	    }else{
	    	heiIm= options.outHeight;
		    widIm= options.outWidth;	
	    }    
	    int heiSc= iv.getHeight();
	    int widSc= iv.getWidth();
	    double heiRat=(double) heiIm/heiSc;
	    double widRat=(double) widIm/widSc;
	    if(heiRat<1){
	    	heiRat=1;
 	    }
 	    if(widRat<1){
 	    	widRat=1;
 	    }
	    Bitmap bm;  
	    if(widRat>heiRat){
	    	bm=Util.decodeSampledBitmapFromFile(s,widSc,(int) (heiIm/widRat),o);
	    }else{
	    	bm=Util.decodeSampledBitmapFromFile(s,(int)(widIm/heiRat),heiSc,o);
	    }
	    iv.setImageBitmap(bm);
	    iv.invalidate();
	    bm=null;
	}
//free memory
	public void setRec(){
		((BitmapDrawable) iv.getDrawable()).getBitmap().recycle();
		iv.setImageBitmap(null);
	}
//send same message like sent by the close button
	@Override
	public void onBackPressed() {
		if(handler!=null){
			Message Msg = new Message();
			Msg.arg1=3;
			handler.sendMessage(Msg);
		}
		FullDialog.this.cancel();
	}
}
