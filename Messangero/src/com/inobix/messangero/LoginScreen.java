package com.inobix.messangero;

import com.crashlytics.android.Crashlytics;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.util.Date;
import java.util.Locale;

import com.google.android.gcm.GCMRegistrar;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.inobix.messangero.Base;
import com.inobix.messangero.R;
import com.inobix.messangero.common.app_settings;
import com.newrelic.agent.android.NewRelic;
import com.ianywhere.ultralitejni12.Connection;
import com.ianywhere.ultralitejni12.PreparedStatement;
import com.ianywhere.ultralitejni12.ResultSet;
import com.ianywhere.ultralitejni12.SyncParms;
import com.ianywhere.ultralitejni12.ULjException;
import com.loopj.android.http.AsyncHttpResponseHandler;

import DAL.DBUtil;
import DAL.User;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;

public class LoginScreen extends Activity {
	private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
	private static final int SETTINGS_REQUEST_CODE = 34543;
	GoogleCloudMessaging gcm;

	DBUtil dbUtil;

	EditText tbUserName;
	EditText tbPassword;

	boolean init = true;

	@Override
	protected void onResume() {
		super.onResume();
		init = true;
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// PreferencesUtil.LanguageID = 2;
		Base.LoadLocale(this);
		Crashlytics.start(this);

		setContentView(R.layout.login_screen);

		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);

		NewRelic.withApplicationToken(
				"AA9ff0289691d009e4b7a4934d0266a93f84336d1d").start(
				this.getApplication());

		tbUserName = (EditText) findViewById(R.id.tbUserName);
		tbPassword = (EditText) findViewById(R.id.tbPassword);

		if (app_settings.IsDemoVersion) {
			tbUserName.setText("test");
			tbPassword.setText("test");
		}

		try {
			PreferencesUtil.LoadSettings(this);

			app_settings.WorkDate = new Date();

			dbUtil = new DBUtil();
			dbUtil.LoadDatabaseFirstTime(this);
			// dbUtil.CreateDatabase();

			// CopyDatabase();

			if (!com.inobix.messangero.common.app_settings.IsDemoVersion) {
				// StartSync();
				new SyncTask().execute();
			}
			boolean isLoginSuccessful = false;
			if (PreferencesUtil.Password != null
					&& PreferencesUtil.Password.equals("") == false
					&& PreferencesUtil.UserName != null
					&& PreferencesUtil.UserName.equals("") == false) {
				isLoginSuccessful = DoLogin(PreferencesUtil.UserName,
						PreferencesUtil.Password);
			}
			if (isLoginSuccessful)
				return;
			if (PreferencesUtil.LanguageID == -1) {
				Spinner spLanguage = (Spinner) findViewById(R.id.spLanguage);
				ArrayAdapter adapterLanguages = ArrayAdapter
						.createFromResource(this, R.array.Languages,
								android.R.layout.simple_spinner_item);
				adapterLanguages
						.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

				spLanguage
						.setOnItemSelectedListener(new OnItemSelectedListener() {

							public void onItemSelected(AdapterView<?> parent,
									View view, int pos, long id) {
								if (init) {
									init = false;
									return;
								}
								PreferencesUtil.LanguageID = pos;
								PreferencesUtil.SavePreferences(
										LoginScreen.this, PreferencesUtil.IP,
										PreferencesUtil.Port,
										PreferencesUtil.DataSyncPeriod,
										PreferencesUtil.GPSSyncPeriod,
										PreferencesUtil.SearchAllBarcodeTypes,
										PreferencesUtil.SearchOnlyCode39,
										PreferencesUtil.RotateScreen, pos);

								Locale locale = new Locale(PreferencesUtil
										.GetLocale());
								Locale.setDefault(locale);
								Configuration config = new Configuration();
								config.locale = locale;
								getBaseContext().getResources()
										.updateConfiguration(
												config,
												getBaseContext().getResources()
														.getDisplayMetrics());

								refresh();

							};

							public void onNothingSelected(AdapterView parent) {
								// Do nothing.
							};

						});

				spLanguage.setAdapter(adapterLanguages);

				spLanguage.performClick();

			}

			/*
			 * String loc = "gr"; if (PreferencesUtil.LanguageID != -1) { loc =
			 * PreferencesUtil.GetLocale(); }
			 * 
			 * Locale locale = new Locale(loc); Locale.setDefault(locale);
			 * Configuration config = new Configuration(); config.locale =
			 * locale;
			 * getBaseContext().getResources().updateConfiguration(config,
			 * getBaseContext().getResources().getDisplayMetrics());
			 */

			// DBUtil.AlterRemoteCourierTable();

			// when program is started synchronizes the vouchers
			// demo version does not supper sync

			Base.LoadLocale(this);

		} catch (ULjException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {

		}

	}

