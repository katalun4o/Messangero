package com.inobix.messangero;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.inobix.messangero.map.MapHelper;
import com.inobix.messangero.map.MyOverlayItem;
import com.loopj.android.http.AsyncHttpResponseHandler;

import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class PlaceLocation extends FragmentActivity {

	Button btnSearchAddress;
	EditText tbAddress;
	MapView mapView;
	GoogleMap gMap;
	MapHelper mapHelper;
	SupportMapFragment mapFragment;

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.place_location);

		// mapView = (MapView) findViewById(R.id.mapview);

		FragmentManager fm = getSupportFragmentManager();
		Fragment fMap = fm.findFragmentByTag("MapDialogFragment");
		if (fMap != null) {
			mapFragment = (SupportMapFragment) fMap;
		} else {
			mapFragment = (SupportMapFragment) fm.findFragmentById(R.id.map);
			if (mapFragment == null) {
				mapFragment = SupportMapFragment.newInstance();
				FragmentTransaction ft = fm.beginTransaction();
				ft.add(R.id.map, mapFragment, "MapDialogFragment");
				ft.commit();
				// ft.commit();
				// fm.executePendingTransactions();
			}
		}

		Handler h = new Handler();
		h.postDelayed(new Runnable() {
			public void run() {
				Bundle Extras = getIntent().getExtras();
				if (savedInstanceState != null)
					Extras = savedInstanceState;
				LoadMap(Extras);
			}
		}, 200);
	}

	String name = "";
	String categName = "";

	private void LoadMap(Bundle extras) {
		gMap = mapFragment.getMap();
		mapHelper = new MapHelper(this, gMap, this, true);
		btnSearchAddress = (Button) findViewById(R.id.btnSearchAddress);
		tbAddress = (EditText) findViewById(R.id.tbAddress);

		double lat = extras.getDouble("PlaceLat");
		double lng = extras.getDouble("PlaceLng");
		int categID = extras.getInt("PlaceCategoryID");
		name = extras.getString("PlaceName");
		categName = extras.getString("PlaceCategoryName");
		int rating = extras.getInt("Rating");

		tbAddress.setText(categName);
		
		btnSearchAddress.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				LoadNewAddress();
			}
		});

		mapHelper.AddMarker(PreferencesUtil.Latitude,
				PreferencesUtil.Longitude, "MyLocation", "",
				R.drawable.truck_32_1, 0, 0, 0, "", false);

		// MyOverlayItem marker = mapHelper.AddMarker(lat, lng,
		// R.drawable.envelope_32, name, "This is " + name, 0, rating,
		// categName);

		mapHelper.AddMarker(lat, lng, name, categName, R.drawable.envelope_32,
				0, rating, 0, "", false);

		// mapHelper.AddMarker(lat, lng, R.drawable.envelope_32, name,
		// "This is "
		// + name, 0, rating, 0, categName);

		// marker.IsTapped = true;
		// mapHelper.SetCurrentMarker(marker);
		mapHelper.CenterMap(lat, lng);

		WebServiceHelper.GetRout(this, lat, lng,
				new AsyncHttpResponseHandler() {
					@Override
					public void onSuccess(String result) {
						HandleRouteResult(result);
					}

					@Override
					public void onFailure(Throwable arg0) {
						// TODO Auto-generated method stub
						super.onFailure(arg0);
					}
				});

	}

	private double newAddresLat = 0;
	private double newAddresLng = 0;

	private void LoadNewAddress() {
		String newAddress = tbAddress.getText().toString();
		WebServiceHelper.GetRout(this, newAddress,
				new AsyncHttpResponseHandler() {
					@Override
					public void onSuccess(String result) {
						mapHelper.ClearMarkers();
						HandleRouteResult(result);
						mapHelper.AddMarker(PreferencesUtil.Latitude,
								PreferencesUtil.Longitude, "MyLocation", "",
								R.drawable.truck_32_1, 0, 0, 0, "", false);
						mapHelper.AddMarker(newAddresLat, newAddresLng,
								name, categName, R.drawable.envelope_32, 0, 0,
								0, "", false);
					}

					@Override
					public void onFailure(Throwable arg0) {
						// TODO Auto-generated method stub
						super.onFailure(arg0);
					}
				});
	}

	private void HandleRouteResult(String result) {
		try {
			double maxLatitude = 0;
			double minLatitude = Double.MAX_VALUE;
			double maxLongitude = 0;
			double minLongitude = Double.MAX_VALUE;

			JSONObject json = new JSONObject(result);

			final JSONObject jsonRoute = json.getJSONArray("routes")
					.getJSONObject(0);
			// Get the leg, only one leg as we don't support waypoints
			final JSONObject leg = jsonRoute.getJSONArray("legs")
					.getJSONObject(0);
			// Get the steps for this leg
			final JSONArray steps = leg.getJSONArray("steps");

			ArrayList<LatLng> geoPoints = new ArrayList<LatLng>();

			for (int i = 0; i < steps.length(); i++) {
				JSONObject step = steps.getJSONObject(i);

				JSONObject stringPoliline = step.getJSONObject("polyline");
				String polylinePoints = stringPoliline.getString("points");

				JSONObject start = step.getJSONObject("start_location");

				LatLng position = new LatLng(start.getDouble("lat"),
						start.getDouble("lng"));
				// geoPoints.add(position);

				List<LatLng> polis = mapHelper.decodePoly(polylinePoints);

				for (LatLng position1 : polis) {
					geoPoints.add(position1);

					maxLatitude = Math.max(position1.latitude, maxLatitude);
					minLatitude = Math.min(position1.latitude, minLatitude);
					maxLongitude = Math.max(position1.longitude, maxLongitude);
					minLongitude = Math.min(position1.longitude, minLongitude);

				}
			}

			// if (geoPoints.size() > 0) {
			// mapHelper.AddRout(geoPoints);
			// }

			/*
			 * double avgLat = (maxLatitude + minLatitude) / 2; double avgLng =
			 * (maxLongitude + minLongitude) / 2; GeoPoint gp = new
			 * GeoPoint((int) avgLat, (int) avgLng);
			 * mapHelper.ZoomMap(maxLatitude, maxLongitude, minLatitude,
			 * minLongitude); mapHelper.CenterMap(gp);
			 */

			if (geoPoints.size() > 0) {
				LatLng lastLatLng = geoPoints.get(geoPoints.size() - 1);
				newAddresLat = lastLatLng.latitude;
				newAddresLng = lastLatLng.longitude;
			}

			CameraUpdate updateZoom = CameraUpdateFactory.newLatLngBounds(
					new LatLngBounds(new LatLng(minLatitude, minLongitude),
							new LatLng(maxLatitude, maxLongitude)), 20);
			gMap.moveCamera(updateZoom);

			if (geoPoints.size() > 0) {
				mapHelper.AddRout1(geoPoints);
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
