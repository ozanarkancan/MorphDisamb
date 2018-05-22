package evaluator;

import io.SentenceReader;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.HashMap;

import core.Sentence;
import core.Word;
import data.Problem;
import data.ProblemSet;

public class ProblemPredictionEvaluator {
	public int totalMatch = 0;
	public int totalAnalysis = 0;
	public int unseenProblem = 0;
	public int unseenMatch = 0;
	public int unambiguousCorrect = 0;
	public int unambiguous = 0;
	public int xmlTagCount = 0;
	public int rootAmbiguity = 0;
	public int rootAmbiguityMatch = 0;
	public HashMap<Integer, Integer[]> countsForEachProblem;
	ProblemSet problems;
	
	public ProblemPredictionEvaluator()
	{
		countsForEachProblem = new HashMap<Integer, Integer[]>();
	}
	
	public void printResult()
	{
		System.out.println("\nTest Results");
		System.out.println("Number of Unambigious words: " + this.unambiguous);
		System.out.print("Total match in unseen problems: " + this.unseenMatch);
		System.out.println(" / " + this.unseenProblem);
		System.out.print("Total match in ambigous words: " + totalMatch);
		System.out.println(" / " + this.totalAnalysis);
		System.out.println("Total match in root ambiguity with same parse: " + rootAmbiguityMatch + " / " + rootAmbiguity);
		System.out.println("Accuracy in ambgious words: " + ((float)(this.totalMatch + this.unseenMatch + this.rootAmbiguityMatch) * 100 / (float)(this.totalAnalysis + this.unseenProblem + this.rootAmbiguity)));
		System.out.println("Accuracy in all words: " + ((float)(this.totalMatch + this.unambiguousCorrect + this.unseenMatch + this.rootAmbiguityMatch) * 100 / (float)(this.totalAnalysis + this.unambiguous + this.unseenProblem+ this.rootAmbiguity)));
		System.out.println("Number of xml tags: " + this.xmlTagCount);
		System.out.println();
	}
	
