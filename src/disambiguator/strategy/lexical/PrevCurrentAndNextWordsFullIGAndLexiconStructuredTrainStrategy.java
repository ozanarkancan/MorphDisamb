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
import java.util.Collections;

import core.Parse;
import core.Sentence;
import core.Word;
import data.Problem;
import data.ProblemSet;
import data.TagSet;
import data.VocabularyList;

public class PrevCurrentAndNextWordsFullIGAndLexiconStructuredTrainStrategy extends AbstractFullIGMorphTagsLexiconStrategy{
	ProblemSet problems;
	
	public PrevCurrentAndNextWordsFullIGAndLexiconStructuredTrainStrategy(ProblemSet problems, TagSet tags, VocabularyList vocabularyList) {
		super(tags, vocabularyList);
		System.out.println("Train Full IG and Lemma  of wi, wi-1 and wi+1 structured form");
		this.problems = problems;
	}

	@Override
	public void extractFeatures(String inputfilename, String extension) {
		File input = new File(inputfilename);
		if (!input.exists())
			try {
				throw new FileNotFoundException();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		else
			try {
				SentenceReader sentenceReader = new SentenceReader(inputfilename);
				BufferedWriter bufferedWriter = null;
				Sentence sentence;
				while ((sentence = sentenceReader.readSentence(true)) != null) {
					
					ArrayList<Word> words = sentence.words;
					int prev;
					int next;
					
					for(int i = 0; i < words.size(); i++)
					{
						Word currentWord = words.get(i);
						if(currentWord.parses.size() == 1)//Unambigous
							continue;
						Problem problem = new Problem(words.get(i));
						
						int indexInArr = problems.contains(problem);
						if(indexInArr == -1)//Problem is not int problem set.
							continue;
						
						problem = problems.getProblemByIndex(indexInArr);
						int problemIndex = problem.getIndex();
						int label = problem.getLabelOfWord(currentWord);
						
						bufferedWriter = new BufferedWriter(new OutputStreamWriter(
								new FileOutputStream(inputfilename + "." + "problem" + Integer.toString(problemIndex) + "." + extension + "." + "featured", true), "UTF8"));
						bufferedWriter.append(Integer.toString(label));
						
						prev = i - 1;
						next = i + 1;
						
						ArrayList<Integer> prevFullIGFeats = prev < 0 ? null : getFullIGFeatures(words.get(prev));
						ArrayList<Integer> currFullIGFeats = getFullIGFeatures(words.get(i));
						ArrayList<Integer> nextFullIGFeats = next == words.size() ? null : getFullIGFeatures(words.get(next));
						
						ArrayList<Integer> prevLexicalFeats = prev < 0 ? null : getLexiconFeatures(words.get(prev));
						ArrayList<Integer> currLexicalFeats = getLexiconFeatures(words.get(i));
						ArrayList<Integer> nextLexicalFeats = next == words.size() ? null : getLexiconFeatures(words.get(next));
						
						if((currFullIGFeats == null || currFullIGFeats.size() == 0) && (prevFullIGFeats == null || prevFullIGFeats.size() == 0) && (nextFullIGFeats == null || nextFullIGFeats.size() == 0)
								&& (currLexicalFeats == null || currLexicalFeats.size() == 0) && (prevLexicalFeats == null || prevLexicalFeats.size() == 0) && (nextLexicalFeats == null || nextLexicalFeats.size() == 0))//Empty feature vector
						{
							bufferedWriter.append(" ").append(Integer.toString(tags.getTagsSize() * 3 + 3 * vocabularyList.getVocabSize() + 1)).append(":1");
							bufferedWriter.append("\n");
							bufferedWriter.flush();
							bufferedWriter.close();
						}
						else
						{
							if(currLexicalFeats != null)
								for(int featIndex : currLexicalFeats)
									bufferedWriter.append(" ").append(Integer.toString(featIndex)).append(":1");
							
							if(currFullIGFeats != null)
								for(int featIndex : currFullIGFeats)
									bufferedWriter.append(" ").append(Integer.toString(featIndex + vocabularyList.getVocabSize())).append(":1");
							
							if(prevFullIGFeats != null)
								for(int featIndex : prevFullIGFeats)
									bufferedWriter.append(" ").append(Integer.toString(featIndex + vocabularyList.getVocabSize() + tags.getTagsSize())).append(":1");
							
							if(prevLexicalFeats != null)
								for(int featIndex : prevLexicalFeats)
									bufferedWriter.append(" ").append(Integer.toString(featIndex + vocabularyList.getVocabSize() + 2 * tags.getTagsSize())).append(":1");
							
							if(nextLexicalFeats != null)
								for(int featIndex : nextLexicalFeats)
									bufferedWriter.append(" ").append(Integer.toString(featIndex + 2 * tags.getTagsSize() + 2 * vocabularyList.getVocabSize())).append(":1");
							
							if(nextFullIGFeats != null)
								for(int featIndex : nextFullIGFeats)
									bufferedWriter.append(" ").append(Integer.toString(featIndex + 2 * tags.getTagsSize() + 3 * vocabularyList.getVocabSize())).append(":1");
							
							//bufferedWriter.append(" ").append(Integer.toString(3 * tags.getTagsSize() + 3 * vocabularyList.getVocabSize())).append(":").append(Double.toString(getAverageCountOfIGs(words.get(i))));
							
							bufferedWriter.append("\n");
							bufferedWriter.flush();
							bufferedWriter.close();
						}
					}
				}
				sentenceReader.close();
				if(bufferedWriter != null)
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

}
