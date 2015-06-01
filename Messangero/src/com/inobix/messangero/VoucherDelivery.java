package com.inobix.messangero;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.inobix.messangero.R;
import com.inobix.messangero.adapters.NotDeliveredTypeAdapter;
import com.inobix.messangero.common.IActionActivity;
import com.inobix.messangero.common.app_settings;
import com.inobix.messangero.map.MyLocation;
import com.inobix.messangero.map.MyLocation.LocationResult;
import com.inobix.messangero.signature.SignatureViewDemo;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ianywhere.ultralitejni12.Connection;
import com.ianywhere.ultralitejni12.PreparedStatement;
import com.ianywhere.ultralitejni12.ResultSet;
import com.ianywhere.ultralitejni12.ULjException;

import DAL.ChargeTrans;
import DAL.Companies;
import DAL.Courier;
import DAL.CourierTran;
import DAL.DBUtil;
import DAL.MyUUID;
import DAL.NotDeliveredType;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Bitmap.Config;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class VoucherDelivery extends Activity implements IActionActivity,
		LocationListener, ConnectionCallbacks, OnConnectionFailedListener {

	public static final int CAMERA_REQUEST_CODE = 64655;
	public static final int GALLERY_REQUEST_CODE = 64659;
	private static final int CAPTURE_REQUEST_CODE = 3500;
	public Courier voucher = null;
	private final String FileName = "temsign1.png";
	private String FilePath = "";

	ActionBar actionBar;
	EditText tbReceiver;
	TextView lblCash;
	EditText tbCash;
	TextView lblCheckValue;
	EditText tbCheckValue;
	// TextView lblCheckCount;
	// EditText tbCheckCount;
	EditText tbOrderVoucherNumber;
	Spinner spDeliveryType;
	// Spinner spNotDeliveredType;
	Button btnNotDeliveredType;
	TextView tbNotDeliveredType;
	ImageView ivSign;
	TextView tbVoucherNotes;
	EditText tbDeliveryNotes;
	EditText tbDeliveryHour;
	CheckBox chbParcelStatus;
	CheckBox chbIsNewTransaction;
	LocationClient locClient;
	LinearLayout layoutCharges;
	NotDeliveredType currentNotDeliveredType = null;
	ImageView ivPhoto;
	
	CourierTran lastTran = new CourierTran();

	// EditText tbRowCashExpected;
	// EditText tbRowCashReceived;
	// EditText tbRowCheckExpected;
	// EditText tbRowCheckReceived;
	// EditText tbRowCheckCountExpected;
	// EditText tbRowCheckCountReceived;

	String voucherNumbers;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Base.LoadLocale(this);
		setContentView(R.layout.voucher_delivery);

		actionBar = (ActionBar) findViewById(R.id.layoutActionBar);
		actionBar.SetLeftButtonText(getString(R.string.Save));
		actionBar.SetRightButtonText(getString(R.string.Sign));
		//actionBar.SetRightButton1Text("Camera");

		voucherNumbers = getIntent().getExtras().getString("VouchersNumbers");

		tbReceiver = (EditText) findViewById(R.id.tbReceiver);
		lblCash = (TextView) findViewById(R.id.lblCashPayment1);
		tbCash = (EditText) findViewById(R.id.tbCashPayment);
		lblCheckValue = (TextView) findViewById(R.id.lblCheckPayment1);
		tbCheckValue = (EditText) findViewById(R.id.tbCheckPayment);
		tbOrderVoucherNumber = (EditText) findViewById(R.id.tbNewVoucherNumber);
		spDeliveryType = (Spinner) findViewById(R.id.spType);
		btnNotDeliveredType = (Button) findViewById(R.id.btnNotDeliveredType);
		tbNotDeliveredType = (TextView) findViewById(R.id.tbNotDeliveredType);
		ivSign = (ImageView) findViewById(R.id.ivSignature);
		btnNotDeliveredType.setEnabled(false);
		tbVoucherNotes = (TextView) findViewById(R.id.tbNotes);
		tbDeliveryNotes = (EditText) findViewById(R.id.tbDeliveryNotes);
		tbDeliveryHour = (EditText) findViewById(R.id.tbDeliveryHour);
		layoutCharges = (LinearLayout) findViewById(R.id.layoutCharges);
		ivPhoto = (ImageView) findViewById(R.id.ivPhoto);

		chbParcelStatus = (CheckBox) findViewById(R.id.chbParcelStatus);
		chbIsNewTransaction = (CheckBox) findViewById(R.id.chbIsNewTransaction);
		TextView lblParcelStatus = (TextView) findViewById(R.id.lblParcelStatus);
		
		chbIsNewTransaction.setChecked(true);
		
		
		

		spDeliveryType.setOnItemSelectedListener(new OnItemSelectedListener() {

			public void onItemSelected(AdapterView<?> parent, View view,
					int pos, long id) {
				if (pos == 0) {
					btnNotDeliveredType.setEnabled(false);
					tbNotDeliveredType.setText("");
					tbReceiver.setEnabled(true);
					tbCheckValue.setEnabled(true);
					tbCash.setEnabled(true);

				} else if (pos == 1) {
					btnNotDeliveredType.setEnabled(true);

					tbReceiver.setEnabled(false);
					tbCheckValue.setEnabled(false);
					tbCash.setEnabled(false);
				}
			};

			public void onNothingSelected(AdapterView parent) {
				// Do nothing.
			};

		});

		try {
			Float id = getIntent().getExtras().getFloat("CourierID");
			if (voucher == null) {
				voucher = new Courier();
				voucher.LoadCourier(this, id);
				
				lastTran.LoadLastTran(this, voucher.getCourierID());
			}

			if (voucherNumbers != null) {
				String[] nums = voucherNumbers.split(";");

				voucher = new Courier();
				try {
					voucher.LoadCourier(this, nums[0]);
					tbReceiver.setText(voucher.getReceiverName());
				} catch (ULjException e) {
					Crashlytics.logException(e);
				}

				if (nums.length > 0) {

					// ------------------------------- 2014-07-20
					int a = 0;
					HashMap<String, ChargeTrans> mapCharges = new HashMap<>();
					ArrayList<String> chargesNames = new ArrayList<String>();
					for (String n : nums) {

						Courier currVoucher = new Courier();

						try {
							currVoucher.LoadCourier(this, n);

							if (a == 0) {
								a = 1;
								voucher = currVoucher;
								tbReceiver.setText(voucher.getReceiverName());
							}

							if (currVoucher.Charges.size() > 0) {
								for (ChargeTrans charge : currVoucher.Charges) {
									if (!mapCharges
											.containsKey(charge.ChargeName)) {
										chargesNames.add(charge.ChargeName);
										mapCharges.put(charge.ChargeName,
												charge);
									} else {
										mapCharges.get(charge.ChargeName).SumCash += charge.SumCash;
										mapCharges.get(charge.ChargeName).SumCheck += charge.SumCheck;
									}
								}
							}

						} catch (ULjException e) {
							// TODO Auto-generated catch block
							Crashlytics.logException(e);
						}
						if (currVoucher.getCourierID() == 0)
							continue;

					}
					// ------------------------------- 2014-07-20
					if (chargesNames.size() > 0
							&& mapCharges.isEmpty() == false) {
						double sumCash = 0;
						double sumCheck = 0;
						voucher.Charges.clear();
						for (String name : chargesNames) {
							ChargeTrans tr = mapCharges.get(name);
							sumCash += tr.SumCash;
							sumCheck += tr.SumCheck;
							voucher.Charges.add(tr);

						}
						voucher.setExpCashValue((float) sumCash);
						voucher.setExpCheckValue((float) sumCheck);
					}
				}
			}
			
			if(lastTran.TransType.equals("17") == true || (voucher.getDocType().equals("5") && lastTran.TransType.equals("20") == true))
			{
				chbIsNewTransaction.setChecked(false);
			}
			else
				chbIsNewTransaction.setChecked(true);

			if (voucher.ParcelStatus == 0) {
				chbParcelStatus.setVisibility(CheckBox.GONE);
				lblParcelStatus.setVisibility(CheckBox.GONE);
			} else {
				chbParcelStatus.setVisibility(CheckBox.VISIBLE);
				lblParcelStatus.setVisibility(CheckBox.VISIBLE);
			}

			TextView lblOrderVoucherNumber = (TextView) findViewById(R.id.lblNewVoucherNumber);
			LinearLayout layout = (LinearLayout) findViewById(R.id.layoutNewVouchNum);
			if (voucher.getDocType().equals("5")) {
				lblCash.setVisibility(TextView.GONE);
				lblCheckValue.setVisibility(TextView.GONE);
				tbCash.setVisibility(TextView.GONE);
				tbCheckValue.setVisibility(TextView.GONE);

				layout.setVisibility(LinearLayout.VISIBLE);
				lblOrderVoucherNumber.setVisibility(TextView.VISIBLE);
			} else {
				lblCash.setVisibility(TextView.VISIBLE);
				lblCheckValue.setVisibility(TextView.VISIBLE);
				tbCash.setVisibility(TextView.VISIBLE);
				tbCheckValue.setVisibility(TextView.VISIBLE);

				layout.setVisibility(LinearLayout.GONE);
				lblOrderVoucherNumber.setVisibility(TextView.GONE);
			}

			if (tbDeliveryHour != null)
				tbDeliveryHour.setText(voucher.DeliveryHour);

			if (chbParcelStatus != null) {
				if (voucher.ParcelStatus != 0) {
					chbParcelStatus.setEnabled(true);

					if (voucher.ParcelStatus == 2) {
						chbParcelStatus.setChecked(true);
					}
				} else
					chbParcelStatus.setEnabled(false);
			}

			// put default receiver if there is not added other receiver
			if (voucher.getTransReceiverName() == null
					|| voucher.getTransReceiverName().equals("")) {
				if (voucher.getDocType().equals("5"))
					tbReceiver.setText(voucher.getSenderName());
				else
					tbReceiver.setText(voucher.getReceiverName());
			} else {
				tbReceiver.setText(voucher.getTransReceiverName());
			}

			String status = voucher.getTransType();
			if (status.equals("5")) {
				tbCash.setText(voucher.getRealCashValueReceived().toString());
				tbCheckValue.setText(voucher.getRealCheckValueReceived()
						.toString());
				spDeliveryType.setSelection(0);
			} else {
				tbCash.setText(voucher.getExpCashValue().toString());
				tbCheckValue.setText(voucher.getExpCheckValue().toString());

				if (status.equals("6")) {
					spDeliveryType.setSelection(1);
				} else
					spDeliveryType.setSelection(-1);
			}

			tbOrderVoucherNumber.setText(voucher.getOrderVoucherNumber());
			tbVoucherNotes.setText(voucher.getVouchMemo());

			ivSign.setImageBitmap(voucher.getTransSignature());

		} catch (ULjException e) {
			Crashlytics.logException(e); 
		}

		ArrayAdapter adapter = null;
		if (voucher.getDocType().equals("5")) {
			adapter = ArrayAdapter
					.createFromResource(this, R.array.DeliveryTypes,
							android.R.layout.simple_spinner_item);
		} else {
			adapter = ArrayAdapter
					.createFromResource(this, R.array.DeliveryTypes,
							android.R.layout.simple_spinner_item);
		}

		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spDeliveryType.setAdapter(adapter);

		if (savedInstanceState != null) {

			boolean showDialog = savedInstanceState.getBoolean("DialogSign");

			if (showDialog) {
				StartCaptureSignature();
			}
		}

		Log.d("MDelivery", "on Create: " + tbReceiver.getText().toString());

		LoadCharges();
	}

	private void LoadCharges() {
		if (voucher.Charges == null || voucher.Charges.size() == 0) {
			layoutCharges.setVisibility(LinearLayout.GONE);
			return;
		}

		layoutCharges.setVisibility(LinearLayout.VISIBLE);

		Collections.sort(voucher.Charges, new Comparator<ChargeTrans>() {

			public int compare(ChargeTrans lhs, ChargeTrans rhs) {
				if (lhs.CreditDebit == null)
					return -1;
				if (rhs.CreditDebit == null)
					return 1;
				return lhs.CreditDebit.compareTo(rhs.CreditDebit);
			}
		});

		boolean isCreditHeaderAdded = false;
		boolean isDebitHeaderAdded = false;
		for (ChargeTrans tran : voucher.Charges) {
			if (tran.CreditDebit != null && tran.CreditDebit.equals("1")
					&& isDebitHeaderAdded == false) {
				// add debit header
				isDebitHeaderAdded = true;

				TextView lblHeader = new TextView(this);
				LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
						LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
				lblHeader.setGravity(Gravity.CENTER_HORIZONTAL);
				lblHeader.setLayoutParams(params);
				lblHeader.setText(getString(R.string.lblTakeMoney));
				layoutCharges.addView(lblHeader);

				LinearLayout l = new LinearLayout(this);
				l.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
						LayoutParams.WRAP_CONTENT));
				l.setOrientation(LinearLayout.HORIZONTAL);
				l.setWeightSum(1);

				TextView lblCharge = new TextView(this);
				LinearLayout.LayoutParams paramHeader = new LinearLayout.LayoutParams(
						LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
				paramHeader.weight = 0.2f;
				lblCharge.setLayoutParams(paramHeader);
				lblCharge.setText("");

				TextView lblCash = new TextView(this);
				LinearLayout.LayoutParams paramsCash = new LinearLayout.LayoutParams(
						LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
				paramsCash.weight = 0.4f;
				lblCash.setLayoutParams(paramsCash);
				lblCash.setText("Cash");

				TextView lblCheck = new TextView(this);
				LinearLayout.LayoutParams paramsCheck = new LinearLayout.LayoutParams(
						LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
				paramsCheck.weight = 0.4f;
				lblCheck.setLayoutParams(paramsCheck);
				lblCheck.setText("Check");

				l.addView(lblCharge);
				l.addView(lblCash);
				l.addView(lblCheck);

				layoutCharges.addView(l);
			}
			if (tran.CreditDebit != null && tran.CreditDebit.equals("2")
					&& isCreditHeaderAdded == false) {
				// add debit header
				isCreditHeaderAdded = true;

				TextView lblHeader = new TextView(this);
				LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
						LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
				lblHeader.setLayoutParams(params);
				lblHeader.setGravity(Gravity.CENTER_HORIZONTAL);
				lblHeader.setText(getString(R.string.lblGiveMoney));
				layoutCharges.addView(lblHeader);

				LinearLayout l = new LinearLayout(this);
				l.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
						LayoutParams.WRAP_CONTENT));
				l.setOrientation(LinearLayout.HORIZONTAL);
				l.setWeightSum(1);

				TextView lblCharge = new TextView(this);
				LinearLayout.LayoutParams paramHeader = new LinearLayout.LayoutParams(
						LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
				paramHeader.weight = 0.2f;
				lblCharge.setLayoutParams(paramHeader);
				lblCharge.setText("");

				TextView lblCash = new TextView(this);
				LinearLayout.LayoutParams paramsCash = new LinearLayout.LayoutParams(
						LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
				paramsCash.weight = 0.4f;
				lblCash.setLayoutParams(paramsCash);
				lblCash.setText("Cash");

				TextView lblCheck = new TextView(this);
				LinearLayout.LayoutParams paramsCheck = new LinearLayout.LayoutParams(
						LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
				paramsCheck.weight = 0.4f;
				lblCheck.setLayoutParams(paramsCheck);
				lblCheck.setText("Check");

				l.addView(lblCharge);
				l.addView(lblCash);
				l.addView(lblCheck);

				layoutCharges.addView(l);
			}
			LinearLayout l = new LinearLayout(this);
			l.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
					LayoutParams.WRAP_CONTENT));
			l.setOrientation(LinearLayout.HORIZONTAL);
			l.setWeightSum(1);

			TextView lblCharge = new TextView(this);
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			params.weight = 0.2f;
			lblCharge.setLayoutParams(params);
			lblCharge.setText(tran.ChargeName);

			TextView lblCash = new TextView(this);
			LinearLayout.LayoutParams paramsCash = new LinearLayout.LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			paramsCash.weight = 0.4f;
			lblCash.setLayoutParams(paramsCash);
			if (tran.SumCash != 0) {
				NumberFormat f = NumberFormat.getNumberInstance();
				f.setMaximumFractionDigits(2);
				String cashStrng = f.format(tran.SumCash);
				lblCash.setText(cashStrng);
			} else
				lblCash.setText(" ");

			TextView lblCheck = new TextView(this);
			LinearLayout.LayoutParams paramsCheck = new LinearLayout.LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			paramsCheck.weight = 0.4f;
			lblCheck.setLayoutParams(paramsCheck);
			if (tran.SumCheck != 0) {
				NumberFormat f = NumberFormat.getNumberInstance();
				f.setMaximumFractionDigits(2);
				String checkStrng = f.format(tran.SumCheck);
				lblCheck.setText(checkStrng);
			} else
				lblCheck.setText(" ");

			l.addView(lblCharge);
			l.addView(lblCash);
			l.addView(lblCheck);

			layoutCharges.addView(l);
		}

		Log.d("MDelivery", "load Charges: " + tbReceiver.getText().toString());
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (voucher.getTransSignature() != null)
			voucher.getTransSignature().recycle();
	};

	Dialog dialogNotDeliveredType;
	ArrayList<NotDeliveredType> listNotDeliveredType;

	public void btnNotDeliveredType_Click(View sender) {
		dialogNotDeliveredType = new Dialog(this, R.style.cust_dialog);
		dialogNotDeliveredType
				.setContentView(R.layout.dialog_not_delivered_type);
		dialogNotDeliveredType
				.setTitle(getString(R.string.lblNotDeliveredType));
		ListView lvNotDeliveredType = (ListView) dialogNotDeliveredType
				.findViewById(R.id.lvNotDeliveredType);

		listNotDeliveredType = new ArrayList<NotDeliveredType>();
		Connection conn = DBUtil.CreateConnection(this);
		PreparedStatement ps;
		try {
			ps = conn
					.prepareStatement("SELECT ID, CourierNotDeliveredTypeName FROM CourierNotDeliveredType");

			ResultSet rs = ps.executeQuery();

			// reads vouchers into array
			while (rs.next()) {
				int id = rs.getInt(1);
				String desc = rs.getString(2);
				// Map<String, String> mapList = new HashMap<String, String>();
				// mapList.put("Key", s);
				// data.add(mapList);
				NotDeliveredType ndt = new NotDeliveredType();
				ndt.ID = id;
				ndt.Desc = desc;
				listNotDeliveredType.add(ndt);
			}
			rs.close();
			ps.close();
		} catch (ULjException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} finally {
			try {
				conn.release();
			} catch (ULjException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		lvNotDeliveredType.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				NotDeliveredType ndt = listNotDeliveredType.get(arg2);
				currentNotDeliveredType = ndt;
				tbNotDeliveredType.setText(currentNotDeliveredType.Desc);
				dialogNotDeliveredType.dismiss();
			}

		});

		NotDeliveredTypeAdapter ndtAdapter = new NotDeliveredTypeAdapter(this,
				0, listNotDeliveredType);
		lvNotDeliveredType.setAdapter(ndtAdapter);

		dialogNotDeliveredType.show();
	}

	@Override
	protected void onStart() {
		super.onStart();

		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
		tbReceiver.selectAll();
		tbReceiver.postDelayed(new Runnable() {
			public void run() {
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.showSoftInput(tbReceiver, InputMethodManager.SHOW_FORCED);

				Log.d("MDelivery", "on Start Post Delayed: "
						+ tbReceiver.getText().toString());
			}
		}, 100);

		Log.d("MDelivery", "on Start: " + tbReceiver.getText().toString());
	}

	boolean isSaveClicked = false;

	public void btnSave_OnClick(View v) {

		if (isSaveClicked)
			return;
		isSaveClicked = true;

		if (Save())
			finish();

		isSaveClicked = false;
	}

	public void btnSign_OnClick(View v) {
		StartCaptureSignature();
		Log.d("MDelivery", "Signature start: "
				+ tbReceiver.getText().toString());
	}

	public static final int ORDER_VOUCHER_NUMBERS_REQUEST_CODE = 1200;

	public void btnScanOrderVoucherNumber_OnClick(View v) {
		// scan voucher
		// ScanNewVoucher();

		((InputMethodManager) VoucherDelivery.this
				.getSystemService(Context.INPUT_METHOD_SERVICE))
				.hideSoftInputFromWindow(tbReceiver.getWindowToken(), 0);

		Intent OrderVoucherNumbers = new Intent(VoucherDelivery.this,
				OrderVoucherNumbers.class);
		OrderVoucherNumbers.putExtra("OrderVoucherNumbers",
				voucher.getOrderVoucherNumber());
		startActivityForResult(OrderVoucherNumbers,
				ORDER_VOUCHER_NUMBERS_REQUEST_CODE);
	}

	private void ScanNewVoucher() {
		if (PreferencesUtil.SearchAllBarcodeTypes)
			IntentIntegrator.initiateScan(VoucherDelivery.this);
		else if (PreferencesUtil.SearchOnlyCode39)
			IntentIntegrator.initiateScan(VoucherDelivery.this,
					IntentIntegrator.DEFAULT_TITLE,
					IntentIntegrator.DEFAULT_MESSAGE,
					IntentIntegrator.DEFAULT_YES, IntentIntegrator.DEFAULT_NO,
					"CODE_39");
	}

	private void UpdateVoucher(Courier currentVoucher) {
		try {
			Calendar cCallendar = Calendar.getInstance();
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(app_settings.WorkDate);
			calendar.set(Calendar.HOUR_OF_DAY,
					cCallendar.get(Calendar.HOUR_OF_DAY));
			calendar.set(Calendar.MINUTE, cCallendar.get(Calendar.MINUTE));
			calendar.set(Calendar.SECOND, cCallendar.get(Calendar.SECOND));
			calendar.set(Calendar.MILLISECOND,
					cCallendar.get(Calendar.MILLISECOND));

			currentVoucher.setDeliveryDate(calendar.getTime());
			// currentVoucher.setDeliveryDate(app_settings.WorkDate);
			currentVoucher.setTransReason(tbDeliveryNotes.getText().toString());

			int pos = spDeliveryType.getSelectedItemPosition();
			String status = "4";
			switch (pos) {
			case 0:
				if (currentVoucher.getDocType().equals("5")) {
					// order taken
					status = "20";
				} else {
					// packet delivered
					status = "17";
				}

				currentVoucher.setTransReceiverName(tbReceiver.getText()
						.toString());

				/*
				 * currentVoucher.setRealCashValueReceived(Float.valueOf(tbCash
				 * .getText().toString()));
				 * 
				 * currentVoucher.setRealCheckValueReceived(Float
				 * .valueOf(tbCheckValue.getText().toString()));
				 */

				currentVoucher.setOrderVoucherNumber(tbOrderVoucherNumber
						.getText().toString());

				break;
			case 1:
				if (currentVoucher.getDocType().equals("5")) {
					// order denied
					status = "6";
					currentVoucher
							.setTransNotDeliveredTypeID((float) currentNotDeliveredType.ID);
				} else {
					// packet not delivered
					status = "6";
					currentVoucher
							.setTransNotDeliveredTypeID((float) currentNotDeliveredType.ID);
				}
				break;
			}

			currentVoucher.setTransType(status);

		} catch (Exception ex) {
			ex.printStackTrace();
		}

		try {
			CourierTran transaction = new CourierTran();
			
			currentVoucher.UpdateCourier(getApplicationContext());
			
			transaction.LoadLastTran(this, currentVoucher.getCourierID());
			
			boolean isNew = false;
			//if(transaction.TransType.equals(currentVoucher.getTransType()) == false) 
				//isNew = true;
			
			if(chbIsNewTransaction.isChecked())
				isNew = true;
			
			transaction.CourierID = currentVoucher.getCourierID();
			transaction.DriverID = currentVoucher.getDriverID();
			transaction.TransDate = currentVoucher.getDeliveryDate();			
			transaction.TransNotDeliveredTypeID = currentVoucher.getTransNotDeliveredTypeID();
			transaction.TransReason = currentVoucher.getTransReason();
			transaction.TransReceiverName = currentVoucher.getTransReceiverName();
			transaction.TransType = currentVoucher.getTransType(); 
			transaction.OrderVoucherNumber = currentVoucher.getOrderVoucherNumber();
			transaction.ParcelStatus = currentVoucher.ParcelStatus;
			transaction.ReceivedCashValue = currentVoucher.getRealCashValueReceived();
			transaction.ReceivedCheckValue = currentVoucher.getRealCheckValueReceived();
			transaction.TransSignature = currentVoucher.getTransSignature();
			
			if(isNew)
			{
				transaction.RemoteCourierTranID = new MyUUID().getString();
				transaction.InsertCourierTrans(this);
			}
			else
			{	
				transaction.UpdateCourierTrans(this);
			}
			
			
			// DBUtil.SyncVouchers(this);
		} catch (ULjException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private String SaveImageToFIle() {
		String imgName = "";
		String fileName = "";

		Bitmap bmp = ((BitmapDrawable) imgPlaceNewPhoto.getDrawable())
				.getBitmap();
		
		if(bmp == null)
			return "";

		MyUUID uuid = new MyUUID();
		
		
		String filesDir = this.getExternalFilesDir(Environment.DIRECTORY_PICTURES) + "/MessangeroCache";
		File f = new File(filesDir);
		f.mkdirs();
		
		
		try {
			imgName = uuid.getString() + ".jpg";
			fileName = filesDir + "/" + imgName;

			FileOutputStream out = null;
			try {
				out = new FileOutputStream(fileName);
				bmp.compress(Bitmap.CompressFormat.JPEG, 90, out);
			} catch (Exception e) {
				e.printStackTrace();
				imgName = "";
			} finally {
				try {
					if (out != null) {
						out.close();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		} catch (ULjException e) {
			Crashlytics.logException(e);
		}

		imgPlaceNewPhoto.setImageBitmap(null);
		bmp.recycle();
		return imgName;
	}

	ArrayList<Float> listIDs;

	private boolean Save() {

		boolean isValid = true;
		listIDs = new ArrayList<Float>();

		if (voucherNumbers != null) {
			// update group vouchers
			try {
				double realCash = Double.parseDouble(tbCash.getText()
						.toString());
				double realCheck = Double.parseDouble(tbCheckValue.getText()
						.toString());

				double c = Math.abs(realCash - voucher.getExpCashValue());
				double c1 = Math.abs(realCheck - voucher.getExpCheckValue());
				if (c > 0.001 || c1 > 0.001) {
					// show error that cannot deliver group of vouchers with
					// different cash
					Toast.makeText(this,
							getString(R.string.msgGroupDeliveryError),
							Toast.LENGTH_SHORT).show();
					return false;
				}
			} catch (Exception ex) {
				Toast.makeText(this, getString(R.string.msgGroupDeliveryError),
						Toast.LENGTH_SHORT).show();
				return false;
			}
			// voucher.getExpCashValue()
			String[] numbers = voucherNumbers.split(";");
			//String imageName = SaveImageToFIle();
			for (String n : numbers) {
				Courier currVoucher = new Courier();

				try {
					currVoucher.LoadCourier(this, n);
					listIDs.add(currVoucher.getCourierID());
					if (bitmap != null)
						currVoucher.setTransSignature(bitmap);

					if (currVoucher.ParcelStatus != 0) {
						if (chbParcelStatus.isChecked())
							currVoucher.ParcelStatus = 2;
						else {
							isValid = false;
							currVoucher.ParcelStatus = 3;
						}
					}
					// currVoucher.setReceiverName(receiverName)
				} catch (ULjException e) {
					// TODO Auto-generated catch block
					Crashlytics.logException(e);
				}
				if (currVoucher.getCourierID() == 0)
					continue;

				currVoucher.setRealCashValueReceived(currVoucher
						.getExpCashValue());
				currVoucher.setRealCheckValueReceived(currVoucher
						.getExpCheckValue());

				//currVoucher.ImageName = imageName;
				UpdateVoucher(currVoucher);
			}

		} else {
			// update single voucher
			listIDs.add(voucher.getCourierID());

			if (voucher.ParcelStatus != 0) {
				if (chbParcelStatus.isChecked())
					voucher.ParcelStatus = 2;
				else {
					voucher.ParcelStatus = 3;
					isValid = false;
				}
			}
			if (isValid) {

				voucher.setRealCashValueReceived(Float.valueOf(tbCash.getText()
						.toString()));

				voucher.setRealCheckValueReceived(Float.valueOf(tbCheckValue
						.getText().toString()));

				//String imageName = SaveImageToFIle();
				//voucher.ImageName = imageName;
				UpdateVoucher(voucher);

				((InputMethodManager) VoucherDelivery.this
						.getSystemService(Context.INPUT_METHOD_SERVICE))
						.hideSoftInputFromWindow(tbReceiver.getWindowToken(), 0);
				Intent i = new Intent(VoucherDelivery.this,
						VoucherDetails.class);
				i.putExtra("CourierID", voucher.getCourierID());
				startActivity(i);
			}

		}

		if (isValid) {
			if (listIDs.size() > 0) {
				for (float id : listIDs) {
					try {
						Courier.UpdateCourierCoordinates(VoucherDelivery.this,
								id, PreferencesUtil.Latitude,
								PreferencesUtil.Longitude);

					} catch (ULjException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}

		if (isValid) {
			new SyncVouchersTask().execute(getApplicationContext());
			finish();
		} else {
			// show message that there is parcel to take
			ShowSaveErrorDialog();
		}

		return isValid;
	}

	private void ShowSaveErrorDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(R.string.msgParcelToTake);
		builder.setPositiveButton("OK", null);

		AlertDialog alert = builder.create();
		alert.show();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		if (signatureDialog != null && signatureDialog.isShowing()) {
			outState.putBoolean("DialogSign", signatureDialog.isShowing());
		}

		super.onSaveInstanceState(outState);
	}

	Dialog signatureDialog;

	private void StartCaptureSignature() {

		((InputMethodManager) VoucherDelivery.this
				.getSystemService(Context.INPUT_METHOD_SERVICE))
				.hideSoftInputFromWindow(this.getCurrentFocus()
						.getWindowToken(), 0);

		signatureDialog = new Dialog(this, R.style.cust_dialog_no_title);
		signatureDialog.setTitle("Signature");
		// dialog.setTitle(getResources().getString(
		// R.string.dialogPlaceTypes));
		signatureDialog.setContentView(R.layout.signature_capture);

		signatureDialog.getWindow().setLayout(LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT);

		Button btnClear = (Button) signatureDialog.findViewById(R.id.btnClear);
		Button btnOK = (Button) signatureDialog.findViewById(R.id.btnOK);
		Button btnCancel = (Button) signatureDialog
				.findViewById(R.id.btnCancel);

		btnClear.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				SignatureView signView = (SignatureView) signatureDialog
						.findViewById(R.id.signView);
				signView.clear();
			}
		});

		btnOK.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				SignatureView signView = (SignatureView) signatureDialog
						.findViewById(R.id.signView);

				Bitmap signature = signView.GetSignature();

				ivSign.setImageBitmap(signature);

				if (voucher != null)
					voucher.setTransSignature(signature);

				Save();

				signatureDialog.cancel();
			}
		});

		btnCancel.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// signatureDialog.cancel();
				signatureDialog.dismiss();
				Log.d("MDelivery", "Signature cancel: "
						+ tbReceiver.getText().toString());
			}
		});

		signatureDialog.show();
	}

	Bitmap bitmap;

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == CAMERA_REQUEST_CODE) {
			if (imgPlaceNewPhoto == null)
				return;
			CameraPhotoTaken(data);
		}
		if (requestCode == GALLERY_REQUEST_CODE) {
			if (imgPlaceNewPhoto == null || data == null)
				return;

			Uri selectedImage = data.getData();
			InputStream imageStream;
			try {
				imageStream = this.getContentResolver().openInputStream(
						selectedImage);
				Bitmap pickedUpImage = BitmapFactory.decodeStream(imageStream);
				imgPlaceNewPhoto.setImageBitmap(pickedUpImage);

			} catch (FileNotFoundException e) {
				Crashlytics.logException(e);
				// Common.LogException(e);
			}

		}

		if (requestCode == CAPTURE_REQUEST_CODE) {
			if (resultCode == SignatureDemoActivity.RESULT_OK) {

				// String fileName = FileName; // the same file name as above
				String fileName = data.getStringExtra("Signature");
				bitmap = BitmapFactory.decodeFile(fileName);
				// bitmap.
				// bitmap = (Bitmap) data.getParcelableExtra("Signature");

				ivSign.setImageBitmap(bitmap);
				// ivSign.setImageURI(Uri.parse(FileName));

				if (voucher != null)
					voucher.setTransSignature(bitmap);

				Save();
			} else {

				// String errorMessage = data
				// .getStringExtra("biz.binarysolutions.signature.ErrorMessage");
				int e = 0;
				e = e + 1;
			}

		} else if (requestCode == IntentIntegrator.REQUEST_CODE) {
			if (resultCode != RESULT_CANCELED) {
				IntentResult scanResult = IntentIntegrator.parseActivityResult(
						requestCode, resultCode, data);
				if (scanResult != null) {
					String number = scanResult.getContents();
					tbOrderVoucherNumber.setText(number);
				}
			}
		} else if (requestCode == ORDER_VOUCHER_NUMBERS_REQUEST_CODE) {
			if (resultCode != RESULT_CANCELED) {
				String resultVoucherNumbers = data
						.getStringExtra("VouchersNumbers");
				voucher.setOrderVoucherNumber(resultVoucherNumbers);
				tbOrderVoucherNumber.setText(voucher.getOrderVoucherNumber());
			}
		}
	}

	private void CameraPhotoTaken(Intent data) {
		if (data == null)
			return;
		Bundle extras = data.getExtras();
		Bitmap image = (Bitmap) extras.get("data");

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		image.compress(Bitmap.CompressFormat.JPEG, 100, baos);

		if (imgPlaceNewPhoto != null)
			imgPlaceNewPhoto.setImageBitmap(image);

	}

	private byte[] getImgBytes(Bitmap image) {
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		image.compress(Bitmap.CompressFormat.PNG, 100, stream);
		return stream.toByteArray();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {

			((InputMethodManager) VoucherDelivery.this
					.getSystemService(Context.INPUT_METHOD_SERVICE))
					.hideSoftInputFromWindow(tbReceiver.getWindowToken(), 0);

			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage(R.string.SaveDialog)
					.setCancelable(false)
					.setPositiveButton(R.string.Yes,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									if (Save())
										finish();
								}
							})
					.setNegativeButton(R.string.No,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									// open details screen
									Intent i = new Intent(VoucherDelivery.this,
											VoucherDetails.class);
									i.putExtra("CourierID",
											voucher.getCourierID());
									startActivity(i);
									finish();
								}
							});
			AlertDialog alert = builder.create();
			alert.show();

			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.voucher_delivery_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.miCallOffice:
			CallOffice();
			break;
		case R.id.miCallCustomer:
			CallCustomer();
			break;
		}
		return true;
	}

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

	private void CallCustomer() {
		if (voucher != null && voucher.getReceiverGSM() != null
				&& voucher.getReceiverGSM() != "") {
			String number = "";
			if (voucher.getDocType() == "5")
				number = voucher.getSenderGSM();
			else
				number = voucher.getReceiverGSM();

			Uri uri = Uri.fromParts("tel", number, null);
			Intent callIntent = new Intent(Intent.ACTION_CALL, uri);
			callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(callIntent);
		}
	}

	public void LeftButtonCLicked(View v) {
		// TODO Auto-generated method stub
		if (Save())
			finish();
	}

	public void RightButtonCLicked(View v) {
		// TODO Auto-generated method stub
		// StartCaptureSignature();

		((InputMethodManager) VoucherDelivery.this
				.getSystemService(Context.INPUT_METHOD_SERVICE))
				.hideSoftInputFromWindow(this.getCurrentFocus()
						.getWindowToken(), 0);

		Intent intentDemoSign = new Intent(this, SignatureDemoActivity.class);
		startActivityForResult(intentDemoSign, CAPTURE_REQUEST_CODE);
	}

	public void onConnectionFailed(ConnectionResult arg0) {
		// TODO Auto-generated method stub
		new SyncVouchersTask().execute(getApplicationContext());
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		if (locClient != null && locClient.isConnected())
			locClient.disconnect();
	}

	public void onConnected(Bundle arg0) {
		// TODO Auto-generated method stub
		try

		{
			Location location = locClient.getLastLocation();
			if (location == null)
				return;

			if (listIDs.size() > 0) {
				for (float id : listIDs) {
					try {
						Courier.UpdateCourierCoordinates(VoucherDelivery.this,
								id, location.getLatitude(),
								location.getLongitude());

					} catch (ULjException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				new SyncVouchersTask().execute(getApplicationContext());
			}
		} catch (Exception ex) {
			Crashlytics.logException(ex);
		}
	}

	public void onDisconnected() {
		// TODO Auto-generated method stub

	}

	public void onLocationChanged(Location arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void RightButton1CLicked(View v) {
		// start camera
		StartPhotoActivity();
	}

	ImageView imgPlaceNewPhoto;
	Dialog dialogPhoto;

	private void StartPhotoActivity() {
		// start take photo dialog
		dialogPhoto = new Dialog(this, R.style.cust_dialog);
		dialogPhoto.setContentView(R.layout.dialog_place_photo);
		dialogPhoto.setTitle("Take photo");
		Button btnTakePhotoCamera = (Button) dialogPhoto
				.findViewById(R.id.btnTakePhotoCamera);
		Button btnTakePhotoGallery = (Button) dialogPhoto
				.findViewById(R.id.btnTakePhotoGallery);
		Button btnCancel = (Button) dialogPhoto.findViewById(R.id.btnCancel);
		Button btnOK = (Button) dialogPhoto.findViewById(R.id.btnOK);

		imgPlaceNewPhoto = (ImageView) dialogPhoto.findViewById(R.id.imgPhoto);

		btnOK.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {

				ivPhoto.setImageDrawable(imgPlaceNewPhoto.getDrawable());

				/*
				 * FileOutputStream out = null; try { out = new
				 * FileOutputStream("test.jpg");
				 * ((BitmapDrawable)imgPlaceNewPhoto
				 * .getDrawable()).getBitmap().compress
				 * (Bitmap.CompressFormat.JPEG, 90, out); } catch (Exception e)
				 * { e.printStackTrace(); } finally { try { if (out != null) {
				 * out.close(); } } catch (IOException e) { e.printStackTrace();
				 * } }
				 */
				dialogPhoto.dismiss();
			}
		});

		btnCancel.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				dialogPhoto.dismiss();
			}
		});

		btnTakePhotoCamera.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Intent takePictureIntent = new Intent(
						MediaStore.ACTION_IMAGE_CAPTURE);
				startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE);
			}
		});

		btnTakePhotoGallery.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
				photoPickerIntent.setType("image/*");
				startActivityForResult(photoPickerIntent, GALLERY_REQUEST_CODE);
			}
		});

		dialogPhoto.show();

		dialogPhoto.setOnDismissListener(new OnDismissListener() {

			public void onDismiss(DialogInterface dialog) {
				//Bitmap bmp = ((BitmapDrawable) imgPlaceNewPhoto.getDrawable()) 
						//.getBitmap(); 
				//if (bmp != null)
				///	bmp.recycle();
			}
		});
	}
}
