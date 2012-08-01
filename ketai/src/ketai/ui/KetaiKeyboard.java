package ketai.ui;

import android.view.inputmethod.*;
import android.app.Activity;
import android.content.Context;

public class KetaiKeyboard {
	
	static public void toggle(Activity parent) {

		InputMethodManager imm = (InputMethodManager)parent
				.getSystemService(Context.INPUT_METHOD_SERVICE);

	    imm.toggleSoftInput(0, 0);
	}
	
	static public void show(Activity parent)
	{
		InputMethodManager imm = (InputMethodManager)parent
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.showSoftInput(parent.getCurrentFocus(), 0);
	}
	
	static public void hide(Activity parent)
	{
		InputMethodManager imm = (InputMethodManager)parent
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(parent.getCurrentFocus().getWindowToken(), 0);
		
	}
	
	
}
