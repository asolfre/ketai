package edu.uic.ketai;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.util.Log;

public class CameraManager {
	private static final String TAG = "CameraManager";
	private Camera camera;

	public CameraManager() {
		camera = Camera.open();
		camera.startPreview();
	}

	public void takePicture() {
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
			try {
				// Write to SD Card
				outStream = new FileOutputStream(
						String.format("/sdcard/ketai_data/%d.jpg",
								System.currentTimeMillis()));
				outStream.write(data);
				outStream.close();
				Log.d(TAG, "onPictureTaken - wrote bytes: " + data.length);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				camera.release();
			}
			Log.d(TAG, "onPictureTaken - jpeg");
		}
	};

}

