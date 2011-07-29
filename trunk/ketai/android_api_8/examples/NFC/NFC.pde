/**
 * <p>Ketai Sensor Library for Android: http://KetaiProject.org</p>
 *
 * <p>Ketai NFC Features:
 * <ul>
 * <li>handles incoming Near Field Communication Events</li>
 * </ul>
 * <p>Note:
 * Add the following within the sketch activity to the AndroidManifest.xml: 
 * <intent-filter>
 * <action android:name="android.nfc.action.TAG_DISCOVERED"/>
 * <category android:name="android.intent.category.DEFAULT"/>
 * </intent-filter>
 * </p> 
 * <p>Updated: 2011-06-09 Daniel Sauter/Jesus Duran</p>
 */

import edu.uic.ketai.*;

String stuff = "";
KetaiNFC ketaiNFC;
Ketai k;

void setup()
{   
  ketaiNFC = new KetaiNFC(this);
  orientation(PORTRAIT);
  textAlign(LEFT, CENTER);
  textSize(24);
}

void draw()
{
  background(0);
  text(millis() + "\n"+ stuff, 20, 0, width, height);
}

void onNFCEvent(String s)
{
  stuff = s;
  println("Sketch received NFCEvent: " + s);
}

