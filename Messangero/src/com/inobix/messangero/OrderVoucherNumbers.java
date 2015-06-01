package com.inobix.messangero;

import java.util.ArrayList;
import java.util.Arrays;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import android.app.Activity;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import android.content.Intent;
import android.os.Bundle;
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

public class OrderVoucherNumbers extends ListActivity
{
	ArrayList<String> lstNumbers ;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		Base.LoadLocale(this);
		setContentView(R.layout.order_voucher_numbers);
		
		registerForContextMenu(getListView());
		
		lstNumbers = new ArrayList<String>();
		
		RefreshList();
	}

	private void RefreshList()
	{
		setListAdapter(new OrderVouchersArrayAdapter(this, R.layout.order_voucher_number_row, lstNumbers));
	}
	
	String resultString = "";
	public void btnOK_OnClick(View v)
	{
		resultString = "";
		for(String s:lstNumbers)
		{
			resultString += s + ";";
		}
		if(resultString.length() > 0 && resultString.contains(";"))
			resultString = resultString.substring(0, resultString.lastIndexOf(";"));
		
		Intent i = this.getIntent();
		i.putExtra("VouchersNumbers", resultString);
			
		if (getParent() == null) {
		    setResult(Activity.RESULT_OK,i );
		} else {
		    getParent().setResult(Activity.RESULT_OK, i);
		}
		finish();

	}
	
	public void btnCancel_OnClick(View v)
	{
		finish();
	}
	
	public void btnScanVoucher_OnClick(View v)
	{	
		ScanNewVoucher();
	}
	
	EditText tbNumber ;
	Dialog dialog;
	public void btnAddVoucher_OnClick(View v)
	{
		ShowEditDialog(null);
	}
	
	private void ShowEditDialog(String val)
	{
		dialog = new Dialog(this);
		dialog.setTitle("Enter Number");
		dialog.setContentView(R.layout.enter_number_dialog);		
		dialog.getWindow().setLayout(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		
		Button btnOK = (Button)dialog.findViewById(R.id.btnOK);
		Button btnCancel = (Button)dialog.findViewById(R.id.btnCancel);
		tbNumber = (EditText)dialog.findViewById(R.id.tbNewVoucherNumber);
		
		if(val != null)
		{
			tbNumber.setText(val);
		}		
		
		dialog.setOnShowListener(new OnShowListener()
		{
			
			public void onShow(DialogInterface dialog)
			{
				// TODO Auto-generated method stub
				getWindow().setSoftInputMode(
						WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
				tbNumber.selectAll();
				tbNumber.postDelayed(new Runnable()
				{
					public void run()
					{
						InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
						imm.showSoftInput(tbNumber, InputMethodManager.SHOW_FORCED);
					}
				}, 100);
			}
		});

		
		btnOK.setOnClickListener(new OnClickListener() {
            
                public void onClick(View v) {
                	if(editIndex == -1)
                		lstNumbers.add(tbNumber.getText().toString());
                	else
                	{
                		lstNumbers.set(editIndex, tbNumber.getText().toString());
                		editIndex = -1;
                	}
                	RefreshList();
                	
            		((InputMethodManager) OrderVoucherNumbers.this
            				.getSystemService(Context.INPUT_METHOD_SERVICE))
            				.hideSoftInputFromWindow(tbNumber.getWindowToken(), 0);
            		
                	dialog.cancel();
                }
            });
		
		btnCancel.setOnClickListener(new OnClickListener() {
            
            public void onClick(View v) {
        		((InputMethodManager) OrderVoucherNumbers.this
        				.getSystemService(Context.INPUT_METHOD_SERVICE))
        				.hideSoftInputFromWindow(tbNumber.getWindowToken(), 0);
            	dialog.cancel();
            }
        });
		dialog.show();
	}
	
	private void ScanNewVoucher()
	{
		if (PreferencesUtil.SearchAllBarcodeTypes)
			IntentIntegrator.initiateScan(OrderVoucherNumbers.this);
		else if (PreferencesUtil.SearchOnlyCode39)
			IntentIntegrator.initiateScan(OrderVoucherNumbers.this,
					IntentIntegrator.DEFAULT_TITLE,
					IntentIntegrator.DEFAULT_MESSAGE,
					IntentIntegrator.DEFAULT_YES,
					IntentIntegrator.DEFAULT_NO, "CODE_39");
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		
		if (requestCode == IntentIntegrator.REQUEST_CODE)
		{
			if (resultCode != RESULT_CANCELED)
			{
				IntentResult scanResult = IntentIntegrator.parseActivityResult(
						requestCode, resultCode, data);
				if (scanResult != null)
				{
					String number = scanResult.getContents();
					if(editIndex == -1)
						lstNumbers.add(number);
					else
					{
						lstNumbers.set(editIndex, number);
						editIndex = -1;
					}
					RefreshList();
					//add number to the list
				}
			}
		}
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
	    AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
	    switch (item.getItemId()) {
	        case R.id.miEdit:
	            editIndex = info.position;
	            String currentNumber = (String)this.getListView().getItemAtPosition(editIndex);
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
