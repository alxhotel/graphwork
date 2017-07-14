package graphwork.finder;

import graphwork.graph.Edge;
import graphwork.graph.Graph;
import graphwork.graph.Vertex;
import java.util.Collections;
import java.util.List;

public class FinderDestructive extends Constructive {

	public FinderDestructive(Graph graph) {
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
	 * Construct a solution by removing least connected known node
	 * 
	 * Note: by they way we select the next node, is always a vertex cover
	 * 
	 * @return Graph
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
			
			Graph test;
			do {
				// Selected vertex from the list
				Vertex nextVertex = candidateList.get(0);
				candidateList.remove(0);
				
				// Try to remove the vertex
				test = new Graph(this.newGraph);
				test.removeVertex(nextVertex);
				
				if (test.isConnected()) {
					// All correct: lets remove it
					
					// Hack: Remove neighours of selectedVertex from solutionArray
					List<Edge> neighbours = this.newGraph.getNeighbors(nextVertex);
					for (Edge edgeAux : neighbours) {
						int index = candidateList.indexOf(edgeAux.getDestination());
						if (index > -1) {
							candidateList.remove(index);	
						}
					}
										
					// And continue
					this.newGraph.removeVertex(nextVertex);
				}
			} while (!candidateList.isEmpty());
		}
		
		return this.newGraph;
	}
	
}
