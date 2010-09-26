package edu.uic.ketai;

import java.util.ArrayList;
import java.util.Iterator;

import edu.uic.ketai.analyzer.IKetaiAnalyzer;
import edu.uic.ketai.inputService.IKetaiInputService;

public class InputManager {
	ArrayList<IKetaiInputService> services;
	ArrayList<IKetaiAnalyzer> analyzers;
	
	public InputManager()
	{
		services = new ArrayList<IKetaiInputService>();
		analyzers = new ArrayList<IKetaiAnalyzer>();
	} 
	
	void addService(IKetaiInputService _service)
	{
		services.add(_service);
	}
	
	void addAnalyzer(IKetaiAnalyzer _analyzer)
	{
		analyzers.add(_analyzer);
	}
	
	IKetaiAnalyzer getAnalyzer(String name)
	{
		Iterator<IKetaiAnalyzer> it = analyzers.iterator();
		while(it.hasNext())
		{
			IKetaiAnalyzer item = (IKetaiAnalyzer) it.next();
			if(item.toString().equalsIgnoreCase(name))
				return item;
		}
		return null;
	}

	IKetaiInputService getInputService(String name)
	{
		Iterator<IKetaiInputService> it = services.iterator();
		while(it.hasNext())
		{
			IKetaiInputService item = (IKetaiInputService) it.next();
			if(item.toString().equalsIgnoreCase(name))
				return item;
		}
		return null;
	}
}
