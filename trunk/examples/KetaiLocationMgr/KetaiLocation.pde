import edu.uic.ketai.*; 

double longitude, latitude, altitude;
KetaiLocation location;

void setup() {
  //Lets lock the orientation so we do not restart on orientation changes
  orientation(PORTRAIT);
}

void draw() {
  background(0); 
  text("Location data:\n" + latitude + "\n" + longitude + "\n" + altitude + "\n" + 
    location.getProvider(), 30, 50);
}

void onResume()
{
  location = new KetaiLocation(this);
  super.onResume();
}

void onLocationEvent(double _latitude, double _longitude, double _altitude)
{
  longitude = _longitude;
  latitude = _latitude;
  altitude = _altitude;
  println("lat/long/alt: " + latitude + "/" + longitude + "/" + altitude);
}

