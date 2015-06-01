package com.inobix.messangero;

import com.inobix.messangero.R;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class MenuArrayAdapter extends ArrayAdapter<MenuItem>
{
	private ArrayList<MenuItem> items;
	public MenuArrayAdapter(Context context, int textViewResourceId,
			ArrayList<MenuItem> objects)
	{
		super(context, textViewResourceId, objects);
		// TODO Auto-generated constructor stub
		items = objects;
	}
	
	public void ShowProgress()
	{
		
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		View v = convertView;
		
		if (v == null)
		{
			LayoutInflater vi = (LayoutInflater) getContext().getSystemService(
					Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(R.layout.menu_row, null);
		}
		
		MenuItem item = items.get(position);
		if(item != null)
		{
			ImageView ivMenuIcon = (ImageView) v.findViewById(R.id.menuIcon);
			ivMenuIcon.setImageResource(item.getImageID());
			
			TextView tbCaption = (TextView) v.findViewById(R.id.menuCaption);
			tbCaption.setText(item.getCaption());
			
			/*ProgressBar pbSync = (ProgressBar)v.findViewById(R.id.pbSync);
			if(item.getCaption() == getContext().getString(R.string.miSync))
			{
				pbSync.setVisibility(ProgressBar.VISIBLE);
			}
			else
			{
				pbSync.setVisibility(ProgressBar.GONE);
			}*/
		}
		
		return v;
	}
	
}
