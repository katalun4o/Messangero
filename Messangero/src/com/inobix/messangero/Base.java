package com.inobix.messangero;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;

public class Base {
	public static String[] VoucherTypesNames;

	public static void LoadVoucherTypesNames(Context ctx) {
		VoucherTypesNames = ctx.getResources().getStringArray(
				R.array.VoucherTypesNames);
	}

	public static Date addDay(Date date, int i) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.add(Calendar.DAY_OF_YEAR, i);
		return c.getTime();
	}

	public static void LoadLocale(Context context) {
		if (PreferencesUtil.RotateScreen == false) {
			if (context instanceof Activity) {
				((Activity) context)
						.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
			}
		}
		else
		{
			if (context instanceof Activity) {
				((Activity) context)
						.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
			}
		}

		String loc = "gr";
		if (PreferencesUtil.LanguageID != -1) {
			loc = PreferencesUtil.GetLocale();
		}

		Locale locale = new Locale(loc);
		Locale.setDefault(locale);
		Configuration config = new Configuration();
		config.locale = locale;
		context.getResources().updateConfiguration(config,
				context.getResources().getDisplayMetrics());
	}

	public static String GetVoucherTypeString(int status) {
		String res = "";

		res = VoucherTypesNames[status - 1];

		return res;
	}
}
