package edu.uic.ketai.inputService;

import java.lang.reflect.Method;
import java.util.Collection;

import processing.core.PApplet;
import android.content.Context;
import android.nfc.NfcAdapter;
import android.nfc.NfcManager;

import edu.uic.ketai.analyzer.IKetaiAnalyzer;

public class KetaiNFCManager implements IKetaiInputService {
	private PApplet parent;
	private NfcManager manager;
	private NfcAdapter adapter;
	private Method onNFCEventMethod;

	public KetaiNFCManager(PApplet pParent) {
		parent = pParent;
		manager = (NfcManager) parent.getApplicationContext().getSystemService(
				Context.NFC_SERVICE);
		adapter = manager.getDefaultAdapter();
		PApplet.println("KetaiNFCManager instantiated...");
		findParentIntentions();

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

	public void registerAnalyzer(IKetaiAnalyzer _analyzer) {
		// TODO Auto-generated method stub

	}

	public void removeAnalyzer(IKetaiAnalyzer _analyzer) {
		// TODO Auto-generated method stub

	}

	public Collection<? extends String> list() {
		// TODO Auto-generated method stub
		return null;
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
