package disambiguator.strategy;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.HashSet;

import disambiguator.AbstractDiscriminativeDisambiguator;
import disambiguator.SVMMorphologicalDisambiguatorNonLexical;
import disambiguator.strategy.decisiontree.PredictorDecisionTreeWithHasimCascadedPrevTwoWordsStrategy;
import disambiguator.strategy.decisiontree.PredictorDecisionTreeWithHybridCascadedHasimStrategy;
import disambiguator.strategy.decisiontree.PredictorDecisionTreeWithPrevCurrentNextWordsFullIGStrategy;
import disambiguator.strategy.decisiontree.PredictorDecisionTreeWithPrevTwoWordsIterativeStrategy;
import disambiguator.strategy.decisiontree.PredictorDecisionTreeWithPrevTwoWordsStrategy;
import disambiguator.strategy.lexical.PredictorWithHybridCascadedWithHasimStrategy;
import disambiguator.strategy.lexical.PredictorWithPrevCurrentAndNextWordsFullIGAndLexiconMergedProblemsStrategy;
import disambiguator.strategy.lexical.PredictorWithPrevCurrentAndNextWordsFullIGAndLexiconStrategy;
import disambiguator.strategy.lexical.PredictorWithPrevCurrentAndNextWordsFullIGAndLexiconStructuredStrategy;
import disambiguator.strategy.lexical.PredictorWithPrevCurrentAndNextWordsFullIGCurrentLexiconStrategy;
import disambiguator.strategy.lexical.PredictorWithPrevCurrentAndNextWordsFullIGPrevAndCurrentLexiconStrategy;
import disambiguator.strategy.lexical.PredictorWithPrevCurrentAndNextWordsFullIGPrevLexiconStrategy;
import disambiguator.strategy.lexical.PredictorWithPrevCurrentNextNext2WordsFullIGAndLexiconStructuredStrategy;
import disambiguator.strategy.lexical.PredictorWithPrevLexiconPrevCurrentAndNextWordsFullIGStrategy;
import disambiguator.strategy.lexical.PredictorWithPrevTwoAndCurrentWordsFullIGLexiconStrategy;
import disambiguator.strategy.lexical.PredictorWithPrevTwoWordsFullIGLexiconStrategy;
import disambiguator.strategy.lexical.PredictorWithPrevTwoWordsLastIGLexiconStrategy;
import disambiguator.strategy.lexical.PrevCurrentAndNextWordsFullIGAndFullLexiconTrainStrategy;
import disambiguator.strategy.lexical.PrevCurrentAndNextWordsFullIGAndLexiconMergedProblemsTrainStrategy;
import disambiguator.strategy.lexical.PrevCurrentAndNextWordsFullIGAndLexiconStructuredTrainStrategy;
import disambiguator.strategy.lexical.PrevCurrentAndNextWordsFullIGCurrentLexiconTrainStrategy;
import disambiguator.strategy.lexical.PrevCurrentAndNextWordsFullIGPrevAndCurrentLexiconTrainStrategy;
import disambiguator.strategy.lexical.PrevCurrentAndNextWordsFullIGPrevLexiconTrainStrategy;
import disambiguator.strategy.lexical.PrevCurrentNextNext2WordsFullIGAndLexiconTrainStrategy;
import disambiguator.strategy.lexical.PrevLexiconPrevCurrentAndNextWordsFullIGTrainStrategy;
import disambiguator.strategy.lexical.PrevTwoAndCurrentWordsFullIGMorphTagsLexiconTrainStrategy;
import disambiguator.strategy.lexical.PrevTwoWordsFullIGMorphTagsLexiconTrainStrategy;
import disambiguator.strategy.lexical.PrevTwoWordsLastIGMorphTagsLexiconTrainStrategy;
import disambiguator.strategy.nonlexical.PredictorWithLastIGStrategy;
import disambiguator.strategy.nonlexical.PredictorWithPrevAndCurrentWordFullIGStrategy;
import disambiguator.strategy.nonlexical.PredictorWithPrevAndNextWordsFullIGStrategy;
import disambiguator.strategy.nonlexical.PredictorWithPrevCurrAndNextWordsFullIGStrategy;
import disambiguator.strategy.nonlexical.PredictorWithPrevTwoAndCurrentWordsFullIGStrategy;
import disambiguator.strategy.nonlexical.PredictorWithPrevTwoAndNextWordsFullIGStrategy;
import disambiguator.strategy.nonlexical.PredictorWithPrevTwoCurrentAndNextWordsFullIGStrategy;
import disambiguator.strategy.nonlexical.PredictorWithPrevTwoWordsFullIGLongestAnalysisStrategy;
import disambiguator.strategy.nonlexical.PredictorWithPrevTwoWordsFullIGLongestStemStrategy;
import disambiguator.strategy.nonlexical.PredictorWithPrevTwoWordsFullIGShortestTagsStrategy;
import disambiguator.strategy.nonlexical.PredictorWithPrevTwoWordsFullIGStrategy;
import disambiguator.strategy.nonlexical.PredictorWithPrevWordFullIGStrategy;
import disambiguator.strategy.nonlexical.PrevAndCurrentWordsFullIGTrainStrategy;
import disambiguator.strategy.nonlexical.PrevAndNextWordsFullIGMorphTagsTrainStrategy;
import disambiguator.strategy.nonlexical.PrevCurrAndNextWordsFullIGMorphTagsTrainStrategy;
import disambiguator.strategy.nonlexical.PrevTwoAndCurrentWordsFullIGMorphTagsTrainStrategy;
import disambiguator.strategy.nonlexical.PrevTwoAndNextWordsFullIGMorphTagsTrainStrategy;
import disambiguator.strategy.nonlexical.PrevTwoCurrentAndNextWordsFullIGMorphTagsTrainStrategy;
import disambiguator.strategy.nonlexical.PrevTwoWordsFullIGMorphTagsTrainStrategy;
import disambiguator.strategy.nonlexical.PrevTwoWordsLastIGMorphTagsTrainStrategy;
import disambiguator.strategy.nonlexical.PrevWordFullIGMorphTagsTrainStrategy;

