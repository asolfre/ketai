package ketai.data.inputService;

import java.util.Collection;
import java.util.Vector;


import processing.core.PApplet;

import ketai.camera.KetaiCamera;
import ketai.data.IDataConsumer;


public class CameraService extends AbstractKetaiInputService implements IDataConsumer {

	private PApplet parent;
	private KetaiCamera ketaiCamera;
	

	public CameraService(PApplet pParent, int _width, int _height,
			int _framesPerSecond) {
		parent = pParent;
		ketaiCamera = new KetaiCamera(parent, _width, _height, _framesPerSecond);
		ketaiCamera.registerDataConsumer(this);
		PApplet.println("CameraService completed instantiation... ");
	}

	public void start() {
		ketaiCamera.start();

	}

	public boolean isStarted() {
		return ketaiCamera.isStarted();
	}

	public void stop() {
		ketaiCamera.stop();
	}

	public Collection<? extends String> list() {
		Vector<String> list = new Vector<String>();
		list.add("Camera");
		return list;
	}

	public void startService() {
			start();
	}

	public int getStatus() {
		if (ketaiCamera.isStarted())
			return IKetaiInputService.STATE_STARTED;
		else
			return IKetaiInputService.STATE_STOPPED;
	}

	public void stopService() {
		stop();
	}

	public String getServiceDescription() {
		return "Android camera access.";
	}

	public void consumeData(Object _data) {
		this.broadcastData(_data);
	}

}
