package com.inobix.messangero;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import com.ianywhere.ultralitejni12.Connection;
import com.ianywhere.ultralitejni12.PreparedStatement;
import com.ianywhere.ultralitejni12.ResultSet;
import com.ianywhere.ultralitejni12.ULjException;
import com.inobix.messangero.adapters.SelectVoucher;
import com.inobix.messangero.common.IActionActivity;
import com.inobix.messangero.common.app_settings;

import DAL.Courier;
import DAL.DBUtil;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class SelectVouchers extends Activity implements IActionActivity {

	public static final int RESULT_OK = 1;
	public static final int RESULT_CANCEL = 2;

	//private int mYear;
	//private int mMonth;
	//private int mDay;

	ListView lvVouchers;
	ActionBar actionBar;
	String[] selectedNumbers;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_select_vouchers);

		lvVouchers = (ListView) findViewById(R.id.lvVouchers);
		actionBar = (ActionBar) findViewById(R.id.layoutActionBar);

		actionBar.SetLeftButtonText(getString(R.string.btnCancel));
		actionBar.SetRightButtonText(getString(R.string.btnDone));

		//String z = getIntent().getStringExtra("Zaek");
		String selectedNumbersString = getIntent().getStringExtra("Numbers");
		if(selectedNumbersString != null)
			selectedNumbers = selectedNumbersString.split(",");

		LoadVouchers();
	}

	ArrayList<Courier> lstVouchers;

	private void LoadVouchers() {
		lstVouchers = new ArrayList<Courier>();
		Connection conn = DBUtil.CreateConnection(this);

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
						+ ", TransSignature"
						+ ", Latitude"
						+ ", Longitude "
						+ ", DeliveryHour"
						+ ", ParcelStatus"
						+ ",CourierCustomerType "
						+ ",IfIsCreditValue "
						+ ",( 6371 * ACOS( COS( RADIANS(:Lat) ) * COS( RADIANS( Latitude ) ) * COS( RADIANS( Longitude ) - RADIANS(:Lng) )"
						+ " + SIN( RADIANS(:Lat) ) * SIN( RADIANS( Latitude ) ) ) ) AS Distance, OrderIndex "
						+ " FROM RemoteCourier WHERE DriverID = :DriverID AND \"TransDate\" >= :DateFrom AND "
						+ " \"TransDate\" < :DateTo "
						+ " AND TransType  IN (4)" + "ORDER BY OrderIndex,Distance");

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(app_settings.WorkDate);
		//mYear = calendar.get(Calendar.YEAR);
		//mMonth = calendar.get(Calendar.MONTH);
		//mDay = calendar.get(Calendar.DAY_OF_MONTH);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);

		Date dateFrom = calendar.getTime();		
		Date dateTo = Base.addDay(dateFrom, 1);

		PreparedStatement ps;
		try {
			ps = conn.prepareStatement(sb.toString());

			ps.set("Lat", PreferencesUtil.Latitude);
			ps.set("Lng", PreferencesUtil.Longitude);
			// driver id
			ps.set("DriverID", app_settings.CurrentDriverID);
			// start date
			ps.set("DateFrom", dateFrom);
			// end date
			ps.set("DateTo", dateTo);

			ResultSet rs = ps.executeQuery();

			// reads vouchers into array
			while (rs.next()) {
				Courier c = new Courier();
				c.GetSignature = false;
				c.Fetch(rs);

				if (selectedNumbers != null) {
					for (String s : selectedNumbers) {
						if (s.equals(c.getVouchNumber())) {
							c.IsSelected = true;
							Log.d("MessangeroLog","voucher prev checked: " + c.getVouchNumber());
						}
					}
				}
				lstVouchers.add(c);
			}

		} catch (ULjException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		final SelectVoucher adapter = new SelectVoucher(this, 0, lstVouchers);
		lvVouchers.setAdapter(adapter);

		lvVouchers.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Courier c = (Courier) adapter.getItem(arg2);
				CheckBox chbSelected = (CheckBox) arg1
						.findViewById(R.id.chbSelected);
				c.IsSelected = !c.IsSelected;
				chbSelected.setChecked(c.IsSelected);
				if(c.IsSelected)
					Log.d("MessangeroLog","select dialog checked: " + c.getVouchNumber());
				else
					Log.d("MessangeroLog","select dialog unchecked: " + c.getVouchNumber());
			}
		});

	}

	public void LeftButtonCLicked(View v) {
		setResult(RESULT_CANCEL);
		finish();
	}

	public void RightButtonCLicked(View v) {

		String res = "";
		for (Courier c : lstVouchers) {
			if (c.IsSelected) {
				Log.d("MessangeroLog","voucher selected: " + c.getVouchNumber());
				res += c.getVouchNumber() + ",";
			}
		}
		if (res.length() > 0) {
			res = res.substring(0, res.length() - 1);
		}

		Log.d("MessangeroLog","select dialog res: " + res);
		Intent resultIntent = new Intent();
		resultIntent.putExtra("Result", res);
		setResult(RESULT_OK, resultIntent);
		finish();
	}

	@Override
	public void RightButton1CLicked(View v) {
		// TODO Auto-generated method stub
		
	}

}
