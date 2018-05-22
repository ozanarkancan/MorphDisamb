package data;

import io.SentenceReader;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.TreeSet;

import core.Parse;
import core.Sentence;
import core.Word;

public class ProblemSet implements Iterable<Problem>, Serializable {
	transient BufferedReader bufferedReader;
	transient BufferedWriter bufferedWriter;
	
	ArrayList<Problem> problemSet;
	HashMap<String, Problem> similarityMatrix;
	
	public ProblemSet()
	{
		problemSet = new ArrayList<Problem>();
	}
	
	public void determineProblemSet(String inputfilename)
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
				
				problemSet = new ArrayList<Problem>();
				String currentLine = "";
				while ((currentLine = bufferedReader.readLine()) != null) {
					Problem problem = getProblem(currentLine);
					if(problem.getLabels().size() < 2)
						continue;
					int indexInArr = problemSet.indexOf(problem);
					int label;
					
					if(indexInArr != -1)//increment count
					{
						Problem p = problemSet.get(indexInArr);
						p.incrementCount();
						label = getLabel(currentLine, problem.getLabels());
						p.incrementLabelCount(label);
					}
					else
					{
						problem.incrementCount();
						problemSet.add(problem);
						label = getLabel(currentLine, problem.getLabels());
						problem.incrementLabelCount(label);
					}
				}
				bufferedReader.close();
				
				setIndex();
				printProblem(inputfilename + ".problemSet");
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
	
