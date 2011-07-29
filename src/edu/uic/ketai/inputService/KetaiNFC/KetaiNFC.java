package edu.uic.ketai.inputService.KetaiNFC;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;
import java.util.Vector;

import processing.core.PApplet;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Parcelable;

import edu.uic.ketai.inputService.AbstractKetaiInputService;
import edu.uic.ketai.inputService.KetaiNFC.record.ParsedNdefRecord;

public class KetaiNFC extends AbstractKetaiInputService {
	private PApplet parent;
	private Method onNFCEventMethod;

	public KetaiNFC(PApplet pParent) {
		parent = pParent;
		PApplet.println("KetaiNFC instantiated...");
		findParentIntentions();
		handleIntent(parent.getIntent());
	}

	public void handleIntent(Intent intent) {
		String action = intent.getAction();
		String thingToReturn = "";

		if (!NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)) {
			return;
		}

		Parcelable[] rawMsgs = intent
				.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
		NdefMessage[] msgs;

		if (rawMsgs != null) {
			msgs = new NdefMessage[rawMsgs.length];
			for (int i = 0; i < rawMsgs.length; i++) {
				msgs[i] = (NdefMessage) rawMsgs[i];
			}
		} else {
			// Unknown tag type
			byte[] empty = new byte[] {};
			NdefRecord record = new NdefRecord(NdefRecord.TNF_UNKNOWN, empty,
					empty, empty);
			NdefMessage msg = new NdefMessage(new NdefRecord[] { record });
			msgs = new NdefMessage[] { msg };
		}

		if (msgs == null || msgs.length == 0) {
			return;
		}
		List<ParsedNdefRecord> records = NdefMessageParser.parse(msgs[0]);
		final int size = records.size();
		for (int i = 0; i < size; i++) {
			ParsedNdefRecord record = records.get(i);
			thingToReturn += "\n" + record.getTag();
		}

		if (onNFCEventMethod != null)
			try {
				onNFCEventMethod.invoke(parent, new Object[] { thingToReturn });

				return;
			} catch (Exception e) {
				PApplet.println("Disabling onLocationEvent() because of an error:"
						+ e.getMessage());
				e.printStackTrace();
				onNFCEventMethod = null;
			}

	}

	public void start() {

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
			onNFCEventMethod = parent.getClass().getMethod("onNFCEvent",
					new Class[] { String.class });
			PApplet.println("Found onNFCEvenMethod...");
		} catch (NoSuchMethodException e) {
		}
	}

}
