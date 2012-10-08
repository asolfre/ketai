/**
 * <p>Ketai Sensor Library for Android: http://KetaiProject.org</p>
 *
 * <p>Ketai Data SQL Features:
 * <ul>
 * <li>Captures Sensor data into SQLite database</li>
 * <li>Sends query to SQLight database </li>
 * <li>Maps values onto the screen</li>
 * </ul>
 * <p>Updated: 2012-03-10 Daniel Sauter/j.duran</p>
 */

import ketai.data.*;
import android.database.Cursor;

Ketai ketai;
long dataCount;
PVector touch = new PVector(0, 0, 0);
boolean somethingchanged = false;
int messageBoxSize = 35;  //in pixels

void setup()
{
  orientation(LANDSCAPE);
  textAlign(CENTER, CENTER);
  textSize(36);
  ketai = new Ketai(this);
  //Enable the default sensor manager & analyzer
  ketai.enableSensorManager();
  ketai.enableDefaultSensorAnalyzer();
  //Get the current data count
  dataCount = ketai.getDataCount();
  background(78, 93, 75);
  fill(78, 93, 75);
}

void draw() {
  renderVisualization();
  drawInterface();
  ellipse(touch.x, touch.y, 15, 15);
}

void drawInterface()
{
  //draw the bottom 'button'
  line(0, screenHeight-messageBoxSize, screenWidth, screenHeight - messageBoxSize);
  pushStyle();
  fill(0);
  stroke(0);
  textSize(24);
  rect(0, screenHeight-messageBoxSize, screenWidth, messageBoxSize);
  fill(255);
  if (ketai.isCollectingData())
  {
    background(78, 93, 75);  
    text("Press to stop collecting data.", screenWidth/2, screenHeight - messageBoxSize/2);
  }
  else
  {
    text("Press to start collecting data. Currently " + dataCount + " data points.", screenWidth/2, screenHeight - messageBoxSize/2);
    text("Press the menu key to clear data.", screenWidth/2, 30);
  }
  popStyle();
}

void renderVisualization()
{
  //only render if something changed or not collecting data
  if (ketai.isCollectingData() || !somethingchanged )
    return;
  background(78, 93, 75);  

  ArrayList points = new ArrayList();
  float temp;

  long count = ketai.datamanager.getRecordCountForTable("sensor_events");  
  if (count < 1)
  {
    text("No data to plot!", screenWidth/2, screenHeight/2);
    return;
  }

  float minY=0;
  float maxY = 0; 

  //get max/min values for the y direction for mapping
  temp = Float.parseFloat(ketai.datamanager.getFieldMin("sensor_events", "value0"));
  if (temp < minY)
    minY = temp; 
  temp = Float.parseFloat(ketai.datamanager.getFieldMin("sensor_events", "value1"));
  if (temp < minY)
    minY = temp; 
  temp = Float.parseFloat(ketai.datamanager.getFieldMin("sensor_events", "value2"));
  if (temp < minY)
    minY = temp; 

  temp = Float.parseFloat(ketai.datamanager.getFieldMax("sensor_events", "value0"));
  if (temp > maxY)
    maxY = temp; 
  temp = Float.parseFloat(ketai.datamanager.getFieldMax("sensor_events", "value1"));
  if (temp > maxY)
    maxY = temp; 
  temp = Float.parseFloat(ketai.datamanager.getFieldMax("sensor_events", "value2"));
  if (temp > maxY)
    maxY = temp; 

  //lets grab a screenful of data
  long val = (long)map(touch.x, 0, screenWidth, 0, count-screenWidth);
  String q = "SELECT * from sensor_events ORDER BY timestamp ASC LIMIT "+val + ", " + screenWidth;

  Cursor cursor = ketai.datamanager.executeSQL(q);
  //nada to visualize
  if (cursor == null || cursor.getCount() < 1)
  {
    println("nada returned");
    return;
  }

  //iterate through data
  if (cursor.moveToFirst()) {
    do {
      PVector v = new PVector(map(cursor.getFloat(cursor.getColumnIndex("value0")), minY, maxY, 0, screenHeight-messageBoxSize), 
      map(cursor.getFloat(cursor.getColumnIndex("value1")), minY, maxY, 0, screenHeight-messageBoxSize), 
      map(cursor.getFloat(cursor.getColumnIndex("value2")), minY, maxY, 0, screenHeight-messageBoxSize));
      points.add(cursor.getPosition(), v);
    } 
    while (cursor.moveToNext ());
  }

  //draw the points
  for (int k = 0; k < points.size(); k++)
  {
    pushStyle();
    PVector p = (PVector)points.get(k);
    PVector n;

    if ((k+1) < points.size())
      n= (PVector)points.get(k+1);
    else 
      n=p;
    stroke(255, 0, 0);
    line(k, p.x, k, n.x);
    stroke(0, 255, 0);
    line(k, p.y, k, n.y);
    stroke(0, 0, 255);
    line(k, p.z, k, n.z);
    popStyle();
  }

  //lets draw relative zero
  int zero = (int)map(0, minY, maxY, 0, screenHeight);
  stroke(255);
  line(0, zero, screenWidth, zero);
  somethingchanged=false;
}

void mousePressed()
{
  if (mouseY > screenHeight-messageBoxSize)
  {
    if (ketai.isCollectingData())
    {
      ketai.stopCollectingData();
      dataCount = ketai.getDataCount();
    }
    else
      ketai.startCollectingData();
    drawInterface();
    return;
  }
  somethingchanged = true;
  touch.set(mouseX, mouseY, 0);
}

void mouseDragged()
{
  somethingchanged = true;
  touch.set(mouseX, mouseY, 0);
}

void keyPressed() {
  if (key == CODED) {
    if (keyCode == MENU) {
      ketai.datamanager.deleteAllData();
      dataCount = ketai.getDataCount();
      somethingchanged = true;
    }
    drawInterface();
    return;
  }
}

// Capturing accelerometer data 
// (you can capture multiple sensors by adding SensorEvent methods for other sensors)
void onAccelerometerEvent(float x, float y, float z, long time, int accuracy)
{
  // The analyzer will handle the data this time
}

