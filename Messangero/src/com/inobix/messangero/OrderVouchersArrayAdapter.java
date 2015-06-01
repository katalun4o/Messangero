package com.inobix.messangero;

import java.util.ArrayList;

import DAL.Courier;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class OrderVouchersArrayAdapter extends ArrayAdapter<String>
{
	private ArrayList<String> items;

	public OrderVouchersArrayAdapter(Context context, int textViewResourceId,
			ArrayList<String> objects)
	{
		super(context, textViewResourceId, objects);
		// TODO Auto-generated constructor stub
		items = objects;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		View v = convertView;
		if (v == null)
		{
			LayoutInflater vi = (LayoutInflater) getContext().getSystemService(
					Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(R.layout.order_voucher_number_row, null);
		}
		String currentNum = items.get(position);
		
		TextView lblNumber = (TextView) v.findViewById(R.id.lblNumber);
		lblNumber.setText(currentNum);
		
		return v;
	}
}
