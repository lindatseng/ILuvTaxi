package proj.ui;

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
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import proj.tool.PhoneNumberAdapter;
import proj.tool.TaxiData;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.opengl.Visibility;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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

	Button home_bt_route, home_bt_pricer, home_bt_call, bt_call_back,
			home_bt_info, bt_info_back;
	View view_map, view_call_back, view_info_back;
	MapView mapView;
	Button bt_go, bt_next, bt_last;
	ImageButton bt_start, bt_end;
	int type_start = 0, type_end = 0;
	LinearLayout layout_startend, layout_result, layout_call, layout_info;
	TextView tv_routeNum, tv_routeDist, tv_routeTime, tv_routePrice,
			tv_copyrights;
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
	SitesOverlay startItem, endItem;
	Boolean isEndItem = false;
	Boolean isStartItem = true;
	private LocationManager locationManager;
	private List<GeoPoint> routePoints = new ArrayList<GeoPoint>();

	private int routeNow = 1;
	private GeoPoint srcPoint;
	private GeoPoint destPoint = new GeoPoint(Dest_LATITUDE, Dest_LONGITUDE);
	private JSONObject info;
	private int routeNum = 1;
	private HashMap<String, Object> routeInfo = new HashMap<String, Object>();
	private HashMap<String, Object> searchInfo = new HashMap<String, Object>();
	private int searchNum = 0;
	List<Overlay> mapOverlays;
	
	ListView taxiInfoListView;
	TaxiData taxidata;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.main);
		findView();
		setListener();
		setMap();
		setTaxiInfo(this);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
	}

	private void findView() {
		home_bt_route = (Button) findViewById(R.id.home_bt_route);
		home_bt_pricer = (Button) findViewById(R.id.home_bt_pricer);
		home_bt_call = (Button) findViewById(R.id.home_button_call);
		bt_call_back = (Button) findViewById(R.id.bt_call_back);
		home_bt_info = (Button) findViewById(R.id.home_bt_info);
		bt_info_back = (Button) findViewById(R.id.bt_info_back);
		view_call_back = (View) findViewById(R.id.view_call_back);
		view_info_back = (View) findViewById(R.id.view_info_back);
		view_map = (View) findViewById(R.id.view_map);
		view_map.setVisibility(View.GONE);
		mapView = (MapView) view_map.findViewById(R.id.mapview);
		bt_go = (Button) findViewById(R.id.bt_go);
		layout_startend = (LinearLayout) view_map
				.findViewById(R.id.layout_startend);
		layout_result = (LinearLayout) view_map
				.findViewById(R.id.layout_result);
		layout_call = (LinearLayout) findViewById(R.id.layout_call);
		layout_info = (LinearLayout) findViewById(R.id.layout_info);
		view_startend = (View) view_map.findViewById(R.id.view_startend);
		et_start = (EditText) view_startend.findViewById(R.id.et_start);
		et_end = (EditText) view_startend.findViewById(R.id.et_end);
		bt_start = (ImageButton) view_startend.findViewById(R.id.bt_start);
		bt_end = (ImageButton) view_startend.findViewById(R.id.bt_end);
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
		
		taxiInfoListView = (ListView) findViewById(R.id.taxiInfoListview);
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

				Intent intent = new Intent();
				intent.setClass(MPP_UI.this, Pricer.class);
				startActivity(intent);

			}
		});

		home_bt_call.setOnClickListener(new Button.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				layout_call.setVisibility(View.VISIBLE);
			}

		});

		bt_call_back.setOnClickListener(new Button.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				layout_call.setVisibility(View.GONE);
			}

		});

		home_bt_info.setOnClickListener(new Button.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				layout_info.setVisibility(View.VISIBLE);
			}

		});

		bt_info_back.setOnClickListener(new Button.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				layout_info.setVisibility(View.GONE);
			}

		});

		view_call_back.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				layout_call.setVisibility(View.GONE);
			}
		});

		view_info_back.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				layout_info.setVisibility(View.GONE);
			}
		});

		prepareQuickActionGrid();

		bt_go.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Src_LATITUDE = startItem.getLat();
				Src_LONGITUDE = startItem.getLon();
				Dest_LATITUDE = endItem.getLat();
				Dest_LONGITUDE = endItem.getLon();

				double srcLat = Src_LATITUDE / 1E6;
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
				selMyPath(routeNow);
				mc.setCenter(new GeoPoint((Src_LATITUDE + Dest_LATITUDE) / 2,
						(Src_LONGITUDE + Dest_LONGITUDE) / 2));
				mc.setZoom(15);
				layout_startend.setVisibility(View.GONE);
				layout_result.setVisibility(View.VISIBLE);
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
				mapOverlays.clear();
				selMyPath(routeNow);

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
				mapOverlays.clear();
				selMyPath(routeNow);
			}
		});

		bt_start.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (type_start == 0) {
					mGridStart.show(v);
				} else if (type_start == 2) {
					mapOverlays.remove(startItem);
					startItem.doPopulate();
					et_start.setText("");
					mc.zoomOut();
					isStartItem = false;
				} else
					et_start.setText("");
			}
		});

		et_start.addTextChangedListener(new TextWatcher() {

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub

				et_start.setTextColor(android.graphics.Color.BLACK);

				if (et_start.getText().length() == 0) {
					bt_start.setImageDrawable(getResources().getDrawable(
							R.drawable.pop));
					type_start = 0;
				} else {
					bt_start.setImageDrawable(getResources().getDrawable(
							R.drawable.close));
					type_start = 1;
				}

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub

			}

		});

		et_start.setOnKeyListener(new View.OnKeyListener() {

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				// TODO Auto-generated method stub
				if (event.getAction() == KeyEvent.ACTION_DOWN
						&& keyCode == KeyEvent.KEYCODE_ENTER) {
					Log.d(getPackageName(), "enter presseddddddddd  "
							+ et_start.getText().toString());
					InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					mgr.hideSoftInputFromWindow(et_start.getWindowToken(), 0);

					if (et_start.getText().length() == 0) {
						Toast toast = Toast.makeText(mapView.getContext(),
								"請輸入地址查詢", Toast.LENGTH_SHORT);
						toast.setGravity(Gravity.CENTER, 0, 0);
						toast.show();
					} else {
						try {
							if (getGeo(et_start.getText().toString()) == true) {
								Src_LATITUDE = Integer.valueOf(searchInfo.get(
										"LAT" + searchNum).toString());
								Src_LONGITUDE = Integer.valueOf(searchInfo.get(
										"LNG" + searchNum).toString());
								et_start.setText(searchInfo.get(
										"ADDRESS" + searchNum).toString());
								mc.setCenter(new GeoPoint(Src_LATITUDE,
										Src_LONGITUDE));
								startItem.changePosition(Src_LATITUDE,
										Src_LONGITUDE);
							} else {
								Toast toast = Toast.makeText(
										mapView.getContext(), "查無資料，請重新輸入地址查詢",
										Toast.LENGTH_SHORT);
								toast.setGravity(Gravity.CENTER, 0, 0);
								toast.show();
							}
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					return true;
				}
				return false;
			}
		});

		et_end.addTextChangedListener(new TextWatcher() {

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				et_end.setTextColor(android.graphics.Color.BLACK);
				if (et_end.getText().length() == 0) {
					bt_end.setImageDrawable(getResources().getDrawable(
							R.drawable.pop));
					type_end = 0;
				} else {
					bt_end.setImageDrawable(getResources().getDrawable(
							R.drawable.close));
					type_end = 1;
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub

			}

		});

		et_end.setOnKeyListener(new View.OnKeyListener() {

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				// TODO Auto-generated method stub
				if (event.getAction() == KeyEvent.ACTION_DOWN
						&& keyCode == KeyEvent.KEYCODE_ENTER) {
					InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					mgr.hideSoftInputFromWindow(et_end.getWindowToken(), 0);

					if (et_end.getText().length() == 0) {
						Toast toast = Toast.makeText(mapView.getContext(),
								"請輸入地址查詢", Toast.LENGTH_SHORT);
						toast.setGravity(Gravity.CENTER, 0, 0);
						toast.show();
					} else {
						try {
							if (getGeo(et_end.getText().toString()) == true) {
								Dest_LATITUDE = Integer.valueOf(searchInfo.get(
										"LAT" + searchNum).toString());
								Dest_LONGITUDE = Integer.valueOf(searchInfo
										.get("LNG" + searchNum).toString());
								et_end.setText(searchInfo.get(
										"ADDRESS" + searchNum).toString());
								mc.setCenter(new GeoPoint(Dest_LATITUDE,
										Dest_LONGITUDE));
								if (isEndItem == false) {
									mapOverlays.add(endItem);
									isEndItem = true;
								}
								endItem.changePosition(Dest_LATITUDE,
										Dest_LONGITUDE);
							} else {
								Toast toast = Toast.makeText(
										mapView.getContext(), "查無資料，請重新輸入地址查詢",
										Toast.LENGTH_SHORT);
								toast.setGravity(Gravity.CENTER, 0, 0);
								toast.show();
							}
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					return true;
				}
				return false;
			}
		});

		bt_end.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (type_end == 0) {
					mGridEnd.show(v);
				} else if (type_end == 2) {
					mapOverlays.remove(endItem);
					et_end.setText("");
					mc.zoomOut();
					isEndItem = false;
				} else
					et_end.setText("");
			}
		});

		mGridStart
				.setOnQuickActionClickListener(new OnQuickActionClickListener() {

					@Override
					public void onQuickActionClicked(QuickActionWidget widget,
							int position) {
						// TODO Auto-generated method stub
						switch (position) {
						case 0:
							if (isStartItem == false) {
								mapOverlays.add(startItem);
								isStartItem = true;
							}
							Location mLocation = getLocation(MPP_UI.this);
							MY_LATITUDE = (int) (mLocation.getLatitude() * 1000000);
							MY_LONGITUDE = (int) (mLocation.getLongitude() * 1000000);

							mc.setCenter(new GeoPoint(MY_LATITUDE, MY_LONGITUDE));
							startItem.changePosition(MY_LATITUDE, MY_LONGITUDE);
							et_start.setText("我的位置");
							et_start.setTextColor(android.graphics.Color.BLUE);
							type_start = 2;
							mc.zoomIn();
							break;
						case 1:
							if (isStartItem == false) {
								mapOverlays.add(startItem);
								isStartItem = true;
							}
							et_start.setText("地圖上的點");
							et_start.setTextColor(android.graphics.Color.BLUE);
							startItem.changeToCenter();
							startItem.setMove(true);
							type_start = 2;
							mc.zoomIn();
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
					if (isEndItem == false) {
						mapOverlays.add(endItem);
						isEndItem = true;
					}
					Location mLocation = getLocation(MPP_UI.this);
					MY_LATITUDE = (int) (mLocation.getLatitude() * 1000000);
					MY_LONGITUDE = (int) (mLocation.getLongitude() * 1000000);

					mc.setCenter(new GeoPoint(MY_LATITUDE, MY_LONGITUDE));
					endItem.changePosition(MY_LATITUDE, MY_LONGITUDE);
					endItem.setMove(false);
					et_end.setText("我的位置");
					et_end.setTextColor(android.graphics.Color.BLUE);
					type_end = 2;
					mc.zoomIn();
					break;

				case 1:
					if (isEndItem == false) {
						mapOverlays.add(endItem);
						isEndItem = true;
					}
					endItem.changeToCenter();
					endItem.setMove(true);
					et_end.setText("地圖上的點");
					et_end.setTextColor(android.graphics.Color.BLUE);
					type_end = 2;
					mc.zoomIn();
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

		if (mLocation == null) {
			Toast.makeText(this, "location is null", Toast.LENGTH_LONG).show();
			return;
		}
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

		OverlayItem startOverlay = new OverlayItem(srcPoint, "", "");
		OverlayItem endOverlay = new OverlayItem(destPoint, "", "");

		Drawable marker_start = getResources().getDrawable(
				R.drawable.startpoint);
		Drawable marker_end = getResources().getDrawable(R.drawable.endpoint);

		startItem = new SitesOverlay(marker_start, startOverlay);
		endItem = new SitesOverlay(marker_end, endOverlay);

		mapOverlays.add(startItem);
		// mapOverlays.add(endItem);

		et_start.setText("我的位置");
		et_start.setTextColor(android.graphics.Color.BLUE);
		type_start = 2;
		layout_startend.setVisibility(View.VISIBLE);
	}

	
	private void setTaxiInfo(Context context){
		taxidata = new TaxiData(context);
		taxiInfoListView.setAdapter(
				new PhoneNumberAdapter(context, taxidata.getAllData() 
											,1 , new String[]{ } , new int[]{} ));
		
	}
	
	private void setResultText() {
		tv_routeNum.setText("路線 " + routeNow + " / " + routeNum);
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
					mapOverlays.add(new MyOverlay(lastPoints, Points.get(i), 3,
							Color.CYAN));
					lastPoints = Points.get(i);
				}
			}
		}
		mapOverlays.remove(startItem);
		mapOverlays.remove(endItem);
		mapOverlays.add(startItem);
		mapOverlays.add(endItem);
	}

	private void selMyPath(int n) {
		int sel = n - 1;
		for (int j = 0; j < routeNum; j++) {
			if (routeInfo.get("POLYLINE" + j).toString().length() > 0
					&& j != sel) {
				List<GeoPoint> Points = new ArrayList<GeoPoint>();
				Points = decodePolylines(routeInfo.get("POLYLINE" + j)
						.toString());
				GeoPoint lastPoints = Points.get(0);
				for (int i = 1; i < Points.size(); i++) {
					mapOverlays.add(new MyOverlay(lastPoints, Points.get(i), 3,
							this.getResources().getColor(R.color.myCyan)));
					lastPoints = Points.get(i);
				}
			}
		}

		if (routeInfo.get("POLYLINE" + sel).toString().length() > 0) {
			List<GeoPoint> Points = new ArrayList<GeoPoint>();
			Points = decodePolylines(routeInfo.get("POLYLINE" + sel).toString());
			GeoPoint lastPoints = Points.get(0);
			for (int i = 1; i < Points.size(); i++) {
				mapOverlays.add(new MyOverlay(lastPoints, Points.get(i), 4,
						this.getResources().getColor(R.color.dark_blue)));
				lastPoints = Points.get(i);
			}
		}

		mapOverlays.add(startItem);
		mapOverlays.add(endItem);
		mc.setZoom(mapView.getZoomLevel());

	}

	private Boolean getGeo(String searchText) throws JSONException, IOException {
		String text[] = searchText.split(" ");
		String urlText = "";
		for (int i = 0; i < text.length; i++) {
			if (i != 0)
				urlText = urlText + "+";
			urlText = urlText + text[i];
		}
		String urlstring = "http://maps.google.com/maps/api/geocode/json?address="
				+ URLEncoder.encode(urlText, "UTF-8")
				+ "&bounds=20.5637908,116.7118602|26.3873532,122.006905&sensor=true&language=zh-TW";
		Log.d(getPackageName(), urlstring);

		JSONObject geo = new JSONObject(readStrFromUrl(urlstring));

		if (geo.getString("status").equals("OK")) {
			String address = geo.getJSONArray("results").getJSONObject(0)
					.getString("formatted_address");
			int lat = (int) ((geo.getJSONArray("results").getJSONObject(0)
					.getJSONObject("geometry").getJSONObject("location")
					.getDouble("lat")) * 1000000);
			int lng = (int) ((geo.getJSONArray("results").getJSONObject(0)
					.getJSONObject("geometry").getJSONObject("location")
					.getDouble("lng")) * 1000000);
			Log.d(getPackageName(), lat + " " + lng);

			if (lat >= 20563790 && lat <= 26387364 && lng >= 116711860
					&& lng <= 122006905) {
				searchNum++;
				searchInfo.put("ADDRESS" + searchNum, address);
				searchInfo.put("LAT" + searchNum, lat);
				searchInfo.put("LNG" + searchNum, lng);
			}
			return true;
		}
		return false;
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
			// http://maps.google.com/maps/api/directions/json?origin=25.018641,121.542526&destination=25.039717,121.528863&sensor=true&alternatives=true&language=zh-TW
			Log.d(getPackageName(), "yaaaaaaaaaaaaaa  " + urlstring);

			info = new JSONObject(readStrFromUrl(urlstring));
			Log.d(getPackageName(), readStrFromUrl(urlstring));

			routeNum = info.getJSONArray("routes").length();

			for (int i = 0; i < routeNum; i++) {
				// tv_copyrights.setText(info.getJSONArray("routes").getJSONObject(i).getString("copyrights"));
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
		// mGridStart.addQuickAction(new MyQuickAction(this,
		// R.drawable.gd_action_bar_search, R.string.gd_mySearch));
		// mGridStart.addQuickAction(new MyQuickAction(this,
		// R.drawable.gd_action_bar_share, R.string.gd_favorite));

		// mGrid.addQuickAction(new MyQuickAction(this,
		// R.drawable.gd_action_bar_edit, R.string.gd_edit));

		mGridEnd = new QuickActionBar(this);
		// mGrid.addQuickAction(new MyQuickAction(this,
		// R.drawable.gd_action_bar_compose, R.string.gd_pin));
		mGridEnd.addQuickAction(new MyQuickAction(this,
				R.drawable.gd_action_bar_locate, R.string.gd_mylocation));
		mGridEnd.addQuickAction(new MyQuickAction(this,
				R.drawable.gd_action_bar_export, R.string.gd_pin));
		// mGridEnd.addQuickAction(new MyQuickAction(this,
		// R.drawable.gd_action_bar_search, R.string.gd_mySearch));
		// mGridEnd.addQuickAction(new MyQuickAction(this,
		// R.drawable.gd_action_bar_share, R.string.gd_favorite));

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
		private Boolean isMove = false;

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

			if (action == MotionEvent.ACTION_DOWN && isMove == true) {
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
			final GeoPoint pt = new GeoPoint(x, y);

			Log.d(getPackageName(),
					"lat " + pt.getLatitudeE6() + " long "
							+ pt.getLongitudeE6());

			OverlayItem toDrop = new OverlayItem(pt, "", "");

			items.add(toDrop);
			populate();

			inDrag = null;
			// result = true;
		}

		public void setMove(Boolean b) {
			isMove = b;
		}

		public void changeToCenter() {
			for (OverlayItem item : items) {
				Point p = new Point(0, 0);

				mapView.getProjection().toPixels(item.getPoint(), p);
				inDrag = item;
				items.remove(inDrag);
				populate();
			}

			Log.d(getPackageName(), "lat "
					+ mapView.getMapCenter().getLatitudeE6() + " long "
					+ mapView.getMapCenter().getLongitudeE6());

			OverlayItem toDrop = new OverlayItem(mapView.getMapCenter(), "", "");

			items.add(toDrop);
			populate();

			inDrag = null;
		}

		public int getLat() {
			GeoPoint tmp = new GeoPoint(0, 0);
			for (OverlayItem item : items) {
				tmp = item.getPoint();
			}
			return tmp.getLatitudeE6();
		}

		public int getLon() {
			GeoPoint tmp = new GeoPoint(0, 0);
			for (OverlayItem item : items) {
				tmp = item.getPoint();
			}
			return tmp.getLongitudeE6();
		}

		public void doPopulate() {
			populate();
		}

	}

}