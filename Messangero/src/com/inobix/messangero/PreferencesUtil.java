package com.inobix.messangero;

import java.util.Date;

import com.crashlytics.android.Crashlytics;
import com.inobix.messangero.common.app_settings;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class PreferencesUtil {
	
	//demo google api key - AIzaSyALfr2EV_oXpscTUlvG6tood0WHdZwRCAw
	//release google api key - AIzaSyCkx_8rMEgQ5wTx1ClEYFUHQEEdCRyYk_4
	//debug google api key - AIzaSyBFSlG8D5An1CtvV_QBsHYRaLH0rjSZUuk
	public static String IP;
	public static Integer Port;
	public static Integer DataSyncPeriod;
	public static Integer GPSSyncPeriod;
	public static boolean SearchAllBarcodeTypes;
	public static boolean SearchOnlyCode39;
	public static boolean RotateScreen;
	public static String UserName;
	public static String Password;
	public static int LanguageID;
	public static boolean IsRated;
	// "Messangero", "Courier109"
	public static String SyncModel = "";
	public static boolean TorchON = true;

	public static double Latitude = 0;
	public static double Longitude = 0;
	public static Date LastLocationCheck = new Date();
	public static Date LastDataSyncDate = null;
	public static Date LastGPSSyncDate = null;
	public static int CurrentUserActivity = -1;
	public static int CurrentUserActivityCount = 0;

	public static String GetLocale() {
		String locale = "gr";
		if (LanguageID == 0)
			locale = "gr";
		else if (LanguageID == 1)
			locale = "en";
		else if (LanguageID == 2)
			locale = "bg";
		else if (LanguageID == 3)
			locale = "es";

		return locale;
	}

	private static final String APP_SHARED_PREFS = "com.inobix.Messangero_preferences"; // Name
																						// of
																						// the
																						// file
																						// -.xml

	public static void LoadSettings(Context context) {
		SharedPreferences appSharedPrefs = context.getSharedPreferences(
				APP_SHARED_PREFS, Activity.MODE_PRIVATE);

		//ip azure - 168.63.65.89
		//IP = appSharedPrefs.getString("IP", "168.63.66.246");
		//IP = appSharedPrefs.getString("IP", "168.63.65.76");
		 //ip paipetis 62.38.153.247 / 193.92.181.118
		//fis
		//IP = appSharedPrefs.getString("IP", "193.92.181.118");
		// port paipetis
		// Port = appSharedPrefs.getInt("Port", 2136);
		// ip home
		//IP = appSharedPrefs.getString("IP", "77.78.32.118");
		//Port = appSharedPrefs.getInt("Port", 2439);
		//IP = appSharedPrefs.getString("IP", "");
		//interpost
		//IP = appSharedPrefs.getString("IP", " 62.38.108.229");
		//Port = appSharedPrefs.getInt("Port", 15000);
		// ip kodolabados
		IP = appSharedPrefs.getString("IP", "62.103.237.188");
		// port kodolabados
		Port = appSharedPrefs.getInt("Port", 2439);
		DataSyncPeriod = appSharedPrefs.getInt("DataSyncPeriod", 120000);
		GPSSyncPeriod = appSharedPrefs.getInt("GPSSyncPeriod", 5000);
		SearchAllBarcodeTypes = appSharedPrefs.getBoolean(
				"SearchAllBarcodeTypes", true);
		SearchOnlyCode39 = appSharedPrefs.getBoolean("SearchOnlyCode39", false);
		RotateScreen = appSharedPrefs.getBoolean("RotateScreen", false);
		UserName = appSharedPrefs.getString("UserName", "");
		Password = appSharedPrefs.getString("Password", "");
		LanguageID = appSharedPrefs.getInt("Language", -1);
		IsRated = appSharedPrefs.getBoolean("IsRated", false);	
		TorchON = appSharedPrefs.getBoolean("Torch", true);
		//SyncModel = appSharedPrefs.getString("SyncModel", "");
		//SyncModel = appSharedPrefs.getString("SyncModel", "Messangero");
		//SyncModel = appSharedPrefs.getString("SyncModel", "MessangeroBG");
		//SyncModel = appSharedPrefs.getString("SyncModel", "Messangero30");
		SyncModel = appSharedPrefs.getString("SyncModel", "Messangero2015");
	}

	public static void LoadSyncDates(Context context) {
		SharedPreferences appSharedPrefs = context.getSharedPreferences(
				APP_SHARED_PREFS, Activity.MODE_PRIVATE);
		try {
			long dateDataMilis = appSharedPrefs.getLong("LastDataSyncDate", 0);
			long dateGPSMilis = appSharedPrefs.getLong("LastGPSSyncDate", 0);

			if (dateDataMilis == 0)
				LastDataSyncDate = null;
			else
				LastDataSyncDate = new Date(dateDataMilis);

			if (dateGPSMilis == 0)
				LastGPSSyncDate = null;
			else
				LastGPSSyncDate = new Date(dateGPSMilis);
		} catch (Exception ex) {
			int a = 0;
		}

	}

	public static String GetSyncModel(Context context) {
		SharedPreferences appSharedPrefs = context.getSharedPreferences(
				APP_SHARED_PREFS, Activity.MODE_PRIVATE);

		String syncModel = appSharedPrefs.getString("SyncModel", "");
		return syncModel;
	}

	
	public static String GetIP(Context context) {
		SharedPreferences appSharedPrefs = context.getSharedPreferences(
				APP_SHARED_PREFS, Activity.MODE_PRIVATE);

		String ip = appSharedPrefs.getString("IP", "");
		return ip;
	}

	public static int GetPort(Context context) {
		SharedPreferences appSharedPrefs = context.getSharedPreferences(
				APP_SHARED_PREFS, Activity.MODE_PRIVATE);

		int port = appSharedPrefs.getInt("Port", 0);
		return port;
	}

	public static int GetDriverID(Context context) {
		SharedPreferences appSharedPrefs = context.getSharedPreferences(
				APP_SHARED_PREFS, Activity.MODE_PRIVATE);

		int driverID = appSharedPrefs.getInt("DriverID", 0);
		return driverID;
	}

	public static void SavePreferences(Context context, String ip,
			Integer port, Integer dataSyncPeriod, Integer gpsSyncPeriod,
			boolean searchAllBarcodeTypes, boolean searchOnlyCode39,boolean rotateScreen,
			int languageID) {

		SharedPreferences appSharedPrefs = context.getSharedPreferences(
				APP_SHARED_PREFS, Activity.MODE_PRIVATE);
		Editor prefsEditor = appSharedPrefs.edit();

		prefsEditor.putString("IP", ip);
		prefsEditor.putInt("Port", port);
		prefsEditor.putInt("DataSyncPeriod", dataSyncPeriod);
		prefsEditor.putInt("GPSSyncPeriod", gpsSyncPeriod);
		prefsEditor.putBoolean("SearchAllBarcodeTypes", searchAllBarcodeTypes);
		prefsEditor.putBoolean("SearchOnlyCode39", searchOnlyCode39);
		prefsEditor.putBoolean("RotateScreen", rotateScreen);		
		prefsEditor.putString("UserName", UserName);
		prefsEditor.putString("Password", Password);
		prefsEditor.putInt("Language", languageID);
		prefsEditor.putString("SyncModel", SyncModel);
		prefsEditor.putBoolean("Torch", TorchON);

		prefsEditor.commit();

		IP = ip;
		Port = port;
		DataSyncPeriod = dataSyncPeriod;
		GPSSyncPeriod = gpsSyncPeriod;
		SearchOnlyCode39 = searchOnlyCode39;
		SearchAllBarcodeTypes = searchAllBarcodeTypes;
		RotateScreen = rotateScreen;
		LanguageID = languageID;
		

	}

	public static void SaveCredentials(Context context, String username,
			String password) {
		SharedPreferences appSharedPrefs = context.getSharedPreferences(
				APP_SHARED_PREFS, Activity.MODE_PRIVATE);
		Editor prefsEditor = appSharedPrefs.edit();

		prefsEditor.putString("IP", IP);
		prefsEditor.putInt("Port", Port);
		prefsEditor.putInt("DataSyncPeriod", DataSyncPeriod);
		prefsEditor.putInt("GPSSyncPeriod", GPSSyncPeriod);
		prefsEditor.putBoolean("SearchAllBarcodeTypes", SearchAllBarcodeTypes);
		prefsEditor.putBoolean("SearchOnlyCode39", SearchOnlyCode39);
		prefsEditor.putBoolean("RotateScreen", RotateScreen);		
		prefsEditor.putString("UserName", username);
		prefsEditor.putString("Password", password);
		prefsEditor.putString("SyncModel", SyncModel);

		prefsEditor.commit();

		UserName = username;
		Password = password;
	}

	public static void SetRated(Context context, boolean rated) {
		SharedPreferences appSharedPrefs = context.getSharedPreferences(
				APP_SHARED_PREFS, Activity.MODE_PRIVATE);
		Editor prefsEditor = appSharedPrefs.edit();

		prefsEditor.putBoolean("IsRated", rated);

		prefsEditor.commit();
		IsRated = rated;
	}

	public static void SetLastGPSDate(Context context, Date date) {
		SharedPreferences appSharedPrefs = context.getSharedPreferences(
				APP_SHARED_PREFS, Activity.MODE_PRIVATE);
		Editor prefsEditor = appSharedPrefs.edit();

		prefsEditor.putLong("LastGPSSyncDate", date.getTime());

		prefsEditor.commit();
		LastGPSSyncDate = date;
	}

	public static void SetLastDataDate(Context context, Date date) {
		SharedPreferences appSharedPrefs = context.getSharedPreferences(
				APP_SHARED_PREFS, Activity.MODE_PRIVATE);
		Editor prefsEditor = appSharedPrefs.edit();

		prefsEditor.putLong("LastDataSyncDate", date.getTime());

		prefsEditor.commit();
		LastDataSyncDate = date;
	}

	public static void SetDriverID(Context context, int driverID) {
		SharedPreferences appSharedPrefs = context.getSharedPreferences(
				APP_SHARED_PREFS, Activity.MODE_PRIVATE);
		Editor prefsEditor = appSharedPrefs.edit();

		prefsEditor.putInt("DriverID", driverID);

		prefsEditor.commit();
	}

	public static void SetFirstTimeSync(Context context) {
		SharedPreferences appSharedPrefs = context.getSharedPreferences(
				APP_SHARED_PREFS, Activity.MODE_PRIVATE);
		Editor prefsEditor = appSharedPrefs.edit();

		String result = appSharedPrefs.getString("IsFirstSync", "");
		if (result.equals(""))
			result = String.valueOf(app_settings.CurrentUserID);
		else
			result = result + "," + String.valueOf(app_settings.CurrentUserID);
		prefsEditor.putString("IsFirstSync", result);

		prefsEditor.commit();
	}

	public static boolean GetIsFirstTimeSync(Context context) {
		SharedPreferences appSharedPrefs = context.getSharedPreferences(
				APP_SHARED_PREFS, Activity.MODE_PRIVATE);

		String result = appSharedPrefs.getString("IsFirstSync", "");
		String[] res = result.split(",");
		boolean isFirst = true;
		if (res.length > 0) {
			for (String s : res) {
				if (s.equals(String.valueOf(app_settings.CurrentUserID))) {
					isFirst = false;
					break;
				}
			}
		}
		return isFirst;
	}
}
