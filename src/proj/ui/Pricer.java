package proj.ui;

import proj.tool.LoggerHelper;
import proj.tool.MyAdapter;
import android.app.Activity;
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

public class Pricer extends Activity {
	private final String TAG = "Pricer";
	private Button start;
	private Button stop;
	private LoggerHelper loggerHelper;
	private SQLiteDatabase loggerDB;
	private ListView listview;
	private MyAdapter adapter;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pricer);
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
    	listview = (ListView) findViewById(R.id.listview);
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
				stop.setEnabled(false);
				start.setEnabled(true);
			}
    	});	
    	stop.setEnabled(false);
    	
    }
    private void updataListView(Cursor c){
    	Log.e(TAG,"updateListView");
    	
    	adapter = new MyAdapter(this, c);
    	listview.setAdapter(adapter);
    	//c.close();
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
				
				ContentValues cv = new ContentValues();
				cv.put("latitude",latitude );
				cv.put("longitude",longitude );
				cv.put("timeval",timeval );
				cv.put("unixTime",unixTime );
				loggerDB.insert("GPSLog", null, cv);
				Cursor c = loggerDB.query("GPSLog", null, null, null, null, null, null);
				updataListView(c);
				
			}
		}
    
    };

}
