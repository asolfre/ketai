/*
tap the screen to start collecting data
tap again to stop collecting data
press the menu button to export to test.csv
*/
import edu.uic.ketai.*;
Ketai ketai;
PFont font;
long dataCount;

void setup()
{
  //Create Ketai object
  ketai = new Ketai(this);
  //Enable the default sensor analyzer
  ketai.enableDefaultAnalyzer();  
  //Get the current data count
  dataCount = ketai.getDataCount();
  orientation(PORTRAIT);
  font = createFont("Helvetica", 22);
}

void draw() {
  //Display Ketai status
  if(ketai.isCollectingData())
    text("Collecting Data...", 20,20);
  else
    text("Not Collecting Data...", 20,20);
  text("Current Data count: " + dataCount, 20, 60);
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

void keyPressed() {
  if (key == CODED) {
    if (keyCode == MENU) {
      println("Exporting data...");
      //Export all data (this will delete all data in DB)
      ketai.exportData("test");
      //update the data count
      dataCount = ketai.getDataCount();
    }
  }
}

//Register accelerometer
void onAccelerometerSensorEvent(long time, int accuracy, float x, float y, float z)
{
  println(x);
}

