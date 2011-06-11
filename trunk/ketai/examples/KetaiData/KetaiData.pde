// Import sensor libary
import edu.uic.ketai.*;

Ketai ketai;
long dataCount;

void setup()
{
  //Create Ketai object
  ketai = new Ketai(this);
  //Enable the default sensor manager & analyzer
  ketai.enableSensorManager();
  ketai.enableDefaultSensorAnalyzer();
  // Lock sketch in portrait mode
  orientation(PORTRAIT);
  //Get the current data count
  dataCount = ketai.getDataCount();
}


void draw() {
  background(128);

  // Status and data count
  if(ketai.isCollectingData())
    text("Collecting Data...", 20,20);
  else
    text("Not Collecting Data...", 20,20);
  text("Current Data count: " + dataCount, 20, 60);
}


//  Start/stop collecting data
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

        // Export all data (and delete data from the database)
        ketai.exportData("test");
        // Update the data count
        dataCount = ketai.getDataCount();
    }
  }
}

// Capturing accelerometer data
void onAccelerometerSensorEvent(long time, int accuracy, float x, float y, float z)
{
  // The analyzer will handle the data this time
}

