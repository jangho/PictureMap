/*
 * Adapter for the listview of the MainActivity. The adapter uses a Viewholder and loads / sample down the picture via a background thread
 */
package com.shuewe.picturemap;

import java.util.ArrayList;
import java.util.List;

import com.shuewe.picturemap.R;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class myAdapter extends BaseAdapter{
Context context; // context of calling Activity = MainActivity.this
Resources res;
List<Double> lat= new ArrayList<Double>(); // List of Latitudes of all pictures
List<Double> lng= new ArrayList<Double>(); // List of Langitudes of all pictures
List<String> path= new ArrayList<String>(); // List of paths of all pictures 
List<Integer> ori= new ArrayList<Integer>(); // List of theire orientations
Handler handler; // Handler to return results to MainActivity
private static LayoutInflater inflater = null;

	static class ViewHolder {
		public TextView text;
		public ImageView image;
		public ImageButton dele;
		public ImageButton edit;
	}	


	public myAdapter(Context c, Handler h,List<Double> lat, List<Double> lng, List<String> path,List<Integer> or){
		this.lat.addAll(lat);
		this.lng.addAll(lng);
		res=c.getResources();
		this.path.addAll(path);
		this.ori=or;
		this.handler=h;
		this.context=c;
		inflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return lat.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		 View vi = convertView;
		 
	        if (vi == null){ //Initialing the ViewHolder for the first use
	        	vi = inflater.inflate(R.layout.row, null);
	        	ViewHolder viewHolder = new ViewHolder();
	        	viewHolder.text = (TextView) vi.findViewById(R.id.text);
	        	viewHolder.image = (ImageView) vi.findViewById(R.id.bild);
	        	viewHolder.dele=(ImageButton) vi.findViewById(R.id.close);
	        	viewHolder.edit=(ImageButton) vi.findViewById(R.id.edit);
	        	vi.setTag(viewHolder); //Link the ViewHolder to the view via view.setTag
	        }
	        final ViewHolder holder = (ViewHolder) vi.getTag(); // get ViewHolder via Tag of view
	        final int p=position;
	        //tagging the positions to find them back later in onclick method
	        holder.dele.setTag(position);
	        holder.edit.setTag(position);
	        holder.image.setClickable(true);
	        //click on picture -> make FullScreen
	        holder.image.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					FullDialog dia = new FullDialog(context,path.get(p),ori.get(p),null);
					dia.show();
				}
	        	
	        });
	        //delete picture from list
	        holder.dele.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					remo(Integer.valueOf(v.getTag().toString()));
				}
	        	
	        });
	        //edit picture information, calling MapSelcect via MainActivity
	        holder.edit.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Message msg= new Message();
					msg.arg1=102;
					msg.arg2=Integer.valueOf(v.getTag().toString());
					msg.obj="not";
					handler.sendMessage(msg);
				}
	        	
	        });
	        // if no position ist stored to the picture, a text will be shown
	        if(lat.get(position) ==0 & lng.get(position)==0){
	        	holder.text.setText(res.getString(R.string.na));
	        	holder.text.setClickable(true);
	        	holder.text.setTag(position);
	        	holder.text.setOnClickListener(new OnClickListener(){

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						clicked(Integer.valueOf(v.getTag().toString()));
					}
	        		
	        	});
	        }else{
	        	holder.text.setText("Lat: "+Util.fmt(lat.get(position))+"\nLng: "+Util.fmt(lng.get(position)));
	        	holder.text.setOnClickListener(null);
	        }
	        //Thread to load the scaled pictures in background
	        PicThread t = new PicThread(holder.image,path.get(position),ori.get(position),handler2);
	        t.start();
	        return vi;
	}
	//method to return remove task to MainActivtiy
	private void remo(int i){
		if(lat.size()>0){
			lat.remove(i);
			lng.remove(i);
			path.remove(i);
			ori.remove(i);
		}
		Object[] o=new Object[4];
		o[0]=lat;
		o[1]=lng;
		o[2]=path;
		o[3]=ori;
		Message msg= new Message();
		msg.obj=o;
		msg.arg1=100;
		msg.arg2=i;
		handler.sendMessage(msg);
	}

private void clicked(int i){
Message msg= new Message();
msg.arg1=101;
msg.arg2=i;
msg.obj="not";
handler.sendMessage(msg);
}
//Handler for the background thread which loads the pictures as scaled bitmaps
Handler handler2 = new Handler(){
	public void handleMessage(Message msg){
		setImage((ImageView)(((Object[])(msg.obj))[0]),(Bitmap)(((Object[])(msg.obj))[1]));
	}
};

private void setImage(ImageView iv, Bitmap bm){
	iv.setImageBitmap(bm);
}

}
