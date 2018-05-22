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

public class PredictorWithHybridCascadedWithHasimStrategy extends AbstractFullIGMorphTagsLexiconStrategy{
	
	ProblemSet trainProblems;
	ProblemSet unseenProblems;
	ProblemSet testProblems;
	String trainFile;
	
	public PredictorWithHybridCascadedWithHasimStrategy(ProblemSet trainProblems, TagSet tags, VocabularyList vocabularyList, String trainFile)
	{
		super(tags, vocabularyList);
		System.out.println("Predictor Full IG and Lemma  of wi, wi-1 and wi+1, structured form cascaded on hasim result");
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
				SentenceReader hasimSentenceReader = new SentenceReader(testFile + ".postagged");
				
				BufferedWriter predictionFile = new BufferedWriter(new OutputStreamWriter(
						new FileOutputStream(testFile + "." + extension, true), "UTF8"));
				
				BufferedWriter rootAnalysisAmbWriter = new BufferedWriter(new OutputStreamWriter(
						new FileOutputStream(testFile + "." + "rootAmbiguity", true), "UTF8"));
				
				Sentence sentence;
				Sentence hasimSentence;
				//BufferedWriter bufferedWriter = null;
				
				ArrayList<Problem> conditionalProblems = generateConditionProblems();
				
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
								hasimWords.get(i).correct = disambiguated;
								hasimWords.get(i).parses.clear();
								hasimWords.get(i).parses.add(disambiguated);
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
							hasimWords.get(i).correct = disambiguated;
							hasimWords.get(i).parses.clear();
							hasimWords.get(i).parses.add(disambiguated);
							predictionFile.flush();
							unseenProblems.addProblem(currentWord.toString(), problem);
							continue;
						}
						
						problem = this.trainProblems.getProblemByIndex(indexInArr);
						int label = RandomNumberGenerator.getRandomNumber(currentWord.parses.size());
						
						String instance = "";
						instance += Integer.toString(label);
						
						prev = i - 1;
						next = i + 1;
						
						ArrayList<Integer> currFullIGFeats = getFullIGFeatures(words.get(i));
						ArrayList<Integer> prevFullIGFeats = prev < 0 ? null : getFullIGFeatures(hasimWords.get(prev));
						ArrayList<Integer> nextFullIGFeats = next == words.size() ? null : getFullIGFeatures(hasimWords.get(next));
						
						ArrayList<Integer> currLexicalFeats = getLexiconFeatures(words.get(i));
						ArrayList<Integer> prevLexicalFeats = prev < 0 ? null : getLexiconFeatures(hasimWords.get(prev));
						ArrayList<Integer> nextLexicalFeats = next == words.size() ? null : getLexiconFeatures(hasimWords.get(next));
						
						if((currFullIGFeats == null || currFullIGFeats.size() == 0) && (prevFullIGFeats == null || prevFullIGFeats.size() == 0) && (nextFullIGFeats == null || nextFullIGFeats.size() == 0)
								&& (currLexicalFeats == null || currLexicalFeats.size() == 0) && (prevLexicalFeats == null || prevLexicalFeats.size() == 0) && (nextLexicalFeats == null || nextLexicalFeats.size() == 0))//Empty feature vector
							instance += " " + Integer.toString(tags.getTagsSize() * 3 + 3 * vocabularyList.getVocabSize() + 1) + ":1";
						else
						{
							if(currLexicalFeats != null)
								for(int featIndex : currLexicalFeats)
									instance += " " + Integer.toString(featIndex) + ":1";
							if(currFullIGFeats != null)
								for(int featIndex : currFullIGFeats)
									instance += " " + Integer.toString(featIndex + vocabularyList.getVocabSize()) + ":1";
							
							if(prevFullIGFeats != null)
								for(int featIndex : prevFullIGFeats)
									instance += " " + Integer.toString(featIndex + tags.getTagsSize() + vocabularyList.getVocabSize()) + ":1";
							
							if(prevLexicalFeats != null)
								for(int featIndex : prevLexicalFeats)
									instance += " " + Integer.toString(featIndex + 2 * tags.getTagsSize() + vocabularyList.getVocabSize()) + ":1";
							
							if(nextLexicalFeats != null)
								for(int featIndex : nextLexicalFeats)
									instance += " " + Integer.toString(featIndex + 2 * tags.getTagsSize() + 2 * vocabularyList.getVocabSize()) + ":1";
							
							if(nextFullIGFeats != null)
								for(int featIndex : nextFullIGFeats)
									instance += " " + Integer.toString(featIndex + 2 * tags.getTagsSize() + 3 * vocabularyList.getVocabSize()) + ":1";
							
							//instance += " " + Integer.toString(3 * tags.getTagsSize() + 3 * vocabularyList.getVocabSize()) + ":" + Double.toString(getAverageCountOfIGs(words.get(i)));
						}
						
