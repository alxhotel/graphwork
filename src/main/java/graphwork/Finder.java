package graphwork;

import java.util.AbstractMap;
import java.util.List;
import java.util.Map;

public class Finder {

	// Original graoh
	private final Graph graph;
	
	// Our resulting graph
	private Graph newGraph;
	
	public Finder(Graph graph) {
		this.graph = graph;
	}

	public Graph getMinimumCoverTree() {
		this.newGraph = new Graph();
		
		while (!isFinished()) {
			// 1) Get most connected unknwon node, based on my current "newGraph" + path
			Map.Entry<Vertex, Graph> nodeAndPath = searchMostConnectedNode();
			
			// 2) Get a random unknown node, based on my current "newGraph" + path
			//Map.Entry<Vertex, Graph> nodeAndPath = searchRandomNode();
			
			// Add new path to graph
			if (nodeAndPath.getValue().getSizeOfAdjList() == 0) {
				newGraph.addVertex(nodeAndPath.getKey());
			} else {
				for (Map.Entry<Vertex, Edge> entryEdge : nodeAndPath.getValue().getAllUniqueEdges()) {
					newGraph.addEdge(
							entryEdge.getKey().getId(),
							entryEdge.getValue().getDestination().getId(),
							entryEdge.getValue().getWeight()
					);
				}
			}
		}
		
		return newGraph;
	}
	
	private boolean isFinished() {
		return newGraph.isTree() && newGraph.isVertexCoverOf(graph);
	}
	
	/**
	 * Method #1
	 * Return the most connected, unknown & cheap node
	 */
	private Map.Entry<Vertex, Graph> searchMostConnectedNode() {
		List<Vertex> mostConnectedNodes = graph.getMostConnectedUnknownNodes(newGraph);
		
		Vertex vertexResult = null;
		Graph newPath = new Graph();
		
		// Get cheapest node
		if (newGraph.getAllVertices().isEmpty()) {
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
		}
		
		return new AbstractMap.SimpleEntry<>(vertexResult, newPath);
	}
	
	/**
	 * Method #2
	 * Returns any random, unknown & cheap node
	 */
	private Map.Entry<Vertex, Graph> searchRandomNode() {
		// Get random unknwonnode
		Vertex vertexResult = graph.getRandomUnknownNode(newGraph);
		Graph newPath = new Graph();
		
		if (newGraph.getAllVertices().isEmpty()) {
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
		}
		
		return new AbstractMap.SimpleEntry<>(vertexResult, newPath);
	}
	
}
