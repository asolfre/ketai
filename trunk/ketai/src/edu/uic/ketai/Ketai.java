package edu.uic.ketai;

import processing.core.*;

import java.io.IOException;

import edu.uic.ketai.analyzer.SensorAnalyzer;
import edu.uic.ketai.data.DataManager;
import edu.uic.ketai.inputService.KetaiSensorManager;

public class Ketai {
	PApplet parent;
	DataManager datamanager;
	InputManager inputmanager;
	boolean isCollecting = false;

	public Ketai(PApplet pparent) {
		parent = pparent;
		datamanager = new DataManager(parent.getApplicationContext());
		inputmanager = new InputManager(parent, datamanager);

		// Let's add the default services
		inputmanager.addService(new KetaiSensorManager(parent));
	}

	public long getDataCount() {
		return datamanager.getDataCount();
	}

	public void enableDefaultAnalyzer() {
		// Let's add the default analyzers
		inputmanager.addAnalyzer(new SensorAnalyzer(datamanager));
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
			datamanager.exportData(_destinationFilename);
		} catch (IOException x) {
			x.printStackTrace();
		}
	}

	public void clearAllData() {
		datamanager.deleteAllData();
	}

}
