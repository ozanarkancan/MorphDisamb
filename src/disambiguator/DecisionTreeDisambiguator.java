package disambiguator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import cc.mallet.classify.C45Trainer;
import cc.mallet.classify.ClassifierTrainer;
import cc.mallet.pipe.SvmLight2FeatureVectorAndLabel;
import cc.mallet.pipe.iterator.SimpleFileLineIterator;
import cc.mallet.types.InstanceList;
import data.Problem;
import disambiguator.strategy.FeatureExtractorStrategyFactory;

public class DecisionTreeDisambiguator extends AbstractDiscriminativeDisambiguator{

	public DecisionTreeDisambiguator(ArrayList<String> trainOptions, String featureParameterFile) {
		super(trainOptions, featureParameterFile);
		// TODO Auto-generated constructor stub
	}

	@Override
	public String analyzeFile(String inputfilename, String extension) {
		this.testFile = inputfilename;
		this.outfile = inputfilename + "." + extension;
			
		this.testFeatureExtractorStrategy = FeatureExtractorStrategyFactory.getInstance().getTestFeatureExtractorStrategy(this);
		this.testFeatureExtractorStrategy.extractFeatures(inputfilename, "dTreeOut");
		
		return "";
	}

	@Override
	public void train(ArrayList<String> traininginputfiles) {
		this.traininginputfilename = traininginputfiles.get(0);
		tags.determineTagList(traininginputfiles.get(0));
		System.out.println("Number of tags: " + tags.getTagsSize());
		problems.determineProblemSet(traininginputfiles.get(0));
		/*System.out.println("Number of problems: " + problems.getNumberOfProblems());
		vocabList.determineVocabSet(traininginputfiles.get(0));
		System.out.println("Number of vocab words: " + vocabList.getVocabSize());*/
		this.trainFeatureExtractorStrategy = FeatureExtractorStrategyFactory.getInstance().getTrainFeatureExtractorStrategy(this);
		this.trainFeatureExtractorStrategy.extractFeatures(traininginputfiles.get(0), "train");
		ClassifierTrainer decisionTree = new C45Trainer();
		for(Problem problem : problems)
		{
			if(problem.getLabels().size() != 1)
			{
				String featuredFile = traininginputfiles.get(0) + "." + "problem" + Integer.toString(problem.getIndex()) + "." + "train" + "." + "featured";
				InstanceList instances = new InstanceList(new SvmLight2FeatureVectorAndLabel());
				instances.addThruPipe(new SimpleFileLineIterator(featuredFile));
				decisionTree.train(instances);
				ObjectOutputStream oos;
				try {
					oos = new ObjectOutputStream(new FileOutputStream (new File(featuredFile + ".model")));
					oos.writeObject (decisionTree.getClassifier());
			        oos.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
	}

}
