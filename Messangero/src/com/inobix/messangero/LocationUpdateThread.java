package com.inobix.messangero;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.location.LocationClient;
import com.ianywhere.ultralitejni12.Connection;
import com.ianywhere.ultralitejni12.PreparedStatement;
import com.ianywhere.ultralitejni12.ULjException;
import com.inobix.messangero.common.app_settings;

import DAL.DBUtil;
import android.content.Context;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

public class LocationUpdateThread implements Runnable {
	Context currContext;
	Location location;

	public LocationUpdateThread(Context ctx, Location loc) {
		currContext = ctx;
		location = loc;
	}

	public void run() {
		SaveLocation(location);
	}

	private void SaveLocation(Location location) {
		Log.d("Messangero", "Save Location");
		int driverID = PreferencesUtil.GetDriverID(currContext);
		if (driverID == 0) {
			driverID = app_settings.CurrentDriverID;
			PreferencesUtil.SetDriverID(currContext, driverID);
		}

		if (location == null) {

			return;
		}
		Date currentDate = new Date();
		if (PreferencesUtil.Latitude == location.getLatitude()
				&& PreferencesUtil.Longitude == location.getLongitude())
			return;
		Log.d("Messangero", "Lat: " + location.getLatitude() + " Lng: " + location.getLongitude());
		
		try {
			Connection conn = DBUtil.CreateConnection(currContext);
			if (conn == null)
				return;
			try {				
				
				Location locationA = new Location("point A");
				locationA.setLatitude(PreferencesUtil.Latitude);
				locationA.setLongitude(PreferencesUtil.Longitude); 
				float d = location.distanceTo(locationA);
				float speed = 0;
				if(d < 100)
				{
					long diffInMs = currentDate.getTime() - PreferencesUtil.LastLocationCheck.getTime();
					long diffInSec = TimeUnit.MILLISECONDS.toSeconds(diffInMs);
					speed = d / diffInSec * 3600 / 1000;
					if(speed > 250)
						speed = 0;
				}
				//float speed = location.getSpeed();
				//speed = speed * 3600 / 1000;
				//if(speed > 250)
				//	speed = 0;
				
				StringBuffer sb = new StringBuffer(
						"INSERT INTO CourierDriverGPSLog1 (LocationTime, CourierDriverID, Latitude, Longitude, Speed, Activity) "
								+ " VALUES ( GetDate(), ?, ?, ?, ?, ?)");
				PreparedStatement ps;

				ps = conn.prepareStatement(sb.toString());

				// driver id
				ps.set(1, driverID);
				// latitude
				ps.set(2, location.getLatitude());
				// longitude
				ps.set(3, location.getLongitude());
				// speed
				ps.set(4, speed);
				// actvity  - walking, car driving etc..
				ps.set(5, PreferencesUtil.CurrentUserActivity);

				ps.execute();				
				conn.commit();
				ps.close();

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Crashlytics.logException(e);
			} finally {
				if (conn != null)
					conn.release();
			}

			PreferencesUtil.Latitude = location.getLatitude();
			PreferencesUtil.Longitude = location.getLongitude();

			PreferencesUtil.LastLocationCheck = currentDate;
			
			//Looper.prepare();
			DBUtil.SyncGPS(currContext);
			//SyncGPS gps = new SyncGPS();
			//gps.execute();
			//Looper.loop();
			/*
			 * Thread t = new Thread(new Runnable() {
			 * 
			 * public void run() { if (IsSycnGPSRunning == true) {
			 * IsSycnGPSRunning = true; DBUtil.SyncGPS(currContext);
			 * IsSycnGPSRunning = false; } } }); t.run();
			 */

		} catch (ULjException ex) {
			ex.printStackTrace();
			//Crashlytics.logException(ex);
		}
	}

	public class SyncGPS extends AsyncTask<Void, Void, Void> {

		/**
		 * The system calls this to perform work in the UI thread and delivers
		 * the result from doInBackground()
		 */
		@Override
		protected void onPostExecute(Void result) {

		};

		@Override
		protected Void doInBackground(Void... params) {
			DBUtil.SyncGPS(currContext);
			return null;
		}
	}

	static boolean IsSycnGPSRunning = false;
}
