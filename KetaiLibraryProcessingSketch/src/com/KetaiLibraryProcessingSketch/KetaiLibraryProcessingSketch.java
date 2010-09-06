package com.KetaiLibraryProcessingSketch;

import android.hardware.SensorEvent;
import processing.core.PApplet;
import edu.uic.innovationcenter.ketai.sensor.KetaiSensorManager;

public class KetaiLibraryProcessingSketch extends PApplet {
	KetaiSensorManager sm;
	float myX, myY;
	long timeStarted;
	int myCounter;
	long startTime;
	float p0, p1, p2;

	public void setup() {
		startTime = millis();
		setRequestedOrientation(android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		sm = new KetaiSensorManager(this);
		myCounter = 0;
		background(255);
		println("starting...at: " + startTime);
		myCounter = 0;
		p0 = p1 = p2 = 0;
		frameRate(60);

	}

	public void draw() {

	}

	public void mousePressed() {
		println("ketai" + sm.isStarted());
		if(sm.isStarted())
			sm.stop();
		else
			sm.start();
		
		//sm.toggleCollect();
		println("MousePressed...." + screenWidth + "/" + screenHeight);

	}

	public void onAccelerometerSensorEvent(long time, int accuracy, float[] data)
	{
		println("onAccelerometerSensorEvent called...");
		// clean up the current points
		stroke(255);
		line(myCounter, 0, myCounter, height);
		line(myCounter - 1, 0, myCounter - 1, height);

		stroke(255, 0, 0);
		line(myCounter - 1, map(p0, -200, 200, 0, height), myCounter,
				map(data[0], -200, 200, 0, height));
		p0 = map(data[0], -200, 200, 0, height);
		
		stroke(0, 255, 0);
		line(myCounter - 1, map(p1, -200, 200, 0, height), myCounter,
				map(data[1], -200, 200, 0, height));
		p1 = map(data[1], -200, 200, 0, height);

		stroke(0, 0, 255);
		line(myCounter - 1, map(p2, -200, 200, 0, height), myCounter,
				map(data[2], -200, 200, 0, height));

		p2 = map(data[2], -200, 200, 0, height);
		stroke(255);

		myCounter++;
		if (myCounter > width)
			myCounter = 0;
	}
	
//	public void onSensorEvent(SensorEvent e) {
//
//		// clean up the current points
//		stroke(255);
//		line(myCounter, 0, myCounter, height);
//		line(myCounter - 1, 0, myCounter - 1, height);
//
//		stroke(255, 0, 0);
//		line(myCounter - 1, map(p0, -200, 200, 0, height), myCounter,
//				map(e.values[0], -200, 200, 0, height));
//		p0 = map(e.values[0], -200, 200, 0, height);
//
//		stroke(0, 255, 0);
//		line(myCounter - 1, map(p1, -200, 200, 0, height), myCounter,
//				map(e.values[1], -200, 200, 0, height));
//		p1 = map(e.values[1], -200, 200, 0, height);
//
//		stroke(0, 0, 255);
//		line(myCounter - 1, map(p2, -200, 200, 0, height), myCounter,
//				map(e.values[2], -200, 200, 0, height));
//
//		p2 = map(e.values[2], -200, 200, 0, height);
//		stroke(255);
//
//		myCounter++;
//		if (myCounter > width)
//			myCounter = 0;
//	}

}
