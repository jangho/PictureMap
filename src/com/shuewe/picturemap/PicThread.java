/*
 * loads scaled pictures in Background. Thread returns Bitmap and imageview via handler to the UI thread where the Bitmap is set as src for the view
 */
package com.shuewe.picturemap;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;

public class PicThread extends Thread {

	ImageView ima;
	int ori;
	Handler h;
	String path;
	
	public PicThread(ImageView iv,String path, int ori, Handler h){
		this.h=h;
		this.ima=iv;
		this.ori=ori;
		this.path=path;
	}
	public void run(){
		Bitmap bm=Util.decodeSampledBitmapFromFile(path,150,150,ori);
		Message m = new Message();
		Object[] o = new Object[2];
		o[0]=ima;
		o[1]=bm;
		m.obj=o;
		h.sendMessage(m);
	}
	
}
