package graphwork.finder;

import graphwork.graph.Graph;
import graphwork.graph.Vertex;
import java.util.Collections;
import java.util.List;

public class FinderConstructive extends Constructive {

	public FinderConstructive(Graph graph) {
		super(graph);
	}

	/**
	 * Get minimum cover tree based on construction
	 * @return Graph
	 */
	@Override
	public Graph getMinimumTreeCover() {
		this.construct();
		
		// Finished: Make sure graph is a minimum spanning tree
		return this.newGraph.getMST();
	}

	/**
	 * Construct a solution by adding most connected
	 * @return 
	 */
	@Override
	public Graph construct() {
		// Create empty graph
		this.newGraph = new Graph();

		while (!this.newGraph.isVertexCoverOf(this.graph)) {
			// Get unknown vertices adyacent to knwon subgraph
			List<Vertex> candidateList = this.graph.getAllUnknownVerticesNeighbourToKnownSubgraph(this.newGraph);
					
			// Order by most connected
			Collections.sort(candidateList, (Vertex t, Vertex t1) -> {
				if (this.graph.getNeighbors(t).size() < this.graph.getNeighbors(t1).size()) {
					return 1;
				} else if (this.graph.getNeighbors(t).size() > this.graph.getNeighbors(t1).size()) {
					return -1;
				} else {
					return 0;
				}
			});

			// Add most connected unknown neighbour node
			Vertex nextVertex = candidateList.get(0);
			try {
				this.newGraph.addVertexFromKnownSupergraph(nextVertex, this.graph);
			} catch (Exception ignored) {
			}
		}
		
		return this.newGraph;
	}
	
}
