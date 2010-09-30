package edu.uic.ketai.analyzer;

import java.util.ArrayList;

import edu.uic.ketai.IKetaiEventListener;
import edu.uic.ketai.data.DataManager;

public abstract class AbstractKetaiAnalyzer implements IKetaiAnalyzer {

	DataManager datamanager;
	ArrayList<IKetaiEventListener>  ketaiEventListeners = new ArrayList<IKetaiEventListener>();
	
	public AbstractKetaiAnalyzer(DataManager _datamanager) {
		datamanager = _datamanager;
	}

	public void registerKetaiEventListener(IKetaiEventListener _listener)
	{
		if(ketaiEventListeners.contains(_listener))
			return;
		ketaiEventListeners.add(_listener);
	}
	
}
