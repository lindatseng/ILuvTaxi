package com.android.gps;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

public class LocationHelper {

	private final String TAG = LocationHelper.class.getSimpleName();

	LocationManager locationManager;
	private LocationResult locationResult;
	boolean gpsEnabled = false;
	boolean networkEnabled = false;
	boolean gpsDone = false;
	boolean networkDone = false;

	public boolean getLocation(Context context, LocationResult result) {
		locationResult = result;

		if (locationManager == null)
			locationManager = (LocationManager) context
					.getSystemService(Context.LOCATION_SERVICE);
		// exceptions thrown if provider not enabled
		try {
			gpsEnabled = locationManager
					.isProviderEnabled(LocationManager.GPS_PROVIDER);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		try {
			networkEnabled = locationManager
					.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		Log.d(TAG,
				"gps:" + String.valueOf(gpsEnabled) + " wireless:"
						+ String.valueOf(networkEnabled));
		// dont start listeners if no provider is enabled
		if (!gpsEnabled && !networkEnabled) {

			return false;
		}

		if (gpsEnabled) {
			locationManager.requestLocationUpdates(
					LocationManager.GPS_PROVIDER, 0, 0, locationListenerGps);
		}
		if (networkEnabled) {
			locationManager.requestLocationUpdates(
					LocationManager.NETWORK_PROVIDER, 0, 0,
					locationListenerNetwork);
		}

		GetLastLocation();
		return true;
	}

	public void startLocationUpdates() {
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,
				0, locationListenerGps);
		locationManager
				.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0,
						locationListenerNetwork);
	}

	public void stopLocationUpdates() {
		locationManager.removeUpdates(locationListenerGps);
		locationManager.removeUpdates(locationListenerNetwork);
	}

	LocationListener locationListenerGps = new LocationListener() {
		public void onLocationChanged(Location location) {
			if (location == null) {
				Log.d(TAG, "gps null location");
				return;
			}
			networkDone = true;
			gpsDone = true;
			locationResult.gotLocation(location);
			locationManager.removeUpdates(this);
			locationManager.removeUpdates(locationListenerNetwork);
			Log.d(TAG, "gps loacation updated");
		}

		public void onProviderDisabled(String provider) {
		}

		public void onProviderEnabled(String provider) {
		}

		public void onStatusChanged(String provider, int status, Bundle extra) {
		}
	};

	LocationListener locationListenerNetwork = new LocationListener() {
		public void onLocationChanged(Location location) {
			if (location == null) {
				Log.d(TAG, "network null location");
				return;
			}
			networkDone = true;
			locationResult.gotLocation(location);
			locationManager.removeUpdates(this);
			Log.d(TAG, "network loacation updated");
			// locationManager.removeUpdates(locationListenerGps);

		}

		public void onProviderDisabled(String provider) {
		}

		public void onProviderEnabled(String provider) {
		}

		public void onStatusChanged(String provider, int status, Bundle extra) {
		}

	};

	private void GetLastLocation() {
		// locationManager.removeUpdates(locationListenerGps);
		// locationManager.removeUpdates(locationListenerNetwork);

		Location gpsLocation = null;
		Location networkLocation = null;

		if (gpsEnabled && gpsDone) {
			gpsLocation = locationManager
					.getLastKnownLocation(LocationManager.GPS_PROVIDER);
			if (gpsLocation != null)
				Log.d(TAG,
						"gps last location:"
								+ String.valueOf(gpsLocation.getLatitude())
								+ ":"
								+ String.valueOf(gpsLocation.getLongitude())
								+ ":"
								+ String.valueOf(gpsLocation.getAccuracy()));
			else
				Log.d(TAG, "gps last location = null");
		}
		if (networkEnabled && networkDone) {
			networkLocation = locationManager
					.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
			if (networkLocation != null)
				Log.d(TAG,
						"network last location:"
								+ String.valueOf(networkLocation.getLatitude())
								+ ":"
								+ String.valueOf(networkLocation.getLongitude())
								+ ":"
								+ String.valueOf(networkLocation.getAccuracy()));
			else
				Log.d(TAG, "network last location= null");

		}
		// if there are both values use the latest one
		if (gpsLocation != null && networkLocation != null) {

			if (gpsLocation.getTime() > networkLocation.getTime())
				locationResult.gotLocation(gpsLocation);
			else
				locationResult.gotLocation(networkLocation);
			return;
		}

		if (gpsLocation != null) {
			locationResult.gotLocation(gpsLocation);
			return;
		}

		if (networkLocation != null) {
			locationResult.gotLocation(networkLocation);
			return;
		}

		Log.d(TAG, "location all null");
		locationResult.gotLocation(null);
	}

	public static abstract class LocationResult {
		public abstract void gotLocation(Location location);
	}

}