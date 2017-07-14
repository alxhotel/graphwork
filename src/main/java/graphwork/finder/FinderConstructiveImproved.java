package graphwork.finder;

import graphwork.graph.Edge;
import graphwork.graph.Graph;
import graphwork.graph.Vertex;
import java.util.Collections;
import java.util.List;

public class FinderConstructiveImproved extends Constructive {

	public FinderConstructiveImproved(Graph graph) {
		super(graph);
	}

	/**
	 * Get minimum tree cover based on construction
	 * @return Graph
	 */
	@Override
	public Graph getMinimumTreeCover() {
		this.construct();
		
		// Finished: Make sure graph is a minimum spanning tree
		return this.newGraph.getMST();
	}

	/**
	 * Construct a solution by adding most connected to unknown nodes
	 * @return 
	 */
	@Override
	public Graph construct() {
		// Create empty graph
		this.newGraph = new Graph();

		while (!this.newGraph.isVertexCoverOf(this.graph)) {
			// Get unknown vertices adyacent to known subgraph
			List<Vertex> candidateList = this.graph.getAllUnknownVerticesNeighbourToKnownSubgraph(this.newGraph);
					
			// Order by most connected
			Collections.sort(candidateList, (Vertex t, Vertex t1) -> {
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
			Vertex nextVertex = candidateList.get(0);
			try {
				this.newGraph.addVertexFromKnownSupergraph(nextVertex, this.graph);
			} catch (Exception ignored) {
			}
		}
		
		return this.newGraph;
	}
	
}
