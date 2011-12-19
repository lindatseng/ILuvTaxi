package proj.tool;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import com.android.gps.UserLocation;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class LocationRunnable implements Runnable {
    private static final String TAG = "LocationRunnable";
    
    private static final String TYPE_NETWORK = "TYPE_NETWORK";
    private static final String TYPE_GPS =  "TYPE_GPS";
    private static final String TYPE_NULL = "TYPE_NULL";

    private static final long LOCATION_TIMEOUT = 1000*3;  // 3 sec

    private final UserLocation mService;
    private Location mBestLocation;
    private LocationListener mMyListener;
    private final LocationManager mLocationManager;
    private Timer mTimer;

    public LocationRunnable(UserLocation service) {
        
        mService = service;
        mLocationManager
                = (LocationManager)mService.getSystemService(Context.LOCATION_SERVICE);
    }
    

    @Override
    public void run() {
    	Log.e(TAG, "LocationRunnable run()");
        mBestLocation = null;
        mMyListener = new MyLocationListener();
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mMyListener);
        mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, mMyListener);
        Location oldGps = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        Location oldNetwork = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        if(isBetterLocation(oldGps, mBestLocation)) {
            mBestLocation = oldGps;
        }
        if(isBetterLocation(oldNetwork, mBestLocation)) {
            mBestLocation = oldNetwork;
        }
        mTimer = new Timer(true);
        mTimer.schedule(new LocationTimeOut(), LOCATION_TIMEOUT);
    }

    private boolean isBetterLocation(Location newLocation, Location oldLocation) {
        if(newLocation == null) {
            return false;
        }
        if(!newLocation.hasAccuracy()) {
            return false;
        }
        long currentTime = new Date().getTime();
        if(currentTime - newLocation.getTime() > LOCATION_TIMEOUT) {
            // The newLocation is too old. Do not use it.
            return false;
        }
        /*
        if(newLocation.getAccuracy() >= MIN_ACCURACY) {
            // The newLocation is not accurate enough. Do not use it.
            return false;
        }*/
        if(oldLocation == null) {
            // There is no oldLocation. Use the newLocation
            return true;
        }
        return newLocation.getAccuracy() < oldLocation.getAccuracy();
    }

    private void returnResultToService() {
            //mLocationManager.removeUpdates(mMyListener);
            if(mBestLocation == null) {
                notifyFinish(TYPE_NULL, 0, 0, 0);
            } else {
                if(mBestLocation.getProvider().equals(LocationManager.GPS_PROVIDER)) {
                    notifyFinish(TYPE_GPS, mBestLocation.getLatitude()
                            , mBestLocation.getLongitude(), mBestLocation.getAccuracy());
                } else if (mBestLocation.getProvider().equals(LocationManager.NETWORK_PROVIDER)) {
                    notifyFinish(TYPE_NETWORK, mBestLocation.getLatitude()
                            , mBestLocation.getLongitude(), mBestLocation.getAccuracy());
                } else {
                    Log.e(TAG, mBestLocation.getProvider());
                    notifyFinish("other", mBestLocation.getLatitude()
                            , mBestLocation.getLongitude(), mBestLocation.getAccuracy());
                }
            }
    }

    private void notifyFinish(String type, double lat, double lng, float accuracy) {
        mService.locationCallBack(type, lat, lng, accuracy);
        mService.getHandler().sendEmptyMessage(UserLocation.MESSAGE_LOCATION);
    }

    private class MyLocationListener implements LocationListener {
        @Override
        public void onLocationChanged(Location location) {
            if(isBetterLocation(location, mBestLocation)) {
                mBestLocation = location;
            }
        }
        @Override
        public void onProviderDisabled(String provider) {}
        @Override
        public void onProviderEnabled(String provider) {}
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {}
    }

    private class LocationTimeOut extends TimerTask {
        @Override
        public void run() {
            returnResultToService();
           // run();
        }
    }
}
