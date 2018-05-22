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
import java.util.HashSet;

public class ReplicateAnalysisHandler {

	public static void main(String[] args) {
		String fileName = "D:\\bitirme\\tests\\testCasesOriginal\\train-mst\\METUSABANCI_treebank_v-1corrected2012.txt.morphinput.ofl2012output.sng";
		ReplicateAnalysisHandler.deleteReplicateAnalysis(fileName);
		
	}
	
	public static void deleteReplicateAnalysis(String fileName)
	{
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
			        new FileInputStream(new File(fileName)), "UTF-8"));
			
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(fileName + ".nonreplicate"), "UTF8"));

			String line = "";
			while((line = reader.readLine()) != null)
			{
				if(line.equals(""))
					continue;
				String[] parts = line.split("\\s+");
				HashSet<String> analysis = new HashSet<String>();
				writer.append(parts[0]);
				
				for(int i = 1; i < parts.length; i++)
					if(!analysis.contains(parts[i]))
					{
						writer.append(" " + parts[i]);
						analysis.add(parts[i]);
					}
				writer.append("\n");
				writer.flush();
			}
			reader.close();
			writer.close();
		} catch (UnsupportedEncodingException | FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
