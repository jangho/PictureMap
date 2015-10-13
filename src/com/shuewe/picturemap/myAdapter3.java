/*
 * adapter for the Dialog in MapSelect Activtiy to allow the user to select pictures which should be stored under the current gps position
 * technically it is very similar to myAdapter1 and myAdapter2, look there for comments 
 */
package com.shuewe.picturemap;

import java.util.ArrayList;
import java.util.List;

import com.shuewe.picturemap.R;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
public class myAdapter3 extends BaseAdapter{
Context context;
Resources res;
int pos;
List<String> path= new ArrayList<String>();
List<Integer> ori= new ArrayList<Integer>();
List<Integer> selected= new ArrayList<Integer>();
List<Double> latlist= new ArrayList<Double>();
List<Double> lnglist= new ArrayList<Double>();
private static LayoutInflater inflater = null;

	public myAdapter3(Context c,int p, List<String> pa, List<Double> lat, List<Double> lng,List<Integer> or,List<Integer> selec){
		res=c.getResources();
		this.path=pa;
		this.pos=p;
		this.selected=selec;
		this.ori=or;
		this.context=c;
		this.latlist=lat;
		this.lnglist=lng;
		inflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	static class ViewHolder {
		public TextView text;
		public ImageView image;
		public ImageView check;
		public LinearLayout lin;
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return path.size();
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
	        if (vi == null){
	        	vi = inflater.inflate(R.layout.row3, null);	 
	        	ViewHolder holder = new ViewHolder();
	        	holder.text = (TextView) vi.findViewById(R.id.ort);
	        	holder.image = (ImageView) vi.findViewById(R.id.bild);
	        	holder.check=(ImageView) vi.findViewById(R.id.check);
	        	holder.lin=(LinearLayout) vi.findViewById(R.id.linlay);
	        	vi.setTag(holder);
	        }
	        if(!(vi.getTag() instanceof ViewHolder)){
	        	vi = inflater.inflate(R.layout.row3, null);	 
	            ViewHolder holder = new ViewHolder();
	            holder.text = (TextView) vi.findViewById(R.id.ort);
	            holder.image = (ImageView) vi.findViewById(R.id.bild);
	            holder.check=(ImageView) vi.findViewById(R.id.check);
	            holder.lin=(LinearLayout) vi.findViewById(R.id.linlay);
	            vi.setTag(holder);
	        }
	        final ViewHolder holder = (ViewHolder) vi.getTag();
	        if(latlist.get(position)==0 && lnglist.get(position)==0){
	        	holder.text.setText("");	
	        }else{
	        	holder.text.setText("Lat: "+Util.fmt(latlist.get(position))+"\nLng: "+Util.fmt(lnglist.get(position)));
	        }
	       if(selected.contains(position)){
	    	   holder.lin.setBackgroundColor(Color.rgb(220, 220, 220));
	    	   //ch.setBackground(res.getDrawable(R.drawable.check));
	    	   holder.check.setImageDrawable(Util.getDrawable(res, R.drawable.check));
	       }else{
	    	   holder.lin.setBackgroundColor(Color.TRANSPARENT);
	    	   holder.check.setImageDrawable(Util.getDrawable(res, R.drawable.checkbox));
	       }
	       holder.lin.setTag(String.valueOf(position));
	       holder.lin.setOnClickListener(new View.OnClickListener(){

				public void onClick(View v) {
					// TODO Auto-generated method stub
				
				
				if(selected.contains(Integer.valueOf((String)v.getTag()))){
					for(int z=0;z<selected.size();z++){
						if(selected.get(z)==Integer.valueOf((String)v.getTag())){
							selected.remove(z);
						}
					}
					v.setBackgroundColor(Color.TRANSPARENT);
					((ImageView) v.findViewById(R.id.check)).setImageDrawable(Util.getDrawable(res, R.drawable.checkbox));
				}else{
					selected.add(Integer.valueOf((String) v.getTag()));
					//v.setBackgroundColor(Color.rgb(0, 255, 0));
					v.setBackgroundColor(Color.rgb(220, 220, 220));
			    	((ImageView) v.findViewById(R.id.check)).setImageDrawable(Util.getDrawable(res, R.drawable.check));
				}
				
				}	        	
	        });
	      PicThread t = new PicThread(holder.image,path.get(position),ori.get(position),handler);
	      t.start();
	        return vi;
	}
	public List<Integer> getSelected(){
		return selected;
	}
	Handler handler = new Handler(){
		public void handleMessage(Message msg){
			setImage((ImageView)(((Object[])(msg.obj))[0]),(Bitmap)(((Object[])(msg.obj))[1]));
		}
	};

	private void setImage(ImageView iv, Bitmap bm){
		iv.setImageBitmap(bm);
	}



}
