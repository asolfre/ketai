package edu.uic.ketai.analyzer;

import edu.uic.ketai.data.DataManager;

public abstract class AbstractKetaiAnalyzer implements IKetaiAnalyzer {

	DataManager datamanager;

	public AbstractKetaiAnalyzer(DataManager _datamanager) {
		datamanager = _datamanager;
	}

}
