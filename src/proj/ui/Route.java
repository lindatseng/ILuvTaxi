package proj.ui;

import greendroid.widget.QuickAction;
import greendroid.widget.QuickActionBar;
import greendroid.widget.QuickActionWidget;
import greendroid.widget.QuickActionWidget.OnQuickActionClickListener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.gps.HelloItemizedOverlay;
import com.android.gps.MyOverlay;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

public class Route extends MapActivity implements LocationListener {
	
	MapView mapView;
	Button bt_show,bt_go,bt_result,bt_next,bt_last;
	LinearLayout layout_startend,layout_result;
	TextView tv_routeNum,tv_routeDist,tv_routeTime,tv_routePrice;
	View view_startend,view_routeresult;
	EditText et_start,et_end;
	//25.039717,121.528863
	static final int INITIAL_ZOOM_LEVEL = 16;
	static int Dest_LATITUDE = 25039717;
	static int Dest_LONGITUDE = 121528863;
	static int Src_LATITUDE = 25018641;
	static int Src_LONGITUDE = 121542526;
	static final double EARTH_RADIUS = 6378.137;
	MapController mc;
	private QuickActionWidget mGridStart,mGridEnd;

	private LocationManager locationManager;
	private List<GeoPoint> routePoints = new ArrayList<GeoPoint>();
	
	private int routeNow = 1 ;
	private GeoPoint srcPoint;
	private GeoPoint destPoint;
	private JSONObject info;
	private int routeNum = 1;
	private HashMap<String, Object> routeInfo = new HashMap<String, Object>();
	List<Overlay> mapOverlays;
	
//	final Animation translateAnimation =AnimationUtils.loadAnimation(this, R.anim.tran_anim);
	public static void setLayoutAnimation(ViewGroup panel, Context ctx) {

		  LayoutAnimationController controller = AnimationUtils.loadLayoutAnimation(ctx, R.anim.tran_anim);

		  panel.setLayoutAnimation(controller);

		}


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.route);
        findView();
        setListener();
        setMap();
    }
    
    private void findView(){
    	mapView = (MapView) findViewById(R.id.mapview);
//    	bt_show = (Button) findViewById(R.id.bt_show);
    	bt_go = (Button) findViewById(R.id.bt_go);
//    	bt_result = (Button) findViewById(R.id.bt_result);
    	layout_startend = (LinearLayout) findViewById(R.id.layout_startend);
    	layout_result = (LinearLayout) findViewById(R.id.layout_result);
    	view_startend = (View) findViewById(R.id.view_startend);
//    	setLayoutAnimation(view_startend);
//    	view_startend.setAnimation(translateAnimation);
    	et_start = (EditText) view_startend.findViewById(R.id.et_start);
    	et_end = (EditText) view_startend.findViewById(R.id.et_end);
    	view_routeresult = (View) findViewById(R.id.view_routeResult);
    	tv_routeNum = (TextView) view_routeresult.findViewById(R.id.tv_routeNum);
    	tv_routeDist = (TextView) view_routeresult.findViewById(R.id.tv_routeDist);
    	tv_routeTime = (TextView) view_routeresult.findViewById(R.id.tv_routeTime);
    	tv_routePrice = (TextView) view_routeresult.findViewById(R.id.tv_routePrice);
    	bt_next = (Button) view_routeresult.findViewById(R.id.bt_next);
    	bt_last = (Button) view_routeresult.findViewById(R.id.bt_last);
    	
    	
    	
    }
    
    private void setListener(){
    	
    	prepareQuickActionGrid();

    	
    	bt_show.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub]
				Log.d(getPackageName(), ""+layout_startend.getVisibility());
				if(layout_startend.getVisibility()==View.VISIBLE)
					layout_startend.setVisibility(View.INVISIBLE);
				else
					layout_startend.setVisibility(View.VISIBLE);
			}
		});
    	
    	bt_go.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
						
				double srcLat = srcPoint.getLatitudeE6()/1E6;
				double srcLon = srcPoint.getLongitudeE6()/1E6;
				double desLat = destPoint.getLatitudeE6()/1E6;
				double desLon = destPoint.getLongitudeE6()/1E6;
				
				try {
					getInfo(srcLat,srcLon,desLat,desLon);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

//				DrawPath(srcPoint, destPoint, Color.BLUE, mapView);
				
				drawMyPath();
				tv_routeNum.setText("���u "+routeNow);
				tv_routeDist.setText("�Z���G"+routeInfo.get("DISTANCE"+(routeNow-1)).toString());
				tv_routeTime.setText("�ɶ��G"+routeInfo.get("DURATION"+(routeNow-1)).toString());
				tv_routePrice.setText("���G"+routeInfo.get("PRICE"+(routeNow-1)).toString());
			}
		});
    	
    	bt_result.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(layout_result.getVisibility()==View.VISIBLE)
					layout_result.setVisibility(View.INVISIBLE);
				else
					layout_result.setVisibility(View.VISIBLE);
			}
		});
    	
    	bt_next.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(routeNow<routeNum) routeNow++;
				else routeNow = 1;
				
				tv_routeNum.setText("���u "+routeNow);
				tv_routeDist.setText("�Z���G"+routeInfo.get("DISTANCE"+(routeNow-1)).toString());
				tv_routeTime.setText("�ɶ��G"+routeInfo.get("DURATION"+(routeNow-1)).toString());
				tv_routePrice.setText("���G"+routeInfo.get("PRICE"+(routeNow-1)).toString());
