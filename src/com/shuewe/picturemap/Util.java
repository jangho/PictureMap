/*
 * Some static methods, which are used frequently
 */

package com.shuewe.picturemap;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.sanselan.ImageReadException;
import org.apache.sanselan.Sanselan;
import org.apache.sanselan.common.IImageMetadata;
import org.apache.sanselan.formats.jpeg.JpegImageMetadata;
import org.apache.sanselan.formats.tiff.TiffImageMetadata;


import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.Window;
import android.widget.ImageView;

public class Util {
	
	private static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
    // Raw height and width of image
    final int height = options.outHeight;
    final int width = options.outWidth;
    int inSampleSize = 1;

    if (height > reqHeight || width > reqWidth) {

        if(height>=width){
        	inSampleSize=(int)Math.ceil(height/reqHeight);
        }else{
        	inSampleSize=(int)Math.ceil(width/reqWidth);
        }
    }
    return inSampleSize;
}
	
	public static Bitmap decodeSampledBitmapFromFile(String s,int reqWidth, int reqHeight, int orival) {

	    // First decode with inJustDecodeBounds=true to check dimensions
	    final BitmapFactory.Options options = new BitmapFactory.Options();
	    options.inJustDecodeBounds = true;
	    //Log.d("String", s);
	    BitmapFactory.decodeFile(s, options);
	  //  BitmapFactory.decodeResource(res, resId, options);

	    // Calculate inSampleSize
	    
	   if(orival==90 || orival==270){
		   options.inSampleSize = calculateInSampleSize(options, reqHeight, reqWidth);
	   }else{
		   options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
	   }
	    
	    // Decode bitmap with inSampleSize set
	    options.inJustDecodeBounds = false;
	    
	    Bitmap b= BitmapFactory.decodeFile(s, options);
	    Matrix m = new Matrix();
	    m.postRotate(orival);
	    if(b!=null){Bitmap b2 = Bitmap.createBitmap(b,0,0,b.getWidth(),b.getHeight(),m,true);
	    return b2;}else{
	    	return null;
	    }
	//    return BitmapFactory.decodeResource(res, resId, options);
	}
	public static void sendDB(String action,int id){
		final int i=id;
		final String a=action.replace(" ", "_");
		Thread thread = new Thread(new Runnable(){
			@Override
			public void run() {
				// TODO Auto-generated method stub
				URL url;
				try {
					url = new URL("http://www.shuewe.de/actionPic.php?id="+String.valueOf(i)+"&action="+a);
					HttpURLConnection conn = (HttpURLConnection) url.openConnection();
					InputStream response = conn.getInputStream();
					} catch (MalformedURLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			}
		});
		thread.start();
	}
	//Work around for getDrawable(i,y) for older android versions
	@SuppressLint("NewApi")
	public static Drawable getDrawable(Resources res,int resInt){
		if(android.os.Build.VERSION.SDK_INT< 21){
			return 	res.getDrawable(resInt);
		}else{
			return res.getDrawable(resInt, null);
		}
	}
	
	
	//Workaround for setBackground for older android versions
	@SuppressLint("NewApi")
	public static void setDrawable(Drawable d,ImageView iv){
		if(Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
		    iv.setBackgroundDrawable(d);
		} else {
		    iv.setBackground(d);
		}
	}
	
	public static String fmt(double d){
	    if(d == (int) d)
	        return String.format("%d",(int)d);
	    else
	        return String.format("%.3f",d);
	}
	
	public static double[] getLatLng(String path) throws ImageReadException, IOException{
		double [] latlng = new double[2];
		latlng[0]=0;
		latlng[1]=0;
		IImageMetadata sanselanmetadata = Sanselan.getMetadata(new File(path));
        if (sanselanmetadata instanceof JpegImageMetadata) {
            JpegImageMetadata jpegMetadata = (JpegImageMetadata) sanselanmetadata;
            TiffImageMetadata tiffImageMetadata = jpegMetadata.getExif();
            if(tiffImageMetadata!=null){
            	TiffImageMetadata.GPSInfo gpsInfo = tiffImageMetadata.getGPS();
            	if(gpsInfo!=null){
            		latlng[0]= gpsInfo.getLatitudeAsDegreesNorth();
            		latlng[1]= gpsInfo.getLongitudeAsDegreesEast();
            	}
            }
        }
        return latlng;
	}
}
