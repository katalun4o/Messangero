package com.inobix.messangero;

import java.util.Calendar;
import java.util.Date;
import java.util.TimerTask;
import java.util.concurrent.ScheduledFuture;

import com.ianywhere.ultralitejni12.ConfigFileAndroid;
import com.ianywhere.ultralitejni12.Connection;
import com.ianywhere.ultralitejni12.DatabaseManager;
import com.ianywhere.ultralitejni12.PreparedStatement;
import com.ianywhere.ultralitejni12.ResultSet;
import com.ianywhere.ultralitejni12.StreamHTTPParms;
import com.ianywhere.ultralitejni12.SyncParms;
import com.ianywhere.ultralitejni12.ULjException;
import com.inobix.messangero.common.app_settings;

import DAL.DBUtil;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class LocalService extends Service {
	// private static Timer timer;
	ScheduledFuture<?> beeperHandle = null;

	// Binder given to clients
	private final IBinder mBinder = new LocalBinder();

	public class LocalBinder extends Binder {
		LocalService getService() {
			// Return this instance of LocalService so clients can call public
			// methods
			return LocalService.this;
		}
	}

	public IBinder onBind(Intent arg0) {
		return mBinder;
	}

	// private final ScheduledExecutorService scheduler = Executors
	// .newScheduledThreadPool(3);

	public void onCreate() {
		super.onCreate();

		Thread thr = new Thread(null, mTask, "LocalService");
		thr.start();

	}

	Runnable mTask = new Runnable() {
		public void run() {
			DBUtil.SyncVouchers(LocalService.this);
			//DBUtil.SyncGPS(LocalService.this);
			LoadVouchersCount(LocalService.this);
			// Done with our work... stop the service!
			LocalService.this.stopSelf();
		}
	};

	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);

	};

	int driverID;
	private void LoadVouchersCount(Context ctx) {
		// int vouchersCount = 0;
		int ordersCount = 0;
		
		driverID = PreferencesUtil.GetDriverID(ctx);
		if(driverID == 0)
		{
			driverID = app_settings.CurrentDriverID;
			PreferencesUtil.SetDriverID(ctx, driverID);
		}
		// loads the count of vouchers left to deliver
		// and shows them in menu next to caption
		try {
			// dbUtil = new DBUtil(this);
			// _conn = dbUtil.get_conn();
			Connection _conn = DBUtil.CreateConnection(ctx);

			Calendar calendar = Calendar.getInstance();
			calendar.set(Calendar.HOUR_OF_DAY, 0);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
			calendar.set(Calendar.MILLISECOND, 0);

			Date currentSearchDate = calendar.getTime();

			// select all orders for today command
			StringBuffer sb = new StringBuffer(
					"SELECT "
							+ "COUNT(*) "
							+ " FROM RemoteCourier WHERE DriverID = ? AND DocType = '5' AND \"TransDate\" >= ? AND \"TransDate\" < ? ");

			Date dateFrom = currentSearchDate;
			Date dateTo = Base.addDay(currentSearchDate, 1);

			PreparedStatement ps = _conn.prepareStatement(sb.toString());

			// driver id
			ps.set(1, driverID);
			// start date
			ps.set(2, dateFrom);
			// end date
			ps.set(3, dateTo);

			ResultSet rs = ps.executeQuery();

			// reads vouchers into array
			if (rs.next()) {
				ordersCount = rs.getInt(1);
			}
			rs.close();
			ps.close();
			_conn.release();

			if (DBUtil.LastOrdersCount != ordersCount) {
				if (DBUtil.LastOrdersCount != -1)
					RaiseNotification(ordersCount - DBUtil.LastOrdersCount);
				DBUtil.LastOrdersCount = ordersCount;
			}
		} catch (Exception ex) {
			// tbMessage.setText(ex.getMessage());
			ex.printStackTrace();
		}

	}

	public static final int Sync_Notification_ID = 1;

	private void RaiseNotification(int newOrdersCount) {
		String ns = Context.NOTIFICATION_SERVICE;
		NotificationManager mNotificationManager = (NotificationManager) getSystemService(ns);

		int icon = R.drawable.maildeliveryicon;
		CharSequence tickerText = "New Order";
		long when = System.currentTimeMillis();

		Notification notification = new Notification(icon, tickerText, when);
		notification.defaults |= Notification.DEFAULT_SOUND;
		notification.defaults |= Notification.DEFAULT_LIGHTS;
		// notification.defaults |= Notification.DEFAULT_VIBRATE;

		Context context = getApplicationContext();
		CharSequence contentTitle = "New order";
		CharSequence contentText;
		if (newOrdersCount == 1)
			contentText = "You have 1 new order!";
		else
			contentText = "You have " + String.valueOf(newOrdersCount)
					+ " new orders!";
		Intent notificationIntent = new Intent(this, MainMenu.class);
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
				notificationIntent, Notification.FLAG_AUTO_CANCEL);

		notification.setLatestEventInfo(context, contentTitle, contentText,
				contentIntent);

		mNotificationManager.notify(Sync_Notification_ID, notification);
	}

	public void onDestroy() {
		super.onDestroy();
		// timer.cancel();
		// beeperHandle.cancel(true);
	}

}
