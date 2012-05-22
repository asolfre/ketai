/**
 * <p>Ketai Sensor Library for Android: http://KetaiProject.org</p>
 *
 * <p>Ketai NFC Features:
 * <ul>
 * <li>handles incoming Near Field Communication Events</li>
 * </ul>
 * <p>Note:
 * Add the following within the sketch activity to the AndroidManifest.xml:
 * 
 * <uses-permission android:name="android.permission.NFC" /> 
 *
 * <intent-filter>
 *   <action android:name="android.nfc.action.TECH_DISCOVERED"/>
 * </intent-filter>
 *
 * <intent-filter>
 *  <action android:name="android.nfc.action.NDEF_DISCOVERED"/>
 * </intent-filter>
 *
 * <intent-filter>
 *  <action android:name="android.nfc.action.TAG_DISCOVERED"/>
 *  <category android:name="android.intent.category.DEFAULT"/>
 * </intent-filter>
 *
 * </p> 
 * <p>Updated: 2012-03-10 Daniel Sauter/j.duran</p>
 */

//The following are required for setup
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;

import ketai.net.nfc.*;

String textRead = "";
KetaiNFC ketaiNFC;

PendingIntent mPendingIntent;  //required for nfc registration

/*
  The following code allows the sketch to handle all NFC events
 when it is running.  Eventually we would like to handle this
 in a more elegant manner for now cut'n'paste will suffice.  
 */
//====================================================================
void onCreate(Bundle savedInstanceState) {
  super.onCreate(savedInstanceState);
  mPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, 
  getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
}

void onNewIntent(Intent intent) {
  if (ketaiNFC != null)
    ketaiNFC.handleIntent(intent);
}

void onResume() {
  super.onResume();
  if (ketaiNFC == null)
    ketaiNFC = new KetaiNFC(this);

  ketaiNFC.start(mPendingIntent);
}

void onPause() {
  super.onPause();
  if (ketaiNFC != null)
    ketaiNFC.onPause();
}

//====================================================================

void setup()
{   
  if (ketaiNFC == null)
    ketaiNFC = new KetaiNFC(this);
  orientation(LANDSCAPE);
  textAlign(CENTER, CENTER);
  textSize(36);
}

void draw()
{
  background(78, 93, 75);
  text("Last tag read:\n"+ textRead, width/2, height/2);
  text("<Touch tag to read>", width/2, height-35);
}

void onNFCEvent(String txt)
{
  textRead = txt;
}

