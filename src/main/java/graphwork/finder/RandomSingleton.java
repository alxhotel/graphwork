package graphwork.finder;

import java.util.Random;

public class RandomSingleton {

	private static RandomSingleton instance;
	private final Random generator;

	private RandomSingleton() {
		this(null);
	}

	private RandomSingleton(Integer seed) {
		this.generator = (seed != null) ? new Random(seed) : new Random();
	}
	
	public static RandomSingleton getInstance() {
		return getInstance(null);
	}

	public static RandomSingleton getInstance(Integer seed) {
		if (instance == null) {
			instance = (seed != null) ? new RandomSingleton(seed) : new RandomSingleton();
		}
		return instance;
	}

	public int nextInt(int i) {
		return generator.nextInt(i);
	}

	public int nextInt() {
		return generator.nextInt();
	}
	
	public double nextDouble() {
		return generator.nextDouble();
	}

}
