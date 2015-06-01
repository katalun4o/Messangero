package com.inobix.messangero;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import com.ianywhere.ultralitejni12.Connection;
import com.ianywhere.ultralitejni12.PreparedStatement;
import com.ianywhere.ultralitejni12.ResultSet;
import com.ianywhere.ultralitejni12.ULjException;
import com.inobix.messangero.common.IActionActivity;
import com.inobix.messangero.common.app_settings;

import DAL.Courier;
import DAL.DBUtil;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.DateTimeKeyListener;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;

public class SearchScreen extends Activity implements IActionActivity {
	static final int DATE_DIALOG_ID = 0;

	private int mYear;
	private int mMonth;
	private int mDay;
	Calendar calendar;
	private Date currentDateFrom;
	private Date currentDateTo;
	private int currentSearchVoucherType;
	private int currentSearchDocType;
	private int currentSearchReportType;
	private TextView lblDateFrom;
	private TextView lblDateTo;
	private EditText tbNumber;
	
	private TextView lblReportType;
	private TextView lblVoucherType;
	private TextView lblDocType;

	Spinner spReportType;
	Spinner spType;
	Spinner spDocType;
	ActionBar actionBar;
	Button btnReportType;
	Button btnDocType;
	Button btnVouchType;

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();

		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
		tbNumber.selectAll();
		tbNumber.postDelayed(new Runnable() {
			public void run() {
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.showSoftInput(tbNumber, InputMethodManager.SHOW_FORCED);

				Log.d("MDelivery", "on Start Post Delayed: "
						+ tbNumber.getText().toString());
			}
		}, 100);
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
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
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();

		((InputMethodManager) SearchScreen.this
				.getSystemService(Context.INPUT_METHOD_SERVICE))
				.hideSoftInputFromWindow(tbNumber.getWindowToken(), 0);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Base.LoadLocale(this);
		setContentView(R.layout.search_screen);

		actionBar = (ActionBar) findViewById(R.id.layoutActionBar);

		actionBar.SetRightButtonText("Search");

		lblDateFrom = (TextView) this.findViewById(R.id.lblDateFromValue);
		lblDateTo = (TextView) this.findViewById(R.id.lblDateToValue);
		tbNumber = (EditText) this.findViewById(R.id.tbNumber);
		spReportType = (Spinner) this.findViewById(R.id.spReportType);
		spType = (Spinner) this.findViewById(R.id.spType);
		spDocType = (Spinner) this.findViewById(R.id.spDocType);

		btnReportType = (Button) this.findViewById(R.id.btnReportType);
		btnDocType = (Button) this.findViewById(R.id.btnDocType);
		btnVouchType = (Button) this.findViewById(R.id.btnVoucherType);
		
		lblReportType = (TextView) this.findViewById(R.id.tbReportType);
		lblDocType = (TextView) this.findViewById(R.id.tbDocType);
		lblVoucherType = (TextView) this.findViewById(R.id.tbVoucherType);

