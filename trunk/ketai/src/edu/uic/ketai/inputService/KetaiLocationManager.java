package edu.uic.ketai.inputService;

import processing.core.*;

import java.lang.reflect.Method;
import java.util.List;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;

public class KetaiLocationManager extends AbstractKetaiInputService implements
		LocationListener {
	private LocationManager locationManager = null;
	private boolean isRegistered = false;
	private PApplet parent;
	private Method onLocationEventMethod;
	private String provider;

	final static String SERVICE_DESCRIPTION = "Android Location.";

	public KetaiLocationManager(PApplet pParent) {
		parent = pParent;
		locationManager = (LocationManager) parent.getApplicationContext()
				.getSystemService(Context.LOCATION_SERVICE);
		Looper.prepare();
		PApplet.println("KetaiLocationManager instantiated...");
		findParentIntentions();
	}

	public void onLocationChanged(Location location) {
		PApplet.println("Location changed in ketaiLocationManager...");
		if(location == null)
			return;
		
		try {
			onLocationEventMethod.invoke(
					parent,
					new Object[] { location.getLatitude(),
							location.getLongitude(), location.getAltitude(),
							location.getBearing(), location.getSpeed() });

			broadcastData(location);
			return;
		} catch (Exception e) {
			PApplet.println("Disabling onLocationEvent() because of an error:"
					+ e.getMessage());
			e.printStackTrace();
			onLocationEventMethod = null;
		}
	}

	public boolean isStarted() {
		return isRegistered;
	}

	public void start() {
		PApplet.println("KetaiLocationManager: start()...");
		Criteria criteria = new Criteria();

		List<String> foo = locationManager.getAllProviders ();
		for (String s : foo) {
			PApplet.println("KetaiLocationManager All Provider list: " + s);
		}

		foo = locationManager.getProviders (true);
		for (String s : foo) {
			PApplet.println("KetaiLocationManager Enabled Provider list: " + s);
		}
		
		provider = locationManager.getBestProvider(criteria, true);
		PApplet.println("Best provider: " + provider);
		Location location = locationManager.getLastKnownLocation(provider);

		if(location != null)
			PApplet.println("Provider " + provider + " has been selected.");
		else
			PApplet.println("Last known location returned was null");

		locationManager.requestLocationUpdates(provider, 0, 0, this);
		onLocationChanged(location);				
	}

	public void stop() {
		PApplet.println("KetaiLocationManager: Stop()....");
		locationManager.removeUpdates(this);
		isRegistered = false;
	}

	private void findParentIntentions() {

		try {
			onLocationEventMethod = parent.getClass().getMethod(
					"onLocationEvent",
					new Class[] { long.class, int.class, float.class,
							float.class, float.class });
			PApplet.println("Found onLocationEvenMethod...");

		} catch (NoSuchMethodException e) {
		}
	}

	public void startService() {
		start();
	}

	public int getStatus() {
		return 0;
	}

	public void stopService() {
		stop();
	}

	public String getServiceDescription() {
		return SERVICE_DESCRIPTION;
	}

	public void onProviderDisabled(String arg0) {
		// TODO Auto-generated method stub

	}

	public void onProviderEnabled(String arg0) {
		// TODO Auto-generated method stub

	}

	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
		// TODO Auto-generated method stub

	}

}
