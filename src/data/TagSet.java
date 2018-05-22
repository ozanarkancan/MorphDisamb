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

public class TagSet {
	private Map<String, Integer> tagSet;
	BufferedReader bufferedReader;
	BufferedWriter bufferedWriter;
	
	public TagSet(){
		tagSet = new HashMap<String, Integer>();
	}
	
	public void determineTagList(String inputfilename)
	{
		File input = new File(inputfilename);
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
				File outFile = new File(inputfilename + "." + "tagInfo");
				bufferedWriter = new BufferedWriter(new OutputStreamWriter(
						new FileOutputStream(outFile), "UTF8"));
				tagSet = new HashMap<String, Integer>();
				String currentLine = "";
				while ((currentLine = bufferedReader.readLine()) != null) {
					String[] analysis = currentLine.split(" ");
					for(int index = 1; index < analysis.length; index++)
					{
						String[] derivationGroups = analysis[index].split("\\^DB");
						for(int db = 0; db < derivationGroups.length; db++)
						{
							String[] parts = derivationGroups[db].split("\\+");
							for(int i = 1; i < parts.length; i++)
							{
								if(!tagSet.keySet().contains(parts[i]) && !parts[i].equals(""))
								{
									bufferedWriter.append(parts[i]).append("\n");
									tagSet.put(parts[i], tagSet.size());
								}
								bufferedWriter.flush();
							}
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
	
	public void loadTagSet(String fileName)
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
				tagSet = new HashMap<String, Integer>();
				String currentLine = "";
				while ((currentLine = bufferedReader.readLine()) != null) {
					if(currentLine.startsWith("Number"))
						continue;
					tagSet.put(currentLine, tagSet.size());
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
	
	public int getIndex(String token)
	{
		if(!tagSet.containsKey(token))
			return -1;
		else
			return tagSet.get(token);
	}
	
	public int getTagsSize()
	{
		return tagSet.size();
	}
}
