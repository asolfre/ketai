/**
 * <p>Ketai Sensor Library for Android: http://KetaiProject.org</p>
 *
 * <p>KetaiSensor Features:
 * <ul>
 * <li>handles incoming Sensor Events</li>
 * <li>Includes Accelerometer, Magnetometer, Gyroscope, GPS, Light, Proximity</li>
 * <li>Use KetaiNFC for Near Field Communication</li>
 * </ul>
 * <p>Updated: 2011-06-09 Daniel Sauter/Jesus Duran</p>
 */

import edu.uic.ketai.*;

KetaiSensor sensor;
PVector orientation, magneticField, accelerometer;

void setup()
{
  sensor = new KetaiSensor(this);
  sensor.list();
  sensor.start();
  orientation = new PVector();
  accelerometer = new PVector();
  magneticField = new PVector();
  orientation(PORTRAIT);
  textAlign(LEFT, CENTER);
  textSize(24);
}

void draw()
{
  background(0);
  text("Orientation data:" + "\n" 
    + "x: " + nfp(orientation.x, 1, 2) + "\n" 
    + "y: " + nfp(orientation.y, 1, 2) + "\n" 
    + "z: " + nfp(orientation.z, 1, 2) + "\n"
    + "Accelerometer data:" + "\n" 
    + "x: " + nfp(accelerometer.x, 1, 2) + "\n" 
    + "y: " + nfp(accelerometer.y, 1, 2) + "\n" 
    + "z: " + nfp(accelerometer.z, 1, 2) + "\n"
    + "MagneticField data:" + "\n" 
    + "x: " + nfp(magneticField.x, 1, 2) + "\n"
    + "y: " + nfp(magneticField.y, 1, 2) + "\n" 
    + "z: " + nfp(magneticField.z, 1, 2), 20, 0, width, height);
}

void onOrientationEvent(float x, float y, float z, long time, int accuracy)
{
  orientation.set(x, y, z);
}

void onAccelerometerEvent(float x, float y, float z, long time, int accuracy)
{
  accelerometer.set(x, y, z);
}

void onMagneticFieldEvent(float x, float y, float z, long time, int accuracy)
{
  magneticField.set(x, y, z);
}

public void mousePressed() { 
  if (sensor.isStarted())
    sensor.stop(); 
  else
    sensor.start(); 
  println("KetaiSensor isStarted: " + sensor.isStarted());
}

