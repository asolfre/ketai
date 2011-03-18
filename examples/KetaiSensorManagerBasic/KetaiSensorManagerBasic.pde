// Import sensor libary
import edu.uic.ketai.*;
import edu.uic.ketai.inputService.*;

// Declare sensor and accelerometer variables
KetaiSensorManager sensorManager;
float accelerometerX, accelerometerY, accelerometerZ;

void setup()
{
  sensorManager = new KetaiSensorManager(this);
  sensorManager.start();
  // Lock sketch in portrait mode
  orientation(PORTRAIT);
}

void draw()
{
  background(0);
  // Show accelerometer values
  text("Accelerometer data:", 5, 50); 
  text(nfp(accelerometerX, 2, 3), 5, 70); 
  text(nfp(accelerometerY, 2, 3), 5, 90); 
  text(nfp(accelerometerZ, 2, 3), 5, 110);
}

// Listen to new values from the sensor
void onAccelerometerSensorEvent(long time, int accuracy, float x, float y, float z)
{
  accelerometerX = x;
  accelerometerY = y;
  accelerometerZ = z;
}

