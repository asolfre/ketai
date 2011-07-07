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
  sensor = new KetaiSensor(this);
  sensor.start();
  orientation(PORTRAIT);
  textAlign(LEFT, CENTER);
  textSize(24);
}

void draw()
{
  background(0);
  text("Accelerometer: \n" + 
    "x: " + nfp(accelerometerX, 1, 3) + "\n" +
    "y: " + nfp(accelerometerY, 1, 3) + "\n" +
    "z: " + nfp(accelerometerZ, 1, 3), 20, 0, width, height);
}

void onAccelerometerEvent(float x, float y, float z)
{
  accelerometerX = x;
  accelerometerY = y;
  accelerometerZ = z;
}
