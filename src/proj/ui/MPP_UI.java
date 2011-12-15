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
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.gps.HelloItemizedOverlay;
import com.android.gps.MyOverlay;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

public class MPP_UI extends MapActivity implements LocationListener {
	/** Called when the activity is first created. */

	Button home_bt_route, home_bt_pricer;
	View view_map;
	MapView mapView;
	Button bt_show, bt_go, bt_result, bt_next, bt_last;
	LinearLayout layout_startend, layout_result;
	TextView tv_routeNum, tv_routeDist, tv_routeTime, tv_routePrice;
	View view_startend, view_routeresult;
	EditText et_start, et_end;

	private QuickActionWidget mGridStart, mGridEnd;

	static final int INITIAL_ZOOM_LEVEL = 16;
	static int Dest_LATITUDE = 25039717;
	static int Dest_LONGITUDE = 121528863;
	static int Src_LATITUDE = 25018641;
	static int Src_LONGITUDE = 121542526;
	int MY_LATITUDE, MY_LONGITUDE;
	static final double EARTH_RADIUS = 6378.137;
	MapController mc;
	SitesOverlay startItem,endItem;
	Boolean isEndItem = false;
	private LocationManager locationManager;
	private List<GeoPoint> routePoints = new ArrayList<GeoPoint>();

