package com.iluvtaxi.main;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.iluvtaxi.tool.LoggerHelper;
import com.iluvtaxi.tool.MyAdapter;
import com.iluvtaxi.tool.PhoneNumberAdapter;
import com.iluvtaxi.tool.TaxiData;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

public class Pricer extends Activity {
	private final String TAG = "Pricer";
	private Button start;
	private Button stop;
	private Button seeLogger;
	private Button clearDB;
	private LoggerHelper loggerHelper;
	private SQLiteDatabase loggerDB;
	private ListView listview;
	private MyAdapter adapter;
	private List<ContentValues> record;
	Context context;
	
	private TaxiData taxidata;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pricer);
        context = this;
        taxidata = new TaxiData(this);
        record = new ArrayList<ContentValues>();
        findView();
        setListener();
        
    }
    @Override
    public void onResume(){
    	super.onResume();
    	IntentFilter filter = new IntentFilter();
		filter.addAction("lockercho.locationUpdate");
		registerReceiver(locReceiver, filter);
		loggerHelper = new LoggerHelper(this);
		loggerDB = loggerHelper.getWritableDatabase();
		if(isServiceRunning("com.android.gps.UserLocation")){
			start.setEnabled(false);
			stop.setEnabled(true);
		}else{
			start.setEnabled(true);
			stop.setEnabled(false);
		}

    }
    
    public void onPause(){
    	super.onPause();
    	unregisterReceiver(locReceiver);
    	loggerDB.close();
    	loggerHelper.close();
    	
    } 
    
    private void findView(){
    	start = (Button) findViewById(R.id.bt_start_service);
    	stop = (Button) findViewById(R.id.bt_stop_service);
    	seeLogger = (Button) findViewById(R.id.bt_see_logger);
    	clearDB = (Button) findViewById(R.id.bt_clear_db);
    	listview = (ListView) findViewById(R.id.listview);
    	listview.setAdapter(new PhoneNumberAdapter(context, taxidata.getAllData() , 1 , new String[]{ } , new int[]{} ));
    }
    
    private void setListener(){
    	
    	start.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				startService(new Intent ("com.android.gps.UserLocation" ));
				start.setEnabled(false);
				stop.setEnabled(true);
				
			}
    	});	
    	stop.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				stopService(new Intent ("com.android.gps.UserLocation" ));
				start.setEnabled(true);
				stop.setEnabled(false);
			}
    	});	
    	
    	seeLogger.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				
				taxidata.printDataInLogCat();
			}
    	
    	});
    	clearDB.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
	
				loggerDB.delete("GPSLog", null, null);
				Toast.makeText(context, "DB cleared", Toast.LENGTH_SHORT);

			}
    	
    	});
    	
    }
    private void updataListView(Cursor c){
    	
    	Log.e(TAG,"updateListView");
    	adapter = new MyAdapter(this, c);
    	listview.setAdapter(adapter);
    }
    
    
    private BroadcastReceiver locReceiver = new  BroadcastReceiver(){
		@Override
		public void onReceive(Context context, Intent intent) {
			if( intent.getAction().equals("lockercho.locationUpdate") ){
				Bundle bundle = intent.getExtras();
				double latitude = bundle.getDouble("latitude");
				double longitude = bundle.getDouble("longitude");
				long timeval = bundle.getLong("timeval");
				long unixTime = bundle.getLong("unixTime");
				
				Log.e(TAG, "get location: "+
						latitude+", "+longitude);
				Log.e(TAG, "timeval: "+timeval);
				Log.e(TAG, "unixTime: "+unixTime);
				Toast.makeText(context, "receive location: "+latitude+", "+longitude, Toast.LENGTH_SHORT).show();
				loggerDB = loggerHelper.getReadableDatabase();
				Cursor c = loggerDB.query("GPSLog", null, null, null, null, null, null);
				updataListView(c);
				
			}
		}
    
    };
    private boolean isServiceRunning(String serviceName) {
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceName.equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

}