package graphwork.finder;

import graphwork.graph.Graph;
import graphwork.graph.Vertex;
import java.util.Collections;
import java.util.List;

public class FinderDestructive extends Finder {

	public FinderDestructive(Graph graph) {
		super(graph);
	}
	
	/**
	 * Get minimum cover tree based on least connected known node
	 * 
	 * Note: by they way we select the next node, is always a vertex cover
	 * 
	 * @return Graph
	 */
	@Override
	public Graph getMinimumCoverTree() {
		// Select the whole graph (clone it)
		this.newGraph = new Graph(this.graph);
		
		// [Observation]: It is always a cover and connected
		while (true) {
			// Get known vertices with all known neighbors
			List<Vertex> solutionArray = this.graph.getAllKnownVerticesWithAllKnownNeighbors(this.newGraph);
			if (solutionArray.isEmpty()) {
				// No more vertices available
				
				break;
			}
			
			// Order by least connected
			Collections.sort(solutionArray, (Vertex t, Vertex t1) -> {
				if (this.newGraph.getNeighbors(t).size() < this.newGraph.getNeighbors(t1).size()) {
					return -1;
				} else if (this.newGraph.getNeighbors(t).size() > this.newGraph.getNeighbors(t1).size()) {
					return 1;
				} else {
					return 0;
				}
			});
			
			// Selected vertex from the list
			Graph test;
			while (!solutionArray.isEmpty()) {
				Vertex selectedVertex = solutionArray.get(0);
			
				// Try to remove the vertex
				test = new Graph(this.newGraph);
				test.removeVertex(selectedVertex);

				if (!test.isConnected()) {
					// Wrong: it broke the graph

					// Try the next one
					solutionArray.remove(0);
				} else {
					// All correct: lets remove it

					// And continue
					newGraph.removeVertex(selectedVertex);
					break;
				}
			}
			
			if (solutionArray.isEmpty()) {
				// None of the available vertices were suitable
				
				break;
			}
		}
		
		// Finished: Make sure graph is a minimum spanning tree
		return this.newGraph.getMST();
	}
	
}
