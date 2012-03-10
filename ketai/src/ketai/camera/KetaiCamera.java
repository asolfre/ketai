package ketai.camera;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import ketai.data.IDataConsumer;
import ketai.data.IDataProducer;



import processing.core.PImage;
import processing.core.PApplet;

import android.graphics.ImageFormat;
//import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.Size;
import android.media.MediaScannerConnection;
import android.media.MediaScannerConnection.OnScanCompletedListener;
import android.net.Uri;
import android.os.Environment;
import android.view.Surface;

public class KetaiCamera extends PImage implements IDataProducer{

	private PApplet parent;
	private Camera camera;
	private int[] myPixels;
	private Method onPreviewEventMethod, onPreviewEventMethodPImage;
	private int frameWidth, frameHeight, cameraFPS, cameraID;
	private int photoWidth, photoHeight;
	public boolean isStarted, enableFlash, isRGBPreviewSupported;
	private String savePhotoPath = "";
	PImage self;
	// Thread runner;
	boolean available = false;
	private ArrayList<IDataConsumer> consumers;

	public KetaiCamera(PApplet pParent, int _width, int _height,
			int _framesPerSecond) {
		parent = pParent;
		frameWidth = _width;
		frameHeight = _height;
		photoWidth = frameWidth;
		photoHeight = frameHeight;
		cameraFPS = _framesPerSecond;
		isStarted = false;
		myPixels = new int[_width * _height];
		self = this;
		isRGBPreviewSupported = false;
		enableFlash = false;
		cameraID = 0;
		consumers = new ArrayList<IDataConsumer>();

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

	public int getImageWidth() {
		return frameWidth;
	}

	public int getImageHeight() {
		return frameHeight;
	}

	public int getPhotoWidth() {
		return photoWidth;
	}

	public int getPhotoHeight() {
		return photoHeight;
	}

	public void setPhotoSize(int width, int height) {
		photoWidth = width;
		photoHeight = height;
		determineCameraParameters();
	}

	public void enableFlash() {
		enableFlash = true;
		if (camera == null)
			return;

		Parameters cameraParameters = camera.getParameters();
		cameraParameters.setFlashMode(Parameters.FLASH_MODE_TORCH);
		// check if flash is supported before setting it
		try {
			camera.setParameters(cameraParameters);
		} catch (Exception x) {
		}// doesnt support flash...its ok...

	}

	public void disableFlash() {
		enableFlash = false;
		if (camera == null)
			return;

		Parameters cameraParameters = camera.getParameters();
		cameraParameters.setFlashMode(Parameters.FLASH_MODE_OFF);
		try {
			camera.setParameters(cameraParameters);
		} catch (Exception x) {
		} // nopers
	}

	public void setCameraID(int _id) {
		if (_id < Camera.getNumberOfCameras())
			cameraID = _id;
	}

	public int getCameraID() {
		return cameraID;
	}

	public boolean start() {
		try {

			PApplet.println("KetaiCamera: opening camera...");
			if (camera == null)
				try {
					camera = Camera.open(cameraID);
				} catch (Exception x) {
					PApplet.println("Failed to open camera for camera ID: "
							+ cameraID + ":" + x.getMessage());
					return false;
				}
			Parameters cameraParameters = camera.getParameters();
			List<Integer> list = cameraParameters.getSupportedPreviewFormats();

			PApplet.println("Supported preview modes...");
			for (Integer i : list) {

				if (i == ImageFormat.RGB_565) {
					PApplet.println("RGB Image preview supported!!!!(try better resolutions/fps combos)");
					isRGBPreviewSupported = true;
				}

				PApplet.println("\t" + i);
			}

			if (isRGBPreviewSupported)
				cameraParameters.setPreviewFormat(ImageFormat.RGB_565);
			// else if (isNV21Supported)
			// cameraParameters.setPreviewFormat(ImageFormat.NV21);
			// else
			// PApplet.println("Camera does not appear to provide data in a format we can convert. Sorry.");
			PApplet.println("default imageformat:"
					+ cameraParameters.getPreviewFormat());

			List<String> flashmodes = cameraParameters.getSupportedFlashModes();
			if (flashmodes != null && flashmodes.size() > 0) {
				for (String s : flashmodes)
					PApplet.println("supported flashmode: " + s);
				if (enableFlash)
					cameraParameters.setFlashMode(Parameters.FLASH_MODE_TORCH);
				else
					cameraParameters.setFlashMode(Parameters.FLASH_MODE_OFF);
			} else
				PApplet.println("No flash support.");

			// camera.setPreviewDisplay(mHolder);
			int rotation = parent.getWindowManager().getDefaultDisplay()
					.getRotation();
			int degrees = 0;
			switch (rotation) {
			case Surface.ROTATION_0:
				degrees = 0;
				break;
			case Surface.ROTATION_90:
				degrees = 90;
				break;
			case Surface.ROTATION_180:
				degrees = 180;
				break;
			case Surface.ROTATION_270:
				degrees = 270;
				break;
			}
			Camera.CameraInfo info = new CameraInfo();
			Camera.getCameraInfo(cameraID, info);
			int result;
			if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
				result = 270;
				result = (360 - result) % 360; // compensate the mirror
			} else { // back-facing
				result = (info.orientation - degrees + 360) % 360;
			}
			// result = (info.orientation - degrees + 360) % 360;
			camera.setDisplayOrientation(result);

			camera.setParameters(cameraParameters);
			camera.setPreviewCallback(previewcallback);

			// set sizes
			determineCameraParameters();

			// create a default texture to kickoff preview then remove it
			// if textures arent supported then set nothing and move along
/** > API 12 **/
//			try {
//				SurfaceTexture st = new SurfaceTexture(0);
//				camera.setPreviewTexture(st);
//				camera.startPreview();
//				camera.setPreviewDisplay(null);
//			} catch (NoClassDefFoundError x) {
//				camera.startPreview();
//			}
			camera.startPreview();
			isStarted = true;

			PApplet.println("Using preview format: "
					+ camera.getParameters().getPreviewFormat());

			PApplet.println("Preview size: " + frameWidth + "x" + frameHeight
					+ "," + cameraFPS);
			PApplet.println("Photo size: " + photoWidth + "x" + photoHeight);

			return true;
		} catch (Exception x) {
			x.printStackTrace();
			if (camera != null)
				camera.release();
			PApplet.println("Exception caught while trying to connect to camera service.  Please check your sketch permissions or that another application is not using the camera.");
			return false;
		}
	}

