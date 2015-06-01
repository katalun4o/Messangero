package com.inobix.messangero;

import DAL.DBUtil;
import android.content.Context;
import android.os.AsyncTask;

public class SyncVouchersTask extends AsyncTask<Context, Void, Void> {

	@Override
	protected Void doInBackground(Context... params) {
		Context ctx =  params[0];
		
		if (!com.inobix.messangero.common.app_settings.IsDemoVersion)
			DBUtil.SyncVouchers(ctx);
		return null;
	}

}
