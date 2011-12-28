package proj.tool;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import proj.main.R;


import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

public class TaxiData {
	public static final String TAG = "TaxiData";
	Context context;
	String str;
	private List<HashMap<String,String>> taxiInfo;
	
	public TaxiData(Context c){
		context = c;
		taxiInfo = new ArrayList<HashMap<String,String>>();
		Resources r = context.getResources();
		HashMap<String,String> map;
		String[] region = r.getStringArray(R.array.region);
		String[] name = r.getStringArray(R.array.name);
		String[] phoneNum = r.getStringArray(R.array.phoneNum);
		 
		for(int i=0 ; i<region.length ; i++){
			map = new HashMap<String, String>();
			if(region[i].length() > 2){
				map.put("region", region[i].substring(0, 3));
			}else{
				map.put("region", region[i]);
			}
			map.put("name", name[i]);
			map.put("phoneNum", phoneNum[i]);
			taxiInfo.add(map);
		}
		
		
	}
	
	public List<HashMap<String, String>> getAllData(){
		return taxiInfo;
	}
	public HashMap<String,String> getDataByLocation(){
		return null;
	}
	public void printDataInLogCat(){
		if(taxiInfo == null) return;
		for(HashMap map : taxiInfo){
			Log.e(TAG,""+map.get("region")+", "+map.get("name")+", "+map.get("phoneNum"));
		}
	}
	
}
