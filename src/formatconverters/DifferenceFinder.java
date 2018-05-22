package formatconverters;

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

public class DifferenceFinder {
	public static void main(String[] args) {
		String fileName = "D:\\bitirme\\tests\\fullig\\train-mst\\METUSABANCI_treebank_vmappedtonewOfltags.txt.morphinput.ofl2012output.sng.nonreplicate.biber2";
		String file2Name = "D:\\bitirme\\tests\\fullig\\train-mst\\METUSABANCI_treebank_vmappedtonewOfltags.txt.morphinput.ofl2012output.sng.nonreplicate.biber";
		DifferenceFinder.findDifferences(fileName, file2Name);
		
	}
	
	public static void findDifferences(String file1, String file2)
	{
		try {
			BufferedReader reader1 = new BufferedReader(new InputStreamReader(
			        new FileInputStream(new File(file1)), "UTF-8"));
			
			BufferedReader reader2 = new BufferedReader(new InputStreamReader(
			        new FileInputStream(new File(file2)), "UTF-8"));
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(file1 + ".diff"), "UTF8"));

			String line = "";
			String line2 = "";
			int lineNo = 1;
			while((line = reader1.readLine()) != null)
			{
				line2 = reader2.readLine();
				if(line.equals(""))
					continue;
				if(line.endsWith("\tU"))
				{
						if(!line2.endsWith("\tU"))
						{
							writer.append("Line: " + Integer.toString(lineNo) + "\n");
							writer.append("l1: " + line + "\n");
							writer.append("l2: " + line2 + "\n");
							writer.append("\n");
							writer.flush();
							System.out.println("Line: " + lineNo);
							System.out.println("l1: " + line);
							System.out.println("l2: " + line2);
							System.out.println();
						}
				}
				lineNo++;
			}
			reader1.close();
			reader2.close();
			writer.close();
		} catch (UnsupportedEncodingException | FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