						double[] prediction = this.predict(instance, this.trainFile 
								+ ".problem" + Integer.toString(problem.getIndex()) + ".train.featured.model");
						
						//Section for reporting test file label counts
						Problem testProblem = new Problem(problem);
						this.testProblems.addProblem(currentWord.toString(), testProblem);
						//end of section
						
						String predictionLabel = problem.getLabel((int)prediction[0]);
						Parse predicted = currentWord.getFirstParseContainsAnalysis(predictionLabel);
						/*if(conditionalProblems.contains(problem))
						{
							hasimWords.get(i).correct = predicted;
							hasimWords.get(i).parses.clear();
							hasimWords.get(i).parses.add(predicted);
						}*/
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
	
	private ArrayList<Problem> generateConditionProblems()
	{
		ArrayList<Problem> conditionProblems = new ArrayList<Problem>();
		
		Problem p1 = new Problem();
		p1.addLabel("Noun+A3sg+Pnon+Acc");
		p1.addLabel("Noun+A3sg+P3sg+Nom");
		conditionProblems.add(p1);
		
		Problem p2 = new Problem();
		p2.addLabel("Noun+A3sg+Pnon+Nom");
		p2.addLabel("Adj");
		conditionProblems.add(p2);
		
		Problem p3 = new Problem();
		p3.addLabel("Adverb");
		p3.addLabel("Adj");
		conditionProblems.add(p3);
		
		Problem p4 = new Problem();
		p4.addLabel("Noun+Prop+A3sg+Pnon+Nom");
		p4.addLabel("Noun+A3sg+Pnon+Nom");
		conditionProblems.add(p4);
		
		Problem p5 = new Problem();
		p5.addLabel("Num+Card");
		p5.addLabel("Adverb");
		p5.addLabel("Adj");
		p5.addLabel("Det");
		conditionProblems.add(p5);
		
		Problem p6 = new Problem();
		p6.addLabel("Noun+A3sg+P3pl+Nom");
		p6.addLabel("Noun+A3pl+Pnon+Acc");
		p6.addLabel("Noun+A3pl+P3sg+Nom");
		p6.addLabel("Noun+A3pl+P3pl+Nom");
		conditionProblems.add(p6);
		
		Problem p7 = new Problem();
		p7.addLabel("Adverb");
		p7.addLabel("Conj");
		p7.addLabel("Pron+Ques+A3sg+Pnon+Nom");
		p7.addLabel("Adj");
		conditionProblems.add(p7);
		
		Problem p8 = new Problem();
		p8.addLabel("Postp+PCNom");
		p8.addLabel("Noun+A3sg+Pnon+Dat");
		p8.addLabel("Conj");
		conditionProblems.add(p8);
		
		Problem p9 = new Problem();
		p9.addLabel("Pron+Pers+A3sg+Pnon+Nom");
		p9.addLabel("Det");
		p9.addLabel("Pron+Demons+A3sg+Pnon+Nom");
		p9.addLabel("Noun+A3sg+Pnon+Nom");
		conditionProblems.add(p9);
		
		Problem p10 = new Problem();
		p10.addLabel("Det");
		p10.addLabel("Pron+Demons+A3sg+Pnon+Nom");
		conditionProblems.add(p10);
		
		Problem p11 = new Problem();
		p11.addLabel("Noun+A3sg+P3pl+Nom");
		p11.addLabel("Noun+A3pl+Pnon+Acc");
		p11.addLabel("Noun+A3pl+P3sg+Nom");
		p11.addLabel("Noun+A3pl+P3pl+Nom");
		p11.addLabel("Verb+Pos^DB+Adj+AorPart^DB+Noun+Zero+A3sg+P3sg+Nom");
		conditionProblems.add(p11);
		
		Problem p12 = new Problem();
		p12.addLabel("Noun+A3sg+Pnon+Nom");
		p12.addLabel("Adj");
		p12.addLabel("Verb+Pos+Imp+A2sg");
		conditionProblems.add(p12);
		
		Problem p13 = new Problem();
		p13.addLabel("Noun+Prop+A3sg+Pnon+Nom");
		p13.addLabel("Adj");
		p13.addLabel("Noun+A3sg+Pnon+Nom");
		conditionProblems.add(p13);
		
		Problem p14 = new Problem();
		p14.addLabel("Interj");
		p14.addLabel("Conj");
		conditionProblems.add(p14);
		
		Problem p15 = new Problem();
		p15.addLabel("Noun+Prop+A3sg+Pnon+Nom");
		p15.addLabel("Adj");
		conditionProblems.add(p14);
		
		
		return conditionProblems;
	}

}
