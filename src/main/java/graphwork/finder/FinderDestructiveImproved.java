package graphwork.finder;

import graphwork.graph.Edge;
import graphwork.graph.Graph;
import graphwork.graph.Vertex;
import java.util.Collections;
import java.util.List;

public class FinderDestructiveImproved extends Constructive {

	public FinderDestructiveImproved(Graph graph) {
		super(graph);
	}

	/**
	 * Get minimum cover tree based on destruction
	 * @return Graph
	 */
	@Override
	public Graph getMinimumTreeCover() {
		this.construct();
		
		// Finished: Make sure graph is a minimum spanning tree
		return this.newGraph.getMST();
	}

	/**
	 * Construct a solution based on least connected nodes
	 * IMPROVED with:
	 *	- Articulation points
	 * 
	 * Note: by they way we select the next node, is always a vertex cover
	 * 
	 * @return 
	 */
	@Override
	public Graph construct() {
		// Select the whole graph (clone it)
		this.newGraph = new Graph(this.graph);
		
		// Get known vertices with all known neighbors
		List<Vertex> candidateList = this.graph.getAllKnownVerticesWithAllKnownNeighbors(this.newGraph);
		
		if (!candidateList.isEmpty()) {
			// Order by least connected
			Collections.sort(candidateList, (Vertex t, Vertex t1) -> {
				if (this.newGraph.getNeighbors(t).size() < this.newGraph.getNeighbors(t1).size()) {
					return -1;
				} else if (this.newGraph.getNeighbors(t).size() > this.newGraph.getNeighbors(t1).size()) {
					return 1;
				} else {
					return 0;
				}
			});

			do {
				// Articulation points
				List<Vertex> articulationPoints = this.newGraph.getArticulationPoints();

				// Remove articulation points
				candidateList.removeAll(articulationPoints);
				if (candidateList.isEmpty()) {
					// No more vertices available

					break;
				}

				// Selected vertex from the list
				Vertex nextVertex = candidateList.get(0);
				candidateList.remove(0);
				
				// Hack: Remove neighours of selectedVertex from solutionArray
				List<Edge> neighbours = this.newGraph.getNeighbors(nextVertex);
				for (Edge edgeAux : neighbours) {
					int index = candidateList.indexOf(edgeAux.getDestination());
					if (index > -1) {
						candidateList.remove(index);	
					}
				}
				
				// Remove selected vertex
				this.newGraph.removeVertex(nextVertex);
			} while (!candidateList.isEmpty());
		}
		
		return this.newGraph;
	}
	
}
