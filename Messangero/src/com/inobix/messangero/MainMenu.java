package com.inobix.messangero;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import com.crashlytics.android.Crashlytics;
import com.google.android.gcm.GCMRegistrar;
import com.google.android.gms.appstate.AppState;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.zxing.client.android.Intents;
import com.inobix.messangero.R.color;
import com.inobix.messangero.adapters.AdapterScanResult;
import com.inobix.messangero.common.IActionActivity;
import com.inobix.messangero.common.app_settings;
import com.inobix.messangero.map.MyLocation;
import com.inobix.messangero.map.MyLocation.LocationResult;
import com.inobix.messangero.LocalService;
import com.inobix.messangero.R;
import com.ianywhere.ultralitejni12.Connection;
import com.ianywhere.ultralitejni12.PreparedStatement;
import com.ianywhere.ultralitejni12.ResultSet;
import com.ianywhere.ultralitejni12.SyncParms;
import com.ianywhere.ultralitejni12.ULjException;

import DAL.Companies;
import DAL.Courier;
import DAL.DBUtil;
import DAL.ScanResult;
import DAL.User;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.backup.RestoreObserver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.DialogInterface.OnShowListener;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class MainMenu extends Activity implements IActionActivity,
		LocationListener, ConnectionCallbacks, OnConnectionFailedListener {
	private PendingIntent mAlarmSender;
	private PendingIntent mGPSSender;
	public static boolean IsSyncActive = false;
	private static final int SETTINGS_REQUEST_CODE = 1391;
	public static final int BARCODE_SCANNER_REQUEST_CODE = 7456;
	public static final int SYNC_GPS_REQUEST_CODE = 84564;
	public static final int SYNC_VOUCHERS_REQUEST_CODE = 53464;
	public static final int GCM_Notification_ID = 434555;
	public static final int NO_CONNECTION_ID = 8876;

	// gps service
	private LocationClient locationclient;
	private LocationRequest locationrequest;
	private Intent mIntentService;
	private PendingIntent mPendingIntent;
	private String scanResult = "";

	private void InitGpsService() {
		/*
		 * mIntentService = new Intent(this, LocationService.class);
		 * mPendingIntent = PendingIntent.getService(this, 1, mIntentService,
		 * 0);
		 */
		if (locationclient != null && locationclient.isConnected()) {
			return;
		}
		int resp = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
		if (resp == ConnectionResult.SUCCESS) {
			locationclient = new LocationClient(this, this, this);
			locationclient.connect();

			Log.d("Messangero", "Location Client Connect");

		} else {
			Toast.makeText(this, "Google Play Service Error " + resp,
					Toast.LENGTH_LONG).show();

		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		// if(locationclient!=null)
		// locationclient.disconnect();
	}

	@Override
	protected void onResume() {
		super.onResume();

		LoadVouchersCount();
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(app_settings.WorkDate);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);

		int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
		actionBar.SetLeftButtonText(String.valueOf(String.valueOf(dayOfMonth)
				+ " "
				+ String.format(Locale.ENGLISH, "%tB", calendar)
						.substring(0, 3) + "."));

		String ns = Context.NOTIFICATION_SERVICE;
		NotificationManager mNotificationManager = (NotificationManager) getSystemService(ns);
		mNotificationManager.cancel(MainMenu.GCM_Notification_ID);
	};

	com.inobix.messangero.MenuItem miTodayVouchers;
	com.inobix.messangero.MenuItem miTodayOrders;
	com.inobix.messangero.MenuItem miSearch;
	com.inobix.messangero.MenuItem miScan;
	com.inobix.messangero.MenuItem miScanGroupVouchers;
	com.inobix.messangero.MenuItem miSync;
	com.inobix.messangero.MenuItem miLogout;
	MenuArrayAdapter lvAdapter;
	ListView lvMenu;
	TextView lblUsername;
	TextView lblVouchersToDeliver;
	TextView lblVouchersDelivered;
	String lblVouchersToDeliverText = "";
	String lblVouchersDeliveredText = "";
	ActionBar actionBar;

	TextView lblOrdersToDeliver;
	TextView lblOrdersDelivered;
	TextView lblOrdersPending;
	String lblOrdersToDeliverText;
	String lblOrdersDeliveredText;
	String lblOrdersPendingText;

	int vouchersCount = 0;
	int ordersCount = 0;
	int vouchersCountDelviered = 0;
	int ordersCountDelviered = 0;
	int ordersPending = 0;

	View btnTodayVouchers;
	View btnTodayOrders;
	View btnSearch;
	View btnScan;
	View btnAddNewVoucher;
	View btnGroup;

	public void RegisterGCM(Context context) {
		GCMRegistrar.checkDevice(context);
		GCMRegistrar.checkManifest(context);

		final String regId = GCMRegistrar.getRegistrationId(context);
		if (regId.equals("")) {
			GCMRegistrar.register(context, app_settings.GCM_ID);
			// User.UpdateUserGCM_ID(this, regId);
			// DBUtil.SyncUsers(this);
		} else {
			User.UpdateUserGCM_ID(context, regId);

			new SyncUsersTask().execute(context);
			// DBUtil.SyncUsers(context);
		}

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Base.LoadLocale(this);
		setContentView(R.layout.main_menu_1);
		boolean screenConfig = false;
		if (savedInstanceState != null) {
			screenConfig = savedInstanceState.getBoolean("ScreenConfig");
		}

		if (!com.inobix.messangero.common.app_settings.IsDemoVersion)
			this.RegisterGCM(this.getApplicationContext());

		if (app_settings.IsDemoVersion) {
			UpdateVouchersDate();
		}

		Bundle extras = getIntent().getExtras();
		if (extras != null && extras.containsKey("SyncOrders")) {
			boolean sync = extras.getBoolean("SyncOrders");
			if (sync) {
				// DBUtil.SyncVouchers(this);
			}
		}

		// com.crashlytics.android.Crashlytics.start(this);
		// com.crashlytics.android.Crashlytics.start(this);

		Base.LoadVoucherTypesNames(this);

		actionBar = (ActionBar) findViewById(R.id.layoutActionBar);

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(app_settings.WorkDate);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);

		int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
		actionBar.SetLeftButtonText(String.valueOf(String.valueOf(dayOfMonth)
				+ " "
				+ String.format(Locale.ENGLISH, "%tB", calendar)
						.substring(0, 3) + "."));

		btnTodayVouchers = (View) findViewById(R.id.btnTodayVouchers);
		btnTodayVouchers.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Intent i = new Intent(MainMenu.this, ListTodayVouchers.class);
				i.putExtra("DocType", "1,2,3");
				startActivity(i);
			}
		});

		btnTodayOrders = (View) findViewById(R.id.btnTodayOrders);
		btnTodayOrders.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Intent i = new Intent(MainMenu.this, ListTodayVouchers.class);
				i.putExtra("DocType", "5");
				startActivity(i);
			}
		});

		btnScan = (View) findViewById(R.id.btnScan);
		btnScan.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				StartScan();
			}
		});
		
		btnAddNewVoucher = (View) findViewById(R.id.btnNewVoucher);
		btnAddNewVoucher.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				ShowEditDialog();
			}
		});


		btnSearch = (View) findViewById(R.id.btnSearch);
		btnSearch.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Intent i = new Intent(MainMenu.this, SearchScreen.class);
				startActivityIfNeeded(i, Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			}
		});

		btnGroup = (View) findViewById(R.id.btnGroup);
		btnGroup.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Intent i = new Intent(MainMenu.this, GroupVoucherDelivery.class);
				startActivityIfNeeded(i, Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);

			}
		});

		lblVouchersToDeliver = (TextView) findViewById(R.id.lblVouchersToDeliver);
		lblVouchersToDeliverText = getResources().getString(
				R.string.lblVouchersToDeliverText);

		lblVouchersDelivered = (TextView) findViewById(R.id.lblVouchersDelivered);
		lblVouchersDeliveredText = getResources().getString(
				R.string.lblVouchersDeliveredText);

		lblOrdersToDeliver = (TextView) findViewById(R.id.lblOrdersToTake);
		lblOrdersToDeliverText = getResources().getString(
				R.string.lblOrdersToTakeText);

		lblOrdersDelivered = (TextView) findViewById(R.id.lblOrdersTaken);
		lblOrdersDeliveredText = getResources().getString(
				R.string.lblOrdersTakenText);

		lblOrdersPending = (TextView) findViewById(R.id.lblOrdersToAccept);
		lblOrdersPendingText = getResources().getString(
				R.string.lblOrdersToAccept);

		lblUsername = (TextView) findViewById(R.id.tbUsername);
		lblUsername.setText(this.getString(R.string.lblUsername) + ": "
				+ PreferencesUtil.UserName);

		if (com.inobix.messangero.common.app_settings.IsDemoVersion) {
			PreferencesUtil.Latitude = 37.988976;
			PreferencesUtil.Longitude = 23.716604;
		}
		// demo version does not support sync
		if (!com.inobix.messangero.common.app_settings.IsDemoVersion) {
			// ShowProgressBar();
			if (!screenConfig) {
				actionBar.ShowProgressBar();
				new SyncTask().execute();
			}
		}

		// Companies.LoadCompanyPhone(this);

		/*
		 * Intent intentstop = new Intent(this, LocalService.class);
		 * PendingIntent senderstop = PendingIntent.getBroadcast(this, 53464,
		 * intentstop, 0); if (senderstop != null) { AlarmManager
		 * alarmManagerstop = (AlarmManager) getSystemService(ALARM_SERVICE);
		 * alarmManagerstop.cancel(senderstop); }
		 * 
		 * Intent intentstop1 = new Intent(this, GPSService.class);
		 * PendingIntent senderstop1 = PendingIntent.getBroadcast(this, 84564,
		 * intentstop1, 0); if (senderstop1 != null) { AlarmManager
		 * alarmManagerstop1 = (AlarmManager) getSystemService(ALARM_SERVICE);
		 * alarmManagerstop1.cancel(senderstop1); }
		 */

		if (!com.inobix.messangero.common.app_settings.IsDemoVersion) {
			// starts alarm to synchronize with sql server
			mAlarmSender = PendingIntent.getService(MainMenu.this,
					SYNC_VOUCHERS_REQUEST_CODE, new Intent(MainMenu.this,
							LocalService.class), 0);

			long firstTime = SystemClock.elapsedRealtime();

			// PreferencesUtil.SyncPeriod
			/*
			 * AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
			 * am.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, firstTime,
			 * PreferencesUtil.DataSyncPeriod, mAlarmSender);
			 */

			/*
			 * mGPSSender = PendingIntent.getService(MainMenu.this,
			 * SYNC_GPS_REQUEST_CODE, new Intent(MainMenu.this,
			 * GPSService.class), 0);
			 * 
			 * long firstTime1 = SystemClock.elapsedRealtime();
			 * 
			 * am.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, firstTime1,
			 * PreferencesUtil.GPSSyncPeriod, mGPSSender);
			 */
			// InitGpsService();

			final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

			if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
				buildAlertMessageNoGps();
			}

			if (!com.inobix.messangero.common.app_settings.IsDemoVersion)
				new GPSTask().execute();

		}
		// PreferencesUtil.SetRated(MainMenu.this, false);

		if (savedInstanceState != null) {

			boolean showDialog = savedInstanceState.getBoolean("DialogScan");

			if (showDialog) {
				String scanRes = savedInstanceState.getString("ScanResult");
				ShowScanResultDialog(scanRes);
			}
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		outState.putBoolean("ScreenConfig", true);
		if (dialogScanResult != null && dialogScanResult.isShowing()) {
			outState.putBoolean("DialogScan", dialogScanResult.isShowing());
			outState.putString("ScanResult", scanResult);
		}

		super.onSaveInstanceState(outState);
	}

	private void buildAlertMessageNoGps() {
		final AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(
				"Your GPS seems to be disabled, do you want to enable it?")
				.setCancelable(false)
				.setPositiveButton("Yes",
						new DialogInterface.OnClickListener() {
							public void onClick(
									@SuppressWarnings("unused") final DialogInterface dialog,
									@SuppressWarnings("unused") final int id) {
								startActivity(new Intent(
										android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
							}
						})
				.setNegativeButton("No", new DialogInterface.OnClickListener() {
					public void onClick(final DialogInterface dialog,
							@SuppressWarnings("unused") final int id) {
						dialog.cancel();
					}
				});
		final AlertDialog alert = builder.create();
		alert.show();
	}

	public void StartScan() {
		Intent intent = new Intent("com.risoft.zxing.client.android.SCAN");
		intent.putExtra(Intents.Scan.MODE, Intents.Scan.ONE_D_MODE);
		intent.putExtra("Torch", PreferencesUtil.TorchON);
		startActivityForResult(intent, BARCODE_SCANNER_REQUEST_CODE);
	}

	private void RefreshStrings() {

		Intent myIntent = new Intent(MainMenu.this, LoginScreen.class);
		startActivity(myIntent);
		finish();
	}

	private void ShowProgressBar() {
		if (lvMenu == null)
			return;
		View syncView = lvMenu.getChildAt(5);

		if (syncView == null)
			return;

		lvMenu.getAdapter().getView(5, syncView, lvMenu);
		((ProgressBar) syncView.findViewById(R.id.pbSync))
				.setVisibility(ProgressBar.VISIBLE);

	}

	private void HideProgressBar() {
		if (lvMenu == null)
			return;
		View syncView = lvMenu.getChildAt(5);
		if (syncView == null)
			return;
		lvMenu.getAdapter().getView(5, syncView, lvMenu);
		((ProgressBar) syncView.findViewById(R.id.pbSync))
				.setVisibility(ProgressBar.GONE);
	}

	private void DoLogout() {

		/*
		 * PendingIntent senderstop1 =
		 * PendingIntent.getService(getApplicationContext(),
		 * SYNC_GPS_REQUEST_CODE, intentstop1, 0); if (senderstop1 != null) {
		 * AlarmManager alarmManagerstop1 = (AlarmManager)
		 * getSystemService(ALARM_SERVICE);
		 * alarmManagerstop1.cancel(senderstop1); }
		 */

		Intent intentstop1 = new Intent(getApplicationContext(),
				GPSService.class);
		// Intent intentstop1 = new Intent(getApplicationContext(),
		// GPSServiceLocManager.class);
		stopService(intentstop1);
		Log.d("Messangero", "Do Logout");

		if (locationclient != null && locationclient.isConnected()) {
			locationclient.removeLocationUpdates(this);
			locationclient.disconnect();
		}

		// boolean stopGPSServiceResult = stopService(intentstop1);

		/*
		 * if(stopGPSServiceResult) { Log.d("Messangero",
		 * "GPS Service Stopped"); } else { Log.d("Messangero",
		 * "GPS Service NOT Stopped"); }
		 */

		Intent intentstopDataService = new Intent(getApplicationContext(),
				LocalService.class);
		PendingIntent senderstopDataService = PendingIntent.getService(
				getApplicationContext(), DBUtil.SYNC_VOUCHERS_REQUEST_CODE,
				intentstopDataService, 0);
		if (senderstopDataService != null) {
			AlarmManager alarmManagerstopDataService = (AlarmManager) getApplication()
					.getSystemService(Context.ALARM_SERVICE);
			alarmManagerstopDataService.cancel(senderstopDataService);
		}

		PreferencesUtil.SaveCredentials(this, "", "");
		GCMIntentService.ClearNoConnectionNotification(getApplicationContext());
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {

		case BARCODE_SCANNER_REQUEST_CODE:// IntentIntegrator.REQUEST_CODE:
		{
			if (resultCode != RESULT_CANCELED) {
				// IntentResult scanResult =
				// IntentIntegrator.parseActivityResult(
				// requestCode, resultCode, data);
				String number = data.getStringExtra("SCAN_RESULT");
				HandleScanResult(number);
			}
			break;
		}

		case SETTINGS_REQUEST_CODE: {
			if (resultCode == Settings.LOCALE_CHANGED) {
				// redraw ui
				RefreshStrings();
			}
			break;
		}

		}
	}

	private void HandleScanResult(String number) {
		Courier c = new Courier();
		try {
			c.LoadCourier(this, number);
			if (c.getCourierID() == 0) {
				ShowScanResultDialog(number);
			} else
				OpenDetailsScreen(c.getCourierID());

		} catch (ULjException e) {
			Crashlytics.logException(e);
		}
	}

	Dialog dialogScanResult;

	private void ShowScanResultDialog(String scanRes) {
		dialogScanResult = new Dialog(this, R.style.cust_dialog);
		dialogScanResult.setContentView(R.layout.dialog_scan_result);
		ListView lvScanResult = (ListView) dialogScanResult
				.findViewById(R.id.lvScanResult);
		// TextView lblScanResult = (TextView) dialogScanResult
		// .findViewById(R.id.lblScanResult);
		// lblScanResult.setText(scanRes);
		dialogScanResult.setTitle(scanRes);
		scanResult = scanRes;
		ArrayList<ScanResult> lstOptions = new ArrayList<ScanResult>();
		lstOptions.add(new ScanResult(1, getResources().getString(
				R.string.miScanAgain)));
		lstOptions.add(new ScanResult(2, getResources().getString(
				R.string.miEnterManually)));
		lstOptions.add(new ScanResult(3, getResources().getString(
				R.string.miAddNewVoucher)));
		AdapterScanResult adapter = new AdapterScanResult(this, 0, lstOptions);
		lvScanResult.setAdapter(adapter);
		lvScanResult.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				if (arg2 == 0) {
					dialogScanResult.dismiss();
					StartScan();
				} else if (arg2 == 1) {
					// enter number manually
					dialogScanResult.dismiss();
					ShowEditDialog();
				} else if (arg2 == 2) {
					// add new voucher with that number
					dialogScanResult.dismiss();
					Courier c = new Courier();
					c.setVouchNumber(scanResult);
					c.setDateCreated(new Date());
					c.setTransDate(new Date());
					c.setVouchDate(new Date());
					c.setDriverID((float) PreferencesUtil
							.GetDriverID(MainMenu.this));
					try {
						c.InsertCourier(MainMenu.this);
					} catch (ULjException e1) {
						Crashlytics.logException(e1);
					}

					OpenDetailsScreen(c.getCourierID());
				}
			}
		});

		dialogScanResult.show();

	}

	EditText tbNumber;
	Dialog dialog;

	private void ShowEditDialog() {
		dialog = new Dialog(this, R.style.cust_dialog);
		dialog.setTitle(getResources().getString(R.string.EnterNumberDialog));
		dialog.setContentView(R.layout.enter_number_dialog);
		dialog.getWindow().setLayout(LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT);

		Button btnOK = (Button) dialog.findViewById(R.id.btnOK);
		Button btnCancel = (Button) dialog.findViewById(R.id.btnCancel);
		tbNumber = (EditText) dialog.findViewById(R.id.tbNewVoucherNumber);

		dialog.setOnShowListener(new OnShowListener() {

			public void onShow(DialogInterface dialog) {
				// TODO Auto-generated method stub
				getWindow()
						.setSoftInputMode(
								WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
				tbNumber.selectAll();
				tbNumber.postDelayed(new Runnable() {
					public void run() {
						InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
						imm.showSoftInput(tbNumber,
								InputMethodManager.SHOW_FORCED);
					}
				}, 100);
			}
		});

		btnOK.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				String number = tbNumber.getText().toString();
				HandleScanResult(number);

				((InputMethodManager) MainMenu.this
						.getSystemService(Context.INPUT_METHOD_SERVICE))
						.hideSoftInputFromWindow(tbNumber.getWindowToken(), 0);

				dialog.cancel();
			}
		});

		btnCancel.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				((InputMethodManager) MainMenu.this
						.getSystemService(Context.INPUT_METHOD_SERVICE))
						.hideSoftInputFromWindow(tbNumber.getWindowToken(), 0);
				dialog.cancel();
			}
		});
		dialog.show();
	}

	private void UpdateVouchersDate() {
		try {
			// dbUtil = new DBUtil(this);
			// _conn = dbUtil.get_conn();
			Connection conn = DBUtil.CreateConnection(this);

			Calendar calendar = Calendar.getInstance();
			calendar.set(Calendar.HOUR_OF_DAY, 12);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
			calendar.set(Calendar.MILLISECOND, 0);

			Date currentSearchDate = calendar.getTime();

			// select all vouchers for today command
			/*
			 * StringBuffer sb = new StringBuffer( "SELECT " +
			 * "sum(case when DocType <> '5' then 1 else 0 end), " +
			 * "sum(case when DocType = '5' then 1 else 0 end)" +
			 * " FROM RemoteCourier WHERE DriverID = ? AND \"TransDate\" >= ? AND \"TransDate\" < ? AND TransType in ('2','18','4') "
			 * );
			 */

			StringBuffer sb = new StringBuffer(
					"UPDATE RemoteCourier SET VouchDate = ?, TransDate = ? ");

			PreparedStatement ps = conn.prepareStatement(sb.toString());
			// start date
			ps.set(1, currentSearchDate);
			ps.set(2, currentSearchDate);

			// ps.executeQuery();
			ps.execute();

			ps.close();

			conn.commit();
			conn.release();

		} catch (ULjException ex) {
			// tbMessage.setText(ex.getMessage());
			ex.printStackTrace();
		}
	}

	private void LoadVouchersCount() {
		HideProgressBar();

		// loads the count of vouchers left to deliver
		// and shows them in menu next to caption
		Connection conn = DBUtil.CreateConnection(this.getApplicationContext());
		try {
			// dbUtil = new DBUtil(this);
			// _conn = dbUtil.get_conn();

			if (conn == null)
				return;
			
			Calendar calendar = GregorianCalendar.getInstance();
			calendar.setTime(app_settings.WorkDate);
			calendar.set(Calendar.HOUR_OF_DAY, 0);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
			calendar.set(Calendar.MILLISECOND, 0);

			Date currentSearchDate = calendar.getTime();
			//Date currentSearchDate = app_settings.WorkDate;

			// select all vouchers for today command
			StringBuffer sb = new StringBuffer(
					"SELECT "
							+ "sum(case when DocType <> '5' AND TransType in ('2','18','4') then 1 else 0 end), "
							+ "sum(case when DocType = '5' AND TransType in ('2','18','4') then 1 else 0 end),"
							+ "sum(case when DocType <> '5' AND TransType in ('3','5','17','20') then 1 else 0 end),"
							+ "sum(case when DocType = '5' AND TransType in ('3','5','17','20') then 1 else 0 end),"
							+ "sum(case when DocType = '5' AND TransType in ('2') then 1 else 0 end)"
							+ " FROM RemoteCourier WHERE DriverID = ? AND \"TransDate\" >= ? AND \"TransDate\" < ?  ");

			Date dateFrom = currentSearchDate;
			Date dateTo = Base.addDay(currentSearchDate, 1);

			PreparedStatement ps = conn.prepareStatement(sb.toString());

			// driver id
			ps.set(1, app_settings.CurrentDriverID);
			// start date
			ps.set(2, dateFrom);
			// end date
			ps.set(3, dateTo);

			ResultSet rs = ps.executeQuery();

			// reads vouchers into array
			if (rs.next()) {
				vouchersCount = rs.getInt(1);
				ordersCount = rs.getInt(2);
				vouchersCountDelviered = rs.getInt(3);
				ordersCountDelviered = rs.getInt(4);
				ordersPending = rs.getInt(5);
			}
			rs.close();
			ps.close();

		} catch (ULjException ex) {
			// tbMessage.setText(ex.getMessage());
			ex.printStackTrace();
			// miTodayVouchers.setCaption("Error");
			// miTodayOrders.setCaption("Error");
		} finally {
			try {
				if (conn != null)
					conn.release();
			} catch (ULjException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		lblVouchersToDeliver.setText(lblVouchersToDeliverText
				+ String.valueOf(vouchersCount));
		lblVouchersDelivered.setText(lblVouchersDeliveredText
				+ String.valueOf(vouchersCountDelviered));

		lblOrdersPending.setText(lblOrdersPendingText
				+ String.valueOf(ordersPending));
		lblOrdersToDeliver.setText(lblOrdersToDeliverText
				+ String.valueOf(ordersCount));
		lblOrdersDelivered.setText(lblOrdersDeliveredText
				+ String.valueOf(ordersCountDelviered));

		/*
		 * miTodayVouchers.setCaption(getString(R.string.miTodayVouchers) + "("
		 * + String.valueOf(vouchersCount) + ")");
		 * miTodayOrders.setCaption(getString(R.string.miTodayOrders) + "(" +
		 * String.valueOf(ordersCount) + ")");
		 */
		// lvMenu.setAdapter(lvAdapter);
	}

	private void OpenMenuScreen() {
		Intent i = new Intent(MainMenu.this, MainMenu.class);
		startActivity(i);
		finish();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.settings_menu, menu);
		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		//if (app_settings.IsDemoVersion == false)
			//menu.findItem(R.id.miResetDB).setVisible(false);

		menu.findItem(R.id.miSettings).setTitle(R.string.lblSettings);
		menu.findItem(R.id.miResetDB).setTitle(R.string.miResetDB);
		menu.findItem(R.id.miCallOffice).setTitle(R.string.miCallOffice);
		menu.findItem(R.id.miExit).setTitle(R.string.lblExit);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.miSettings:
			Intent i = new Intent(MainMenu.this, Settings.class);
			startActivityForResult(i, SETTINGS_REQUEST_CODE);
			break;
		case R.id.miExit:
			DoLogout();
			finish();
			break;
		case R.id.miCallOffice:
			CallOffice();
			break;
		case R.id.miResetDB:
			ResetDatabase();
			/*if (app_settings.IsDemoVersion) {
				ResetDatabase();
			}*/
			break;
		}
		return true;
	}

	private void ResetDatabase() {
		if(!app_settings.IsDemoVersion)
		{
			//reset database on demo version
			DBUtil.CopyFirstTimeDatabase(MainMenu.this);
		}
		else
		{
			//reset database on full version
			DBUtil.DeleteDatabase(MainMenu.this);
		}
		
		AlarmManager mgr = (AlarmManager) this
				.getSystemService(Context.ALARM_SERVICE);

		PendingIntent RESTART_INTENT = PendingIntent.getActivity(this
				.getBaseContext(), 0, new Intent(MainMenu.this,
				LoginScreen.class), getIntent().getFlags());
		mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 1000,
				RESTART_INTENT);
		System.exit(2);	

		
	}

	Dialog rateDialog;

	@SuppressWarnings("unused")
	@Override
	public void finish() {
		if (app_settings.IsDemoVersion == true) {
			if (PreferencesUtil.IsRated == false) {
				rateDialog = new Dialog(this, R.style.cust_dialog);
				rateDialog.setTitle("Messangero Mobile");
				rateDialog.setContentView(R.layout.rate_dialog);

				Button btnClose = (Button) rateDialog
						.findViewById(R.id.btnClose);
				btnClose.setOnClickListener(new OnClickListener() {

					public void onClick(View v) {

						Intent i = new Intent(Intent.ACTION_VIEW);
						i.setData(Uri.parse("market://details?id="
								+ getPackageName()));
						startActivity(i);
						PreferencesUtil.SetRated(MainMenu.this, true);

						rateDialog.cancel();
						finish();
					}
				});

				rateDialog.show();

			} else
				super.finish();
		} else
			super.finish();
	};

	private void CallOffice() {
		if (app_settings.CompanyPhoneNumber == null
				|| app_settings.CompanyPhoneNumber.equals(""))
			Companies.LoadCompanyPhone(this);

		if (app_settings.CompanyPhoneNumber != "") {
			Uri uri = Uri.fromParts("tel", app_settings.CompanyPhoneNumber,
					null);
			Intent callIntent = new Intent(Intent.ACTION_CALL, uri);
			callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(callIntent);
		}
	}

	private void OpenDetailsScreen(Float voucherID) {
		if (voucherID == null)
			return;

		// Intent i = new Intent(MainMenu.this, CourierDetails.class);
		Intent i = new Intent(MainMenu.this, VoucherDelivery.class);
		i.putExtra("CourierID", voucherID);
		startActivity(i);
	}

	public class GPSTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... arg0) {
			// TODO Auto-generated method stub

			startService(new Intent(getApplicationContext(), GPSService.class));
			// startService(new Intent(getApplicationContext(),
			// GPSServiceLocManager.class));

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
		}
	}

	boolean IsSyncPressed = false;

	public class SyncTask extends AsyncTask<Void, Void, Void> {

		/**
		 * The system calls this to perform work in the UI thread and delivers
		 * the result from doInBackground()
		 */
		@Override
		protected void onPostExecute(Void result) {
			if (IsSyncPressed)
				Toast.makeText(
						getApplicationContext(),
						getApplicationContext().getResources().getString(
								R.string.toast_sync_completed),
						Toast.LENGTH_SHORT).show();
			IsSyncPressed = false;

			LoadVouchersCount();
			actionBar.HideProgressBar();
		};

		@Override
		protected Void doInBackground(Void... params) {
			DBUtil.SyncVouchers(MainMenu.this);
			DBUtil.SyncGPS(MainMenu.this);
			return null;
		}
	}

	public void LeftButtonCLicked(View v) {
		// TODO Auto-generated method stub

	}

	public void RightButtonCLicked(View v) {
		if (!app_settings.IsDemoVersion) {
			actionBar.ShowProgressBar();
			new SyncTask().execute();
		}
		// DBUtil.SyncVouchers(MainMenu.this);
		else {
			actionBar.ShowProgressBar();
			Handler taskHandler = new Handler();

			taskHandler.postDelayed(new Runnable() {
				public void run() {
					actionBar.HideProgressBar();
				}
			}, 3000);

		}
	}

	public void onConnectionFailed(ConnectionResult arg0) {
		// TODO Auto-generated method stub
		Log.d("Messangero", "Connection Failed");
	}

	public void onConnected(Bundle arg0) {
		Log.d("Messangero", "On Connect");
		// TODO Auto-generated method stub
		locationrequest = new LocationRequest();
		locationrequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
		// locationrequest.setInterval(5000);
		locationrequest.setInterval(PreferencesUtil.GPSSyncPeriod);
		locationclient.requestLocationUpdates(locationrequest, this);

	}

	public void onDisconnected() {
		Log.d("Messangero", "On Disconnect");
		// TODO Auto-generated method stub
		if (locationclient != null)
			locationclient.removeLocationUpdates(this);

	}

	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub

		Log.d("Messangero", "Location Changed");
		Thread thr = new Thread(new LocationUpdateThread(MainMenu.this,
				location));
		thr.start();

	}

	private class SyncUsersTask extends AsyncTask<Context, Void, Void> {

		/**
		 * The system calls this to perform work in the UI thread and delivers
		 * the result from doInBackground()
		 */
		@Override
		protected void onPostExecute(Void result) {

		};

		@Override
		protected Void doInBackground(Context... params) {
			DBUtil.SyncUsers(params[0]);
			return null;
		}
	}

	@Override
	public void RightButton1CLicked(View v) 
	{
		
	}

}
