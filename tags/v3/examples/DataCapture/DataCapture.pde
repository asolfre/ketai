/**
 * <p>Ketai Sensor Library for Android: http://KetaiProject.org</p>
 *
 * <p>Ketai Data Manager Features:
 * <ul>
 * <li>Captures Sensor data into SQLite database</li>
 * <li>Writes data into .csv flat file</li>
 * <li>Captures all sensors registered via SensorEvent into one db/file</li>
 * </ul>
 * <p>Updated: 2011-08-01 Daniel Sauter/j.duran</p>
 */

import edu.uic.ketai.*;

Ketai ketai;
long dataCount;

void setup()
{
  ketai = new Ketai(this);
  //Enable the default sensor manager & analyzer
  ketai.enableSensorManager();
  ketai.enableDefaultSensorAnalyzer();
  //Get the current data count
  dataCount = ketai.getDataCount();
  orientation(LANDSCAPE);
  textAlign(CENTER, CENTER);
  textSize(36);
}

void draw() {
  background(78, 93, 75);
  // Status and data count
  if (ketai.isCollectingData())
    text("Collecting Data\n(tap to stop)", screenWidth/2, screenHeight/4);
  else
    text("Not Collecting Data\n(tap to start)", screenWidth/2, screenHeight/4);
  text("Current Data count: " + dataCount, screenWidth/2, screenHeight-screenHeight/4);
  if(!ketai.isCollectingData())
    text("\n\nPress menu to export and clear data.", screenWidth/2, screenHeight-screenHeight/4);
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
}

void keyPressed() {
  if (key == CODED) {
    if (keyCode == MENU) {
      println("Exporting data...");
      // Export all data into flat file "test" (and delete data from the database)
      ketai.exportData("test");
      // Update the data count
      dataCount = ketai.getDataCount();
    }
  }
}

// Capturing accelerometer data 
// (you can capture multiple sensors by adding SensorEvent methods for other sensors)
void onAccelerometerEvent(float x, float y, float z, long time, int accuracy)
{
  // The analyzer will handle the data this time
}

