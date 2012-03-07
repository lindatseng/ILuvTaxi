package com.iluvtaxi.tool;

import com.cyrilmottier.android.greendroid.R;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

public class MyAdapter extends CursorAdapter {
	private LayoutInflater inflater;
	public MyAdapter(Context context, Cursor c) {
		super(context, c);
		inflater = LayoutInflater.from(context);
	}

	@Override
	public void bindView(View v, Context context, Cursor c) {
		String latitude = c.getString(c.getColumnIndex("latitude"));
		String longitude = c.getString(c.getColumnIndex("longitude"));
		String timeval = c.getString(c.getColumnIndex("timeval"));
		String unixTime = c.getString(c.getColumnIndex("unixTime"));
			
		//TextView gpsInfo = (TextView) v.findViewById(R.id.gpsInfo);
		//TextView timeInfo = (TextView) v.findViewById(R.id.timeInfo);
			
		//gpsInfo.setText("lat: "+latitude+", long: "+longitude);
		//timeInfo.setText("unix: "+unixTime+", val:"+timeval);	

	} 

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		View v = inflater.inflate( R.layout.tmplistviewitem, parent, false);
		return v;
	}

}