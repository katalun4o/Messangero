package com.inobix.messangero.map;

import java.text.DecimalFormat;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.OverlayItem;

public class MyOverlayItem extends OverlayItem
{
	public boolean IsTapped = false;
	public float CourierID = 0;
	public String Rating = "";
	public String PlaceCategoryName = "";

	public MyOverlayItem(GeoPoint point, String title, String snippet,
			float courierID, double rating, String placeCategoryName)
	{
		super(point, title, snippet);
		CourierID = courierID;
		DecimalFormat df = new DecimalFormat("0.0");
		Rating = df.format(rating);
		PlaceCategoryName = placeCategoryName;
	}

}
