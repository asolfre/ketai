package edu.uic.ketai.analyzer;

import java.util.ArrayList;

import edu.uic.ketai.IKetaiEventListener;
import edu.uic.ketai.data.DataManager;

public abstract class AbstractKetaiAnalyzer implements IKetaiAnalyzer {

	DataManager datamanager;
	ArrayList<IKetaiEventListener>  eventListeners = new ArrayList<IKetaiEventListener>();
	
	public AbstractKetaiAnalyzer(DataManager _datamanager) {
		datamanager = _datamanager;
	}

	public void registerKetaiEventListener(IKetaiEventListener _listener)
	{
		if(eventListeners.contains(_listener))
			return;
		eventListeners.add(_listener);
	}
	
}
