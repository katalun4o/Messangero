package com.inobix.messangero.common;

import java.util.Date;

import android.content.Context;
import android.os.Environment;

public class app_settings
{
	public static final boolean IsDemoVersion = false;
	public static int CurrentUserID = 0;
	public static int CurrentDriverID = 0;
	public static Date WorkDate = new Date();
	public static String CompanyPhoneNumber = "";
	public static String GCM_ID = "62105108905";
	public static String GetImagesDir(Context ctx)
	{
		return ctx.getExternalFilesDir(Environment.DIRECTORY_PICTURES) + "/MessangeroCache";
	}
	
}
