import edu.uic.ketai.*;
import edu.uic.ketai.inputService.KetaiCamera;

Ketai ketai;
PFont font;
long dataCount;
color backgroundColor;

void setup()
{
  //Create Ketai object
  ketai = new Ketai(this);
  ketai.setCameraParameters(320, 240, 10);
  backgroundColor = color(255,255,255);

  ketai.enableCamera();
  ketai.enableFaceAnalyzer();

  //Get the current data count
  dataCount = ketai.getDataCount();

  //Let's lock the orientation so we dont restart on orientation changes
  orientation(LANDSCAPE);

  font = loadFont("font.vlw");
  textFont(font, 22);
  frameRate(10);
  noFill();
  strokeWeight(3);
  stroke(255, 0, 0);

  background(0);
}

void draw() {

  //We will simply display the ketai status and data count
  if(ketai.isCollectingData())
    text("Collecting Data...", 20,20);
  else
    text("Not Collecting Data...", 20,20);

  text("Current Data count: " + dataCount, 20, 60);
}


//  We will toggle collection by touching the screen
void mousePressed()
{
  if(ketai.isCollectingData())
  {
    ketai.stopCollectingData();
    dataCount = ketai.getDataCount();
  }
  else
    ketai.startCollectingData();
}

void keyPressed() {
  background(backgroundColor);

  if (key == CODED) {
    if (keyCode == MENU) {
      println("Exporting data...");

      //Export all data (this will delete all data in DB)
      ketai.exportData("test");

      //update the data count
      dataCount = ketai.getDataCount();
      background(0);
    }
  }
}


/*
     The KetaiFaceAnalyzer is simply a wrapper for the android face detector.  We will get a "face" event with a PVector
       of the x/y coordinates of the point between the eyes and the z variable in our PVector will be the distance between
       the eyes.
*/
void onKetaiEvent(String _eventName, Object _data)
{
  if (_eventName.equals("face"))
  {
    if(_data == null || !(_data instanceof PVector))
      return;

    PVector _where = (PVector)_data;
    backgroundColor = color(0,255,0);
    background(backgroundColor);
    pushMatrix();
    translate(screenWidth/2 - 320/2, screenHeight/2 - 240/2);
    ellipse(_where.x, _where.y, _where.z, 20);
    popMatrix();
  }
  else if(_eventName.equals("noface"))
  {
    backgroundColor = color(0,0,0);    
    background(backgroundColor);
  }
}

void onCameraPreviewEvent(KetaiCamera video)
{
  video.read();

  //We can stretch the camera preview to fill the screen
  imageMode(CENTER);
  image(video, screenWidth/2,screenHeight/2);
}  

