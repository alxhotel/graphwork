package graphwork.algo;

import graphwork.graph.Edge;
import graphwork.graph.Graph;
import graphwork.graph.Vertex;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Kruskal algorithm to find the minimum spanning tree
 * for undirected edge-weighted graph
 */
public class KruskalAlgorithm {

	private final int numOfVertices;
	private final List<Map.Entry<Vertex, Edge>> edges;
	
	public KruskalAlgorithm(Graph graph) {
		// Create a copy of all edges
		this.edges = graph.getAllUniqueEdges();
		this.numOfVertices = graph.getNumOfVertices();
	}

	// To represent a subset for "Union-Find"
	private class Subset {
		public int parent;
		public int rank;
		
		public Subset(int parent, int rank) {
			this.parent = parent;
			this.rank = rank;
		}
	}

	public Graph getMinimumSpanningTree() {
		// Initialize subsetMap
		Map<Integer, Subset> subsetMap = new HashMap<>();
		for (Map.Entry<Vertex, Edge> auxEdge : this.edges) {
			int vertexId;
			Subset newSubset;
			
			// Source Vertex
			vertexId = auxEdge.getKey().getId();
			newSubset = new Subset(vertexId, 0);
			subsetMap.put(vertexId, newSubset);
			
			// Destination Vertex
			vertexId = auxEdge.getValue().getDestination().getId();
			newSubset = new Subset(vertexId, 0);
			subsetMap.put(vertexId, newSubset);
		}
		
		// Step 1: Sort by ascending order of weight
		Collections.sort(this.edges, (Map.Entry<Vertex, Edge> t, Map.Entry<Vertex, Edge> t1) -> {
			if (t.getValue().getWeight() < t1.getValue().getWeight()) {
				return -1;
			} else if (t.getValue().getWeight() > t1.getValue().getWeight()) {
				return 1;
			} else {
				return 0;
			}
		});
		
		// Generate minimum spanning tree
		Graph resultGraph = new Graph();
		while (resultGraph.getAllUniqueEdges().size() < this.numOfVertices - 1) {
			// Pick the edge with the least weight
			Map.Entry<Vertex, Edge> nextEdge = this.edges.remove(0);

			int x = this.find(subsetMap, nextEdge.getKey().getId());
			int y = this.find(subsetMap, nextEdge.getValue().getDestination().getId());

			// Check that there isn't any cycles
			if (x != y) {
				resultGraph.addEdge(
					nextEdge.getKey(),
					nextEdge.getValue().getDestination(),
					nextEdge.getValue().getWeight()
				);
				this.union(subsetMap, x, y);
			} else {
				// Do nothing
			}
		}
		
		return resultGraph;
	}
	
	/**
	 * A function to find set of a vertex
	 * Note:
	 * - Uses path compression technique
	 */
	private int find(Map<Integer, Subset> subsetMap, int vertexId) {
		// Find root and make root as parent of i (path compression)
		if (subsetMap.get(vertexId).parent != vertexId) {
			subsetMap.get(vertexId).parent = this.find(subsetMap, subsetMap.get(vertexId).parent);
		}

		return subsetMap.get(vertexId).parent;
	}

	/**
	 * A function that does union of two sets of x and y
	 */
	private void union(Map<Integer, Subset> subsetMap, int x, int y) {
		int xroot = this.find(subsetMap, x);
		int yroot = this.find(subsetMap, y);

		// Attach smaller rank tree under root of high rank tree
		if (subsetMap.get(xroot).rank < subsetMap.get(yroot).rank) {
			subsetMap.get(xroot).parent = yroot;
		} else if (subsetMap.get(xroot).rank > subsetMap.get(yroot).rank) {
			subsetMap.get(yroot).parent = xroot;
		} else {
			// If ranks are same, then make one as root and increment
			// its rank by one
			subsetMap.get(yroot).parent = xroot;
			subsetMap.get(xroot).rank++;
		}
	}
	
}
