/*
    KetaiDataManager:  Example of starting the ketai framework to capture 
          android accelerometer data and export it to a tab delimitted file.
 */

import edu.uic.ketai.*;

Ketai ketai;
PFont font;
long dataCount;

void setup()
{
  //Create Ketai object
  ketai = new Ketai(this);

  //Enable the default sensor manager & analyzer
  ketai.enableSensorManager();
  ketai.enableDefaultSensorAnalyzer();
  

  //Get the current data count
  dataCount = ketai.getDataCount();
  
  //Let's lock the orientation so we dont restart on orientation changes
  setRequestedOrientation(android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

  font = loadFont("font.vlw");
  textFont(font, 22);
}


void draw() {
  background(128);

  //We will simply display the ketai status and data count
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

//This lets the framework know we are interested in accelerometer data
void onAccelerometerSensorEvent(long time, int accuracy, float x, float y, float z)
{
  //Dont do anything as our analyzer will handle the data.
}


