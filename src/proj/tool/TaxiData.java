package proj.tool;

import java.io.InputStream;
import java.util.HashMap;

import android.content.Context;

public class TaxiData {
	Context context;
	TaxiData(Context c){
		context = c;
		InputStream is = c.getResources().openRawResource(R.raw.test);	
	}
	
	public HashMap<String,String> getDataByLocation(){
		return null;
	}
	
}
