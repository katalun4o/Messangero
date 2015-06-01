package com.inobix.messangero;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import com.ianywhere.ultralitejni12.Connection;
import com.ianywhere.ultralitejni12.PreparedStatement;
import com.ianywhere.ultralitejni12.ResultSet;
import com.ianywhere.ultralitejni12.ULjException;
import com.inobix.messangero.adapters.AntikatavoliAdapter;
import com.inobix.messangero.common.app_settings;

import DAL.Courier;
import DAL.DBUtil;
import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

public class SearchResultScreen extends ListActivity {

	String number;
	int type;
	int docType;
	int reportType;
	String dateFromString;
	Date dateFrom;
	String dateToString;
	Date dateTo;
	LinearLayout listHeader;
	LinearLayout listFooter;
	ProgressBar pbLoading;

	TextView tbCashExpTotal;
	TextView tbCashRecTotal;
	TextView tbCheckExpTotal;
	TextView tbCheckRecTotal;

	//float cashExpectedTotal = 0;
	//float cashReceivedTotal = 0;
	//float checkExpectedTotal = 0;
	//float checkReceivedTotal = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		Base.LoadLocale(this);
		setContentView(R.layout.search_result_screen);
		
		listHeader = (LinearLayout) findViewById(R.id.listHeader);
		listFooter = (LinearLayout) findViewById(R.id.listFooter);
		tbCashExpTotal = (TextView) findViewById(R.id.tbCashExpTotal);
		tbCashRecTotal = (TextView) findViewById(R.id.tbCashRecTotal);
		tbCheckExpTotal = (TextView) findViewById(R.id.tbCheckExpTotal);
		tbCheckRecTotal = (TextView) findViewById(R.id.tbCheckRecTotal);
		pbLoading = (ProgressBar) findViewById(R.id.pbLoading);

		Bundle extras = getIntent().getExtras();
		number = extras.getString("VoucherNumber");
		type = extras.getInt("VouchType");
		docType = extras.getInt("DocType");
		reportType = extras.getInt("ReportType");

		if (reportType == 0) {
			listHeader.setVisibility(LinearLayout.GONE);
			listFooter.setVisibility(LinearLayout.GONE);
		} else {
			listHeader.setVisibility(LinearLayout.VISIBLE);
			listFooter.setVisibility(LinearLayout.VISIBLE);
		}

		dateFromString = extras.getString("DateFrom");
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		try {
			dateFrom = df.parse(dateFromString);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// dateFrom = new Date(dateFromString);
		dateToString = extras.getString("DateTo");

		try {
			dateTo = df.parse(dateToString);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// dateTo= new Date(dateToString);

		// LoadVouchers(number, type, dateFrom, dateTo);
	}

	@Override
	protected void onResume() {
		super.onResume();
		LoadVoucherTask();
	};

	private void LoadVoucherTask() {
		pbLoading.setVisibility(ProgressBar.VISIBLE);
		this.getListView().setAdapter(
				new MyArrayAdapter(SearchResultScreen.this, R.layout.row,
						new ArrayList<Courier>()));
		new LoadTask() {
			@Override
			protected void onPostExecute(SearchResult result) {
				// TODO Auto-generated method stub
				super.onPostExecute(result);

				if (reportType == 0)
					setListAdapter(new MyArrayAdapter(SearchResultScreen.this,
							R.layout.row, result.Result));
				else if (reportType == 1) {
					setListAdapter(new AntikatavoliAdapter(
							SearchResultScreen.this, R.layout.row, result.Result));

					DecimalFormat df = new DecimalFormat("########0.00");
					tbCashExpTotal.setText(df.format(result.cashExpectedTotal));
					tbCashRecTotal.setText(df.format(result.cashReceivedTotal));
					tbCheckExpTotal.setText(df.format(result.checkExpectedTotal));
					tbCheckRecTotal.setText(df.format(result.checkReceivedTotal));
				}

				pbLoading.setVisibility(ProgressBar.GONE);
			}
		}.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,number, reportType, docType, type, dateFrom, dateTo);
		//}.execute(number, reportType, docType, type, dateFrom, dateTo);
		
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		OpenDetailsScreen(((Courier) l.getItemAtPosition(position))
				.getCourierID());
	}

	private void OpenDetailsScreen(Float voucherID) {
		Intent i = new Intent(SearchResultScreen.this, VoucherDetails.class);
		i.putExtra("CourierID", voucherID);
		startActivity(i);
		// finish();
	}