	private void refresh() {
		finish();
		Intent myIntent = new Intent(LoginScreen.this, LoginScreen.class);
		startActivity(myIntent);
	}

	private void CopyDatabase() {
		try {
			File data = Environment.getDataDirectory();
			File sdcard = Environment.getExternalStorageDirectory();
			File imgs = Environment
					.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

			// File wallpaperDirectory = new File(sdcard.getPath() +
			// "/Wallpapers1/");
			// wallpaperDirectory.mkdirs();
			// if (sd.canWrite()) {
			String currentDBPath = "/data/com.inobix.messangero/courierdb.udb";
			String backupDBPath = "zaek2.udb";
			File currentDB = new File(data, currentDBPath);
			File backupDB = new File(imgs, backupDBPath);

			if (currentDB.exists()) {
				FileChannel src = new FileInputStream(currentDB).getChannel();
				FileChannel dst = new FileOutputStream(backupDB).getChannel();
				dst.transferFrom(src, 0, src.size());
				src.close();
				dst.close();
				// }
			}
		} catch (Exception e) {
			int a = 0;
		}

	}

	public void onBtnLoginClick(View v) {
		EditText tbUserName = (EditText) findViewById(R.id.tbUserName);
		EditText tbPassword = (EditText) findViewById(R.id.tbPassword);
		DoLogin(tbUserName.getText().toString(), tbPassword.getText()
				.toString());
	}

	private boolean DoLogin(String username, String password) {
		boolean isLoggedIn = false;
		Connection _conn;

		_conn = DBUtil.CreateConnection(this);
		if (_conn == null)
			return false;

		PreparedStatement ps;
		boolean isLoginSuccessful = false;
		try {
			ps = _conn
					.prepareStatement(" SELECT * FROM rUsers WHERE LoginName = ? AND UserPass = ?");
			ps.set(1, username);
			ps.set(2, password);

			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				app_settings.CurrentUserID = rs.getInt("UserID");
				app_settings.CurrentDriverID = rs.getInt("DriverID");
				PreferencesUtil.SetDriverID(this, app_settings.CurrentDriverID);
				isLoginSuccessful = true;
				isLoggedIn = true;
				PreferencesUtil.SaveCredentials(this, username, password);
			}
			rs.close();
			ps.close();
		} catch (ULjException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				_conn.release();
			} catch (ULjException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		TextView tvError = (TextView) findViewById(R.id.lblWrongInput);
		if (tvError != null) {
			if (isLoginSuccessful) {
				// open menu screen
				app_settings.WorkDate = new Date();
				tvError.setVisibility(TextView.INVISIBLE);
				OpenMenuScreen();
			} else {
				// show error label
				tvError.setVisibility(TextView.VISIBLE);
			}
		}
		return isLoggedIn;
	}

	private void OpenMenuScreen() {
		Intent i = new Intent(LoginScreen.this, MainMenu.class);
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
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.miSettings:
			Intent i = new Intent(LoginScreen.this, Settings.class);
			i.putExtra("IsLogin", true);
			startActivityForResult(i, SETTINGS_REQUEST_CODE);
			break;
		case R.id.miExit:
			finish();
			break;
		case R.id.miResetDB:
			DBUtil.ResetDatabase(this);
			break;
		}
		return true;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == SETTINGS_REQUEST_CODE) {
			if (resultCode == Settings.RESULT_IP_PORT_CHANGED) {
				// ip or port changed, sync users again
				if (!com.inobix.messangero.common.app_settings.IsDemoVersion) {
					Log.d("Messangero", "Sync Users");
					new SyncTask().execute();
				}
			}
		}
	}

	private void StartSync() {
		try {
			Connection conn = DBUtil.CreateConnection(this);
			try {
				SyncParms syncParms = conn.createSyncParms(
						SyncParms.HTTP_STREAM, "sa", PreferencesUtil.SyncModel);
				syncParms.getStreamParms().setHost(PreferencesUtil.IP);
				syncParms.getStreamParms().setPort(PreferencesUtil.Port);
				syncParms.setAuthenticationParms("0,0");
				syncParms.setPublications("pblUsers");
				syncParms.setLivenessTimeout(10);

				conn.synchronize(syncParms);
			} finally {
				conn.release();
			}
			// SyncResult result = syncParms.getSyncResult();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private class SyncTask extends AsyncTask<Void, Void, Void> {

		/**
		 * The system calls this to perform work in the UI thread and delivers
		 * the result from doInBackground()
		 */
		@Override
		protected void onPostExecute(Void result) {

		};

		@Override
		protected Void doInBackground(Void... params) {
			StartSync();
			return null;
		}
	}

}
