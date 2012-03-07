package com.iluvtaxi.tool;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.location.Location;
import android.util.Log;

public class PriceCounter {
	int pathLength;
	List<Location> locationlist;
	private long time_now;
	private long time_prev;
	private Location lastLocation;
	
	private double lat_last,lng_last;
//	private long time_sum;

	private float distance_total;
	private long time_total;

	public static final int IDLE = 0;
	public static final int COUNTING = 1;
	public static final int STOP = 2;
	
	static final double EARTH_RADIUS = 6378.137;

	private boolean isRunning = false;

	public PriceCounter() {
		init();
	}

	void init() {
		locationlist = new ArrayList<Location>();
		time_now = 0;
		time_prev = 0;
//		time_sum = 0;
		distance_total = 0;
		time_total = 0;
		isRunning = false;
		lat_last = 0;
		lng_last = 0;
	}

	public void reset() {
		init();
	}

	// public int getPrice(){
	// return price;
	// }
	public void start() {
		isRunning = true;
		time_now = System.currentTimeMillis();
	}

	public void stop() {
		isRunning = false;
	}
	
//	public void timePause(){
//		
//	}

	public long getTotalTime() {
		return time_total;
	}

	public float getTotalDistance() {
		return distance_total;
	}
	
	public void timeChange(Location location){
		time_total = System.currentTimeMillis() - time_now;
		if(location!=null){
			if( lat_last != 0 && lng_last != 0 ){
				distance_total += GetDistance(lat_last, lng_last, location.getLatitude(), location.getLongitude());
Log.d("proj.main", "lat "+location.getLatitude()+" lng "+location.getLongitude());
				Log.d("proj.main", "dis "+distance_total);
			}
			lat_last = location.getLatitude();
			lng_last = location.getLongitude();
		}
		
	} 
	
	

	public void addLocation(Location newLocation, long unixTime) {
		// record the path
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("lat", "" + newLocation.getLatitude());
		map.put("lng", "" + newLocation.getLongitude());
		map.put("unixTime", "" + unixTime);
		map.put("accuracy", "" + newLocation.getAccuracy());
		locationlist.add(newLocation);
		time_now = unixTime;

		
		
		
//		if (time_prev == 0) {
//			/* first call addLocation */
//			time_prev = time_now;
//			return;
//		}
//		long timeval = time_now - time_prev; // in millisecond
//		float distance = lastLocation.distanceTo(newLocation);
//
//		// float speed = newLocation.getSpeed();
//		float speed = (distance / timeval) * 1000;
//		if (speed < 5) {
//			
//
//		} else {
////			time_sum += timeval;
//			time_total += timeval;
////			if (time_sum > 100 * 1000 && distance_total >= 1250) { // 1 min 40
////																	// sec ->
////																	// 100 sec
////				time_sum = time_sum % (100 * 1000);
////			}
//		}
//		distance_total += distance;
	}

