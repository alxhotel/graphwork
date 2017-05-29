package graphwork.finder;

import graphwork.graph.Graph;
import graphwork.graph.Vertex;
import java.util.AbstractMap;
import java.util.Map;

public class FinderRandom extends Finder {

	public FinderRandom(Graph graph) {
		super(graph);
	}
	
	@Override
	public Graph getMinimumCoverTree() {
		// 3) Get a random unknown node, based on my current "newGraph" + path
		//nodeAndPath = searchRandomNode();
		
		return newGraph;
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
