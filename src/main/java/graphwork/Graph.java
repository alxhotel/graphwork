package graphwork;

import java.util.*;
import java.util.stream.Collectors;

public class Graph {

	private final Map<Vertex, List<Edge>> adjList;

	public Graph() {
		adjList = new HashMap<>();
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

	public Map<Vertex, List<Edge>> getAdjList() {
		return adjList;
	}

	public int getSizeOfAdjList() {
		return adjList.size();
	}

	public Vertex getVertex(int vertexId) {
		return new Vertex(vertexId);
	}

	public List<Edge> getEdges(int vertexId) {
		return adjList.get(new Vertex(vertexId));
	}

	public List<Vertex> getAllVertices() {
		List<Vertex> result = new ArrayList<>();
		result.addAll(adjList.keySet());
		return result;
	}

	public List<Edge> removeVertex(Vertex auxVertex) {
		return adjList.remove(auxVertex);
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

	Boolean isVertexCoverOf(Graph originalGraph) {
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

	public List<Vertex> getMostConnectedUnknownNodes(Graph knownGraph) {
		List<Map.Entry<Vertex, List<Edge>>> list = new ArrayList<>(this.adjList.entrySet());
		
		// Filter by: unknown nodes
		List<Map.Entry<Vertex, List<Edge>>> filteredList = list.stream().filter(auxEntry -> {
			return !knownGraph.existsVertex(auxEntry.getKey());
		}).collect(Collectors.toList());
		
		// Get the connections of the most connected node
		int mostConnections = 0;
		for (Map.Entry<Vertex, List<Edge>> entry : filteredList) {
			if (entry.getValue().size() > mostConnections) {
				mostConnections = entry.getValue().size();
			}
		}
		
		// Filter by the connections of the most connected node
		final int auxMostConnections = mostConnections;
		List<Map.Entry<Vertex, List<Edge>>> filteredByConnectionsList = filteredList.stream().filter(auxEntry -> {
			return auxEntry.getValue().size() == auxMostConnections;
		}).collect(Collectors.toList());
		
		// Create resulting list
		List<Vertex> resultingList = new ArrayList<>();
		for (Map.Entry<Vertex, List<Edge>> entry : filteredByConnectionsList) {
			resultingList.add(entry.getKey());
		}
		
		return resultingList;
	}
	
	public Vertex getRandomUnknownNode(Graph knownGraph) {
		List<Map.Entry<Vertex, List<Edge>>> list = new ArrayList<>(this.adjList.entrySet());
		
		// Filter by: unknown nodes
		List<Map.Entry<Vertex, List<Edge>>> filteredList = list.stream().filter(auxEntry -> {
			return !knownGraph.existsVertex(auxEntry.getKey());
		}).collect(Collectors.toList());
		
		// Get random unknown vertex
		Random randomGenerator = new Random();
		int randomIndex = randomGenerator.nextInt(filteredList.size());
		Map.Entry<Vertex, List<Edge>> randomEntry = filteredList.get(randomIndex);
		
		return randomEntry.getKey();
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
		for (Map.Entry<Vertex, Boolean> entry :  visitedMap.entrySet()) {
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

	public Boolean existsVertex(Vertex key) {
		return this.adjList.containsKey(key);
	}

	public Float getTotalWeight() {
		float weightTotal = 0;
		for (Map.Entry<Vertex, Edge> entry : getAllUniqueEdges()) {
			weightTotal += entry.getValue().getWeight();
		}
		
		return weightTotal;
	}

}
