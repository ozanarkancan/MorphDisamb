package disambiguator;

import java.io.IOException;
import java.util.ArrayList;

import data.Problem;
import disambiguator.strategy.FeatureExtractorStrategyFactory;


public class SVMMorphologicalDisambiguatorNonLexical extends AbstractDiscriminativeDisambiguator{
	
	public SVMMorphologicalDisambiguatorNonLexical(ArrayList<String> trainOptions, String featureParameterFile)
	{
		super(trainOptions, featureParameterFile);
	}
	
	
	public String analyzeFile(String inputfilename,String extension){
		this.testFile = inputfilename;
		this.outfile = inputfilename + "." + extension;
			
		this.testFeatureExtractorStrategy = FeatureExtractorStrategyFactory.getInstance().getTestFeatureExtractorStrategy(this);
		this.testFeatureExtractorStrategy.extractFeatures(inputfilename, "svmout");
		
		return "";
	}
	
	@Override
	public void train(ArrayList<String> traininginputfiles) {
		this.traininginputfilename = traininginputfiles.get(0);
		tags.determineTagList(traininginputfiles.get(0));
		System.out.println("Number of tags: " + tags.getTagsSize());
		problems.determineProblemSet(traininginputfiles.get(0));
		System.out.println("Number of problems: " + problems.getNumberOfProblems());
		problems.saveBinary(traininginputfiles.get(0) + ".problemSetBinary");
		
		this.trainFeatureExtractorStrategy = FeatureExtractorStrategyFactory.getInstance().getTrainFeatureExtractorStrategy(this);
		this.trainFeatureExtractorStrategy.extractFeatures(traininginputfiles.get(0), "train");
		SvmTrainer svmTrain = new SvmTrainer();
		for(Problem problem : problems)
		{
			if(problem.getLabels().size() != 1)
			{
				String[] commands;
				if(this.trainOptions != null)
				{
					commands = new String[this.trainOptions.size() + 1];
					for(int i = 0; i < this.trainOptions.size(); i++)
						commands[i] = this.trainOptions.get(i);
				}
				else
					commands = new String[1];
				
				commands[commands.length - 1] = traininginputfiles.get(0) + "." + "problem" + Integer.toString(problem.getIndex()) + "." + "train" + "." + "featured";
				try {
					svmTrain.run(commands);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
	}
}
