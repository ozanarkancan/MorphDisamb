package disambiguator.strategy.decisiontree;

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
import disambiguator.strategy.nonlexical.AbstractFullIGMorphTagsStrategy;

public class PredictorDecisionTreeWithHybridCascadedHasimStrategy extends AbstractFullIGMorphTagsStrategy{
	
	ProblemSet trainProblems;
	ProblemSet unseenProblems;
	ProblemSet testProblems;
	String trainFile;
	public PredictorDecisionTreeWithHybridCascadedHasimStrategy(ProblemSet trainProblems, TagSet tags, String trainFile)
	{
		super(tags);
		this.trainProblems = trainProblems;
		this.unseenProblems= new ProblemSet();
		this.testProblems = new ProblemSet();
		this.trainFile = trainFile;
	}

	@Override
	public void extractFeatures(String testFile, String extension) {
		//ProblemPredictionEvaluator evaluator = new ProblemPredictionEvaluator(this.trainProblems);
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
				SentenceReader hasimSentenceReader = new SentenceReader(testFile + ".postagged");
				
				BufferedWriter predictionFile = new BufferedWriter(new OutputStreamWriter(
						new FileOutputStream(testFile + "." + extension, true), "UTF8"));
				
				BufferedWriter rootAnalysisAmbWriter = new BufferedWriter(new OutputStreamWriter(
						new FileOutputStream(testFile + "." + "rootAmbiguity", true), "UTF8"));
				
				Sentence sentence;
				Sentence hasimSentence;
				//BufferedWriter bufferedWriter = null;
				while ((sentence = sentenceReader.readSentence(false)) != null) {
					hasimSentence = hasimSentenceReader.readSentence(true);
					
					ArrayList<Word> words = sentence.words;
					ArrayList<Word> hasimWords = hasimSentence.words;
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
								Parse disambiguated = hasimWords.get(i).correct;
								predictionFile.append(currentWord.surfaceForm + " ").append(disambiguated.toString()).append("\n");
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
							Parse disambiguated = hasimWords.get(i).correct;
							predictionFile.append(currentWord.surfaceForm + " ").append(disambiguated.toString()).append("\n");
							predictionFile.flush();
							unseenProblems.addProblem(currentWord.toString(), problem);
							continue;
						}
						
						problem = this.trainProblems.getProblemByIndex(indexInArr);
						int problemIndex = problem.getIndex();
						int label = RandomNumberGenerator.getRandomNumber(currentWord.parses.size());
						
						ArrayList<String> instance = new ArrayList<String>();
						instance.add(Integer.toString(label));
						
						prev = i - 1;
						next = i + 1;
						
						ArrayList<Integer> currFeats = getFullIGFeatures(words.get(i));
						ArrayList<Integer> prevFeats = prev < 0 ? null : getFullIGFeatures(hasimWords.get(prev));
						ArrayList<Integer> nextFeats = next == words.size() ? null : getFullIGFeatures(hasimWords.get(next));
						
						if((currFeats == null || currFeats.size() == 0) && (prevFeats == null || prevFeats.size() == 0) && (nextFeats == null || nextFeats.size() == 0))//Empty feature vector
							instance.add(Integer.toString(tags.getTagsSize() * 2 + 1) + ":1");
						else
						{
							if(nextFeats != null)
								for(int featIndex : nextFeats)
									instance.add(Integer.toString(featIndex) + ":1");
							if(currFeats != null)
								for(int featIndex : currFeats)
									instance.add(Integer.toString(featIndex + 1 + tags.getTagsSize()) + ":1");
							if(prevFeats != null)
								for(int featIndex : prevFeats)
									instance.add(Integer.toString(featIndex + 1 + 2 * tags.getTagsSize()) + ":1");
						}
						
						double[] prediction = this.predictDT(instance, this.trainFile 
								+ ".problem" + Integer.toString(problem.getIndex()) + ".train.featured.model");
						
						//Section for reporting test file label counts
						Problem testProblem = new Problem(problem);
						this.testProblems.addProblem(currentWord.toString(), testProblem);
						//end of section
						
						String predictionLabel = problem.getLabel((int)prediction[0]);
						Parse predicted = currentWord.getFirstParseContainsAnalysis(predictionLabel);
						hasimWords.get(i).correct = predicted;
						predictionFile.append(currentWord.surfaceForm + " ").append(predicted.toString());
						predictionFile.append("\n");
					}
				}
				sentenceReader.close();
				hasimSentenceReader.close();
				unseenProblems.setIndex();
				unseenProblems.printProblem(input + ".unseenProblemSet");
				predictionFile.close();
				rootAnalysisAmbWriter.close();
				this.testProblems.printProblem(testFile + ".testProblemSet");
				deleteFeatureFiles(testFile, extension);
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
