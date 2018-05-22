package disambiguator.strategy.lexical;

import io.SentenceReader;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import utils.RandomNumberGenerator;
import core.Parse;
import core.Sentence;
import core.Word;
import data.Problem;
import data.ProblemSet;
import data.TagSet;
import data.VocabularyList;

public class PredictorWithPrevCurrentAndNextWordsFullIGAndLexiconStrategy extends AbstractFullIGMorphTagsLexiconStrategy{
	
	ProblemSet trainProblems;
	ProblemSet unseenProblems;
	ProblemSet testProblems;
	String trainFile;
	public PredictorWithPrevCurrentAndNextWordsFullIGAndLexiconStrategy(ProblemSet trainProblems, TagSet tags, VocabularyList vocabularyList, String trainFile)
	{
		super(tags, vocabularyList);
		this.trainProblems = trainProblems;
		this.unseenProblems= new ProblemSet();
		this.testProblems = new ProblemSet();
		this.trainFile = trainFile;
	}

	@Override
	public void extractFeatures(String testFile, String extension) {
		File input = new File(testFile);
		if (!input.exists())
			try {
				throw new FileNotFoundException();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		else
			try {
				SentenceReader sentenceReader = new SentenceReader(testFile);
				
				BufferedWriter predictionFile = new BufferedWriter(new OutputStreamWriter(
						new FileOutputStream(testFile + "." + extension, true), "UTF8"));
				
				BufferedWriter rootAnalysisAmbWriter = new BufferedWriter(new OutputStreamWriter(
						new FileOutputStream(testFile + "." + "rootAmbiguity", true), "UTF8"));
				
				Sentence sentence;
				//BufferedWriter bufferedWriter = null;
				while ((sentence = sentenceReader.readSentence(false)) != null) {
					
					ArrayList<Word> words = sentence.words;
					int prev;
					int next;
					
					for(int i = 0; i < words.size(); i++)
					{
						Word currentWord = words.get(i);
						Problem problem = new Problem(currentWord);
						int indexInArr = this.trainProblems.contains(problem);
						
						if(problem.getLabels().size() == 1 || currentWord.parses.size() != problem.getLabels().size())
						{
							if(currentWord.parses.size() != problem.getLabels().size())
							{
								predictionFile.append(currentWord.surfaceForm + " ").append(currentWord.surfaceForm + "+NoDisambResult").append("\n");
								predictionFile.flush();
								rootAnalysisAmbWriter.append(currentWord.toString()).append("\n");
								rootAnalysisAmbWriter.flush();
								continue;
							}
							
							predictionFile.append(currentWord.toString()).append("\n");
							predictionFile.flush();
							continue;
						}
						
						if(indexInArr == -1)
						{
							predictionFile.append(currentWord.surfaceForm + " ").append(currentWord.surfaceForm + "+NoDisambResult").append("\n");
							predictionFile.flush();
							unseenProblems.addProblem(currentWord.toString(), problem);
							continue;
						}
						
						problem = this.trainProblems.getProblemByIndex(indexInArr);
						int problemIndex = problem.getIndex();
						int label = RandomNumberGenerator.getRandomNumber(currentWord.parses.size());
						
						/*bufferedWriter = new BufferedWriter(new OutputStreamWriter(
								new FileOutputStream(testFile + "." + "problem" + Integer.toString(problemIndex) + "." + extension + "." + "featured", true), "UTF8"));
						bufferedWriter.append(Integer.toString(label));*/
						
						String instance = "";
						instance += Integer.toString(label);
						
						prev = i - 1;
						next = i + 1;
						
						ArrayList<Integer> currFullIGFeats = getFullIGFeatures(words.get(i));
						ArrayList<Integer> prevFullIGFeats = prev < 0 ? null : getFullIGFeatures(words.get(prev));
						ArrayList<Integer> nextFullIGFeats = next == words.size() ? null : getFullIGFeatures(words.get(next));
						
						ArrayList<Integer> currLexicalFeats = getLexiconFeatures(words.get(i));
						ArrayList<Integer> prevLexicalFeats = prev < 0 ? null : getLexiconFeatures(words.get(prev));
						ArrayList<Integer> nextLexicalFeats = next == words.size() ? null : getLexiconFeatures(words.get(next));
						
						if((currFullIGFeats == null || currFullIGFeats.size() == 0) && (prevFullIGFeats == null || prevFullIGFeats.size() == 0) && (nextFullIGFeats == null || nextFullIGFeats.size() == 0)
								&& (currLexicalFeats == null || currLexicalFeats.size() == 0) && (prevLexicalFeats == null || prevLexicalFeats.size() == 0) && (nextLexicalFeats == null || nextLexicalFeats.size() == 0))//Empty feature vector
							instance += " " + Integer.toString(tags.getTagsSize() * 2 + vocabularyList.getVocabSize() * 2 + 1) + ":1";
						else
						{
							if(prevFullIGFeats != null)
								for(int featIndex : prevFullIGFeats)
									instance += " " + Integer.toString(featIndex) + ":1";
							if(currFullIGFeats != null)
								for(int featIndex : currFullIGFeats)
									instance += " " + Integer.toString(featIndex + 1 + tags.getTagsSize()) + ":1";
							if(nextFullIGFeats != null)
								for(int featIndex : nextFullIGFeats)
									instance += " " + Integer.toString(featIndex + 1 + 2 * tags.getTagsSize()) + ":1";
							if(prevLexicalFeats != null)
								for(int featIndex : prevLexicalFeats)
									instance += " " + Integer.toString(featIndex + 1 + 2 * tags.getTagsSize()) + ":1";
							if(currLexicalFeats != null)
								for(int featIndex : currLexicalFeats)
									instance += " " + Integer.toString(featIndex + 1 + 2 * tags.getTagsSize() + vocabularyList.getVocabSize()) + ":1";
							if(nextLexicalFeats != null)
								for(int featIndex : nextLexicalFeats)
									instance += " " + Integer.toString(featIndex + 1 + 2 * tags.getTagsSize() + 2 * vocabularyList.getVocabSize()) + ":1";
						}
						
						double[] prediction = this.predict(instance, this.trainFile 
								+ ".problem" + Integer.toString(problem.getIndex()) + ".train.featured.model");
						
						//Section for reporting test file label counts
						Problem testProblem = new Problem(problem);
						this.testProblems.addProblem(currentWord.toString(), testProblem);
						//end of section
						
						String predictionLabel = problem.getLabel((int)prediction[0]);
						Parse predicted = currentWord.getFirstParseContainsAnalysis(predictionLabel);
						predictionFile.append(currentWord.surfaceForm + " ").append(predicted.toString());
						predictionFile.append("\n");
						/*bufferedWriter.append("\n");
						bufferedWriter.flush();
						bufferedWriter.close();*/
					}
				}
				sentenceReader.close();
				
				unseenProblems.setIndex();
				unseenProblems.printProblem(input + ".unseenProblemSet");
				predictionFile.close();
				rootAnalysisAmbWriter.close();
				this.testProblems.printProblem(testFile + ".testProblemSet");
				//deleteFeatureFiles(testFile, extension);
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
	
	public void deleteFeatureFiles(String testFile, String extension)
	{
		for(Problem p : this.testProblems)
		{
			File willDeleted = new File(testFile + "." + "problem" + Integer.toString(p.getIndex()) + "." + extension + "." + "featured");
			willDeleted.delete();
		}
	}

}
