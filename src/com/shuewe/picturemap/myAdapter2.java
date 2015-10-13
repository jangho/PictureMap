/*
 * Adapter for the dialog which shows all pictures without gps coordinates. This adapter is very similar to myAdapter.
 * For explanations to the techniques like viewholder look up there
 */
package com.shuewe.picturemap;

import java.util.ArrayList;
import java.util.List;

import com.shuewe.picturemap.R;
import com.shuewe.picturemap.myAdapter.ViewHolder;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class myAdapter2 extends BaseAdapter{
Context context;
Resources res;
List<Integer> pos = new ArrayList<Integer>();
List<String> path= new ArrayList<String>();
List<Integer> ori= new ArrayList<Integer>();
private static LayoutInflater inflater = null;

	public myAdapter2(Context c,List<Integer> p, List<String> pa, List<Integer> or){
		res=c.getResources();
		this.path=pa;
		this.pos=p;
		this.ori=or;
		this.context=c;
		inflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	static class ViewHolder {
		public TextView text;
		public ImageView image;
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
	            vi = inflater.inflate(R.layout.row2, null);
	            ViewHolder holder=new ViewHolder();
	            holder.text = (TextView) vi.findViewById(R.id.text);
	            holder.image = (ImageView) vi.findViewById(R.id.bild);
	            vi.setTag(holder);        	
	        }
	        final ViewHolder holder = (ViewHolder) vi.getTag();
	        holder.text.setText(res.getString(R.string.posi)+" "+pos.get(position));
	        PicThread t = new PicThread(holder.image,path.get(position),ori.get(position),handler);
	        t.start();
	        return vi;
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
