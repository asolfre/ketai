package edu.uic.ketai;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import processing.core.PApplet;

import edu.uic.ketai.analyzer.IKetaiAnalyzer;
import edu.uic.ketai.data.DataManager;
import edu.uic.ketai.inputService.IKetaiInputService;

public class InputManager {
	ArrayList<IKetaiInputService> services;
	ArrayList<IKetaiAnalyzer> analyzers;
	PApplet parent;
	DataManager datamanager;

	public InputManager(PApplet _parent, DataManager _datamanager) {
		services = new ArrayList<IKetaiInputService>();
		analyzers = new ArrayList<IKetaiAnalyzer>();
		datamanager = _datamanager;
		parent = _parent;
	}

	void startServices() {
		Iterator<IKetaiInputService> it = services.iterator();
		while (it.hasNext()) {
			IKetaiInputService item = (IKetaiInputService) it.next();
			item.startService();
		}
	}

	void stopServices() {
		Iterator<IKetaiInputService> it = services.iterator();
		while (it.hasNext()) {
			try {
				IKetaiInputService item = (IKetaiInputService) it.next();
				item.stopService();
			} catch (RuntimeException x) {
				x.printStackTrace();
			}
		}
	}

	void addService(IKetaiInputService _service) {
		for (IKetaiInputService s : services) {
			// only one service of each kind allowed
			if (s.getClass().toString()
					.equalsIgnoreCase(_service.getClass().toString()))
				return;
		}
		services.add(_service);
	}

	void addAnalyzer(IKetaiAnalyzer _analyzer) {
		analyzers.add(_analyzer);
		registerAnalyzerWithService(_analyzer);
	}

	IKetaiAnalyzer getAnalyzer(String name) {
		Iterator<IKetaiAnalyzer> it = analyzers.iterator();
		while (it.hasNext()) {
			IKetaiAnalyzer item = (IKetaiAnalyzer) it.next();
			if (item.toString().equalsIgnoreCase(name))
				return item;
		}
		return null;
	}

	IKetaiInputService getInputService(String name) {
		Iterator<IKetaiInputService> it = services.iterator();
		while (it.hasNext()) {
			IKetaiInputService item = (IKetaiInputService) it.next();
			if (item.toString().equalsIgnoreCase(name))
				return item;
		}
		return null;
	}

	public List<String> list() {
		Vector<String> list = new Vector<String>();
		Iterator<IKetaiInputService> it = services.iterator();
		while (it.hasNext()) {
			IKetaiInputService item = (IKetaiInputService) it.next();
			list.addAll(item.list());
		}
		return list;
	}

	private void registerAnalyzerWithService(IKetaiAnalyzer _analyzer) {
		for (IKetaiInputService service : services) {
			if (service.getClass() == _analyzer.getServiceProviderClass()) {
				service.registerAnalyzer(_analyzer);
				return;
			}
		}
	}

}
