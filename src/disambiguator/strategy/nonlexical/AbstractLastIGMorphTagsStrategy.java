package disambiguator.strategy.nonlexical;

import java.util.ArrayList;
import java.util.Collections;

import core.Parse;
import core.Word;
import data.TagSet;
import disambiguator.strategy.AbstractPredictor;
import disambiguator.strategy.IFeatureExtractor;

public abstract class AbstractLastIGMorphTagsStrategy extends AbstractPredictor implements IFeatureExtractor{
	protected TagSet tags;
	public AbstractLastIGMorphTagsStrategy(TagSet tags)
	{
		this.tags = tags;
	}
	
	public ArrayList<Integer> getLastIGFeatures(Word word)
	{
		ArrayList<Integer> features = new ArrayList<Integer>();
		if(word.parses.size() > 0)
			if(word.parses.get(0).equals("***UNKNOWN"))
				return features;
		
		for(Parse parse : word.parses)
		{
			if(parse.igs.size() != 0)
			{
				for(String morphFeat : parse.igs.get(parse.igs.size() - 1))
				{
					if(!morphFeat.equals(""))
					{
						int index = tags.getIndex(morphFeat);
						if(index != -1 && !features.contains(index))//There can be unknown feature in test
							features.add(index);
					}
				}
			}
		}
		Collections.sort(features);
		return features;
	}

}
