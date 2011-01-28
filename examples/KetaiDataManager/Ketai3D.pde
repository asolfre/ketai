import edu.uic.ketai.inputService.*; 
KetaiSensorManager sensorManager;
PVector accelerometer;

float x, y, z;
float smoothx, smoothy, smoothz;

void setup() {
  size(480, 800, A3D);
  orientation(PORTRAIT);   
  sensorManager = new KetaiSensorManager(this);
  sensorManager.start(); 
  accelerometer = new PVector(); 
  noStroke();
}

void draw() {
  background(200);
  fill(0);
  text("Accelerometer data:\n" + nfp(accelerometer.x, 2, 2) + "\n" +
                                 nfp(accelerometer.y, 2, 2) + "\n" +
                                 nfp(accelerometer.z, 2, 2) + "\n" +
       "Framerate:" + nf(frameRate, 2, 2) + "fps", 30, 50); 

  translate(width/2, height/2, 0);
  scale(height/2); 

  // Spotlight from top.
  spotLight(155, 180, 230, 0, 0, 0.6, 0, 0, -1, THIRD_PI, 1);

  PVector up = new PVector(0, 1, 0); 
  // dir vector
  PVector N = new PVector(-accelerometer.x, -accelerometer.y, -accelerometer.z);
  N.normalize(); 
  // up vector
  PVector U = up.cross(N);
  U.normalize(); 
  // right vector
  PVector V = N.cross(U);
  V.normalize(); 
  applyMatrix(U.x, U.y, U.z, 0, V.x, V.y, V.z, 0, N.x, N.y, N.z, 0, 0, 0, 0, 1); 

  // Updating sphere position
  if (z <= 0.5) x += accelerometer.x / 100;
  if (z <= 0.5) y += accelerometer.y / 100;
  if (abs(x) > 0.51 || abs(y) > 0.51) z += abs(accelerometer.z - 9.81);
  if (abs(z) > 5) {
    x = y = z = 0;
  }  

  fill(150, 150, 230);
  pushMatrix();
  translate(x, y, z - 0.08);
  sphere(0.08);
  popMatrix();  
  fill(100, 100, 200);
  translate(0, 0, 0.1);
  box(1, 1, 0.2);
}

void onAccelerometerSensorEvent(long time, int accuracy, float x, float y, float z) {
  smoothx += 0.1 * (x - smoothx);
  smoothy += 0.1 * (y - smoothy);
  smoothz += 0.1 * (z - smoothz);
  accelerometer.set(smoothx, smoothy, smoothz);
}