	public boolean isFlashEnabled() {
		return enableFlash;
	}

	public void takePicture() {
		if (camera != null) {
			savePhotoPath = "";
			takePicture(savePhotoPath);

		}
	}

	public void takePicture(String _filename) {
		savePhotoPath = _filename;
		if (camera != null)
			camera.takePicture(null, null, jpegCallback);
	}

	public void onResume() {
		if (camera == null) {
			camera = Camera.open();
		}

		if (isStarted())
			return;

		try {
			camera.reconnect();
			camera.startPreview();
			isStarted = true;
		} catch (IOException e) {
			e.printStackTrace();
			start();
		}
	}

	PictureCallback rawCallback = new PictureCallback() { // <7>
		public void onPictureTaken(byte[] data, Camera camera) {
		}
	};

	public void read() {
		if (pixels.length != frameWidth * frameHeight)
			pixels = new int[frameWidth * frameHeight];
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

	int lastProcessedFrame = 0;

	PreviewCallback previewcallback = new PreviewCallback() {
		public void onPreviewFrame(byte[] data, Camera camera) {
			if ((parent.millis() - lastProcessedFrame) < (1000 / cameraFPS))
				return;

			lastProcessedFrame = parent.millis();

			if (camera == null || !isStarted)
				return;

			if (myPixels == null || myPixels.length != frameWidth * frameHeight)
				myPixels = new int[frameWidth * frameHeight];

			if (isRGBPreviewSupported)
				System.arraycopy(myPixels, 0, data, 0, frameWidth * frameHeight);
			else
				decodeYUV420SP(data);

			if (myPixels == null)
				return;

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

			for(IDataConsumer c: consumers)
			{
				c.consumeData(self);
			}	
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

				PApplet.println(savePhotoPath);

				// Write to SD Card
				if (savePhotoPath == "") {

					File mediaStorageDir = new File(
							Environment
									.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
							"kCameraApp");

					// Create the storage directory if it does not exist
					if (!mediaStorageDir.exists()) {
						if (!mediaStorageDir.mkdirs()) {
							PApplet.println("failed to create directory to save photo");
						}
					}

					// Create a media file name
					String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
							.format(new Date());
					File mediaFile;
					mediaFile = new File(mediaStorageDir.getPath()
							+ File.separator + "IMG_" + timeStamp + ".jpg");
					PApplet.println("Saving image: "
							+ mediaFile.getAbsolutePath());

					outStream = new FileOutputStream(
							mediaFile.getAbsolutePath());

					outStream.write(data);
					outStream.close();
					String[] paths = { mediaFile.getAbsolutePath() };
					MediaScannerConnection.scanFile(
							parent.getApplicationContext(), paths, null,
							myScannerCallback);

				} else {
					PApplet.println("Saving image: " + savePhotoPath);
					outStream = new FileOutputStream(savePhotoPath);
					outStream.write(data);
					outStream.close();

				}

				// create a default texture to kickoff preview then remove it
				// if textures arent supported then set nothing and move along
/** > API 12 **/
//				try {
//					SurfaceTexture st = new SurfaceTexture(0);
//					camera.setPreviewTexture(st);
//					camera.startPreview();
//					camera.setPreviewDisplay(null);
//				} catch (NoClassDefFoundError x) {
//					camera.startPreview();
//				}

			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
			}
		}
	};

