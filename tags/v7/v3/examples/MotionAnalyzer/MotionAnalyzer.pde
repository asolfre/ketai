/**
 * <p>Ketai Sensor Library for Android: http://KetaiProject.org</p>
 *
 * <p>Ketai Custom Analyzer Features:
 * <ul>
 * <li>Allows development of custom motion analyzers</li>
 * <li>Refer to Ketai Face Analyzer for a more basic analyzer example</li>
 * </ul>
 * <p>Updated: 2011-08-01 Daniel Sauter/j.duran</p>
 */

import edu.uic.ketai.*;

Ketai ketai;
CustomAnalyzer ca;

void setup()
{
  ketai = new Ketai(this);
  ketai.enableSensorManager();
  ca = new CustomAnalyzer();
  ketai.addAnalyzer(ca);
  orientation(LANDSCAPE);
  textAlign(CENTER, CENTER);
  textSize(36);
  background(78, 93, 75);
  text("Tap to start.", 0, 0, width, height);
}

void draw() {
}

// Toggle collection by touching the screen
void mousePressed()
{
  if (ketai.isCollectingData())
  {
    ketai.stopCollectingData();
    background(78, 93, 75);
    text("Tap to start.", 0, 0, width, height);
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
  {
    background(0, 255, 0);
    text("Device is laying Flat.", 0, 0, width, height);
  }
  else if (_event == "notflat")
  {    
    background(255, 0, 0);
    text("Device is NOT laying Flat.", 0, 0, width, height);
  }
}

