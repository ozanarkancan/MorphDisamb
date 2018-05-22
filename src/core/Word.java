package core;

import java.util.ArrayList;

public class Word {
	public String surfaceForm;
	public ArrayList<Parse> parses;
	public Parse correct;
	
	public Word()
	{
		surfaceForm = "";
		parses = new ArrayList<Parse>();
	}
	
	public Word(String line, boolean setFirstAsCorrect)
	{
		parses = new ArrayList<Parse>();
		String[] parts = line.split("\\s+");
		surfaceForm = parts[0];
		for(int i = 1; i < parts.length; i++)
		{
			Parse parse = new Parse(parts[i]);
			parses.add(parse);
		}
		
		if(setFirstAsCorrect)
			correct = parses.get(0);
	}
	
	public void setCorrectParse(Parse parse)
	{
		correct = parse;
	}
	
	public Parse getFirstParseContainsAnalysis(String analysis)
	{
		for(Parse parse : parses)
			if(parse.analysis.equals(analysis))
				return parse;
		return null;
	}
	
	public String toString()
	{
		String str = surfaceForm;
		for(Parse parse : parses)
			str += " " + parse.toString();
		return str;
	}

}