	public void determineProblemSetWithSimilarityMetric(String inputfilename)
	{
		File input = new File(inputfilename);
		if (!input.exists())
			try {
				throw new FileNotFoundException();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		else {
			similarityMatrix = new HashMap<String, Problem>();
			SentenceReader sentenceReader = new SentenceReader(inputfilename);
			
			problemSet = new ArrayList<Problem>();
			Sentence sentence;
			
			while ((sentence = sentenceReader.readSentence(true)) != null) {
				ArrayList<Word> words = sentence.words;
				for(Word w : words)
				{
					Problem problem = getProblem(w.toString());
					if(problem.getLabels().size() < 2)
						continue;
					int indexInArr = problemSet.indexOf(problem);
					int label;
					
					if(indexInArr != -1)//increment count
					{
						Problem p = problemSet.get(indexInArr);
						p.incrementCount();
						label = getLabel(w.toString(), problem.getLabels());
						p.incrementLabelCount(label);
					}
					else
					{
						String key = "";
						for(String l : problem.getLabels())
							key += l;
						Problem similarProblem;
						if(similarityMatrix.containsKey(key))
						{
							similarProblem = similarityMatrix.get(key);
							similarProblem.incrementCount();
							label = getLabel(w.toString(), problem.getLabels());
							similarProblem.incrementLabelCount(label);
						}
						else
						{
							similarProblem = getSimilarProblem(problem);
							if(similarProblem != null)
							{
								for(Parse parse : w.parses)
									similarProblem.addLabel(parse.analysis);
								
								similarProblem.incrementCount();
								label = getLabel(w.toString(), problem.getLabels());
								similarProblem.incrementLabelCount(label);
								key = "";
								for(String l : problem.getLabels())
									key += l;
								similarityMatrix.put(key, similarProblem);
							}
							else
							{
								problem.incrementCount();
								problemSet.add(problem);
								label = getLabel(w.toString(), problem.getLabels());
								problem.incrementLabelCount(label);
								key = "";
								for(String l : problem.getLabels())
									key += l;
								similarityMatrix.put(key, problem);
							}
						}
						
					}
				}
			}
			sentenceReader.close();
			
			setIndex();
			printProblem(inputfilename + ".problemSet");
		}
	}
	
	public void setIndex()
	{
		Collections.sort(problemSet);
		int index = 1;
		for(Problem p : problemSet)
		{
			p.setIndex(index);
			index++;
		}
	}
	
	public int contains(Problem p)
	{
		return problemSet.indexOf(p);
	}
	
	public Problem getProblem(String line)
	{
		Problem problem = new Problem();
		String[] analysis = line.split("\\s+");
		for(int i = 1; i < analysis.length; i++)
		{
			String parse = getParse(analysis[i]);
			problem.addLabel(parse);
		}
		return problem;
	}
	
	
	public Problem getProblemByIndex(int index)
	{
		return problemSet.get(index);
	}
	
	public String getParse(String analysis)
	{
		String parse = "";
		if(analysis.startsWith("+"))
		{
			parse = "Punc";
			return parse;
		}
		String[] parts = analysis.split("\\+");
		for(int i = 1; i < parts.length - 1; i++)
			parse += parts[i] + "+";
		parse += parts[parts.length - 1];
		return parse;
	}

	@Override
	public Iterator<Problem> iterator() {
		return problemSet.iterator();
	}
	
	public int getNumberOfProblems()
	{
		return problemSet.size();
	}
	
	public Problem getSimilarProblem(Problem rhs)
	{
		for(int i = 0; i < problemSet.size(); i++)
		{
			Problem candidateProblem = problemSet.get(i);
			HashSet<String> currentProblemVector = (HashSet<String>)rhs.getMorphFeatureVectorForm().clone();
			HashSet<String> candidateProblemVector = (HashSet<String>)candidateProblem.getMorphFeatureVectorForm().clone();
			
			currentProblemVector.retainAll(candidateProblemVector);
			
			double candidateProblemSimilarityRatio = (double)currentProblemVector.size() / (double)candidateProblemVector.size();
			double currentProblemSimilarityRatio = (double)currentProblemVector.size() / (double)rhs.getMorphFeatureVectorForm().size();
			
			if(candidateProblemSimilarityRatio >= 0.75 && currentProblemSimilarityRatio >= 0.75)
				return candidateProblem;
		}
		return null;
	}
	
	public Problem getSimilarProblemForTrainingAndTesting(Problem rhs)
	{
		String key = "";
		for(String l : rhs.getLabels())
			key += l;
		if(similarityMatrix.containsKey(key))
			return similarityMatrix.get(key);
		/*else
		{
			for(int i = 0; i < problemSet.size(); i++)
			{
				Problem candidateProblem = problemSet.get(i);
				HashSet<String> currentProblemVector = (HashSet<String>)rhs.getMorphFeatureVectorForm().clone();
				HashSet<String> candidateProblemVector = (HashSet<String>)candidateProblem.getMorphFeatureVectorForm().clone();
			
				currentProblemVector.retainAll(candidateProblemVector);
			
				double candidateProblemSimilarityRatio = (double)currentProblemVector.size() / (double)candidateProblemVector.size();
				double currentProblemSimilarityRatio = (double)currentProblemVector.size() / (double)rhs.getMorphFeatureVectorForm().size();
			
				if(candidateProblemSimilarityRatio >= 0.8 && currentProblemSimilarityRatio >= 0.8)
					return candidateProblem;
			}
		}*/
		return null;
	}
	
	
	public int getLabel(String line, TreeSet<String> labels)
	{
		int result = -1;
		String[] analysis = line.split("\\s+");
		String parse = getParse(analysis[1]);
		
		int classIndex = 0;
		for(String classParse : labels)
		{
			if(parse.equals(classParse))
			{
				result = classIndex;
				break;
			}
			else
				classIndex++;
		}
			
		return result;
	}
	
	public void addProblem(String currentLine, Problem problem)
	{
		int indexInArr = problemSet.indexOf(problem);
		int label;
		
		if(indexInArr != -1)//increment count
			problem = problemSet.get(indexInArr);
		else
			problemSet.add(problem);
		problem.incrementCount();
		label = getLabel(currentLine, problem.getLabels());
		problem.incrementLabelCount(label);
	}
	
	public void printProblem(String fileName)
	{
		File outFile = new File(fileName);
		try {
			bufferedWriter = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(outFile), "UTF8"));

			for(Problem p : problemSet)
			{
				int i = 0;
				for(String parse : p.getLabels())
				{
					bufferedWriter.append(parse).append("\t").append(Integer.toString(p.getLabelCount(i)))
						.append("\n");
					i++;
				}
				bufferedWriter.append("Count:\t").append(Integer.toString(p.getCount())).append("\n");
				bufferedWriter.append("Problem\t").append(Integer.toString(p.getIndex())).append("\n\n");
				bufferedWriter.flush();
			}
			
			bufferedWriter.close();
		} catch (UnsupportedEncodingException | FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void saveBinary(String fileName)
	{
		FileOutputStream fos;
		try {
			fos = new FileOutputStream(fileName);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(this);
			oos.flush();
			oos.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void loadProblemSet(String fileName)
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
				String currentLine = "";
				Problem p = new Problem();
				int labelIndex = 0;
				while ((currentLine = bufferedReader.readLine()) != null) {
					if(currentLine.equals(""))
					{
						this.problemSet.add(p);
						p = new Problem();
						labelIndex = 0;
						continue;
					}
					String parts[] = currentLine.split("\t");
					if(parts[0].equals("Count:"))
						p.setCount(Integer.parseInt(parts[1]));
					else if(parts[0].equals("Problem"))
						p.setIndex(Integer.parseInt(parts[1]));
					else
					{
						p.addLabel(parts[0]);
						p.setLabelCounts(labelIndex, Integer.parseInt(parts[1]));
						labelIndex++;
					}
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
	
	public static ProblemSet loadBinary(String fileName)
	{
		FileInputStream fis;
		try {
			fis = new FileInputStream(fileName);
			ObjectInputStream ois = new ObjectInputStream(fis);
			ProblemSet saved = (ProblemSet) ois.readObject();
			return saved;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public int getProblemIndex(Problem p)
	{
		int indexInArr = this.problemSet.indexOf(p);
		if(indexInArr != -1)
		{
			Problem prob = this.problemSet.get(indexInArr);
			return prob.getIndex();
		}
		else
			return -1;
	}
	
	public void removeProblem(Problem problem)
	{
		this.problemSet.remove(problem);
	}

}
