package core;

import java.util.ArrayList;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class TestWord {
	
	@Test
	public void testParses()
	{
		String line = "Milli milli+Adj mil+Noun+A3sg+Pnon+Nom^DB+Adj+With Mil+Noun+Prop+A3sg+Pnon+Nom^DB+Adj+With Milli+Noun+Prop+A3sg+Pnon+Nom";
		Word word = new Word(line, false);
		
		Parse p1 = new Parse("milli+Adj");
		Parse p2 = new Parse("mil+Noun+A3sg+Pnon+Nom^DB+Adj+With");
		Parse p3 = new Parse("Mil+Noun+Prop+A3sg+Pnon+Nom^DB+Adj+With");
		Parse p4 = new Parse("Milli+Noun+Prop+A3sg+Pnon+Nom");
		
		ArrayList<Parse> parses = new ArrayList<Parse>();
		parses.add(p1);
		parses.add(p2);
		parses.add(p3);
		parses.add(p4);
		
		Assert.assertEquals(word.parses, parses);
	}

}
