package edu.uic.ketai.inputService;

import edu.uic.ketai.analyzer.IKetaiAnalyzer;

public interface IKetaiInputService {

	public void startService();

	public int getStatus();

	public void stopService();

	public String getServiceDescription();

	public void registerAnalyzer(IKetaiAnalyzer _analyzer);

	public void removeAnalyzer(IKetaiAnalyzer _analyzer);

	final static int STATE_STARTED = 0;
	final static int STATE_STOPPED = 1;
	final static int STATE_STARTED_WITH_ERRORS = 2;
}
