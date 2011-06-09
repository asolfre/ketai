package edu.uic.ketai.inputService.KetaiNFC;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Vector;

import processing.core.PApplet;
import android.content.Context;
import android.nfc.NfcAdapter;
import android.nfc.NfcManager;

import edu.uic.ketai.analyzer.IKetaiAnalyzer;
import edu.uic.ketai.inputService.AbstractKetaiInputService;

public class KetaiNFC extends AbstractKetaiInputService {
	private PApplet parent;
	private NfcManager manager;
	private NfcAdapter adapter;
	private Method onNFCEventMethod;

	public KetaiNFC(PApplet pParent){
		parent = pParent;
		manager = (NfcManager) parent.getApplicationContext().getSystemService(
				Context.NFC_SERVICE);
		adapter = manager.getDefaultAdapter();
		PApplet.println("KetaiNFCManager instantiated...");
		findParentIntentions();

	}
	
	public void start()
	{
		
	}

	public void startService() {
		// TODO Auto-generated method stub

	}

	public int getStatus() {
		// TODO Auto-generated method stub
		return 0;
	}

	public void stopService() {
		// TODO Auto-generated method stub

	}

	public String getServiceDescription() {
		// TODO Auto-generated method stub
		return null;
	}

	public Collection<? extends String> list() {
		Vector<String> list = new Vector<String>();
		list.add("NFC");
		return list;
	}

	private void findParentIntentions() {

		try {
			onNFCEventMethod = parent.getClass().getMethod(
					"onNFCEvent",
					new Class[] { long.class, int.class, float.class,
							float.class, float.class });
			PApplet.println("Found onNFCEvenMethod...");
		} catch (NoSuchMethodException e) {
		}
	}

}
