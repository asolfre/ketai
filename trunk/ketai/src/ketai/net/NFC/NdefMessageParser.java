/*
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package ketai.net.NFC;

import android.nfc.NdefMessage;
import android.nfc.NdefRecord;

import java.util.ArrayList;
import java.util.List;

import ketai.net.NFC.record.ParsedNdefRecord;
import ketai.net.NFC.record.SmartPoster;
import ketai.net.NFC.record.TextRecord;
import ketai.net.NFC.record.UriRecord;


import processing.core.PApplet;


/**
 * Utility class for creating {@link ParsedNdefMessage}s.
 */
public class NdefMessageParser {

	// Utility class
	private NdefMessageParser() {

	}

	/** Parse an NdefMessage */
	public static List<ParsedNdefRecord> parse(NdefMessage message) {
		return getRecords(message.getRecords());
	}

	public static List<ParsedNdefRecord> getRecords(NdefRecord[] records) {
		List<ParsedNdefRecord> elements = new ArrayList<ParsedNdefRecord>();
		for (NdefRecord record : records) {
			if (TextRecord.isText(record)) {
				PApplet.println("NdefMessageParser.getRecords says this record is a text");
				elements.add(TextRecord.parse(record));
			} else if (SmartPoster.isPoster(record)) {
				PApplet.println("NdefMessageParser.getRecords says this record is a smart poster");
				elements.add(SmartPoster.parse(record));
			} else if (UriRecord.isUri(record)) {
				PApplet.println("NdefMessageParser.getRecords says this record is a URI");
				elements.add(UriRecord.parse(record));
			}
		}
		return elements;
	}
}
