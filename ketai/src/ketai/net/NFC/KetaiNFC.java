package ketai.net.NFC;

import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Vector;

import ketai.net.NFC.record.ParsedNdefRecord;

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
	private Method onNFCEventMethod, onNFCWriteMethod;
	private PendingIntent mPendingIntent;
	private IntentFilter[] mFilters;
	private String[][] mTechLists;
	private NdefMessage mMessage;
	private String pushMessage = "";
	private NfcAdapter mAdapter;
	private String textToWrite = "";

	// private PendingIntent pendingIntent;

	public KetaiNFC(PApplet pParent) {
		parent = pParent;
		PApplet.println("KetaiNFC instantiated...");
		findParentIntentions();
		// handleIntent(parent.getIntent());
		// Create a generic PendingIntent that will be deliver to this activity.
		// The NFC stack
		// will fill in the intent with the details of the discovered tag before
		// delivering to
		// this activity.
		mPendingIntent = PendingIntent.getActivity(parent, 0, new Intent(
				parent, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),
				0);
		// Intent intnt = new Intent(parent, parent.getClass());
		// intnt.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

		// if(pendingIntent == null)
		// pendingIntent = PendingIntent.getActivity(parent, 0, new
		// Intent(parent, parent.getClass()), 0);

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

		mAdapter = NfcAdapter.getDefaultAdapter(parent);
		if (mAdapter == null)
			Log.i("KetaiNFC", "Failed to get NFC adapter...");

		// if (mPendingIntent == null)
		// Log.i("KetaiNFC", "KetaiNFC(): mPendingIntent was null  ");

		if (mFilters == null)
			Log.i("KetaiNFC", "KetaiNFC(): mFilters was null  ");

		if (mTechLists == null)
			Log.i("KetaiNFC", "KetaiNFC(): mTechLists was null  ");

//		 if (parent != null && mAdapter != null && mPendingIntent != null
//		 && mFilters != null && mTechLists != null)
//		 mAdapter.enableForegroundDispatch(parent, mPendingIntent, mFilters,
//		 mTechLists);
		// else
		// Log.i("KetaiNFC",
		// "KetaiNFC(): Something was null and foreground dispatch registration failed  ");

		Intent intent = parent.getIntent();
		handleIntent(intent);
	}

	public void onResume() {
		if (mAdapter != null) {
			PApplet.println("enabling NFC foreground dispatch");
			mAdapter.enableForegroundDispatch(parent, mPendingIntent, mFilters,
					mTechLists);
		} else
			PApplet.println("mAdapter was null in onResume()");
	}

	public void onPause() {
		if (mAdapter != null)
			mAdapter.disableForegroundDispatch(parent);

	}

	public void setPushMessage(String msg) {
		pushMessage = msg;
		// Create an NDEF message with some sample text
		mMessage = new NdefMessage(new NdefRecord[] { newTextRecord(
				pushMessage, Locale.ENGLISH, true) });
		if (mAdapter != null && mMessage != null)
			mAdapter.enableForegroundNdefPush(parent, mMessage);
	}

	public void handleIntent(Intent intent) {
		String action = intent.getAction();
		String thingToReturn = "";
		Tag tag = null;

		if (!NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)
				&& !NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)
				&& !NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)) {
			return;
		}

		PApplet.println("Got nfc intent!: " + intent.toString());

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
			PApplet.println("Tag tech: " + foo + "\n");
		} else
			tag = null;

		PApplet.println("KetaiNFC textToWrite is set to: " + textToWrite);
		if (tag != null && textToWrite != "") {
			PApplet.println("KetaiNFC writing to tag: " + textToWrite);
			writeNFCString(tag);
			return;
		}

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
		String foo = new String(msgs[0].toByteArray());
		PApplet.println("got nfc message:" + foo);

		List<ParsedNdefRecord> records = NdefMessageParser.parse(msgs[0]);

		final int size = records.size();
		for (int i = 0; i < size; i++) {
			ParsedNdefRecord record = records.get(i);
			thingToReturn += "\n" + record.getTag();
		}

		if (onNFCEventMethod != null)
			try {
				onNFCEventMethod.invoke(parent, new Object[] { thingToReturn });

				// return;
			} catch (Exception e) {
				PApplet.println("Disabling onNFCEvent() because of an error:"
						+ e.getMessage());
				e.printStackTrace();
				onNFCEventMethod = null;
			}
	}

	public void start(PendingIntent _pi) {
		mPendingIntent = _pi;
		if (parent != null) {
			if (mAdapter == null)
				mAdapter = NfcAdapter.getDefaultAdapter(parent);

			if (mAdapter != null) {
				if (mPendingIntent == null)
					mPendingIntent = PendingIntent.getActivity(parent, 0,
							new Intent(parent, getClass())
									.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),
							0);

				if (parent != null && mAdapter != null
						&& mPendingIntent != null && mFilters != null
						&& mTechLists != null)
					mAdapter.enableForegroundDispatch(parent, mPendingIntent,
							mFilters, mTechLists);
				else
					Log.i("KetaiNFC",
							"KetaiNFC.start() failed to enable foreground dispatch");
			}

		}
	}

	public Collection<? extends String> list() {
		Vector<String> list = new Vector<String>();
		list.add("NFC");
		return list;
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

	public void foregroundPush(String _data) {
		NfcAdapter mAdapter = NfcAdapter.getDefaultAdapter(parent);

		// Create an NDEF message with some sample text
		NdefMessage mMessage = new NdefMessage(
				new NdefRecord[] { newTextRecord(_data, Locale.ENGLISH, true) });
		if (mAdapter != null && parent != null && mMessage != null)
			mAdapter.setNdefPushMessage(mMessage,  parent);
	}

	public void writeTextToTag(String s) {
		textToWrite = s;
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
							onNFCWriteMethod = null;
							return;
						}
					}

				}
			} else
				return;
		}
		PApplet.println("trying to write tag:" + textToWrite);
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
		try {
			NdefRecord[] records = { record };
			NdefMessage message = new NdefMessage(records);
			if (tag != null) {
				tag.connect();
				tag.format(message);
			} else if (ndefTag != null) {
				ndefTag.connect();
				ndefTag.writeNdefMessage(message);
				ndefTag.close();
				if (onNFCWriteMethod != null) {
					try {
						onNFCWriteMethod.invoke(parent, new Object[] { true,
								textToWrite });
						textToWrite = "";
					} catch (Exception e) {
						PApplet.println("Disabling onNFCWriteEvent() because of an error:"
								+ e.getMessage());
						e.printStackTrace();
						onNFCWriteMethod = null;
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
					PApplet.println("Disabling onNFCWrite() because of an error:"
							+ ex.getMessage());
					ex.printStackTrace();
					onNFCWriteMethod = null;
				}
			}
		}
	}

	private void findParentIntentions() {

		try {
			onNFCEventMethod = parent.getClass().getMethod("onNFCEvent",
					new Class[] { String.class });
			PApplet.println("Found onNFCEventMethod...");
		} catch (NoSuchMethodException e) {
		}
		try {
			onNFCWriteMethod = parent.getClass().getMethod("onNFCWrite",
					new Class[] { boolean.class, String.class });
			PApplet.println("Found onNFCWriteMethod...");
		} catch (NoSuchMethodException e) {
		}
	}

}
