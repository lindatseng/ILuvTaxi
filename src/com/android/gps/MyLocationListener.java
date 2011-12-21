package com.android.gps;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

public class MyLocationListener implements LocationListener {
	UserLocation mService;
	MyLocationListener(UserLocation service){
		mService = service;
	}
    @Override
    public void onLocationChanged(Location location) {
    	mService.locationCallBack();
    }
    @Override
    public void onProviderDisabled(String provider) {}
    @Override
    public void onProviderEnabled(String provider) {}
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {}
}
