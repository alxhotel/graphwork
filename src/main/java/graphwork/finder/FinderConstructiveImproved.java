package graphwork.finder;

import graphwork.graph.Edge;
import graphwork.graph.Graph;
import graphwork.graph.Vertex;
import java.util.Collections;
import java.util.List;

public class FinderConstructiveImproved extends Finder {

	public FinderConstructiveImproved(Graph graph) {
		super(graph);
	}

	/**
	 * Get minimum cover tree based on most connected to unknown node
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
				int unknownNeighboursA = 0;
				for (Edge edgeAux : this.graph.getNeighbors(t)) {
					if (!this.newGraph.existsVertex(edgeAux.getDestination())) {
						unknownNeighboursA++;
					}
				}

				int unknownNeighboursB = 0;
				for (Edge edgeAux : this.graph.getNeighbors(t1)) {
					if (!this.newGraph.existsVertex(edgeAux.getDestination())) {
						unknownNeighboursB++;
					}
				}

				if (unknownNeighboursA > unknownNeighboursB) {
					return -1;
				} else if (unknownNeighboursA < unknownNeighboursB) {
					return 1;
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
