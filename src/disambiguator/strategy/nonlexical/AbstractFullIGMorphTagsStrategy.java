package disambiguator.strategy.nonlexical;

import java.util.ArrayList;
import java.util.Collections;

import core.Parse;
import core.Word;
import data.TagSet;
import disambiguator.strategy.AbstractPredictor;
import disambiguator.strategy.IFeatureExtractor;

public abstract class AbstractFullIGMorphTagsStrategy extends AbstractPredictor implements IFeatureExtractor{
	protected TagSet tags;
	public AbstractFullIGMorphTagsStrategy(TagSet tags)
	{
		this.tags = tags;
	}
	
	public ArrayList<Integer> getFullIGFeatures(Word word)
	{
		ArrayList<Integer> features = new ArrayList<Integer>();
		if(word.parses.size() > 0)
			if(word.parses.get(0).equals("***UNKNOWN"))
				return features;
		
		for(Parse parse : word.parses)
		{
			for(ArrayList<String> ig : parse.igs)
			{
				for(String morphFeat : ig)
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
