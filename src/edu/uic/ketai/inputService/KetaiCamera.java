package edu.uic.ketai.inputService;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Vector;

import edu.uic.ketai.analyzer.IKetaiAnalyzer;

import processing.core.PImage;
import processing.core.PApplet;

//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.graphics.ImageFormat;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.PreviewCallback;

public class KetaiCamera extends PImage implements IKetaiInputService {

	private ArrayList<IKetaiAnalyzer> listeners;
	private PApplet parent;
	private Camera camera;
	private int[] myPixels;
	private Method onPreviewEventMethod, onPreviewEventMethodPImage;
	private int frameWidth, frameHeight, cameraFPS;
	public boolean isStarted, enableFlash, isRGBPreviewSupported;
	PImage self;
	Thread runner;
	boolean available = false;

	public KetaiCamera(PApplet pParent, int _width, int _height,
			int _framesPerSecond) {
		parent = pParent;
		frameWidth = _width;
		frameHeight = _height;
		cameraFPS = _framesPerSecond;
		isStarted = false;
		super.init(_width, _height, RGB);
		myPixels = new int[_width * _height];
		listeners = new ArrayList<IKetaiAnalyzer>();
		self = this;
		isRGBPreviewSupported = false;
		enableFlash = false;

		try {
			// the following uses reflection to see if the parent
			// exposes the callback method. The first argument is the method
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

		try {
			onPreviewEventMethodPImage = parent.getClass().getMethod(
					"onCameraPreviewEvent", new Class[] { KetaiCamera.class });
			PApplet.println("KetaiCamera found onCameraPreviewEvent for PImage in parent... ");
		} catch (Exception e) {
			// no such method, or an error.. which is fine, just ignore
			onPreviewEventMethodPImage = null;
			PApplet.println("KetaiCamera did not find onCameraPreviewEvent for Image Method: "
					+ e.getMessage());
		}

		PApplet.println("KetaiCamera completed instantiation... ");
	}

	public void enableFlash() {
		enableFlash = true;
		if (camera == null)
			return;

		Parameters cameraParameters = camera.getParameters();
		cameraParameters.setFlashMode(Parameters.FLASH_MODE_TORCH);
		camera.setParameters(cameraParameters);
	}

	public void disableFlash() {
		enableFlash = false;
		if (camera == null)
			return;

		Parameters cameraParameters = camera.getParameters();
		cameraParameters.setFlashMode(Parameters.FLASH_MODE_OFF);
		camera.setParameters(cameraParameters);
	}

	public void start() {
		try {
			boolean isNV21Supported = false;

			PApplet.println("KetaiCamera: opening camera...");
			if (camera == null)
				camera = Camera.open();

			Parameters cameraParameters = camera.getParameters();
			List<Integer> list = cameraParameters.getSupportedPreviewFormats();

			PApplet.println("Supported preview modes...");
			for (Integer i : list) {

				if (i == ImageFormat.RGB_565) {
					PApplet.println("RGB Image preview supported!!!!(try better resolutions/fps combos)");
					isRGBPreviewSupported = true;
				}

				if (i == ImageFormat.NV21)
					isNV21Supported = true;

				PApplet.println("\t" + i);
			}

			if (isRGBPreviewSupported)
				cameraParameters.setPreviewFormat(ImageFormat.RGB_565);
			else if (isNV21Supported)
				cameraParameters.setPreviewFormat(ImageFormat.NV21);
			else
				PApplet.println("Camera does not appear to provide data in a format we can convert. Sorry.");

			if (enableFlash)
				cameraParameters.setFlashMode(Parameters.FLASH_MODE_TORCH);
			else
				cameraParameters.setFlashMode(Parameters.FLASH_MODE_OFF);

			// too bad the following doesnt work yet..even on 2.2 :-/

			// /We should probably verify that the numbers passed in are
			// suppported by the camera....sure...eventually we will
			cameraParameters.setPreviewFrameRate(cameraFPS);
			cameraParameters.setPreviewSize(frameWidth, frameHeight);
			cameraParameters.setFocusMode(Parameters.FOCUS_MODE_AUTO);
			
			camera.setParameters(cameraParameters);
			isStarted = true;
			PApplet.println("KetaiCamera: Set camera parameters...");
			camera.setPreviewCallback(previewcallback);
			camera.startPreview();

			PApplet.println("Using preview format: "
					+ camera.getParameters().getPreviewFormat());

		} catch (Exception x) {
			x.printStackTrace();
			if (camera != null)
				camera.release();
			PApplet.println("Exception caught while trying to connect to camera service.  Please check your sketch permissions or that another application is not using the camera.");
		}
	}

	public boolean isFlashEnabled() {
		return enableFlash;
	}

	public void takePicture() {
		if (camera != null)
			camera.takePicture(null, null, jpegCallback);
	}

	public void onResume() {
		// if (camera == null) {
		// camera = Camera.open();
		// camera.startPreview();
		// }
	}

	PictureCallback rawCallback = new PictureCallback() { // <7>
		public void onPictureTaken(byte[] data, Camera camera) {
		}
	};

	public void read() {
		loadPixels();
		synchronized (pixels) {
			System.arraycopy(myPixels, 0, pixels, 0, frameWidth * frameHeight);
			available = false;
			updatePixels();
		}
	}

	public boolean isStarted() {
		return isStarted;
	}

	PreviewCallback previewcallback = new PreviewCallback() {
		public void onPreviewFrame(byte[] data, Camera camera) {

			if (camera == null || !isStarted)
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
			// //PApplet.println("KetaiCamera:  Unable to convert cameraPreview data to bitmap...Data has length: "
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
			if (myPixels == null)
				myPixels = new int[frameWidth * frameHeight];

			if (isRGBPreviewSupported)
				System.arraycopy(myPixels, 0, data, 0, frameWidth * frameHeight);
			else
				KetaiCamera.decodeYUV420SP(myPixels, data, frameWidth,
						frameHeight);

			if (myPixels == null)
				return;

			// PApplet.println("KetaiCamera.previewCallback: pixels buffer is of length: "
			// + myPixels.length +"/"+onPreviewEventMethod);
			if (onPreviewEventMethod != null && myPixels != null)
				try {
					onPreviewEventMethod.invoke(parent);
				} catch (Exception e) {
					PApplet.println("Disabling onCameraPreviewEvent() because of an error:"
							+ e.getMessage());
					e.printStackTrace();
					onPreviewEventMethod = null;
				}

			if (onPreviewEventMethodPImage != null && myPixels != null)
				try {
					onPreviewEventMethodPImage.invoke(parent,
							new Object[] { (KetaiCamera) self });
				} catch (Exception e) {
					PApplet.println("Disabling onCameraPreviewEvent(KetaiCamera) because of an error:"
							+ e.getMessage());
					e.printStackTrace();
					onPreviewEventMethodPImage = null;
				}
			broadcastData(self);
		}
	};

	PictureCallback jpegCallback = new PictureCallback() {
		public void onPictureTaken(byte[] data, Camera camera) {
			if (camera == null)
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
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
			}
		}
	};

