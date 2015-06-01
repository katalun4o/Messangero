package com.inobix.messangero.map;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import android.R;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;
import com.google.android.maps.Projection;
import com.inobix.messangero.VoucherDetails;

public class MyMapMarker extends ItemizedOverlay<MyOverlayItem>
{

	private ArrayList<MyOverlayItem> mOverlays = new ArrayList<MyOverlayItem>();
	private Context mContext;
	private Paint innerPaint, borderPaint, textPaint, placeCategTextPaint,
			ratingPaint;
	private Point arrowPointCoordinates = new Point();
	private Bitmap rightArrowBitmap;
	public static MyOverlayItem currentItem;

	int INFO_WINDOW_WIDTH = 250;
	int INFO_WINDOW_HEIGHT = 90;
	int INFO_WINDOW_PIN_Y_OFFSET = 10;
	int BUTTON_RIGHT_MARGIN = 10;
	int BUTTON_HEIGHT = 48;
	int BUTTON_WIDTH = 48;

	int pinHeight = 10;


	public MyMapMarker(Drawable defaultMarker)
	{
		super(boundCenterBottom(defaultMarker));
	}


	public MyMapMarker(Drawable defaultMarker, Context context)
	{
		this(defaultMarker);
		mContext = context;
		pinHeight = defaultMarker.getBounds().height();
		INFO_WINDOW_PIN_Y_OFFSET = pinHeight + 3;

//		rightArrowBitmap = BitmapFactory.decodeResource(context.getResources(),
//				com.risoft.smartdiscountclient.R.drawable.rating_32);

		/*
		 * DisplayMetrics metrics = new DisplayMetrics(); Activity ac = new
		 * Activity();
		 * ac.getWindowManager().getDefaultDisplay().getMetrics(metrics);
		 */

	}

	public void addOverlay(MyOverlayItem item)
	{
		mOverlays.add(item);
		populate();

	}

	boolean isTapped = false;

	@Override
	public void draw(Canvas canvas, MapView mapView, boolean shadow)
	{
		if (!shadow)
		{
			super.draw(canvas, mapView, shadow);

			if (currentItem != null && currentItem.IsTapped)
				DrawInfoWindow(canvas, mapView, shadow);
		}
	}

