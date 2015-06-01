package com.inobix.messangero.adapters;

import java.util.ArrayList;

import com.inobix.messangero.R;

import DAL.Courier;
import DAL.ScanResult;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

public class SelectVoucher extends ArrayAdapter<Courier> {
	Context currentContext;
	private ArrayList<Courier> data;

	public SelectVoucher(Context context, int textViewResourceId,
			ArrayList<Courier> objects)
	{
		super(context, textViewResourceId, objects);
		// TODO Auto-generated constructor stub
		currentContext = context;
		data = objects;
	}
	
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{   
		View v = convertView;
		if (v == null)
		{
			LayoutInflater vi = (LayoutInflater) getContext().getSystemService(
					Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(R.layout.row_checked_voucher, null);
		}
		
		Courier item = data.get(position);
		
		CheckBox chbSelected = (CheckBox) v.findViewById(R.id.chbSelected);
		TextView tbNumber = (TextView) v.findViewById(R.id.tbNumber);
		TextView tbReceiver = (TextView) v.findViewById(R.id.tbReceiverName);
		TextView tbAddress = (TextView) v.findViewById(R.id.tbReceiverAddress);
		
		tbNumber.setText(item.getVouchNumber());
		tbReceiver.setText(item.getReceiverName());
		tbAddress.setText(item.getReceiverAddress());
				
		chbSelected.setChecked(item.IsSelected);
		chbSelected.setTag(item);
				
		
		return v;
	}
}
