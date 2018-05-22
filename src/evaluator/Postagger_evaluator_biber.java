package evaluator;


import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import externalcodes.common.HashMultiMap;


/**
 * @author tantug
 *
 */
public class Postagger_evaluator_biber {
		
	String morphFile, disOutputFile, refFile;
	boolean useMorphFile;
	boolean singleLineMode;
	
	DataInputStream morph;
	DataInputStream dis;
	DataInputStream ref;
	BufferedReader morphR;
	BufferedReader disR;
	BufferedReader refR;
	
	protected int 	sentenceCount=0;
	protected int	tokenCount=0;
	protected int	puncCount=0;
	protected int	tagCount=0;
	protected int	ambiguityLevelCounts[] = new int[50];
	protected int   maxAmbiguityLevel = 0;
	protected int   ambiguityLevel1ButFalse = 0;
	
	protected int	fullParseSuccessCount = 0;
	protected int	rootSuccessCount = 0;
	protected int   ambiguityLevel1ButRootFalse = 0;
	protected int   ambiguityLevel0RootTrue = 0;
	private Set<String> distinctsurfaceforms;
	private Set<String> distinctmorphanalysis;
	Map<String,Integer> confusionmatrix = new HashMap<String,Integer>();
	
	public int getSentenceCount() {
		return sentenceCount;
	}
	public int gettokenCount() {
		return tokenCount;
	}
	public int getTagCount() {
		return tagCount;
	}
	
	public int getPuncCount() {
		return puncCount;
	}

	public int getUnambiguoustokenCount() {
		return ambiguityLevelCounts[1]+ambiguityLevelCounts[0];
	}

	public int getAmbiguoustokenCount() {
		return tokenCount- getUnambiguoustokenCount();
	}
	
	public int[] getAmbiguityLevelCounts() {
		return ambiguityLevelCounts;
	}
	
	public int getRootSuccessCount() {
		return rootSuccessCount;
	}	

	public int getFullParseSuccessCount() {
		return fullParseSuccessCount;
	}

	public int getMaxAmbiguityLevel() {
		return maxAmbiguityLevel;
	}
	
	public void updateMaxAmbiguityLevel(int level) {
		if (level > maxAmbiguityLevel)
			maxAmbiguityLevel = level;		
	}

	public float overallFullParseAccuracy(){
		return fullParseSuccessCount / (float) tokenCount;
	}
	
	public float ambiguousFullParseAccuracy(){
		return (fullParseSuccessCount-ambiguityLevelCounts[1] + ambiguityLevel1ButFalse) / (float) getAmbiguoustokenCount();
	}	
	
	public float overallRootAccuracy(){
		return rootSuccessCount / (float) tokenCount;
	}
	
	public float ambiguousRootAccuracy(){
		return (rootSuccessCount-ambiguityLevelCounts[1] + ambiguityLevel1ButRootFalse - ambiguityLevel0RootTrue) / (float) getAmbiguoustokenCount();
	}			
		
