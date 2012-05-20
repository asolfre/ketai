package ketai.ui;

import java.lang.reflect.Method;
import java.util.ArrayList;

import processing.core.PApplet;

import android.graphics.Color;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class KetaiList extends ListView {
	private PApplet parent;
	private ArrayAdapter<String> adapter;
	String name = "KetaiList";
	String selection = "";
	ListView self;
	RelativeLayout layout;
	private Method parentCallback;
	String title = "";

	public KetaiList(PApplet _parent, ArrayList<String> data) {
		super(_parent.getApplicationContext());
		parent = _parent;
		adapter = new ArrayAdapter<String>(parent, android.R.layout.simple_list_item_1,
				data);
		init();

	}

	public KetaiList(PApplet _parent, String[] data) {
		super(_parent.getApplicationContext());

		parent = _parent;
		adapter = new ArrayAdapter<String>(parent, android.R.layout.simple_list_item_1,
				data);
		init();
	}

	public KetaiList(PApplet _parent, String _title, String[] data) {
		super(_parent.getApplicationContext());

		parent = _parent;
		title = _title;
		adapter = new ArrayAdapter<String>(parent, android.R.layout.simple_list_item_1,
				data);
		init();
	}

	public KetaiList(PApplet _parent, String _title, ArrayList<String> data) {
		super(_parent.getApplicationContext());
		parent = _parent;
		title = _title;
		adapter = new ArrayAdapter<String>(parent, android.R.layout.simple_list_item_1,
				data);
		init();

	}

	public void refresh() {
		if (adapter == null)
			return;
		parent.runOnUiThread(new Runnable() {
			public void run() {
				adapter.notifyDataSetChanged();
			}
		});
	}

	public String getSelection() {
		return selection;
	}

	public boolean onKeyDown(int KeyCode, KeyEvent event) {
		PApplet.println(" key pressed!");
		return false;
	}

	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			PApplet.println("BACK key released!");
			self.setVisibility(View.GONE);
			((ViewManager) self.getParent()).removeView(self);
			parent.runOnUiThread(new Runnable() {
				public void run() {
					layout.removeAllViews();
					layout.setVisibility(View.GONE);
				}
			});

			return true;
		}
		return false;
	}

	private void init() {
		setBackgroundColor(Color.BLACK);
		setAlpha(1);
		self = this;
		layout = new RelativeLayout(parent);

		if (title != "") {
			TextView tv = new TextView(parent);
			tv.setText(title);
			addHeaderView(tv);
		}

		try {
			parentCallback = parent.getClass().getMethod(
					"onKetaiListSelection", new Class[] { KetaiList.class });
			PApplet.println("Found onKetaiListSelection...");
		} catch (NoSuchMethodException e) {
		}

		setAdapter(adapter);

		setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> p, View view, int position,
					long id) {

				selection = adapter.getItem(position).toString();

				layout.removeAllViewsInLayout();
				try {
					parentCallback.invoke(parent, new Object[] { self });
				} catch (Exception ex) {
				}

				self.setVisibility(View.GONE);
				((ViewManager) self.getParent()).removeView(self);
				parent.runOnUiThread(new Runnable() {
					public void run() {
						layout.removeAllViews();
						layout.setVisibility(View.GONE);
					}
				});
			}
		});

		// add to the main view...

		parent.runOnUiThread(new Runnable() {
			public void run() {
				parent.addContentView(self, new ViewGroup.LayoutParams(
						ViewGroup.LayoutParams.FILL_PARENT,
						ViewGroup.LayoutParams.FILL_PARENT));
			}
		});

	}
}
