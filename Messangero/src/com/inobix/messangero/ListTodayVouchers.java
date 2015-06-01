package com.inobix.messangero;

import com.example.android.AdapterVouchers;
import com.example.android.DynamicListView;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.ianywhere.ultralitejni12.*;
import com.inobix.messangero.R;
import com.inobix.messangero.common.IActionActivity;
import com.inobix.messangero.common.app_settings;
import com.inobix.messangero.map.MapHelper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import DAL.Courier;
import DAL.DBUtil;
import android.annotation.TargetApi;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;

public class ListTodayVouchers extends FragmentActivity implements
		IActionActivity {
	ActionBar actionBar;
	DBUtil dbUtil;
	private String docType = "";
	private int mYear;
	private int mMonth;
	private int mDay;
	static final int DATE_DIALOG_ID = 0;
	Calendar calendar;
	ProgressBar pbLoading;

	//ListView lvVouchers;
	DynamicListView lvVouchers;
	// MapView mapView;
	com.inobix.messangero.map.MapHelper mapHelper;

	TabHost tabHost;
	String tabMapCaption;
	String tabListCaption;

	TextView lblCurrentSearchDate;
	TextView lblCurrentSearchType;
	
	TextView lblRearrangeHeader;
	LinearLayout layoutRearrangeMenu;

	Spinner spType;

	private int currentSearchVoucherType = 1;
	//private Date currentSearchDate = new Date();
	private boolean isInitializeFinished = false;
	ArrayList<Courier> lstVouchers = new ArrayList<Courier>();

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Base.LoadLocale(this);
		setContentView(R.layout.listtodayvouchers);

		actionBar = (ActionBar) findViewById(R.id.layoutActionBar);

		actionBar.SetLeftButtonText("25 Nov");
		actionBar.SetRightButtonText("Type");

		docType = getIntent().getExtras().getString("DocType");

		SetupMap();

		// init controls
		lblCurrentSearchDate = (TextView) findViewById(R.id.lblCurrentSearchDateVal);
		lblCurrentSearchType = (TextView) findViewById(R.id.lblCurrentSearchTypeVal);
		pbLoading = (ProgressBar)findViewById(R.id.pbLoading);
		lblRearrangeHeader = (TextView)findViewById(R.id.lblRearrangeHeader);
		layoutRearrangeMenu = (LinearLayout)findViewById(R.id.layoutRearrangeMenu);
		Button btnSaveVoucherOrder = (Button)findViewById(R.id.btnSaveVoucherOrder);
		Button btnCancelVoucherOrder = (Button)findViewById(R.id.btnCancelVoucherOrder);
		
		btnSaveVoucherOrder.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//save the new order
				/*for(int i = 0; i < lvVouchers.getCount(); i++)
				{
					Courier curr = ((AdapterVouchers)lvVouchers.getAdapter()).getItem(i);
					curr.OrderIndex = i;
				}*/
				Courier.SaveOrderIndexes(ListTodayVouchers.this, ((AdapterVouchers)lvVouchers.getAdapter()).GetItems());
				CloseRearrange();
				//reload vouchers
				Log.d("Messangero", "LoadVoucherTask save vouchers order");
				LoadVouchersTask();
			}
		});
		
		btnCancelVoucherOrder.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				CloseRearrange();
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

		int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
		actionBar.SetLeftButtonText(String.valueOf(String.valueOf(dayOfMonth)
				+ " "
				+ String.format(Locale.ENGLISH, "%tB", calendar)
						.substring(0, 3) + "."));

		tabHost = (TabHost) findViewById(R.id.tabHost);
		tabHost.setup();
		tabListCaption = getResources().getString(R.string.tabListCaption);
		tabMapCaption = getResources().getString(R.string.tabMapCaption);
		setupTab(tabHost, R.id.tabVouchersList, tabListCaption);
		setupTab(tabHost, R.id.tabVouchersMap, tabMapCaption);

		//currentSearchDate = calendar.getTime();
		app_settings.WorkDate = calendar.getTime();

		//lvVouchers = (ListView) findViewById(R.id.lvVouchers);
		lvVouchers = (DynamicListView) findViewById(R.id.lvVouchers);
		lvVouchers.setIsRearrangeEnabled(false);

		spType = (Spinner) this.findViewById(R.id.spType);

		ArrayAdapter adapter = null;
		if (docType.equals("5")) {
			adapter = ArrayAdapter.createFromResource(this,
					R.array.SearchOrdersTypes,
					android.R.layout.simple_spinner_item);
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			spType.setAdapter(adapter);
		} else {
			adapter = ArrayAdapter.createFromResource(this,
					R.array.SearchDeliveryTypes,
					android.R.layout.simple_spinner_item);
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			spType.setAdapter(adapter);
		}

		spType.setOnItemSelectedListener(new OnItemSelectedListener() {

			public void onItemSelected(AdapterView<?> parent, View view,
					int pos, long id) {

				if (isInitializeFinished == false) {
					isInitializeFinished = true;
					return;
				}

				currentSearchVoucherType = pos;
				lblCurrentSearchType.setText(parent.getItemAtPosition(pos)
						.toString());

				Log.d("Messangero", "LoadVoucherTask type changed");
				LoadVouchersTask();
			};

			public void onNothingSelected(AdapterView parent) {
				// Do nothing.
				
			};

		});

		spType.setSelection(1);

		lvVouchers.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> view, View arg1,
					int itemIndex, long arg3) {
				OpenDetailsScreen(((Courier) view.getItemAtPosition(itemIndex))
						.getCourierID());
			};
		});
		
		tabHost.setOnTabChangedListener(new OnTabChangeListener() {
			
			@Override
			public void onTabChanged(String tabId) {
				if(tabId.equals(tabMapCaption))
				{
					if(areMapVouchersLoaded == false)
						LoadMapMarkers();
				}
			}
		}); 

	}

	
	private void LoadVouchersTask() {
		Log.d("Messangero", "LoadVoucherTask");
		pbLoading.setVisibility(ProgressBar.VISIBLE);
		lvVouchers.setAdapter(new AdapterVouchers(
				ListTodayVouchers.this, R.layout.row, new ArrayList<Courier>()));
		
		LoadTask myTask = new LoadTask() {
			@Override
			protected void onPostExecute(ArrayList<Courier> result) {
				// TODO Auto-generated method stub
				super.onPostExecute(result);

				//lvVouchers.setAdapter(new MyArrayAdapter(
				//		ListTodayVouchers.this, R.layout.row, result));
				
				lvVouchers.setList(result);
				lvVouchers.setAdapter(new AdapterVouchers(
						ListTodayVouchers.this, R.layout.row, result));
				
				ListTodayVouchers.this.lstVouchers = result;
				pbLoading.setVisibility(ProgressBar.GONE);
				areMapVouchersLoaded = false;
			}
		};
		//myTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,currentSearchVoucherType, currentSearchDate);
		myTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,currentSearchVoucherType, app_settings.WorkDate);
		
		/*ArrayList<Courier> lstVouchers = new ArrayList<Courier>();
		lstVouchers = LoadVouchers(currentSearchVoucherType, currentSearchDate);
		
		lvVouchers.setAdapter(new MyArrayAdapter(
				ListTodayVouchers.this, R.layout.row, lstVouchers));
		ListTodayVouchers.this.lstVouchers = lstVouchers;
		pbLoading.setVisibility(ProgressBar.GONE);
		areMapVouchersLoaded = false;
		*/
		/*new LoadTask() {
			@Override
			protected void onPostExecute(ArrayList<Courier> result) {
				// TODO Auto-generated method stub
				super.onPostExecute(result);

				lvVouchers.setAdapter(new MyArrayAdapter(
						ListTodayVouchers.this, R.layout.row, result));
				ListTodayVouchers.this.lstVouchers = result;
				LoadMapMarkers();
				pbLoading.setVisibility(ProgressBar.GONE);
			}
		}.executeOnExecutor(currentSearchVoucherType, currentSearchDate);
		
		ArrayList<Courier> lstVouchers = new ArrayList<Courier>();
		lstVouchers = LoadVouchers(currentSearchVoucherType, currentSearchDate);
		
		lvVouchers.setAdapter(new MyArrayAdapter(
				ListTodayVouchers.this, R.layout.row, lstVouchers));
		ListTodayVouchers.this.lstVouchers = lstVouchers;*/

	}

	SupportMapFragment mapFragment;

	private void SetupMap() {
		FragmentManager fm = getSupportFragmentManager();
		Fragment fMap = fm.findFragmentByTag("MapDialogFragment");
		if (fMap != null) {
			mapFragment = (SupportMapFragment) fMap;
		} else {
			mapFragment = (SupportMapFragment) fm.findFragmentById(R.id.map);
			if (mapFragment == null) {
				mapFragment = SupportMapFragment.newInstance();
				FragmentTransaction ft = fm.beginTransaction();
				ft.add(R.id.map, mapFragment, "MapDialogFragment");
				ft.commit();
				fm.executePendingTransactions();
			}
		}

		actionBar.post(new Runnable() {
			
			public void run() {
				LoadMap();
			}
		});
		
		/*Handler h = new Handler();
		h.postDelayed(new Runnable() {
			public void run() {
				LoadMap();
			}
		}, 500);*/
	}

	GoogleMap gMap;

	boolean isMapLoaded = false;
	boolean AreVouchersLoaded = false;

	private void LoadMap() {
		gMap = mapFragment.getMap();
		if(gMap == null)
			return;
		mapHelper = new MapHelper(this, gMap, this, true);

		mapHelper.AddMarker(PreferencesUtil.Latitude,
				PreferencesUtil.Longitude, "MyLocation", "",
				R.drawable.truck_32_1, 0, 0, 0, "", false);
		mapHelper
				.CenterMap(PreferencesUtil.Latitude, PreferencesUtil.Longitude);

		if (AreVouchersLoaded) {
			for (Courier c : lstVouchers) {

				if (c.Latitude != 0 && c.Longitude != 0) {
					if (docType.equals("5")) {

						mapHelper.AddMarker(c.Latitude, c.Longitude,
								c.getVouchNumber(), c.getSenderAddress(),
								R.drawable.envelope_32, 0, 0, 0, "", false);

					} else {
						mapHelper.AddMarker(c.Latitude, c.Longitude,
								c.getVouchNumber(), c.getReceiverAddress(),
								R.drawable.envelope_32, 0, 0, 0, "", false);
					}
				}
			}
		}
		isMapLoaded = true;
	}

	private void setupTab(TabHost tbHost, final int viewID, final String tag) {
		View tabview = createTabView(tbHost.getContext(), tag);
		TabSpec setContent = tbHost.newTabSpec(tag).setIndicator(tabview)
				.setContent(viewID);
		tbHost.addTab(setContent);
	}

	private static View createTabView(final Context context, final String text) {
		View view = LayoutInflater.from(context).inflate(R.layout.custom_tabs,
				null);
		TextView tv = (TextView) view.findViewById(R.id.tabsText);
		tv.setText(text);
		return view;
	}

	public void btnMyLocation_Click(View v) {

	}

	@Override
	protected void onResume() {
		super.onResume();
		if (docType.equals("5"))
			currentSearchVoucherType = 0;
		// LoadVouchers(currentSearchVoucherType, currentSearchDate);
		Log.d("Messangero", "LoadVoucherTask on resume");
		LoadVouchersTask();
		UpdateDateText();
	};

	private void UpdateDateText() {
		
		calendar = Calendar.getInstance();
		calendar.setTime(app_settings.WorkDate);
		mYear = calendar.get(Calendar.YEAR);
		mMonth = calendar.get(Calendar.MONTH);
		mDay = calendar.get(Calendar.DAY_OF_MONTH);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);

		int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
		actionBar.SetLeftButtonText(String.valueOf(String.valueOf(dayOfMonth)
				+ " "
				+ String.format(Locale.ENGLISH, "%tB", calendar)
						.substring(0, 3) + "."));
		
		//int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
		//lblCurrentSearchDate.setText(String.valueOf(String.valueOf(dayOfMonth)
		//		+ "." + String.format(Locale.ENGLISH, "%tB", calendar) + "."
		//		+ calendar.get(Calendar.YEAR)));

		//actionBar.SetLeftButtonText(String.valueOf(String.valueOf(dayOfMonth)
			//	+ " "
				//+ String.format(Locale.ENGLISH, "%tB", calendar)
					//	.substring(0, 3) + "."));

	}

	private ArrayList<Courier> LoadVouchers(int type, Date date) {
		ArrayList<Courier> lstVouchersRes = new ArrayList<Courier>();
		try {
			// dbUtil = new DBUtil(this);
			// _conn = dbUtil.get_conn();
			
			Connection conn = DBUtil.CreateConnection(this);

			String statusString = "4";
			if (type == 0)
				statusString = "2,3,4,5,6,13,17,18,19,20";
			else {
				if (docType.equals("5")) {
					switch (type) {
					case 1:
						statusString = "2";
						break;
					case 2:
						statusString = "18";
						break;
					case 3:
						statusString = "19";
						break;
					case 4:
						statusString = "20";
						break;
					case 5:
						statusString = "3";
						break;
					}
				} else {
					switch (type) {
					case 1:
						statusString = "4";
						break;
					case 2:
						statusString = "5,17";
						break;
					case 3:
						statusString = "6";
						break;
					}
				}

			}

			// select all vouchers for today command
			StringBuffer sb = new StringBuffer(
					"SELECT   CourierID "
							+ ", VouchNumber "
							+ ", VouchDate "
							+ ", VouchMemo "
							+ ", SenderName "
							+ ", SenderArea "
							+ ", SenderCity "
							+ ", SenderAddress "
							+ ", SenderGSM "
							+ ", ReceiverName "
							+ ", ReceiverArea "
							+ ", ReceiverCity "
							+ ", ReceiverAddress "
							+ ", ReceiverGSM "
							+ ", VouchCount "
							+ ", DocType "
							+ ", DriverID "
							+ ", TransType "
							+ ", TransReceiverName "
							+ ", TransDate "
							+ ", DeliveryDate "
							+ ", TransReason "
							+ ", TransNotDeliveredTypeID "
							+ ", ExpCashValue "
							+ ", ExpCheckValue "
							+ ", ExpCheckCount "
							+ ", ReceivedCashValue "
							+ ", ReceivedCheckValue "
							+ ", ReceivedCheckCount "
							+ ", OrderVoucherNumber "
							+ ", BranID "
							+ ", DateCreated "
							+ ", Latitude"
							+ ", Longitude "
							+ ", DeliveryHour"
							+ ", ParcelStatus"
							+ ",CourierCustomerType "
							+ ",IfIsCreditValue "
							+ ",( 6371 * ACOS( COS( RADIANS(:Lat) ) * COS( RADIANS( Latitude ) ) * COS( RADIANS( Longitude ) - RADIANS(:Lng) )"
							+ " + SIN( RADIANS(:Lat) ) * SIN( RADIANS( Latitude ) ) ) ) AS Distance,  OrderIndex "
							+ " FROM RemoteCourier WHERE DriverID = :DriverID AND \"TransDate\" >= :DateFrom AND "
							+ " \"TransDate\" < :DateTo "
							+ " AND TransType  IN (" + statusString + ")"
							+ " AND DocType in (" + docType
							+ ") ORDER BY OrderIndex, Distance");

			Date dateFrom = date;
			Date dateTo = Base.addDay(date, 1);

			PreparedStatement ps = conn.prepareStatement(sb.toString());
			ps.set("Lat", PreferencesUtil.Latitude);
			ps.set("Lng", PreferencesUtil.Longitude);
			// driver id
			ps.set("DriverID", app_settings.CurrentDriverID);
			// start date
			ps.set("DateFrom", dateFrom);
			// end date
			ps.set("DateTo", dateTo);

			ResultSet rs = ps.executeQuery();

			int ordIndexCounter = 0;
			// reads vouchers into array
			while (rs.next()) {
				Courier c = new Courier();
				c.GetSignature = false;
				c.Fetch(rs);
				if(c.OrderIndex == 0)
				{
					ordIndexCounter += 10;
					c.OrderIndex = ordIndexCounter;
				}
				lstVouchersRes.add(c);

				// c.getCourierID()
				/*if (isMapLoaded == true && mapHelper != null && gMap != null) {
					if (c.Latitude != 0 && c.Longitude != 0) {
						if (docType.equals("5")) {

							mapHelper.AddMarker(c.Latitude, c.Longitude,
									c.getVouchNumber(), c.getSenderAddress(),
									R.drawable.envelope_32, 0, 0, 0, "", false);

						} else {
							mapHelper.AddMarker(c.Latitude, c.Longitude,
									c.getVouchNumber(), c.getReceiverAddress(),
									R.drawable.envelope_32, 0, 0, 0, "", false);
						}
					}
				}*/

			}
			// mapView.invalidate();
			rs.close();
			ps.close();

			// when item in the list is clicked starts a new activity
			// with vouchers details

			// lvVouchers.setAdapter(new MyArrayAdapter(this, R.layout.row,
			// lstVouchers));

			conn.release();

		} catch (Exception ex) {
			// tbMessage.setText(ex.getMessage());
			ex.printStackTrace();
		}

		AreVouchersLoaded = true;
		return lstVouchersRes;
	}

	boolean areMapVouchersLoaded = false;
	private void LoadMapMarkers() {
		
		for (Courier c : lstVouchers) {
			if (isMapLoaded == true && mapHelper != null && gMap != null) {
				if (c.Latitude != 0 && c.Longitude != 0) {
					if (docType.equals("5")) {

						mapHelper.AddMarker(c.Latitude, c.Longitude,
								c.getVouchNumber(), c.getSenderAddress(),
								R.drawable.envelope_32, 0, 0, 0, "", false);

					} else {
						mapHelper.AddMarker(c.Latitude, c.Longitude,
								c.getVouchNumber(), c.getReceiverAddress(),
								R.drawable.envelope_32, 0, 0, 0, "", false);
					}
				}
			}
		}
		areMapVouchersLoaded = true;
	}

	private void OpenDetailsScreen(Float voucherID) {
		Intent i = new Intent(ListTodayVouchers.this, VoucherDetails.class);
		i.putExtra("CourierID", voucherID);
		startActivity(i);
		// finish();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {

			// open menu
			finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.vouchers_list_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.mitype:
			spType.performClick();
			break;
		case R.id.miDate:
			showDialog(DATE_DIALOG_ID);
			break;
		case R.id.miRearrange:
			InitRearrange();
			break;
		}
		return true;
	}

	private void InitRearrange()
	{
		tabHost.getTabWidget().setVisibility(TabWidget.GONE);
		((AdapterVouchers)lvVouchers.getAdapter()).SetOrderActive(true);
		lvVouchers.setIsRearrangeEnabled(true);
		lblRearrangeHeader.setVisibility(TextView.VISIBLE);
		layoutRearrangeMenu.setVisibility(LinearLayout.VISIBLE);
	}
	
	private void CloseRearrange()
	{
		tabHost.getTabWidget().setVisibility(TabWidget.VISIBLE);
		((AdapterVouchers)lvVouchers.getAdapter()).SetOrderActive(false);
		lvVouchers.setIsRearrangeEnabled(false);
		lblRearrangeHeader.setVisibility(TextView.GONE);
		layoutRearrangeMenu.setVisibility(LinearLayout.GONE);
	}
	
	// updates the date in the TextView
	private void updateDisplay() {

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
			//currentSearchDate = calendar.getTime();
			app_settings.WorkDate = calendar.getTime();			

			UpdateDateText();

			// LoadVouchers(currentSearchVoucherType, currentSearchDate);
			Log.d("Messangero", "LoadVoucherTask date changed");
			LoadVouchersTask();
		}
	};

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DATE_DIALOG_ID:
			return new DatePickerDialog(this, mDateSetListener, mYear, mMonth,
					mDay);
		}
		return null;
	}

	public void LeftButtonCLicked(View v) {
		showDialog(DATE_DIALOG_ID);
	}

	public void RightButtonCLicked(View v) {
		spType.performClick();
	}

	private class LoadTask extends
			AsyncTask<Object, ArrayList<Courier>, ArrayList<Courier>> {

		@Override
		protected ArrayList<Courier> doInBackground(Object... params) {

			ArrayList<Courier> lstVouchers = new ArrayList<Courier>();
			int type = (Integer) params[0];
			Date date = (Date) params[1];
			lstVouchers = LoadVouchers(type, date);
			return lstVouchers;
		}

		@Override
		protected void onPostExecute(ArrayList<Courier> result) {

		};
	}

	@Override
	public void RightButton1CLicked(View v) 
	{
		
	}

}
