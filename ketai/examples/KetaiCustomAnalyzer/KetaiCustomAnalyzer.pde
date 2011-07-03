/**
 * <p>Ketai Sensor Library for Android: http://KetaiProject.org</p>
 *
 * <p>Ketai Custom Analyzer Features:
 * <ul>
 * <li>Allows development of custom motion analyzers</li>
 * <li>Refer to Ketai Face Analyzer for a more basic analyzer example</li>
 * </ul>
 * <p>Updated: 2011-06-09 Daniel Sauter/Jesus Duran</p>
 */

import edu.uic.ketai.*;

Ketai ketai;
CustomAnalyzer ca;

void setup()
{
  orientation(PORTRAIT); 
  ketai = new Ketai(this);
  ketai.enableSensorManager();

  ca = new CustomAnalyzer();
  ketai.addAnalyzer(ca);
}

void draw() {
}

// Toggle collection by touching the screen
void mousePressed()
{
  if (ketai.isCollectingData())
  {
    ketai.stopCollectingData();
  }
  else
    ketai.startCollectingData();
}

void onAccelerometerEvent(float x, float y, float z, long time, int accuracy) {
}

void onKetaiEvent(String _event, Object _data)
{
  println("sketch onKetaiEvent called...");
  if (_event == "flat")
    background(0, 255, 0);
  else if (_event == "notflat")
    background(255, 0, 0);
}