public class FeatureExtractorStrategyFactory {
	public static FeatureExtractorStrategyFactory instance = null;
	private static HashSet<String> parameters;
	private FeatureExtractorStrategyFactory(){}
	
	public static FeatureExtractorStrategyFactory getInstance()
	{
		if(instance == null)
			instance = new FeatureExtractorStrategyFactory();
		return instance;
	}
	
	public void setFeatureSeletionParameters(String parametersFile)
	{
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
			        new FileInputStream(parametersFile), "UTF-8"));
			String line = "";
			parameters = new HashSet<String>();
			while((line = reader.readLine()) != null)
			{
				if(line == "" || line.startsWith("#"))
					continue;
				parameters.add(line.trim());
			}
			reader.close();
		} catch (UnsupportedEncodingException | FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static IFeatureExtractor getTrainFeatureExtractorStrategy(AbstractDiscriminativeDisambiguator disamb)
	{
		if(parameters.contains("DECISIONTREE") && parameters.contains("FULLIGPREV") && parameters.contains("FULLIGPREV2") 
				&& parameters.contains("CASCADED"))
			return new PrevTwoWordsFullIGMorphTagsTrainStrategy(disamb.getProblems(), disamb.getTags());
		else if(parameters.contains("DECISIONTREE") && parameters.contains("FULLIGPREV") && parameters.contains("FULLIGPREV2") && parameters.contains("ITERATIVE"))
			return new PrevTwoWordsFullIGMorphTagsTrainStrategy(disamb.getProblems(), disamb.getTags());
		else if(parameters.contains("DECISIONTREE") && parameters.contains("FULLIGPREV") && parameters.contains("FULLIGPREV2"))
			return new PrevTwoWordsFullIGMorphTagsTrainStrategy(disamb.getProblems(), disamb.getTags());
		else if(parameters.contains("DECISIONTREE") && parameters.contains("FULLIGPREV") && parameters.contains("FULLIGCURRENT") && parameters.contains("FULLIGNEXT"))
			return new PrevCurrAndNextWordsFullIGMorphTagsTrainStrategy(disamb.getProblems(), disamb.getTags());
		if(parameters.contains("HYBRIDDECISIONTREECASCADEDHASIM"))
			return new PrevCurrAndNextWordsFullIGMorphTagsTrainStrategy(disamb.getProblems(), disamb.getTags());
		else if(parameters.contains("HYBRIDCASCADEDHASIM"))
			return new PrevCurrentAndNextWordsFullIGAndLexiconStructuredTrainStrategy(disamb.getProblems(), disamb.getTags(), disamb.getVocabList());
		else if(parameters.contains("FULLIGPREV") && parameters.contains("FULLIGCURRENT") && parameters.contains("FULLIGNEXT") && parameters.contains("FULLIGNEXT2") &&
				parameters.contains("LEXICONPREV") && parameters.contains("LEXICONCURRENT") && parameters.contains("LEXICONNEXT") && parameters.contains("LEXICONNEXT2") && parameters.contains("STRUCTURED"))
			return new PrevCurrentNextNext2WordsFullIGAndLexiconTrainStrategy(disamb.getProblems(), disamb.getTags(), disamb.getVocabList());
		else if(parameters.contains("FULLIGPREV") && parameters.contains("FULLIGCURRENT") && parameters.contains("FULLIGNEXT") &&
				parameters.contains("LEXICONPREV") && parameters.contains("LEXICONCURRENT") && parameters.contains("LEXICONNEXT") && parameters.contains("STRUCTURED"))
			return new PrevCurrentAndNextWordsFullIGAndLexiconStructuredTrainStrategy(disamb.getProblems(), disamb.getTags(), disamb.getVocabList());
		else if(parameters.contains("FULLIGPREV") && parameters.contains("FULLIGCURRENT") && parameters.contains("FULLIGNEXT") &&
				parameters.contains("LEXICONPREV") && parameters.contains("LEXICONCURRENT") && parameters.contains("LEXICONNEXT"))
			return new PrevCurrentAndNextWordsFullIGAndFullLexiconTrainStrategy(disamb.getProblems(), disamb.getTags(), disamb.getVocabList());
		else if(parameters.contains("FULLIGPREV") && parameters.contains("FULLIGCURRENT") && parameters.contains("FULLIGNEXT") &&
				parameters.contains("LEXICONPREV") && parameters.contains("LEXICONCURRENT"))
			return new PrevCurrentAndNextWordsFullIGPrevAndCurrentLexiconTrainStrategy(disamb.getProblems(), disamb.getTags(), disamb.getVocabList());
		else if(parameters.contains("FULLIGPREV") && parameters.contains("FULLIGCURRENT") && parameters.contains("FULLIGNEXT") &&
				parameters.contains("LEXICONCURRENT"))
			return new PrevCurrentAndNextWordsFullIGCurrentLexiconTrainStrategy(disamb.getProblems(), disamb.getTags(), disamb.getVocabList());
		else if(parameters.contains("FULLIGPREV") && parameters.contains("FULLIGCURRENT") && parameters.contains("FULLIGNEXT") &&
				parameters.contains("LEXICONPREV"))
			return new PrevLexiconPrevCurrentAndNextWordsFullIGTrainStrategy(disamb.getProblems(), disamb.getTags(), disamb.getVocabList());
			//return new PrevCurrentAndNextWordsFullIGPrevLexiconTrainStrategy(disamb.getProblems(), disamb.getTags(), disamb.getVocabList());
		else if(parameters.contains("FULLIGPREV2") && parameters.contains("LEXICON") && parameters.contains("FULLIGCURRENT"))
			return new PrevTwoAndCurrentWordsFullIGMorphTagsLexiconTrainStrategy(disamb.getProblems(), disamb.getTags(), disamb.getVocabList());
		else if(parameters.contains("FULLIGPREV2") && parameters.contains("LEXICON"))
			return new PrevTwoWordsFullIGMorphTagsLexiconTrainStrategy(disamb.getProblems(), disamb.getTags(), disamb.getVocabList());
		else if(parameters.contains("FULLIGPREV2") && parameters.contains("SHORTESTTAG"))
			return new PrevTwoWordsFullIGMorphTagsTrainStrategy(disamb.getProblems(), disamb.getTags());
		else if(parameters.contains("FULLIGPREV2") && parameters.contains("LONGESTSTEM"))
			return new PrevTwoWordsFullIGMorphTagsTrainStrategy(disamb.getProblems(), disamb.getTags());
		else if(parameters.contains("FULLIGPREV2") && parameters.contains("LONGESTANALYSIS"))
			return new PrevTwoWordsFullIGMorphTagsTrainStrategy(disamb.getProblems(), disamb.getTags());
		else if(parameters.contains("FULLIGPREV2") && parameters.contains("FULLIGCURRENT") && parameters.contains("FULLIGNEXT"))
			return new PrevTwoCurrentAndNextWordsFullIGMorphTagsTrainStrategy(disamb.getProblems(), disamb.getTags());
		else if(parameters.contains("FULLIGPREV2") && parameters.contains("FULLIGCURRENT"))
			return new PrevTwoAndCurrentWordsFullIGMorphTagsTrainStrategy(disamb.getProblems(), disamb.getTags());
		else if(parameters.contains("FULLIGPREV2") && parameters.contains("FULLIGNEXT"))
			return new PrevTwoAndNextWordsFullIGMorphTagsTrainStrategy(disamb.getProblems(), disamb.getTags());
		else if(parameters.contains("FULLIGPREV2"))
			return new PrevTwoWordsFullIGMorphTagsTrainStrategy(disamb.getProblems(), disamb.getTags());
		else if(parameters.contains("FULLIGPREV") && parameters.contains("FULLIGCURRENT") && parameters.contains("FULLIGNEXT"))
			return new PrevCurrAndNextWordsFullIGMorphTagsTrainStrategy(disamb.getProblems(), disamb.getTags());
		else if(parameters.contains("FULLIGPREV") && parameters.contains("FULLIGCURRENT"))
			return new PrevAndCurrentWordsFullIGTrainStrategy(disamb.getProblems(), disamb.getTags());
		else if(parameters.contains("FULLIGPREV") && parameters.contains("FULLIGNEXT"))
			return new PrevAndNextWordsFullIGMorphTagsTrainStrategy(disamb.getProblems(), disamb.getTags());
		else if(parameters.contains("FULLIGPREV"))
			return new PrevWordFullIGMorphTagsTrainStrategy(disamb.getProblems(), disamb.getTags());
		else if(parameters.contains("LEXICON") && parameters.contains("LASTIGPREV2"))
			return new PrevTwoWordsLastIGMorphTagsLexiconTrainStrategy(disamb.getProblems(), disamb.getTags(), disamb.getVocabList());
		else if(parameters.contains("LASTIGPREV2"))
			return new PrevTwoWordsLastIGMorphTagsTrainStrategy(disamb.getProblems(), disamb.getTags());
		else if(parameters.contains("MERGEDPROBLEMS"))
			return new PrevCurrentAndNextWordsFullIGAndLexiconMergedProblemsTrainStrategy(disamb.getProblems(), disamb.getTags(), disamb.getVocabList());
		else
			return null;
	}
	
	public static IFeatureExtractor getTestFeatureExtractorStrategy(AbstractDiscriminativeDisambiguator disamb)
	{
		if(parameters.contains("DECISIONTREE") && parameters.contains("FULLIGPREV") && parameters.contains("FULLIGPREV2") 
				&& parameters.contains("CASCADED"))
			return new PredictorDecisionTreeWithHasimCascadedPrevTwoWordsStrategy(disamb.getProblems(), disamb.getTags(), disamb.getTrainingFile());
		else if(parameters.contains("DECISIONTREE") && parameters.contains("FULLIGPREV") && parameters.contains("FULLIGPREV2") && parameters.contains("ITERATIVE"))
			return new PredictorDecisionTreeWithPrevTwoWordsIterativeStrategy(disamb.getProblems(), disamb.getTags(), disamb.getTrainingFile());
		else if(parameters.contains("DECISIONTREE") && parameters.contains("FULLIGPREV") && parameters.contains("FULLIGPREV2"))
			return new PredictorDecisionTreeWithPrevTwoWordsStrategy(disamb.getProblems(), disamb.getTags(), disamb.getTrainingFile());
		else if(parameters.contains("DECISIONTREE") && parameters.contains("FULLIGPREV") && parameters.contains("FULLIGCURRENT") && parameters.contains("FULLIGNEXT"))
			return new PredictorDecisionTreeWithPrevCurrentNextWordsFullIGStrategy(disamb.getProblems(), disamb.getTags(), disamb.getTrainingFile());
		else if(parameters.contains("HYBRIDDECISIONTREECASCADEDHASIM"))
			return new PredictorDecisionTreeWithHybridCascadedHasimStrategy(disamb.getProblems(), disamb.getTags(), disamb.getTrainingFile());
		else if(parameters.contains("HYBRIDCASCADEDHASIM"))
			return new PredictorWithHybridCascadedWithHasimStrategy(disamb.getProblems(), disamb.getTags(), disamb.getVocabList(), disamb.getTrainingFile());
		else if(parameters.contains("FULLIGPREV") && parameters.contains("FULLIGCURRENT") && parameters.contains("FULLIGNEXT") && parameters.contains("FULLIGNEXT2") &&
				parameters.contains("LEXICONPREV") && parameters.contains("LEXICONCURRENT") && parameters.contains("LEXICONNEXT") && parameters.contains("LEXICONNEXT2") && parameters.contains("STRUCTURED"))
			return new PredictorWithPrevCurrentNextNext2WordsFullIGAndLexiconStructuredStrategy(disamb.getProblems(), disamb.getTags(), disamb.getVocabList(), disamb.getTrainingFile());
		else if(parameters.contains("FULLIGPREV") && parameters.contains("FULLIGCURRENT") && parameters.contains("FULLIGNEXT") &&
				parameters.contains("LEXICONPREV") && parameters.contains("LEXICONCURRENT") && parameters.contains("LEXICONNEXT") && parameters.contains("STRUCTURED"))
			return new PredictorWithPrevCurrentAndNextWordsFullIGAndLexiconStructuredStrategy(disamb.getProblems(), disamb.getTags(), disamb.getVocabList(), disamb.getTrainingFile());
		else if(parameters.contains("FULLIGPREV") && parameters.contains("FULLIGCURRENT") && parameters.contains("FULLIGNEXT") &&
				parameters.contains("LEXICONPREV") && parameters.contains("LEXICONCURRENT") && parameters.contains("LEXICONNEXT"))
			return new PredictorWithPrevCurrentAndNextWordsFullIGAndLexiconStrategy(disamb.getProblems(), disamb.getTags(), disamb.getVocabList(), disamb.getTrainingFile());
		else if(parameters.contains("FULLIGPREV") && parameters.contains("FULLIGCURRENT") && parameters.contains("FULLIGNEXT") &&
				parameters.contains("LEXICONPREV") && parameters.contains("LEXICONCURRENT"))
			return new PredictorWithPrevCurrentAndNextWordsFullIGPrevAndCurrentLexiconStrategy(disamb.getProblems(), disamb.getTags(), disamb.getVocabList(), disamb.getTrainingFile());
		else if(parameters.contains("FULLIGPREV") && parameters.contains("FULLIGCURRENT") && parameters.contains("FULLIGNEXT") &&
				parameters.contains("LEXICONCURRENT"))
			return new PredictorWithPrevCurrentAndNextWordsFullIGCurrentLexiconStrategy(disamb.getProblems(), disamb.getTags(), disamb.getVocabList(), disamb.getTrainingFile());
		else if(parameters.contains("FULLIGPREV") && parameters.contains("FULLIGCURRENT") && parameters.contains("FULLIGNEXT") &&
				parameters.contains("LEXICONPREV"))
			return new PredictorWithPrevLexiconPrevCurrentAndNextWordsFullIGStrategy(disamb.getProblems(), disamb.getTags(), disamb.getVocabList(), disamb.getTrainingFile());
			//return new PredictorWithPrevCurrentAndNextWordsFullIGPrevLexiconStrategy(disamb.getProblems(), disamb.getTags(), disamb.getVocabList(), disamb.getTrainingFile());
		else if(parameters.contains("FULLIGPREV2") && parameters.contains("LEXICON") && parameters.contains("FULLIGCURRENT"))
			return new PredictorWithPrevTwoAndCurrentWordsFullIGLexiconStrategy(disamb.getProblems(), disamb.getTags(), disamb.getVocabList(), disamb.getTrainingFile());
		else if(parameters.contains("FULLIGPREV2") && parameters.contains("LEXICON"))
			return new PredictorWithPrevTwoWordsFullIGLexiconStrategy(disamb.getProblems(), disamb.getTags(), disamb.getVocabList(), disamb.getTrainingFile());
		else if(parameters.contains("FULLIGPREV2") && parameters.contains("SHORTESTTAG"))
			return new PredictorWithPrevTwoWordsFullIGShortestTagsStrategy(disamb.getProblems(), disamb.getTags(), disamb.getTrainingFile());
		else if(parameters.contains("FULLIGPREV2") && parameters.contains("LONGESTSTEM"))
			return new PredictorWithPrevTwoWordsFullIGLongestStemStrategy(disamb.getProblems(), disamb.getTags(), disamb.getTrainingFile());
		else if(parameters.contains("FULLIGPREV2") && parameters.contains("LONGESTANALYSIS"))
			return new PredictorWithPrevTwoWordsFullIGLongestAnalysisStrategy(disamb.getProblems(), disamb.getTags(), disamb.getTrainingFile());
		else if(parameters.contains("FULLIGPREV2") && parameters.contains("FULLIGCURRENT") && parameters.contains("FULLIGNEXT"))
			return new PredictorWithPrevTwoCurrentAndNextWordsFullIGStrategy(disamb.getProblems(), disamb.getTags(), disamb.getTrainingFile());
		else if(parameters.contains("FULLIGPREV2") && parameters.contains("FULLIGCURRENT"))
			return new PredictorWithPrevTwoAndCurrentWordsFullIGStrategy(disamb.getProblems(), disamb.getTags(), disamb.getTrainingFile());
		else if(parameters.contains("FULLIGPREV2") && parameters.contains("FULLIGNEXT"))
			return new PredictorWithPrevTwoAndNextWordsFullIGStrategy(disamb.getProblems(), disamb.getTags(), disamb.getTrainingFile());
		else if(parameters.contains("FULLIGPREV2"))
			return new PredictorWithPrevTwoWordsFullIGStrategy(disamb.getProblems(), disamb.getTags(), disamb.getTrainingFile());
		else if(parameters.contains("FULLIGPREV2") && parameters.contains("FULLIGCURRENT"))
			return new PredictorWithPrevAndCurrentWordFullIGStrategy(disamb.getProblems(), disamb.getTags(), disamb.getTrainingFile());
		else if(parameters.contains("FULLIGPREV") && parameters.contains("FULLIGCURRENT") && parameters.contains("FULLIGNEXT"))
			return new PredictorWithPrevCurrAndNextWordsFullIGStrategy(disamb.getProblems(), disamb.getTags(), disamb.getTrainingFile());
		else if(parameters.contains("FULLIGPREV") && parameters.contains("FULLIGNEXT"))
			return new PredictorWithPrevAndNextWordsFullIGStrategy(disamb.getProblems(), disamb.getTags(), disamb.getTrainingFile());
		else if(parameters.contains("FULLIGPREV"))
			return new PredictorWithPrevWordFullIGStrategy(disamb.getProblems(), disamb.getTags(), disamb.getTrainingFile());
		else if(parameters.contains("LEXICON") && parameters.contains("LASTIGPREV2"))
			return new PredictorWithPrevTwoWordsLastIGLexiconStrategy(disamb.getProblems(), disamb.getTags(), disamb.getVocabList(), disamb.getTrainingFile());
		else if(parameters.contains("LASTIGPREV2"))
			return new PredictorWithLastIGStrategy(disamb.getProblems(), disamb.getTags(), disamb.getTrainingFile());
		else if(parameters.contains("MERGEDPROBLEMS"))
			return new PredictorWithPrevCurrentAndNextWordsFullIGAndLexiconMergedProblemsStrategy(disamb.getProblems(), disamb.getTags(), disamb.getVocabList(), disamb.getTrainingFile());
		else
			return null;
	}

}
