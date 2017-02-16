package graphwork;

import java.util.AbstractMap;
import java.util.Map;

public class Finder {

	public static final int TYPE_MOST_CONNECTED_CONSTRUCTIVE = 1;
	public static final int TYPE_LEAST_CONNECTED_DESTRUCTIVE = 2;
	public static final int TYPE_RANDOM = 3;
	
	// Original graoh
	private final Graph graph;
	
	// Our resulting graph
	private Graph newGraph;
	
	public Finder(Graph graph) {
		this.graph = graph;
	}

	public Graph getMinimumCoverTree() {
		// By default
		return getMinimumCoverTree(TYPE_MOST_CONNECTED_CONSTRUCTIVE);
	}
	
	public Graph getMinimumCoverTree(int type) {
		this.newGraph = new Graph();
		
		// Select strategy
		switch (type) {
			case TYPE_MOST_CONNECTED_CONSTRUCTIVE:
				// 1) Get most connected unknwon node, based on my current "newGraph" + path
				
				while (!isFinished()) {
					Map.Entry<Vertex, Graph> nodeAndPath = searchMostConnectedUnknownNeighborNode();
					
					// Add new path to graph
					if (nodeAndPath.getValue().getNumOfVertices() == 0) {
						// Fisrt vertex
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
				
				break;
			case TYPE_LEAST_CONNECTED_DESTRUCTIVE:
				// TODO
				// 2) Get most connected unknwon node, based on my current "newGraph" + path
				
				// Select the whole graph (clone it)
				this.newGraph = new Graph(this.graph);
				
				while (!isFinished()) {
					Map.Entry<Vertex, Graph> nodeAndPath = searchLeastConnectedKnownNeighborNode();
					
					//System.out.println("V: " + newGraph.getNumOfVertices());
					
					//System.out.println(newGraph.isVertexCoverOf(graph));
					//System.out.println(newGraph.isConnected());
					
					// Remove from knownGraph
					if (nodeAndPath.getKey() != null) {
						// Try to remove the vertex
						Graph test = new Graph(newGraph);
						test.removeVertex(nodeAndPath.getKey());
						
						if (!test.isVertexCoverOf(graph) || !test.isConnected()) {
							// Make sure graph is a tree
							newGraph.convertToTree();
						} else {
							// All correct, lets remove it
							newGraph.removeVertex(nodeAndPath.getKey());
						}
						
					} else {
						// No more vertices avilable
						
						// Make sure graph is a tree
						newGraph.convertToTree();
					}
				}
				
				break;
			case TYPE_RANDOM:
				// 3) Get a random unknown node, based on my current "newGraph" + path
				//nodeAndPath = searchRandomNode();
				break;
			default:
				// 1) Get most connected unknwon node, based on my current "newGraph" + path
				//nodeAndPath = searchMostConnectedNode();
				break;
		}
		
		return newGraph;
	}
	
	private boolean isFinished() {
		return newGraph.isTree() && newGraph.isVertexCoverOf(graph);
	}
	
	/**
	 * Method #1
	 * Return the most connected, unknown & neighbor node
	 */
	private Map.Entry<Vertex, Graph> searchMostConnectedUnknownNeighborNode() {
		Map.Entry<Vertex, Graph> mostConnectedNode = graph.getMostConnectedUnknownNeighborNode(newGraph);
		
		// Get any node
		Vertex vertexResult = mostConnectedNode.getKey();
		Graph newPath = mostConnectedNode.getValue();
		
		// Get cheapest node
		/*if (newGraph.getAllVertices().isEmpty()) {
			// There is no path
			// Get any node
			vertexResult = mostConnectedNodes.get(0);
		} else {
			float currentMinDistance = Float.MAX_VALUE;
			for (Vertex possibleVertex : mostConnectedNodes) {
				DijkstraAlgorithm alg = new DijkstraAlgorithm(graph);
				alg.execute(possibleVertex);

				for (Vertex knownVertex : newGraph.getAllVertices()) {
					if (alg.getShortestDistance(knownVertex) < currentMinDistance) {
						currentMinDistance = alg.getShortestDistance(knownVertex);
						vertexResult = possibleVertex;
						newPath = alg.getShortestPath(knownVertex);
					}
				}
			}
		}*/
		
		return new AbstractMap.SimpleEntry<>(vertexResult, newPath);
	}
	
	/**
	 * Method #2
	 * Return the least connected, known & neighbor node
	 */
	private Map.Entry<Vertex, Graph> searchLeastConnectedKnownNeighborNode() {
		return graph.getLeastConnectedKnownNeighborNode(newGraph);
	}
	
	/**
	 * Method #3
	 * Returns any random, unknown & cheap node
	 */
	private Map.Entry<Vertex, Graph> searchRandomNode() {
		// Get random unknwonnode
		Map.Entry<Vertex, Graph> randomNode = graph.getRandomUnknownNeighborNode(newGraph);
		
		Vertex vertexResult = randomNode.getKey();
		Graph newPath = randomNode.getValue();
		
		/*if (newGraph.getAllVertices().isEmpty()) {
			// There is no path
			
		} else {
			// Calculate shortest path
			DijkstraAlgorithm alg = new DijkstraAlgorithm(graph);
			alg.execute(vertexResult);
			
			float currentMinDistance = Float.MAX_VALUE;
			for (Vertex knownVertex : newGraph.getAllVertices()) {
				if (alg.getShortestDistance(knownVertex) < currentMinDistance) {
					currentMinDistance = alg.getShortestDistance(knownVertex);
					newPath = alg.getShortestPath(knownVertex);
				}
			}
		}*/
		
		return new AbstractMap.SimpleEntry<>(vertexResult, newPath);
	}
	
}
