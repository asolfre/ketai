package ketai.ui;

import android.view.inputmethod.*;
import android.app.Activity;
import android.content.Context;

public class KetaiSoftKeyboard {
	
	static public void toggle(Activity parent) {

		InputMethodManager imm = (InputMethodManager)parent
				.getSystemService(Context.INPUT_METHOD_SERVICE);

	    imm.toggleSoftInput(0, 0);
	}
}
