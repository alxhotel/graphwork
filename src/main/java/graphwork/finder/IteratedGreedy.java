package graphwork.finder;

import static graphwork.finder.FinderIteratedGreedyCustom.DEFAULT_NUM_TRIES;
import static graphwork.finder.FinderIteratedGreedyCustom.DEFAULT_RANDOM_PERCENTAGE;
import graphwork.graph.Graph;

public abstract class IteratedGreedy extends Finder {

	public static final float DEFAULT_RANDOM_PERCENTAGE = 0.5f;
	public static final int DEFAULT_NUM_TRIES = 300;
	public static final int DEFAULT_SEED = 24;
	
	public IteratedGreedy(Graph graph) {
		super(graph);
	}
	
	public abstract Graph initialize();
	
	public abstract Graph construct();
	
	public abstract Graph destruct();
	
	public abstract Graph getMinimumTreeCover(float destroyPercentage, int numOfTries) throws Exception;
	
	@Override
	public Graph getMinimumTreeCover() {
		try {
			// Use default values
			return this.getMinimumTreeCover(
				DEFAULT_RANDOM_PERCENTAGE,
				DEFAULT_NUM_TRIES
			);
		} catch (Exception ignored) {
			ignored.printStackTrace();
			return null;
		}
	}
	
}
