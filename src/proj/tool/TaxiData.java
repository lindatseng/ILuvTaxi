package proj.tool;

import java.io.InputStream;
import java.util.HashMap;

import proj.ui.R;

import android.content.Context;

public class TaxiData {
	Context context;
	TaxiData(Context c){
		context = c;
		InputStream is = c.getResources().openRawResource(R.raw.taxilist);	
		
	}
	
	public HashMap<String,String> getDataByLocation(){
		return null;
	}
	
}
