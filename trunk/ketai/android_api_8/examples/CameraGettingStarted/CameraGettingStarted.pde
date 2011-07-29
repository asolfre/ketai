/**
 * <p>Ketai Sensor Library for Android: http://KetaiProject.org</p>
 *
 * <p>Ketai Camera Features:
 * <ul>
 * <li>Interface for built-in camera</li>
 * <li></li>
 * </ul>
 * <p>Updated: 2011-06-09 Daniel Sauter/j.duran</p>
 */

import edu.uic.ketai.*;

KetaiCamera cam;

void setup() {
  orientation(LANDSCAPE);
  imageMode(CENTER);
  cam = new KetaiCamera(this, 320, 240, 24);
}

void draw() {
  image(cam, screenWidth/2, screenHeight/2);
}

void onPause()
{
  super.onPause();
  //Make sure to releae the camera when we go
  //  to sleep otherwise it stays locked
  if (cam != null && cam.isStarted())
    cam.stop();
}

void onCameraPreviewEvent()
{
  cam.read();
}

void exit() {
  cam.stop();
}

// start/stop camera preview by tapping the screen
void mousePressed()
{
  if (cam.isStarted())
  {
    cam.stop();
  }
  else
    cam.start();
}
void keyPressed() {
  if (key == CODED) {
    if (keyCode == MENU) {
      if (cam.isFlashEnabled())
        cam.disableFlash();
      else
        cam.enableFlash();
    }
  }
}

