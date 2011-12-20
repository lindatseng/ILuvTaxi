package com.android.gps;

import org.json.JSONException;
import org.json.JSONObject;

import proj.tool.LoggerHelper;

import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;

import android.location.LocationManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

public class UserLocation extends Service{
	private final String TAG = "UserLocationService";
	private final String broadcastName = "lockercho.locationUpdate";
	private long prevTime = 0;
	private long nowTime = 0;
	Context context;
	private Handler mHandler;
	public static final int MESSAGE_LOCATION = 5566;
	private JSONObject mLocation;
	private HandlerThread mLocationThread;
	private Handler mLocationHandler;
	private boolean mIsLocationRunning = false;
	private LoggerHelper loggerHelper;
	private SQLiteDatabase loggerDB;
	
	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}
	@Override  
    public void onCreate() {  
		Log.e(TAG,"service start!");
		Toast.makeText(this, "start service", Toast.LENGTH_SHORT).show();
		context = this;
    }  
	
	@Override  
    public void onStart(Intent intent, int startid) {
		mLocationThread = new HandlerThread("location");
        mLocationThread.start();
        mLocationHandler = new Handler(mLocationThread.getLooper());
		mHandler = new Handler() {
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                UserLocation.this.handleMessage(msg.what);
            }
        };
        loggerHelper = new LoggerHelper(this);
        startLocation();
    }  
  
    @Override  
    public void onDestroy() {
    	mLocationThread.quit();
    	Log.e(TAG,"stop service");
    	Toast.makeText(this, "stop service", Toast.LENGTH_SHORT).show();
    	
    }  
    
    
    public void startLocation() {
        if(!mIsLocationRunning) {
            mLocation = null;
            mIsLocationRunning = true;
            mLocationHandler.post(new proj.tool.LocationRunnable(this));
        }
    }
    
    public void locationCallBack(String type, double lat, double lng, float accuracy) {
    	 try {
             mLocation = new JSONObject();
             mLocation.put("type", type);
             mLocation.put("latitude", lat);
             mLocation.put("longitude", lng);
             mLocation.put("accuracy", accuracy);
             mIsLocationRunning = false;
         } catch (JSONException e) {
             Log.w(TAG, "Unable to write location data");
         }
    }
    
    public Handler getHandler() {
        return mHandler;
    }
    
    private void handleMessage(int type) {
        switch(type) {
            case MESSAGE_LOCATION:
            	Log.e(TAG, "location change!");
        		Toast.makeText(context, "Location change", Toast.LENGTH_SHORT).show();
        		nowTime = System.currentTimeMillis();
        		long pass = nowTime - prevTime;
        		if(prevTime == 0) pass = 0;
        		prevTime = nowTime;
        		
        		
        		Thread thr = new Thread(new Runnable(){
					@Override
					public void run() {
						loggerDB = loggerHelper.getWritableDatabase();
		        		ContentValues cv = new ContentValues();
						try {
							cv.put("latitude",""+mLocation.getDouble("latitude") );
							cv.put("longitude", ""+mLocation.getDouble("longitude") );
						} catch (JSONException e) {
							e.printStackTrace();
						}
						cv.put("timeval",(prevTime == 0)? "0": (""+(prevTime-nowTime)) );
						cv.put("unixTime", ""+nowTime );
						loggerDB.insert("GPSLog", null, cv);
					}
        			
        		});
        		
        		
        		Intent i = new Intent();
        		Bundle b = new Bundle();
        		try {
        			b.putDouble("latitude", mLocation.getDouble("latitude"));
        			b.putDouble("longitude", mLocation.getDouble("longitude"));
        		} catch (JSONException e) {
        			e.printStackTrace();
        		}
        		
        		b.putLong("timeval", pass);
        		b.putLong("unixTime", nowTime);
        		i.setAction(broadcastName);
        		i.putExtras(b);
        		this.sendBroadcast(i);
        		startLocation();
                break;
        }
        
    }

}