	private int routeNow = 1;
	private GeoPoint srcPoint;
	private GeoPoint destPoint;
	private JSONObject info;
	private int routeNum = 1;
	private HashMap<String, Object> routeInfo = new HashMap<String, Object>();
	List<Overlay> mapOverlays;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		findView();
		setListener();
		setMap();
	}

	private void findView() {
		home_bt_route = (Button) findViewById(R.id.home_bt_route);
		home_bt_pricer = (Button) findViewById(R.id.home_bt_pricer);
		view_map = (View) findViewById(R.id.view_map);
		view_map.setVisibility(View.GONE);
		mapView = (MapView) view_map.findViewById(R.id.mapview);
		bt_show = (Button) findViewById(R.id.bt_show);
		bt_go = (Button) findViewById(R.id.bt_go);
		bt_result = (Button) findViewById(R.id.bt_result);
		layout_startend = (LinearLayout) view_map
				.findViewById(R.id.layout_startend);
		layout_result = (LinearLayout) view_map
				.findViewById(R.id.layout_result);
		view_startend = (View) view_map.findViewById(R.id.view_startend);
		et_start = (EditText) view_startend.findViewById(R.id.et_start);
		et_end = (EditText) view_startend.findViewById(R.id.et_end);
		view_routeresult = (View) view_map.findViewById(R.id.view_routeResult);
		tv_routeNum = (TextView) view_routeresult
				.findViewById(R.id.tv_routeNum);
		tv_routeDist = (TextView) view_routeresult
				.findViewById(R.id.tv_routeDist);
		tv_routeTime = (TextView) view_routeresult
				.findViewById(R.id.tv_routeTime);
		tv_routePrice = (TextView) view_routeresult
				.findViewById(R.id.tv_routePrice);
		bt_next = (Button) view_routeresult.findViewById(R.id.bt_next);
		bt_last = (Button) view_routeresult.findViewById(R.id.bt_last);
	}

	private void setListener() {

		home_bt_route.setOnClickListener(new Button.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				view_map.setVisibility(View.VISIBLE);
				// Intent intent = new Intent();
				// intent.setClass(MPP_UI.this, Route.class);
				// startActivity(intent);

			}

		});

		home_bt_pricer.setOnClickListener(new Button.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				// Intent intent = new Intent();
				// intent.setClass(MPP_UI.this, Pricer.class);
				// startActivity(intent);

			}
		});

		prepareQuickActionGrid();

		bt_show.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub]
				if (layout_startend.getVisibility() == View.VISIBLE)
					layout_startend.setVisibility(View.INVISIBLE);
				else {
					if (layout_result.getVisibility() == View.VISIBLE)
						layout_result.setVisibility(View.GONE);
					layout_startend.setVisibility(View.VISIBLE);
				}
			}
		});

		bt_go.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Src_LATITUDE = startItem.getLat();
				Src_LONGITUDE = startItem.getLon();
				Dest_LATITUDE = endItem.getLat();
				Dest_LONGITUDE = endItem.getLon();

				double srcLat = Src_LATITUDE/ 1E6;
				double srcLon = Src_LONGITUDE / 1E6;
				double desLat = Dest_LATITUDE / 1E6;
				double desLon = Dest_LONGITUDE / 1E6;
				
				Log.d(getPackageName(), "yoyoyoyoyoyo");

				try {
					getInfo(srcLat, srcLon, desLat, desLon);
					Log.d(getPackageName(), routeInfo.toString());
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				// DrawPath(srcPoint, destPoint, Color.BLUE, mapView);

				drawMyPath();
				setResultText();
			}
		});

		bt_result.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				if (layout_result.getVisibility() == View.VISIBLE)
					layout_result.setVisibility(View.INVISIBLE);
				else {
					if (layout_startend.getVisibility() == View.VISIBLE)
						layout_startend.setVisibility(View.GONE);
					layout_result.setVisibility(View.VISIBLE);
				}
			}
		});

		bt_next.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (routeNow < routeNum)
					routeNow++;
				else
					routeNow = 1;

				setResultText();

			}
		});

		bt_last.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (routeNow > 1)
					routeNow--;
				else
					routeNow = routeNum;

				setResultText();
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

		mGridStart
				.setOnQuickActionClickListener(new OnQuickActionClickListener() {

					@Override
					public void onQuickActionClicked(QuickActionWidget widget,
							int position) {
						// TODO Auto-generated method stub
						switch (position) {
						case 0:
							Location mLocation = getLocation(MPP_UI.this);
							MY_LATITUDE = (int) (mLocation.getLatitude() * 1000000);
							MY_LONGITUDE = (int) (mLocation.getLongitude() * 1000000);

							mc.setCenter(new GeoPoint(MY_LATITUDE, MY_LONGITUDE));
							startItem.changePosition(MY_LATITUDE, MY_LONGITUDE);
							break;

						case 1:
							

							break;

						case 2:

							break;

						case 3:

							break;

						default:
							break;
						}

					}
				});

		mGridEnd.setOnQuickActionClickListener(new OnQuickActionClickListener() {

			@Override
			public void onQuickActionClicked(QuickActionWidget widget,
					int position) {
				// TODO Auto-generated method stub
				switch (position) {
				case 0:
					if(isEndItem == false) {
						mapOverlays.add(endItem);
						isEndItem = true;
					}
					Location mLocation = getLocation(MPP_UI.this);
					MY_LATITUDE = (int) (mLocation.getLatitude() * 1000000);
					MY_LONGITUDE = (int) (mLocation.getLongitude() * 1000000);

					mc.setCenter(new GeoPoint(MY_LATITUDE, MY_LONGITUDE));
					endItem.changePosition(MY_LATITUDE, MY_LONGITUDE);
					Log.d(getPackageName(), "lat "+endItem.getLat()+"lon "+endItem.getLon());
					
					break;

				case 1:
					if(isEndItem == false) {
						mapOverlays.add(endItem);
						isEndItem = true;
					}
						endItem.changeToCenter();
					break;

				case 2:
					et_end.setInputType(1);

					break;

				case 3:

					break;

				default:
					break;
				}

			}
		});

	}

	private void setMap() {
		mapView.setBuiltInZoomControls(true);

		locationManager = (LocationManager) this
				.getSystemService(LOCATION_SERVICE);
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,
				0, this);

		Location mLocation = getLocation(this);

		Src_LATITUDE = (int) (mLocation.getLatitude() * 1000000);
		Src_LONGITUDE = (int) (mLocation.getLongitude() * 1000000);

		srcPoint = new GeoPoint(Src_LATITUDE, Src_LONGITUDE);
		destPoint = new GeoPoint(Dest_LATITUDE, Dest_LONGITUDE);

		mc = mapView.getController();
		mc.setZoom(INITIAL_ZOOM_LEVEL);
		mc.setCenter(srcPoint);

		// HelloItemizedOverlay startItem = new
		// HelloItemizedOverlay(this.getResources().getDrawable(R.drawable.startpoint));
		// HelloItemizedOverlay endItem = new
		// HelloItemizedOverlay(this.getResources().getDrawable(R.drawable.endpoint));

		mapOverlays = mapView.getOverlays();

		// OverlayItem startOverlay = new OverlayItem(srcPoint, "", "");
		// startItem.addOverlay(startOverlay);
		// mapOverlays.add(startItem);
		//
		// OverlayItem endOverlay = new OverlayItem(destPoint, "", "");
		// endItem.addOverlay(endOverlay);
		// mapOverlays.add(endItem);

		OverlayItem startOverlay = new OverlayItem(srcPoint, "", "");
		OverlayItem endOverlay = new OverlayItem(destPoint, "", "");

		Drawable marker_start = getResources().getDrawable(
				R.drawable.startpoint);
		Drawable marker_end = getResources().getDrawable(R.drawable.endpoint);

		startItem = new SitesOverlay(marker_start, startOverlay);
		endItem = new SitesOverlay(marker_end, endOverlay);

		mapOverlays.add(startItem);
