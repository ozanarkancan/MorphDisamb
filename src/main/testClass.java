package main;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import disambiguator.DecisionTreeDisambiguator;
import disambiguator.MorphologicalDisambiguator;
import disambiguator.SVMMorphologicalDisambiguatorLexical;
import disambiguator.SVMMorphologicalDisambiguatorNonLexical;
import evaluator.Postagger_evaluator_biber;
import evaluator.ProblemPredictionEvaluator;

public class testClass {
	public enum InputType {
		HASIM, YURET, ITU_CRF, HMM_Cuneyd, SVM, DT
	}
	public static void main(String[] args) throws FileNotFoundException, IOException {
		long startTime,endTime ;
		MorphologicalDisambiguator morphdisamb = null;
		//SVMMorphologicalDisambiguatorLexical morphdisamb = null;
		InputType choice = InputType.SVM;
		ArrayList<String> parameters = new ArrayList<String>();
		parameters.add("-t");
		parameters.add("1");
		parameters.add("-d");
		parameters.add("2");
		parameters.add("-g");
		parameters.add("0.12");
		parameters.add("-c");
		parameters.add("0.17");
		parameters.add("-r");
		parameters.add("0.6");
		switch (choice){
	//	case HASIM:  morphdisamb = new HasimMorphologicalDisambiguator(HasimMorphologicalDisambiguator.InputType.OFL);break;
		//case YURET:  morphdisamb = new YuretMorphologicalDisambiguator();break;
//		case ITU_CRF: morphdisamb = new ITU_CRFMorphologicalDisambiguator();break;
//		case HMM_Cuneyd: morphdisamb = new HMM_CuneydMorphologicalDisambiguator();break;
		//case SVM: morphdisamb = new SVMMorphologicalDisambiguatorLexical(parameters, "src/disambiguator/strategy/resources/parameters"); break;//Run with default parameters
		case SVM: morphdisamb = new SVMMorphologicalDisambiguatorLexical(parameters, "src/disambiguator/strategy/resources/parameters"); break;//Run with default parameters
		case DT: morphdisamb = new DecisionTreeDisambiguator(parameters, "src/disambiguator/strategy/resources/parameters"); break;//Run with default parameters
		}
		
				
		
		//Training
		String originaltrainingfilename = "D:\\bitirme\\tests\\demo\\mst-ivs\\METUSABANCI_treebank_vmappedtonewOfltags";
		//String originaltrainingfilename="D:\\bitirme\\data\\forTestingCode\\MSTtrain-TDStest\\METUSABANCI_treebank_vmappedtonewOfltags";
		//String originaltrainingfilename = "D:\\bitirme\\data\\testOptimalTrain\\METUSABANCI_treebank_vmappedtonewOfltags";
		//String originaltrainingfilename="D:\\bitirme\\data\\forTestingCode\\train_vmappedtonewOfltags";
		//String originaltrainingfilename="D:\\bitirme\\data\\testOptimalTrain\\METUSABANCI_treebank_vmappedtonewOfltags";
		//String originaltrainingfilename="D:\\bitirme\\data\\forTestingCode\\MST+Train-IVStest\\mixxedTrainMST";
		//String originaltrainingfilename="D:\\bitirme\\data\\forTestingCode\\MSTtrain-IVStest\\METUSABANCI_treebank_vmappedtonewOfltags";
		//String originaltrainingfilename2="D:\\bitirme\\data\\forTestingCode\\train";
		startTime = System.currentTimeMillis();
		System.out.println("Training on : "+originaltrainingfilename);
		
		ArrayList<String> trainingfiles=new ArrayList<String>();
		trainingfiles.add(originaltrainingfilename);
		morphdisamb.train(trainingfiles);
		endTime = System.currentTimeMillis();
		System.out.println("Total elapsed time in execution of :"+ (endTime-startTime)/1000+ "sn");
		
		String originaltestfilename="D:\\bitirme\\tests\\demo\\mst-ivs\\ituvalidationset_vmappedtonewOfltags";
		//String originaltestfilename="D:\\bitirme\\data\\testOptimalTrain\\test_vmappedtonewOfltags";
		//String originaltestfilename="D:\\bitirme\\data\\forTestingCode\\MSTtrain-TDStest\\TDS";
		//String originaltestfilename="D:\\bitirme\\data\\forTestingCode\\METUSABANCI_treebank_vmappedtonewOfltags";
		//String originaltestfilename="D:\\bitirme\\data\\forTestingCode\\ituvalidation\\ituvalidationset_vmappedtonewOfltags";
		//String originaltestfilename="D:\\bitirme\\data\\testOptimalTrain\\ituvalidationset_vmappedtonewOfltags";
		//String originaltestfilename="D:\\bitirme\\data\\testOptimalTrain\\test_vmappedtonewOfltags";
		//String originaltestfilename="D:\\bitirme\\data\\forTestingCode\\MSTtrain-IVStest\\ituvalidationset_vmappedtonewOfltags";
		//String originaltestfilename="D:\\bitirme\\data\\forTestingCode\\MST+Train-IVStest\\ituvalidationset_vmappedtonewOfltags";
		//String originaltestfilename="D:\\bitirme\\data\\forTestingCode\\test_vmapped\\test_vmappedtonewOfltags";
		//String originaltestfilename2="D:\\bitirme\\data\\forTestingCode\\test_vmappedtonewOfltags";
		//String originaltestfilename3="D:\\bitirme\\data\\forTestingCode\\test";
		String morphoutput = originaltestfilename+".txt."+"morphinput"+"."+"ofl2012output"+"."+"sng";
		String referencefile=originaltestfilename + ".txt";
		
		//Comment out when training is not necessary
		//((SVMMorphologicalDisambiguatorLexical)morphdisamb).setTrainingFileName(originaltrainingfilename);
		//((SVMMorphologicalDisambiguatorLexical)morphdisamb).loadTagSet(originaltrainingfilename + ".tagInfo");
		//((SVMMorphologicalDisambiguatorLexical)morphdisamb).loadProblemSet(originaltrainingfilename + ".problemSet");
		//((SVMMorphologicalDisambiguatorLexical)morphdisamb).loadVocabList(originaltrainingfilename + ".vocab");
		startTime= System.currentTimeMillis();
		System.out.println("Disambiguation on : "+originaltestfilename);
		morphdisamb.analyzeFile(morphoutput + ".nonreplicate","svmout");
		endTime = System.currentTimeMillis();
		System.out.println("Total elapsed time in execution of :"+ (endTime-startTime)/1000+ "sn");
		
		//RuleApplier.applyEditingWrongAcc(originaltestfilename, morphoutput + ".nonreplicate.postagged");
		
		ProblemPredictionEvaluator ppb = new ProblemPredictionEvaluator();
		ppb.evaluate(originaltrainingfilename + ".problemSet", morphoutput + ".nonreplicate", referencefile, morphoutput + ".nonreplicate.svmout");
		
		Postagger_evaluator_biber b = new Postagger_evaluator_biber(morphoutput + ".nonreplicate.svmout", referencefile, morphoutput + ".nonreplicate");
		b.evaluate();
		/*ProblemSet ps = new ProblemSet();
		ps.determineProblemSetWithSimilarityMetric(originaltrainingfilename);*/
		}
}