		btnReportType.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				spReportType.performClick();
			}
		});

		btnDocType.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				spDocType.performClick();
			}
		});

		btnVouchType.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				spType.performClick();
			}
		});

		calendar = Calendar.getInstance();
		calendar.setTime(app_settings.WorkDate);
		mYear = calendar.get(Calendar.YEAR);
		mMonth = calendar.get(Calendar.MONTH);
		mDay = calendar.get(Calendar.DAY_OF_MONTH);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);

		currentDateFrom = new Date();
		currentDateTo = new Date();

		int dayOfMonth1 = calendar.get(Calendar.DAY_OF_MONTH);
		String textFrom = String.valueOf(String.valueOf(dayOfMonth1) + "."
				+ String.format(Locale.ENGLISH, "%tB", calendar) + "."
				+ calendar.get(Calendar.YEAR));
		lblDateFrom.setText(textFrom);
		lblDateTo.setText(textFrom);

		ArrayAdapter adapterReportTypes = ArrayAdapter.createFromResource(this,
				R.array.ReportTypes, android.R.layout.simple_spinner_item);
		adapterReportTypes
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spReportType.setAdapter(adapterReportTypes);
		spReportType.setSelection(0);
		
		spReportType.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				String item = (String)spReportType.getItemAtPosition(position);
				lblReportType.setText(item);
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub
				
			}
		});
		
		
		
		ArrayAdapter adapter = ArrayAdapter.createFromResource(this,
				R.array.SearchDeliveryTypes,
				android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spType.setAdapter(adapter);
		spType.setSelection(0);

		spType.setOnItemSelectedListener(new OnItemSelectedListener() {

			public void onItemSelected(AdapterView<?> parent, View view,
					int pos, long id) {
				currentSearchVoucherType = pos;
				String item = (String)spType.getItemAtPosition(pos);
				lblVoucherType.setText(item);
			};

			public void onNothingSelected(AdapterView parent) {
				// Do nothing.
			};
		});

		ArrayAdapter adapterDocTypes = ArrayAdapter.createFromResource(this,
				R.array.DocTypes, android.R.layout.simple_spinner_item);
		adapterDocTypes
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spDocType.setAdapter(adapterDocTypes);
		spDocType.setSelection(0);

		spDocType.setOnItemSelectedListener(new OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> parent, View view,
					int pos, long id) {
				currentSearchDocType = pos;
				
				String item = (String)spDocType.getItemAtPosition(pos);
				lblDocType.setText(item);

				currentSearchVoucherType = 0;
				ArrayAdapter adapter = null;

				int deliveryTypesResourceID = 0;
				if (currentSearchDocType == 1 || currentSearchDocType == 2
						|| currentSearchDocType == 3) {
					deliveryTypesResourceID = R.array.SearchDeliveryTypes;
				} else {
					deliveryTypesResourceID = R.array.SearchOrdersTypes;
				}
				adapter = ArrayAdapter.createFromResource(SearchScreen.this,
						deliveryTypesResourceID,
						android.R.layout.simple_spinner_item);
				adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				spType.setAdapter(adapter);
				spType.setSelection(0);

			};

			public void onNothingSelected(AdapterView parent) {
				// Do nothing.
			};
		});
		
		if(spDocType.getSelectedItem() != null)
			lblDocType.setText(spDocType.getSelectedItem().toString());
		
		if(spReportType.getSelectedItem() != null)
			lblReportType.setText(spReportType.getSelectedItem().toString());
		
		if(spType.getSelectedItem() != null)
			lblVoucherType.setText(spType.getSelectedItem().toString());

	}

	View currentDateView = null;

	public void lblDateOnClick(View v) {
		Date cDate = new Date();
		if (v.getId() == R.id.btnChooseDateFrom) {
			currentDateView = lblDateFrom;
			cDate = currentDateFrom;
		} else if (v.getId() == R.id.btnChooseDateTo) {
			currentDateView = lblDateTo;
			cDate = currentDateTo;
		}

		Calendar c = Calendar.getInstance();
		c.setTime(cDate);
		mYear = c.get(Calendar.YEAR);
		mMonth = c.get(Calendar.MONTH);
		mDay = c.get(Calendar.DAY_OF_MONTH);

		DatePickerDialog d = new DatePickerDialog(this, mDateSetListener,
				mYear, mMonth, mDay);
		d.show();

		// showDialog(DATE_DIALOG_ID);
	}

	public void btnChooseTypeOnClick(View v) {
		spType.performClick();
	}

	public void btnSearchOnClick(View v) {
		DoSearch();
	}

	private void DoSearch() {
		Intent i = new Intent(SearchScreen.this, SearchResultScreen.class);

		i.putExtra("VoucherNumber", tbNumber.getText().toString());
		i.putExtra("VouchType", currentSearchVoucherType);
		i.putExtra("DocType", currentSearchDocType);
		// 0 normal, 1 antiaktavoli
		int repPos = spReportType.getSelectedItemPosition();
		i.putExtra("ReportType", repPos);

		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		if (currentDateFrom == null) {
			// currentDateFrom = new Date(1900 - 1900, 0, 1);

			i.putExtra("DateFrom", "0000-00-00");
		} else
			i.putExtra("DateFrom", df.format(currentDateFrom));
		// i.putExtra("DateFrom", currentDateFrom.toString());

		if (currentDateTo == null) {
			// currentDateTo = new Date(9900 - 1900, 0, 1);
			i.putExtra("DateTo", "9000-00-00");
		} else {
			i.putExtra("DateTo", df.format(currentDateTo));
		}
		// i.putExtra("DateTo", currentDateTo.toString());

		startActivity(i);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.search_screen_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.miClear:
			ClearCriteria();
			break;
		}
		return true;
	}

	private void ClearCriteria() {
		currentDateFrom = null;
		currentDateTo = null;
		currentSearchVoucherType = 0;
		spType.setSelection(0);
		lblDateFrom.setText("");
		lblDateTo.setText("");
		tbNumber.setText("");

	}

	private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {

		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			mYear = year;
			mMonth = monthOfYear;
			mDay = dayOfMonth;
			calendar.set(Calendar.YEAR, year);
			calendar.set(Calendar.MONTH, monthOfYear);
			calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
			calendar.set(Calendar.HOUR_OF_DAY, 0);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
			calendar.set(Calendar.MILLISECOND, 0);

			if (currentDateView.getId() == R.id.lblDateFromValue)
				currentDateFrom = calendar.getTime();
			else if (currentDateView.getId() == R.id.lblDateToValue)
				currentDateTo = calendar.getTime();

			int dayOfMonth1 = calendar.get(Calendar.DAY_OF_MONTH);
			String text = String.valueOf(String.valueOf(dayOfMonth1) + "."
					+ String.format(Locale.ENGLISH, "%tB", calendar) + "."
					+ calendar.get(Calendar.YEAR));
			((TextView) currentDateView).setText(text);

		}
	};

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DATE_DIALOG_ID: {

			return new DatePickerDialog(this, mDateSetListener, mYear, mMonth,
					mDay);
		}
		}
		return null;
	}

	@Override
	public void LeftButtonCLicked(View v) {
		// TODO Auto-generated method stub

	}

	@Override
	public void RightButtonCLicked(View v) {
		// TODO Auto-generated method stub
		DoSearch();
	}

	@Override
	public void RightButton1CLicked(View v) 
	{
		
	}

}
