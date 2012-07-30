package ketai.ui;

import java.lang.reflect.Method;

import processing.core.PApplet;
import android.view.GestureDetector;
import android.view.GestureDetector.OnDoubleTapListener;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;

public class KetaiGestures implements OnGestureListener, OnDoubleTapListener {

	PApplet parent;
	GestureDetector gestures;
	KetaiGestures me;
	Method onDoubleTapMethod, onScrollMethod, onFlingMethod, onTapMethod,
			onLongPressMethod;

	public KetaiGestures(PApplet _parent) {
		parent = _parent;
		me = this;
		parent.runOnUiThread(new Runnable() {
			public void run() {
				gestures = new GestureDetector(parent, me);
				parent.registerMouseEvent(me);
			}
		});
		findParentIntentions();
	}

	public boolean onDown(MotionEvent arg0) {
		// TODO Auto-generated method stub
		return true;
	}

	public boolean onFling(MotionEvent arg0, MotionEvent arg1, float arg2,
			float arg3) {
		if (onFlingMethod != null) {
			try {
				onFlingMethod.invoke(parent, new Object[] { arg0.getX(),
						arg0.getY(), arg1.getX(), arg1.getY(), arg3 });
			} catch (Exception e) {
			}
		}
		return true;
	}

	public void onLongPress(MotionEvent arg0) {
		if (onLongPressMethod != null) {
			try {
				onLongPressMethod.invoke(parent,
						new Object[] { arg0.getX(), arg0.getY() });
			} catch (Exception e) {
			}
		}

	}

	public boolean onScroll(MotionEvent arg0, MotionEvent arg1, float arg2,
			float arg3) {
		return true;
	}

	public void onShowPress(MotionEvent arg0) {
		// TODO Auto-generated method stub

	}

	public boolean onSingleTapUp(MotionEvent arg0) {
		if (onTapMethod != null) {
			try {
				onTapMethod.invoke(parent,
						new Object[] { arg0.getX(), arg0.getY() });
			} catch (Exception e) {
			}
		}
		return true;
	}

	public boolean surfaceTouchEvent(MotionEvent event) {
		parent.onTouchEvent(event);
		return gestures.onTouchEvent(event);
	}

	public boolean onDoubleTapEvent(MotionEvent arg0) {
		if (onDoubleTapMethod != null) {
			try {
				onDoubleTapMethod.invoke(parent, new Object[] { arg0.getX(),
						arg0.getY() });
			} catch (Exception e) {
			}
		}
		return true;
	}

	public boolean onSingleTapConfirmed(MotionEvent arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean onDoubleTap(MotionEvent arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	private void findParentIntentions() {

		try {
			onTapMethod = parent.getClass().getMethod("onTap",
					new Class[] { float.class, float.class });
		} catch (Exception e) {
		}

		try {
			onDoubleTapMethod = parent.getClass().getMethod("onDoubleTap",
					new Class[] { float.class, float.class });
		} catch (Exception e) {
		}

		try {
			onFlingMethod = parent.getClass().getMethod("onFling",
					new Class[] { float.class, float.class, float.class, float.class, float.class });
		} catch (Exception e) {
		}

		try {
			onScrollMethod = parent.getClass().getMethod("onScroll",
					new Class[] { int.class, int.class });
		} catch (Exception e) {
		}

		try {
			onLongPressMethod = parent.getClass().getMethod("onLongPress",
					new Class[] { float.class, float.class });
		} catch (Exception e) {
		}

	}

}