	// Report
	public void printReport(){
		System.out.println("All values are calculated excluding xml tags such as <S> </S>");
		System.out.println(String.format("Overall Full Parse Accuracy 	      : %.2f%%", overallFullParseAccuracy()*100));		
		System.out.println(String.format("Full Parse Accuracy of ambiguous cases    : %.2f%%", ambiguousFullParseAccuracy()*100));
		System.out.println(String.format("Overall Root Accuracy               : %.2f%%", overallRootAccuracy()*100));
		System.out.println(String.format("Ambiguous Root Accuracy             : %.2f%%", ambiguousRootAccuracy()*100));
		System.out.println(String.format("Sentence Count                      : %d", getSentenceCount()));
		System.out.println(String.format("Token Count                         : %d", gettokenCount()));
		System.out.println(String.format("XML Tag Count                       : %d", getTagCount()));
		System.out.println(String.format("Punctuation Count                   : %d", getPuncCount()));		
		System.out.println(String.format("Token Count excl. puncs             : %d", gettokenCount()-getPuncCount()));
		System.out.println(String.format("Distinct surface forms              : %d", distinctsurfaceforms.size()));
		System.out.println(String.format("Distinct morph analysis             : %d", distinctmorphanalysis.size()));
		System.out.println(String.format("Ambiguous Word Count                : %d (%%%.2f)", getAmbiguoustokenCount(), 100 * getAmbiguoustokenCount() / (float) gettokenCount()));
		System.out.println(String.format("Unambiguous Word Count              : %d (%%%.2f)", (getUnambiguoustokenCount()), 100 * (getUnambiguoustokenCount()) / (float) (gettokenCount())));
		System.out.println(String.format("Successfully Dis. Full Parse Count  : %d", fullParseSuccessCount));
		System.out.println(String.format("Successfully Dis. Root Count        : %d", rootSuccessCount));		
		for (int i=0; i<maxAmbiguityLevel+1; i++)
			System.out.println(String.format("Count of words that have %d parses  : %d ", i, ambiguityLevelCounts[i]));
	}	
	// Report
		public void printReport(String reportfilename,String headermessage){
			reportfilename=reportfilename.replace("\\", File.separator);
			Formatter out=null;
			try {
				out = new Formatter(new File(reportfilename));
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			out.format("%s\n\n", headermessage);
			out.format("All values are calculated excluding xml tags such as <S> </S>\n");
			out.format(String.format("Overall Full Parse Accuracy 	      : %.2f\n", overallFullParseAccuracy()*100));		
			out.format(String.format("Full Parse Accuracy of ambiguous cases    : %.2f\n", ambiguousFullParseAccuracy()*100));
			/*out.format(String.format("Overall Root Accuracy               : %.2f%%", overallRootAccuracy()*100));
			out.format(String.format("Ambiguous Root Accuracy             : %.2f%%", ambiguousRootAccuracy()*100));
			out.format(String.format("Sentence Count                      : %d", getSentenceCount()));
			out.format(String.format("Token Count                         : %d", gettokenCount()));
			out.format(String.format("XML Tag Count                       : %d", getTagCount()));
			out.format(String.format("Punctuation Count                   : %d", getPuncCount()));		
			out.format(String.format("Token Count excl. puncs             : %d", gettokenCount()-getPuncCount()));
			out.format(String.format("Distinct surface forms              : %d", distinctsurfaceforms.size()));
			out.format(String.format("Distinct morph analysis             : %d", distinctmorphanalysis.size()));
			out.format(String.format("Ambiguous Word Count                : %d (%%%.2f)", getAmbiguoustokenCount(), 100 * getAmbiguoustokenCount() / (float) gettokenCount()));
			out.format(String.format("Unambiguous Word Count              : %d (%%%.2f)", (getUnambiguoustokenCount()), 100 * (getUnambiguoustokenCount()) / (float) (gettokenCount())));
			out.format(String.format("Successfully Dis. Full Parse Count  : %d", fullParseSuccessCount));
			out.format(String.format("Successfully Dis. Root Count        : %d", rootSuccessCount));
			*/		
			/*for (int i=0; i<maxAmbiguityLevel+1; i++)
				out.format(String.format("Count of words that have %d parses  : %d ", i, ambiguityLevelCounts[i]));
			*/
			out.close();
		}
	public Postagger_evaluator_biber(String disOutputFile, String refFile, String morphFile) {
		this.disOutputFile = disOutputFile;
		this.refFile = refFile;
		this.morphFile = morphFile;
	}
	
	private void openFiles(){
		try {
			if (useMorphFile) {
				morph  = new DataInputStream(new FileInputStream(morphFile));			
				morphR = new BufferedReader(new InputStreamReader(morph));
			}
			dis  = new DataInputStream(new FileInputStream(disOutputFile));			
			disR = new BufferedReader(new InputStreamReader(dis));
			ref  = new DataInputStream(new FileInputStream(refFile));			
			refR = new BufferedReader(new InputStreamReader(ref));			
		}catch (Exception e){//Catch exception if any
			System.err.println("Cannot open files: " + e.getMessage());
		}	
	}
	
	private void closeFiles(){
		try {
			morph.close();
			dis.close();	
			ref.close();
		}catch (Exception e){//Catch exception if any
			System.err.println("Cannot close files: " + e.getMessage());
		}	
	}
	
	private int getMorphAnalysisCount(){
		String line = null;
		try {
			if (singleLineMode) {
				line=morphR.readLine();
				boolean deneme;				
				String parts[]=line.split("\\s+");
				if(parts[1].endsWith("+?"))
				{
					return 0;
				}
				return parts.length-1;					
			} else {
				int count = 0;
				while (!(line=morphR.readLine()).isEmpty()){
					if(line.endsWith("+?"))
						return 0;
					count++;
				}
				return count;
			}
		} catch (Exception e){
			System.out.println("Error : " + e.getMessage());
		}
		return 0;
	}
   public static boolean isMarkup(String s){
	  
			if(s.contains("<DOC")||
				s.contains("<TITLE")||
				s.contains("<S")||
				s.contains("</S")||
				s.contains("<DOC")||
				s.contains("</TITLE")||
				s.contains("</DOC")
			)
				return true;
			return false;
	
   }
   static public String 	TB	= " ";     				// Token Boundary
	static public String	IGB = "^";					// IG Boundary
	static public String	DB =  "\\" + IGB + "DB";	// DB Boundary
	static public String 	TAB	= "+";     	
   public String returnRoot(String s){
		s = s.trim();
		return s.substring(0, indexOfDelimiter(s, TAB));
	}
   static private int indexOfDelimiter(String s, String p){
		int pos = s.indexOf(p);
		return (pos > -1) ? pos : s.length();
	}
	private void processLines(){
		distinctsurfaceforms=new HashSet<String>();
		distinctmorphanalysis=new HashSet<String>();
		try {
			String disResult, correct;
			while ( (disResult = disR.readLine()) != null ){
				correct = refR.readLine();				
				if (disResult.contains("<s>") | disResult.contains("<S>")){
					sentenceCount++;					
				}
				if (isMarkup(disResult)){
					tagCount++;
					if (useMorphFile) 
						getMorphAnalysisCount();
				}
				else{
					tokenCount++;
					distinctsurfaceforms.add(correct.split("\\s+")[0]);
					distinctmorphanalysis.add(correct);
					//System.out.println(correct.split("\\s")[0]);
					int count = -1;
					if (useMorphFile) {
		    			count = getMorphAnalysisCount();
		    			ambiguityLevelCounts[count]++;
		    			updateMaxAmbiguityLevel(count);
		    		}
					
					if (disResult.contains("Punc"))
						puncCount++;
		    		if (correct.equals(disResult))
		    			fullParseSuccessCount++;
		    		else
		    		{
		    			add2confusionmatrix(correct,disResult);
		    			if(count == 1)
		    				ambiguityLevel1ButFalse++;
		    		}
		    		if (returnRoot(correct).equals(returnRoot(disResult))){
		    			rootSuccessCount++;
		    			if(count == 0)
		    				ambiguityLevel0RootTrue++;
		    		}
		    		else
		    		{
		    			if(count == 1)
		    				ambiguityLevel1ButRootFalse++;
		    		}
				}
			}
		} catch (Exception e) {
			System.err.println("Evaluator error: " + e.getMessage());
		}		
	}
	private void add2confusionmatrix(String correct, String disResult) {			
		String tag1=correct.substring(correct.indexOf('+'), correct.length());
		if(tag1.contains("^DB"))
			tag1=correct.substring(correct.lastIndexOf('^')+3, correct.length());
		String tag2=disResult.substring(disResult.indexOf('+'), disResult.length());
		if(tag2.contains("^DB"))
			tag2=disResult.substring(disResult.lastIndexOf('^')+3, disResult.length());		
		String confused=tag1.concat("<->").concat(tag2);		
		if(tag1.equals(tag2))
			confused="IGcount";
		
		boolean ok;
		if(confused.equals("+Num+Card<->+?"))
				ok=true;
			
		Integer f = confusionmatrix.get(confused);
		try{
			confusionmatrix.put(confused,f+1);
		}
		catch(Exception e){
			confusionmatrix.put(confused,1);			
		}
		
	}
	public static boolean isMorphAnalysisInSingleLine(String f){
		try{
			int iMAX = 10; // Just check first 10 lines for an empty line
			DataInputStream in = new DataInputStream(new FileInputStream(f));
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String strLine;
			int i = 0;
			while ( ((strLine = br.readLine()) != null) && (i<iMAX) && (!strLine.trim().isEmpty()))  {
			  i++;
			}
			in.close();
			// if i==10 that means there are no empty lines in the first 10 lines so that means the file content is in singleline mode
			return (i==iMAX) ? true : false;
			}catch (Exception e){//Catch exception if any
				System.err.println("Error: " + e.getMessage());
		}
		return false;
	}   
	public void evaluate(){
		useMorphFile = !morphFile.isEmpty();
		if (useMorphFile)
			singleLineMode = isMorphAnalysisInSingleLine(morphFile);
		openFiles();
		processLines();
		printReport();
		//printConfusionMatrix("confusionmatrix.txt");
		closeFiles();
	}
		
	private void printConfusionMatrix(String reportfilename) {
		reportfilename=reportfilename.replace("\\", File.separator);
		Formatter out=null;
		try {
			out = new Formatter(new File(reportfilename));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		HashMultiMap revmap = new HashMultiMap();		
		
		String element = null;		
		Iterator<String> it =  confusionmatrix.keySet().iterator();
		while(it.hasNext()){
			element=it.next();
			int f=confusionmatrix.get(element);
			revmap.add(f,element);
		}
	/*	Iterator elit = revmap.entrySet().iterator();
		while(elit.hasNext())
		{  
			Map.Entry x = (Map.Entry)elit.next();
			Set<String> values=(Set<String>) x.getValue();
			for(String v:values)
				System.out.println(v);
		}*/
	
        ArrayList<Integer> keys = new ArrayList<Integer>(revmap.keySet());
        for(int i=keys.size()-1; i>=0;i--){
            System.out.printf("%d\t%s\n",keys.get(i),revmap.get(keys.get(i)));
            out.format("%d\t%s\n",keys.get(i),revmap.get(keys.get(i)));
        }
		out.close();
	}
	

	/**
	 * @param args
	 */
	public static void main(String[] args) {
	/*	switch (args.length){
			case 0:
			case 1: System.out.println("Usage : MorphDisEvaluator {MorphologicalOutputFile} DisambiguatorOutputFile ReferenceFile");
					return;
			case 2: (new Postagger_evaluator_biber(args[0], args[1], "")).evaluate();
					break;
			case 3: (new Postagger_evaluator_biber(args[1], args[2], args[0])).evaluate();
					break;															
		}	*/
	/*	String file1 = "./src/PosTagger/Hasim/inputOfl/test.merge";
		String file2 = "./src/PosTagger/Hasim/inputOfl/test.merge.postagged";
		String file3 = "./src/PosTagger/Hasim/inputOfl/test.merge.txt";*/
	//	String file1="C:/Users/gulsen/Dropbox/MorphDisambwithcuneyd/originaldata/";
		
	/*	String originaltestfilename="C:/Users/gulsen/Dropbox/MorphDisambwithcuneyd/originaldata/test";
		String morphoutput = "C:/Users/gulsen/Dropbox/MorphDisambwithcuneyd/originaldata/test";
		String referencefile="C:/Users/gulsen/Dropbox/MorphDisambwithcuneyd/originaldata/test.txt";
		String DisambiguatorOutputFile=originaltestfilename+".postagged";*/
		
		String originaltestfilename="C:/Users/gulsen/Dropbox/MorphDisambwithcuneyd/originaldata/train";
		String morphoutput = "C:/Users/gulsen/Dropbox/MorphDisambwithcuneyd/originaldata/train";
		String referencefile="C:/Users/gulsen/Dropbox/MorphDisambwithcuneyd/originaldata/train.txt";
		String DisambiguatorOutputFile=referencefile;
	
		
		Postagger_evaluator_biber b = new Postagger_evaluator_biber(DisambiguatorOutputFile,referencefile,morphoutput);
		b.evaluate();
	}

}
