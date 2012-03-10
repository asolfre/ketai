package data.analyzer;

import data.IKetaiEventListener;

public interface IKetaiAnalyzer {

	public String getAnalyzerName();

	public String getAnalysisDescription();

	public void analyzeData(Object dataSet);

	public String getTableCreationString();

	public Class<?> getServiceProviderClass();

	public void registerKetaiEventListener(
			IKetaiEventListener _ketaiEventListener);

}
