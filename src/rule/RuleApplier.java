package rule;

import io.SentenceReader;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import core.Sentence;
import core.Word;
import data.Problem;

public class RuleApplier {
	
	public static void applyEditingWrongAcc(String testFile, String predictionFile)
	{
		SentenceReader testSentenceReader = new SentenceReader(testFile);
		SentenceReader predictionSentenceReader = new SentenceReader(predictionFile);
		
			try {
				BufferedWriter output = new BufferedWriter(new OutputStreamWriter(
						new FileOutputStream(predictionFile + "Edited"), "UTF8"));
				Sentence testSentence = null;
				Sentence predictedSentence = null;
				
				Problem conditionProblem = new Problem();
				conditionProblem.addLabel("Noun+A3sg+P3sg+Nom");
				conditionProblem.addLabel("Noun+A3sg+Pnon+Acc");
				
				while((testSentence = testSentenceReader.readSentence(false)) != null)
				{
					predictedSentence = predictionSentenceReader.readSentence(true);
						
					ArrayList<Word> testWords = testSentence.words;
					ArrayList<Word> predictedWords = predictedSentence.words;
						
					for(int i = 0; i < testWords.size(); i++)
					{
						Word testWord = testWords.get(i);
						Word predictedWord = predictedWords.get(i);
							
						Problem problem = new Problem(testWord);
						
						if(problem.equals(conditionProblem)  && ((i >= 1
								&& predictedWords.get(i - 1).correct.analysis.endsWith("+Gen")) ||
								(i >= 2
								&& predictedWords.get(i - 2).correct.analysis.endsWith("+Gen"))))
						{
							if(predictedWord.correct.analysis.equals("Noun+A3sg+Pnon+Acc"))
							{
								for(int j = 0; j < testWord.parses.size(); j++)
								{
									if(testWord.parses.get(j).analysis.equals("Noun+A3sg+P3sg+Nom"))
									{
										output.append(predictedWord.surfaceForm).append(" ")
											.append(testWord.parses.get(j).toString()).append("\n");
										output.flush();
										continue;
									}
								}
							}
							else
							{
								output.append(predictedWord.toString()).append("\n");
								output.flush();
							}
						}
						else if(i >= 1 && problem.equals(conditionProblem) 
								&& !predictedWords.get(i - 1).correct.analysis.endsWith("+Gen"))
						{
							if(predictedWord.correct.analysis.equals("Noun+A3sg+P3sg+Nom"))
							{
								for(int j = 0; j < testWord.parses.size(); j++)
								{
									if(testWord.parses.get(j).analysis.equals("Noun+A3sg+Pnon+Acc"))
									{
										output.append(predictedWord.surfaceForm).append(" ")
											.append(testWord.parses.get(j).toString()).append("\n");
										output.flush();
										continue;
									}
								}
							}
							else
							{
								output.append(predictedWord.toString()).append("\n");
								output.flush();
							}
						}
						else
						{
							output.append(predictedWord.toString()).append("\n");
							output.flush();
						}
					}
				}
				
				testSentenceReader.close();
				predictionSentenceReader.close();
				output.close();
			} catch (UnsupportedEncodingException | FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
	}

}