	public void printResultForEachProblem(String fileName)
	{
		try {
			Formatter out = new Formatter(fileName);
			for(Integer key : countsForEachProblem.keySet())
			{
				Integer[] values = countsForEachProblem.get(key);
				out.format("%d\t%d\t%d\t%f\n", key, values[0], values[1], (100 * (float)values[0]) / (float)(values[1]));
			}
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
	}
	
	public boolean isMarkup(String s){
		  
		if(s.contains("<DOC")||
			s.contains("<TITLE")||
			s.contains("<S")||
			s.contains("</S")||
			s.contains("</DOC")||
			s.contains("</TITLE")
		)
			return true;
		return false;

	}
	
	public void evaluate(String problemFile, String testFile, String referenceFile, String predictionFile)
	{
		this.problems = new ProblemSet();
		this.problems.loadProblemSet(problemFile);
		SentenceReader testSentenceReader = new SentenceReader(testFile);
		SentenceReader referenceSentenceReader = new SentenceReader(referenceFile);
		SentenceReader predictionSentenceReader = new SentenceReader(predictionFile);
		
		try {
			BufferedWriter missMatchesWriter = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(testFile + "." + "missMatches"), "UTF8"));
			
			Sentence testSentence = null;
			Sentence referenceSentence = null;
			Sentence predictedSentence = null;
				
			while((testSentence = testSentenceReader.readSentence(false)) != null)
			{
				referenceSentence = referenceSentenceReader.readSentence(true);
				predictedSentence = predictionSentenceReader.readSentence(true);
					
				ArrayList<Word> testWords = testSentence.words;
				ArrayList<Word> referenceWords = referenceSentence.words;
				ArrayList<Word> predictedWords = predictedSentence.words;
					
				for(int i = 0; i < testWords.size(); i++)
				{
					Word testWord = testWords.get(i);
					Word referenceWord = referenceWords.get(i);
					Word predictedWord = predictedWords.get(i);
						
					Problem problem = new Problem(testWord);
					int indexInArr = this.problems.contains(problem);
						
					if(problem.getLabels().size() == 1 || testWord.parses.size() != problem.getLabels().size())
					{
						if(testWord.parses.size() != problem.getLabels().size())
						{
							rootAmbiguity++;
								
							if(predictedWord.toString().equals(referenceWord.toString()))
								rootAmbiguityMatch++;
							else
							{
								missMatchesWriter.append("Miss word: " + testWord).append("\n");
								missMatchesWriter.append("Problem: " + "Root Ambiguity").append("\n");
								missMatchesWriter.append("Prediction: " + predictedWord.toString()).append("\n");
								missMatchesWriter.append("Correct: " + referenceWord.toString()).append("\n");
								missMatchesWriter.append("Sentence: " + testSentence.toStringLinedVersionWithParses()).append("\n").append("\n");
								missMatchesWriter.flush();
							}
							continue;
									
						}
						else if(!isMarkup(testWord.toString()))
						{
							unambiguous++;
									
							if(predictedWord.toString().equals(referenceWord.toString()))
								unambiguousCorrect++;
							else
							{
								missMatchesWriter.append("Miss word: " + testWord).append("\n");
								missMatchesWriter.append("Problem: " + "Unambigous").append("\n");
								missMatchesWriter.append("Prediction: " + predictedWord.toString()).append("\n");
								missMatchesWriter.append("Correct: " + referenceWord.toString()).append("\n");
								missMatchesWriter.append("Sentence: " + testSentence.toStringLinedVersionWithParses()).append("\n").append("\n");
								missMatchesWriter.flush();
							}
						}
						else
							xmlTagCount++;
							
						continue;
					}
						
					if(indexInArr == -1)
					{
						unseenProblem++;
						if(predictedWord.toString().equals(referenceWord.toString()))
							unseenMatch++;
						else
						{
							missMatchesWriter.append("Miss word: " + testWord).append("\n");
							missMatchesWriter.append("Problem: " + "Unseen Problem").append("\n");
							missMatchesWriter.append("Prediction: " + predictedWord.toString()).append("\n");
							missMatchesWriter.append("Correct: " + referenceWord.toString()).append("\n");
							missMatchesWriter.append("Sentence: " + testSentence.toStringLinedVersionWithParses()).append("\n").append("\n");
							missMatchesWriter.flush();
						}
						continue;
					}
					
					problem = this.problems.getProblemByIndex(indexInArr);
					int problemIndex = problem.getIndex();
					totalAnalysis++;
					if(predictedWord.toString().equals(referenceWord.toString()))
					{
						totalMatch++;
						if(countsForEachProblem.containsKey(problemIndex))
						{
							Integer[] counts = countsForEachProblem.get(problemIndex);
							counts[0]++;
							counts[1]++;
							countsForEachProblem.put(problemIndex, counts);
						}
						else
							countsForEachProblem.put(problemIndex, new Integer[]{1, 1});
					}
					else
					{
						if(countsForEachProblem.containsKey(problemIndex))
						{
							Integer[] counts = countsForEachProblem.get(problemIndex);
							counts[1]++;
							countsForEachProblem.put(problemIndex, counts);
						}
						else
							countsForEachProblem.put(problemIndex, new Integer[]{0, 1});
						
						missMatchesWriter.append("Miss word: " + testWord).append("\n");
						missMatchesWriter.append("Problem" + Integer.toString(problemIndex)).append("\n");
						missMatchesWriter.append("Prediction: " + predictedWord.toString()).append("\n");
						missMatchesWriter.append("Correct: " + referenceWord.toString()).append("\n");
						missMatchesWriter.append("Sentence:\n" + testSentence.toStringLinedVersionWithParses()).append("\n").append("\n");
						missMatchesWriter.flush();
					}
				}
			}
				
			printResult();
			printResultForEachProblem(testFile + ".report");
				
			testSentenceReader.close();
			predictionSentenceReader.close();
			referenceSentenceReader.close();
			missMatchesWriter.close();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
	}
}
