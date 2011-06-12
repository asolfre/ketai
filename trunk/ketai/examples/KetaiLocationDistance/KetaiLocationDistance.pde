/**
 * <p>Ketai Sensor Library for Android: http://KetaiProject.org</p>
 *
 * <p>KetaiLocation Features:
 * <ul>
 * <li>Uses GPS location data (latitude, longitude, altitude (if available)</li>
 * <li>Updates if location changes by 1 meter, or every 10 seconds</li>
 * <li>If unavailable, defaults to system provider (cell tower or WiFi network location)</li>
 * </ul>
 * More information:
 * http://developer.android.com/reference/android/location/Location.html</p>
 * <p>Updated: 2011-06-09 Daniel Sauter/Jesus Duran</p>
 */

import edu.uic.ketai.*; 
import android.location.Location;

double longitude, latitude, altitude, accuracy;
KetaiLocation location;
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
  text("Location data:\n" + 
    "latitude: " + latitude + "\n" + 
    "longitude: " + longitude + "\n" + 
    "altitude: " + altitude + "\n" +
    "acurracy: " + accuracy + "\n" +
    "distance to uic: "+ location.getLocation().distanceTo(uic) + "\n" + 
    "provider: " + location.getProvider(), 30, 50);
}

void onResume()
{
  location = new KetaiLocation(this);
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

