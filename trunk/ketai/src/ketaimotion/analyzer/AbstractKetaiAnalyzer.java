package ketaimotion.analyzer;

import java.util.ArrayList;

import ketaimotion.IKetaiEventListener;
import ketaimotion.data.DataManager;


public abstract class AbstractKetaiAnalyzer implements IKetaiAnalyzer {

	DataManager datamanager;
	ArrayList<IKetaiEventListener> ketaiEventListeners = new ArrayList<IKetaiEventListener>();

	public AbstractKetaiAnalyzer() {
		datamanager = null;
	}

	public AbstractKetaiAnalyzer(DataManager _datamanager) {
		datamanager = _datamanager;
	}

	public void registerKetaiEventListener(IKetaiEventListener _listener) {
		if (ketaiEventListeners.contains(_listener))
			return;
		ketaiEventListeners.add(_listener);
	}

	public void broadcastKetaiEvent(String _event, Object _data) {
		for (IKetaiEventListener l : ketaiEventListeners) {
			l.receiveKetaiEvent(_event, _data);
		}
	}
}
