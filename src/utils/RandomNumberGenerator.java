package utils;

import java.util.Random;

public class RandomNumberGenerator {
	static Random rand = null;
	
	public static int getRandomNumber(int bound)
	{
		if(rand == null)
			rand = new Random();
		
		return rand.nextInt(bound);
	}

}
