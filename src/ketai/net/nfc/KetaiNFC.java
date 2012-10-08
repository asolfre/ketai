package ketai.net.nfc;

import java.lang.reflect.Method;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Locale;

import ketai.net.nfc.record.ParsedNdefRecord;

import processing.core.PApplet;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentFilter.MalformedMimeTypeException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareUltralight;
import android.nfc.tech.Ndef;
import android.nfc.tech.NfcA;
import android.nfc.tech.NfcF;
import android.os.Parcelable;
import android.util.Log;
import android.nfc.tech.NdefFormatable;

public class KetaiNFC {
	private PApplet parent;
	private Method onNFCEventMethod_String, onNFCWriteMethod,
			onNFCEventMethod_URI, onNFCEventMethod_bArray;

	private IntentFilter[] mFilters;
	private String[][] mTechLists;
	private NfcAdapter mAdapter;
	private NdefMessage messageToWrite, messageToBeam;
	private PendingIntent p;

	public KetaiNFC(PApplet pParent) {
		parent = pParent;
		Log.d("KetaiNFC", "KetaiNFC instantiated...");
		findParentIntentions();

		mAdapter = NfcAdapter.getDefaultAdapter(parent);

		if (mAdapter == null)
			Log.i("KetaiNFC", "Failed to get NFC adapter...");
		
		p = PendingIntent.getActivity(parent, 0,
				new Intent(parent, parent.getClass())
						.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

		// Setup an intent filter for all MIME based dispatches
		// For now we'll just pretend to handle all types, but should
		// be configurable...eventually
		IntentFilter ndef = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
		IntentFilter tech = new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED);
		IntentFilter tag = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
		try {
			ndef.addDataType("*/*");
			tag.addDataType("*/*");
			tech.addDataType("*/*");
		} catch (MalformedMimeTypeException e) {
			throw new RuntimeException("fail", e);
		}
		mFilters = new IntentFilter[] { ndef, tag, tech, };

		// Setup a tech list for all NfcF tags
		mTechLists = new String[][] { new String[] { NfcA.class.getName() },
				new String[] { MifareUltralight.class.getName() },
				new String[] { NfcF.class.getName() },
				new String[] { NdefFormatable.class.getName() } };
	}

	public void onResume() {
		Log.i("KetaiNFC", "resuming...");
		if (mAdapter != null) {
			mAdapter.enableForegroundDispatch(parent, p, mFilters, mTechLists);
			Intent intent = parent.getIntent();
			Log.i("KetaiNFC", "resuming...intent: " + intent.getAction());
			handleIntent(intent);
		} else
			PApplet.println("mAdapter was null in onResume()");
		
		if(NfcAdapter.ACTION_NDEF_DISCOVERED.equals(parent.getIntent().getAction()) ||
				NfcAdapter.ACTION_TAG_DISCOVERED.equals(parent.getIntent().getAction()) ||
				NfcAdapter.ACTION_TECH_DISCOVERED.equals(parent.getIntent().getAction()))
			handleIntent(parent.getIntent());
	}
	
