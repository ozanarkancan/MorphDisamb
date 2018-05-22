package data;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class VocabularyList {
	private Map<String, Integer> vocabSet;
	BufferedReader bufferedReader;
	BufferedWriter bufferedWriter;
	
	public VocabularyList()
	{
		vocabSet = new HashMap<String, Integer>();
	}	
	
	public void determineVocabSet(String inputFile)
	{
		File input = new File(inputFile);
		if (!input.exists())
			try {
				throw new FileNotFoundException();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		else
			try {
				bufferedReader = new BufferedReader(new InputStreamReader(
	                      new FileInputStream(input), "UTF-8"));
				File outFile = new File(inputFile + "." + "vocab");
				bufferedWriter = new BufferedWriter(new OutputStreamWriter(
						new FileOutputStream(outFile), "UTF8"));
				vocabSet = new HashMap<String, Integer>();
				String currentLine = "";
				while ((currentLine = bufferedReader.readLine()) != null) {
					String[] analysis = currentLine.split(" ");
					
					String subString = analysis[0].length() > 4 ? analysis[0].substring(analysis[0].length() - 4) : analysis[0];
					for(int index = 1; index < analysis.length; index++)
					{
						String root = analysis[index].startsWith("+") ? "+" : analysis[index].split("\\+")[0];
						if(!vocabSet.containsKey(root))
						{
							vocabSet.put(root, vocabSet.size());
							bufferedWriter.append(root + "\n");
						}
					}
				}
				
				bufferedWriter.flush();
				bufferedReader.close();
				bufferedWriter.close();
			}
			catch (UnsupportedEncodingException e) 
			{
				System.out.println(e.getMessage());
			}
			catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
	}
	
	public int getIndex(String token)
	{
		if(!vocabSet.containsKey(token))
			return -1;
		else
			return vocabSet.get(token);
	}
	
	public int getVocabSize()
	{
		return vocabSet.size();
	}
	
	public void loadVocabSet(String fileName)
	{
		File input = new File(fileName);
		if (!input.exists())
			try {
				throw new FileNotFoundException();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		else
			try {
				bufferedReader = new BufferedReader(new InputStreamReader(
	                      new FileInputStream(input), "UTF-8"));
				vocabSet = new HashMap<String, Integer>();
				String currentLine = "";
				while ((currentLine = bufferedReader.readLine()) != null) {
					if(currentLine.equals(""))
						continue;
					vocabSet.put(currentLine, vocabSet.size());
				}
				bufferedReader.close();
			}
			catch (UnsupportedEncodingException e) 
			{
				System.out.println(e.getMessage());
			}
			catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
	}

}
