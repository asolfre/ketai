package edu.uic.ketai.inputService;

import processing.core.*;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;
import java.util.Vector;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

public class KetaiLocation extends AbstractKetaiInputService implements
		LocationListener {
	private LocationManager locationManager = null;
	private PApplet parent;
	private Method onLocationEventMethod, onLocationEventMethod_adv;
	private String provider;
	private Location location;

	final static String SERVICE_DESCRIPTION = "Android Location.";

	public KetaiLocation(PApplet pParent) {
		parent = pParent;
		locationManager = (LocationManager) parent.getApplicationContext()
				.getSystemService(Context.LOCATION_SERVICE);
		PApplet.println("KetaiLocationManager instantiated...");
		findParentIntentions();
		start();
	}

	public void onLocationChanged(Location _location) {
		location = _location;
		if (onLocationEventMethod != null)
			try {
				onLocationEventMethod
						.invoke(parent,
								new Object[] { location.getLatitude(),
										location.getLongitude(),
										location.getAltitude() });

				broadcastData(location);
				return;
			} catch (Exception e) {
				PApplet.println("Disabling onLocationEvent() because of an error:"
						+ e.getMessage());
				e.printStackTrace();
				onLocationEventMethod = null;
			}

		if (onLocationEventMethod_adv != null)
			try {
				onLocationEventMethod_adv.invoke(parent,
						new Object[] { location });

				broadcastData(location);
				return;
			} catch (Exception e) {
				PApplet.println("Disabling onLocationEvent() because of an error:"
						+ e.getMessage());
				e.printStackTrace();
				onLocationEventMethod_adv = null;
			}
	}

	public Location getLocation() {
		return location;
	}

	public boolean isStarted() {
		return (onLocationEventMethod != null);
	}

	public void start() {
		PApplet.println("KetaiLocationManager: start()...");

		List<String> foo = locationManager.getAllProviders();
		PApplet.println("KetaiLocationManager All Provider(s) list: ");

		for (String s : foo) {
			PApplet.println("\t" + s);
		}

		foo = locationManager.getProviders(true);
		PApplet.println("KetaiLocationManager Enabled Provider(s) list: ");
		for (String s : foo) {
			PApplet.println("\t" + s);
		}

		if (determineProvider())
			location = locationManager.getLastKnownLocation(provider);
		else {
			PApplet.println("Error obtaining location provider.  Check your location settings.");
			location = new Location("default");
			provider = "none";
		}
		if (location != null)
			onLocationChanged(location);
		else
			location = new Location("default");
	}

	public void stop() {
		PApplet.println("KetaiLocationManager: Stop()....");
		locationManager.removeUpdates(this);
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
		PApplet.println("LocationManager onDisabledEnabled: " + arg0);
		determineProvider();
	}

	public void onProviderEnabled(String arg0) {
		PApplet.println("LocationManager onProviderEnabled: " + arg0);
		determineProvider();
	}

	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
		PApplet.println("LocationManager onStatusChanged: " + arg0 + ":" + arg1
				+ ":" + arg2.toString());
		determineProvider();
	}

	public String getProvider() {
		return provider;
	}

	public Collection<? extends String> list() {
		Vector<String> list = new Vector<String>();
		list.add("Location");
		return list;
	}

	private boolean determineProvider() {
		if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
			provider = LocationManager.GPS_PROVIDER;
		else
			provider = locationManager.getBestProvider(new Criteria(), true);
		if (provider == null)
			return false;
		locationManager.requestLocationUpdates(provider, 10000, 1, this);
		return true;
	}

	private void findParentIntentions() {
		try {
			onLocationEventMethod = parent.getClass().getMethod(
					"onLocationEvent",
					new Class[] { double.class, double.class, double.class });
			PApplet.println("Found basic onLocationEvenMethod...");

		} catch (NoSuchMethodException e) {
		}

		try {
			onLocationEventMethod_adv = parent.getClass().getMethod(
					"onLocationEvent", new Class[] { Location.class });
			PApplet.println("Found Advanced onLocationEvenMethod...");

		} catch (NoSuchMethodException e) {
		}

	}

}
