/*------------------------------------------------------------
Add the following within the sketch activity 
to the AndroidManifest.xml: 

 <intent-filter>
 <action android:name="android.nfc.action.TAG_DISCOVERED"/>
 <category android:name="android.intent.category.DEFAULT"/>
 </intent-filter>
------------------------------------------------------------*/

import edu.uic.ketai.*;

String stuff = "";
KetaiNFC   ketaiNFC = new KetaiNFC(this);

void setup()
{   
  orientation(PORTRAIT);
  ketaiNFC.handleIntent(getIntent());
}

void draw()
{
  background(0);
  text(millis() + "\n"+ stuff, 5, 50);
}

void onNFCEvent(String s)
{
  stuff = s;
  println("Sketch got NFCEvent: " + s);
}
