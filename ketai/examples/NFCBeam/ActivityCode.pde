import android.content.Intent;
import android.os.Bundle;

public void onCreate(Bundle savedInstanceState) { 
  super.onCreate(savedInstanceState);
  ketaiNFC = new KetaiNFC(this);
}

public void onNewIntent(Intent intent) { 
  if (ketaiNFC != null)
    ketaiNFC.handleIntent(intent);
}