	private void DrawInfoWindow(Canvas canvas, MapView mapView, boolean shadow)
	{
		Point selDestinationOffset = new Point();
		// mapView.getProjection().toPixels(selectedMapLocation.getPoint(),
		// selDestinationOffset);
		if (currentItem == null)
			return;
		GeoPoint gp = currentItem.getPoint();

		mapView.getProjection().toPixels(gp, selDestinationOffset);

		// Setup the info window with the right size & location

		RectF infoWindowRect = new RectF(0, 0, INFO_WINDOW_WIDTH,
				INFO_WINDOW_HEIGHT);
		int infoWindowOffsetX = selDestinationOffset.x - INFO_WINDOW_WIDTH / 2;
		// int infoWindowOffsetY =
		// selDestinationOffset.y-INFO_WINDOW_HEIGHT-bubbleIcon.getHeight();
		int infoWindowOffsetY = selDestinationOffset.y - INFO_WINDOW_HEIGHT
				- pinHeight + 3;
		infoWindowRect.offset(infoWindowOffsetX, infoWindowOffsetY);

		// Draw inner info window
		canvas.drawRoundRect(infoWindowRect, 5, 5, getInnerPaint());

		// Draw border for info window
		canvas.drawRoundRect(infoWindowRect, 5, 5, getBorderPaint());

		// Draw the MapLocation's name
		int TEXT_OFFSET_X = 10;
		int TEXT_OFFSET_Y = 25;
		// String name = selectedMapLocation.getName();
		String name = currentItem.getTitle();

		getTextPaint().setTextSize(20);

		Rect txtBounds = new Rect();
		getTextPaint().getTextBounds(name, 0, name.length(), txtBounds);
		int nameHeight = txtBounds.height();
		int nameWidth = txtBounds.width();

		int nameTextSize = (int) getTextPaint().getTextSize();

		while (nameWidth > (INFO_WINDOW_WIDTH - 35) && nameTextSize > 12)
		{
			textPaint.setTextSize(textPaint.getTextSize() - 1);
			textPaint.getTextBounds(name, 0, name.length(), txtBounds);
			nameWidth = txtBounds.width();
			nameTextSize = (int) textPaint.getTextSize();
		}

		if (name.length() > 38)
		{
			name = name.substring(0, 36) + "..";
		}

		canvas.drawText(name, infoWindowOffsetX + TEXT_OFFSET_X,
				infoWindowOffsetY + TEXT_OFFSET_Y, getTextPaint());

		// int iconX = infoWindowOffsetX + INFO_WINDOW_WIDTH - BUTTON_WIDTH -
		// BUTTON_RIGHT_MARGIN;
		// int iconY = infoWindowOffsetY + (INFO_WINDOW_HEIGHT -
		// INFO_WINDOW_PIN_Y_OFFSET)/2;
		
		canvas.drawText(currentItem.PlaceCategoryName, infoWindowOffsetX
				+ TEXT_OFFSET_X, infoWindowOffsetY + TEXT_OFFSET_Y
				+ nameHeight + 3, getPlaceCategTextPaint());
		
		/*if (currentItem.PlaceID != 0)
		{

			canvas.drawText(currentItem.PlaceCategoryName, infoWindowOffsetX
					+ TEXT_OFFSET_X, infoWindowOffsetY + TEXT_OFFSET_Y
					+ nameHeight + 3, getPlaceCategTextPaint());

			int iconX = selDestinationOffset.x + INFO_WINDOW_WIDTH / 2
					- BUTTON_RIGHT_MARGIN - BUTTON_WIDTH;

			int iconY = selDestinationOffset.y - INFO_WINDOW_PIN_Y_OFFSET
					+ ((INFO_WINDOW_HEIGHT - BUTTON_HEIGHT) / 2)
					- INFO_WINDOW_HEIGHT;

			Rect rectDest = new Rect(iconX, iconY + 13, iconX + BUTTON_WIDTH,
					iconY + BUTTON_HEIGHT + 13);
			//canvas.drawBitmap(rightArrowBitmap, null, rectDest, null);

			Rect ratingBounds = new Rect();
			getRatingPaint().getTextBounds(currentItem.Rating, 0,
					currentItem.Rating.length() - 1, ratingBounds);
			int ratingHeight = ratingBounds.height();
			int ratingWidth = ratingBounds.width();

			int ratingX = iconX + BUTTON_WIDTH / 2 - ratingWidth / 2 + 6;
			int ratingY = iconY + BUTTON_HEIGHT / 2 - ratingHeight / 2 + 16;

			canvas.drawText(currentItem.Rating, ratingX, ratingY + 13,
					getRatingPaint());
		}*/

		// canvas.drawText(selectedMapLocation.getPrice(),infoWindowOffsetX+TEXT_OFFSET_X,infoWindowOffsetY+TEXT_OFFSET_Y+20,getTextPaint());
		// if(!flag)
		// {
		// canvas.drawBitmap(iconForMapKit,
		// infoWindowOffsetX+160,infoWindowOffsetY+10, null);
		// }
		// else
		// {
		// canvas.drawBitmap(iconForMapKitRollOver,
		// infoWindowOffsetX+160,infoWindowOffsetY+10, null);
		// }

		arrowPointCoordinates.x = infoWindowOffsetX + 160;
		arrowPointCoordinates.y = infoWindowOffsetY + 10;

	}

	public Paint getInnerPaint()
	{
		if (innerPaint == null)
		{
			innerPaint = new Paint();
			// innerPaint.setARGB(225, 75, 75, 75); // gray
			//innerPaint.setARGB(225, 209, 211, 216);
			innerPaint.setColor(mContext.getResources().getColor(com.inobix.messangero.R.color.light_blue));
			innerPaint.setAlpha(120);
			innerPaint.setAntiAlias(true);
		}
		return innerPaint;
	}

	public Paint getBorderPaint()
	{
		if (borderPaint == null)
		{
			borderPaint = new Paint();
			//borderPaint.setARGB(255, 255, 255, 255);
			borderPaint.setColor(mContext.getResources().getColor(com.inobix.messangero.R.color.dark_blue));
			borderPaint.setAlpha(120);
			borderPaint.setAntiAlias(true);
			borderPaint.setStyle(Style.STROKE);
			borderPaint.setStrokeWidth(2);
		}
		return borderPaint;
	}

	public Paint getTextPaint()
	{
		if (textPaint == null)
		{
			textPaint = new Paint();
			textPaint.setARGB(255, 255, 255, 255);
			//textPaint.setARGB(225, 235, 131, 61);
			textPaint.setTextSize(20);
			textPaint.setTypeface(Typeface
					.defaultFromStyle(Typeface.BOLD_ITALIC));
			textPaint.setAntiAlias(true);
		}
		return textPaint;
	}

	public Paint getPlaceCategTextPaint()
	{
		if (placeCategTextPaint == null)
		{
			placeCategTextPaint = new Paint();
			//placeCategTextPaint.setARGB(255, 0, 0, 0);
			placeCategTextPaint.setARGB(255, 255, 255, 255);
			placeCategTextPaint.setTextSize(14);
			placeCategTextPaint.setTypeface(Typeface
					.defaultFromStyle(Typeface.ITALIC));
			placeCategTextPaint.setAntiAlias(true);
		}
		return placeCategTextPaint;
	}

