package core;

import java.util.ArrayList;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.junit.Assert;

@RunWith(JUnit4.class)
public class TestParse {
	
	@Test
	public void testRoot()
	{
		String analysis = "mil+Noun+A3sg+Pnon+Nom^DB+Adj+With";
		Parse parse = new Parse(analysis);
		
		Assert.assertEquals(parse.root, "mil");	
	}
	
	@Test
	public void testAnalysis()
	{
		String analysis = "mil+Noun+A3sg+Pnon+Nom^DB+Adj+With";
		Parse parse = new Parse(analysis);
		
		Assert.assertEquals(parse.analysis, "Noun+A3sg+Pnon+Nom^DB+Adj+With");
	}
	
	@Test
	public void testIGs()
	{
		String analysis = "mil+Noun+A3sg+Pnon+Nom^DB+Adj+With";
		Parse parse = new Parse(analysis);
		
		ArrayList<String> ig1 = new ArrayList<String>();
		ig1.add("Noun");
		ig1.add("A3sg");
		ig1.add("Pnon");
		ig1.add("Nom");
		
		ArrayList<String> ig2 = new ArrayList<String>();
		ig2.add("Adj");
		ig2.add("With");
		
		ArrayList<ArrayList<String>> igs = new ArrayList<ArrayList<String>>();
		igs.add(ig1);
		igs.add(ig2);
		
		Assert.assertEquals(parse.igs, igs);
	}

}
