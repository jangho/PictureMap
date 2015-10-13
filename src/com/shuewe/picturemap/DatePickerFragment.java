/*makes the Date Picker Dialog*/
package com.shuewe.picturemap;

import java.util.Calendar;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;
import android.widget.TimePicker;



public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener{
	 private DatePickedListener mListener;
	 int viewnumber;
	 public int year=0, day, month;
	 public void setYear(int y, int m, int d){
		 year=y;
		 month= m;
		 day= d;
	 }
	 public DatePickerFragment(int z){
		 viewnumber=z;
	 }
	 
	    @Override
	    public Dialog onCreateDialog(Bundle savedInstanceState)
	    {
	        // use the current time as the default values for the picker
	        final Calendar c = Calendar.getInstance();
	        if (year==0){year = c.get(Calendar.YEAR);
	        month = c.get(Calendar.MONTH);
	        day = c.get(Calendar.DAY_OF_MONTH);}
	        // create a new instance of TimePickerDialog and return it
	        return new DatePickerDialog(getActivity(), this,year, month, day);
	    }
	 
	    @Override
	    public void onAttach(Activity activity)
	    {
	        // when the fragment is initially shown (i.e. attached to the activity), cast the activity to the callback interface type
	        super.onAttach(activity);
	        try
	        {
	            mListener = (DatePickedListener) activity;
	        }
	        catch (ClassCastException e)
	        {
	            throw new ClassCastException(activity.toString() + " must implement " + DatePickedListener.class.getName());
	        }
	    }
	 
	    public void onTimeSet(TimePicker view, int hourOfDay, int minute)
	    {
	        // when the time is selected, send it to the activity via its callback interface method

	    }
	 
	    public static interface DatePickedListener
	    {
	        public void onDatePicked(Calendar c, int z);
	    }

		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			// TODO Auto-generated method stub
	        Calendar c = Calendar.getInstance();
	        c.set(Calendar.YEAR, year);
	        c.set(Calendar.MONTH, monthOfYear);
	        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
	        c.set(Calendar.HOUR_OF_DAY,0);
	        c.set(Calendar.MINUTE,0);
	        c.set(Calendar.SECOND, 0);
	        c.set(Calendar.MILLISECOND,0);
	        
	 
	        mListener.onDatePicked(c, viewnumber);
		}
}
