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
float accelerometerX, accelerometerY, accelerometerZ;

void setup()
{
  orientation(PORTRAIT);
  sensor = new KetaiSensor(this);
  sensor.start();
}

void draw()
{
  background(0);
  text("Accelerometer:", 5, 50); 
  text("x:" + nfp(accelerometerX, 3, 3), 5, 70); 
  text("y:" + nfp(accelerometerY, 3, 3), 5, 90); 
  text("z:" + nfp(accelerometerZ, 3, 3), 5, 110);
}

void onAccelerometerEvent(float x, float y, float z)
{
  accelerometerX = x;
  accelerometerY = y;
  accelerometerZ = z;
}

