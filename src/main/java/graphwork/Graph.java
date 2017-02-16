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
	
	public Graph(int numberVertices) {
		adjList = new HashMap<>();
		for (int i = 0; i < numberVertices; i++) {
			adjList.put(new Vertex(i), new ArrayList<>());
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
	
	public String toString() {
		StringBuilder str = new StringBuilder("");
		
		Iterator<Map.Entry<Vertex, List<Edge>>> it = this.adjList.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<Vertex, List<Edge>> entry = it.next();
			
			str.append(entry.getKey() + " ==> ");
			
			Iterator<Edge> it2 = entry.getValue().iterator();
			while (it2.hasNext()) {
				Edge entryAux = it2.next();
				
				str.append("[" + entryAux.getDestination() + "," + entryAux.getWeight() + "], ");
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
		Vertex resultingVertex = null;
		Graph resultingGraph = null;
		
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
		for (Map.Entry<Vertex, Edge> entry : getAllUniqueEdges()) {
			weightTotal += entry.getValue().getWeight();
		}
		
		return weightTotal;
	}
	
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
	

}
