package lifiReceiver;

import analyzer.Analyzer;

public class TestAnalyzer {

	public static void main(String[] args) 
	{
		Analyzer analyzer = new Analyzer();
		analyzer.readFile();
		analyzer.analyze();
	}

}
