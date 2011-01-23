import edu.uic.ketai.inputService.*;
import android.content.pm.ActivityInfo;
KetaiSensorManager sensorManager;
PVector orientation, magneticField, accelerometer;

void setup()
{
  orientation(PORTRAIT);
  sensorManager = new KetaiSensorManager(this);
  sensorManager.start();
  orientation = new PVector();
  accelerometer = new PVector();
  magneticField = new PVector();
}

void draw()
{
  background(0);
  text("Orientation data:" 
  + nf(orientation.x, 2, 2) + "/" 
  + nf(orientation.y, 2, 2) + "/" 
  + nf(orientation.z, 2, 2), 5, 20); 
  text("Accelerometer data:" 
  + nf(accelerometer.x, 2, 2) + "/" 
  + nf(accelerometer.y, 2, 2) + "/" 
  + nf(accelerometer.z, 2, 2), 5, 50); 
  text("MagneticField data:" 
  + nf(magneticField.x, 2, 2) + "/" 
  + nf(magneticField.y, 2, 2) + "/" 
  + nf(magneticField.z, 2, 2), 5, 80);
}

void onOrientationSensorEvent(long time, int accuracy, float x, float y, float z)
{
  orientation.set(x,y,z);
}

void onAccelerometerSensorEvent(long time, int accuracy, float x, float y, float z)
{
  accelerometer.set(x,y,z);
}

void onMagneticFieldSensorEvent(long time, int accuracy, float x, float y, float z)
{
  magneticField.set(x,y,z);
}

public void mousePressed() { 
  if(sensorManager.isStarted())
    sensorManager.stop(); 
  else
    sensorManager.start(); 
  println("SensorManager isStarted: " + sensorManager.isStarted());
}
