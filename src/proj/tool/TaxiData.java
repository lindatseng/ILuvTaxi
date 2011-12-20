package proj.tool;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import proj.ui.R;

import android.content.Context;

public class TaxiData {
	Context context;
	String str;
	TaxiData(Context c){
		context = c;
		BufferedInputStream bis = new BufferedInputStream(c.getResources().openRawResource(R.raw.taxilist));
		DataInputStream dis = new DataInputStream(bis);
		int i = 0;
		try {
			while((str = dis.readLine()) != null){
				switch(i){
					case 0:
						break;
					case 1:
						break;
					case 2:
						break;
					default:
						break;	
				}
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public HashMap<String,String> getDataByLocation(){
		return null;
	}
	
}
