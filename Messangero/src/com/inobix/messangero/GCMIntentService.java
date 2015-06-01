package com.inobix.messangero;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.google.android.gcm.GCMBaseIntentService;

import DAL.DBUtil;
import DAL.User;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.text.format.DateFormat;

public class GCMIntentService extends GCMBaseIntentService {
	public GCMIntentService() {

	}

	public GCMIntentService(String senderID) {
		super(senderID);
	}

	@Override
	protected void onError(Context arg0, String errorId) {

	}

	@Override
	protected void onMessage(Context arg0, Intent arg1) {
		PreferencesUtil.LoadSettings(arg0);
		if (PreferencesUtil.Password == null
				|| PreferencesUtil.Password.equals("") == true
				|| PreferencesUtil.UserName == null
				|| PreferencesUtil.UserName.equals("") == true) {
			return;
		}

		String status = arg1.getExtras().getString("status");

		if (status.equals("1")) {
			RaiseNotification(arg0, com.inobix.messangero.R.drawable.appicon);
			// DBUtil.SyncVouchers(arg0);
		}
	}

	private void RaiseNotification(Context ctx, int drawableResID) {
		try {
			String ns = Context.NOTIFICATION_SERVICE;
			NotificationManager mNotificationManager = (NotificationManager) getSystemService(ns);

			// String title = placeName;
			String message = ctx
					.getString(com.inobix.messangero.R.string.app_name);

			CharSequence tickerText = "Messangero";
			long when = System.currentTimeMillis();

			Notification notification = new Notification(drawableResID,
					tickerText, when);
			notification.defaults |= Notification.DEFAULT_SOUND;
			notification.defaults |= Notification.DEFAULT_LIGHTS;
			// notification.defaults |= Notification.DEFAULT_VIBRATE;

			Context context = getApplicationContext();
			CharSequence contentTitle = tickerText;
			CharSequence contentText;
			// contentText =
			// "Thank you for using our services, you have 20% discount active at Temida";
			contentText = message;
			Intent notificationIntent = new Intent(this, LoginScreen.class);
			/*
			 * notificationIntent.putExtra("UserVisitID", userVisitID);
			 * notificationIntent.putExtra("UserID", userID);
			 * notificationIntent.putExtra("PlaceName", placeName);
			 * notificationIntent.putExtra("ConfirmUserName", confirmUserName);
			 * notificationIntent.putExtra("ConfirmUserMediaID",
			 * confirmUserMediaID); notificationIntent.putExtra("Value", value);
			 */
			notificationIntent.putExtra("SyncOrders", true);

			PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
					notificationIntent, Notification.FLAG_AUTO_CANCEL
							| PendingIntent.FLAG_CANCEL_CURRENT);

			notification.setLatestEventInfo(context, contentTitle, contentText,
					contentIntent);

			mNotificationManager.notify(MainMenu.GCM_Notification_ID,
					notification);
		} catch (Exception ex) {

		}
	}

	public static void ClearNoConnectionNotification(Context ctx) {
		String ns = Context.NOTIFICATION_SERVICE;
		NotificationManager mNotificationManager = (NotificationManager) ctx
				.getSystemService(ns);
		mNotificationManager.cancel(MainMenu.NO_CONNECTION_ID);
	}

	public static void RaiseNoConnectionNotification(Context ctx) {
		try {
			PreferencesUtil.LoadSyncDates(ctx);
			Date lastDataSyncDate = PreferencesUtil.LastDataSyncDate;
			Date lastGPSSyncDate = PreferencesUtil.LastGPSSyncDate;

			SimpleDateFormat format = new SimpleDateFormat("dd.MM HH:mm");
			String dataString = "";
			String gpsString = "";
			if (lastDataSyncDate != null)
				dataString = format.format(lastDataSyncDate);

			if (lastGPSSyncDate != null)
				gpsString = format.format(lastGPSSyncDate);

			int drawableResID = R.drawable.no_connection;
			String ns = Context.NOTIFICATION_SERVICE;
			NotificationManager mNotificationManager = (NotificationManager) ctx
					.getSystemService(ns);

			// String title = placeName;
			String message = ctx
					.getString(com.inobix.messangero.R.string.msgNoConnection);
			message = String.format(message, dataString, gpsString);

			CharSequence tickerText = "Messangero, no connection";
			long when = System.currentTimeMillis();

			Notification notification = new Notification(drawableResID,
					tickerText, when);
			// notification.defaults |= Notification.DEFAULT_SOUND;
			// notification.defaults |= Notification.DEFAULT_LIGHTS;
			// notification.defaults |= Notification.DEFAULT_VIBRATE;

			Context context = ctx.getApplicationContext();
			CharSequence contentTitle = tickerText;
			CharSequence contentText;
			// contentText =
			// "Thank you for using our services, you have 20% discount active at Temida";
			contentText = message;
			Intent notificationIntent = new Intent(ctx, LoginScreen.class);
			/*
			 * notificationIntent.putExtra("UserVisitID", userVisitID);
			 * notificationIntent.putExtra("UserID", userID);
			 * notificationIntent.putExtra("PlaceName", placeName);
			 * notificationIntent.putExtra("ConfirmUserName", confirmUserName);
			 * notificationIntent.putExtra("ConfirmUserMediaID",
			 * confirmUserMediaID); notificationIntent.putExtra("Value", value);
			 */
			// notificationIntent.putExtra("SyncOrders", true);

			PendingIntent contentIntent = PendingIntent.getActivity(ctx, 0,
					notificationIntent, Notification.FLAG_AUTO_CANCEL
							| PendingIntent.FLAG_CANCEL_CURRENT);

			notification.setLatestEventInfo(context, contentTitle, contentText,
					contentIntent);

			mNotificationManager
					.notify(MainMenu.NO_CONNECTION_ID, notification);
		} catch (Exception ex) {

		}
	}

	@Override
	protected void onRegistered(Context arg0, String regId) {
		User.UpdateUserGCM_ID(this, regId);
		DBUtil.SyncUsers(this);
	}

	@Override
	protected void onUnregistered(Context arg0, String regId) {

	}

}
