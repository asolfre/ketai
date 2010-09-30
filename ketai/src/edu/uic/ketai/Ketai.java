package edu.uic.ketai;

import processing.core.*;

import java.io.IOException;

import edu.uic.ketai.analyzer.FaceAnalyzer;
import edu.uic.ketai.analyzer.SensorAnalyzer;
import edu.uic.ketai.data.DataManager;
import edu.uic.ketai.inputService.KetaiCamera;
import edu.uic.ketai.inputService.KetaiSensorManager;

public class Ketai implements IKetaiEventListener, Runnable{
	PApplet parent;
	DataManager datamanager;
	InputManager inputmanager;
	boolean isCollecting = false;
	int cameraWidth, cameraHeight, cameraFPS;
	Thread runner;

	public Ketai(PApplet pparent) {
		parent = pparent;
		datamanager = new DataManager(parent.getApplicationContext());
		inputmanager = new InputManager(parent, datamanager);

		//setup defaults for camera
		cameraWidth = 320;
		cameraHeight = 240;
		cameraFPS = 24;
		
		runner = new Thread(this);
		runner.start();
	}
	
	public void setCameraParameters(int _width, int _height, int _framesPerSecond)
	{
		cameraWidth = _width; 
		cameraHeight = _height;
		cameraFPS = _framesPerSecond;
	}
	
	public void enableSensorManager()
	{
		inputmanager.addService(new KetaiSensorManager(parent));
	}
	
	public void enableCamera()
	{
		inputmanager.addService(new KetaiCamera(parent, cameraWidth, cameraHeight, cameraFPS));		
	}
	
	public long getDataCount() {
		return datamanager.getDataCount();
	}

	public void enableDefaultSensorAnalyzer()
	{
		inputmanager.addAnalyzer(new SensorAnalyzer(datamanager));		
	}

	public void enableFaceAnalyzer()
	{
		inputmanager.addAnalyzer(new FaceAnalyzer(datamanager));
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
			if(isCollecting)
				inputmanager.stopServices();
			datamanager.exportData(_destinationFilename);
			if(isCollecting)
				inputmanager.startServices();
		} catch (IOException x) {
			x.printStackTrace();
		}
	}

	public void clearAllData() {
		datamanager.deleteAllData();
	}

	public void run() {
		
	}


	public static final int KETAI_EVENT_FACES_DETECTED = 1;

	@Override
	public void receiveKetaiEvent(int _event, Object[] _payload) {
		parent.background(0xFF0000);
		
	}
	
}
