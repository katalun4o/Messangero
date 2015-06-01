package com.inobix.messangero.common;

import java.lang.ref.WeakReference;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

public class BitmapWorkerTask extends AsyncTask<String, Void, Bitmap> {
	    private final WeakReference<ImageView> imageViewReference;
	    private int data = 0;

	    public BitmapWorkerTask(ImageView imageView) {
	        // Use a WeakReference to ensure the ImageView can be garbage collected
	        imageViewReference = new WeakReference<ImageView>(imageView);
	    }

	    // Decode image in background.
	    @Override
	    protected Bitmap doInBackground(String... params) {
	        String filePath = params[0];
	        Bitmap bitmap = null;
	        BitmapFactory.Options bounds = new BitmapFactory.Options();
	        bounds.inJustDecodeBounds = true;
	        BitmapFactory.decodeFile(filePath, bounds);
	        if (bounds.outWidth == -1) { 
	        	// TODO: Error 
	        }
	        int width = bounds.outWidth;
	        int height = bounds.outHeight;
	        
	        BitmapFactory.Options resample = new BitmapFactory.Options();
            resample.inSampleSize = 4;
            bitmap = BitmapFactory.decodeFile(filePath, resample);
	        
	        return bitmap;
	    }

	    // Once complete, see if ImageView is still around and set bitmap.
	    @Override
	    protected void onPostExecute(Bitmap bitmap) {
	        if (imageViewReference != null && bitmap != null) {
	            final ImageView imageView = imageViewReference.get();
	            if (imageView != null) {
	                imageView.setImageBitmap(bitmap);
	            }
	        }
	    }
	}

