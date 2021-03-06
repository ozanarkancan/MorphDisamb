package disambiguator.strategy.nonlexical;

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

public class PrevTwoAndNextWordsFullIGMorphTagsTrainStrategy extends AbstractFullIGMorphTagsStrategy{
	ProblemSet problems;
	
	public PrevTwoAndNextWordsFullIGMorphTagsTrainStrategy(ProblemSet problems, TagSet tags) {
		super(tags);
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
						prev2 = i - 2;
						next = i + 1;
						
						ArrayList<Integer> prevFeats = prev < 0 ? null : getFullIGFeatures(words.get(prev));
						ArrayList<Integer> prev2Feats = prev2 < 0 ? null : getFullIGFeatures(words.get(prev2));
						ArrayList<Integer> nextFeats = next == words.size() ? null : getFullIGFeatures(words.get(next));
						
						if((prevFeats == null || prevFeats.size() == 0) && (prev2Feats == null || prev2Feats.size() == 0) && (nextFeats == null || nextFeats.size() == 0))//Empty feature vector
						{
							bufferedWriter.append(" ").append(Integer.toString(tags.getTagsSize() * 2 + 1)).append(":1");
							bufferedWriter.append("\n");
							bufferedWriter.flush();
							bufferedWriter.close();
						}
						else
						{
							if(nextFeats != null)
								for(int featIndex : nextFeats)
									bufferedWriter.append(" ").append(Integer.toString(featIndex)).append(":1");
							if(prevFeats != null)
								for(int featIndex : prevFeats)
									bufferedWriter.append(" ").append(Integer.toString(featIndex+ 1 + tags.getTagsSize())).append(":1");
							if(prev2Feats != null)
								for(int featIndex : prev2Feats)
									bufferedWriter.append(" ").append(Integer.toString(featIndex + 1 + 2 * tags.getTagsSize())).append(":1");
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
