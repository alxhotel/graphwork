package graphwork.finder;

import graphwork.graph.Graph;

public abstract class Constructive extends Finder {

	public Constructive(Graph graph) {
		super(graph);
	}
	
	public abstract Graph construct();
	
}
