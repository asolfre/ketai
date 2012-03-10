package ketaimotion.inputService;

import processing.core.*;
import java.util.Collection;
import java.util.Vector;

import ketai.sensors.KetaiSensor;
import ketaimotion.IDataConsumer;

public class SensorService extends AbstractKetaiInputService implements
		IDataConsumer {

	private PApplet parent;
	private KetaiSensor kSensor;

	final static String SERVICE_DESCRIPTION = "Android Sensors.";

	public SensorService(PApplet pParent) {
		parent = pParent;
		kSensor = new KetaiSensor(parent);
		kSensor.registerDataConsumer(this);
	}


	public Collection<? extends String> list() {
		Vector<String> list = new Vector<String>();

		return list;
	}

	public boolean isStarted() {
		return kSensor.isStarted();
	}

	public void start() {
		PApplet.println("SensorService: start()...");
		kSensor.start();
	}

	public void stop() {
		PApplet.println("SensorService: Stop()....");
		kSensor.stop();
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


	public void consumeData(Object _data) {
		broadcastData(_data);
	}
}
