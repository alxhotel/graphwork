package graphwork;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FinderIteratedGreedy extends Finder {

	public static final float DEFAULT_RANDOM_PERCENTAGE = 0.5f;
	public static final int DEFAULT_NUM_TRIES = 200;
	
	public FinderIteratedGreedy(Graph graph) {
		super(graph);
	}
	
	@Override
	public Graph getMinimumCoverTree() {
		try {
			// Use default calues
			return this.getMinimumCoverTree(DEFAULT_RANDOM_PERCENTAGE, DEFAULT_NUM_TRIES);
		} catch (Exception ignored) {
			ignored.printStackTrace();
			return null;
		}
	}

	/**
	 * Get minimum cover tree based on iterated greedy
	 * Note:
	 * - It uses de "destructive improved"
	 * - It uses articulation points
	 * 
	 * @param randomPercentage - percentage of the graph to destroy 
	 * @param numOfTries - number of times until we give up
	 * @return Graph
	 * @throws java.lang.Exception
	 */
	public Graph getMinimumCoverTree(float randomPercentage, int numOfTries) throws Exception {
		// Validate params
		if (randomPercentage > 1 || randomPercentage <= 0) {
			throw new Exception("randomPercentage must be between 0 and 1");
		}
		if (numOfTries < 0) {
			throw new Exception("numOfTimes must be bigger than 0");
		}
		
		// Select the whole graph (clone it)
		this.newGraph = new Graph(this.graph);
		
		// Initial graph (least connected)
		this.constructSolution();
		
		// [Debug]
		//if (!this.newGraph.isVertexCoverOf(this.graph)) {
		//	throw new Exception("Something went wrong");
		//}
		//if (!this.newGraph.isConnected()) {
		//	throw new Exception("Something went wrong");
		//}
		
		// Calculate original articulation points
		//List<Vertex> originalArticulationPoints = this.newGraph.getArticulationPoints();
		
		Graph bestGraph = new Graph(this.newGraph);
		float bestWeight = bestGraph.getTotalWeight();
		
		for (int currentNumberOfTimes = 0; currentNumberOfTimes < numOfTries; currentNumberOfTimes++) {
			// Articulation points
			Set<Vertex> totalArticulationPoints = new HashSet<>(this.newGraph.getArticulationPoints());
			// [IDEA]: to increase efficiency, add original articulation points
			//totalArticulationPoints.addAll(originalArticulationPoints);
			int numTotalArticulationPoints = this.newGraph.countKnownVertices(totalArticulationPoints);
			
			// [Debug]
			//if (this.newGraph.isVertexCoverOf(this.graph)) {
			//	System.out.println("IS COVER:");
			//}
			//System.out.println(this.newGraph.getAllVertices());
			
			// Destroy graph (randomly)
			int numOfVerticesToRemove = (int) Math.ceil((this.newGraph.getNumOfVertices() - numTotalArticulationPoints) * randomPercentage);
			List<Vertex> vertices = this.newGraph.getRandomNodesExceptOther(numOfVerticesToRemove, totalArticulationPoints);
			
			// [Debug]
			//System.out.println("Remove N nodes:");
			//System.out.println(numOfVerticesToRemove);
			
			for (Vertex aux : vertices) {
				// [Debug]
				//System.out.println("Remove (num of edges):");
				//System.out.println(aux);
				//System.out.println(this.newGraph.getEdges(aux.getId()).size());
				
				// Recalculate articulation points
				Set<Vertex> newArticulationPoints = new HashSet<>(this.newGraph.getArticulationPoints());
				
				if (!newArticulationPoints.contains(aux)) {
					this.newGraph.removeVertex(aux);
				}
			}
			
			// Construct graph (by most connected)
			List<Vertex> verticesToAdd;
			while (!this.newGraph.isVertexCoverOf(this.graph)) {
				
				// TODO: merge these two lines into one loop
				verticesToAdd = this.graph.getAllVerticesNeighbourToKnownSubgraph(this.newGraph);
				// Order by most connected
				Collections.sort(verticesToAdd, (Vertex t, Vertex t1) -> {
					if (this.graph.getNeighbors(t).size() < this.graph.getNeighbors(t1).size()) {
						return 1;
					} else if (this.graph.getNeighbors(t).size() > this.graph.getNeighbors(t1).size()) {
						return -1;
					} else {
						return 0;
					}
				});
				
				// [Debug]
				//if (verticesToAdd.isEmpty()) {
				//	throw new Exception("Something went wrong");
				//}
				
				// Add most connected unknown neighbour node
				Vertex nextVertex = verticesToAdd.remove(0);
				
				this.newGraph.addVertexFromKnownGraph(nextVertex, this.graph);
				
				// [Debug]
				//System.out.println("Add (num of edges):");
				//System.out.println(nextVertex);
				//if (this.newGraph.getEdges(nextVertex.getId()) == null) {
				//	System.out.println("WROOOOONG AT ADDING");
				//}
				//System.out.println(this.newGraph.getEdges(nextVertex.getId()).size());
			}
			
			// [Debug]
			//System.out.println("Fin:");
			//System.out.println(this.newGraph.getAllVertices());
			
			// [Debug]
			//if (!this.newGraph.isConnected()) {
			//	throw new Exception("Something went wrong");
			//}
			
			// Checkout new try
			float newWeight = this.newGraph.getTotalWeight();
			if (newWeight < bestWeight) {
				bestWeight = newWeight;
				bestGraph = new Graph(this.newGraph);
			}
		}
		
		// Convert to tree
		bestGraph.convertToTree();
		
		return bestGraph;
	}
	
	/**
	 * Remove least connected nodes
	 * @return 
	 */
	private void constructSolution() {
		// [Observation]: It is always a cover and connected
		while (true) {
			// Get known vertices with all known neighbors
			List<Vertex> solutionArray = this.graph.getAllKnownVerticesWithAllKnownNeighbors(this.newGraph);
			if (solutionArray.isEmpty()) {
				// No more vertices available
				
				break;
			}
			
			// Articulation points
			List<Vertex> articulationPoints = this.newGraph.getArticulationPoints();
			
			// Remove articulation points
			solutionArray.removeAll(articulationPoints);
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
			
			// Selected vertex
			Vertex selectedVertex = solutionArray.get(0);
			
			// Remove vertex
			this.newGraph.removeVertex(selectedVertex);
		}

		// Finished: Make sure graph is a tree
		this.newGraph.convertToTree();
	}
	
}