	public int getPrice(int type, int dis, int time) {
		int price_sum = 0;
		int price_time = 0;
		int price_dis = 0;
		int price_plus = 0;
		int price_mul = 0;

		int price_init = 70;
		int dis_threshold = 1250;
		int dis_per = 250;
		int time_per = 100;

		switch (type / 4) {
		case 0:
			time_per = 100;
			price_init = 70;
			switch (type % 4) {
			case 0:
				price_plus = 0;
				price_mul = 0;
				break;
			case 1:
				price_plus = 20;
				price_mul = 0;
				break;
			case 2:
				price_plus = 20;
				price_mul = 0;
				break;
			case 3:
				price_plus = 40;
				price_mul = 0;
				break;
			default:
				break;
			}
			break;
		case 1:
			time_per = 180;
			price_init = 85;
			switch (type % 4) {
			case 0:
				price_plus = 0;
				price_mul = 0;
				break;
			case 1:
				price_plus = 0;
				price_mul = 2;
				break;
			case 2:
				price_plus = 50;
				price_mul = 0;
				break;
			case 3:
				price_plus = 50;
				price_mul = 2;
				break;
			default:
				break;
			}
			break;
		case 2:
			time_per = 180;
			price_init = 85;
			switch (type % 4) {
			case 0:
				price_plus = 0;
				price_mul = 0;
				break;
			case 1:
				price_plus = 0;
				price_mul = 2;
				break;
			case 2:
				price_plus = 20;
				price_mul = 0;
				break;
			case 3:
				price_plus = 20;
				price_mul = 2;
				break;
			default:
				break;
			}
			break;
		case 3:
			time_per = 180;
			price_init = 100;
			switch (type % 4) {
			case 0:
				price_plus = 0;
				price_mul = 0;
				break;
			case 1:
				price_plus = 20;
				price_mul = 0;
				break;
			case 2:
				price_plus = 0;
				price_mul = 0;
				break;
			case 3:
				price_plus = 20;
				price_mul = 0;
				break;
			default:
				break;
			}
			break;
		case 4:
			price_init = 120;
			switch (type % 4) {
			case 0:
				dis_threshold = 1500;
				price_plus = 0;
				price_mul = 0;
				time_per = 120;
				break;
			case 1:
				dis_threshold = 1250;
				price_plus = 20;
				price_mul = 2;
				time_per = 100;
				break;
			case 2:
				dis_threshold = 1500;
				price_plus = 0;
				price_mul = 0;
				time_per = 120;
				break;
			case 3:
				dis_threshold = 1250;
				price_plus = 20;
				price_mul = 2;
				time_per = 100;
				break;
			default:
				break;
			}
			break;
		case 5:
			dis_threshold = 1500;
			time_per = 150;
			price_init = 80;
			switch (type % 4) {
			case 0:
				price_plus = 0;
				price_mul = 0;
				break;
			case 1:
				price_plus = 20;
				price_mul = 0;
				break;
			case 2:
				price_plus = 0;
				price_mul = 0;
				break;
			case 3:
				price_plus = 20;
				price_mul = 0;
				break;
			default:
				break;
			}
			break;
		case 6:
			price_init = 85;
			switch (type % 4) {
			case 0:
				dis_threshold = 1500;
				time_per = 180;
				price_plus = 0;
				price_mul = 0;
				break;
			case 1:
				dis_threshold = 1250;
				time_per = 150;
				price_plus = 0;
				price_mul = 2;
				break;
			case 2:
				dis_threshold = 1500;
				time_per = 180;
				price_plus = 0;
				price_mul = 0;
				break;
			case 3:
				dis_threshold = 1250;
				time_per = 150;
				price_plus = 0;
				price_mul = 2;
				break;
			default:
				break;
			}
			break;
		default:
			time_per = 100;
			break;
		}

		if (dis < 1250) {
			price_dis = price_init;
		} else {
			price_dis = price_init + ((dis - dis_threshold) / dis_per + 1) * 5;
		}

		price_time = (time / time_per) * 5;
		price_sum = price_time + price_dis + price_plus;

		if (price_mul != 0) {
			price_sum = price_sum + ((price_sum * price_mul / 10) / 5) * 5 + 5;
		}

		return price_sum;

	}
	
	private static double rad(double d) {
		return d * Math.PI / 180.0;
	}

	public static double GetDistance(double lat1, double lng1, double lat2,
			double lng2) {
		double radLat1 = rad(lat1);
		double radLat2 = rad(lat2);
		double a = radLat1 - radLat2;
		double b = rad(lng1) - rad(lng2);
		double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2)
				+ Math.cos(radLat1) * Math.cos(radLat2)
				* Math.pow(Math.sin(b / 2), 2)));
		s = s * EARTH_RADIUS;
		// s = Math.round(s * 10000) / 10000;
		Log.d("xxxxxx", "         " + s);
		return s;
	}
}
