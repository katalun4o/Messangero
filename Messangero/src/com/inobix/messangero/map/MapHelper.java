package com.inobix.messangero.map;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;



import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.inobix.messangero.R;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.app.FragmentActivity;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

public class MapHelper
{
	Context currentContext;
	MapView currentMap;
	public GoogleMap googleMap;
	HashMap<Marker, MarkerInfo> markersInfo;
	boolean AllowInfoWindowClick = true;

	public MapHelper(Context ctx, MapView map)
	{
		currentContext = ctx;
		currentMap = map;
		placeIdsAdded = new ArrayList<Integer>();
		markersInfo = new HashMap<Marker, MarkerInfo>();
	}

	public void ShowOnlyItemsInArea(LatLngBounds bounds)
	{
		ClearMarkers();
		for (Entry<Marker, MarkerInfo> entry : markersInfo
				.entrySet())
		{
			//Marker marker = entry.getKey();
			MarkerInfo markerinfo = entry.getValue();

			if (bounds.contains(markerinfo.Position))
			{
				AddMarker(markerinfo.Position.latitude, markerinfo.Position.longitude,
						markerinfo.Name, markerinfo.Address, markerinfo.Icon,
						markerinfo.PlaceID, markerinfo.RatingDouble,
						markerinfo.DiscountDouble, markerinfo.PlaceCategoryName,
						markerinfo.MultiDiscMark);
				// googleMap.getMarkers().add(marker);
			}
		}
	}

	public MapHelper(final Context ctx,
			GoogleMap map,
			final FragmentActivity mainActivity, boolean allowInfoWindowClick)
	{
		currentContext = ctx;
		googleMap = map;
		// ClusteringSettings sett = new ClusteringSettings();
		// sett.iconDataProvider(new
		// MapIconProvicer(ctx.getResources())).addMarkersDynamically(true);
		// sett.clusterSize(96);
		// googleMap.setClustering(sett);

		AllowInfoWindowClick = allowInfoWindowClick;
		markersInfo = new HashMap<Marker, MarkerInfo>();

		googleMap
				.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter()
				{
					

					public View getInfoContents(
							Marker marker)
					{
						// TODO Auto-generated method stub
						return null;
					}

					public View getInfoWindow(
							Marker marker)
					{
						View window = ((Activity) ctx).getLayoutInflater()
								.inflate(R.layout.map_info_window, null);
						if (!markersInfo.containsKey(marker))
							return window;

						MarkerInfo mi = markersInfo.get(marker);

						TextView lblName = (TextView) window
								.findViewById(R.id.lblName);
						TextView lblAddress = (TextView) window
								.findViewById(R.id.lblAddress);
						
						lblName.setText(mi.Name);
						lblAddress.setText(mi.Address);						
						
						return window;
					}

					
				});

