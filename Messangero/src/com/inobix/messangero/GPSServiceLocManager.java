package com.inobix.messangero;

import java.util.Timer;
import java.util.TimerTask;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;

import android.app.AlertDialog;
import android.app.IntentService;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Looper;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

public class GPSServiceLocManager extends Service implements
		android.location.LocationListener, android.location.GpsStatus.Listener {

	private final Context mContext;

	// flag for GPS status
	boolean isGPSEnabled = false;

	// flag for network status
	boolean isNetworkEnabled = false;

	// flag for GPS status
	boolean canGetLocation = false;

	Location location; // location
	double latitude; // latitude
	double longitude; // longitude
	
	private Location lastNetworkLocation;
	private Location lastGPSLocation;
	private int lastNetworkLocationTime;
	private int lastGPSLocationTime;

	// The minimum distance to change Updates in meters
	private static long MIN_DISTANCE_CHANGE_FOR_UPDATES = 0; // 10 meters

	// The minimum time between updates in milliseconds
	private static long MIN_TIME_BW_UPDATES = 1000 * 5 * 1; // 15 sec

	// Declaring a Location Manager
	protected LocationManager locationManager;

	public GPSServiceLocManager() {
		this.mContext = this;
	}

	public GPSServiceLocManager(Context context) {
		this.mContext = context;
		getLocation();
	}

	long mLastLocationMillis = 0;
	Location mLastLocation = null;
	boolean isGPSFix = false;
	
	Timer timerHeartBeat = null;
	public void onCreate() {
		super.onCreate();
		InitGPService();
		
		timerHeartBeat = new Timer();
		timerHeartBeat.schedule(new TimerTask() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				long dif = SystemClock.elapsedRealtime() - mLastLocationMillis;
				Log.d("Messangero","time between sync: " + dif);
				//check each 1 min if gps service has stopped
				//and restart it
				if(dif > 5 * 60 * 1000)
				{
						//restart gps service
					//Toast.makeText(getApplicationContext(), "Restart GPS",
							//Toast.LENGTH_SHORT).show();
					Log.d("Messangero","GPS Restarted");
					Looper.prepare();
					Looper.loop();
					RestartGPS();
					mLastLocationMillis = SystemClock.elapsedRealtime();
				}
			}
		}, 1000 * 60, 1000 * 60);
		
	};

	public void InitGPService() {
		try {
			
			
			
			locationManager = (LocationManager) mContext
					.getSystemService(LOCATION_SERVICE);

			// getting GPS status
			isGPSEnabled = locationManager
					.isProviderEnabled(LocationManager.GPS_PROVIDER);

			// getting network status
			isNetworkEnabled = locationManager
					.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

			if (!isGPSEnabled && !isNetworkEnabled) {
				// no network provider is enabled
			} else {
				this.canGetLocation = true;
				// First get location from Network Provider
				if (isNetworkEnabled) {
					locationManager.requestLocationUpdates(
							LocationManager.NETWORK_PROVIDER,
							MIN_TIME_BW_UPDATES,
							MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
				}
				// if GPS Enabled get lat/long using GPS Services
				if (isGPSEnabled) {
					if (location == null) {
						locationManager.requestLocationUpdates(
								LocationManager.GPS_PROVIDER,
								MIN_TIME_BW_UPDATES,
								MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
					}
				}

				if (locationManager != null)
					SaveLocation(locationManager
							.getLastKnownLocation(LocationManager.GPS_PROVIDER));
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public Location getLocation() {
		try {
			locationManager = (LocationManager) mContext
					.getSystemService(LOCATION_SERVICE);

			// getting GPS status
			isGPSEnabled = locationManager
					.isProviderEnabled(LocationManager.GPS_PROVIDER);

			// getting network status
			isNetworkEnabled = locationManager
					.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

			if (!isGPSEnabled && !isNetworkEnabled) {
				// no network provider is enabled
			} else {
				this.canGetLocation = true;
				// First get location from Network Provider
				if (isNetworkEnabled) {
					locationManager.requestLocationUpdates(
							LocationManager.NETWORK_PROVIDER,
							MIN_TIME_BW_UPDATES,
							MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
					Log.d("Network", "Network");
					if (locationManager != null) {
						location = locationManager
								.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
						if (location != null) {
							latitude = location.getLatitude();
							longitude = location.getLongitude();
						}
					}
				}
				// if GPS Enabled get lat/long using GPS Services
				if (isGPSEnabled) {
					if (location == null) {
						locationManager.requestLocationUpdates(
								LocationManager.GPS_PROVIDER,
								MIN_TIME_BW_UPDATES,
								MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
						Log.d("GPS Enabled", "GPS Enabled");
						if (locationManager != null) {
							location = locationManager
									.getLastKnownLocation(LocationManager.GPS_PROVIDER);
							if (location != null) {
								latitude = location.getLatitude();
								longitude = location.getLongitude();
							}
						}
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return location;
	}

	/**
	 * Stop using GPS listener Calling this function will stop using GPS in your
	 * app
	 * */
	public void stopUsingGPS() {
		if (locationManager != null) {
			locationManager.removeUpdates(GPSServiceLocManager.this);
		}
	}

	/**
	 * Function to get latitude
	 * */
	public double getLatitude() {
		if (location != null) {
			latitude = location.getLatitude();
		}

		// return latitude
		return latitude;
	}

	/**
	 * Function to get longitude
	 * */
	public double getLongitude() {
		if (location != null) {
			longitude = location.getLongitude();
		}

		// return longitude
		return longitude;
	}

	/**
	 * Function to check GPS/wifi enabled
	 * 
	 * @return boolean
	 * */
	public boolean canGetLocation() {
		return this.canGetLocation;
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	public void onConnectionFailed(ConnectionResult arg0) {
		RestartGPS();
	}

	private void RestartGPS() {
		stopUsingGPS();
		InitGPService();
	}

	public void onConnected(Bundle arg0) {
		// TODO Auto-generated method stub

	}

	public void onDisconnected() {
		// TODO Auto-generated method stub
		RestartGPS();
	}

	long prevLocUpdate;
	public static final int GPSProvider = 0;
	public static final int NetworkProvider = 1;
	int CurrentProvider = GPSProvider; 
	public void onLocationChanged(Location location) {

		
		mLastLocationMillis = SystemClock.elapsedRealtime();
		
		if(CurrentProvider == GPSProvider)
		{	
			if (location.getProvider().equalsIgnoreCase("GPS") && location.getAccuracy() < 25)
			{	
				LogLoc(location,true);
				SaveLocation(location);
			}
			else
			{
				CurrentProvider = NetworkProvider;
				Toast.makeText(getApplicationContext(), "Change provider to Network", Toast.LENGTH_SHORT).show();				
			}
		}
		else
			if(CurrentProvider == NetworkProvider)
			{
				if (location.getProvider().equalsIgnoreCase("network") && location.getAccuracy() < 25)
				{
					LogLoc(location,true);
					SaveLocation(location);
				}
				else
				{
					CurrentProvider = GPSProvider;
					Toast.makeText(getApplicationContext(), "Change provider to GPS", Toast.LENGTH_SHORT).show();
				}	
			}
		
		/*if (location.getProvider().equalsIgnoreCase("GPS") && location.getAccuracy() < 20) 
		{
			mLastLocationMillis = SystemClock.elapsedRealtime();
			SaveLocation(location);
		}
		else
		if(SystemClock.elapsedRealtime() - mLastLocationMillis < 60 * 1000 
				&& location.getProvider().equalsIgnoreCase("network") && location.getAccuracy() < 50)
		{
			SaveLocation(location);
		}*/
	}
	
	private void LogLoc(Location location, boolean save)
	{
		float speed = location.getSpeed();
		speed = speed * 3600 / 1000;
		String saveString = save ? "Save" : "No Save";
		Toast.makeText(
				getApplicationContext(),
				saveString + "Location: provider:" + location.getProvider() + " speed: "
						+ speed + " accuracy: "
						+ location.getAccuracy() + " lat: "
						+ location.getLatitude() + " lng: "
						+ location.getLongitude(), Toast.LENGTH_SHORT).show();

		Log.d("Messangero",
				saveString + "Location: provider:" + location.getProvider() + " speed: "
						+ speed + " accuracy: "
						+ location.getAccuracy() + " lat: "
						+ location.getLatitude() + " lng: "
						+ location.getLongitude());
	}

	private void SaveLocation(Location location) {
		if (location == null) {
			Toast.makeText(getApplicationContext(), "Location null",
					Toast.LENGTH_SHORT).show();
			return;
		}

		float speed = location.getSpeed();
		speed = speed * 3600 / 1000;
		
		/*Toast.makeText(
				getApplicationContext(),
				"Location: provider:" + location.getProvider() + " speed: "
						+ speed + " accuracy: "
						+ location.getAccuracy() + " lat: "
						+ location.getLatitude() + " lng: "
						+ location.getLongitude(), Toast.LENGTH_SHORT).show();

		Log.d("Messangero",
				"Location: provider:" + location.getProvider() + " speed: "
						+ speed + " accuracy: "
						+ location.getAccuracy() + " lat: "
						+ location.getLatitude() + " lng: "
						+ location.getLongitude());*/

		if (location.getAccuracy() > 200)
			return;

		prevLocUpdate = SystemClock.elapsedRealtime();
		Thread thr = new Thread(new LocationUpdateThread(
				GPSServiceLocManager.this, location));
		thr.start();
	}

	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub

	}

	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub

	}

	public void onStatusChanged(String provider, int status, Bundle extras) {
		Log.d("Messangero", "provider: " + provider + " status: " + status);
		Toast.makeText(getApplicationContext(),
				"provider: " + provider + " status: " + status,
				Toast.LENGTH_SHORT).show();
	}

	

	public void onGpsStatusChanged(int event) {
		// TODO Auto-generated method stub
		switch (event) {
		case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
			if (mLastLocation != null)
				isGPSFix = (SystemClock.elapsedRealtime() - mLastLocationMillis) < 3000;
				//isGPSFix = (SystemClock.elapsedRealtime() - mLastLocationMillis) < 3000;

			if (isGPSFix) {
				// A fix has been acquired. gps signal

			} else {
				// The fix has been lost. no gps signal

			}

			break;
		case GpsStatus.GPS_EVENT_FIRST_FIX:
			// Do something.
			isGPSFix = true;

			break;
		}
	}

}
