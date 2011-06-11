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
PFont font;
long dataCount;
CustomAnalyzer ca;

void setup()
{
  ketai = new Ketai(this);
  //Get the current data count
  dataCount = ketai.getDataCount();
  orientation(PORTRAIT); 
  ca = new CustomAnalyzer();
  ketai.enableSensorManager();
  ketai.addAnalyzer(ca);
}

void draw() {
}

// Toggle collection by touching the screen
void mousePressed()
{
  if(ketai.isCollectingData())
  {
    ketai.stopCollectingData();
    dataCount = ketai.getDataCount();
  }
  else
    ketai.startCollectingData();
}

void onAccelerometerSensorEvent(long time, int accuracy, float x, float y, float z) {
}

void onKetaiEvent(String _event, Object _data)
{
  println("sketch onKetaiEvent called...");
  if(_event == "flat")
    background(0,255,0);
  else if(_event == "notflat")
    background(255,0,0);
}

void keyPressed() {
  if (key == CODED) {
    if (keyCode == MENU) {
      println("Exporting data...");
      //Export all data (this will delete all data in DB)
      ketai.exportData("test");
      //update the data count
      dataCount = ketai.getDataCount();
      background(0);
    }
  }
}