	public void stop() {
		PApplet.println("Stopping Camera...");
		if (camera != null && isStarted) {
			isStarted = false;
			camera.stopPreview();
			camera.setPreviewCallback(null);
			camera.release();
			camera = null;
		}
	}

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

	public Collection<? extends String> list() {
		Vector<String> list = new Vector<String>();
		list.add("Camera");
		return list;
	}

	public void startService() {
		if (!isStarted || camera == null)
			start();
	}

	public int getStatus() {
		if (isStarted)
			return IKetaiInputService.STATE_STARTED;
		else
			return IKetaiInputService.STATE_STOPPED;

	}

	public void stopService() {
		stop();
	}

	public String getServiceDescription() {
		return "Android camera access.";
	}

	public void registerAnalyzer(IKetaiAnalyzer _analyzer) {
		if (listeners.contains(_analyzer))
			return;
		PApplet.println("KetaiCamera Registering this analyzer: "
				+ _analyzer.getClass());
		listeners.add(_analyzer);
	}

	public void removeAnalyzer(IKetaiAnalyzer _analyzer) {
		listeners.remove(_analyzer);
	}

	public void broadcastData(Object data) {
		for (IKetaiAnalyzer analyzer : listeners) {
			analyzer.analyzeData(data);
		}
	}

}
