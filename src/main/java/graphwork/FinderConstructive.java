package graphwork;

import java.util.Map;

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

		while (!newGraph.isVertexCoverOf(graph)) {
			Map.Entry<Vertex, Graph> nodeAndPath = searchMostConnectedUnknownNeighborNode();

			// Add new path to graph
			if (nodeAndPath.getValue().getNumOfVertices() == 0) {
				// First vertex
				newGraph.addVertex(nodeAndPath.getKey());
			} else {
				// Next vertices
				for (Map.Entry<Vertex, Edge> entryEdge : nodeAndPath.getValue().getAllUniqueEdges()) {
					newGraph.addEdge(
							entryEdge.getKey(),
							entryEdge.getValue().getDestination(),
							entryEdge.getValue().getWeight()
					);
				}
			}
		}
		
		return newGraph;
	}
	
	/**
	 * Method #1
	 * Return the most connected, unknown & neighbor node
	 */
	private Map.Entry<Vertex, Graph> searchMostConnectedUnknownNeighborNode() {
		return graph.getMostConnectedUnknownNeighborNode(newGraph);
	}
	
}
