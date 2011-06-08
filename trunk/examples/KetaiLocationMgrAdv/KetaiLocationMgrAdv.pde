/*
  KetaiLocationMgrAdv - An example of using the KetaiLocationManager 
 to return a raw android Location object.
 
 Detailed information of the android location object can be found here:
 http://developer.android.com/reference/android/location/Location.html
 */

import edu.uic.ketai.*; 
import android.location.Location;

double longitude, latitude, altitude, accuracy;
KetaiLocationManager locationManager;
Location uic;

void setup() {
  //Lets lock the orientation so we do not restart on orientation changes
  orientation(PORTRAIT);

  //creates a location object that refers to UIC
  Location uic = new Location("uic");
  uic.setLatitude(41.87426641155081);
  uic.setLongitude(-87.64921545982361);
}

void draw() {
  background(0); 
  float distance = 0;

  text("Location data:\nlatitude: " + latitude + "\nlongitude: " + 
    longitude + "\naltitude: " + altitude + "\nacurracy: " + 
    accuracy + "\ndistance to uic: "+ 
    locationManager.getLocation().distanceTo(uic) + 
    "\nprovider: " + locationManager.getProvider(), 30, 50);
}

void onResume()
{
  locationManager = new KetaiLocationManager(this);
  super.onResume();
}

void onLocationEvent(Location _location)
{
  //print out the location object
  println("onLocation event: " + _location.toString());

  longitude = _location.getLongitude();
  latitude = _location.getLatitude();
  altitude = _location.getAltitude();
  accuracy = _location.getAccuracy();
}

