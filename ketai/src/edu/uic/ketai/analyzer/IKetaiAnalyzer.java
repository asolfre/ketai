package edu.uic.ketai.analyzer;

public interface IKetaiAnalyzer {

	public String[][] getDataFields();
	
	public String getAnalysisDescription();
	
	public String[] getServiceSubscription();

	public void consumeData(Object[] dataSet);
	
	
}
