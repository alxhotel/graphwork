package graphwork.finder;

import graphwork.graph.Graph;
import graphwork.graph.Vertex;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FinderIteratedGreedy extends Finder {

	public static final float DEFAULT_RANDOM_PERCENTAGE = 0.5f;
	public static final int DEFAULT_NUM_TRIES = 300;
	
	public FinderIteratedGreedy(Graph graph) {
		super(graph);
	}
	
	@Override
	public Graph getMinimumCoverTree() {
		try {
			// Use default values
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
	 * @param destroyPercentage - percentage of the graph to destroy 
	 * @param numOfTries - number of times until we give up
	 * @return Graph
	 * @throws java.lang.Exception
	 */
	public Graph getMinimumCoverTree(float destroyPercentage, int numOfTries) throws Exception {
		// Validate params
		if (destroyPercentage <= 0 || destroyPercentage > 1) {
			throw new Exception("randomPercentage must be between 0 and 1");
		}
		if (numOfTries < 0) {
			throw new Exception("numOfTimes must be bigger than 0");
		}
		
		// Select the whole graph (clone it)
		this.newGraph = new Graph(this.graph);
		
		// Initial graph (construct by least connected)
		this.constructSolution();
		
		// Calculate original articulation points
		//List<Vertex> originalArticulationPoints = this.newGraph.getArticulationPoints();
		
		Graph bestGraph = new Graph(this.newGraph);
		float bestMSTWeight = bestGraph.getTotalWeightOfMST();
		
		for (int currentNumberOfTimes = 0; currentNumberOfTimes < numOfTries; currentNumberOfTimes++) {
			// Articulation points
			Set<Vertex> totalArticulationPoints = new HashSet<>(this.newGraph.getArticulationPoints());
			// [IDEA]: to increase efficiency, add original articulation points
			//totalArticulationPoints.addAll(originalArticulationPoints);
			int numTotalArticulationPoints = this.newGraph.countKnownVertices(totalArticulationPoints);
			
			// Destroy graph (randomly)
			int numOfVerticesToRemove = (int) Math.ceil((this.newGraph.getNumOfVertices() - numTotalArticulationPoints) * destroyPercentage);
			List<Vertex> vertices = this.newGraph.getRandomNodesExceptOther(numOfVerticesToRemove, totalArticulationPoints);

			for (Vertex auxVertex: vertices) {
				// Recalculate articulation points
				Set<Vertex> newArticulationPoints = new HashSet<>(this.newGraph.getArticulationPoints());
				
				if (!newArticulationPoints.contains(auxVertex)) {
					this.newGraph.removeVertex(auxVertex);
				}
			}
			
			// Construct graph (by most connected)
			while (!this.newGraph.isVertexCoverOf(this.graph)) {
				List<Vertex> verticesToAdd = this.graph.getAllUnknownVerticesNeighbourToKnownSubgraph(this.newGraph);
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
				
				// Add most connected unknown neighbour node
				Vertex nextVertex = verticesToAdd.get(0);
				this.newGraph.addVertexFromKnownSupergraph(nextVertex, this.graph);
			}
			
			// Checkout new try
			float newMSTWeight = this.newGraph.getTotalWeightOfMST();
			if (newMSTWeight < bestMSTWeight) {
				bestMSTWeight = newMSTWeight;
				bestGraph = new Graph(this.newGraph);
			}
		}
		
		// Finished: Generate the minimum spanning tree
		return bestGraph.getMST();
	}
	
	/**
	 * Construct solution by removing the least connected nodes
	 */
	protected void constructSolution() {
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
			
			// Remove selected vertex
			Vertex selectedVertex = solutionArray.get(0);
			this.newGraph.removeVertex(selectedVertex);
		}
	}
	
}
