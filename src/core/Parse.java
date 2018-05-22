package core;

import java.util.ArrayList;

public class Parse {
	public String root;
	public String analysis;
	public ArrayList<ArrayList<String>> igs;
	
	public Parse()
	{
		root = "";
		analysis = "";
		igs = new ArrayList<ArrayList<String>>();
	}
	
	public Parse(String analysis)
	{
		igs = new ArrayList<ArrayList<String>>();
		if(analysis.startsWith("+"))
		{
			root = "+";
			this.analysis = "Punc";
			ArrayList<String> morphFeature = new ArrayList<String>();
			morphFeature.add("Punc");
			igs.add(morphFeature);
		}
		else
		{
			String[] igParts = analysis.split("\\^DB");
			int endOfRoot = igParts[0].indexOf("+");
			if(endOfRoot == -1)
			{
				root = igParts[0];
				this.analysis = "";
			}
			else
			{
				root = igParts[0].substring(0, endOfRoot);
				this.analysis = analysis.substring(endOfRoot + 1); 
				igParts[0] = igParts[0].substring(endOfRoot + 1);
			
				for(int i = 0; i < igParts.length; i++)
				{
					ArrayList<String> morphFeatures = new ArrayList<String>();
					for (String morphFeat : igParts[i].split("\\+"))
						if(!morphFeat.equals(""))
							morphFeatures.add(morphFeat);
					igs.add(morphFeatures);
				}
			}
		}
	}
	
	public boolean equals(Object obj)
	{
		if (obj == null)
            return false;
        if (obj == this)
            return true;
        if (!(obj instanceof Parse))
            return false;

        Parse rhs = (Parse) obj;
        return this.root.equals(rhs.root) && this.analysis.equals(rhs.analysis) 
        		&& this.igs.equals(rhs.igs) ;
	}
	
	public String toString()
	{
		String str = root;
		if(igs == null || igs.size() == 0)
			return str;
		for(String morphFeat : igs.get(0))
			str += "+" + morphFeat;
		for(int i = 1; i < igs.size(); i++)
		{
			str += "^DB";
			for(String morphFeat : igs.get(i))
				str += "+" + morphFeat;
		}
		
		return str;
	}
	
	public int getNumberOfMorphTags()
	{
		int total = 0;
		for(ArrayList<String> ig : igs)
				total += ig.size();
		
		return total;
	}
	
	public int getTotalCharacterLengthOfAllIGs()
	{
		int length = 0;
		for(ArrayList<String> ig : igs)
			for(String tag : ig)
				length += tag.length();
		
		return length;
	}

}
