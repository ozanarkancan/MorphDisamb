package data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.TreeSet;

import core.Parse;
import core.Word;

public class Problem implements Comparable<Problem>,Serializable{
	private int index;
	private int count;
	private TreeSet<String> labels;
	private HashMap<Integer, Integer> counts;
	private HashSet<String> morphFeatureVector;
	
	public Problem()
	{
		morphFeatureVector = new HashSet<String>();
		setLabels(new TreeSet<String>());
		counts = new HashMap<Integer, Integer>();
		count = 0;
	}
	
	public Problem(Word word)
	{
		setLabels(new TreeSet<String>());
		counts = new HashMap<Integer, Integer>();
		count = 0;
		
		for(Parse p : word.parses)
				this.addLabel(p.analysis);
	}
	
	public Problem(Problem rhs)
	{
		setLabels(rhs.getLabels());
		setIndex(rhs.getIndex());
		counts = new HashMap<Integer, Integer>();
	}
	
	public Problem clone()
	{
		Problem cloneProblem = new Problem();
		for(String l : labels)
			cloneProblem.addLabel(l);
		cloneProblem.setIndex(index);
		return cloneProblem;
	}
	
	@Override
	public int compareTo(Problem arg0) {//Used in sorting. Descending order
		if(this.count > arg0.getCount())
			return -1;
		else if(this.count == arg0.getCount())
			return 0;
		else
			return 1;
	}
	
	public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (obj == this)
            return true;
        if (!(obj instanceof Problem))
            return false;

        Problem rhs = (Problem) obj;
        return this.labels.equals(rhs.getLabels());
    }
	
	public void incrementCount()
	{
		count++;
	}
	
	public int getIndex() {
		return index;
	}
	
	public void setIndex(int index) {
		this.index = index;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}
	
	public void addLabel(String label)
	{
		if(!labels.contains(label))
		{
			labels.add(label);
			if(morphFeatureVector == null)
				morphFeatureVector = new HashSet<String>();
			
			String[] igs = label.split("\\^DB");
			for(String ig : igs)
			{
				String[] morphFeats = ig.split("\\+");
				for(String feat : morphFeats)
					if(!feat.equals(""))
						morphFeatureVector.add(feat);
			}
		}		
	}

	public TreeSet<String> getLabels() {
		return labels;
	}

	public void setLabels(TreeSet<String> labels) {
		this.labels = labels;
	}
	
	public void incrementLabelCount(int label)
	{
		if(counts.containsKey(label))
			counts.put(label, counts.get(label) + 1);
		else
			counts.put(label, 1);
	}
	
	public int getLabelCount(int label)
	{
		if(counts.containsKey(label))
			return counts.get(label);
		else
			return 0;
	}
	
	public String getLabel(int labelIndex)
	{
		int i = 0;
		for(String label : labels)
		{
			if(i == labelIndex)
				return label;
			else
				i++;
		}
		return "";
	}
	
	public int getLabelOfWord(Word word)
	{
		int result = -1;
		
		int classIndex = 0;
		for(String classParse : labels)
		{
			if(word.correct.analysis.equals(classParse))
			{
				result = classIndex;
				break;
			}
			else
				classIndex++;
		}
			
		return result;
	}
	
	public void setLabelCounts(int label, int count)
	{
		counts.put(label, count);
	}
	
	public HashSet<String> getMorphFeatureVectorForm()
	{
		return this.morphFeatureVector;
	}
}