	public void start(PendingIntent p)
	{
		p = PendingIntent.getActivity(parent, 0,
				new Intent(parent, parent.getClass())
						.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
		mAdapter.enableForegroundDispatch(parent, p, mFilters, mTechLists);
		
	}

	public void onPause() {
		Log.i("KetaiNFC", "pausing...");

		if (mAdapter != null)
			mAdapter.disableForegroundDispatch(parent);
	}

	public void handleIntent(Intent intent) {

		if (mAdapter == null)
			return;

		Log.i("KetaiNFC", "processing intent...");
		String action = intent.getAction();
		String thingToReturn = "";
		Tag tag = null;
//		if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())) 
			
		if (!NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)
				&& !NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)
				&& !NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)) {
			return;
		}

		Parcelable[] rawMsgs = intent
				.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
		NdefMessage[] msgs;

		Parcelable pTag = intent.getParcelableExtra("android.nfc.extra.TAG");

		if (pTag != null && pTag.getClass() == Tag.class) {

			tag = (Tag) pTag;
			PApplet.println("Found Tag object: " + tag.toString());
			String foo = "";
			for (String a : tag.getTechList())
				foo += a + "\n";
			PApplet.println("Supported Tag tech: " + foo + "\n");
		} else
			tag = null;

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

		if (tag != null && messageToWrite != null) {
			writeNFCString(tag);
			return;
		}else
		if (msgs == null || msgs.length == 0) {
			return;
		}
		String foo = new String(msgs[0].toByteArray());
		PApplet.println("got nfc message:" + foo);

		List<ParsedNdefRecord> records = NdefMessageParser.parse(msgs[0]);

		final int size = records.size();
		for (int i = 0; i < size; i++) {
			ParsedNdefRecord record = records.get(i);
			thingToReturn += record.getTag();
		}

		if (onNFCEventMethod_String != null)
			try {
				onNFCEventMethod_String.invoke(parent,
						new Object[] { thingToReturn });

				// return;
			} catch (Exception e) {
				PApplet.println("Disabling onNFCEvent() because of an error:"
						+ e.getMessage());
				e.printStackTrace();
			}
	}

	public static NdefRecord newTextRecord(String text, Locale locale,
			boolean encodeInUtf8) {
		byte[] langBytes = locale.getLanguage().getBytes(
				Charset.forName("US-ASCII"));

		Charset utfEncoding = encodeInUtf8 ? Charset.forName("UTF-8") : Charset
				.forName("UTF-16");
		byte[] textBytes = text.getBytes(utfEncoding);

		int utfBit = encodeInUtf8 ? 0 : (1 << 7);
		char status = (char) (utfBit + langBytes.length);

		byte[] data = new byte[1 + langBytes.length + textBytes.length];
		data[0] = (byte) status;
		System.arraycopy(langBytes, 0, data, 1, langBytes.length);
		System.arraycopy(textBytes, 0, data, 1 + langBytes.length,
				textBytes.length);

		return new NdefRecord(NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_TEXT,
				new byte[0], data);
	}

	public void write(URI _url) {
		NdefRecord record = new NdefRecord(NdefRecord.TNF_ABSOLUTE_URI, _url
				.toString().getBytes(Charset.forName("UTF-8")), new byte[0],
				new byte[0]);
		NdefRecord[] records = { record };
		messageToWrite = new NdefMessage(records);
	}

	public void write(String textToWrite) {

		Locale locale = Locale.US;
		final byte[] langBytes = locale.getLanguage().getBytes(
				Charset.forName("UTF-8"));
		final byte[] textBytes = textToWrite.getBytes(Charset.forName("UTF-8"));

		final int utfBit = 0;
		final char status = (char) (utfBit + langBytes.length);
		final byte[] data = new byte[1 + langBytes.length + textBytes.length];

		data[0] = (byte) status;
		System.arraycopy(langBytes, 0, data, 1, langBytes.length);
		System.arraycopy(textBytes, 0, data, 1 + langBytes.length,
				textBytes.length);

		NdefRecord record = new NdefRecord(NdefRecord.TNF_WELL_KNOWN,
				NdefRecord.RTD_TEXT, new byte[0], data);
		NdefRecord[] records = { record };
		messageToWrite = new NdefMessage(records);
	}

	public void beam(String textToWrite) {

		Locale locale = Locale.US;
		final byte[] langBytes = locale.getLanguage().getBytes(
				Charset.forName("UTF-8"));
		final byte[] textBytes = textToWrite.getBytes(Charset.forName("UTF-8"));

		final int utfBit = 0;
		final char status = (char) (utfBit + langBytes.length);
		final byte[] data = new byte[1 + langBytes.length + textBytes.length];

		data[0] = (byte) status;
		System.arraycopy(langBytes, 0, data, 1, langBytes.length);
		System.arraycopy(textBytes, 0, data, 1 + langBytes.length,
				textBytes.length);

		NdefRecord record = new NdefRecord(NdefRecord.TNF_WELL_KNOWN,
				NdefRecord.RTD_TEXT, new byte[0], data);
		NdefRecord[] records = { record };
		messageToBeam = new NdefMessage(records);
	}

	public void write(byte[] _data) {
		PApplet.println("NFC tag byte writing not yet implemented...");
	}

	public void cancelWrite() {
		messageToWrite = null;
	}

	private void writeNFCString(Tag t) {
		NdefFormatable tag = NdefFormatable.get(t);
		Ndef ndefTag = null;

		if (tag == null) {
			PApplet.println("Tag does not support writing (via NdefFormattable). Trying NDEF write...");
			ndefTag = Ndef.get(t);
			if (ndefTag != null) {
				if (ndefTag.isWritable()) {
					PApplet.println("KetaiNFC: Tag is NDEF writable.");
					Log.i("KetaiNFC", "NDEFTag is writable");
				} else {
					PApplet.println("KetaiNFC: Tag is NOT writable");
					Log.i("KetaiNFC", "Tag is NOT writable");
					if (onNFCWriteMethod != null) {
						try {
							onNFCWriteMethod.invoke(parent, new Object[] {
									false, "Tag is NOT writable" });
							return;
						} catch (Exception e) {
							PApplet.println("Disabling onNFCWriteEvent() because of an error:"
									+ e.getMessage());
							e.printStackTrace();
							return;
						}
					}

				}
			} else
				return;
		}

		try {
			if (tag != null) {
				tag.connect();
				tag.format(messageToWrite);
				messageToWrite = null;
			} else if (ndefTag != null) {
				ndefTag.connect();
				ndefTag.writeNdefMessage(messageToWrite);
				ndefTag.close();
				messageToWrite = null;
				if (onNFCWriteMethod != null) {
					try {
						onNFCWriteMethod.invoke(parent,
								new Object[] { true, "" });
					} catch (Exception e) {
						PApplet.println("Failed to write NFC Tag: "
								+ e.getMessage());
					}
				}
			}
		} catch (Exception e) {
			String errorMessage = e.getMessage();
			PApplet.println("Failed to write to tag.  Error: " + errorMessage);
			if (onNFCWriteMethod != null) {
				try {
					onNFCWriteMethod.invoke(parent, new Object[] { false,
							errorMessage });

					// return;
				} catch (Exception ex) {
					PApplet.println("Failed to write nfc tag because of an error:"
							+ ex.getMessage());
					ex.printStackTrace();
				}
			}
		}
	}

	private void findParentIntentions() {
		try {
			onNFCEventMethod_String = parent.getClass().getMethod("onNFCEvent",
					new Class[] { String.class });
			Log.d("KetaiNFC", "Found onNFCEvent(String) callback...");
		} catch (NoSuchMethodException e) {
		}
		try {
			onNFCWriteMethod = parent.getClass().getMethod("onNFCWrite",
					new Class[] { boolean.class, String.class });
			Log.d("KetaiNFC", "Found onNFCWrite callback...");
		} catch (NoSuchMethodException e) {
		}

		try {
			onNFCEventMethod_URI = parent.getClass().getMethod("onNFCEvent",
					new Class[] { URI.class });
			Log.d("KetaiNFC", "Found onNFCEvent(URI) callback...");
		} catch (NoSuchMethodException e) {
		}

		try {
			onNFCEventMethod_bArray = parent.getClass().getMethod("onNFCEvent",
					new Class[] { byte[].class });
			Log.d("KetaiNFC", "Found onNFCEvent(byte[]) callback...");
		} catch (NoSuchMethodException e) {
		}
	}
}