	private OnScanCompletedListener myScannerCallback = new OnScanCompletedListener() {
		public void onScanCompleted(String arg0, Uri arg1) {
			PApplet.println("Media Scanner returned: " + arg1.toString()
					+ " => " + arg0);
		}
	};

	public void pause() {
		camera.stopPreview();
		camera.release();
		isStarted = false;
	}

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

	public void decodeYUV420SP(byte[] yuv420sp) {

		// here we're using our own internal PImage attributes
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

				// use interal buffer instead of pixels for UX reasons
				myPixels[yp] = 0xff000000 | ((r << 6) & 0xff0000)
						| ((g >> 2) & 0xff00) | ((b >> 10) & 0xff);
			}
		}

	}

	public int getNumberOfCameras() {
		return Camera.getNumberOfCameras();
	}

	public Collection<? extends String> list() {
		Vector<String> list = new Vector<String>();
		String facing = "";
		int count = Camera.getNumberOfCameras();
		for (int i = 0; i < count; i++) {
			Camera.CameraInfo info = new Camera.CameraInfo();
			Camera.getCameraInfo(i, info);
			if (info.facing == CameraInfo.CAMERA_FACING_BACK)
				facing = "backfacing";
			else
				facing = "frontfacing";

			list.add("camera id [" + i + "] facing:" + facing);
			PApplet.println("camera id[" + i + "] facing:" + facing);
		}
		return list;
	}

	// figure out closest requested width/height, FPS combos
	private void determineCameraParameters() {
		if (camera == null)
			return;

		PApplet.println("Requested camera parameters as (w,h,fps):"
				+ frameWidth + "," + frameHeight + "," + cameraFPS);

		Parameters cameraParameters = camera.getParameters();

		List<Size> supportedSizes = cameraParameters.getSupportedPreviewSizes();
		boolean foundSupportedSize = false;
		Size nearestRequestedSize = null;

		for (Size s : supportedSizes) {
			PApplet.println("Checking supported preview size:" + s.width + ","
					+ s.height);
			if (nearestRequestedSize == null)
				nearestRequestedSize = s;

			if (!foundSupportedSize) {
				if (s.width == frameWidth && s.height == frameHeight) {
					PApplet.println("Found matching camera size");
					nearestRequestedSize = s;
					foundSupportedSize = true;
				} else {
					int delta = (frameWidth * frameHeight)
							- (nearestRequestedSize.height * nearestRequestedSize.width);
					int current = (frameWidth * frameHeight)
							- (s.height * s.width);
					delta = Math.abs(delta);
					current = Math.abs(current);
					if (current < delta)
						nearestRequestedSize = s;
				}
			}
		}
		if (nearestRequestedSize != null) {
			frameWidth = nearestRequestedSize.width;
			frameHeight = nearestRequestedSize.height;
		}
		cameraParameters.setPreviewSize(frameWidth, frameHeight);

		supportedSizes = cameraParameters.getSupportedPictureSizes();
		foundSupportedSize = false;
		nearestRequestedSize = null;

		for (Size s : supportedSizes) {
			if (!foundSupportedSize) {
				if (s.width == photoWidth && s.height == photoHeight) {
					nearestRequestedSize = s;

					foundSupportedSize = true;
				} else if (photoWidth <= s.width) {
					nearestRequestedSize = s;
				}
			}
		}
		if (nearestRequestedSize != null) {
			photoWidth = nearestRequestedSize.width;
			photoHeight = nearestRequestedSize.height;
		}
		cameraParameters.setPictureSize(photoWidth, photoHeight);

		List<Integer> supportedFPS = cameraParameters
				.getSupportedPreviewFrameRates();
		int nearestFPS = 0;

		for (int r : supportedFPS) {
			if ((Math.abs(cameraFPS - r)) > (Math.abs(cameraFPS - nearestFPS))) {
				nearestFPS = r;
			}
		}
		cameraParameters.setPreviewFrameRate(nearestFPS);

		camera.setParameters(cameraParameters);

		cameraParameters = camera.getParameters();
		frameHeight = cameraParameters.getPreviewSize().height;
		frameWidth = cameraParameters.getPreviewSize().width;

		// if what was requested is what we set then update
		// otherwise we'll compensate here
		if (cameraFPS == cameraParameters.getPreviewFrameRate())
			cameraFPS = cameraParameters.getPreviewFrameRate();
		PApplet.println("Calculated camera parameters as (w,h,fps):"
				+ frameWidth + "," + frameHeight + "," + cameraFPS);
		PApplet.println(cameraParameters.flatten());

		// update PImage
		resize(frameWidth, frameHeight);
	}

	public void registerDataConsumer(IDataConsumer _dataConsumer) {
		consumers.add(_dataConsumer);	
	}

	public void removeDataConsumer(IDataConsumer _dataConsumer) {
			consumers.remove(_dataConsumer);
	}
}
