package graphwork;

import java.io.IOException;

public class Main {

	public static void main(String[] args) throws IOException {
		System.out.println("Importing graph...");
		
		// Insert manually a graph file here
		//Graph graph = createGraph("./dtp_large/dtp_100_150_0.txt");
		Graph graph = Reader.createGraph("./dtp_small/dtp_10_15_0.txt");
		
		
		System.out.println("Num. vertices of the original graph: " + graph.getSizeOfAdjList());
		System.out.println("--------------------");
		
		// Execute algorithm
		Finder finder = new Finder(graph);
		Graph result = finder.getMinimumCoverTree();
		
		// Validate result
		System.out.println("Validating result...");
		if (result.isTree()) {
			System.out.println("Result is a tree");
		} else {
			System.out.println("Result is not a tree");
		}
		
		if (result.isVertexCoverOf(graph)) {
			System.out.println("Result is a vertex cover");
		} else {
			System.out.println("Result is not a vertex cover");
		}
		
		System.out.println("--------------------");
		
		// Show result
		System.out.println("Num. vertices of the tree cover: " + result.getSizeOfAdjList());
		System.out.println("Total weight of the tree cover: " + result.getTotalWeight());
		
		System.out.println("--------------------");
		
		// Show vertices of the tree cover
		System.out.println("Vertices of the tree cover:");
		for (Vertex vertexAux : result.getAllVertices()) {
			System.out.println(vertexAux);	
		}
	}
	
}
