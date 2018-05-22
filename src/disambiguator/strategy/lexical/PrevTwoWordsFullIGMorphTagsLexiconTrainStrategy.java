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

import core.Sentence;
import core.Word;
import data.Problem;
import data.ProblemSet;
import data.TagSet;
import data.VocabularyList;

public class PrevTwoWordsFullIGMorphTagsLexiconTrainStrategy extends AbstractFullIGMorphTagsLexiconStrategy{
	ProblemSet problems;
	
	public PrevTwoWordsFullIGMorphTagsLexiconTrainStrategy(ProblemSet problems, TagSet tags, VocabularyList vocabularyList) {
		super(tags, vocabularyList);
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
					int prev2;
					
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
						prev2 = i - 2;
						
						ArrayList<Integer> prevFullIGFeats = prev < 0 ? null : getFullIGFeatures(words.get(prev));
						ArrayList<Integer> prev2FullIGFeats = prev2 < 0 ? null : getFullIGFeatures(words.get(prev2));
						ArrayList<Integer> prevLexicalFeats = prev < 0 ? null : getLexiconFeatures(words.get(prev));
						ArrayList<Integer> prev2LexicalFeats = prev2 < 0 ? null : getLexiconFeatures(words.get(prev2));
						
						if((prevFullIGFeats == null || prevFullIGFeats.size() == 0) && (prev2FullIGFeats == null || prev2FullIGFeats.size() == 0)
								&& (prevLexicalFeats == null || prevLexicalFeats.size() == 0) && (prev2LexicalFeats == null || prev2LexicalFeats.size() == 0))//Empty feature vector
						{
							bufferedWriter.append(" ").append(Integer.toString(tags.getTagsSize() * 2 + vocabularyList.getVocabSize() * 2 + 1)).append(":1");
							bufferedWriter.append("\n");
							bufferedWriter.flush();
							bufferedWriter.close();
						}
						else
						{
							if(prevFullIGFeats != null)
								for(int featIndex : prevFullIGFeats)
									bufferedWriter.append(" ").append(Integer.toString(featIndex)).append(":1");
							if(prev2FullIGFeats != null)
								for(int featIndex : prev2FullIGFeats)
									bufferedWriter.append(" ").append(Integer.toString(featIndex + 1 + tags.getTagsSize())).append(":1");
							if(prevLexicalFeats != null)
								for(int featIndex : prevLexicalFeats)
									bufferedWriter.append(" ").append(Integer.toString(featIndex + 1 + 2 * tags.getTagsSize())).append(":1");
							if(prev2LexicalFeats != null)
								for(int featIndex : prev2LexicalFeats)
									bufferedWriter.append(" ").append(Integer.toString(featIndex + 1 + 2 * tags.getTagsSize() + vocabularyList.getVocabSize())).append(":1");
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
