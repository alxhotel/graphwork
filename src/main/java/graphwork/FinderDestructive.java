package graphwork;

import java.util.Collections;
import java.util.List;
import java.util.Map;

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
		
		while (true) {
			// Get known vertices with all known neighbors
			List<Vertex> solutionArray = this.graph.getAllKnownVerticesWithAllKnownNeighbors(this.newGraph);
			if (solutionArray.isEmpty()) {
				// No more vertices available
				
				// Finished: Make sure graph is a tree
				this.newGraph.convertToTree();
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
			
			// Selected vertex
			Vertex selectedVertex = solutionArray.get(0);
			
			// Try to remove the vertex
			Graph test = new Graph(newGraph);
			test.removeVertex(selectedVertex);
			
			if (!test.isConnected()) {
				// Wrong: it broke the graph
				
				// Finished: Make sure graph is a tree
				newGraph.convertToTree();
				break;
			} else {
				// All correct: lets remove it

				// And continue
				newGraph.removeVertex(selectedVertex);
			}
		}
		
		return newGraph;
	}
	
	/**
	 * Method #2
	 * Return the least connected, known & neighbor node
	 */
	private Map.Entry<Vertex, Graph> searchLeastConnectedKnownNeighborNode() {
		return graph.getLeastConnectedKnownNeighborNode(newGraph);
	}
	
}
