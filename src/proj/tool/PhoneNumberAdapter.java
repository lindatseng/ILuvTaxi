package proj.tool;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import proj.ui.R;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class PhoneNumberAdapter extends SimpleAdapter {
	LayoutInflater  inflater;
	Context context;
	List<HashMap<String,String>> mList;
	public PhoneNumberAdapter(Context context,
			List<HashMap<String, String>> data, int resource, String[] from,
			int[] to) {
		super(context, data, resource, from, to);
		mList = data;
		this.context = context ;
		inflater = LayoutInflater.from(context);
		
	}
	
	 @Override
	 public View getView(int position, View convertView, ViewGroup parent) {
	 
		 View v = inflater.inflate(R.layout.tmplistviewitem,
				 	parent, false);
		 TextView tv1 = (TextView) v.findViewById(R.id.taxiInfo_tv);
		 tv1.setText(mList.get(position).get("region")+"\n"
				 	+mList.get(position).get("name") +"\n"
				 	+mList.get(position).get("phoneNum"));
//		 ImageView call = (ImageView) v.findViewById(R.id.bt_call);
		 v.setOnClickListener(
				 new ClickListener("tel:"+mList.get(position).get("phoneNum").trim() , context));

		 return v;
	 }
	 private class ClickListener implements OnClickListener{
			
			private String number;
			Context context;
			ClickListener(String num, Context c){
				number = num;
				context = c;
			}
			@Override
			public void onClick(View v) {
				Intent callIntent = new Intent(Intent.ACTION_CALL, Uri.parse(number)); 
		        context.startActivity(callIntent);
			}
			 
		 }
}
