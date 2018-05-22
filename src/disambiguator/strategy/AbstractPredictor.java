package disambiguator.strategy;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.StringTokenizer;

import cc.mallet.classify.Classifier;
import cc.mallet.pipe.SvmLight2FeatureVectorAndLabel;
import cc.mallet.pipe.iterator.ArrayIterator;
import cc.mallet.types.InstanceList;
import libsvm.svm;
import libsvm.svm_model;
import libsvm.svm_node;

public abstract class AbstractPredictor {
	protected double[] predict(String instance, String modelFile)
	{
		double[] result = new double[2];
		double v = -1;
		try {
			svm_model model = svm.svm_load_model(modelFile);
			StringTokenizer st = new StringTokenizer(instance," \t\n\r\f:");

			double target = atof(st.nextToken());
			int m = st.countTokens()/2;
			svm_node[] x = new svm_node[m];
			for(int j=0;j<m;j++)
			{
				x[j] = new svm_node();
				x[j].index = atoi(st.nextToken());
				x[j].value = atof(st.nextToken());
			}

			v = svm.svm_predict(model,x);
			result[0] = v;
			if(target == v)
				result[1] = 1;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	protected double[] predictDT(ArrayList<String> instance, String modelFile)
	{
		double[] result = new double[2];
		try {
			/*oos = new ObjectOutputStream(new FileOutputStream (new File("D:\\deneme\\METUSABANCI_treebank_vmappedtonewOfltags.problem1.train.featured.model")));
			oos.writeObject (decisionTree.getClassifier());
	        oos.close();*/
			Classifier classifier;

	        ObjectInputStream ois =
	            new ObjectInputStream (new FileInputStream (new File(modelFile)));
	        classifier = (Classifier) ois.readObject();
	        ois.close();
	        
	        InstanceList instances = new InstanceList(new SvmLight2FeatureVectorAndLabel());
	        instances.addThruPipe(new ArrayIterator(instance));
	        
	        result[0] = Double.parseDouble(classifier.classify(instances.get(0)).getLabelVector().getBestLabel().toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}
	
	protected double atof(String s)
	{
		return Double.valueOf(s).doubleValue();
	}

	protected int atoi(String s)
	{
		return Integer.parseInt(s);
	}

}
