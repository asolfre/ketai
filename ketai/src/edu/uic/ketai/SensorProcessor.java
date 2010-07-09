package edu.uic.ketai;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorListener;
import android.hardware.SensorManager;

public class SensorProcessor implements SensorEventListener, SensorListener{
	private DataManager dataManager; 
    SensorManager sensorManager = null;
    private boolean collect = false;
    private int sensors = SensorManager.SENSOR_ALL;
	
	SensorProcessor(DataManager dm, SensorManager sm)
	{	
		dataManager = dm;
		sensorManager = sm;
    }
	
	public void setSensorsToListenTo(int sensorz)
	{ sensors = sensorz; }
	
	public int getSensorsToListenTo() { return sensors; }
	
	public void toggleCollect()
	{
		if(collect){
			collect = false;
			sensorManager.unregisterListener(this, sensors);
		}
		else{
			collect = true;
			sensorManager.registerListener(this, sensors, SensorManager.SENSOR_DELAY_NORMAL);
		}
	}
	
	public boolean getCollectionState() { return collect; }
	
    public void onSensorChanged(int sensor, float[] values) {
    	if(!collect) return;
        synchronized (this) {
        	long nowTime = System.currentTimeMillis();
        	
            if (sensor == SensorManager.SENSOR_ORIENTATION) {
            	dataManager.insertSensorData(nowTime, SensorManager.SENSOR_ORIENTATION, values);
            }
            if (sensor == SensorManager.SENSOR_ACCELEROMETER) {
            	dataManager.insertSensorData(nowTime, SensorManager.SENSOR_ACCELEROMETER, values);
            }            
        }
    }

	public void onAccuracyChanged(Sensor arg0, int arg1) {
		
	}

	public void resume()
	{
	    // register this class as a listener for the orientation and accelerometer sensors
		sensorManager.registerListener(this, 
	            SensorManager.SENSOR_ALL,
	            SensorManager.SENSOR_DELAY_NORMAL);
	}
	
    public void stop() {
        // unregister listener
    	sensorManager.unregisterListener(this, SensorManager.SENSOR_ALL);    }

	public void onAccuracyChanged(int arg0, int arg1) {
		
	}

	public void onSensorChanged(SensorEvent arg0) {

        if (arg0.sensor.getType() == SensorManager.SENSOR_ORIENTATION) {
        	dataManager.insertSensorData(arg0.timestamp, SensorManager.SENSOR_ORIENTATION, arg0.values);
        }
        if (arg0.sensor.getType() == SensorManager.SENSOR_ACCELEROMETER) {
        	dataManager.insertSensorData(arg0.timestamp, SensorManager.SENSOR_ACCELEROMETER, arg0.values);
        }            
	}
}
