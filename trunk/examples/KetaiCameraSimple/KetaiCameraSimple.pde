/*
    KetaiCameraSimple:  Example of basic android camera usage through the KetaiCamera
                          class.
*/

import edu.uic.ketai.inputService.KetaiCamera; 

KetaiCamera video;

 void setup() {
  //Let's prevent orientation restarts....camera is usually landscape anyhow
  setRequestedOrientation(android.content.pm.ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
  video = new KetaiCamera(this, 320, 240, 24);
  background(255);
}

 void draw() {
  //nothing needed...
}

 void onCameraPreviewEvent()
{
  video.read();
  
  //We can stretch the camera preview to fill the screen
  image(video, 0,0, screenWidth, screenHeight);
}

void pause()
{
  video.stop();
}

void mousePressed()
{
  if(video.isStarted())
  {
    video.stop();
    background(0);
  }
  else
    video.start();
}
