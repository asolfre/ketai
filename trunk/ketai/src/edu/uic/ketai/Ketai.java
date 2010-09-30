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
		FaceAnalyzer _facer = new FaceAnalyzer(datamanager);
		_facer.registerKetaiEventListener(this);
		inputmanager.addAnalyzer(_facer);
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
	public static final int KETAI_EVENT_NO_FACES_DETECTED = 2;

	public void receiveKetaiEvent(int _event, Object _payload) {
		if(_event == KETAI_EVENT_FACES_DETECTED){
			if(!(_payload instanceof PVector))
				return;
			PVector _where = (PVector)_payload;

			parent.noFill();
			parent.strokeWeight(3);
			parent.stroke(255,0,0);
			parent.ellipse(_where.x, _where.y, 20, 20);
			parent.background(0, 255,0);
		}
		
		if(_event == KETAI_EVENT_NO_FACES_DETECTED)
			parent.background(0,0,0);
		
	}
}
