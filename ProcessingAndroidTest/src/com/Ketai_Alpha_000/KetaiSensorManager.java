package com.Ketai_Alpha_000;

import android.content.Context;
import android.hardware.SensorEventListener;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;

// SENSOR MANAGER

 public class KetaiSensorManager implements SensorEventListener
{
    private SensorManager sm;
    private SensorEvent se;
    private Context context;
    
    public KetaiSensorManager(Context c)
    {
        context = c;
  	sm = (SensorManager)context.getSystemService(Context.SENSOR_SERVICE);
        Sensor aSensor = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);	      
        sm.registerListener(this, aSensor, SensorManager.SENSOR_DELAY_UI);
    }
    
    public float getNorthDirection(){
        return se.values[0];
    }
    
    public void onAccuracyChanged(Sensor arg0, int arg1) {
      
    }

    public void onSensorChanged(SensorEvent event) {
      se = event;
    }    
    
    public SensorEvent getSensorEvent()
    {
      return se;
    }
    
}

