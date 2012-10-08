package edu.uic.ketai;

import processing.core.*;

import java.io.IOException;
import java.lang.reflect.Method;

import edu.uic.ketai.analyzer.FaceAnalyzer;
import edu.uic.ketai.analyzer.IKetaiAnalyzer;
import edu.uic.ketai.analyzer.MotionAnalyzer;
import edu.uic.ketai.analyzer.SensorAnalyzer;
import edu.uic.ketai.data.DataManager;
import edu.uic.ketai.inputService.KetaiCamera;
import edu.uic.ketai.inputService.KetaiLocation;
import edu.uic.ketai.inputService.KetaiSensor;

public class Ketai implements IKetaiEventListener, Runnable {
	PApplet parent;
	public DataManager datamanager;
	InputManager inputmanager;
	boolean isCollecting = false;
	int cameraWidth, cameraHeight, cameraFPS;
	Thread runner;
	private Method eventListener = null;
	final private static String VERSION = "0.3";

	public Ketai(PApplet pparent) {
		parent = pparent;
		datamanager = new DataManager(parent.getApplicationContext());
		inputmanager = new InputManager(parent, datamanager);
		PApplet.println("Ketai version: " + VERSION);
		// setup defaults for camera
		cameraWidth = 320;
		cameraHeight = 240;
		cameraFPS = 24;

		runner = new Thread(this);
		runner.start();

		try {
			eventListener = parent.getClass().getMethod("onKetaiEvent",
					new Class[] { String.class, Object.class });

			PApplet.println("Adding parent to event notifier...");

		} catch (NoSuchMethodException e) {
		}

	}

	public void setCameraParameters(int _width, int _height,
			int _framesPerSecond) {
		cameraWidth = _width;
		cameraHeight = _height;
		cameraFPS = _framesPerSecond;
	}

	public void enableSensorManager() {
		inputmanager.addService(new KetaiSensor(parent));
	}

	public void enableCamera() {
		inputmanager.addService(new KetaiCamera(parent, cameraWidth,
				cameraHeight, cameraFPS));
	}

	public void enableLocationManager() {
		inputmanager.addService(new KetaiLocation(parent));
	}

	public long getDataCount() {
		return datamanager.getDataCount();
	}

	public void enableDefaultSensorAnalyzer() {
		inputmanager.addAnalyzer(new SensorAnalyzer(datamanager));
	}

	public void enableFaceAnalyzer() {
		FaceAnalyzer _facer = new FaceAnalyzer(datamanager);
		_facer.registerKetaiEventListener(this);
		inputmanager.addAnalyzer(_facer);
	}

	public void enableMotionAnalyzer() {
		inputmanager.addAnalyzer(new MotionAnalyzer(datamanager));
	}

	public boolean isCollectingData() {
		return isCollecting;
	}

	public void startCollectingData() {
		inputmanager.startServices();
		isCollecting = true;
	}

	public void stopCollectingData() {
		inputmanager.stopServices();
		isCollecting = false;
	}

	public void stop() {
		inputmanager.stopServices();
	}

	public void exportData(String _destinationFilename) {
		try {
			if (isCollecting)
				inputmanager.stopServices();
			datamanager.exportData(_destinationFilename);
			if (isCollecting)
				inputmanager.startServices();
		} catch (IOException x) {
			x.printStackTrace();
		}
	}

	public void clearAllData() {
		datamanager.deleteAllData();
	}

	public void run() {
		// eventually do stuff...
	}

	public static final int KETAI_EVENT_FACES_DETECTED = 1;
	public static final int KETAI_EVENT_NO_FACES_DETECTED = 2;

	public void receiveKetaiEvent(String _event, Object _data) {
		if (eventListener != null) {
			try {
				eventListener.invoke(parent, new Object[] { _event, _data });
				return;
			} catch (Exception e) {
				PApplet.println("Ketai->onKetaiEvent() because of an error:"
						+ e.getMessage());
				e.printStackTrace();
			}
		}
	}

	public void addAnalyzer(IKetaiAnalyzer _analyzer) {
		_analyzer.registerKetaiEventListener(this);
		inputmanager.addAnalyzer(_analyzer);
	}

	public Object[] list() {
		return inputmanager.list().toArray();
	}
}