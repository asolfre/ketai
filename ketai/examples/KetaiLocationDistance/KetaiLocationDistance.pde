/**
 * <p>Ketai Sensor Library for Android: http://KetaiProject.org</p>
 *
 * <p>KetaiLocation Features:
 * <ul>
 * <li>handles the Android Location Object</li>
 * <li>defaults to GPS location Provider</li>
 * <li>if GPS is unavailable, uses Android default:</li>
 * <li>network (cell tower), or passive (WiFI MAC Address)</li>
 * </ul>
 * More information:
 * http://developer.android.com/reference/android/location/Location.html</p>
 * <p>Updated: 2011-06-09 Daniel Sauter/Jesus Duran</p>
 */

import edu.uic.ketai.*; 
import android.location.Location;

double longitude, latitude, altitude, accuracy;
KetaiLocationManager locationManager;
Location uic;

void setup() {
  orientation(PORTRAIT);
  //creates a location object that refers to UIC
  uic = new Location("uic");
  uic.setLatitude(41.87426641155081);
  uic.setLongitude(-87.64921545982361);
}

void draw() {
  background(0); 
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