//		mapOverlays.add(endItem);
	}

	private void setResultText() {
		tv_routeNum.setText("路線 " + routeNow);
		tv_routeDist.setText("距離："
				+ routeInfo.get("DISTANCE" + (routeNow - 1)).toString());
		tv_routeTime.setText("時間："
				+ routeInfo.get("DURATION" + (routeNow - 1)).toString());
		tv_routePrice.setText("價錢："
				+ routeInfo.get("PRICE" + (routeNow - 1)).toString());
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

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onLocationChanged(Location arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onBackPressed() {
		// super.onBackPressed();
		if (view_map.getVisibility() == View.VISIBLE)
			view_map.setVisibility(View.GONE);
		else
			super.onBackPressed();

	}

	private void drawMyPath() {

		for (int j = 0; j < routeNum; j++) {
			if (routeInfo.get("POLYLINE" + j).toString().length() > 0) {
				List<GeoPoint> Points = new ArrayList<GeoPoint>();
				Points = decodePolylines(routeInfo.get("POLYLINE" + j)
						.toString());

				GeoPoint lastPoints = Points.get(0);

				for (int i = 1; i < Points.size(); i++) {
					mapOverlays.add(
							new MyOverlay(lastPoints, Points.get(i), 3,
									Color.CYAN));
					lastPoints = Points.get(i);
				}
			}
		}
	}

	private void getInfo(double srcLat, double srcLon, double desLat,
			double desLon) throws JSONException {

		try {
			String urlstring = "http://maps.google.com/maps/api/directions/json?origin="

					+ srcLat
					+ ","
					+ srcLon
					+ "&destination="
					+ desLat
					+ ","
					+ desLon + "&sensor=true&alternatives=true&language=zh-TW";
//			http://maps.google.com/maps/api/directions/json?origin=25.018641,121.542526&destination=25.039717,121.528863&sensor=true&alternatives=true&language=zh-TW
			Log.d(getPackageName(), "yaaaaaaaaaaaaaa  "+urlstring);
			
			info = new JSONObject(readStrFromUrl(urlstring));
			Log.d(getPackageName(),readStrFromUrl(urlstring) );

			routeNum = info.getJSONArray("routes").length();

			for (int i = 0; i < routeNum; i++) {
				String distance = info.getJSONArray("routes").getJSONObject(i)
						.getJSONArray("legs").getJSONObject(0)
						.getJSONObject("distance").getString("text");
				String duration = info.getJSONArray("routes").getJSONObject(i)
						.getJSONArray("legs").getJSONObject(0)
						.getJSONObject("duration").getString("text");
				String polyline = info.getJSONArray("routes").getJSONObject(i)
						.getJSONObject("overview_polyline").getString("points");
				int dist = info.getJSONArray("routes").getJSONObject(i)
						.getJSONArray("legs").getJSONObject(0)
						.getJSONObject("distance").getInt("value");
				int time = info.getJSONArray("routes").getJSONObject(i)
						.getJSONArray("legs").getJSONObject(0)
						.getJSONObject("duration").getInt("value");
				int price = getPrice(dist, time);
				routeInfo.put("DISTANCE" + i, distance);
				routeInfo.put("DURATION" + i, duration);
				routeInfo.put("POLYLINE" + i, polyline);
				routeInfo.put("DIST" + i, dist);
				routeInfo.put("TIME" + i, time);
				routeInfo.put("PRICE" + i, price);
			}

		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private int getPrice(int distance, int duration) {

		int distancePrice = 70;
		int durationPrice = 0;

		if (distance > 1250) {
			distancePrice += 5 * (1 + (distance - 1250) / 250);
		}

		return (distancePrice + durationPrice);

	}

	private List<GeoPoint> decodePolylines(String poly) {
		int len = poly.length();
		int index = 0;
		int lat = 0;
		int lng = 0;
		routePoints.clear();

		while (index < len) {
			int b, shift = 0, result = 0;
			do {
				b = poly.charAt(index++) - 63;
				result |= (b & 0x1f) << shift;
				shift += 5;
			} while (b >= 0x20);
			int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
			lat += dlat;

			shift = 0;
			result = 0;
			do {
				b = poly.charAt(index++) - 63;
				result |= (b & 0x1f) << shift;
				shift += 5;
			} while (b >= 0x20);
			int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
			lng += dlng;

			GeoPoint p = new GeoPoint((int) (((double) lat / 1E5) * 1E6),
					(int) (((double) lng / 1E5) * 1E6));
			routePoints.add(p);
		}
		return routePoints;
	}

	public static String readStrFromUrl(String url) throws IOException,
			JSONException {
		InputStream is = new URL(url).openStream();
		try {
			BufferedReader rd = new BufferedReader(new InputStreamReader(is,
					Charset.forName("UTF-8")));
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

	private void prepareQuickActionGrid() {
		mGridStart = new QuickActionBar(this);
		// mGrid.addQuickAction(new MyQuickAction(this,
		// R.drawable.gd_action_bar_compose, R.string.gd_pin));
		mGridStart.addQuickAction(new MyQuickAction(this,
				R.drawable.gd_action_bar_locate, R.string.gd_mylocation));
		mGridStart.addQuickAction(new MyQuickAction(this,
				R.drawable.gd_action_bar_export, R.string.gd_pin));
		mGridStart.addQuickAction(new MyQuickAction(this,
				R.drawable.gd_action_bar_search, R.string.gd_mySearch));
		mGridStart.addQuickAction(new MyQuickAction(this,
				R.drawable.gd_action_bar_share, R.string.gd_favorite));

		// mGrid.addQuickAction(new MyQuickAction(this,
		// R.drawable.gd_action_bar_edit, R.string.gd_edit));

		mGridEnd = new QuickActionBar(this);
		// mGrid.addQuickAction(new MyQuickAction(this,
		// R.drawable.gd_action_bar_compose, R.string.gd_pin));
		mGridEnd.addQuickAction(new MyQuickAction(this,
				R.drawable.gd_action_bar_locate, R.string.gd_mylocation));
		mGridEnd.addQuickAction(new MyQuickAction(this,
				R.drawable.gd_action_bar_export, R.string.gd_pin));
		mGridEnd.addQuickAction(new MyQuickAction(this,
				R.drawable.gd_action_bar_search, R.string.gd_mySearch));
		mGridEnd.addQuickAction(new MyQuickAction(this,
				R.drawable.gd_action_bar_share, R.string.gd_favorite));

		// mGrid.addQuickAction(new MyQuickAction(this,
		// R.drawable.gd_action_bar_edit, R.string.gd_edit));

	}

	private class SitesOverlay extends ItemizedOverlay<OverlayItem> {
		private List<OverlayItem> items = new ArrayList<OverlayItem>();
		private Drawable marker = null;
		private OverlayItem inDrag = null;
		private ImageView dragImage = null;
		private int xDragImageOffset = 0;
		private int yDragImageOffset = 0;
		private int xDragTouchOffset = 0;
		private int yDragTouchOffset = 0;

		public SitesOverlay(Drawable marker, OverlayItem overlay) {
			super(marker);
			this.marker = marker;

			dragImage = (ImageView) view_map.findViewById(R.id.drag_start);

			xDragImageOffset = dragImage.getDrawable().getIntrinsicWidth() / 2;
			yDragImageOffset = dragImage.getDrawable().getIntrinsicHeight();

			items.add(overlay);

			populate();
		}

		@Override
		protected OverlayItem createItem(int i) {
			return (items.get(i));
		}

		@Override
		public void draw(Canvas canvas, MapView mapView, boolean shadow) {
			super.draw(canvas, mapView, shadow);

			boundCenterBottom(marker);
		}

		@Override
		public int size() {
			return (items.size());
		}

		@Override
		public boolean onTouchEvent(MotionEvent event, MapView mapView) {
			final int action = event.getAction();
			final int x = (int) event.getX();
			final int y = (int) event.getY();
			boolean result = false;

			if (action == MotionEvent.ACTION_DOWN) {
				for (OverlayItem item : items) {
					Point p = new Point(0, 0);

					mapView.getProjection().toPixels(item.getPoint(), p);

					if (hitTest(item, marker, x - p.x, y - p.y)) {
						result = true;
						inDrag = item;
						items.remove(inDrag);
						populate();

						xDragTouchOffset = 0;
						yDragTouchOffset = 0;

						setDragImagePosition(p.x, p.y);
						dragImage.setVisibility(View.VISIBLE);

						xDragTouchOffset = x - p.x;
						yDragTouchOffset = y - p.y;

						break;
					}
				}
			} else if (action == MotionEvent.ACTION_MOVE && inDrag != null) {
				setDragImagePosition(x, y);
				result = true;
			} else if (action == MotionEvent.ACTION_UP && inDrag != null) {
				dragImage.setVisibility(View.GONE);

				final GeoPoint pt = mapView.getProjection().fromPixels(
						x - xDragTouchOffset, y - yDragTouchOffset);

				Log.d(getPackageName(), "lat " + pt.getLatitudeE6() + " long "
						+ pt.getLongitudeE6());

				OverlayItem toDrop = new OverlayItem(pt, inDrag.getTitle(),
						inDrag.getSnippet());

				items.add(toDrop);
				populate();

				inDrag = null;
				result = true;
			}

			return (result || super.onTouchEvent(event, mapView));
		}

		private void setDragImagePosition(int x, int y) {
			RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) dragImage
					.getLayoutParams();

			lp.setMargins(x - xDragImageOffset - xDragTouchOffset, y
					- yDragImageOffset - yDragTouchOffset, 0, 0);
			dragImage.setLayoutParams(lp);
		}

		public void changePosition(int x, int y) {
			
			for (OverlayItem item : items) {
				Point p = new Point(0, 0);

				mapView.getProjection().toPixels(item.getPoint(), p);
				
				inDrag = item;
				items.remove(inDrag);
				populate();

			}
			
			dragImage.setVisibility(View.GONE);
			final GeoPoint pt = new GeoPoint(x,y);

			Log.d(getPackageName(),
					"lat " + pt.getLatitudeE6() + " long "
							+ pt.getLongitudeE6());

			OverlayItem toDrop = new OverlayItem(pt, "",
					"");

			items.add(toDrop);
			populate();

			inDrag = null;
//			result = true;
		}
		
		public void changeToCenter(){
			for (OverlayItem item : items) {
				Point p = new Point(0, 0);

				mapView.getProjection().toPixels(item.getPoint(), p);
				
				inDrag = item;
				items.remove(inDrag);
				populate();

			}

			Log.d(getPackageName(),
					"lat " + mapView.getMapCenter().getLatitudeE6() + " long "
							+ mapView.getMapCenter().getLongitudeE6());

			OverlayItem toDrop = new OverlayItem(mapView.getMapCenter(), "","");

			items.add(toDrop);
			populate();

			inDrag = null;
		}
		
		public int getLat(){
			GeoPoint tmp = new GeoPoint(0, 0);
			for (OverlayItem item : items) {
				tmp = item.getPoint();
			}
			return tmp.getLatitudeE6();	
		}
		
		public int getLon(){
			GeoPoint tmp = new GeoPoint(0, 0);
			for (OverlayItem item : items) {
				tmp = item.getPoint();
			}
			return tmp.getLongitudeE6();	
		}
		
	}

}