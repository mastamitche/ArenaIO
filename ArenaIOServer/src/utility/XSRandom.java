// Odessa 2016

package utility;

import java.util.Random;

/**
 * A subclass of java.util.random that implements the 
 * Xorshift random number generator - which is hella-fast, and seeded
 */

public class XSRandom extends Random {
	private long seed;

	public XSRandom(long seed) {
		this.seed = seed;
	}
	public XSRandom() {
		this.seed = System.currentTimeMillis();
	}

	protected int next(int nbits) {
		long x = seed;
		x ^= (x << 21);
		x ^= (x >>> 35);
		x ^= (x << 4);
		seed = x;
		x &= ((1L << nbits) - 1);
		return (int) x;
	}
	
	private static XSRandom instance;
	
	private static synchronized void  initRNG() {
			if (instance == null)
				instance = new XSRandom();
		}
	
	public static float random(){
		if (instance == null) initRNG();
		return instance.nextFloat();
	}
	public float seededRandom(){
		return nextFloat();
	}
}