package graphwork.finder;

import graphwork.graph.Graph;
import graphwork.graph.Vertex;
import java.util.Collections;
import java.util.List;

public class FinderConstructive extends Finder {

	public FinderConstructive(Graph graph) {
		super(graph);
	}

	/**
	 * Get minimum cover tree based on most connected unknown node
	 * @return Graph
	 */
	@Override
	public Graph getMinimumCoverTree() {
		// Create empty graph
		this.newGraph = new Graph();

		while (!this.newGraph.isVertexCoverOf(this.graph)) {
			List<Vertex> verticesToAdd = this.graph.getAllUnknownVerticesNeighbourToKnownSubgraph(this.newGraph);
					
			// Order by most connected
			Collections.sort(verticesToAdd, (Vertex t, Vertex t1) -> {
				if (this.graph.getNeighbors(t).size() < this.graph.getNeighbors(t1).size()) {
					return 1;
				} else if (this.graph.getNeighbors(t).size() > this.graph.getNeighbors(t1).size()) {
					return -1;
				} else {
					return 0;
				}
			});

			// Add most connected unknown neighbour node
			Vertex nextVertex = verticesToAdd.get(0);
			try {
				this.newGraph.addVertexFromKnownSupergraph(nextVertex, this.graph);
			} catch (Exception ignored) {
			}
		}
		
		// Finished: Make sure graph is a minimum spanning tree
		return this.newGraph.convertToTree();
	}
	
}
