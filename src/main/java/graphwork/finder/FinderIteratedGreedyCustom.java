package graphwork.finder;

import graphwork.algo.KruskalAlgorithm;
import graphwork.graph.Edge;
import graphwork.graph.Graph;
import graphwork.graph.Vertex;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FinderIteratedGreedyCustom extends Finder {

	public static final float DEFAULT_RANDOM_PERCENTAGE = 0.5f;
	public static final int DEFAULT_NUM_TRIES = 300;
	
	public IGType type;
	
	public enum IGType {
		CONSTRUCTIVE_REMOVE_RANDOM_ADD_GREEDY(1),
		CONSTRUCTIVE_ADD_RANDOM_REMOVE_GREEDY(2),
		DESTRUCTIVE_REMOVE_RANDOM_ADD_GREEDY(3),
		DESTRUCTIVE_ADD_RANDOM_REMOVE_GREEDY(4);
				
		private final int num;

		private IGType(int num) {
			this.num = num;
		}
	}
	
	public FinderIteratedGreedyCustom(Graph graph) {
		super(graph);
		this.type = IGType.CONSTRUCTIVE_REMOVE_RANDOM_ADD_GREEDY;
	}
	
	public FinderIteratedGreedyCustom(Graph graph, IGType type) {
		super(graph);
		this.type = type;
	}
	
	@Override
	public Graph getMinimumCoverTree() {
		try {
			// Use default values
			return this.getMinimumCoverTree(
					DEFAULT_RANDOM_PERCENTAGE,
					DEFAULT_NUM_TRIES
			);
		} catch (Exception ignored) {
			ignored.printStackTrace();
			return null;
		}
	}

	/**
	 * Get minimum cover tree based on iterated greedy
	 * Note:
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
			throw new Exception("destroyPercentage must be between 0 and 1");
		}
		if (numOfTries < 0) {
			throw new Exception("numOfTimes must be bigger than 0");
		}
		
		// Select the whole graph (clone it)
		this.newGraph = new Graph(this.graph);
		
		// Initial graph (least connected)
		if (this.type == IGType.CONSTRUCTIVE_REMOVE_RANDOM_ADD_GREEDY
				|| this.type == IGType.CONSTRUCTIVE_ADD_RANDOM_REMOVE_GREEDY) {
			this.constructSolutionConstructive();
		} else {
			this.constructSolutionDestructive();
		}
		
		Graph bestGraph = new Graph(this.newGraph);
		float bestMSTWeight = bestGraph.getTotalWeightOfMST();
		
		for (int currentNumberOfTimes = 0; currentNumberOfTimes < numOfTries; currentNumberOfTimes++) {
			if (this.type == IGType.CONSTRUCTIVE_REMOVE_RANDOM_ADD_GREEDY
					|| this.type == IGType.DESTRUCTIVE_REMOVE_RANDOM_ADD_GREEDY) {
				// Articulation points
				Set<Vertex> totalArticulationPoints = new HashSet<>(this.newGraph.getArticulationPoints());
				int numTotalArticulationPoints = totalArticulationPoints.size();
				
				// Destroy graph randomly
				int numOfVerticesToRemove = (int) Math.ceil((this.newGraph.getNumOfVertices() - numTotalArticulationPoints) * destroyPercentage);
				List<Vertex> vertices = this.newGraph.getRandomNodesExceptOther(numOfVerticesToRemove, totalArticulationPoints);

				for (Vertex aux : vertices) {
					// Recalculate articulation points
					Set<Vertex> newArticulationPoints = new HashSet<>(this.newGraph.getArticulationPoints());

					if (!newArticulationPoints.contains(aux)) {
						this.newGraph.removeVertex(aux);
					}
				}
				
				// Construct graph (by most connected)
				while (!this.newGraph.isVertexCoverOf(this.graph)) {
					List<Vertex> verticesToAdd = this.graph.getAllUnknownVerticesNeighbourToKnownSubgraph(this.newGraph);
					
					// Order by most connected to unknown nodes
					Collections.sort(verticesToAdd, (Vertex t, Vertex t1) -> {
						int unknownNeighboursA = 0;
						for (Edge edgeAux : this.graph.getNeighbors(t)) {
							if (!this.newGraph.existsVertex(edgeAux.getDestination())) {
								unknownNeighboursA++;
							}
						}

						int unknownNeighboursB = 0;
						for (Edge edgeAux : this.graph.getNeighbors(t1)) {
							if (!this.newGraph.existsVertex(edgeAux.getDestination())) {
								unknownNeighboursB++;
							}
						}

						if (unknownNeighboursA > unknownNeighboursB) {
							return -1;
						} else if (unknownNeighboursA < unknownNeighboursB) {
							return 1;
						} else {
							return 0;
						}
					});

					// Add most connected unknown neighbour node
					Vertex nextVertex = verticesToAdd.get(0);
					this.newGraph.addVertexFromKnownSupergraph(nextVertex, this.graph);
				}
				
			} else {
				// CONSTRUCTIVE_ADD_RANDOM_REMOVE_GREEDY
				// DESTRUCTIVE_ADD_RANDOM_REMOVE_GREEDY
				
				// Construct graph randomly
				List<Vertex> candidateVertices = this.graph.getAllUnknownVerticesNeighbourToKnownSubgraph(this.newGraph);
				
				int numOfVerticesToRemove = (int) Math.ceil(candidateVertices.size() * destroyPercentage);
				RandomSingleton generator = RandomSingleton.getInstance();
				for (int i = 0; i < numOfVerticesToRemove; i++) {
					int index = generator.nextInt(candidateVertices.size());
					Vertex auxVertex = candidateVertices.remove(index);
					
					this.newGraph.addVertexFromKnownSupergraph(auxVertex, this.graph);
				}
				
				// Destroy graph (by least connected)
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
						if (this.graph.getNeighbors(t).size() < this.graph.getNeighbors(t1).size()) {
							return -1;
						} else if (this.graph.getNeighbors(t).size() > this.graph.getNeighbors(t1).size()) {
							return 1;
						} else {
							return 0;
						}
					});

					// Add most connected unknown neighbour node
					Vertex selectedVertex = solutionArray.get(0);
					this.newGraph.removeVertex(selectedVertex);
				}
			}
			
			// Checkout new try
			float newMSTWeight = this.newGraph.getTotalWeightOfMST();
			if (newMSTWeight < bestMSTWeight) {
				bestMSTWeight = newMSTWeight;
				bestGraph = new Graph(this.newGraph);
			}
		}
		
		// Finished: Generate the minimum spanning tree
		KruskalAlgorithm krustkalAlgo = new KruskalAlgorithm(bestGraph);
		Graph resultGraph = krustkalAlgo.getMinimumSpanningTree();
		
		return resultGraph;
	}
	
	/**
	 * Adds most connected nodes
	 * @throws Exception
	 */
	protected void constructSolutionConstructive() throws Exception {
		while (!this.newGraph.isVertexCoverOf(this.graph)) {
			List<Vertex> verticesToAdd = this.graph.getAllUnknownVerticesNeighbourToKnownSubgraph(this.newGraph);

			// Order by most connected to unknown nodes
			Collections.sort(verticesToAdd, (Vertex t, Vertex t1) -> {
				int unknownNeighboursA = 0;
				for (Edge edgeAux : this.graph.getNeighbors(t)) {
					if (!this.newGraph.existsVertex(edgeAux.getDestination())) {
						unknownNeighboursA++;
					}
				}

				int unknownNeighboursB = 0;
				for (Edge edgeAux : this.graph.getNeighbors(t1)) {
					if (!this.newGraph.existsVertex(edgeAux.getDestination())) {
						unknownNeighboursB++;
					}
				}

				if (unknownNeighboursA > unknownNeighboursB) {
					return -1;
				} else if (unknownNeighboursA < unknownNeighboursB) {
					return 1;
				} else {
					return 0;
				}
			});

			// Add most connected unknown neighbour node
			Vertex nextVertex = verticesToAdd.get(0);
			this.newGraph.addVertexFromKnownSupergraph(nextVertex, this.graph);
		}
	}
	
	/**
	 * Remove least connected nodes
	 */
	protected void constructSolutionDestructive() {
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
