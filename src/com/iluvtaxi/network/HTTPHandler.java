package com.iluvtaxi.network;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

	
public class HTTPHandler {
	
	public static String doPost(String android_id, double lat, double lng, int counter) {
	    // Create HttpClient and Post Header
	    String url = "http://iluvtaxi.appspot.com/upload";
		HttpClient httpclient = new DefaultHttpClient();
	    HttpPost httppost = new HttpPost(url);

	    try {
	        // Add data
	        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(4);
	        nameValuePairs.add(new BasicNameValuePair("deviceID", android_id));
	        nameValuePairs.add(new BasicNameValuePair("lat", String.valueOf(lat)));
	        nameValuePairs.add(new BasicNameValuePair("lng", String.valueOf(lng)));
	        nameValuePairs.add(new BasicNameValuePair("counter", String.valueOf(counter)));
	        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

	        // Execute HTTP Post Request
	        HttpResponse response = httpclient.execute(httppost);
	       
	        int status = response.getStatusLine().getStatusCode();
	        
	        return EntityUtils.toString(response.getEntity());
	        
	    } catch (ClientProtocolException e) {
	        // TODO Auto-generated catch block
	    } catch (IOException e) {
	        // TODO Auto-generated catch block
	    }
		return url;
	} 	
	
}
