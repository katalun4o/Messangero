package com.inobix.messangero;

import com.inobix.messangero.R;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import DAL.Courier;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.app.*;

public class MyArrayAdapter extends ArrayAdapter<Courier> {

	private ArrayList<Courier> items;

	public MyArrayAdapter(Context context, int textViewResourceId,
			ArrayList<Courier> objects) {
		super(context, textViewResourceId, objects);
		// TODO Auto-generated constructor stub
		items = objects;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		if (v == null) {
			LayoutInflater vi = (LayoutInflater) getContext().getSystemService(
					Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(R.layout.row, null);
		}
		Courier o = items.get(position);
		String docType = o.getDocType();
		if (o != null) {
			TextView tbNumber = (TextView) v.findViewById(R.id.tbNumber);
			TextView tbDistance = (TextView) v.findViewById(R.id.tbDistance);

			TextView tbDeliveryHour = (TextView) v
					.findViewById(R.id.tbDeliveryHour);
			tbDeliveryHour.setText(o.DeliveryHour);

			if (tbNumber != null) {
				tbNumber.setText(o.getVouchNumber());
			}
			if (tbDistance != null) {
				String distance = "";
				if (o.Distance < 1) {
					DecimalFormat df4 = new DecimalFormat("########0");
					distance = df4.format(o.Distance * 1000) + "m";
				} else {
					DecimalFormat df = new DecimalFormat("########0.0#");
					distance = df.format(o.Distance) + "km";
				}

				tbDistance.setText(distance);
			}

			TextView lblDocType = (TextView) v.findViewById(R.id.lblDocType);
			ImageView ivIcon = (ImageView) v.findViewById(R.id.icon);
			if (lblDocType != null) {
				if (o.getDocType().equals("1")) {
					lblDocType.setText(getContext().getString(
							R.string.stringVoucher));
					ivIcon.setImageResource(R.drawable.envmdpi);
				} else if (o.getDocType().equals("2")) {
					lblDocType.setText(getContext().getString(
							R.string.stringInvoice));
					ivIcon.setImageResource(R.drawable.invoice_icon);
				} else if (o.getDocType().equals("3")) {
					lblDocType.setText(getContext().getString(
							R.string.stringReceipt));
					ivIcon.setImageResource(R.drawable.receipt);
				} else if (o.getDocType().equals("5")) {
					lblDocType.setText(getContext().getString(
							R.string.stringOrder));
					ivIcon.setImageResource(R.drawable.envmdpi);
				}
			}

			TextView tbReceiverName = (TextView) v
					.findViewById(R.id.tbReceiverName);
			if (tbReceiverName != null) {
				if (docType.equals("5"))
					tbReceiverName.setText(o.getSenderName());
				else
					tbReceiverName.setText(o.getReceiverName());
			}

			TextView tbReceiverAddress = (TextView) v
					.findViewById(R.id.tbReceiverAddress);
			if (tbReceiverAddress != null) {
				if (docType.equals("5")) {
					String address = "";
					if (o.getSenderCity() != null)
						address = o.getSenderCity();
					if (o.getSenderArea() != null
							&& o.getSenderArea().equals("") == false) {
						address += ", " + o.getSenderArea();
					}
					if (o.getSenderAddress() != null
							&& o.getSenderAddress().equals("") == false) {
						address += ", " + o.getSenderAddress();
					}

					tbReceiverAddress.setText(address);
				} else {
					String address = "";
					if (o.getReceiverCity() != null)
						address = o.getReceiverCity();
					if (o.getReceiverArea() != null
							&& o.getReceiverArea().equals("") == false) {
						address += ", " + o.getReceiverArea();
					}
					if (o.getReceiverAddress() != null
							&& o.getReceiverAddress().equals("") == false) {
						address += ", " + o.getReceiverAddress();
					}

					tbReceiverAddress.setText(address);
				}
			}

			v.setBackgroundResource(android.R.color.white);
			if (o.DeliveryHour != null) {
				String[] delHours = o.DeliveryHour.split("-");
				if (delHours.length > 0) {
					String firstHour = delHours[0];
					String[] delHourParts = firstHour.split(":");
					if (delHourParts.length > 0) {
						String h = delHourParts[0];
						if (h != null && h.equals("") == false) {
							int hour = -1;
							try {
								hour = Integer.valueOf(h);
							} catch (Exception ex) {

							}

							Calendar c = Calendar.getInstance();
							int currHour = c.get(Calendar.HOUR_OF_DAY);
							if ((hour - currHour == 1)
									|| (hour - currHour == 0)) {
								v.setBackgroundResource(R.color.light_pink);
							}
						}
					}
				}
			}

			String status = o.getTransType();
			ImageView imgType = (ImageView) v.findViewById(R.id.iconType);
			if (imgType != null) {
				if (status.equals("19") && o.getDocType().equals("5")) {
					imgType.setImageResource(R.drawable.red_phone_16);
				} else if (status.equals("2") && o.getDocType().equals("5")) {
					// awaiting reply if the order is accepted
					imgType.setImageResource(R.drawable.question);
				} else if (status.equals("17")
						|| status.equals("5")
						|| ((status.equals("20") || status.equals("3")) && o
								.getDocType().equals("5"))) {
					// shows tick if the packet is delivered or the order is
					// accepted
					imgType.setImageResource(R.drawable.check16);
				} else if (status.equals("6")) {
					// shows X if the customer is not found
					imgType.setImageResource(R.drawable.deleteicon16);
				} else {
					imgType.setImageBitmap(null);
				}
			}

			ImageView imgCash = (ImageView) v.findViewById(R.id.iconCash);
			if (imgCash != null) {
				if (o.getExpCashValue() != 0F || o.getExpCheckValue() != 0F)
					imgCash.setVisibility(ImageView.VISIBLE);
				else
					imgCash.setVisibility(ImageView.INVISIBLE);
			}
		}
		return v;
	}
}
