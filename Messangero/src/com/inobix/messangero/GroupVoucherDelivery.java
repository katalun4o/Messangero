package com.inobix.messangero;

import java.nio.channels.SelectableChannel;
import java.util.ArrayList;
import java.util.Arrays;

import com.crashlytics.android.Crashlytics;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.ianywhere.ultralitejni12.ULjException;

import DAL.Courier;
import android.app.Activity;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.view.WindowManager;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class GroupVoucherDelivery extends Activity {
	public static final int BARCODE_SCANNER_REQUEST_CODE = 7456;
	public static final int SELECT_VOUCHERS_REQUEST_CODE = 7457;
	ListView lvVouchers;
	ArrayList<String> lstNumbers;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		Base.LoadLocale(this);
		setContentView(R.layout.group_voucher_delivery);

		lvVouchers = (ListView) findViewById(R.id.lstVouchers);
		registerForContextMenu(lvVouchers);

		lstNumbers = new ArrayList<String>();
		// lstNumbers.add("12313");
		// lstNumbers.add("56456");
		// lstNumbers.add("67567");

		RefreshList();
	}

	private void RefreshList() {
		lvVouchers.setAdapter(new GroupVoucherDeliveryArrayAdapter(this,
				R.layout.group_voucher_delivery_row, lstNumbers));
	}

	String resultString = "";

	public void btnOK_OnClick(View v) {
		resultString = "";
		for (String s : lstNumbers) {
			resultString += s + ";";
		}
		if (resultString.length() > 0 && resultString.contains(";"))
			resultString = resultString.substring(0,
					resultString.lastIndexOf(";"));

		Intent i = new Intent(GroupVoucherDelivery.this, VoucherDelivery.class);
		i.putExtra("VouchersNumbers", resultString);
		startActivity(i);

		finish();

	}

	public void btnCancel_OnClick(View v) {
		finish();
	}

	public void btnScanVoucher_OnClick(View v) {
		ScanNewVoucher();
	}

	EditText tbNumber;
	Dialog dialog;

	public void btnAddVoucher_OnClick(View v) {
		ShowSelectDialog();
		// ShowEditDialog(null);
	}

	private void ShowSelectDialog() {
		Intent startSelectActivity = new Intent(this, SelectVouchers.class);
		String nums = "";
		if(lstNumbers.size() > 0)
		{
		for(String s : lstNumbers)
		{
			nums += s + ",";
		}
		if(nums.length() > 0)
			nums = nums.substring(0, nums.length() - 1);
		}
		startSelectActivity.putExtra("Numbers", nums);
		startActivityForResult(startSelectActivity,
				SELECT_VOUCHERS_REQUEST_CODE);
	}

	private void ShowEditDialog(String val) {
		dialog = new Dialog(this, R.style.cust_dialog);
		dialog.setTitle(getResources().getString(R.string.EnterNumberDialog));
		dialog.setContentView(R.layout.enter_number_dialog);
		dialog.getWindow().setLayout(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT);

		Button btnOK = (Button) dialog.findViewById(R.id.btnOK);
		Button btnCancel = (Button) dialog.findViewById(R.id.btnCancel);
		tbNumber = (EditText) dialog.findViewById(R.id.tbNewVoucherNumber);

		if (val != null) {
			tbNumber.setText(val);
		}

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
				boolean exists = CheckVoucherExists(number);
				if (exists == false) {
					tbNumber.selectAll();
					return;
				}
				if (editIndex == -1) {
					lstNumbers.add(tbNumber.getText().toString());
				} else {
					lstNumbers.set(editIndex, tbNumber.getText().toString());
					editIndex = -1;
				}
				RefreshList();

				((InputMethodManager) GroupVoucherDelivery.this
						.getSystemService(Context.INPUT_METHOD_SERVICE))
						.hideSoftInputFromWindow(tbNumber.getWindowToken(), 0);

				dialog.cancel();
			}
		});

		btnCancel.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				((InputMethodManager) GroupVoucherDelivery.this
						.getSystemService(Context.INPUT_METHOD_SERVICE))
						.hideSoftInputFromWindow(tbNumber.getWindowToken(), 0);
				dialog.cancel();
			}
		});
		dialog.show();
	}

	private void ScanNewVoucher() {
		/*
		 * if (PreferencesUtil.SearchAllBarcodeTypes)
		 * IntentIntegrator.initiateScan(GroupVoucherDelivery.this); else if
		 * (PreferencesUtil.SearchOnlyCode39)
		 * IntentIntegrator.initiateScan(GroupVoucherDelivery.this,
		 * IntentIntegrator.DEFAULT_TITLE, IntentIntegrator.DEFAULT_MESSAGE,
		 * IntentIntegrator.DEFAULT_YES, IntentIntegrator.DEFAULT_NO,
		 * "CODE_39");
		 */

		Intent intent = new Intent("com.risoft.zxing.client.android.SCAN");
		// intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
		startActivityForResult(intent, BARCODE_SCANNER_REQUEST_CODE);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);

		// if (requestCode == IntentIntegrator.REQUEST_CODE)
		if (requestCode == BARCODE_SCANNER_REQUEST_CODE) {
			if (resultCode != RESULT_CANCELED) {
				String number = data.getStringExtra("SCAN_RESULT");

				// IntentResult scanResult =
				// IntentIntegrator.parseActivityResult(
				// requestCode, resultCode, data);
				// if (scanResult != null)
				// {
				// String number = scanResult.getContents();

				// check if voucher with that number exists
				if (CheckVoucherExists(number) == false)
					return;

				if (editIndex == -1)
					lstNumbers.add(number);
				else {
					lstNumbers.set(editIndex, number);
					editIndex = -1;
				}
				RefreshList();
				// add number to the list
				// }
			}
		} else if (requestCode == SELECT_VOUCHERS_REQUEST_CODE) {
			if(resultCode != SelectVouchers.RESULT_OK)
			{
				return;
			}
			String res = data.getStringExtra("Result");
			Log.d("MessangeroLog",res);
			if (res.equals("") == false) {
				String[] numbers = res.split(",");
				if (numbers.length > 0) {
					for (String num : numbers) 
					{
						boolean exists = false;
						for(String s :lstNumbers)
						{
							if(s.equals(num) == true)
							{
								exists = true;
								break;
							}
						}
						if(exists == false)
							lstNumbers.add(num);
					}
					RefreshList();
				}
			}

		}
	}

	private boolean CheckVoucherExists(String number) {
		Courier c = new Courier();
		try {
			c.LoadCourier(this, number);

		} catch (ULjException e) {
			// TODO Auto-generated catch block
			Crashlytics.logException(e);
		}
		if (c.getCourierID() == 0 || c.getDocType() == "5") {
			Toast.makeText(
					getApplicationContext(),
					getApplicationContext().getResources().getString(
							R.string.VoucherWithNumNotExist),
					Toast.LENGTH_SHORT).show();
			return false;
		} else
			return true;
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.order_voucher_numbers_context_menu, menu);
	}

	int editIndex = -1;

	public boolean onContextItemSelected(android.view.MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();
		switch (item.getItemId()) {
		case R.id.miEdit:
			editIndex = info.position;
			String currentNumber = (String) lvVouchers
					.getItemAtPosition(editIndex);
			ShowEditDialog(currentNumber);
			return true;
		case R.id.miScan:
			editIndex = info.position;
			ScanNewVoucher();
			return true;
		case R.id.miDelete:
			lstNumbers.remove(info.position);
			RefreshList();
			return true;
		default:
			return super.onContextItemSelected(item);
		}
	}
}
