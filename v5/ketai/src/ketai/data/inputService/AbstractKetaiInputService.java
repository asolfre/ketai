package ketai.data.inputService;

import java.util.ArrayList;

import ketai.data.analyzer.IKetaiAnalyzer;


import processing.core.PApplet;


public abstract class AbstractKetaiInputService implements IKetaiInputService {
	ArrayList<IKetaiAnalyzer> listeners;

	public AbstractKetaiInputService() {
		listeners = new ArrayList<IKetaiAnalyzer>();
	}

	public void registerAnalyzer(IKetaiAnalyzer _analyzer) {
		if (listeners.contains(_analyzer))
			return;
		PApplet.println("InputService Registering this analyzer: "
				+ _analyzer.getClass());
		listeners.add(_analyzer);
	}

	public void removeAnalyzer(IKetaiAnalyzer _analyzer) {
		listeners.remove(_analyzer);
	}

	public void broadcastData(Object data) {
		for (IKetaiAnalyzer analyzer : listeners) {
			analyzer.analyzeData(data);
		}
	}

}
