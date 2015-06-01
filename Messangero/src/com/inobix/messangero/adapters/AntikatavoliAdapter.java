package com.inobix.messangero.adapters;

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

public class AntikatavoliAdapter extends ArrayAdapter<Courier> {

	private ArrayList<Courier> items;

	public AntikatavoliAdapter(Context context, int textViewResourceId,
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
			v = vi.inflate(R.layout.row_antikatavoli, null);
		}
		Courier o = items.get(position);

		if (o != null) 
		{
			TextView tbNumber = (TextView) v.findViewById(R.id.tbVoucherNumber);
			TextView tbCashExp = (TextView) v.findViewById(R.id.tbCashExp);
			TextView tbCashRec = (TextView) v.findViewById(R.id.tbCashRec);
			TextView tbCheckExp = (TextView) v.findViewById(R.id.tbCheckExp);
			TextView tbCheckRec = (TextView) v.findViewById(R.id.tbCheckRec);
			
			tbNumber.setText(o.getVouchNumber());
			
			DecimalFormat df = new DecimalFormat("########0.00");
			
			tbCashExp.setText(df.format(o.getExpCashValue()));
			tbCashRec.setText(df.format(o.getRealCashValueReceived()));
			tbCheckExp.setText(df.format(o.getExpCheckValue()));
			tbCheckRec.setText(df.format(o.getRealCheckValueReceived()));
			
		}
		return v;
	}
}
