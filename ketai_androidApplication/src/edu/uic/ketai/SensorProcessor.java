package edu.uic.ketai;

import java.util.List;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

public class SensorProcessor implements SensorEventListener{
	private DataManager dataManager; 
    SensorManager sensorManager = null;
    private boolean collect = false;
    private int sensors = SensorManager.SENSOR_ALL;
	
	SensorProcessor(DataManager dm, SensorManager sm)
	{	
		dataManager = dm;
		sensorManager = sm;
		
		List <Sensor> foo = sensorManager.getSensorList(Sensor.TYPE_ALL);
		for (Sensor s : foo)
		{
			Log.w("SensorProcessor: ", s.getName() + ":" + s.getType());
		}
	}
	
	public void setSensorsToListenTo(int sensorz)
	{ sensors = sensorz; }
	
	public int getSensorsToListenTo() { return sensors; }
	
	public void toggleCollect()
	{
		if(collect){
			collect = false;
		    sensorManager.unregisterListener(this);
		}
		else{
			collect = true;
		      Sensor oSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);       
		      Sensor aSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		      Sensor mSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
		      
		      sensorManager.registerListener(this, oSensor, SensorManager.SENSOR_DELAY_UI);
		      sensorManager.registerListener(this, aSensor, SensorManager.SENSOR_DELAY_UI);
		      sensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_UI);
		}
	}
	
	public boolean getCollectionState() { return collect; }

	public void resume()
	{
	    // register this class as a listener for the orientation and accelerometer sensors
		sensorManager.registerListener(this, 
	            sensorManager.getDefaultSensor(SensorManager.SENSOR_ALL),
	            SensorManager.SENSOR_DELAY_NORMAL);
	}
	
    public void stop() {
        // unregister listener
    	sensorManager.unregisterListener(this);    }

	public void onSensorChanged(SensorEvent arg0) {
		dataManager.insertSensorEvent(arg0.timestamp, arg0.sensor.getType(), arg0.values);
//		dataManager.insertSensorData(arg0.timestamp, arg0.sensor.getType(), arg0.values);
//        if ((arg0.sensor.getType() == Sensor.TYPE_ORIENTATION)) {
//        	dataManager.insertSensorData(arg0.timestamp, SensorManager.SENSOR_ORIENTATION, arg0.values);
//        }
//        if ((arg0.sensor.getType() == Sensor.TYPE_ACCELEROMETER)) {
//        	dataManager.insertSensorData(arg0.timestamp, SensorManager.SENSOR_ACCELEROMETER, arg0.values);
//        }
        
	}

	public void onAccuracyChanged(Sensor sensor, int accuracy) {
	}
}
