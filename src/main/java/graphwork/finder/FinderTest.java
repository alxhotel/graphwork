package graphwork.finder;

import graphwork.graph.Graph;
import graphwork.graph.Vertex;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FinderTest extends Finder {

	public static final float DEFAULT_RANDOM_PERCENTAGE = 0.5f;
	public static final int DEFAULT_NUM_TRIES = 300;
	
	public FinderTest(Graph graph) {
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
			throw new Exception("destroyPercentage must be between 0 and 1");
		}
		if (numOfTries < 0) {
			throw new Exception("numOfTimes must be bigger than 0");
		}
		
		// Select the whole graph (clone it)
		this.newGraph = new Graph(this.graph);
		
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

		// Finished: Make sure graph is a minimum spanning tree
		return this.newGraph.getMST();
	}
	
}
