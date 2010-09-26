package edu.uic.ketai.inputService;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;

import processing.core.PImage;
import processing.core.PApplet;

//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.PreviewCallback;
import android.util.Log;

public class KetaiCamera extends PImage implements Runnable {

	private PApplet parent;
	private static final String TAG = "ketaiCamera";
	private Camera camera;
	private int[] myPixels;
	private Method onPreviewEventMethod;
	private int frameWidth, frameHeight, cameraFPS;
	public boolean crop;

	public int cropX;
	public int cropY;
	public int cropW;
	public int cropH;
	Thread runner;
	boolean available = false;

	public KetaiCamera(PApplet pParent, int _width, int _height,
			int _framesPerSecond) {
		parent = pParent;
		frameWidth = _width;
		frameHeight = _height;
		cameraFPS = _framesPerSecond;
		start();
		super.init(_width, _height, RGB);
		myPixels = new int[_width*_height];
		
		try {
			// the following uses reflection to see if the parent
			// exposes the call-back method. The first argument is the method
			// name followed by what should match the method argument(s)
			onPreviewEventMethod = parent.getClass().getMethod(
					"onCameraPreviewEvent");
			PApplet.println("KetaiCamera found onCameraPreviewEvent in parent... ");
		} catch (Exception e) {
			// no such method, or an error.. which is fine, just ignore
			onPreviewEventMethod = null;
			PApplet.println("KetaiCamera did not find onCameraPreviewEvent Method: "
					+ e.getMessage());
		}

		if (camera != null) {
			PApplet.println("KetaiCamera: starting preview for camera...");
			camera.setPreviewCallback(previewcallback);
			camera.startPreview();
		}
		PApplet.println("KetaiCamera completed instantiation... ");
		runner = new Thread(this);
		runner.run();
	}

	public void start()
	{
		try {
			PApplet.println("KetaiCamera: opening camera...");
			camera = Camera.open();
			Parameters cameraParameters = camera.getParameters();
			// too bad the following doesnt work yet..even on 2.2 :-/
			// cameraParameters.setPreviewFormat(ImageFormat.RGB_565);

			// /We should probably verify that the numbers passed in are
			// suppported by the camera....sure...eventually we will
//			cameraParameters.setPreviewFormat(ImageFormat.NV21);
			cameraParameters.setPreviewFrameRate(cameraFPS);
			cameraParameters.setPreviewSize(frameWidth, frameHeight);

			camera.setParameters(cameraParameters);
			PApplet.println("KetaiCamera: Set camera parameters...");

			// Parameters p = camera.getParameters();
			// PApplet.println("KetaiCamera preview format returned from camera is: "
			// + p.getPreviewFormat() + " and it should be : " +
			// ImageFormat.RGB_565);
		} catch (Exception x) {
			x.printStackTrace(System.out);
		}	
	}
	
	public void takePicture() {
		if (camera != null)
			camera.takePicture(null, null, jpegCallback);
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

	public void read() {
		// try {
		// synchronized (capture) {
		loadPixels();
		synchronized (pixels) {
			if (crop) {
				// System.out.println("read2a");
				// f#$)(#$ing quicktime / jni is so g-d slow, calling
				// copyToArray
				// for the invidual rows is literally 100x slower. instead,
				// first
				// copy the entire buffer to a separate array (i didn't need
				// that
				// memory anyway), and do an arraycopy for each row.
				// if (data == null) {
				// data = new int[frameWidth * frameHeight];
				// }
				// raw.copyToArray(0, data, 0, frameWidth * frameHeight);
				// int sourceOffset = cropX + cropY * dataWidth;
				// int destOffset = 0;
				// for (int y = 0; y < cropH; y++) {
				// System.arraycopy(data, sourceOffset, pixels, destOffset,
				// cropW);
				// sourceOffset += dataWidth;
				// destOffset += width;
				// }
				// } else { // no crop, just copy directly
				// // System.out.println("read2b");
				System.arraycopy(myPixels, 0, pixels, 0, width*height);
				// raw.copyToArray(0, pixels, 0, width * height);
			}else
				System.arraycopy(myPixels, 0, pixels, 0, width*height);
				
			// System.out.println("read3");

			available = false;
			// mark this image as modified so that PGraphicsJava2D and
			// PGraphicsOpenGL will properly re-blit and draw this guy
			updatePixels();
			// System.out.println("read4");
		}
	}

	PreviewCallback previewcallback = new PreviewCallback() {
		public void onPreviewFrame(byte[] data, Camera camera) {
			if (camera == null)
				return;
			// The camera does NOT return RGB even when set for it so we will
			// just deal w/the NV21 format
			//
			// BitmapFactory.Options options = new BitmapFactory.Options();
			// options.inSampleSize = 1;
			// Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0,
			// data.length, null);
			// if(bitmap == null)
			// {
			// //PApplet.println("KetaiCamera:  Unable to convert cameraPreview data to bitmap...Data has lenght: "
			// + data.length);
			// myPixels = new int[width * height];
			// KetaiCamera.decodeYUV420SP(myPixels, data, width, height);
			// }
			// else
			// {
			// int w = bitmap.getWidth();
			// int h = bitmap.getHeight();
			// myPixels = new int[width * height];
			// bitmap.getPixels(myPixels, 0, w, 0, 0, w, h);
			// }
			if(myPixels == null)
				myPixels = new int[width * height];

			KetaiCamera.decodeYUV420SP(myPixels, data, width, height);

			if (myPixels == null)
				return;

			// PApplet.println("KetaiCamera.previewCallback: pixels buffer is of length: "
			// + myPixels.length);
			if (onPreviewEventMethod != null && myPixels != null)
				try {
					// PApplet.println("onCameraPreviewEvent() calling parent method");
					onPreviewEventMethod.invoke(parent);
					return;
				} catch (Exception e) {
					PApplet.println("Disabling onCameraPreviewEvent() because of an error:"
							+ e.getMessage());
					e.printStackTrace();
					onPreviewEventMethod = null;
				}
		}
	};

	PictureCallback jpegCallback = new PictureCallback() {
		public void onPictureTaken(byte[] data, Camera camera) {
			if(camera == null)
				return;
			FileOutputStream outStream = null;
			// BitmapFactory.Options options = new BitmapFactory.Options();
			// options.inSampleSize = 1;
			// Bitmap bitmap = BitmapFactory
			// .decodeByteArray(data, 0, data.length, options);
			// int w = bitmap.getWidth();
			// int h = bitmap.getHeight();
			// int[] pixels = new int[w * h];
			// bitmap.getPixels(pixels, 0, w, 0, 0, w, h);
			//
			// Log.w(TAG,
			// "CameraManager PictureCallback.  About to call native code image h/w is "
			// + h + "/" + w);
			// // opencv.setSourceImage(pixels, w, h);
			// byte[] calculatedData = opencv.findContours( w, h);

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
			}
			Log.d(TAG, "onPictureTaken - jpeg");
		}
	};