		if (AllowInfoWindowClick)
		{
			googleMap
					.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener()
					{
						public void onInfoWindowClick(
								Marker marker)
						{
							if (!markersInfo.containsKey(marker))
								return;
							MarkerInfo mi = markersInfo.get(marker);

							//((Main) mainActivity).OpenPlaceScreen(mi.PlaceID,
								//	false, false);
						}
					});
		}

		placeIdsAdded = new ArrayList<Integer>();
	}

	public void ClearMarkers()
	{
		placeIdsAdded.clear();
		markersInfo.clear();
		if (currentMap != null)
			currentMap.getOverlays().clear();
		
		if (googleMap != null)
			googleMap.clear();
	}

	public void SetCurrentMarker(MyOverlayItem marker)
	{
		MyMapMarker.currentItem = marker;
	}

	private Drawable resize(Drawable image)
	{
		if (image == null)
			return null;

		WindowManager wm = (WindowManager) currentContext
				.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();

		float displayWidth = display.getWidth();
		float displayHeight = display.getHeight();
		if (displayWidth > displayHeight)
		{
			float buf = displayHeight;
			displayHeight = displayWidth;
			displayWidth = buf;
		}

		// 480x800
		int width = 60;
		int height = 80;

		width = (int) ((displayWidth / 480f) * width);
		height = (int) ((displayHeight / 800f) * height);

		Bitmap d = ((BitmapDrawable) image).getBitmap();
		if (d == null)
			return null;
		Bitmap bitmapOrig = Bitmap.createScaledBitmap(d, width, height, false);
		// d.recycle();
		BitmapDrawable bdr = new BitmapDrawable(bitmapOrig);
		// bitmapOrig.recycle();
		return bdr;
	}
	
	/*private Bitmap resize(Bitmap image)
	{
		if (image == null)
			return null;

		WindowManager wm = (WindowManager) currentContext
				.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();

		float displayWidth = display.getWidth();
		float displayHeight = display.getHeight();
		if (displayWidth > displayHeight)
		{
			float buf = displayHeight;
			displayHeight = displayWidth;
			displayWidth = buf;
		}

		// 480x800
		int width = 60;
		int height = 80;

		width = (int) ((displayWidth / 480f) * width);
		height = (int) ((displayHeight / 800f) * height);

		Bitmap d = image;
		if (d == null)
			return null;
		Bitmap bitmapOrig = Bitmap.createScaledBitmap(d, width, height, false);

		return bitmapOrig;
	}*/

	public MyOverlayItem AddMarker(double lat, double lng, int drawableID,
			String name, String desc, int placeID, double rating,
			double discount, String placeCategoryName)
	{
		Drawable drawable = currentContext.getResources().getDrawable(
				drawableID);

		drawable = resize(drawable);

		return AddMarker(lat, lng, drawable, name, desc, placeID, rating,
				discount, placeCategoryName);
	}

	List<Integer> placeIdsAdded;

	public Marker AddMarker(double lat,
			double lng, String name, String address, int iconResID,
			int placeID, double rating, double discount,
			String placeCategoryName, boolean multiDiscMark)
	{
		//Bitmap icon = BitmapFactory.decodeResource(currentContext.getResources(),iconResID);
		//icon = resize(icon);
		//BitmapDescriptor bdesc = BitmapDescriptorFactory.fromBitmap(icon);
		BitmapDescriptor bdesc = BitmapDescriptorFactory.fromResource(iconResID);
		MarkerOptions mo = new MarkerOptions();
		mo.position(new LatLng(lat, lng));
		mo.title(name);
		mo.icon(bdesc);
		mo.snippet(address);

		Marker marker = googleMap.addMarker(mo);

		MarkerInfo mi = new MarkerInfo(new LatLng(lat, lng), name, address,
				placeID, rating, discount, placeCategoryName, multiDiscMark);
		markersInfo.put(marker, mi);

		return marker;
	}

	HashMap<Bitmap, BitmapDescriptor> mapBitmaps;
	public Marker AddMarker(double lat,
			double lng, String name, String address, Bitmap icon, int placeID,
			double rating, double discount, String placeCategoryName,
			boolean multiDiscMark)
	{
		MarkerOptions mo = new MarkerOptions();
		mo.position(new LatLng(lat, lng));
		mo.title(name);
		
		if(mapBitmaps == null)
			mapBitmaps = new HashMap<Bitmap, BitmapDescriptor>();
		
		if(mapBitmaps.containsKey(icon))
			mo.icon(mapBitmaps.get(icon));
		else
		{
			BitmapDescriptor desc = BitmapDescriptorFactory.fromBitmap(icon);
			mapBitmaps.put(icon, desc);
			mo.icon(desc);
		}
		
		//mo.icon(BitmapDescriptorFactory.fromResource(R.drawable.star_blue_72));
		mo.snippet(address);
		Marker marker = googleMap.addMarker(mo);

		MarkerInfo mi = new MarkerInfo(new LatLng(lat, lng), name, address,
				placeID, rating, discount, placeCategoryName, multiDiscMark);
		mi.Position = new LatLng(lat, lng); 
		mi.Icon = icon;
		markersInfo.put(marker, mi);
		

		return marker;
	}

	public MyOverlayItem AddMarker(double lat, double lng, Drawable drawable1,
			String name, String desc, int placeID, double rating,
			double discount, String placeCategoryName)
	{
		if (drawable1 == null)
			return null;
		List<Overlay> overlays = currentMap.getOverlays();

		if (placeIdsAdded.contains(placeID))
		{
			return null;
		} else
			placeIdsAdded.add(placeID);

		MyMapMarker itemizedoverlay = new MyMapMarker(drawable1, currentContext);

		MyOverlayItem overlayitem = new MyOverlayItem(GetGeopoint(
				lat, lng), name, desc, placeID, rating, placeCategoryName);

		itemizedoverlay.addOverlay(overlayitem);
		overlays.add(itemizedoverlay);

		return overlayitem;
	}

	public void AddRout(ArrayList<GeoPoint> points)
	{
		List<Overlay> overlays = currentMap.getOverlays();
		RouteOverlay routeOverlay = new RouteOverlay(points, Color.BLUE);
		overlays.add(routeOverlay);
		currentMap.invalidate();

	}

	public void AddRout1(ArrayList<LatLng> points)
	{
		PolylineOptions opts = new PolylineOptions();
		opts.addAll(points);
		googleMap.addPolyline(opts);
		
	}

	public void ClearInfoWindows()
	{
		MyMapMarker.currentItem.IsTapped = false;
		currentMap.invalidate();
	}

	public void CenterMap(double lat, double lng)
	{
		if (currentMap != null)
		{
			MapController myMapController = currentMap.getController();
			myMapController.setCenter(GetGeopoint(lat, lng));
		} else
		{
			if (googleMap != null)
			{
				CameraUpdate updateZoom = CameraUpdateFactory.newLatLngZoom(
						new LatLng(lat, lng), 17);
				googleMap.moveCamera(updateZoom);
			}
		}
	}

	public static GeoPoint GetGeopoint(double latitude, double longitude)
	{
		int lat = (int) (latitude * Math.pow(10, 6));
		int lng = (int) (longitude * Math.pow(10, 6));
		return new GeoPoint(lat, lng);
	}
	public List<LatLng> decodePoly(String encoded) {

        List<LatLng> poly = new ArrayList<LatLng>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }

        return poly;
    }
}
