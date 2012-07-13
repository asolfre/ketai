//The following are required for setup
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;

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

