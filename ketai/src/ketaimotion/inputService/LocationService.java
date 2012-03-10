package ketaimotion.inputService;

import processing.core.*;

import java.util.Collection;
import java.util.Vector;

import ketai.sensors.KetaiLocation;
import ketaimotion.IDataConsumer;


public class LocationService extends AbstractKetaiInputService implements
		IDataConsumer {
	private PApplet parent;
	private KetaiLocation ketaiLocation;
	
	final static String SERVICE_DESCRIPTION = "Android Location.";

	public LocationService(PApplet pParent) {
		parent = pParent;
		ketaiLocation = new KetaiLocation(parent);
		ketaiLocation.registerDataConsumer(this);
		
		PApplet.println("LocationService instantiated...");
		}


	public boolean isStarted() {
		return ketaiLocation.isStarted();
	}

	public void start() {
		PApplet.println("LocationService: start()...");

		ketaiLocation.start();
	}

	public void stop() {
		PApplet.println("KetaiLocationManager: Stop()....");
		ketaiLocation.stop();
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


	public Collection<? extends String> list() {
		Vector<String> list = new Vector<String>();
		list.add("Location");
		return list;
	}


	public void consumeData(Object _data) {
		this.broadcastData(_data);
	}

}
