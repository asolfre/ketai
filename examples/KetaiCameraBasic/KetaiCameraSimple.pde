import edu.uic.ketai.inputService.KetaiCamera; 

KetaiCamera cam;

 void setup() {
  //Let's prevent orientation restarts....camera is usually landscape anyhow
  setRequestedOrientation(android.content.pm.ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
  cam = new KetaiCamera(this, 320, 240, 30);
  background(255);
}

 void draw() {
  //nothing needed...
}

 void onCameraPreviewEvent(int[] _pixels)
{
  cam.read();
  //We can stretch the camera preview to fill the screen
  image(cam, 0,0, screenWidth, screenHeight);
}

