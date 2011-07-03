/**
 * <p>Ketai Sensor Library for Android: http://KetaiProject.org</p>
 *
 * <p>Ketai Data Manager Features:
 * <ul>
 * <li>Captures Sensor data into SQLite database</li>
 * <li>Writes data into .csv flat file</li>
 * <li>Captures all sensors registered via SensorEvent into one db/file</li>
 * </ul>
 * <p>Updated: 2011-06-09 Daniel Sauter/Jesus Duran</p>
 */
 
import edu.uic.ketai.*;

Ketai ketai;
long dataCount;

void setup()
{
  orientation(PORTRAIT);
  ketai = new Ketai(this);
  //Enable the default sensor manager & analyzer
  ketai.enableSensorManager();
  ketai.enableDefaultSensorAnalyzer();
  //Get the current data count
  dataCount = ketai.getDataCount();
}

void draw() {
  background(128);
  // Status and data count
  if (ketai.isCollectingData())
    text("Collecting Data...", 20, 20);
  else
    text("Not Collecting Data...", 20, 20);
  text("Current Data count: " + dataCount, 20, 60);
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

