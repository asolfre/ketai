package ketai.sensors;

import processing.core.*;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Vector;

import ketaimotion.IDataConsumer;

import android.content.Context;
import android.location.Criteria;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

public class KetaiLocation implements LocationListener {
	private LocationManager locationManager = null;
	private PApplet parent;
	private Method onLocationEventMethod1arg, onLocationEventMethod2arg,
			onLocationEventMethod3arg, onLocationEventMethod4arg;
	private String provider;
	private Location location;
	private ArrayList<IDataConsumer> consumers;

	final static String SERVICE_DESCRIPTION = "Android Location.";

	public KetaiLocation(PApplet pParent) {
		parent = pParent;
		locationManager = (LocationManager) parent.getApplicationContext()
				.getSystemService(Context.LOCATION_SERVICE);
		PApplet.println("KetaiLocationManager instantiated:"
				+ locationManager.toString());
		findParentIntentions();
		consumers = new ArrayList<IDataConsumer>();
		start();
	}

	public void onLocationChanged(Location _location) {
		PApplet.println("LocationChanged:" + _location.toString());
		location = _location;

		if (onLocationEventMethod1arg != null)
			try {
				onLocationEventMethod1arg.invoke(parent,
						new Object[] { location });

				return;
			} catch (Exception e) {
				PApplet.println("Disabling onLocationEvent() because of an error:"
						+ e.getMessage());
				e.printStackTrace();
				onLocationEventMethod1arg = null;
			}

		if (onLocationEventMethod2arg != null)
			try {
				onLocationEventMethod2arg.invoke(parent, new Object[] {
						location.getLatitude(), location.getLongitude() });
				return;
			} catch (Exception e) {
				PApplet.println("Disabling onLocationEvent() because of an error:"
						+ e.getMessage());
				e.printStackTrace();
				onLocationEventMethod2arg = null;
			}

		if (onLocationEventMethod3arg != null)
			try {
				onLocationEventMethod3arg.invoke(parent, new Object[] {
						location.getLatitude(), location.getLongitude(),
						location.getAltitude() });
				return;
			} catch (Exception e) {
				PApplet.println("Disabling onLocationEvent() because of an error:"
						+ e.getMessage());
				e.printStackTrace();
				onLocationEventMethod3arg = null;
			}
		if (onLocationEventMethod4arg != null)
			try {
				onLocationEventMethod4arg.invoke(parent, new Object[] {
						location.getLatitude(), location.getLongitude(),
						location.getAltitude(), location.getAccuracy() });
				return;
			} catch (Exception e) {
				PApplet.println("Disabling onLocationEvent() because of an error:"
						+ e.getMessage());
				e.printStackTrace();
				onLocationEventMethod4arg = null;
			}
	}

	public Location getLocation() {
		return location;
	}

	public boolean isStarted() {
		return (onLocationEventMethod4arg != null);
	}

	public void start() {
		PApplet.println("KetaiLocationManager: start()...");

		List<String> foo = locationManager.getAllProviders();
		PApplet.println("KetaiLocationManager All Provider(s) list: ");

		for (String s : foo) {
			PApplet.println("\t" + s);
		}

		if (!determineProvider()) {
			PApplet.println("Error obtaining location provider.  Check your location settings.");
			provider = "none";
		}

		if (location == null) {
			foo = locationManager.getProviders(true);
			PApplet.println("KetaiLocationManager Enabled Provider(s) list: ");
			for (String s : foo) {
				if (location == null) {
					android.location.Location l = locationManager
							.getLastKnownLocation(s);
					if (l != null) {
						location = new Location(l);
						PApplet.println("\t" + s

						+ " - lastLocation for provider:" + location.toString());
					}
				}
			}

			if (location == null)
				location = new Location("default");
		}
		// send last location
		onLocationChanged(location);
	}

	public void stop() {
		PApplet.println("KetaiLocationManager: Stop()....");
		locationManager.removeUpdates(this);
	}

	public void onProviderDisabled(String arg0) {
		PApplet.println("LocationManager onProviderDisabled: " + arg0);
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
		PApplet.println("Requesting location updates from: " + provider);
		locationManager.requestLocationUpdates(provider, 10000, 1, this);
		return true;
	}

	private void findParentIntentions() {
		try {
			onLocationEventMethod1arg = parent.getClass().getMethod(
					"onLocationEvent", new Class[] { Location.class });
			PApplet.println("Found Advanced onLocationEventMethod(Location)...");

		} catch (NoSuchMethodException e) {
		}

		try {
			onLocationEventMethod2arg = parent.getClass().getMethod(
					"onLocationEvent",
					new Class[] { double.class, double.class });
			PApplet.println("Found Advanced onLocationEventMethod(long, lat)...");

		} catch (NoSuchMethodException e) {
		}

		try {
			onLocationEventMethod3arg = parent.getClass().getMethod(
					"onLocationEvent",
					new Class[] { double.class, double.class, double.class });
			PApplet.println("Found basic onLocationEventMethod(long,lat,alt)...");

		} catch (NoSuchMethodException e) {
		}

		try {
			onLocationEventMethod4arg = parent.getClass().getMethod(
					"onLocationEvent",
					new Class[] { double.class, double.class, double.class,
							float.class });
			PApplet.println("Found basic onLocationEventMethod(long,lat,alt, acc)...");

		} catch (NoSuchMethodException e) {
		}

	}

	public void onLocationChanged(android.location.Location arg0) {
		onLocationChanged(new Location(arg0));
	}
	
	public void registerDataConsumer(IDataConsumer _dataConsumer) {
		consumers.add(_dataConsumer);	
	}

	public void removeDataConsumer(IDataConsumer _dataConsumer) {
			consumers.remove(_dataConsumer);
	}


}
