package com.inobix.messangero;

import java.io.FileOutputStream;

import com.inobix.messangero.signature.SignatureViewDemo;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class SignatureDemoActivity extends Activity {
	public static final int RESULT_OK = 3454;
	public static final int RESULT_CANCEL = 3455;
	SignatureViewDemo signView;
	Button btnClear;
	Button btnOK;
	Button btnCancel;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_signature_demo);

		btnClear = (Button) findViewById(R.id.btnClear);
		btnOK = (Button) findViewById(R.id.btnOK);
		btnCancel = (Button) findViewById(R.id.btnCancel);
		signView = (SignatureViewDemo) findViewById(R.id.signView);

		btnClear.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				signView.resetImage();
			}
		});

		btnCancel.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				setResult(RESULT_CANCEL);
				finish();
			}
		});

		btnOK.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				String filename = getExternalCacheDir().getAbsolutePath()
						+ "/sign.png";
				Intent result = new Intent();
				Bitmap b = signView.getBitmap();
				try {
					FileOutputStream out = new FileOutputStream(filename);
					b.compress(Bitmap.CompressFormat.PNG, 90, out);
					out.close();
				} catch (Exception e) {
					e.printStackTrace();
				}

				result.putExtra("Signature", filename);
				setResult(RESULT_OK, result);
				finish();
			}
		});

	}
}
