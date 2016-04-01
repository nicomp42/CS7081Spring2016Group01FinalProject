package DatabaseUtilities;

import java.util.Random;

public class Config {
	private static Random myRandom = new Random();
	public static Random getRandom() { return myRandom;}
}
