/**
 * <p>Ketai Sensor Library for Android: http://KetaiProject.org</p>
 *
 * <p>Ketai Data SQL Features:
 * <ul>
 * <li>Captures Sensor data into SQLite database</li>
 * <li>Writes data into .csv flat file</li>
 * <li>Sends query to SQLight database </li>
 * <li>Retrieves entries and maps them on the screen</li>
 * </ul>
 * <p>Updated: 2011-06-09 Daniel Sauter/Jesus Duran</p>
 */
 
import edu.uic.ketai.*;
import android.database.Cursor;

Ketai ketai;
long dataCount;
PVector touch = new PVector(0, 0, 0);
boolean somethingchanged = false;

void setup()
{
  orientation(LANDSCAPE);
  ketai = new Ketai(this);
  //Enable the default sensor manager & analyzer
  ketai.enableSensorManager();
  ketai.enableDefaultSensorAnalyzer();
  //Get the current data count
  dataCount = ketai.getDataCount();
  background(0);
}

void draw() {
  // Status and data count
  if (ketai.isCollectingData())
    text("Collecting Data...", 20, 20);
  else
    text("Not Collecting Data...", 20, 20);
  text("Current Data count: " + dataCount, 20, 60);
  ellipse(touch.x, touch.y, 5, 5);
  renderVisualization();
}

void renderVisualization()
{
  //only render if something changed or not collecting data
  if (ketai.isCollectingData() || !somethingchanged )
    return;

  ArrayList points = new ArrayList();
  float temp;

  background(0);
  long count = ketai.datamanager.getRecordCountForTable("sensor_events");  
  if(count < 1)
  {
    println("No data to plot!");
    return;
  }
  
  float minY=0;
  float maxY = 0; 

  //get max/min values for the y direction
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

  //lets map 50 points
  long val = (long)map(touch.x, 0, screenWidth, 0, count-screenWidth);

  String q = "SELECT * from sensor_events ORDER BY timestamp ASC LIMIT "+val + ", " + screenWidth;

  Cursor cursor = ketai.datamanager.executeSQL(q);
  //nada to visualize
  if (cursor == null || cursor.getCount() < 1)
  {
    println("nada returned");
    return;
  }
  if (cursor.moveToFirst()) {
    do {
      PVector v = new PVector(map(cursor.getLong(cursor.getColumnIndex("value0")), minY, maxY, 0, screenHeight), 
      map(cursor.getLong(cursor.getColumnIndex("value1")), minY, maxY, 0, screenHeight), 
      map(cursor.getLong(cursor.getColumnIndex("value2")), minY, maxY, 0, screenHeight));
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
    fill(0, 0, 255);
    stroke(k, p.z, k, n.z);
    popStyle();
  }
  int zero = (int)map(0, minY, maxY, 0, screenHeight);
  stroke(255);
  line(0, zero, screenWidth, zero);
  somethingchanged=false;
}

void mousePressed()
{
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
      println("Exporting data...");
      // Export all data into flat file "test" (and delete data from the database)
      ketai.exportData("test");
      // Update the data count
      dataCount = ketai.getDataCount();
    }
    return;
  }
  if (key == 't')
  {
    String[] tables = ketai.datamanager.getTables();
    println("Tables: "); 
    for (int i=0; i < tables.length; i++)
      println ("\t"+tables[i]);
    return;
  }
  if (key == 'f')
  {
    String[] tables = ketai.datamanager.getTables();
    for (int k=0; k < tables.length; k++)
    {
      println("Table: " + tables[k]);
      String[] fields = ketai.datamanager.getFields(tables[k]);
      println("\tFields: "); 
      for (int i=0; i < fields.length; i++)
        println ("\t\t"+fields[i] +" (min,max): "+ ketai.datamanager.getFieldMin(tables[k], fields[i]) + ", " + ketai.datamanager.getFieldMax(tables[k], fields[i]));
    }
    return;
  }
  if (key=='d')
  {
    if (ketai.isCollectingData())
    {
      ketai.stopCollectingData();
      dataCount = ketai.getDataCount();
    }
    else
      ketai.startCollectingData();
  }
}

// Capturing accelerometer data 
// (you can capture multiple sensors by adding SensorEvent methods for other sensors)
void onAccelerometerEvent(float x, float y, float z, long time, int accuracy)
{
  // The analyzer will handle the data this time
}

