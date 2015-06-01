package com.inobix.messangero.adapters;

import java.util.ArrayList;

import com.inobix.messangero.R;

import DAL.NotDeliveredType;
import DAL.ScanResult;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class AdapterScanResult extends ArrayAdapter<ScanResult> {
	
	Context currentContext;
	private ArrayList<ScanResult> data;

	public AdapterScanResult(Context context, int textViewResourceId,
			ArrayList<ScanResult> objects)
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
			v = vi.inflate(R.layout.row_scan_result, null);
		}
		
		ScanResult item = data.get(position);
		
		TextView lblDesc = (TextView) v.findViewById(R.id.lblOption);
		lblDesc.setText(item.Desc);
		
		return v;
	}

}
