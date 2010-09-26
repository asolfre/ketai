import edu.uic.innovationcenter.ketai.sensor.*;
import android.content.pm.ActivityInfo;
import java.text.NumberFormat;
import java.text.DecimalFormat;

PFont font;
KetaiSensorManager sensorManager;
PVector orientation, magneticField, accelerometer;
NumberFormat formatter;

void setup()
{
  setRequestedOrientation(android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

  font = loadFont("font.vlw");
  sensorManager = new KetaiSensorManager(this);
  textFont(font, 18);
  fill(255);
  orientation = new PVector();
  accelerometer = new PVector();
  magneticField = new PVector();
  formatter = new DecimalFormat("000.00");
}


void draw()
{
  background(0);
  text("Orientation data:" + formatter.format(orientation.x) + "/" + formatter.format(orientation.y) + "/" + formatter.format(orientation.z), 5, 20); 
  text("Accelerometer data:" + formatter.format(accelerometer.x) + "/" + formatter.format(accelerometer.y) + "/" + formatter.format(accelerometer.z), 5, 50); 
  text("MagneticField data:" + formatter.format(magneticField.x) + "/" + formatter.format(magneticField.y) + "/" + formatter.format(magneticField.z), 5, 80);
}


void onOrientationSensorEvent(long time, int accuracy, float x, float y, float z)
{
  orientation.set(x,y,z);
}

void onAccelerometerSensorEvent(long time, int accuracy, float x, float y, float z)
{
  aX = x;
  accelerometer.set(x,y,z);
}


void onMagneticFieldSensorEvent(long time, int accuracy, float x, float y, float z)
{
  magneticField.set(x,y,z);
}

public void mousePressed() {
  if(sensorManager.isStarted())
    sensorManager.stop();
  else
    sensorManager.start();
  println("SensorManager isStarted: " + sensorManager.isStarted());
}