//				clearMyPath(routeNow);
				
			}
		});
    	
    	bt_last.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(routeNow>1) routeNow--;
				else routeNow = routeNum;
				
				tv_routeNum.setText("���u "+routeNow);
				tv_routeDist.setText("�Z���G"+routeInfo.get("DISTANCE"+(routeNow-1)).toString());
				tv_routeTime.setText("�ɶ��G"+routeInfo.get("DURATION"+(routeNow-1)).toString());
				tv_routePrice.setText("���G"+routeInfo.get("PRICE"+(routeNow-1)).toString());
			}
		});
    	
    	et_start.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mGridStart.show(v);
				
			}
		});
    	et_start.setInputType(0);
    	
    	et_end.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mGridEnd.show(v);	
			}
		});
    	et_end.setInputType(0);
    	
    }
    
    private void setMap(){
    	

    	mapView.setBuiltInZoomControls(true);
    	
		locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,0, this);

		Location mLocation = getLocation(this);

		Src_LATITUDE  = (int) (mLocation.getLatitude() * 1000000);
		Src_LONGITUDE = (int) (mLocation.getLongitude() * 1000000);
   	
		srcPoint = new GeoPoint(Src_LATITUDE, Src_LONGITUDE);
		destPoint = new GeoPoint(Dest_LATITUDE, Dest_LONGITUDE);	
	
		mc = mapView.getController();
		mc.setZoom(INITIAL_ZOOM_LEVEL);
		mc.setCenter(srcPoint);
		
		HelloItemizedOverlay startItem = new HelloItemizedOverlay(this.getResources().getDrawable(R.drawable.startpoint));
		HelloItemizedOverlay endItem = new HelloItemizedOverlay(this.getResources().getDrawable(R.drawable.endpoint));
		
		mapOverlays = mapView.getOverlays();

		OverlayItem startOverlay = new OverlayItem(srcPoint, "", "");
		startItem.addOverlay(startOverlay);		
		mapOverlays.add(startItem);
		
		OverlayItem endOverlay = new OverlayItem(destPoint, "", "");
		endItem.addOverlay(endOverlay);		
		mapOverlays.add(endItem);
		
		Drawable marker=getResources().getDrawable(R.drawable.startpoint);
