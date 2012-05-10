package ketai.ui;

import java.lang.reflect.Method;
import java.util.ArrayList;

import processing.core.PApplet;

import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;

public class KetaiList extends ListView {
	private PApplet parent;
	private ArrayAdapter adapter;
	String name = "KetaiList";
	String selection = "";
	ListView self;
	RelativeLayout layout;
	private Method parentCallback;

	public KetaiList(PApplet _parent, ArrayList<String> data) {
		super(_parent.getApplicationContext());
		parent = _parent;
		adapter = new ArrayAdapter(parent, android.R.layout.simple_list_item_1,
				data);
		init();

	}

	public KetaiList(PApplet _parent, String[] data) {
		super(_parent.getApplicationContext());

		parent = _parent;
		adapter = new ArrayAdapter(parent, android.R.layout.simple_list_item_1,
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

	private void init() {
		setBackgroundColor(Color.BLACK);
		this.setAlpha(1);
		self = this;
		layout = new RelativeLayout(parent);
		this.setAdapter(adapter);
		try {
			parentCallback = parent.getClass().getMethod(
					"onKetaiListSelection", new Class[] { KetaiList.class });
			PApplet.println("Found onKetaiListSelection...");
		} catch (NoSuchMethodException e) {
		}

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
