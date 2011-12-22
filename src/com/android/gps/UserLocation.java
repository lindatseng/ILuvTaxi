package com.android.gps;

import proj.tool.LoggerHelper;
import proj.tool.PriceCounter;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class UserLocation extends Service{
	private final String TAG = "UserLocationService";
	Context context;
	private Location mLocation;

	
	private LocationManager mLocationManager;
	MyLocationListener mLocListener;
	
	public static PriceCounter priceCounter = new PriceCounter();
	
	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}
	@Override  
    public void onCreate() {  
		Log.e(TAG,"service start!");
		Toast.makeText(this, "start service", Toast.LENGTH_SHORT).show();
		context = this;
		mLocListener = new MyLocationListener(this);
    }  
	
	@Override  
    public void onStart(Intent intent, int startid) {
		mLocationManager
         		= (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mLocListener);
    }  
  
    @Override  
    public void onDestroy() {
    	Log.e(TAG,"stop service");
    	Toast.makeText(this, "stop service", Toast.LENGTH_SHORT).show();
    }  
   
    public void locationCallBack() {
    	mLocation = getLocation(this);
    	Log.e("location callback", "location: "+mLocation.getLatitude()+", "+mLocation.getLongitude()); 
    	priceCounter.addLocation(mLocation, System.currentTimeMillis());
    
    }
    
    public Location getLocation(Context context) {
		LocationManager locMan = (LocationManager) context
				.getSystemService(Context.LOCATION_SERVICE);
		Location location = locMan
				.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		if (location == null) {
			location = locMan
					.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		}
		return location;
	}
    
    
}
