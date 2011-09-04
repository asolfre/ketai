/*
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.uic.ketai.inputService.KetaiNFC.record;

import android.net.Uri;
import android.nfc.NdefRecord;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.HashMap;

/**
 * A parsed record containing a Uri.
 */
public class UriRecord implements ParsedNdefRecord {


	public static final String RECORD_TYPE = "UriRecord";

	/**
	 * NFC Forum "URI Record Type Definition"
	 * 
	 * This is a mapping of "URI Identifier Codes" to URI string prefixes, per
	 * section 3.2.2 of the NFC Forum URI Record Type Definition document.
	 */
	@SuppressWarnings("serial")
	private static final HashMap<Byte, String> URI_PREFIX_MAP = new HashMap<Byte, String>() {
		{
			put(new Byte((byte) 0x00), "");
			put(new Byte((byte) 0x01), "http://www.");
			put(new Byte((byte) 0x02), "https://www.");
			put(new Byte((byte) 0x03), "http://");
			put(new Byte((byte) 0x04), "https://");
			put(new Byte((byte) 0x05), "tel:");
			put(new Byte((byte) 0x06), "mailto:");
			put(new Byte((byte) 0x07), "ftp://anonymous:anonymous@");
			put(new Byte((byte) 0x08), "ftp://ftp.");
			put(new Byte((byte) 0x09), "ftps://");
			put(new Byte((byte) 0x0A), "sftp://");
			put(new Byte((byte) 0x0B), "smb://");
			put(new Byte((byte) 0x0C), "nfs://");
			put(new Byte((byte) 0x0D), "ftp://");
			put(new Byte((byte) 0x0E), "dav://");
			put(new Byte((byte) 0x0F), "news:");
			put(new Byte((byte) 0x10), "telnet://");
			put(new Byte((byte) 0x11), "imap:");
			put(new Byte((byte) 0x12), "rtsp://");
			put(new Byte((byte) 0x13), "urn:");
			put(new Byte((byte) 0x14), "pop:");
			put(new Byte((byte) 0x15), "sip:");
			put(new Byte((byte) 0x16), "sips:");
			put(new Byte((byte) 0x17), "tftp:");
			put(new Byte((byte) 0x18), "btspp://");
			put(new Byte((byte) 0x19), "btl2cap://");
			put(new Byte((byte) 0x1A), "btgoep://");
			put(new Byte((byte) 0x1B), "tcpobex://");
			put(new Byte((byte) 0x1C), "irdaobex://");
			put(new Byte((byte) 0x1D), "file://");
			put(new Byte((byte) 0x1E), "urn:epc:id:");
			put(new Byte((byte) 0x1F), "urn:epc:tag:");
			put(new Byte((byte) 0x20), "urn:epc:pat:");
			put(new Byte((byte) 0x21), "urn:epc:raw:");
			put(new Byte((byte) 0x22), "urn:epc:");
			put(new Byte((byte) 0x23), "urn:nfc:");
		}
	};

	private final Uri mUri;

	private UriRecord(Uri uri) {
		if (uri != null)
			this.mUri = uri;
		else
			this.mUri = Uri.EMPTY;
	}

	public Uri getUri() {
		return mUri;
	}

	/**
	 * Convert {@link android.nfc.NdefRecord} into a {@link android.net.Uri}.
	 * This will handle both TNF_WELL_KNOWN / RTD_URI and TNF_ABSOLUTE_URI.
	 * 
	 * @throws IllegalArgumentException
	 *             if the NdefRecord is not a record containing a URI.
	 */
	public static UriRecord parse(NdefRecord record) {
		short tnf = record.getTnf();
		if (tnf == NdefRecord.TNF_WELL_KNOWN) {
			return parseWellKnown(record);
		} else if (tnf == NdefRecord.TNF_ABSOLUTE_URI) {
			return parseAbsolute(record);
		}
		throw new IllegalArgumentException("Unknown TNF " + tnf);
	}

	/** Parse and absolute URI record */
	private static UriRecord parseAbsolute(NdefRecord record) {
		byte[] payload = record.getPayload();
		Uri uri = Uri.parse(new String(payload, Charset.forName("UTF-8")));
		return new UriRecord(uri);
	}

	/** Parse an well known URI record */
	private static UriRecord parseWellKnown(NdefRecord record) {

		if (!Arrays.equals(record.getType(), NdefRecord.RTD_URI))
			return new UriRecord(Uri.EMPTY);

		byte[] payload = record.getPayload();
		/*
		 * payload[0] contains the URI Identifier Code, per the NFC Forum
		 * "URI Record Type Definition" section 3.2.2.
		 * 
		 * payload[1]...payload[payload.length - 1] contains the rest of the
		 * URI.
		 */
		String prefix = URI_PREFIX_MAP.get(payload[0]);
		byte[] fullUri = Arrays.copyOf(prefix.getBytes(),
				prefix.getBytes(Charset.forName("UTF-8")).length
						+ payload.length);
		int k = 0;
		for (int i = prefix.getBytes(Charset.forName("UTF-8")).length; (i < prefix
				.getBytes(Charset.forName("UTF-8")).length + payload.length && (k < payload.length)); i++) {
			fullUri[i] = payload[k];
			k++;
		}

		Uri uri = Uri.parse(new String(fullUri, Charset.forName("UTF-8")));
		return new UriRecord(uri);
	}

	public static boolean isUri(NdefRecord record) {
		try {
			parse(record);
			return true;
		} catch (IllegalArgumentException e) {
			return false;
		}
	}

	public String getTag() {
		return mUri.toString();
	}
}
