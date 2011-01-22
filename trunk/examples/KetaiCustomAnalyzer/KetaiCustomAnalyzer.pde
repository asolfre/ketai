import edu.uic.ketai.*;
import edu.uic.ketai.inputService.KetaiCamera;

Ketai ketai;
PFont font;
long dataCount;
CustomAnalyzer ca;

void setup()
{
  //Create Ketai object
  ketai = new Ketai(this);

  //Get the current data count
  dataCount = ketai.getDataCount();

  //Let's lock the orientation so we dont restart on orientation changes
  orientation(PORTRAIT); 
  font = loadFont("font.vlw");
  textFont(font, 22);
  frameRate(10);
  background(0);
  ca = new CustomAnalyzer();
  ketai.enableSensorManager();
  ketai.addAnalyzer(ca);
}

void draw() {
}


//  We will toggle collection by touching the screen
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


