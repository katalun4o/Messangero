package DAL;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;

import microsoft.aspnet.signalr.client.hubs.HubConnection;
import microsoft.aspnet.signalr.client.hubs.HubProxy;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.ianywhere.ultralitejni12.*;
import com.inobix.messangero.GCMIntentService;
import com.inobix.messangero.GPSService;
import com.inobix.messangero.LocalService;
import com.inobix.messangero.LoginScreen;
import com.inobix.messangero.MainMenu;
import com.inobix.messangero.PreferencesUtil;
import com.inobix.messangero.common.app_settings;

public class DBUtil {
	public static final int SYNC_VOUCHERS_REQUEST_CODE = 53464;
	public static boolean IsSyncRunning = false;
	public static final String DB_NAME = "courierdb.udb";
	// public static Connection _conn;
	public static int LastOrdersCount = -1;

	// public static Context context;

	// public static Connection CreateConnection() {
	// return CreateConnection(context);
	// }

	public static Connection CreateConnection(Context ctx) {
		Connection conn = null;

		ConfigFileAndroid config;
		try {
			// String dbLocation = ctx.getDatabasePath("courierdb.udb")
			// .getParentFile().getParent();

			// the location of the database file in application data directory
			// String dbFileLocation = dbLocation + "/courierdb.udb"; // +
			// DBUtil.DB_NAME;

			// String dataPath = ctx.getFilesDir().getParent();
			// String dbFileLocation = dataPath + "/databases/courierdb.udb";
			config = DatabaseManager.createConfigurationFileAndroid(DB_NAME,
					ctx);
			// String dbPath = ctx.getExternalFilesDir(null)
			// + DB_NAME;

			// config = DatabaseManager.createConfigurationFileAndroid(
			// dbFileLocation, ctx);
			
			conn = DatabaseManager.connect(config);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			// Log.i("DBUtilDB_NAME", DBUtil.DB_NAME);
			e.printStackTrace();
			Crashlytics.logException(e);
		}

		return conn;
	}

	ConfigFileAndroid config;

	
	
	public DBUtil() throws ULjException {
		// create config file for the database

		/*config = DatabaseManager.createConfigurationFileAndroid(DB_NAME, act);

		// String s = config.getConnectionString();
		Connection conn = null;
		// Connection conn = DBUtil.CreateConnection(act);
		try {
			// try to connect to the database
			// _conn = DatabaseManager.createDatabase(config);
			conn = DatabaseManager.connect(config);
			conn.release();
		} catch (ULjException e) {
			// if doesnt exist create the database
			if (app_settings.IsDemoVersion == false) {
				conn = DatabaseManager.createDatabase(config);
				CreateDatabase(act);
			} else
				DBUtil.CopyFirstTimeDatabase(act);
			// _conn = DatabaseManager.connect(config);
		}*/
	}
	
	public void LoadDatabaseFirstTime(Context act) throws ULjException
	{
		config = DatabaseManager.createConfigurationFileAndroid(DB_NAME, act);

		Connection conn = null;
		try {
			// try to connect to the database
			conn = DatabaseManager.connect(config);
		} catch (ULjException e) {
			// if doesnt exist create the database
			if (app_settings.IsDemoVersion == false) {
				conn = DatabaseManager.createDatabase(config);
				CreateDatabase(act); 
			} else
				DBUtil.CopyFirstTimeDatabase(act);
		}
		finally
		{
			if(conn != null)
				conn.release();
		}
	}

