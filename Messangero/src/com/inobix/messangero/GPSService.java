package com.inobix.messangero;

import java.util.Timer;
import java.util.TimerTask;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.location.ActivityRecognitionClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;

import android.app.IntentService;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Looper;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

public class GPSService extends Service implements LocationListener,
		ConnectionCallbacks, OnConnectionFailedListener {

	private LocationClient locationclient;
	private LocationRequest locationrequest;
	private ActivityRecognitionClient mActivityRecognitionClient;
	PendingIntent mActivityRecognitionPendingIntent;

	public static final int MILLISECONDS_PER_SECOND = 1000;
	public static final int DETECTION_INTERVAL_SECONDS = 5;
	public static final int DETECTION_INTERVAL_MILLISECONDS = MILLISECONDS_PER_SECOND
			* DETECTION_INTERVAL_SECONDS;

	public GPSService() {
		// super("GPSService");
		super();
		// TODO Auto-generated constructor stub
	}

	long lastLocationTime;
	Timer timerHeartBeat;
	Timer timerActivityCheck;

	public void InitGpsService() {
		if (locationclient != null && locationclient.isConnected()) {
			return;
		}
		int resp = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
		if (resp == ConnectionResult.SUCCESS) {
			locationclient = new LocationClient(this, this, this);
			locationclient.connect();

			Log.d("Messangero", "Location Client Connect");

		} else {
			//Toast.makeText(this, "Google Play Service Error " + resp,
					//Toast.LENGTH_LONG).show();
		}
	}

	// Binder given to clients
	private final IBinder mBinder = new LocalBinder();

	public class LocalBinder extends Binder {
		GPSService getService() {
			// Return this instance of LocalService so clients can call public
			// methods
			return GPSService.this;
		}
	}

	public IBinder onBind(Intent arg0) {
		return mBinder;
	}

	private void InitActivityRecognition() {
		mActivityRecognitionClient = new ActivityRecognitionClient(this, this,
				this);
		mActivityRecognitionClient.connect();

		Intent intent = new Intent(this.getApplicationContext(),
				ActivityRecognitionIntentService.class);
		/*
		 * Return a PendingIntent that starts the IntentService.
		 */
		mActivityRecognitionPendingIntent = PendingIntent.getService(
				this.getApplicationContext(), 0, intent,
				PendingIntent.FLAG_UPDATE_CURRENT);

	}

	public void onCreate() {
		super.onCreate();

		InitGpsService();
		InitActivityRecognition();

		timerHeartBeat = new Timer();
		timerHeartBeat.schedule(new TimerTask() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				Looper.prepare();
				Looper.loop();
				RestartGPSService();
			}
		}, 5 * 60 * 1000, 5 * 60 * 1000);

		timerActivityCheck = new Timer();
		timerActivityCheck.schedule(new TimerTask() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				Log.d("Messangero", "Activity check type= "
						+ PreferencesUtil.CurrentUserActivity + " count= "
						+ PreferencesUtil.CurrentUserActivityCount);

				if (PreferencesUtil.CurrentUserActivity == 3
						&& PreferencesUtil.CurrentUserActivityCount > 8) {
					// stop listeners
					if (locationclient != null && locationclient.isConnected())
						StopLocListeners();
				} else {
					if (locationclient != null && !locationclient.isConnected()) {
						Log.d("Messangero", "Timer activity restart gps");
						// Looper.prepare();
						// Looper.loop();
						RestartGPSService();
					}
				}
			}
		}, 20 * 1000, 20 * 1000);
	};

	private void RestartGPSService() {
		// long dif = SystemClock.elapsedRealtime() - lastLocationTime;
		// Log.d("Messangero", "time between sync: " + dif);
		// if (dif > 60000)
		{
			// restart gps service
			if (locationclient.isConnected()
					&& locationclient
							.isConnectionCallbacksRegistered(GPSService.this)) {
				locationclient.removeLocationUpdates(GPSService.this);
				locationclient.disconnect();
			}
			locationclient = new LocationClient(GPSService.this,
					GPSService.this, GPSService.this);
			locationclient.connect();
			lastLocationTime = SystemClock.elapsedRealtime();

			// Looper.prepare();
			// Looper.loop();
			// Toast.makeText(getApplicationContext(),
			// "Location client restarted", Toast.LENGTH_SHORT).show();
			//Log.d("Messangero", "Location service restarted");
		}
	}

	private void StopLocListeners() {
		Log.d("Messangero", "Stop loc listeners");
		locationclient.removeLocationUpdates(GPSService.this);
		locationclient.disconnect();
		//

	}

	boolean isDestroyed = false;

	public void onDestroy() {
		super.onDestroy();
		isDestroyed = true;
		if (locationclient != null && locationclient.isConnected()) {
			locationclient.removeLocationUpdates(this);
			locationclient.disconnect();			
			Log.d("Messangero", "GPSService Destroy");
		}
		timerHeartBeat.cancel();
		timerActivityCheck.cancel();
		if (mActivityRecognitionClient.isConnected())
			mActivityRecognitionClient.disconnect();
	}

	public void onConnectionFailed(ConnectionResult arg0) {
		// TODO Auto-generated method stub

	}

	public void onConnected(Bundle arg0) {
		// TODO Auto-generated method stub
		PreferencesUtil.LoadSettings(this);

		if (locationclient.isConnected()) {
			locationrequest = new LocationRequest();
			locationrequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
			locationrequest.setInterval(PreferencesUtil.GPSSyncPeriod);
			locationclient.requestLocationUpdates(locationrequest, this);
		}

		if (mActivityRecognitionClient.isConnected()) {
			mActivityRecognitionClient.requestActivityUpdates(
					DETECTION_INTERVAL_MILLISECONDS,
					mActivityRecognitionPendingIntent);
		}

	}

	public void onDisconnected() {
		// TODO Auto-generated method stub
		try {
			if (isDestroyed)
				return;
			if (locationclient != null && locationclient.isConnected())
				locationclient.removeLocationUpdates(this);

			if (mActivityRecognitionClient.isConnected())
				mActivityRecognitionClient.disconnect();
		} catch (Exception ex) {

		}
	}

	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		//LogLoc(location, true);
		lastLocationTime = SystemClock.elapsedRealtime();

		if (location.getAccuracy() < 50) {
			Thread thr = new Thread(new LocationUpdateThread(GPSService.this,
					location));
			thr.start();
		}
	}

	private void LogLoc(Location location, boolean save) {
		float speed = location.getSpeed();
		speed = speed * 3600 / 1000;
		String saveString = save ? "Save" : "No Save";
		Toast.makeText(
				getApplicationContext(),
				saveString + "Location: provider:" + location.getProvider()
						+ " speed: " + speed + " accuracy: "
						+ location.getAccuracy() + " lat: "
						+ location.getLatitude() + " lng: "
						+ location.getLongitude(), Toast.LENGTH_SHORT).show();

		Log.d("Messangero",
				saveString + "Location: provider:" + location.getProvider()
						+ " speed: " + speed + " accuracy: "
						+ location.getAccuracy() + " lat: "
						+ location.getLatitude() + " lng: "
						+ location.getLongitude() + " act:"
						+ PreferencesUtil.CurrentUserActivity);
	}

	// @Override
	// protected void onHandleIntent(Intent intent) {
	// TODO Auto-generated method stub
	// InitGpsService();
	// Log.d("Messangero", "GPS Service started");
	// }

}
