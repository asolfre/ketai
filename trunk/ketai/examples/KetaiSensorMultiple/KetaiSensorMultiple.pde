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
  orientation(PORTRAIT);
  sensor = new KetaiSensor(this);
  sensor.list();
  sensor.start();
  orientation = new PVector();
  accelerometer = new PVector();
  magneticField = new PVector();
}

void draw()
{
  background(0);
  text("Orientation data:" 
  + nf(orientation.x, 3, 2) + "/" 
  + nf(orientation.y, 3, 2) + "/" 
  + nf(orientation.z, 3, 2), 5, 20); 
  text("Accelerometer data:" 
  + nf(accelerometer.x, 3, 2) + "/" 
  + nf(accelerometer.y, 3, 2) + "/" 
  + nf(accelerometer.z, 3, 2), 5, 50); 
  text("MagneticField data:" 
  + nf(magneticField.x, 3, 2) + "/" 
  + nf(magneticField.y, 3, 2) + "/" 
  + nf(magneticField.z, 3, 2), 5, 80);
}

void onOrientationEvent(float x, float y, float z, long time, int accuracy)
{
  orientation.set(x,y,z);
}

void onAccelerometerEvent(float x, float y, float z, long time, int accuracy)
{
  accelerometer.set(x,y,z);
}

void onMagneticFieldEvent(float x, float y, float z, long time, int accuracy)
{
  magneticField.set(x,y,z);
}

public void mousePressed() { 
  if(sensor.isStarted())
    sensor.stop(); 
  else
    sensor.start(); 
  println("KetaiSensor isStarted: " + sensor.isStarted());
}
