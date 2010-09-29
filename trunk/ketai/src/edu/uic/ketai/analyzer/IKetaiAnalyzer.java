package edu.uic.ketai.analyzer;

public interface IKetaiAnalyzer {
		
	public String getAnalyzerName();

	public String getAnalysisDescription();

	public void analyzeData(Object dataSet);

	public String getTableCreationString();
	
	public Class<?> getServiceProviderClass();
	
}
