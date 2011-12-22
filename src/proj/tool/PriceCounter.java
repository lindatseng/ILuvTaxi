package proj.tool;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.location.Location;

public class PriceCounter {
	int pathLength;
	List<Location> locationlist;
	private long time_now;
	private long time_prev;
	private int price;
	private Location lastLocation;
	private float distance_sum;
	private long time_sum;
	
	private float distance_total;
	private long time_total;
	
	public static final int IDLE = 0;
	public static final int COUNTING = 1;
	public static final int STOP = 2;
	
	private boolean isRunning = false;
	
	public PriceCounter(){
		init();
	}
	
	void init(){
		locationlist = new ArrayList<Location>();	
		price = 70;
		time_now = 0;
		time_prev = 0;
		distance_sum = 0;
		time_sum = 0;
		distance_total = 0;
		time_total = 0;
		isRunning = false;
	}
	public void reset(){
		init();
	}
	public int getPrice(){
		return price; 
	}
	public void start(){
		isRunning = true;
	}
	public void stop(){
		isRunning = false;
	}
	
	
	public long getTotalTime(){
		return time_total;
	}
	
	public float getTotalDistance(){
		return distance_total;
	}
	
	public void addLocation(Location newLocation, long unixTime){
		// record the path
		HashMap<String,String> map = new HashMap<String,String>();
		map.put("lat", ""+newLocation.getLatitude());
		map.put("lng", ""+newLocation.getLongitude());
		map.put("unixTime", ""+unixTime);
		map.put("accuracy", ""+newLocation.getAccuracy());
		locationlist.add(newLocation);
		time_now = unixTime;
		
		if(time_prev == 0){
			/* first call addLocation */
			time_prev = time_now;
			return ;
		}
		long timeval = time_now - time_prev;  // in millisecond
		float distance = lastLocation.distanceTo(newLocation);		
		
		//float speed = newLocation.getSpeed();
		float speed = (distance / timeval) * 1000;
		if(speed > 5){
			distance_sum += distance;
			distance_total += distance;
			if( distance_sum >= 250 && distance_total >= 1250){
				price += ((int)(distance_sum/1250))*5;
				distance_sum = distance_sum % 1250;
			}
		}else{
			time_sum += timeval;
			time_total += timeval;
			if( time_sum > 100 * 1000 && distance_total >= 1250){  // 1 min 40 sec -> 100 sec
				price += ((int)(time_sum/(100*1000)))*5;
				time_sum = time_sum % (100*1000);
			}
		}
	}
}
