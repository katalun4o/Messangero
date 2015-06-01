package com.inobix.messangero;

import com.crashlytics.android.Crashlytics;
import com.inobix.messangero.R;
import com.inobix.messangero.common.BitmapWorkerTask;
import com.inobix.messangero.common.IActionActivity;
import com.inobix.messangero.common.app_settings;

import java.io.ByteArrayOutputStream;
import java.lang.ref.WeakReference;
import java.net.URI;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import com.ianywhere.ultralitejni12.Connection;
import com.ianywhere.ultralitejni12.ULjException;
import com.ianywhere.ultralitejni12.UUIDValue;

import DAL.ChargeTrans;
import DAL.Companies;
import DAL.Courier;
import DAL.CourierTran;
import DAL.DBUtil;
import DAL.MyUUID;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.net.Uri;
import android.opengl.Visibility;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class VoucherDetails extends Activity implements IActionActivity {
	ActionBar actionBar;
	Button btnAccept = null;
	Button btnDecline = null;
	// Button btnDeliver = null;
	LinearLayout layoutBottom;
	public Courier voucher = null;
	LinearLayout layoutCharges;
	LinearLayout layoutTrans;
	ImageView ivPhotos;

	// DBUtil dbUtil;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Base.LoadLocale(this);
		setContentView(R.layout.voucher_details);

		actionBar = (ActionBar) findViewById(R.id.layoutActionBar);
		layoutBottom = (LinearLayout) findViewById(R.id.layoutBottom);

		actionBar.SetLeftButtonText(getString(R.string.Deliver));
		actionBar.SetRightButtonText(getString(R.string.tabMapCaption));

		try {
			// dbUtil = new DBUtil(this);
			// _conn = dbUtil.get_conn();
			Float id = getIntent().getExtras().getFloat("CourierID");
			voucher = new Courier();
			voucher.LoadCourier(this, id);
		} catch (ULjException e) {
			// TODO Auto-generated catch block
			Crashlytics.logException(e);
		}

		if (voucher != null) {
			TextView tbNumber = (TextView) findViewById(R.id.tbNumber);
			if (tbNumber != null)
				tbNumber.setText(voucher.getVouchNumber());

			TextView tbStatus = (TextView) findViewById(R.id.tbStatus);
			if (tbStatus != null) {

				// tbStatus.setText(voucher.getTransType());
				if (!voucher.getTransType().equals("")
						&& voucher.getTransType() != null)
					tbStatus.setText(Base.GetVoucherTypeString(Integer
							.valueOf((voucher.getTransType()))));
			}

			TextView tbDate = (TextView) findViewById(R.id.tbDate);
			if (tbDate != null) {
				SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");

				tbDate.setText(sdf.format(voucher.getVouchDate()));
			}

			TextView tbPacketsCount = (TextView) findViewById(R.id.tbPacketsCount);
			if (tbPacketsCount != null)
				tbPacketsCount.setText(String.valueOf(voucher.getVouchCount()));

			// TextView tbPacketsWeight = (TextView)
			// findViewById(R.id.tbPacketsWeight);
			// if (tbPacketsWeight != null)
			// tbPacketsWeight
			// .setText(String.valueOf(voucher.ge()));

			TextView tbSenderName = (TextView) findViewById(R.id.tbSenderName);
			if (tbSenderName != null)
				tbSenderName.setText(voucher.getSenderName());

			TextView tbSenderAddress = (TextView) findViewById(R.id.tbSenderAddress);
			if (tbSenderAddress != null) {
				String address = "";
				if (voucher.getSenderCity() != null)
					address = voucher.getSenderCity();
				if (voucher.getSenderArea() != null
						&& voucher.getSenderArea().equals("") == false) {
					address += ", " + voucher.getSenderArea();
				}
				if (voucher.getSenderAddress() != null
						&& voucher.getSenderAddress().equals("") == false) {
					address += ", " + voucher.getSenderAddress();
				}

				tbSenderAddress.setText(address);
			}

			TextView tbSenderPhone = (TextView) findViewById(R.id.tbSenderPhone);
			if (tbSenderPhone != null)
				tbSenderPhone.setText(voucher.getSenderGSM());

			TextView tbReceiverName = (TextView) findViewById(R.id.tbReceiverName);
			if (tbReceiverName != null)
				tbReceiverName.setText(voucher.getReceiverName());

			TextView tbReceiverAddress = (TextView) findViewById(R.id.tbReceiverAddress);

			if (tbReceiverAddress != null) {
				String address = "";
				if (voucher.getReceiverCity() != null)
					address = voucher.getReceiverCity();
				if (voucher.getReceiverArea() != null
						&& voucher.getReceiverArea().equals("") == false) {
					address += ", " + voucher.getReceiverArea();
				}
				if (voucher.getReceiverAddress() != null
						&& voucher.getReceiverAddress().equals("") == false) {
					address += ", " + voucher.getReceiverAddress();
				}

				tbReceiverAddress.setText(address);
			}
			/*
			 * if (tbReceiverAddress != null)
			 * tbReceiverAddress.setText(voucher.getReceiverCity() + ", " +
			 * voucher.getReceiverArea() + ", " + voucher.getReceiverAddress());
			 */

			TextView tbReceiverPhone = (TextView) findViewById(R.id.tbReceiverPhone);
			if (tbReceiverPhone != null)
				tbReceiverPhone.setText(voucher.getReceiverGSM());

			TextView tbCashPayment = (TextView) findViewById(R.id.tbCashPayment);
			if (tbCashPayment != null)
				tbCashPayment
						.setText(String.valueOf(voucher.getExpCashValue()));

			TextView tbCheckPayment = (TextView) findViewById(R.id.tbCheckPayment);
			if (tbCheckPayment != null)
				tbCheckPayment.setText(String.valueOf(voucher
						.getExpCheckValue()));

			TextView tbChecksCount = (TextView) findViewById(R.id.tbChecksCount);
			if (tbChecksCount != null)
				tbChecksCount
						.setText(String.valueOf(voucher.getExpCheckCount()));

			TextView tbNotes = (TextView) findViewById(R.id.tbNotes);
			if (tbNotes != null)
				tbNotes.setText(voucher.getVouchMemo());

			TextView tbDeliveryHour = (TextView) findViewById(R.id.tbDeliveryHour);
			if (tbDeliveryHour != null)
				tbDeliveryHour.setText(voucher.DeliveryHour);

			ivPhotos = (ImageView) findViewById(R.id.ivPhoto);
			String filesDir = app_settings.GetImagesDir(this);
			// ivPhotos.setImageURI(Uri.parse(filesDir + "/" +
			// voucher.ImageName));

			// new BitmapWorkerTask(ivPhotos).execute(filesDir + "/" +
			// voucher.ImageName);

			// TextView tbCharges = (TextView) findViewById(R.id.tbCharges);
			// if (tbCharges != null)
			// tbCharges.setText(voucher.Charges);
			layoutCharges = (LinearLayout) findViewById(R.id.layoutCharges);
			layoutTrans = (LinearLayout) findViewById(R.id.layoutTrans);

			TextView tbCustomerType = (TextView) findViewById(R.id.tbCustomerType);
			if (tbCustomerType != null) {
				if (voucher.CustomerType != null
						&& voucher.CustomerType.equals("1")) {
					tbCustomerType.setText(getString(R.string.CustomerSender));
				} else if (voucher.CustomerType != null
						&& voucher.CustomerType.equals("2")) {
					tbCustomerType
							.setText(getString(R.string.CustomerReceiver));
				}

			}

			TextView tbIfIsCreditValue = (TextView) findViewById(R.id.tbIfIsCreditValue);
			if (tbIfIsCreditValue != null)
				tbIfIsCreditValue.setText(String
						.valueOf(voucher.IfIsCreditValue));

			TextView lblParcelStatus = (TextView) findViewById(R.id.lblParcelStatus);
			CheckBox chbParcelStatus = (CheckBox) findViewById(R.id.chbParcelStatus);
			if (chbParcelStatus != null) {
				if (voucher.ParcelStatus == 0) {
					chbParcelStatus.setVisibility(CheckBox.GONE);
					lblParcelStatus.setVisibility(CheckBox.GONE);
				} else {

					chbParcelStatus.setVisibility(CheckBox.VISIBLE);
					lblParcelStatus.setVisibility(CheckBox.VISIBLE);
					chbParcelStatus.setEnabled(false);
					if (voucher.ParcelStatus == 1 || voucher.ParcelStatus == 2)
						chbParcelStatus.setChecked(true);
					else
						chbParcelStatus.setChecked(false);
				}
				if (voucher.ParcelStatus == 2 || voucher.ParcelStatus == 3) {
					// TextView lblParcelStatus =
					// (TextView)findViewById(R.id.lblde)

				}
			}

			ImageView ivSign = (ImageView) findViewById(R.id.ivSignature);
			if (ivSign != null) {
				if (voucher.getTransSignature() != null) {
					ivSign.setImageBitmap(voucher.getTransSignature());
				}
			}

			btnAccept = (Button) findViewById(R.id.btnAccept);
			btnDecline = (Button) findViewById(R.id.btnDecline);
			// btnDeliver = (Button) findViewById(R.id.btnDone);
			if (voucher.getDocType().equals("5")
					&& voucher.getTransType().equals("2")) {
				// btnAccept.setVisibility(Button.VISIBLE);
				// btnDecline.setVisibility(Button.VISIBLE);
				layoutBottom.setVisibility(LinearLayout.VISIBLE);
				// btnDeliver.setVisibility(Button.INVISIBLE);
			} else {
				// btnAccept.setVisibility(Button.INVISIBLE);
				// btnDecline.setVisibility(Button.INVISIBLE);
				layoutBottom.setVisibility(LinearLayout.GONE);
				// btnDeliver.setVisibility(Button.VISIBLE);
			}

			LoadCharges();
			LoadTrans();
		}

	}

	ArrayList<CourierTran> lstTrans;

	private void LoadTrans() {
		lstTrans = CourierTran.GetCourierTrans(this, voucher.getCourierID());

		for (CourierTran tran : lstTrans) {
			LinearLayout l = new LinearLayout(this);
			l.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
					LayoutParams.WRAP_CONTENT));
			l.setOrientation(LinearLayout.HORIZONTAL);
			l.setWeightSum(1);

			TextView lblCash = new TextView(this);
			LinearLayout.LayoutParams paramsCash = new LinearLayout.LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			paramsCash.weight = 0.5f;
			lblCash.setLayoutParams(paramsCash);
			String dateString = android.text.format.DateFormat.format(
					"dd/MM/yyyy kk:mm", tran.TransDate).toString();
			lblCash.setText(dateString);

			TextView lblCheck = new TextView(this);
			LinearLayout.LayoutParams paramsCheck = new LinearLayout.LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			paramsCheck.weight = 0.5f;
			lblCheck.setLayoutParams(paramsCheck);
			String vouchStatus = Base.GetVoucherTypeString(Integer
					.valueOf((tran.TransType)));
			lblCheck.setText(vouchStatus);

			l.addView(lblCash);
			l.addView(lblCheck);

			layoutTrans.addView(l);

			if (tran.TransSignature != null) {
				ImageView imgSign = new ImageView(this);
				LinearLayout.LayoutParams paramsImageSign = new LinearLayout.LayoutParams(
						LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
				paramsImageSign.height = 80;
				imgSign.setLayoutParams(paramsImageSign);
				imgSign.setImageBitmap(tran.TransSignature);
				layoutTrans.addView(imgSign);
			}

		}
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

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (voucher.getTransSignature() != null)
			voucher.getTransSignature().recycle();
		
		if(lstTrans != null)
		{
			for(CourierTran tran : lstTrans)
			{
				if(tran.TransSignature != null)
					tran.TransSignature.recycle();
			}
		}
	};

	public void ShowOnMap() {
		String googleQuery = "";
		// show map with direction to receiver addres
		if (voucher.getDocType().equals("5")) {
			googleQuery = "http://maps.google.com/maps?q="
					+ voucher.getSenderCity() + " " + voucher.getSenderArea()
					+ " " + voucher.getSenderAddress();
		} else {
			googleQuery = "http://maps.google.com/maps?q="
					+ voucher.getReceiverCity() + " "
					+ voucher.getReceiverArea() + " "
					+ voucher.getReceiverAddress();
		}
		Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
				Uri.parse(googleQuery));
		// Uri.parse("http://maps.google.com/maps?q=ruse riga 8"));
		// Uri.parse("http://maps.google.com/navigation?q=ruse riga 8"));

		intent.setClassName("com.google.android.apps.maps",
				"com.google.android.maps.MapsActivity");
		startActivity(intent);

	}

	public void btnDeliver_OnClick(View v) {
		Deliver();
	}

	public void btnGetDirection_OnClick(View v) {
		// ShowOnMap();
		GetRout();
	}

	public void btnAccept_OnClick(View v) {
		AcceptOrder();
	}

	private void AcceptOrder() {

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(R.string.SaveDialog)
				.setCancelable(false)
				.setPositiveButton(R.string.Yes,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								try {
									voucher.AcceptOrder(getApplicationContext());
									//DBUtil.SyncVouchers(VoucherDetails.this);
									new SyncVouchersTask().execute(getApplicationContext());
								} catch (ULjException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								// btnAccept.setVisibility(Button.INVISIBLE);
								// btnDecline.setVisibility(Button.INVISIBLE);
								layoutBottom.setVisibility(LinearLayout.GONE);
								// btnDeliver.setVisibility(Button.VISIBLE);
							}
						})
				.setNegativeButton(R.string.No,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								// open details screen

							}
						});
		AlertDialog alert = builder.create();
		alert.show();
	}

	private void DeclineOrder() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(R.string.SaveDialog)
				.setCancelable(false)
				.setPositiveButton(R.string.Yes,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								try {
									voucher.DeclineOrder(getApplicationContext());
									//DBUtil.SyncVouchers(VoucherDetails.this);
									new SyncVouchersTask().execute(getApplicationContext());
								} catch (ULjException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}

								// btnAccept.setVisibility(Button.INVISIBLE);
								// btnDecline.setVisibility(Button.INVISIBLE);
								layoutBottom.setVisibility(LinearLayout.GONE);
								// btnDeliver.setVisibility(Button.VISIBLE);
							}
						})
				.setNegativeButton(R.string.No,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								// open details screen

							}
						});
		AlertDialog alert = builder.create();
		alert.show();
	}

	public void btnDecline_OnClick(View v) {
		DeclineOrder();
	}

	private void Deliver() {
		Intent i = new Intent(VoucherDetails.this, VoucherDelivery.class);
		i.putExtra("CourierID", voucher.getCourierID());
		startActivity(i);
		finish();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.voucher_details_menu, menu);
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

	private void GetRout() {
		Intent showMap = new Intent(this, PlaceLocation.class);
		showMap.putExtra("PlaceLat", voucher.Latitude);
		showMap.putExtra("PlaceLng", voucher.Longitude);
		showMap.putExtra("PlaceCategoryID", 0);
		showMap.putExtra("PlaceName", voucher.getVouchNumber());
		if (voucher.getDocType().equals("5"))
			showMap.putExtra("PlaceCategoryName", voucher.getSenderAddress());
		else
			showMap.putExtra("PlaceCategoryName", voucher.getReceiverAddress());
		showMap.putExtra("Rating", 0);

		startActivity(showMap);
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
		if (voucher != null && voucher.getReceiverGSM() != "") {
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

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {

			// open menu
			// Intent i = new Intent(CourierDetails.this,
			// ListTodayVouchers.class);
			// startActivity(i);
			finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	public void LeftButtonCLicked(View v) {
		// TODO Auto-generated method stub
		Deliver();
	}

	public void RightButtonCLicked(View v) {
		// TODO Auto-generated method stub
		GetRout();
	}

	@Override
	public void RightButton1CLicked(View v) {
		// TODO Auto-generated method stub

	}

}
