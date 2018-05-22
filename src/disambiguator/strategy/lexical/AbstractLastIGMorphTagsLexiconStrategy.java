package disambiguator.strategy.lexical;

import java.util.ArrayList;
import java.util.Collections;

import core.Parse;
import core.Word;
import data.TagSet;
import data.VocabularyList;
import disambiguator.strategy.nonlexical.AbstractLastIGMorphTagsStrategy;

public abstract class AbstractLastIGMorphTagsLexiconStrategy extends AbstractLastIGMorphTagsStrategy{

	protected VocabularyList vocabularyList;
	public AbstractLastIGMorphTagsLexiconStrategy(TagSet tags, VocabularyList vocabularyList) {
		super(tags);
		this.vocabularyList = vocabularyList;
	}
	
	public ArrayList<Integer> getLexiconFeatures(Word word)
	{
		ArrayList<Integer> features = new ArrayList<Integer>();
		if(word.parses.size() > 0)
			if(word.parses.get(0).equals("***UNKNOWN"))
				return features;
		
		for(Parse parse : word.parses)
		{
			int rootIndex = vocabularyList.getIndex(parse.root);
			if(rootIndex != -1)
				features.add(rootIndex);
		}
		Collections.sort(features);
		return features;
	}

}