	private SearchResult LoadVouchers(String number, int reportType,
			int docType, int vouchType, Date dateFrom, Date dateTo) {

		ArrayList<Courier> lstResult = new ArrayList<Courier>();

		float cashExpectedTotal = 0;
		float cashReceivedTotal = 0;
		float checkExpectedTotal = 0;
		float checkReceivedTotal = 0;

		try {
			// dbUtil = new DBUtil(this);
			// _conn = dbUtil.get_conn();
			Connection conn = DBUtil.CreateConnection(this);

			String docTypeString = "1,2,3,5";
			switch (docType) {
			case 1:
				docTypeString = "1";
				break;
			case 2:
				docTypeString = "2";
				break;
			case 3:
				docTypeString = "3";
				break;
			case 4:
				docTypeString = "5";
				break;
			default:
				docTypeString = "1,2,3,5";
				break;
			}

			String statusString = "4";
			if (vouchType == 0)
				statusString = "2,3,4,5,6,17,18,19,20";
			else {
				if (docTypeString.equals("5")) {
					switch (vouchType) {
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
					switch (vouchType) {
					case 1:
						statusString = "4";
						break;
					case 2:
						statusString = "5";
						break;
					case 3:
						statusString = "6";
						break;
					}
				}
			}

			String numberFilter = "";
			if (number != null && number.equals("") == false)
				numberFilter = " AND VouchNumber LIKE '%" + number + "%'";
			
			String antikatavoliFilter = "";
			if (reportType == 1)
				antikatavoliFilter = " AND (ExpCashValue IS NOT NULL AND ExpCashValue <> 0 OR "
						+ "ExpCheckValue IS NOT NULL AND  ExpCheckValue <> 0 OR "
						+ "ReceivedCashValue IS NOT NULL AND  ReceivedCashValue <> 0 OR "
						+ "ReceivedCheckValue IS NOT NULL AND  ReceivedCheckValue <> 0) ";

			// select all vouchers for today command
			StringBuffer sb = new StringBuffer(
					"SELECT   RemoteCourier.CourierID "
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
							// + ", TransSignature "
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
							+ " + SIN( RADIANS(:Lat) ) * SIN( RADIANS( Latitude ) ) ) ) AS Distance, OrderIndex, "
							+ " max(CourierCharge.CreditDebit) CreditDebit"
							+ " FROM RemoteCourier "
							+ " LEFT JOIN RCourierChargeTrans ON RCourierChargeTrans.CourierID = RemoteCourier.CourierID "
							+ " LEFT JOIN CourierCharge ON CourierCharge.CourierChargeID = RCourierChargeTrans.CourierChargeID "
							+ "WHERE DriverID = :DriverID AND \"TransDate\" >= :DateFrom AND \"TransDate\" < :DateTo "
							+ numberFilter 
							+ antikatavoliFilter + "AND TransType  IN ("
							+ statusString + ")  " + " AND DocType  IN ("
							+ docTypeString + ")"
							+ " GROUP BY "
							+ " RemoteCourier.CourierID "
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
							+ ",OrderIndex "
							+ ",IfIsCreditValue " );
			// WHERE \"Date\" >= ? AND \"Date\" < ?

		//	if (number != null && number.equals("") == false) 
	//		{
//				sb.append(" AND VouchNumber LIKE '%" + number + "%'");
			//}

			PreparedStatement ps = conn.prepareStatement(sb.toString());
			ps.set("Lat", PreferencesUtil.Latitude);
			ps.set("Lng", PreferencesUtil.Longitude);
			ps.set("DriverID", app_settings.CurrentDriverID);
			ps.set("DateFrom", dateFrom);
			ps.set("DateTo", Base.addDay(dateTo, 1));

			ResultSet rs = ps.executeQuery();
			// ArrayList<Courier> lstVouchers = new ArrayList<Courier>();

			// reads vouchers into array
			while (rs.next()) {
				Courier c = new Courier();
				c.GetSignature = false;
				c.Fetch(rs);
				String cd = rs.getString("CreditDebit");
				if(cd != null && cd.equals("2"))
				{
					c.setExpCashValue(0-c.getExpCashValue());
					c.setExpCheckValue(0-c.getExpCheckValue());
				}
				lstResult.add(c);

				cashExpectedTotal += c.getExpCashValue();
				cashReceivedTotal += c.getRealCashValueReceived();
				checkExpectedTotal += c.getExpCheckValue();
				checkReceivedTotal += c.getRealCheckValueReceived();
			}
			rs.close();
			ps.close();
			conn.release();
			// when item in the list is clicked starts a new activity
			// with vouchers details
			/*
			 * if (reportType == 0) setListAdapter(new MyArrayAdapter(this,
			 * R.layout.row, lstVouchers)); else if (reportType == 1)
			 * setListAdapter(new AntikatavoliAdapter(this, R.layout.row,
			 * lstVouchers));
			 */

		} catch (ULjException ex) {
			// tbMessage.setText(ex.getMessage());
			ex.printStackTrace();
		}

		SearchResult res = new SearchResult();
		res.Result = lstResult;
		res.cashExpectedTotal = cashExpectedTotal;
		res.cashReceivedTotal = cashReceivedTotal;
		res.checkExpectedTotal = checkExpectedTotal;
		res.checkReceivedTotal = checkReceivedTotal;
		return res;
	}
	
	private class SearchResult
	{
		ArrayList<Courier> Result;
		float cashExpectedTotal;
		float cashReceivedTotal;
		float checkExpectedTotal;
		float checkReceivedTotal;
	}

	private class LoadTask extends
			AsyncTask<Object, ArrayList<Courier>, SearchResult> {

		@Override
		protected SearchResult doInBackground(Object... params) {

			String number = (String) params[0];
			int reportType = (Integer) params[1];
			int docType = (Integer) params[2];
			int voucherType = (Integer) params[3];
			Date dateFrom = (Date) params[4];
			Date dateTo = (Date) params[5];

			SearchResult res = LoadVouchers(number, reportType, docType,
					voucherType, dateFrom, dateTo);
			return res;
		}

		@Override
		protected void onPostExecute(SearchResult result) {

		};
	}

}
