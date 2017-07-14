package graphwork.finder;

import graphwork.graph.Edge;
import graphwork.graph.Graph;
import graphwork.graph.Vertex;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FinderIteratedGreedyCustom extends IteratedGreedy {
	
	public IGType type;
	
	// Internal variables
	private float destroyPercentage = 0;
	private int numOfTries = 0;
	private List<Vertex> removedVertices;
	private int randomSeed;
	
	public enum IGType {
		CONSTRUCTIVE_REMOVE_RANDOM_ADD_GREEDY(1),
		CONSTRUCTIVE_ADD_RANDOM_REMOVE_GREEDY(2),
		DESTRUCTIVE_REMOVE_RANDOM_ADD_GREEDY(3),
		DESTRUCTIVE_ADD_RANDOM_REMOVE_GREEDY(4);
		
		private final int num;

		private IGType(int num) {
			this.num = num;
		}
		
		public boolean equals(IGType other) {
			return this.num == other.num;
		}
	}
	
	public FinderIteratedGreedyCustom(Graph graph) {
		this(graph, IGType.CONSTRUCTIVE_REMOVE_RANDOM_ADD_GREEDY);
	}
	
	public FinderIteratedGreedyCustom(Graph graph, IGType type) {
		this(graph, type, DEFAULT_SEED);
	}
	
	public FinderIteratedGreedyCustom(Graph graph, IGType type, int randomSeed) {
		super(graph);
		this.type = type;
		this.randomSeed = randomSeed;
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
	@Override
	public Graph getMinimumTreeCover(float destroyPercentage, int numOfTries) throws Exception {
		// Validate params
		if (destroyPercentage <= 0 || destroyPercentage > 1) {
			throw new Exception("destroyPercentage must be between 0 and 1");
		}
		if (numOfTries < 0) {
			throw new Exception("numOfTimes must be bigger than 0");
		}
		
		this.destroyPercentage = destroyPercentage;
		this.numOfTries = numOfTries;
		
		// Construct initial graph
		this.initialize();
		
		// Calculate original articulation points
		//List<Vertex> originalArticulationPoints = this.newGraph.getArticulationPoints();
		
		// Save best graph
		Graph bestGraph = new Graph(this.newGraph);
		float bestMSTWeight = bestGraph.getTotalWeightOfMST();
		
		for (int currentNumberOfTimes = 0; currentNumberOfTimes < this.numOfTries; currentNumberOfTimes++) {
			if (this.type.equals(IGType.CONSTRUCTIVE_REMOVE_RANDOM_ADD_GREEDY)
					|| this.type.equals(IGType.DESTRUCTIVE_REMOVE_RANDOM_ADD_GREEDY)) {
				// Destruction phase
				this.destruct();

				// Construction phase
				this.construct();
			} else {
				// CONSTRUCTIVE_ADD_RANDOM_REMOVE_GREEDY
				// DESTRUCTIVE_ADD_RANDOM_REMOVE_GREEDY
							
				// Construction phase
				this.construct();
				
				// Destruction phase
				this.destruct();
			}
			
			// Checkout new try
			float newMSTWeight = this.newGraph.getTotalWeightOfMST();
			if (newMSTWeight < bestMSTWeight) {
				bestMSTWeight = newMSTWeight;
				bestGraph = new Graph(this.newGraph);
			}
		}
		
		// Finished: Generate the minimum spanning tree
		Graph resultGraph = bestGraph.getMST();
		
		return resultGraph;
	}
	
	/**
	 * Create a initial solution
	 * @return Graph
	 */
	@Override
	public Graph initialize() {
		if (this.type.equals(IGType.CONSTRUCTIVE_REMOVE_RANDOM_ADD_GREEDY)
				|| this.type.equals(IGType.CONSTRUCTIVE_ADD_RANDOM_REMOVE_GREEDY)) {
			FinderConstructiveImproved finderC2 = new FinderConstructiveImproved(this.graph);
			this.newGraph = finderC2.construct();
		} else {
			// DESTRUCTIVE_REMOVE_RANDOM_ADD_GREEDY
			// DESTRUCTIVE_ADD_RANDOM_REMOVE_GREEDY
			FinderDestructiveImproved finderD2 = new FinderDestructiveImproved(this.graph);
			this.newGraph = finderD2.construct();
		}
		
		return this.newGraph;
	}
	
	/**
	 * Adds most connected nodes
	 * @return Graph
	 */
	@Override
	public Graph construct() {
		if (this.type.equals(IGType.CONSTRUCTIVE_REMOVE_RANDOM_ADD_GREEDY)
				|| this.type.equals(IGType.DESTRUCTIVE_REMOVE_RANDOM_ADD_GREEDY)) {
			// Construct graph greedy (by most connected)
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

				// [IMPROVEMENT]: Put the currently removed nodes at the end of the list
				//List<Vertex> common = new ArrayList<>(verticesToAdd);
				//common.retainAll(verticesRemoved);
				//verticesToAdd.removeAll(common);
				//verticesToAdd.addAll(common);

				// Add most connected unknown neighbour node
				Vertex nextVertex = verticesToAdd.get(0);
				try {
					this.newGraph.addVertexFromKnownSupergraph(nextVertex, this.graph);
				} catch (Exception ex) {
				}
			}
		} else {
			// CONSTRUCTIVE_ADD_RANDOM_REMOVE_GREEDY
			// DESTRUCTIVE_ADD_RANDOM_REMOVE_GREEDY
			
			// Construct graph randomly
			List<Vertex> candidateVertices = this.graph.getAllUnknownVerticesNeighbourToKnownSubgraph(this.newGraph);

			//List<Vertex> verticesAdded = new ArrayList<>();

			int numOfVerticesToRemove = (int) Math.ceil(candidateVertices.size() * destroyPercentage);
			RandomSingleton generator = RandomSingleton.getInstance(this.randomSeed);
			for (int i = 0; i < numOfVerticesToRemove; i++) {
				int index = generator.nextInt(candidateVertices.size());

				Vertex auxVertex = candidateVertices.remove(index);
				//verticesAdded.add(auxVertex);
				try {
					this.newGraph.addVertexFromKnownSupergraph(auxVertex, this.graph);
				} catch (Exception ex) {
				}
			}
		}
		
		return this.newGraph;
	}

	/**
	 * Remove least connected nodes
	 * @return Graph
	 */
	@Override
	public Graph destruct() {
		if (this.type.equals(IGType.CONSTRUCTIVE_REMOVE_RANDOM_ADD_GREEDY)
				|| this.type.equals(IGType.DESTRUCTIVE_REMOVE_RANDOM_ADD_GREEDY)) {	
			// Articulation points
			Set<Vertex> totalArticulationPoints = new HashSet<>(this.newGraph.getArticulationPoints());
			int numTotalArticulationPoints = totalArticulationPoints.size();

			// Destroy graph randomly
			int numOfVerticesToRemove = (int) Math.ceil((this.newGraph.getNumOfVertices() - numTotalArticulationPoints) * destroyPercentage);
			List<Vertex> candidateVertices = this.newGraph.getRandomNodesExceptOther(
					numOfVerticesToRemove,
					totalArticulationPoints,
					this.randomSeed
			);

			//List<Vertex> verticesRemoved = new ArrayList<>();

			for (Vertex auxVertex : candidateVertices) {
				// Recalculate articulation points
				Set<Vertex> newArticulationPoints = new HashSet<>(this.newGraph.getArticulationPoints());

				if (!newArticulationPoints.contains(auxVertex)) {
					//verticesRemoved.add(auxVertex);
					this.newGraph.removeVertex(auxVertex);
				}
			}
		} else {
			// CONSTRUCTIVE_ADD_RANDOM_REMOVE_GREEDY
			// DESTRUCTIVE_ADD_RANDOM_REMOVE_GREEDY
			
			// Destroy graph greedy (by least connected)
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

				// [IMPROVEMENT]: Put the currently added nodes at the end of the list
				//List<Vertex> common = new ArrayList<>(solutionArray);
				//common.retainAll(verticesAdded);
				//solutionArray.removeAll(common);
				//solutionArray.addAll(common);

				// Add most connected unknown neighbour node
				Vertex selectedVertex = solutionArray.get(0);
				this.newGraph.removeVertex(selectedVertex);
			}
		}
		
		return this.newGraph;
	}
	
}
