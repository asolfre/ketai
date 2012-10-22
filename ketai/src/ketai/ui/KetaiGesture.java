package ketai.ui;

import java.lang.reflect.Method;
import java.util.HashMap;

import processing.core.PApplet;
import processing.core.PVector;
import processing.event.TouchEvent;
import android.view.GestureDetector;
import android.view.GestureDetector.OnDoubleTapListener;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;

public class KetaiGesture implements OnGestureListener, OnDoubleTapListener {

	PApplet parent;
	GestureDetector gestures;
	KetaiGesture me;
	Method onDoubleTapMethod, onScrollMethod, onFlickMethod, onTapMethod,
			onLongPressMethod, onPinchMethod, onRotateMethod;
	HashMap<Integer, PVector> cursors = new HashMap<Integer, PVector>();
	HashMap<Integer, PVector> pcursors = new HashMap<Integer, PVector>();

	public KetaiGesture(PApplet _parent) {
		parent = _parent;
		me = this; // self reference for UI-thread constructor hackiness

		parent.runOnUiThread(new Runnable() {
			public void run() {
				gestures = new GestureDetector(parent, me);
			}
		});
		//this stuff is still not working in b4
//		parent.registerMethod("touchEvent", this);
		
		findParentIntentions();
	}

	public boolean onDown(MotionEvent arg0) {
		return true;
	}

	public boolean onFling(MotionEvent arg0, MotionEvent arg1, float arg2,
			float arg3) {
		if (onFlickMethod != null) {
			try {
				onFlickMethod.invoke(parent,
						new Object[] { arg1.getX(), arg1.getY(), arg0.getX(),
								arg0.getY(), arg3 });
			} catch (Exception e) {
			}
		}
		return true;
	}

	public void onLongPress(MotionEvent arg0) {
		if (onLongPressMethod != null) {
			try {
				onLongPressMethod.invoke(parent, new Object[] { arg0.getX(),
						arg0.getY() });
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

	public void touchEvent(TouchEvent e) {
		PApplet.println("motionEvent called inside kgesture");
		if (e.getNative() instanceof MotionEvent) {
			PApplet.println("KGesture got a MotionEvent!");
			MotionEvent me = (MotionEvent) e.getNative();
			surfaceTouchEvent(me);
		}
	}

	public boolean surfaceTouchEvent(MotionEvent event) {
		// public boolean touchEvent(TouchEvent event){

		int code = event.getAction() & MotionEvent.ACTION_MASK;
		int index = event.getAction() >> MotionEvent.ACTION_POINTER_ID_SHIFT;

		float x = event.getX(index);
		float y = event.getY(index);
		int id = event.getPointerId(index);

		if (code == MotionEvent.ACTION_DOWN
				|| code == MotionEvent.ACTION_POINTER_DOWN) {
			cursors.put(id, new PVector(x, y));
		} else if (code == MotionEvent.ACTION_UP
				|| code == MotionEvent.ACTION_POINTER_UP) {
			if (cursors.containsKey(id))
				cursors.remove(id);
			if (pcursors.containsKey(id))
				pcursors.remove(id);

		} else if (code == MotionEvent.ACTION_MOVE) {
			int numPointers = event.getPointerCount();
			for (int i = 0; i < numPointers; i++) {
				id = event.getPointerId(i);
				x = event.getX(i);
				y = event.getY(i);
				if (cursors.containsKey(id))
					pcursors.put(id, cursors.get(id));
				else
					pcursors.put(id, new PVector(x, y));

				cursors.put(id, new PVector(x, y));
			}
		}
		analyse();
		parent.onTouchEvent(event);
		return gestures.onTouchEvent(event);
	}

	public boolean onSingleTapConfirmed(MotionEvent arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean onDoubleTap(MotionEvent arg0) {
		if (onDoubleTapMethod != null) {
			try {
				onDoubleTapMethod.invoke(parent, new Object[] { arg0.getX(),
						arg0.getY() });
			} catch (Exception e) {
			}
		}
		return true;
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
			onFlickMethod = parent.getClass().getMethod(
					"onFlick",
					new Class[] { float.class, float.class, float.class,
							float.class, float.class });
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

		try {
			onPinchMethod = parent.getClass().getMethod("onPinch",
					new Class[] { float.class, float.class, float.class });
		} catch (Exception e) {
		}

		try {
			onRotateMethod = parent.getClass().getMethod("onRotate",
					new Class[] { float.class, float.class, float.class });
		} catch (Exception e) {
		}
	}

	private synchronized void analyse() {
		if (cursors.size() > 1 && pcursors.size() > 1) {
			PVector c1, c2, p1, p2;
			c1 = cursors.get(0);
			p1 = pcursors.get(0);

			c2 = cursors.get(1);
			p2 = pcursors.get(1);

			// only use cursors 1/2 for our gestures...for now
			if (c1 == null || c2 == null || p1 == null || p2 == null)
				return;

			float midx = (c1.x + c2.x) / 2;
			float midy = (c1.y + c2.y) / 2;

			float dp = PApplet.dist(p1.x, p1.y, p2.x, p2.y);
			float dc = PApplet.dist(c1.x, c1.y, c2.x, c2.y);

			float oldangle = PApplet.atan2(PVector.sub(p1, p2).y,
					PVector.sub(p1, p2).x);
			float newangle = PApplet.atan2(PVector.sub(c1, c2).y,
					PVector.sub(c1, c2).x);

			float delta = (newangle - oldangle);

			if (onPinchMethod != null) {
				try {
					onPinchMethod.invoke(parent, new Object[] { midx, midy,
							dc - dp });
				} catch (Exception e) {
				}
			}

			if (onRotateMethod != null) {
				try {
					onRotateMethod.invoke(parent, new Object[] { midx, midy,
							delta });
				} catch (Exception e) {
				}
			}

		}
	}

	public boolean onDoubleTapEvent(MotionEvent arg0) {
		// TODO Auto-generated method stub
		return false;
	}
}
