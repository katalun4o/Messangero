package com.inobix.messangero.map;

import java.text.DecimalFormat;

import android.graphics.Bitmap;

import com.google.android.gms.maps.model.LatLng;

public class MarkerInfo
{
	public int PlaceID = 0;
	public String Name = "";
	public String Address = "";
	public String Rating = "";
	public double RatingDouble = 0;
	public String Discount = "";
	public double DiscountDouble = 0;
	public String PlaceCategoryName = "";	
	public boolean MultiDiscMark = false;
	public Bitmap Icon;
	public LatLng Position;

	public MarkerInfo(LatLng position, String title, String snippet,
			int placeID, double rating, double discount,
			String placeCategoryName, boolean multiDiscMark)
	{
		PlaceID = placeID;
		Position = position;
		DecimalFormat df = new DecimalFormat("0.0");
		Rating = df.format(rating);
		RatingDouble = rating;
		DiscountDouble = discount;
		DecimalFormat df1 = new DecimalFormat("##0.##");
		MultiDiscMark = multiDiscMark;
		if(discount != 0)
		{
			Discount = df1.format(discount) + "%";
		}
		PlaceCategoryName = placeCategoryName;
		Name = title;
		Address = snippet;
	}
}
