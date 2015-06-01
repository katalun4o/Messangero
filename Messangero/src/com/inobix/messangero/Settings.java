package com.inobix.messangero;

import java.util.Locale;

import com.inobix.messangero.R;
import com.inobix.messangero.common.IActionActivity;

import com.ianywhere.ultralitejni12.ULjException;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.Spinner;

public class Settings extends Activity implements IActionActivity {
	public static final int DEFAULT_RESULT = 1000;
	public static final int LOCALE_CHANGED = 1012;
	public static final int GPS_SYNC_TIME_CHANGED = 1013;
	public static final int DB_SYNC_TIME_CHANGED = 1014;

	public static final int RESULT_IP_PORT_CHANGED = 1015;

	boolean startedFromLogin = false;

	EditText tbIP;
	EditText tbPort;
	EditText tbSyncModel;
	EditText tbDataSyncPeriod;
	EditText tbGPSSyncPeriod;
	CheckBox chbAllBarcodes;
	CheckBox chbOnlyCode39;
	CheckBox chbTorch;
	CheckBox chbRotateScreen;
	Spinner spLanguage;
	ActionBar actionBar;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Base.LoadLocale(this);
		setContentView(R.layout.settings);

		actionBar = (ActionBar) findViewById(R.id.layoutActionBar);
		actionBar
				.SetRightButtonText(getResources().getString(R.string.btnDone));
		actionBar.SetLeftButtonText("");

		tbIP = (EditText) findViewById(R.id.tbIP);
		tbPort = (EditText) findViewById(R.id.tbPort);
		tbSyncModel = (EditText) findViewById(R.id.tbSyncModel);
		tbDataSyncPeriod = (EditText) findViewById(R.id.tbDataSyncPeriod);
		tbGPSSyncPeriod = (EditText) findViewById(R.id.tbGPSSyncPeriod);
		chbAllBarcodes = (CheckBox) findViewById(R.id.chbAllBarcodes);
		chbOnlyCode39 = (CheckBox) findViewById(R.id.chbOnlyCode39);
		chbTorch= (CheckBox) findViewById(R.id.chTorch);
		spLanguage = (Spinner) findViewById(R.id.spLanguage);
		chbRotateScreen = (CheckBox) findViewById(R.id.chbRotateScreen);

		startedFromLogin = getIntent().getBooleanExtra("IsLogin", false);

		ArrayAdapter adapterLanguages = ArrayAdapter.createFromResource(this,
				R.array.Languages, android.R.layout.simple_spinner_item);
		adapterLanguages
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spLanguage.setAdapter(adapterLanguages);

		tbIP.setText(PreferencesUtil.IP);
		tbPort.setText(PreferencesUtil.Port.toString());
		tbSyncModel.setText(PreferencesUtil.SyncModel);

		tbDataSyncPeriod.setText(PreferencesUtil.DataSyncPeriod.toString());
		tbGPSSyncPeriod.setText(PreferencesUtil.GPSSyncPeriod.toString());
		chbAllBarcodes.setChecked(PreferencesUtil.SearchAllBarcodeTypes);
		chbOnlyCode39.setChecked(PreferencesUtil.SearchOnlyCode39);
		chbRotateScreen.setChecked(PreferencesUtil.RotateScreen);
		chbTorch.setChecked(PreferencesUtil.TorchON);
		spLanguage.setSelection(PreferencesUtil.LanguageID);

		chbAllBarcodes
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					public void onCheckedChanged(CompoundButton arg0,
							boolean arg1) {
						// TODO Auto-generated method stub
						if (arg1) {
							if (chbOnlyCode39.isChecked() == true)
								chbOnlyCode39.setChecked(false);
						} else {
							if (chbOnlyCode39.isChecked() == false)
								chbOnlyCode39.setChecked(true);
						}
					}
				});

		chbOnlyCode39.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				// TODO Auto-generated method stub
				if (arg1) {
					if (chbAllBarcodes.isChecked() == true)
						chbAllBarcodes.setChecked(false);
				} else {
					if (chbAllBarcodes.isChecked() == false)
						chbAllBarcodes.setChecked(true);
				}
			}
		});

	}

	private void Save() {
		String ip = tbIP.getText().toString().trim();
		String port = tbPort.getText().toString().trim();
		String dataSyncPeriod = tbDataSyncPeriod.getText().toString();
		String gpsSyncPeriod = tbGPSSyncPeriod.getText().toString();
		String syncModel = tbSyncModel.getText().toString();

		boolean allBarcodes = chbAllBarcodes.isChecked();
		boolean onlyBarcodeCode39 = chbOnlyCode39.isChecked();
		boolean rotateScreen = chbRotateScreen.isChecked();
		boolean torchOn = chbTorch.isChecked();
		PreferencesUtil.TorchON = torchOn;
		int languageID = spLanguage.getSelectedItemPosition();

		boolean changeLocale = false;
		if (PreferencesUtil.LanguageID != languageID)
			changeLocale = true;

		boolean syncSettingsChanged = false;
		if (PreferencesUtil.IP.equals(ip) == false
				|| PreferencesUtil.Port.equals(Integer.valueOf(port)) == false
				|| PreferencesUtil.SyncModel.equals(syncModel) == false)
			syncSettingsChanged = true;

		PreferencesUtil.SyncModel = syncModel;

		PreferencesUtil.SavePreferences(this, ip, Integer.valueOf(port),
				Integer.valueOf(dataSyncPeriod),
				Integer.valueOf(gpsSyncPeriod), allBarcodes, onlyBarcodeCode39,rotateScreen,
				languageID);

		if (changeLocale) {
			Locale locale = new Locale(PreferencesUtil.GetLocale());
			Locale.setDefault(locale);
			Configuration config = new Configuration();
			config.locale = locale;
			getBaseContext().getResources().updateConfiguration(config,
					getBaseContext().getResources().getDisplayMetrics());

			if (startedFromLogin && syncSettingsChanged)
				setResult(RESULT_IP_PORT_CHANGED);
			else
				setResult(LOCALE_CHANGED);
		} else {
			if (startedFromLogin && syncSettingsChanged)
				setResult(RESULT_IP_PORT_CHANGED);
			else
				setResult(DEFAULT_RESULT);
		}

		/*
		 * if (changeLocale) { AlarmManager mgr =
		 * (AlarmManager)this.getSystemService(Context.ALARM_SERVICE);
		 * PendingIntent RESTART_INTENT =
		 * PendingIntent.getActivity(this.getBaseContext(), 0, new
		 * Intent(Settings.this, LoginScreen.class), getIntent().getFlags());
		 * mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 1000,
		 * RESTART_INTENT); System.exit(2); }
		 */

		finish();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	public void LeftButtonCLicked(View v) {

	}

	public void RightButtonCLicked(View v) {
		Save();
	}

	@Override
	public void RightButton1CLicked(View v) {
		// TODO Auto-generated method stub
		
	}
}
