import edu.uic.ketai.*;
import edu.uic.ketai.inputService.*;
import edu.uic.ketai.data.*;
import edu.uic.ketai.analyzer.*;

import android.content.pm.ActivityInfo;
import java.text.NumberFormat;
import java.text.DecimalFormat;

PFont font;
KetaiSensorManager sensorManager;
float accelerometerX, accelerometerY, accelerometerZ;
NumberFormat formatter;

void setup()
{
  setRequestedOrientation(android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

  font = loadFont("font.vlw");
  sensorManager = new KetaiSensorManager(this);
  textFont(font, 18);
  fill(255);
  formatter = new DecimalFormat("000.00");
}


void draw()
{
  background(0);
  text("Accelerometer data:" + formatter.format(accelerometerX) + "/" + formatter.format(accelerometerY) + "/" + formatter.format(accelerometerZ), 5, 50); 
}


void onAccelerometerSensorEvent(long time, int accuracy, float x, float y, float z)
{
  accelerometerX = x;
  accelerometerY = y;
  accelerometerZ = z;
}


public void mousePressed() {
  if(sensorManager.isStarted())
    sensorManager.stop();
  else
    sensorManager.start();
  println("SensorManager isStarted: " + sensorManager.isStarted());
}