	public Paint getRatingPaint()
	{
		if (ratingPaint == null)
		{
			ratingPaint = new Paint();
			ratingPaint.setARGB(255, 0, 0, 0);
			ratingPaint.setTextSize(16);
			ratingPaint.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
			ratingPaint.setTextAlign(Align.CENTER);
			ratingPaint.setAntiAlias(true);
		}
		return ratingPaint;
	}

	@Override
	protected MyOverlayItem createItem(int i)
	{
		return mOverlays.get(i);
	}

	@Override
	public int size()
	{
		return mOverlays.size();
	}

	@Override
	protected boolean onTap(int index)
	{
		OverlayItem item = getItem(index);
		currentItem = getItem(index);

		for (MyOverlayItem i : mOverlays)
		{
			i.IsTapped = false;
		}

		currentItem.IsTapped = true;

		return true;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event, MapView mapView)
	{
		if (currentItem == null || currentItem.CourierID == 0)
			return super.onTouchEvent(event, mapView);

		if (event.getAction() == MotionEvent.ACTION_UP)
		{

			Point pResult = new Point((int) event.getX(), (int) event.getY());

			Point pLastItem = new Point();
			mapView.getProjection().toPixels(currentItem.getPoint(), pLastItem);

			/*
			 * int leftX = pLastItem.x + INFO_WINDOW_WIDTH / 2 -
			 * BUTTON_RIGHT_MARGIN - BUTTON_WIDTH; int rightX = pLastItem.x +
			 * INFO_WINDOW_WIDTH / 2; // - BUTTON_RIGHT_MARGIN;
			 * 
			 * int topY = pLastItem.y - INFO_WINDOW_PIN_Y_OFFSET +
			 * ((INFO_WINDOW_HEIGHT - BUTTON_HEIGHT) / 2) - INFO_WINDOW_HEIGHT -
			 * 10; int bottomY = pLastItem.y - INFO_WINDOW_PIN_Y_OFFSET -
			 * ((INFO_WINDOW_HEIGHT - BUTTON_HEIGHT) / 2 + 10);
			 */

			int iconX = pLastItem.x - INFO_WINDOW_WIDTH / 2;

			int iconY = pLastItem.y - INFO_WINDOW_PIN_Y_OFFSET
					- INFO_WINDOW_HEIGHT;

			// if (pResult.x > leftX && pResult.x < rightX && pResult.y > topY
			// && pResult.y < bottomY)
			if (pResult.x > iconX && pResult.x < (iconX + INFO_WINDOW_WIDTH)
					&& pResult.y > iconY
					&& pResult.y < (iconY + INFO_WINDOW_HEIGHT))
			{
				// open place screen for current Item
				// Common.ShowToastMessage(mContext, currentItem.getTitle());

				Intent openCourierDetailsScreen = new Intent(mContext, VoucherDetails.class);
				openCourierDetailsScreen.putExtra("CourierID", currentItem.CourierID);
				mContext.startActivity(openCourierDetailsScreen);
				return true;
			}

		}

		return super.onTouchEvent(event, mapView);
	}

	@Override
	public boolean onTap(GeoPoint p, MapView mapView)
	{
		boolean isMarkerTapped = super.onTap(p, mapView);

		if (isMarkerTapped == false)
		{
			for (MyOverlayItem i : mOverlays)
			{
				i.IsTapped = false;
			}
			currentItem = null;
		}
		/*
		 * if (isMarkerTapped) { isTapped = !isTapped;
		 * 
		 * //mapView.invalidate(); } else { if(currentItem == null) return
		 * isMarkerTapped; Point pResult = new Point();
		 * mapView.getProjection().toPixels(p, pResult);
		 * 
		 * Point pLastItem = new Point();
		 * mapView.getProjection().toPixels(currentItem.getPoint(), pLastItem);
		 * 
		 * int leftX = pLastItem.x + INFO_WINDOW_WIDTH / 2 - BUTTON_RIGHT_MARGIN
		 * - BUTTON_WIDTH; int rightX = pLastItem.x + INFO_WINDOW_WIDTH / 2 -
		 * BUTTON_RIGHT_MARGIN;
		 * 
		 * int topY = pLastItem.y - INFO_WINDOW_PIN_Y_OFFSET -
		 * ((INFO_WINDOW_HEIGHT - BUTTON_HEIGHT) / 2) - INFO_WINDOW_HEIGHT; int
		 * bottomY = pLastItem.y - INFO_WINDOW_PIN_Y_OFFSET -
		 * ((INFO_WINDOW_HEIGHT - BUTTON_HEIGHT) / 2);
		 * 
		 * if (pResult.x > leftX && pResult.x < rightX && pResult.y > topY &&
		 * pResult.y < bottomY) { //open place screen for current Item
		 * Common.ShowToastMessage(mContext, currentItem.getTitle()); return
		 * false; } }
		 */
		return isMarkerTapped;
	}


}