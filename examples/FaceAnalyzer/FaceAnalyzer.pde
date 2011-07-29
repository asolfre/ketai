/**
 * <p>Ketai Sensor Library for Android: http://KetaiProject.org</p>
 *
 * <p>Ketai Face Analyzer Features:
 * <ul>
 * <li>Wrapper for the Android face detector</li>
 * <li>Returns a PVector containing PVector.x and PVector.y coordinates of 
 * point between detected eyes, as well as eye distance PVector.z</li>
 * </ul>
 * <p>Updated: 2011-06-09 Daniel Sauter/j.duran</p>
 */

import edu.uic.ketai.*;

Ketai ketai;
PFont font;
long dataCount;
color backgroundColor;
PGraphics iOverlay;

void setup()
{
  ketai = new Ketai(this);
  ketai.setCameraParameters(320, 240, 10);
  backgroundColor = color(255, 255, 255);
  ketai.enableCamera();
  ketai.enableFaceAnalyzer();
  //Get the current data count
  dataCount = ketai.getDataCount();
  orientation(LANDSCAPE);
  textAlign(CENTER, CENTER);
  textSize(36);
}

void draw() {

  if (ketai.isCollectingData())
    text("Looking for faces...", screenWidth/2, screenHeight/4);
  else
    text("Not Collecting Data\n(currently "+dataCount+" data points)", screenWidth/2, screenHeight/4);
}

void mousePressed()
{
  if (ketai.isCollectingData())
  {
    ketai.stopCollectingData();
    dataCount = ketai.getDataCount();
  }
  else
    ketai.startCollectingData();
  background(0);
}

void keyPressed() {
  background(backgroundColor);
  if (key == CODED) {
    if (keyCode == MENU) {
      println("Exporting data...");
      //Export all data and delete all data in DB
      ketai.exportData("test");
      //Update data count
      dataCount = ketai.getDataCount();
      background(0);
    }
  }
}

void onKetaiEvent(String _eventName, Object _data)
{
  if (_eventName.equals("face"))
  {
    if (_data == null || !(_data instanceof PVector))
      return;

    PVector _where = (PVector)_data;
    backgroundColor = color(0, 255, 0);
    background(backgroundColor);
    println("iCoords:" + _where.x + "," + _where.y);
  }
  else if (_eventName.equals("noface"))
  {
    backgroundColor = color(0, 0, 0);    
    background(backgroundColor);
  }
}

void onCameraPreviewEvent(KetaiCamera cam)
{
  cam.read();
  image(cam, screenWidth/2, screenHeight/2);
}  

