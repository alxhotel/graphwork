package graphwork;

public abstract class Finder {
	
	// Original graoh
	protected final Graph graph;
	
	// Our resulting graph
	protected Graph newGraph;
	
	public Finder(Graph graph) {
		this.graph = graph;
	}
	
	/**
	 * Get the minimum cover tree for the given graph
	 * @return Graph
	 */
	public abstract Graph getMinimumCoverTree();
	
}
