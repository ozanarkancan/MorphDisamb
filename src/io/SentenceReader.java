package io;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import core.Sentence;
import core.Word;

public class SentenceReader {
	BufferedReader reader;
	
	public SentenceReader(String fileName)
	{
		try {
			reader = new BufferedReader(new InputStreamReader(
			        new FileInputStream(fileName), "UTF-8"));
		} catch (UnsupportedEncodingException | FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public Sentence readSentence(boolean setFirstAsCorrect)
	{
		Sentence sentence = null;
		
		String line = "";
		try {
			while((line = reader.readLine()) != null)
			{
				if(line.contains("<s>") || line.contains("<S>"))
				{
					sentence = new Sentence();
					sentence.addSentenceHead();
				}
				else if(line.contains("</s>") || line.contains("</S>"))
				{
					sentence.addSentenceTail();
					break;
				}
				else if(line.contains("<DOC")|| line.contains("<TITLE")||
						line.contains("</DOC")||line.contains("</TITLE"))
					continue;
				else if(line.equals(""))
					continue;
				else
					sentence.addWord(line, setFirstAsCorrect);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return sentence;
	}
	
	public void close()
	{
		try {
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
