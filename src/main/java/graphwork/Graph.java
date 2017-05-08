package graphwork;

import java.util.*;

public class Graph {

	private final Map<Vertex, List<Edge>> adjList;

	public Graph() {
		adjList = new HashMap<>();
	}

	public Graph(Graph toClone) {
		adjList = new HashMap<>();
		
		Iterator<Map.Entry<Vertex, List<Edge>>> it = toClone.adjList.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<Vertex, List<Edge>> entry = it.next();
			
			Vertex newVertex = new Vertex(entry.getKey().getId());
			if (!adjList.containsKey(newVertex)) {
				adjList.put(newVertex, new ArrayList<>());
			}
			
			Iterator<Edge> it2 = entry.getValue().iterator();
			while (it2.hasNext()) {
				Edge entryAux = it2.next();
				if (!adjList.containsKey(entryAux.getDestination())) {
					Vertex newVertex2 = new Vertex(entryAux.getDestination().getId());
					adjList.put(newVertex2, new ArrayList<>());
					adjList.get(newVertex).add(new Edge(newVertex2, entryAux.getWeight()));
				} else {
					Vertex newVertex2 = new Vertex(entryAux.getDestination().getId());
					adjList.get(newVertex).add(new Edge(newVertex2, entryAux.getWeight()));
				}
			}
		}
	}
	
	public void addVertex(Vertex newVertex) {
		adjList.put(newVertex, new ArrayList<>());
	}

	public void addEdge(Vertex source, Vertex destination, float weight) {
		addEdge(source.getId(), destination.getId(), weight);
	}
	
	public void addEdge(int sourceId, int destinationId, float weight) {
		Vertex sourceVertex = new Vertex(sourceId);
		Vertex destinationVertex = new Vertex(destinationId);

		// Just in case
		if (adjList.get(sourceVertex) == null) {
			adjList.put(sourceVertex, new ArrayList<>());
		}
		if (adjList.get(destinationVertex) == null) {
			adjList.put(destinationVertex, new ArrayList<>());
		}

		adjList.get(sourceVertex).add(new Edge(destinationVertex, weight));
		adjList.get(destinationVertex).add(new Edge(sourceVertex, weight));
	}

	public void removeVertex(Vertex vertex) {
		// Remove links to "vertex" from other vertices
		for (Edge edgeAuxFromVertex : this.adjList.get(vertex)) {
			Iterator<Edge> it = this.adjList.get(edgeAuxFromVertex.getDestination()).iterator();
			while (it.hasNext()) {
				Edge edgeAuxFromOther = it.next();
				if (edgeAuxFromOther.getDestination().equals(vertex)) {
					it.remove();
				}
			}
		}
		
		// Remove "vertex"
		adjList.remove(vertex);
	}
	
	public void removeEdge(Vertex vertex, Vertex otherVertex) {
		// Remove from list of "vertex"
		Iterator<Edge> it = this.adjList.get(vertex).iterator();
		while (it.hasNext()) {
			Edge edgeAux = it.next();
			if (edgeAux.getDestination().equals(otherVertex)) {
				it.remove();
			}
		}
		
		// Remove from list of "otherVertex"
		it = this.adjList.get(otherVertex).iterator();
		while (it.hasNext()) {
			Edge edgeAux = it.next();
			if (edgeAux.getDestination().equals(vertex)) {
				it.remove();
			}
		}
	}
	
	public int getNumOfVertices() {
		return adjList.size();
	}

	public Vertex getVertex(int vertexId) {
		return new Vertex(vertexId);
	}

	public List<Edge> getEdges(int vertexId) {
		return adjList.get(new Vertex(vertexId));
	}
	
	public List<Edge> getNeighbors(Vertex key) {
		return adjList.get(key);
	}
	
	public boolean isEmpty() {
		return this.adjList.isEmpty();
	}

	public List<Vertex> getAllVertices() {
		List<Vertex> result = new ArrayList<>();
		result.addAll(adjList.keySet());
		return result;
	}
	
	public Vertex getRandomNode() {
		Random generator = new Random();
		Vertex[] keys = (Vertex[]) this.adjList.keySet().toArray();
		return keys[generator.nextInt(keys.length)];
	}
	
	public Boolean existsVertex(Vertex key) {
		return this.adjList.containsKey(key);
	}
	
	@Override
	public String toString() {
		StringBuilder str = new StringBuilder("");
		
		Iterator<Map.Entry<Vertex, List<Edge>>> it = this.adjList.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<Vertex, List<Edge>> entry = it.next();
			
			str.append(entry.getKey()).append(" ==> ");
			
			Iterator<Edge> it2 = entry.getValue().iterator();
			while (it2.hasNext()) {
				Edge entryAux = it2.next();
				
				str.append("[").append(entryAux.getDestination()).append(",").append(entryAux.getWeight()).append("], ");
			}
			str.append("\n");
		}
		
		return str.toString();
	}
	
	public List<Map.Entry<Vertex, Edge>> getAllEdges() {
		List<Map.Entry<Vertex, Edge>> result = new ArrayList<>();
		for (Map.Entry<Vertex, List<Edge>> entry : adjList.entrySet()) {
			for (Edge edgeAux : entry.getValue()) {
				result.add(new AbstractMap.SimpleEntry<>(entry.getKey(), edgeAux));
			}
		}

		return result;
	}

	public List<Map.Entry<Vertex, Edge>> getAllUniqueEdges() {
		List<Map.Entry<Vertex, Edge>> result = new ArrayList<>();
		for (Map.Entry<Vertex, List<Edge>> entry : adjList.entrySet()) {
			for (Edge edgeAux : entry.getValue()) {

				// Check if already have it
				boolean notInList = true;
				for (Map.Entry<Vertex, Edge> entryresult : result) {
					if ((entry.getKey().equals(entryresult.getKey()) && edgeAux.getDestination().equals(entryresult.getValue().getDestination()))
							|| (edgeAux.getDestination().equals(entryresult.getKey()) && entry.getKey().equals(entryresult.getValue().getDestination()))) {
						notInList = false;
						break;
					}
				}

				if (notInList) {
					result.add(new AbstractMap.SimpleEntry<>(entry.getKey(), edgeAux));
				}
			}
		}

		return result;
	}

	public Boolean isVertexCoverOf(Graph originalGraph) {
		// Get all vertices of thsi graph
		List<Vertex> vertices = new ArrayList<>(adjList.keySet());
		
		// Get all edges of "otherGraph"
		List<Map.Entry<Vertex, Edge>> edges = originalGraph.getAllUniqueEdges();
		
		// Aux list to checklist every edge
		List<Map.Entry<Vertex, Edge>> edgesClone = new ArrayList<>(edges);
		
		for (Vertex vertexCovering : vertices) {
			for (Map.Entry<Vertex, Edge> possibleEdgeCovered : edges) {
				if (vertexCovering.equals(possibleEdgeCovered.getKey())
						|| vertexCovering.equals(possibleEdgeCovered.getValue().getDestination())) {
					// Remove
					edgesClone.remove(possibleEdgeCovered);
				}
			}
		}
		
		// All edges removed, graph is a vertex cover
		return edgesClone.isEmpty();
	}

	/**
	 * Get the most connected, unknown and neighbor node; based on a known graph
	 * NOTE: Graph contains ONE edge
	 * 
	 * @param knownGraph
	 * @return 
	 */
	public Map.Entry<Vertex, Graph> getMostConnectedUnknownNeighborNode(Graph knownGraph) {
		Vertex resultingVertex = null;
		Graph resultingGraph = null;
		
		if (knownGraph.isEmpty()) {
			// Get most connected node
			Map.Entry<Vertex, List<Edge>> mostConnected = null;
			for (Map.Entry<Vertex, List<Edge>> entryAux : this.adjList.entrySet()) {
				if (mostConnected == null || entryAux.getValue().size() > mostConnected.getValue().size()) {
					mostConnected = entryAux;
				}
			}
			
			// "mostConnected" is always NOT null
			if (mostConnected != null) {
				resultingVertex = mostConnected.getKey();
				resultingGraph = new Graph();
			}
		} else {
			// TODO: fix ugly fix
			List<Map.Entry<Vertex, List<Edge>>> list = new ArrayList<>(knownGraph.adjList.entrySet());
			
			// Get most connected node
			Map.Entry<Vertex, Edge> mostConnected = null;
			int numOfEdgesOfMostConnected = 0;
			
			outerloop:
			for (Map.Entry<Vertex, List<Edge>> entryAux : list) {
				// This can NOT be null (knownGraph is a subgraph)
				Vertex originalVertex = this.getVertex(entryAux.getKey().getId());
				
				// There is an unknown neighbor
				if (this.getNeighbors(originalVertex).size() > knownGraph.getNeighbors(entryAux.getKey()).size()) {
					for (Edge edgeAux : this.getNeighbors(originalVertex)) {
						// Filter by: unknown nodes
						if (!knownGraph.existsVertex(edgeAux.getDestination())) {
							// Is more connect than the current one
							if (this.getNeighbors(edgeAux.getDestination()).size() > numOfEdgesOfMostConnected) {
								numOfEdgesOfMostConnected = this.getNeighbors(edgeAux.getDestination()).size();
								// Create (existing) edge from unknown node to known node 
								mostConnected = new AbstractMap.SimpleEntry(edgeAux.getDestination(), new Edge(originalVertex, edgeAux.getWeight()));
							}
						}
					}
				}
			}
			
			// "mostConnected" is always NOT null
			if (mostConnected != null) {
				resultingVertex = mostConnected.getKey();
				resultingGraph = new Graph();
				resultingGraph.addEdge(
						mostConnected.getKey(),
						mostConnected.getValue().getDestination(),
						mostConnected.getValue().getWeight()
				);
			} else {
				resultingVertex = null;
				resultingGraph = new Graph();
			}
		}
		
		return new AbstractMap.SimpleEntry(resultingVertex, resultingGraph);
	}
	
	/**
	 * Get the least connected, known and neighbor node; based on a known graph
	 * NOTE: Graph contains MULTIPLE edges
	 * 
	 * @param knownGraph
	 * @return 
	 */
	public Map.Entry<Vertex, Graph> getLeastConnectedKnownNeighborNode(Graph knownGraph) {
		Vertex resultingVertex;
		Graph resultingGraph;
		
		if (knownGraph.isEmpty()) {
			resultingVertex = null;
			resultingGraph = new Graph();
		} else {
			// TODO: fix ugly fix
			List<Map.Entry<Vertex, List<Edge>>> list = new ArrayList<>(knownGraph.adjList.entrySet());
			
			// Get most connected node
			Map.Entry<Vertex, List<Edge>> leastConnected = null;
			int numOfEdgesOfLeastConnected = Integer.MAX_VALUE;
			
			outerloop:
			for (Map.Entry<Vertex, List<Edge>> entryAux : list) {
				// This can NOT be null (knownGraph is a subgraph)
				Vertex originalVertex = this.getVertex(entryAux.getKey().getId());
				
				// There is an known neighbor && with all neighbors known (!!)
				if (this.getNeighbors(originalVertex).size() == knownGraph.getNeighbors(entryAux.getKey()).size()) {
					// Is less connect than the current one
					int numOfNeighbors = this.getNeighbors(originalVertex).size();
					if (this.getNeighbors(originalVertex).size() < numOfEdgesOfLeastConnected) {
						numOfEdgesOfLeastConnected = numOfNeighbors;
						// Create (existing) edge from unknown node to known node
						leastConnected = new AbstractMap.SimpleEntry(originalVertex, this.getNeighbors(originalVertex));
					}
				}
			}
			
			if (leastConnected != null) {
				resultingVertex = leastConnected.getKey();
				resultingGraph = new Graph();
				for (Edge edgeAux : leastConnected.getValue()) {
					resultingGraph.addEdge(
							leastConnected.getKey(),
							edgeAux.getDestination(),
							edgeAux.getWeight()
					);
				}
			} else {
				resultingVertex = null;
				resultingGraph = new Graph();
			}
		}
		
		
		return new AbstractMap.SimpleEntry(resultingVertex, resultingGraph);
	}
	
	/**
	 * Get a random, unknown and neighbor node; based on a known graph
	 * @param knownGraph
	 * @return 
	 */
	public Map.Entry<Vertex, Graph> getRandomUnknownNeighborNode(Graph knownGraph) {
		Vertex resultingVertex = null;
		Graph resultingGraph = null;
		
		if (knownGraph.isEmpty()) {
			resultingVertex = this.getRandomNode();
			resultingGraph = new Graph();
		} else {
			// TODO: fix ugly fix
			List<Map.Entry<Vertex, List<Edge>>> list = new ArrayList<>(knownGraph.adjList.entrySet());
			
			// Randomize list
			long seed = System.nanoTime();
			Collections.shuffle(list, new Random(seed));
			
			outerloop:
			for (Map.Entry<Vertex, List<Edge>> entryAux : list) {
				
				// This can NOT be null (knownGraph is a subgraph)
				Vertex originalVertex = this.getVertex(entryAux.getKey().getId());
				
				// There is an unknown neighbor
				if (this.getNeighbors(originalVertex).size() > knownGraph.getNeighbors(entryAux.getKey()).size()) {
					for (Edge edgeAux : this.getNeighbors(originalVertex)) {
						// Filter by: unknown nodes
						if (!knownGraph.existsVertex(edgeAux.getDestination())) {
							resultingVertex = edgeAux.getDestination();
							resultingGraph = new Graph();
							resultingGraph.addEdge(entryAux.getKey(), edgeAux.getDestination(), edgeAux.getWeight());
							break outerloop;
						}
					}
				}
			}
		}
		
		return new AbstractMap.SimpleEntry(resultingVertex, resultingGraph);
	}

	/**
	 * Returns true if the graph is a tree, else false.
	 *
	 * @return isTree
	 *
	 */
	public Boolean isTree() {
		// Mark all the vertices as not visited and not part
		// of recursion stack
		Map<Vertex, Boolean> visitedMap = new HashMap<>();
		for (Map.Entry<Vertex, List<Edge>> entry : adjList.entrySet()) {
			visitedMap.put(entry.getKey(), false);
		}

		// "Is not cyclic"
		// The call to isCyclicUtil serves multiple purposes
		// It returns true if graph reachable from vertex 0
		// is cyclcic. It also marks all vertices reachable
		// from 0.
		// Just in case
		if (adjList.size() > 0) {
			Vertex anyVertex = adjList.entrySet().iterator().next().getKey();
			if (isCyclicUtil(anyVertex, visitedMap, null)) {
				return false;
			}
		}

		// "Is connected"
		// If we find a vertex which is not reachable from 0
		// (not marked by isCyclicUtil(), then we return false
		for (Map.Entry<Vertex, Boolean> entry : visitedMap.entrySet()) {
			if (!entry.getValue()) {
				return false;
			}
		}

		return true;
	}

	/**
	 * A recursive function that uses visited[] and parent to detect cycle in subgraph reachable from vertex v.
	 *
	 * @param currentVertex
	 * @param visitedMap
	 * @param parent
	 * @return isCyclic
	 */
	public Boolean isCyclicUtil(Vertex currentVertex, Map<Vertex, Boolean> visitedMap, Vertex parent) {
		// Mark the current node as visited
		visitedMap.put(currentVertex, true);
		Edge itEdge;

		// Recur for all the vertices adjacent to this vertex
		Iterator<Edge> it = adjList.get(currentVertex).iterator();
		while (it.hasNext()) {
			itEdge = it.next();

			// If an adjacent is not visited, then recur for
			// that adjacent
			if (!visitedMap.get(itEdge.getDestination())) {
				if (isCyclicUtil(itEdge.getDestination(), visitedMap, currentVertex)) {
					return true;
				}
			} else if (!itEdge.getDestination().equals(parent)) {
				// If an adjacent is visited and not parent of 
				// current vertex, then there is a cycle.
				return true;
			}
		}
		return false;
	}

	public Float getTotalWeight() {
		float weightTotal = 0;
		for (Map.Entry<Vertex, Edge> entry : this.getAllUniqueEdges()) {
			weightTotal += entry.getValue().getWeight();
		}
		
		return weightTotal;
	}
	
	/**
	 * Get if graph is connected
	 * @return boolean
	 */
	public boolean isConnected() {
		Map<Vertex, Boolean> visitedMap = new HashMap<>();
		for (Map.Entry<Vertex, List<Edge>> entry : adjList.entrySet()) {
			visitedMap.put(entry.getKey(), false);
		}
		
		if (adjList.size() > 0) {
			Vertex anyVertex = adjList.entrySet().iterator().next().getKey();
			isConnectedUtils(anyVertex, visitedMap, null);
		}
		
		for (Map.Entry<Vertex, Boolean> entry : visitedMap.entrySet()) {
			if (!entry.getValue()) {
				return false;
			}
		}
		
		return true;
	}
	
	public void isConnectedUtils(Vertex currentVertex, Map<Vertex, Boolean> visitedMap, Vertex parent) {
		visitedMap.put(currentVertex, true);
		Edge itEdge;

		Iterator<Edge> it = adjList.get(currentVertex).iterator();
		while (it.hasNext()) {
			itEdge = it.next();

			if (!visitedMap.get(itEdge.getDestination())) {
				isConnectedUtils(itEdge.getDestination(), visitedMap, currentVertex);
			}
		}
	}
	
	/**
	 * Convert any connected graph to a tree, by removing the cycles
	 */
	public void convertToTree() {
		// Mark all the vertices as not visited and not part
		// of recursion stack
		Map<Vertex, Boolean> visitedMap = new HashMap<>();
		for (Map.Entry<Vertex, List<Edge>> entry : adjList.entrySet()) {
			visitedMap.put(entry.getKey(), false);
		}
		
		// Just in case
		if (adjList.size() > 0) {
			Vertex anyVertex = adjList.entrySet().iterator().next().getKey();
			convertToTreeUtil(anyVertex, visitedMap, null);
		}
	}
	
	private void convertToTreeUtil(Vertex currentVertex, Map<Vertex, Boolean> visitedMap, Vertex parent) {
		// Mark the current node as visited
		visitedMap.put(currentVertex, true);
		Edge itEdge;

		// Recur for all the vertices adjacent to this vertex
		Iterator<Edge> it = this.adjList.get(currentVertex).iterator();
		while (it.hasNext()) {
			itEdge = it.next();

			// If an adjacent is not visited, then recur for
			// that adjacent
			if (!visitedMap.get(itEdge.getDestination())) {
				convertToTreeUtil(itEdge.getDestination(), visitedMap, currentVertex);
			} else if (!itEdge.getDestination().equals(parent)) {
				// If an adjacent is visited and not parent of 
				// current vertex, then there is a cycle.
				
				// Delete edge
				//this.removeEdge(currentVertex, itEdge.getDestination());
				it.remove();
			}
		}
	}
 
    /**
	 * The function to do DFS traversal. It uses recursive function APUtil()
	 * 
	 * Complexity: O(V+E)
	 * @return articulation points (vertices)
	 */	
	public List<Vertex> getArticulationPoints() {
		// Mark all the vertices as not visited
		Map<Vertex, Boolean> visited = new HashMap<>();
		Map<Vertex, Integer> disc = new HashMap<>();
		Map<Vertex, Integer> low = new HashMap<>();
		Map<Vertex, Vertex> parent = new HashMap<>();
		Map<Vertex, Boolean> ap = new HashMap<>();
 
		// Initialize arrays
		List<Vertex> vertices = this.getAllVertices();
		for (Vertex v : vertices) {
			visited.put(v, false);
			parent.put(v, null);
			ap.put(v, false);
		}
 
		time = 0;
		
        // Call the recursive helper function to find articulation
        // points in DFS tree rooted with vertex 'i'
		for (Vertex v : vertices) {
			if (!visited.get(v)) {
				articulationPointsUtil(v, visited, disc, low, parent, ap);
			}
		}
		
		List<Vertex> result = new ArrayList<>();
		for (Vertex v : vertices) {
			if (ap.get(v)) {
				result.add(v);
			}
		}
		
		return result;
	}

	public int time = 0;
	
	/**
	 * A recursive function that find articulation points using DFS
	 * 
     * u --> The vertex to be visited next
     * visited[] --> keeps tract of visited vertices
     * disc[] --> Stores discovery times of visited vertices
     * parent[] --> Stores parent vertices in DFS tree
     * ap[] --> Store articulation points
	 */
    private void articulationPointsUtil(Vertex u, Map<Vertex, Boolean> visited,
			Map<Vertex, Integer> disc, Map<Vertex, Integer> low,
			Map<Vertex, Vertex> parent, Map<Vertex, Boolean> ap) {
        // Count of children in DFS Tree
        int children = 0;
 
        // Mark the current node as visited
        visited.put(u, true);
 
        // Initialize discovery time and low value
		time++;
		disc.put(u, time);
		low.put(u, time);
 
        // Go through all vertices aadjacent to this
        Iterator<Edge> i = this.adjList.get(u).iterator();
        while (i.hasNext()) {
            Vertex v = i.next().getDestination();  // v is current adjacent of u
			
            // If v is not visited yet, then make it a child of u
            // in DFS tree and recur for it
			if (!visited.get(v)) {
                children++;
				parent.put(v, u);
                articulationPointsUtil(v, visited, disc, low, parent, ap);
 
                // Check if the subtree rooted with v has a connection to
                // one of the ancestors of u
				low.put(u, Math.min(low.get(u), low.get(v)));
 
                // "u" is an articulation point in following cases
 
                // 1) "u" is root of DFS tree and has two or more chilren.
                if (parent.get(u) == null && children >= 2) {
                    ap.put(u, true);
				}
 
                // 2) If "u" is not root and low value of one of its child
                // is more than discovery value of "u".
                if (parent.get(u) != null && low.get(v) >= disc.get(u)) {
                    ap.put(u, true);
				}
            } else if (v != parent.get(u)) {
				// Update low value of u for parent function calls.
				low.put(u, Math.min(low.get(u), disc.get(v)));
			}
        }
    }

	/**
	 * Get all known vertices with all known neighbors
	 * @param knownGraph
	 * @return Graph
	 */
	public List<Vertex> getAllKnownVerticesWithAllKnownNeighbors(Graph knownGraph) {
		List<Map.Entry<Vertex, List<Edge>>> list = new ArrayList<>(knownGraph.adjList.entrySet());
		List<Vertex> result = new ArrayList<>();
		for (Map.Entry<Vertex, List<Edge>> entryAux : list) {
			// This can NOT be null (knownGraph is a subgraph)
			Vertex originalVertex = this.getVertex(entryAux.getKey().getId());

			// There is an known neighbor && with all neighbors known (!!)
			if (this.getNeighbors(originalVertex).size() == knownGraph.getNeighbors(entryAux.getKey()).size()) {
				result.add(entryAux.getKey());
			}
		}

		return result;
	}

	public List<Vertex> getRandomNodes(int numOfNodes) {
		Random generator = new Random();
		List<Vertex> allVertices = new ArrayList<>();
		allVertices.addAll(this.adjList.keySet());
		
		List<Vertex> list = new ArrayList<>();
		for (int i = 0; i < numOfNodes; i++) {
			int index = generator.nextInt(allVertices.size());
			Vertex auxVertex = allVertices.remove(index);
			
			list.add(auxVertex);
		}
		
		return list;
	}

	public void addVertexFromKnownGraph(Vertex newVertex, Graph graph) throws Exception {
		if (!graph.existsVertex(newVertex)) {
			throw new Exception("newVertex is not in known graph");
		}
		
		// Add vertex
		this.addVertex(newVertex);
		
		// Add edges (if any)
		List<Edge> edges = graph.getNeighbors(newVertex);

		for (Edge edgeAux : edges) {
			if (this.existsVertex(edgeAux.getDestination())) {
				// Add edge from newVertex to destination
				Vertex destinationVertex = this.getVertex(edgeAux.getDestination().getId());
				this.addEdge(newVertex, destinationVertex, edgeAux.getWeight());
			}
		}
	}

	public List<Vertex> getRandomNodesExceptOther(int numOfNodes, Set<Vertex> exceptionList) {
		Random generator = new Random();
		List<Vertex> allVertices = new ArrayList<>();
		allVertices.addAll(this.adjList.keySet());
		allVertices.removeAll(exceptionList);
		
		List<Vertex> list = new ArrayList<>();
		for (int i = 0; i < numOfNodes; i++) {
			int index = generator.nextInt(allVertices.size());
			Vertex auxVertex = allVertices.remove(index);
			
			list.add(auxVertex);
		}
		
		return list;
	}

	/**
	 * Get all vertices adjacent to known subgraph
	 * @param subGraph
	 * @return 
	 */
	public List<Vertex> getAllVerticesNeighbourToKnownSubgraph(Graph subGraph) {
		List<Vertex> result = new ArrayList<>();
		
		List<Vertex> allVertices = subGraph.getAllVertices();
		
		// If empty, all vertices are adjacent to empty subgraph
		if (allVertices.isEmpty()) {
			result.addAll(this.getAllVertices());
			return result;
		}
		
		for (Vertex auxVertex : allVertices) {
			List<Edge> neighbours = this.getNeighbors(auxVertex);
			
			// Coditional optimization
			if (subGraph.getNeighbors(auxVertex).size() < neighbours.size()) {
				for (Edge auxEdge : neighbours) {
					if (!subGraph.existsVertex(auxEdge.getDestination())) {
						result.add(auxEdge.getDestination());
					}
				}
			}
		}
		
		return result;
	}

	public int countKnownVertices(Set<Vertex> vertices) {
		int count = 0;
		
		for (Vertex aux : vertices) {
			if (this.existsVertex(aux)) {
				count++;
			}
		}
		
		return count;
	}
	
}
