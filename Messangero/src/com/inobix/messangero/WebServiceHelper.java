package com.inobix.messangero;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.util.LangUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class WebServiceHelper {
	public static final boolean IsWCFWebService = true;

	private static AsyncHttpClient client = new AsyncHttpClient();

	static {
		client.addHeader("Accept", "application/json");
		client.addHeader("Content-type", "application/json");
	}

	private static final String GetServiceURI(String serverIP,
			String serviceName) {
		String url = "http://%s/%s";
		url = String.format(url, serverIP, serviceName);

		return url;
	}

	public static void GetRout(Context ctx, String address,
			AsyncHttpResponseHandler responseHandler) {
		String sourceLat = String.valueOf(PreferencesUtil.Latitude);
		String sourceLng = String.valueOf(PreferencesUtil.Longitude);
		String urlAddres;
		try {
			urlAddres = URLEncoder.encode(address, "UTF-8");
			String googleRoutApi = "http://maps.googleapis.com/maps/api/directions/json?origin=%s,%s&destination=%s&sensor=true";
			client.get(String.format(googleRoutApi, sourceLat, sourceLng,
					urlAddres), responseHandler);
		} catch (Exception e) 
		{
			e.printStackTrace();
		}

	}

	public static void GetRout(Context ctx, double lat, double lng,
			AsyncHttpResponseHandler responseHandler) {
		String sourceLat = String.valueOf(PreferencesUtil.Latitude);
		String sourceLng = String.valueOf(PreferencesUtil.Longitude);
		String destLat = String.valueOf(lat);
		String destLng = String.valueOf(lng);
		String googleRoutApi = "http://maps.googleapis.com/maps/api/directions/json?origin=%s,%s&destination=%s,%s&sensor=true";
		client.get(String.format(googleRoutApi, sourceLat, sourceLng, destLat,
				destLng), responseHandler);
	}

	public static JSONObject getJsonObjectFromMap(Map<String, String> params)
			throws JSONException {

		// all the passed parameters from the post request
		// iterator used to loop through all the parameters
		// passed in the post request
		Iterator<Map.Entry<String, String>> iter = params.entrySet().iterator();

		// Stores JSON
		JSONObject holder = new JSONObject();

		// using the earlier example your first entry would get email
		// and the inner while would get the value which would be 'foo@bar.com'
		// { fan: { email : 'foo@bar.com' } }

		// While there is another entry
		while (iter.hasNext()) {
			// gets an entry in the params
			Map.Entry<String, String> pairs = (Map.Entry<String, String>) iter
					.next();

			// creates a key for Map
			String key = (String) pairs.getKey();

			// Create a new map
			String val = (String) pairs.getValue();

			holder.put(key, val);
		}
		return holder;
	}

	public static HttpEntity GetHttpParamsEntity(HashMap<String, String> params) {
		HttpEntity entityResult = null;
		JSONObject holder;
		try {
			holder = getJsonObjectFromMap(params);

			entityResult = new ByteArrayEntity(holder.toString().getBytes(
					"UTF8"));
		} catch (Exception e) {
			e.printStackTrace();
		}

		return entityResult;
	}
}
