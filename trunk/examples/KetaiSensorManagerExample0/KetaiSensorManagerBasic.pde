import edu.uic.ketai.*;
import edu.uic.ketai.inputService.*;
import edu.uic.ketai.data.*;
import edu.uic.ketai.analyzer.*;
KetaiSensorManager sensorManager;
float accelerometerX, accelerometerY, accelerometerZ;

void setup()
{
  orientation(PORTRAIT);
  sensorManager = new KetaiSensorManager(this);
  sensorManager.start();
}

void draw()
{
  background(0);
  text("Accelerometer data:", 5, 50); 
  text(nf(accelerometerX, 2, 2), 5, 70); 
  text(nf(accelerometerY, 2, 2), 5, 90); 
  text(nf(accelerometerZ, 2, 2), 5, 110);  
}

void onAccelerometerSensorEvent(long time, int accuracy, float x, float y, float z)
{
  accelerometerX = x;
  accelerometerY = y;
  accelerometerZ = z;
}