	public void CreateNewDatabase(Context context) {
		try {
			Connection conn = DatabaseManager.createDatabase(config);
			
			CreateDatabase(context);
		} catch (ULjException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	public static void DeleteDatabase(Context c) {
		// application data directory

		// String dbLocation = "/data/data/com.inobix.messangero/";
		String dbLocation = c.getDatabasePath("courierdb.udb").getParentFile()
				.getParent();

		// the location of the database file in application data directory
		String dbFileLocation = dbLocation + "/" + DBUtil.DB_NAME;

		File dbFile = new File(dbFileLocation);
		if (dbFile.exists()) {
			dbFile.delete();
		}
	}

	public static void CopyFirstTimeDatabase(Context c) {
		// application data directory

		// String dbLocation = "/data/data/com.inobix.messangero/";
		String dbLocation = c.getDatabasePath("courierdb.udb").getParentFile()
				.getParent();

		// the location of the database file in application data directory
		String dbFileLocation = dbLocation + "/" + DBUtil.DB_NAME;

		File dbFile = new File(dbFileLocation);
		if (dbFile.exists()) {
			dbFile.delete();
		}
		// if not exists copy database from assets
		if (!dbFile.exists()) {
			try {
				InputStream myInput = c.getAssets().open(DBUtil.DB_NAME);	
				// Path to the new db
				String outFileName = dbFileLocation;

				// Open the empty db as the output stream
				OutputStream myOutput = new FileOutputStream(outFileName);

				// transfer bytes from the inputfile to the outputfile
				byte[] buffer = new byte[1024];
				int length;
				while ((length = myInput.read(buffer)) > 0) {
					myOutput.write(buffer, 0, length);
				}

				// Close the streams
				myOutput.flush();
				myOutput.close();
				myInput.close();

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void CreateDatabase(Context context) throws ULjException {
		// _conn = DatabaseManager.createDatabase(config);
		/*
		 * try { // try to connect to the database _conn =
		 * DatabaseManager.connect(config); } catch (ULjException e) { // if
		 * doesnt exist create the database
		 * if(!com.inobix.messangero.common.app_settings.IsDemoVersion) _conn =
		 * DatabaseManager.createDatabase(config); }
		 */
		// create all the tables in the database
		if (!com.inobix.messangero.common.app_settings.IsDemoVersion)
			CreateTables(context);
	}

	public void CreateTables(Context context) throws ULjException {
		CreateTableVoucher(context);
	}

	private void CreateTableVoucher(Context context) throws ULjException {
		// create table companies

		Connection conn = CreateConnection(context);
		try {
			StringBuffer sbCompanies = new StringBuffer();
			sbCompanies
					.append("CREATE TABLE IF NOT EXISTS Companies (comp_id numeric(6,0) PRIMARY KEY, "
							+ "comp_cod varchar(10)  , "
							+ "comp_desc varchar(120)  , "
							+ "comp_long_desc varchar(400)  , "
							+ "comp_tax_num varchar(30)  , "
							+ "trus_id numeric(6, 0)  , "
							+ "comp_addr varchar(120)  , "
							+ "comp_zip varchar(10)  , "
							+ "comp_city varchar(60)  , "
							+ "comp_phone varchar(30)  , "
							+ "comp_fax varchar(30)  , "
							+ "comp_email varchar(120)  , "
							+ "comp_web varchar(120)  , "
							+ "comp_book_num varchar(100) " + ")");
			PreparedStatement psCompanies = conn.prepareStatement(sbCompanies
					.toString());
			psCompanies.execute();
			psCompanies.close();

			// create table users
			StringBuffer sbUsers = new StringBuffer();
			sbUsers.append("CREATE TABLE IF NOT EXISTS rUsers (UserID numeric(6,0) PRIMARY KEY, "
					+ "UserCode varchar(30), "
					+ "UserPass varchar(120), "
					+ "FirstName varchar(100), "
					+ "LastName varchar(100), "
					+ "LoginName varchar(100), "
					+ "GCM_ID varchar(256), "
					+ "DriverID numeric(18,0) " + ")");
			PreparedStatement psUsers = conn.prepareStatement(sbUsers
					.toString());
			psUsers.execute();
			psUsers.close();

			// CourierCharges
			StringBuffer sbCourierCharges = new StringBuffer();
			sbCourierCharges
					.append("CREATE TABLE IF NOT EXISTS CourierCharge (CourierChargeID numeric(18,0) PRIMARY KEY, "
							+ "CourierChargeCode varchar(100), "
							+ "CourierChargeDesc varchar(100), "
							+ "CreditDebit char(1) " + ")");
			PreparedStatement psCourierCharges = conn
					.prepareStatement(sbCourierCharges.toString());
			psCourierCharges.execute();
			psCourierCharges.close();

			// RCourierChargeTrans
			StringBuffer sbRCourierChargeTrans = new StringBuffer();
			sbRCourierChargeTrans
					.append("CREATE TABLE IF NOT EXISTS RCourierChargeTrans (CourierChargeTransID numeric(18,0) PRIMARY KEY, "
							+ "CourierID numeric(18,0), "
							+ "CourierChargeID numeric(18,0), "
							+ "SumCash numeric(24,3), "
							+ "SumCheck numeric(24,3) )");
			PreparedStatement psRCourierChargeTrans = conn
					.prepareStatement(sbRCourierChargeTrans.toString());
			psRCourierChargeTrans.execute();
			psRCourierChargeTrans.close();

			// create table users
			StringBuffer sbCourierDriverGPSLog = new StringBuffer();
			sbCourierDriverGPSLog
					.append("CREATE TABLE IF NOT EXISTS CourierDriverGPSLog1 ("
							+ "ID numeric(18,0) primary key default autoincrement,"
							+ " LocationTime DATETIME , "
							+ "CourierDriverID numeric(18, 0), "
							+ "Latitude numeric(18, 6), "
							+ "Longitude numeric(18, 6), "
							+ "Speed numeric(18, 6), " + "Activity INTEGER, "
							+ "SyncTime DATETIME " + ")");
			PreparedStatement CourierDriverGPSLog = conn
					.prepareStatement(sbCourierDriverGPSLog.toString());
			CourierDriverGPSLog.execute();
			CourierDriverGPSLog.close();

			StringBuffer sbNotDeliveredType = new StringBuffer();
			sbNotDeliveredType
					.append("CREATE TABLE IF NOT EXISTS CourierNotDeliveredType (ID numeric(6,0) PRIMARY KEY, "
							+ "CourierNotDeliveredTypeName varchar(50))");
			PreparedStatement psNotDeliveredType = conn
					.prepareStatement(sbNotDeliveredType.toString());
			psNotDeliveredType.execute();
			psNotDeliveredType.close();

			// create table new vouchers
			/*
			 * StringBuffer sbNewVoucher = new StringBuffer(); sbNewVoucher
			 * .append(
			 * "CREATE TABLE IF NOT EXISTS RemoteNewVouchers (ID numeric(18,0) PRIMARY KEY , "
			 * + "Number varchar(100), " + "DriverID numeric(18,0)," +
			 * "TransType varchar(2), " + "DeliveryDate datetime, " +
			 * "TransReason varchar(400), " +
			 * "TransNotDeliveredTypeID numeric(6,0), " +
			 * "TransReceiverName varchar(200), " +
			 * "ReceivedCashValue Numeric(18,3), " +
			 * "ReceivedCheckValue Numeric(18,3), " +
			 * "ReceivedCheckCount Numeric(6,0), " + "ParcelStatus INTEGER, " +
			 * "DeliveryLatitude NUMERIC(18,6), " +
			 * "DeliveryLongitude NUMERIC(18,6), " + "DateCreated datetime, " +
			 * "TransSignature LONG BINARY " + ")"); PreparedStatement
			 * psNewVoucher = conn.prepareStatement(sbNewVoucher .toString());
			 * psNewVoucher.execute(); psNewVoucher.close();
			 */

			// create table vouchers
			StringBuffer sb = new StringBuffer();
			sb.append("CREATE TABLE IF NOT EXISTS RemoteCourier (CourierID numeric(18,0) PRIMARY KEY , "
					+ "VouchNumber varchar(100), "
					+ "VouchDate datetime, "
					+ "VouchMemo varchar(1000), "
					+ "SenderName varchar(200), "
					+ "SenderArea varchar(200), "
					+ "SenderCity varchar(200), "
					+ "SenderAddress varchar(200),"
					+ "SenderGSM varchar(100),"
					+ "ReceiverName varchar(200), "
					+ "ReceiverArea varchar(200), "
					+ "ReceiverCity varchar(200), "
					+ "ReceiverAddress varchar(200), "
					+ "ReceiverGSM varchar(100), "
					+ "VouchCount INTEGER,"
					+ "DocType varchar(2),"
					+ "DriverID numeric(18,0),"
					+ "TransType varchar(2), "
					+ "TransReceiverName varchar(200), "
					+ "TransDate datetime, "
					+ "DeliveryDate datetime, "
					+ "TransReason varchar(400), "
					+ "TransNotDeliveredTypeID numeric(6,0), "
					+ "ExpCashValue Numeric(18,3),"
					+ "ExpCheckValue Numeric(18,3),"
					+ "ExpCheckCount INTEGER,"
					+ "ReceivedCashValue Numeric(18,3), "
					+ "ReceivedCheckValue Numeric(18,3), "
					+ "ReceivedCheckCount Numeric(6,0), "
					+ "OrderVoucherNumber varchar(100), "
					+ "BranID numeric(6,0), "
					+ "DateCreated datetime, "
					+ "Latitude NUMERIC(18,6), "
					+ "Longitude NUMERIC(18,6), "
					+ "DeliveryHour varchar(100), "
					+ "ParcelStatus INTEGER, "
					+ "DeliveryLatitude NUMERIC(18,6), "
					+ "DeliveryLongitude NUMERIC(18,6), "
					+ "CourierCustomerType Char(1), "
					+ "IfIsCreditValue NUMERIC(24,3), "
					+ "OrderIndex INTEGER, "
					+ "TransSignature LONG BINARY "
					+ ")");
			PreparedStatement ps = conn.prepareStatement(sb.toString());
			ps.execute();
			ps.close();
			
			// RCourierChargeTrans
						StringBuffer sbRemoteCourierTrans = new StringBuffer();
						sbRemoteCourierTrans
								.append("CREATE TABLE IF NOT EXISTS RemoteCourierTrans ("
										+ "RemoteCourierTranID varchar(256) primary key , "
										+ "CourierTransID numeric(18,0), "
										+ "CourierID numeric(18,0), "
										+ "DriverID numeric(18,0), "
										+ "TransType varchar(4), "
										+ "TransDate datetime, "
										+ "TransReceiverName varchar(200), "
										+ "TransReason varchar(400), "
										+ "TransNotDeliveredTypeID numeric(6,0), "
										+ "ReceivedCashValue Numeric(18,3), "
										+ "ReceivedCheckValue Numeric(18,3), "
										+ "ReceivedCheckCount Numeric(6,0), "
										+ "DeliveryLatitude NUMERIC(18,6), "
										+ "DeliveryLongitude NUMERIC(18,6), "
										+ "DateCreated datetime, "
										+ "OrderVoucherNumber varchar(100), "
										+ "TransSignature LONG BINARY) " );
						PreparedStatement psRemoteCourierTrans = conn
								.prepareStatement(sbRemoteCourierTrans.toString());
						psRemoteCourierTrans.execute();
						psRemoteCourierTrans.close();

			PreparedStatement psCreatePublUsers = conn
					.prepareStatement("CREATE PUBLICATION IF NOT EXISTS pblUsers (TABLE rUsers) ");
			psCreatePublUsers.execute();
			psCreatePublUsers.close();

			PreparedStatement psCreatePublGPSLog = conn
					.prepareStatement("CREATE PUBLICATION IF NOT EXISTS pblGPS1 (TABLE CourierDriverGPSLog1)");
			psCreatePublGPSLog.execute();
			psCreatePublGPSLog.close();

			PreparedStatement psCreatePubl = conn
					.prepareStatement("CREATE PUBLICATION IF NOT EXISTS pblCourier (Table Companies, TABLE RemoteCourier, TABLE RemoteCourierTrans, TABLE rUsers, "
							+ "Table CourierCharge, TABLE RCourierChargeTrans, TABLE CourierNotDeliveredType) ");
			psCreatePubl.execute();
			psCreatePubl.close();
		} catch (Exception ex) {
			int a = 0;
		} finally {
			conn.release();
		}

	}

	public static void SyncUsers(Context context) {

		try {
			while (DBUtil.IsSyncRunning == true) {

			}
			boolean isFirstTimeSync = PreferencesUtil
					.GetIsFirstTimeSync(context);
			DBUtil.IsSyncRunning = true;
			Connection conn = DBUtil.CreateConnection(context);
			try {
				SyncParms syncParms = conn.createSyncParms(
						SyncParms.HTTP_STREAM, "sa", PreferencesUtil.SyncModel);

				StreamHTTPParms params = syncParms.getStreamParms();
				params.setHost(PreferencesUtil.GetIP(context));
				params.setPort(PreferencesUtil.GetPort(context));
				syncParms.setPublications("pblUsers");
				if (isFirstTimeSync == false) {
					syncParms.setAuthenticationParms(String
							.valueOf(app_settings.CurrentUserID) + ",0"); 
				} else {
					syncParms.setAuthenticationParms(String
							.valueOf(app_settings.CurrentUserID) + ",1");

					PreferencesUtil.SetFirstTimeSync(context);
				}

				conn.synchronize(syncParms);
				SyncResult sr = conn.getSyncResult();
				conn.commit();
			} catch (Exception ex) {
				ex.printStackTrace();
			} finally {
				conn.release();
				DBUtil.IsSyncRunning = false;
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			DBUtil.IsSyncRunning = false;
		}
	}

	private static PendingIntent mAlarmSender;

	public static void SyncVouchers(Context context) {
		boolean isSyncSuccessful = false;
		Log.d("Messangero", "Sync Vouchers");
		try {
			while (DBUtil.IsSyncRunning == true) {

			}
			boolean isFirstTimeSync = PreferencesUtil
					.GetIsFirstTimeSync(context);
			DBUtil.IsSyncRunning = true;
			Connection conn = DBUtil.CreateConnection(context);
			try {
				SyncParms syncParms = conn.createSyncParms(
						SyncParms.HTTP_STREAM, "sa", PreferencesUtil.SyncModel);
				//syncParms.setVersion("Messangero_3_0");

				StreamHTTPParms params = syncParms.getStreamParms();
				params.setHost(PreferencesUtil.GetIP(context));
				params.setPort(PreferencesUtil.GetPort(context));
				// params.setHost("77.78.32.118");
				// params.setHost(PreferencesUtil.IP);
				// params.setPort(PreferencesUtil.Port);
				// params.setPort(2439);
				syncParms.setPublications("pblCourier");
				if (isFirstTimeSync == false) {
					syncParms.setAuthenticationParms(String
							.valueOf(app_settings.CurrentUserID) + ",0");
				} else {
					syncParms.setAuthenticationParms(String
							.valueOf(app_settings.CurrentUserID) + ",1");

					PreferencesUtil.SetFirstTimeSync(context);
				}

				conn.synchronize(syncParms);
				SyncResult sr = conn.getSyncResult();
				conn.commit();
				String errorMsg = sr.getStreamErrorMessage();
				if (errorMsg == null || errorMsg.equals("")) {
					isSyncSuccessful = true;
				}

			} catch (Exception ex) {
				SyncResult sr = conn.getSyncResult();
				ex.printStackTrace();

			} finally {
				conn.release();
				DBUtil.IsSyncRunning = false;
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			DBUtil.IsSyncRunning = false;
		}

		if (isSyncSuccessful) {
			CleanNegativeVouchers(context);
		}

		if (isSyncSuccessful) {
			GCMIntentService.ClearNoConnectionNotification(context);
			PreferencesUtil.SetLastDataDate(context, new Date());
		} else {
			GCMIntentService.RaiseNoConnectionNotification(context);
		}

		Log.d("Messangero", "Result Sync Vouchers :" + isSyncSuccessful);
		Context appContext = context.getApplicationContext();
		if (!isSyncSuccessful) {
			mAlarmSender = PendingIntent.getService(appContext,
					SYNC_VOUCHERS_REQUEST_CODE, new Intent(appContext,
							LocalService.class), 0);

			long firstTime = SystemClock.elapsedRealtime();

			AlarmManager am = (AlarmManager) appContext
					.getSystemService(Context.ALARM_SERVICE);
			am.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, firstTime,
					PreferencesUtil.DataSyncPeriod, mAlarmSender);
			Log.d("Messangero", "Sync Vouchers Alarm Started");
		} else {
			Intent intentstop1 = new Intent(appContext, LocalService.class);
			PendingIntent senderstop1 = PendingIntent.getService(appContext,
					SYNC_VOUCHERS_REQUEST_CODE, intentstop1, 0);
			if (senderstop1 != null) {
				AlarmManager alarmManagerstop1 = (AlarmManager) appContext
						.getSystemService(Context.ALARM_SERVICE);
				alarmManagerstop1.cancel(senderstop1);
				Log.d("Messangero", "Sync Vouchers Alarm Stopperd");
			}
		}
	}

	private static void CleanNegativeVouchers(Context ctx) {
		Connection conn = DBUtil.CreateConnection(ctx);
		try {
			PreparedStatement ps = conn
					.prepareStatement("DELETE FROM RemoteCourier WHERE  CourierID < 0 AND "
							+ "EXISTS (SELECT * FROM RemoteCourier rReal WHERE  rReal.CourierID > 0 AND rReal.VouchNumber = RemoteCourier.VouchNumber)");

			ps.execute();
			ps.close();
			conn.commit();
		} catch (ULjException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				conn.release();
			} catch (ULjException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public static void ResetDatabase(Context ctx) {
		if(!app_settings.IsDemoVersion)
		{
			//reset database on demo version
			DBUtil.CopyFirstTimeDatabase(ctx);
		}
		else
		{
			//reset database on full version
			DBUtil.DeleteDatabase(ctx);
		}
		
		AlarmManager mgr = (AlarmManager) ctx
				.getSystemService(Context.ALARM_SERVICE);

		PendingIntent RESTART_INTENT = PendingIntent.getActivity(ctx, 0
				, new Intent(ctx,
				LoginScreen.class), ((Activity)ctx).getIntent().getFlags());
		mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 1000,
				RESTART_INTENT);
		System.exit(2);	

		
	}
	
	public static void UploadFile(Context ctx, String fileName)
	{
		/*ConfigFileAndroid config = DatabaseManager.createConfigurationFileAndroid(DB_NAME,
				ctx);
		
		FileTransfer ft = new 
		IFileTransfer f = DatabaseManager.CreateFileTransfer(itemImageID + ".jpg",SyncParms.HttpStream,PreferencesUtil.SyncUser,PreferencesUtil.SyncModel);
        f.StreamParms.Host = PreferencesUtil.IP;
        f.StreamParms.Port = PreferencesUtil.Port;

        if (!System.IO.Directory.Exists(fileLocation))
        {
            System.IO.Directory.CreateDirectory(fileLocation);
        }
        f.LocalPath = fileLocation;
        f.LocalFileName = fileName;
        bool res = f.DownloadFile();
        string ms = f.StreamErrorMessage;*/
        ////Base.Log.LogMessage("File download
	}
	
	public static void OnSyncError(String userID)
	{
		HubConnection conn = new HubConnection("http://77.78.32.118:2754");
		HubProxy proxy = conn.createHubProxy("CourierHub");
		conn.start();
		proxy.invoke("OnSyncError", userID);
		conn.stop();
	}

	public static void SyncGPS(Context context) {

		if (DBUtil.IsSyncRunning == true)
			return;
		while (DBUtil.IsSyncRunning == true) {

		}
		try {
			boolean isSyncSuccessful = false;
			DBUtil.IsSyncRunning = true;
			Connection _conn = DBUtil.CreateConnection(context);

			try {
				SyncParms syncParms = _conn.createSyncParms(
						SyncParms.HTTP_STREAM, "sa", PreferencesUtil.SyncModel);
				syncParms.setUploadOnly(true);
				StreamHTTPParms params = syncParms.getStreamParms();
				params.setHost(PreferencesUtil.GetIP(context));
				params.setPort(PreferencesUtil.GetPort(context));

				syncParms.setPublications("pblGPS1");

				syncParms.setAuthenticationParms(String
						.valueOf(app_settings.CurrentUserID) + ",0");
				_conn.synchronize(syncParms);
				SyncResult sr = _conn.getSyncResult();

				_conn.commit();

				String errorMsg = sr.getStreamErrorMessage();
				if (errorMsg == null || errorMsg.equals(""))
					isSyncSuccessful = true;

			} catch (Exception ex) {
				ex.printStackTrace();
				// Crashlytics.logException(ex);
			} finally {
				_conn.release();
				DBUtil.IsSyncRunning = false;
			}
			
			if (isSyncSuccessful) {
				GCMIntentService.ClearNoConnectionNotification(context);
				PreferencesUtil.SetLastGPSDate(context, new Date());
			} else {
				GCMIntentService.RaiseNoConnectionNotification(context);
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			DBUtil.IsSyncRunning = false;
		}
	}

}