	public void stop() {
		if (camera != null) {
			camera.stopPreview();
			camera.release();
			camera = null;
		}
		runner = null; // unwind the thread
	}

	public void dispose() {
		stop();
		// System.out.println("calling dispose");
		// this is important so that the next app can do video
	}

	// public void surfaceCreated(SurfaceHolder holder) {
	// try {
	// camera.setPreviewDisplay(holder);
	//
	// camera.setPreviewCallback( new PreviewCallback() {
	// // Called for each frame previewed
	// public void onPreviewFrame(byte[] data, Camera camera) {
	// Log.d(TAG, "onPreviewFrame called at: " + System.currentTimeMillis());
	// // CameraManager.this.invalidate();
	// }
	// });
	// } catch (IOException e) {
	// e.printStackTrace();
	// }
	// }

	static public void decodeYUV420SP(int[] rgb, byte[] yuv420sp, int width,
			int height) {
		final int frameSize = width * height;

		for (int j = 0, yp = 0; j < height; j++) {
			int uvp = frameSize + (j >> 1) * width, u = 0, v = 0;
			for (int i = 0; i < width; i++, yp++) {
				int y = (0xff & ((int) yuv420sp[yp])) - 16;
				if (y < 0)
					y = 0;
				if ((i & 1) == 0) {
					v = (0xff & yuv420sp[uvp++]) - 128;
					u = (0xff & yuv420sp[uvp++]) - 128;
				}

				int y1192 = 1192 * y;
				int r = (y1192 + 1634 * v);
				int g = (y1192 - 833 * v - 400 * u);
				int b = (y1192 + 2066 * u);

				if (r < 0)
					r = 0;
				else if (r > 262143)
					r = 262143;
				if (g < 0)
					g = 0;
				else if (g > 262143)
					g = 262143;
				if (b < 0)
					b = 0;
				else if (b > 262143)
					b = 262143;

				rgb[yp] = 0xff000000 | ((r << 6) & 0xff0000)
						| ((g >> 2) & 0xff00) | ((b >> 10) & 0xff);
			}
		}
	}

	@Override
	public void run() {
		// while ((Thread.currentThread() == runner) && (camera != null)) {
		// try {
		// synchronized (camera) {
		// available = true;
		//
		// // if (this.captureEventMethod != null) {
		// // try {
		// // captureEventMethod.invoke(parent, new Object[] { this });
		// // } catch (Exception e) {
		// // System.err.println("Disabling captureEvent() for " + name +
		// // " because of an error.");
		// // e.printStackTrace();
		// // captureEventMethod = null;
		// // }
		// }
		// }
		//
		// } catch (Exception e) {
		// PApplet.println("KetaiCamera: run() Exception: " + e.getMessage());
		// }
		//
		// try {
		// Thread.sleep(1000 / cameraFPS);
		// } catch (InterruptedException e) { }
		// }
		//
		// }

	}
}
