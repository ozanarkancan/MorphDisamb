package disambiguator;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.util.ArrayList;
import java.util.Locale;

import data.ProblemSet;
import data.TagSet;
import data.VocabularyList;
import disambiguator.strategy.FeatureExtractorStrategyFactory;
import disambiguator.strategy.IFeatureExtractor;
import evaluator.ProblemPredictionEvaluator;

public abstract class AbstractDiscriminativeDisambiguator implements MorphologicalDisambiguator{
	
	protected String traininginputfilename;
	protected String testFile;
	protected String outfile;
	protected BufferedReader bufferedReader;
	protected BufferedWriter bufferedWriter;
	protected ArrayList<String> trainOptions;
	protected TagSet tags;
	protected VocabularyList vocabList;
	protected ProblemSet problems;
	protected ProblemSet unSeenProblems;
	protected IFeatureExtractor trainFeatureExtractorStrategy;
	protected IFeatureExtractor testFeatureExtractorStrategy;
	
	public AbstractDiscriminativeDisambiguator(ArrayList<String> trainOptions, String featureParameterFile)
	{
		this.trainOptions = trainOptions;
		Locale.setDefault(new Locale("tr"));
		tags = new TagSet();
		problems = new ProblemSet();
		vocabList = new VocabularyList();
		unSeenProblems = new ProblemSet();
		FeatureExtractorStrategyFactory.getInstance().setFeatureSeletionParameters(featureParameterFile);
	}
	
	private void exit() {		
	}
	
	public ProblemSet getProblems()
	{
		return problems;
	}
	
	public TagSet getTags()
	{
		return tags;
	}
	
	public String getTrainingFile()
	{
		return traininginputfilename;
	}
	
	public void loadTagSet(String tagFile)
	{
		this.tags.loadTagSet(tagFile);
	}
	
	public void loadProblemSet(String problemFile)
	{
		this.problems.loadProblemSet(problemFile);
		//this.problems = ProblemSet.loadBinary(problemFile);
	}
	
	public void setTrainingFileName(String fileName)
	{
		this.traininginputfilename = fileName;
	}
	
	public VocabularyList getVocabList()
	{
		return vocabList;
	}
	
	public void loadVocabList(String vocabFile)
	{
		this.vocabList.loadVocabSet(vocabFile);
	}

}
