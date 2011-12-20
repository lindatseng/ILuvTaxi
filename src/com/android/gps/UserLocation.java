package com.android.gps;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class UserLocation extends Service implements LocationListener{
	private final String TAG = "UserLocationService";
	private LocationManager locationManager;
	private final String broadcastName = "lockercho.locationUpdate";
	private long prevTime = 0;
	private long nowTime = 0;
	
	
	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}
	@Override  
    public void onCreate() {  
		Log.e(TAG,"service start!");
		Toast.makeText(this, "start service", Toast.LENGTH_SHORT).show();
		locationManager = (LocationManager) this
				.getSystemService(LOCATION_SERVICE);
    }  
	
	@Override  
    public void onStart(Intent intent, int startid) {
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2,
				2, this);
    }  
  
    @Override  
    public void onDestroy() {  
    	locationManager.removeUpdates(this);
    	Log.e(TAG,"stop service");
    	Toast.makeText(this, "stop service", Toast.LENGTH_SHORT).show();
    }  
      
    
	@Override
	public void onLocationChanged(Location location) {
		Log.e(TAG, "location change!");
		nowTime = System.currentTimeMillis();
		long pass = nowTime - prevTime;
		if(prevTime == 0) pass = 0;
		prevTime = nowTime;
		Intent i = new Intent();
		Bundle b = new Bundle();
		b.putDouble("latitude", location.getLatitude());
		b.putDouble("longitude", location.getLongitude());
		b.putLong("timeval", pass);
		b.putLong("unixTime", nowTime);
		i.setAction(broadcastName);
		i.putExtras(b);
		this.sendBroadcast(i);
	}

	@Override
	public void onProviderDisabled(String arg0) {
	}

	@Override
	public void onProviderEnabled(String arg0) {
	}

	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
	}

}
