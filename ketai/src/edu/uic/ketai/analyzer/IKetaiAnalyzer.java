package edu.uic.ketai.analyzer;

import edu.uic.ketai.IKetaiEventListener;

public interface IKetaiAnalyzer {

	public String getAnalyzerName();

	public String getAnalysisDescription();

	public void analyzeData(Object dataSet);

	public String getTableCreationString();

	public Class<?> getServiceProviderClass();

	public void registerKetaiEventListener(
			IKetaiEventListener _ketaiEventListener);

}
