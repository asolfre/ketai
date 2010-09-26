package edu.uic.ketai;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.siprop.opencv.OpenCV;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.PreviewCallback;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class CameraManager extends SurfaceView implements SurfaceHolder.Callback{
	private static final String TAG = "ketai";
	private Camera camera;
	private OpenCV opencv;
	SurfaceHolder mHolder; 	

	public CameraManager(Context ctx) {
		super(ctx);
		try{
			camera = Camera.open();
		}catch (Exception x){x.printStackTrace();}
		
		if (camera != null)
			camera.startPreview();
		
		opencv = new OpenCV();
	    mHolder = getHolder();  // <4>
	    mHolder.addCallback(this);  // <5>
	    mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS); // <6>
	    //((FrameLayout) findViewById(R.id.preview_surface)).addView(this);
	}

	public void takePicture() {
		if(camera != null)
			camera.takePicture(null, null, jpegCallback);
	}

	public void getReadyForSuspension() {
		if (camera != null) {
			camera.stopPreview();
			camera.release();
			camera = null;
		}
	}

	public void onResume() {
		if (camera == null) {
			camera = Camera.open();
		camera.startPreview();
		}
	}

	PictureCallback rawCallback = new PictureCallback() { // <7>
		public void onPictureTaken(byte[] data, Camera camera) {
			Log.d(TAG, "onPictureTaken - raw");
		}
	};

	PictureCallback jpegCallback = new PictureCallback() {
		public void onPictureTaken(byte[] data, Camera camera) {
			
			FileOutputStream outStream = null;
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 1;
            Bitmap bitmap = BitmapFactory
                    .decodeByteArray(data, 0, data.length, options);
            int w = bitmap.getWidth();
            int h = bitmap.getHeight();
            int[] pixels = new int[w * h];
            bitmap.getPixels(pixels, 0, w, 0, 0, w, h);

            Log.w(TAG, "CameraManager PictureCallback.  About to call native code image h/w is " + h + "/" + w);
            opencv.setSourceImage(pixels, w, h);
            byte[] calculatedData = opencv.findContours( w, h);
			      
			try {
				// Write to SD Card
				outStream = new FileOutputStream(
						String.format("/sdcard/ketai_data/%d.jpg",
								System.currentTimeMillis()));
				
				outStream.write(calculatedData);
				outStream.close();
				Log.d(TAG, "onPictureTaken - wrote bytes: " + data.length);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
			}
			Log.d(TAG, "onPictureTaken - jpeg");
		}
	};

	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
	    try {
	        camera.setPreviewDisplay(holder);

	        camera.setPreviewCallback( new PreviewCallback() {
	          // Called for each frame previewed
	          public void onPreviewFrame(byte[] data, Camera camera) {
	            Log.d(TAG, "onPreviewFrame called at: " + System.currentTimeMillis());
	            CameraManager.this.invalidate();
	          }
	        });
	      } catch (IOException e) {
	        e.printStackTrace();
	      }		
	}

	public void surfaceCreated(SurfaceHolder holder) {
		if (camera != null)
			camera.startPreview();
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
	    camera.stopPreview();
	    camera.release();
	    camera = null;		
	}

}

