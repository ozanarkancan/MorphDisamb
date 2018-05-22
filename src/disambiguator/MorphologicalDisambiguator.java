package disambiguator;

import java.util.ArrayList;

public interface MorphologicalDisambiguator {
	public enum InputType {
		OFL, HASIM
	}	
	String analyzeFile(String testinputfilename,String extension) ;
	void train(ArrayList<String> trainingfiles);
}
