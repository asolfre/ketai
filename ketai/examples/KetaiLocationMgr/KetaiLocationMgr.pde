/*
  KetaiLocationMgr - An example of using the KetaiLocationManager 
 in the mose basic form.  The manager will try to use the gps 
 location data if it is available otherwise it will get location 
 information from the system provider.
 */

import edu.uic.ketai.*; 

double longitude, latitude, altitude;
KetaiLocationManager locationManager;

void setup() {
  //Lets lock the orientation so we do not restart on orientation changes
  orientation(PORTRAIT);
}

void draw() {
  background(0); 
  text("Location data:\n" + latitude + "\n" + longitude + "\n" + altitude + "\n" + 
    locationManager.getProvider(), 30, 50);
}

void onResume()
{
  locationManager = new KetaiLocationManager(this);
  super.onResume();
}

void onLocationEvent(double _latitude, double _longitude, double _altitude)
{
  longitude = _longitude;
  latitude = _latitude;
  altitude = _altitude;
  println("lat/long/alt: " + latitude + "/" + longitude + "/" + altitude);
}