//		mapOverlays.add(new SitesOverlay(marker));


		
    }
    
    private void drawMyPath(){
    	
    	for(int j=0;j<routeNum;j++){
            if (routeInfo.get("POLYLINE"+j).toString().length() > 0)
            {
            	List<GeoPoint> Points = new ArrayList<GeoPoint>();
            	Points = decodePolylines(routeInfo.get("POLYLINE"+j).toString());

            	GeoPoint lastPoints = Points.get(0);
        	
            	for(int i=1;i<Points.size();i++){
            		mapView.getOverlays().add(new MyOverlay(lastPoints, Points.get(i), 3, Color.CYAN));
            		lastPoints = Points.get(i);
            	}
            }	
    	}
    }
    
    private void clearMyPath(int theRoute){
    	if (routeInfo.get("POLYLINE"+theRoute).toString().length() > 0)
        {
        	List<GeoPoint> Points = new ArrayList<GeoPoint>();
        	Points = decodePolylines(routeInfo.get("POLYLINE"+theRoute).toString());

        	GeoPoint lastPoints = Points.get(0);
    	
        	for(int i=1;i<Points.size();i++){
        		mapView.getOverlays().remove(new MyOverlay(lastPoints, Points.get(i), 3, Color.CYAN));
        		lastPoints = Points.get(i);
        	}
        }
    }
    
	private void getInfo(double srcLat, double srcLon, double desLat, double desLon) throws JSONException{
		
		try {
			
			String urlstring="http://maps.google.com/maps/api/directions/json?origin="+srcLat+","+srcLon+"&destination="+desLat+","+desLon+"&sensor=true&alternatives=true&language=zh-TW";
			info = new JSONObject(readStrFromUrl(urlstring)); 
			
			routeNum = info.getJSONArray("routes").length();
			
			for(int i=0;i<routeNum;i++){
				String distance = info.getJSONArray("routes").getJSONObject(i).getJSONArray("legs").getJSONObject(0).getJSONObject("distance").getString("text");
				String duration = info.getJSONArray("routes").getJSONObject(i).getJSONArray("legs").getJSONObject(0).getJSONObject("duration").getString("text");
				String polyline = info.getJSONArray("routes").getJSONObject(i).getJSONObject("overview_polyline").getString("points");
				int dist = info.getJSONArray("routes").getJSONObject(i).getJSONArray("legs").getJSONObject(0).getJSONObject("distance").getInt("value");
				int time = info.getJSONArray("routes").getJSONObject(i).getJSONArray("legs").getJSONObject(0).getJSONObject("duration").getInt("value");
				int price = getPrice(dist,time);
				routeInfo.put("DISTANCE"+i, distance);
				routeInfo.put("DURATION"+i, duration);
				routeInfo.put("POLYLINE"+i, polyline);
				routeInfo.put("DIST"+i, dist);
				routeInfo.put("TIME"+i, time);
				routeInfo.put("PRICE"+i, price);
			}

		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	
	private int getPrice(int distance, int duration){
		
		int distancePrice = 70 ;
		int durationPrice = 0 ;
		
		if(distance > 1250){
			distancePrice += 5*(1+(distance-1250)/250);
		}
		
		return (distancePrice+durationPrice);
		
	}
	
	private List<GeoPoint> decodePolylines(String poly)
	{
	    int len = poly.length();
	    int index = 0;
	    int lat = 0;
	    int lng = 0;
	    routePoints.clear();

	    while (index < len)
	    {
	        int b, shift = 0, result = 0;
	        do
	        {
	            b = poly.charAt(index++) - 63;
	            result |= (b & 0x1f) << shift;
	            shift += 5;
	        } while (b >= 0x20);
	        int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
	        lat += dlat;

	        shift = 0;
	        result = 0;
	        do
	        {
	            b = poly.charAt(index++) - 63;
	            result |= (b & 0x1f) << shift;
	            shift += 5;
	        } while (b >= 0x20);
	        int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
	        lng += dlng;

	        GeoPoint p = new GeoPoint((int) (((double) lat / 1E5) * 1E6), (int) (((double) lng / 1E5) * 1E6));
	        routePoints.add(p);
	    }
	    return routePoints;
	}
	
	
	 public static String readStrFromUrl(String url) throws IOException, JSONException {
		    InputStream is = new URL(url).openStream();
		    try {
		      BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
		      String jsonText = readAll(rd);
		      Log.d("json", jsonText);
		      return jsonText;
		    } finally {
		      is.close();
		    }
		  }
	 
	 private static String readAll(Reader rd) throws IOException {
		    StringBuilder sb = new StringBuilder();
		    int cp;
		    while ((cp = rd.read()) != -1) {
		      sb.append((char) cp);
		    }
		    return sb.toString();
		  }
	 
		private void DrawPath(GeoPoint src, GeoPoint dest, int color,MapView mMapView01) {
			// connect to map web service
			StringBuilder urlString = new StringBuilder();
			urlString.append("http://maps.google.com/maps?f=d&hl=en");
			urlString.append("&saddr=");// from
			urlString.append(Double.toString((double) src.getLatitudeE6() / 1.0E6));
			urlString.append(",");
			urlString
					.append(Double.toString((double) src.getLongitudeE6() / 1.0E6));
			urlString.append("&daddr=");// to
			urlString
					.append(Double.toString((double) dest.getLatitudeE6() / 1.0E6));
			urlString.append(",");
			urlString
					.append(Double.toString((double) dest.getLongitudeE6() / 1.0E6));
			urlString.append("&ie=UTF8&0&om=0&output=kml");
			Log.d("xxx", "URL=" + urlString.toString());
			// get the kml (XML) doc. And parse it to get the coordinates(direction
			// route).
			Document doc = null;
			HttpURLConnection urlConnection = null;
			URL url = null;
			try {
				url = new URL(urlString.toString());
				urlConnection = (HttpURLConnection) url.openConnection();
				urlConnection.setRequestMethod("GET");
				urlConnection.setDoOutput(true);
				urlConnection.setDoInput(true);
				urlConnection.connect();

				DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
				DocumentBuilder db = dbf.newDocumentBuilder();
				doc = db.parse(urlConnection.getInputStream());

				if (doc.getElementsByTagName("GeometryCollection").getLength() > 0) {
					// String path =
					// doc.getElementsByTagName("GeometryCollection").item(0).getFirstChild().getFirstChild().getNodeName();
					String path = doc.getElementsByTagName("GeometryCollection")
							.item(0).getFirstChild().getFirstChild()
							.getFirstChild().getNodeValue();
					Log.d("xxx", "path=" + path);
					String[] pairs = path.split(" ");
					String[] lngLat = pairs[0].split(","); // lngLat[0]=longitude
															// lngLat[1]=latitude
															// lngLat[2]=height
					// src
					double dis = 0;
					GeoPoint startGP = new GeoPoint(
							(int) (Double.parseDouble(lngLat[1]) * 1E6),
							(int) (Double.parseDouble(lngLat[0]) * 1E6));
					mMapView01.getOverlays().add(
							new MyOverlay(startGP, startGP, 1, Color.RED));
					GeoPoint gp1;
					GeoPoint gp2 = startGP;
					for (int i = 1; i < pairs.length; i++) // the last one would be
															// crash
					{
						lngLat = pairs[i].split(",");
						gp1 = gp2;
						// watch out! For GeoPoint, first:latitude, second:longitude
						gp2 = new GeoPoint(
								(int) (Double.parseDouble(lngLat[1]) * 1E6),
								(int) (Double.parseDouble(lngLat[0]) * 1E6));
						mMapView01.getOverlays().add(
								new MyOverlay(gp1, gp2, 2, color));
						dis += GetDistance((double) gp1.getLatitudeE6() / 1E6,
								(double) gp1.getLongitudeE6() / 1E6,
								(double) gp2.getLatitudeE6() / 1E6,
								(double) gp2.getLongitudeE6() / 1E6);
						Log.d("xxx", "pair:" + pairs[i]);
						Log.d(getPackageName(),
								(double) gp1.getLatitudeE6() / 1E6 + " "
										+ gp1.getLongitudeE6() + " "
										+ gp2.getLatitudeE6() + " "
										+ gp2.getLongitudeE6());
						Log.d(getPackageName(), " " + dis);
					}
					Log.d(getPackageName(), "" + dis);
					mMapView01.getOverlays().add(
							new MyOverlay(dest, dest, 3, Color.BLACK)); // use
					// the
					// default
					// color
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
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
		
	public Location getLocation(Context context) {
			LocationManager locMan = (LocationManager) context
					.getSystemService(Context.LOCATION_SERVICE);
			Location location = locMan
					.getLastKnownLocation(LocationManager.GPS_PROVIDER);
			if (location == null) {
				location = locMan
						.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
			}
			return location;
		}
	
    private void prepareQuickActionGrid() {
        mGridStart = new QuickActionBar(this);
//        mGrid.addQuickAction(new MyQuickAction(this, R.drawable.gd_action_bar_compose, R.string.gd_pin));
        mGridStart.addQuickAction(new MyQuickAction(this, R.drawable.gd_action_bar_export, R.string.gd_pin));
        mGridStart.addQuickAction(new MyQuickAction(this, R.drawable.gd_action_bar_share, R.string.gd_favorite));
        mGridStart.addQuickAction(new MyQuickAction(this, R.drawable.gd_action_bar_search, R.string.gd_mySearch));
//        mGrid.addQuickAction(new MyQuickAction(this, R.drawable.gd_action_bar_edit, R.string.gd_edit));
        mGridStart.addQuickAction(new MyQuickAction(this, R.drawable.gd_action_bar_locate, R.string.gd_mylocation));

        mGridStart.setOnQuickActionClickListener(mActionListener);
        
        mGridEnd = new QuickActionBar(this);
//      mGrid.addQuickAction(new MyQuickAction(this, R.drawable.gd_action_bar_compose, R.string.gd_pin));
      mGridEnd.addQuickAction(new MyQuickAction(this, R.drawable.gd_action_bar_export, R.string.gd_pin));
      mGridEnd.addQuickAction(new MyQuickAction(this, R.drawable.gd_action_bar_share, R.string.gd_favorite));
      mGridEnd.addQuickAction(new MyQuickAction(this, R.drawable.gd_action_bar_search, R.string.gd_mySearch));
//      mGrid.addQuickAction(new MyQuickAction(this, R.drawable.gd_action_bar_edit, R.string.gd_edit));
      mGridEnd.addQuickAction(new MyQuickAction(this, R.drawable.gd_action_bar_locate, R.string.gd_mylocation));

      mGridEnd.setOnQuickActionClickListener(mActionListener);
    }


	@Override
	public void onLocationChanged(Location arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderDisabled(String arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}
	
    private OnQuickActionClickListener mActionListener = new OnQuickActionClickListener() {
        public void onQuickActionClicked(QuickActionWidget widget, int position) {
         //   Toast.makeText(QuickActionActivity.this, "Item " + position + " clicked", Toast.LENGTH_SHORT).show();
        }
    };
	
    private static class MyQuickAction extends QuickAction {
        
        private static final ColorFilter BLACK_CF = new LightingColorFilter(Color.BLACK, Color.BLACK);

        public MyQuickAction(Context ctx, int drawableId, int titleId) {
            super(ctx, buildDrawable(ctx, drawableId), titleId);
        }
        
        private static Drawable buildDrawable(Context ctx, int drawableId) {
            Drawable d = ctx.getResources().getDrawable(drawableId);
            d.setColorFilter(BLACK_CF);
            return d;
        }
        
    }
    
    private class SitesOverlay extends ItemizedOverlay<OverlayItem> {
        private List<OverlayItem> items=new ArrayList<OverlayItem>();
        private Drawable marker=null;
        private OverlayItem inDrag=null;
        private ImageView dragImage=null;
        private int xDragImageOffset=0;
        private int yDragImageOffset=0;
        private int xDragTouchOffset=0;
        private int yDragTouchOffset=0;
        
        public SitesOverlay(Drawable marker, OverlayItem overlay) {
          super(marker);
          this.marker=marker;
          
//          dragImage=(ImageView)findViewById(R.id.drag);
          xDragImageOffset=dragImage.getDrawable().getIntrinsicWidth()/2;
          yDragImageOffset=dragImage.getDrawable().getIntrinsicHeight();
          GeoPoint testPoint = new GeoPoint(25039717,121528863);
          items.add(new OverlayItem(testPoint,
                                    "UN", "United Nations"));
//          items.add(new OverlayItem(getPoint(40.76866299974387,
//                                              -73.98268461227417),
//                                    "Lincoln Center",
//                                    "Home of Jazz at Lincoln Center"));
//          items.add(new OverlayItem(getPoint(40.765136435316755,
//                                              -73.97989511489868),
//                                    "Carnegie Hall",
//                  "Where you go with practice, practice, practice"));
//          items.add(new OverlayItem(getPoint(40.70686417491799,
//                                              -74.01572942733765),
//                                    "The Downtown Club",
//                            "Original home of the Heisman Trophy"));

          populate();
        }
        
        @Override
        protected OverlayItem createItem(int i) {
          return(items.get(i));
        }
        
        @Override
        public void draw(Canvas canvas, MapView mapView,
                          boolean shadow) {
          super.draw(canvas, mapView, shadow);
          
          boundCenterBottom(marker);
        }
        
        @Override
        public int size() {
          return(items.size());
        }
        
        @Override
        public boolean onTouchEvent(MotionEvent event, MapView mapView) {
          final int action=event.getAction();
          final int x=(int)event.getX();
          final int y=(int)event.getY();
          boolean result=false;
          
          if (action==MotionEvent.ACTION_DOWN) {
            for (OverlayItem item : items) {
              Point p=new Point(0,0);
              
              mapView.getProjection().toPixels(item.getPoint(), p);
              
              if (hitTest(item, marker, x-p.x, y-p.y)) {
                result=true;
                inDrag=item;
                items.remove(inDrag);
                populate();

                xDragTouchOffset=0;
                yDragTouchOffset=0;
                
                setDragImagePosition(p.x, p.y);
                dragImage.setVisibility(View.VISIBLE);

                xDragTouchOffset=x-p.x;
                yDragTouchOffset=y-p.y;
                
                break;
              }
            }
          }
          else if (action==MotionEvent.ACTION_MOVE && inDrag!=null) {
            setDragImagePosition(x, y);
            result=true;
          }
          else if (action==MotionEvent.ACTION_UP && inDrag!=null) {
            dragImage.setVisibility(View.GONE);
            
            final GeoPoint pt=mapView.getProjection().fromPixels(x-xDragTouchOffset,
                                                       y-yDragTouchOffset);
            
            Log.d(getPackageName(), "lat "+pt.getLatitudeE6()+" long "+pt.getLongitudeE6());
//            Route.this.runOnUiThread(new Runnable() {
//
//				@Override
//				public void run() {
//					// TODO Auto-generated method stub
//
//					Toast.makeText(Route.class, "lat "+pt.getLatitudeE6()+" long "+pt.getLongitudeE6(), Toast.LENGTH_SHORT);
//				}
//			});

            
            OverlayItem toDrop=new OverlayItem(pt, inDrag.getTitle(),
                                               inDrag.getSnippet());
            
            items.add(toDrop);
            populate();
            
            inDrag=null;
            result=true;
          }
          
          return(result || super.onTouchEvent(event, mapView));
        }
        
        private void setDragImagePosition(int x, int y) {
          RelativeLayout.LayoutParams lp=
            (RelativeLayout.LayoutParams)dragImage.getLayoutParams();
                
          lp.setMargins(x-xDragImageOffset-xDragTouchOffset,
                          y-yDragImageOffset-yDragTouchOffset, 0, 0);
          dragImage.setLayoutParams(lp);
        }
      }
    
    
    
    
	
}
