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

public class PrevTwoWordsLastIGMorphTagsTrainStrategy extends AbstractLastIGMorphTagsStrategy{
	ProblemSet problems;
	
	public PrevTwoWordsLastIGMorphTagsTrainStrategy(ProblemSet problems, TagSet tags)
	{
		super(tags);
		System.out.println("Train Last IG  of wi-1 and wi-2");
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
						
						ArrayList<Integer> prevFeats = prev < 0 ? null : getLastIGFeatures(words.get(prev));
						ArrayList<Integer> prev2Feats = prev2 < 0 ? null : getLastIGFeatures(words.get(prev2));
						
						if((prevFeats == null || prevFeats.size() == 0) && (prev2Feats == null || prev2Feats.size() == 0))//Empty feature vector
						{
							bufferedWriter.append(" ").append(Integer.toString(tags.getTagsSize() * 2 + 1)).append(":1");
							bufferedWriter.append("\n");
							bufferedWriter.flush();
							bufferedWriter.close();
						}
						else
						{
							if(prevFeats != null)
								for(int featIndex : prevFeats)
									bufferedWriter.append(" ").append(Integer.toString(featIndex)).append(":1");
							if(prev2Feats != null)
								for(int featIndex : prev2Feats)
									bufferedWriter.append(" ").append(Integer.toString(featIndex + 1 + tags.getTagsSize())).append(":1");
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
	
	public void deleteFeaturesFile(String inputFileName, String extension)
	{
		for(Problem p : this.problems)
		{
			File willDeleted = new File(inputFileName + "." + "problem" + Integer.toString(p.getIndex()) + "." + extension + "." + "featured");
			willDeleted.delete();
		}
	}
}
