package ketai.ui;

import android.content.Context;
import android.os.Vibrator;
import processing.core.PApplet;

public class KetaiVibrate {
	private PApplet parent;
	private Vibrator vibe;
	
	public KetaiVibrate(PApplet _parent)
	{
		parent = _parent;
		vibe = (Vibrator)parent.getSystemService(Context.VIBRATOR_SERVICE);
	}

	public boolean hasVibrator(){
		return vibe.hasVibrator();
	}
	
	public void vibrate()
	{
		//forever! (well...almost)
		long[] pattern = {0, Long.MAX_VALUE};
		vibe.vibrate(pattern, 0);
	}
	
	public void vibrate(long _duration)
	{
		vibe.vibrate(_duration);
	}
	
	public void vibrate(long[] pattern, int repeat)
	{
		vibe.vibrate(pattern, repeat);
	}
	
	public void stop()
	{
		vibe.cancel();
	}
}
