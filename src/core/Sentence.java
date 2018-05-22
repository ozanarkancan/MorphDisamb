package core;

import java.util.ArrayList;

public class Sentence {
	public ArrayList<Word> words;
	
	public Sentence()
	{
		words = new ArrayList<Word>();
	}
	
	public void addWord(Word w)
	{
		words.add(w);
	}
	
	public void addWord(String line, boolean setFirstAsCorrect)
	{
		words.add(new Word(line, setFirstAsCorrect));
	}
	
	public void addSentenceHead()
	{
		Parse p = new Parse();
		p.root = "<S>";
		p.analysis = "BSTag";
		ArrayList<String> ig = new ArrayList<String>();
		ig.add("BSTag");
		p.igs.add(ig);
		
		Word w = new Word();
		w.surfaceForm = "<S>";
		w.parses.add(p);
		words.add(w);
		w.correct = p;
	}
	
	public void addSentenceTail()
	{
		Parse p = new Parse();
		p.root = "</S>";
		p.analysis = "ESTag";
		ArrayList<String> ig = new ArrayList<String>();
		ig.add("ESTag");
		p.igs.add(ig);
		
		Word w = new Word();
		w.surfaceForm = "</S>";
		w.parses.add(p);
		words.add(w);
		w.correct = p;
	}
	
	public String toStringLinedVersion()
	{
		String str = "";
		for(Word w : words)
			str += w.surfaceForm + "\n";
		return str.trim();
	}
	
	public String toStringLinedVersionWithParses()
	{
		String str = "";
		for(Word w : words)
			str += w.toString() + "\n";
		return str.trim();
	}

}
