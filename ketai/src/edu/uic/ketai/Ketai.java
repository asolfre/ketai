package edu.uic.ketai;

import processing.core.*;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Vector;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.content.Context;
import edu.uic.ketai.data.DataManager;
import edu.uic.ketai.inputService.*;

public class Ketai {
	PApplet parent;
	SensorManager sensormanager;
	DataManager datamanager;
	KetaiCamera camera;
	Method onSensorEventMethod;

	public Ketai(PApplet pparent) {
		parent = pparent;
		sensormanager = (SensorManager) parent.getApplicationContext()
				.getSystemService(Context.SENSOR_SERVICE);
		if (sensormanager == null)
			PApplet.println("KetaiSensorManager failed to initialize due to a failure to get the Sensor Service.");
		else
			PApplet.println("KetaiSensorManager instantiated...");

		try {

			// the following uses reflection to see if the parent
			// exposes the call-back method. The first argument is the method
			// name
			// followed by what should match the method argument(s)
			// in this case we will call parent.onSensorEvent(SensorEvent);
			onSensorEventMethod = parent.getClass().getMethod("onSensorEvent",
					new Class[] { SensorEvent.class });
		} catch (Exception e) {
			// no such method, or an error.. which is fine, just ignore
		}
	}

	/**
	 * List. Returns a list of the available sensors.
	 * 
	 * @return the sensors available
	 */
	public String[] listAvailableSensors() {
		Vector<String> list = new Vector<String>();

		List<Sensor> foo = sensormanager.getSensorList(Sensor.TYPE_ALL);
		for (Sensor s : foo) {
			list.add(s.getName());
			PApplet.println("KetaiSensorManager sensor list: " + s.getName()
					+ ":" + s.getType());
		}
		String returnList[] = new String[list.size()];
		list.copyInto(returnList);
		return returnList;
	}

	public boolean isCollectingData() {
		return true;
	}

	public void startCollectingData() {
	}

	public void stopCollectingData() {
	}

	public void getSensorDataUsingTimeRange(long startTime, long endTime) {

	}

	public void exportDatabase(String filename) {

	}

	public void exportDatabaseUsingDataRange(String filename, long startTime,
			long endTime) {

	}

	public void resume() {

	}

	public void stop() {
	}

}
