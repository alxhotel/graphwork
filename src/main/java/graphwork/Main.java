package graphwork;

public class Main {

	public static void main(String[] args) {
		System.out.println("Importing graph...");
		
		// Insert a graph file here
		Graph graph;
		try {
			//graph = createGraph("./dtp_large/dtp_100_150_0.txt");
			graph = Reader.createGraph("./dtp_small/dtp_10_15_0.txt");
		} catch (Exception ex) {
			System.out.println("Bad graph file");
			return;
		}
		
		System.out.println("Num. vertices of the original graph: " + graph.getSizeOfAdjList());
		System.out.println("--------------------");
		
		// Execute algorithm
		Finder finder = new Finder(graph);
		Graph resultRandom = finder.getMinimumCoverTree(Finder.TYPE_RANDOM);
		Graph resultMostConnected = finder.getMinimumCoverTree(Finder.TYPE_MOST_CONNECTED);
		
		// Show result by most connected
		System.out.println("Num. vertices of the tree cover (by most connected): " + resultMostConnected.getSizeOfAdjList());
		System.out.println("Total weight of the tree cover (by most connected): " + resultMostConnected.getTotalWeight());
		
		System.out.println("--------------------");
		
		// Show result by random
		System.out.println("Num. vertices of the tree cover (by random): " + resultRandom.getSizeOfAdjList());
		System.out.println("Total weight of the tree cover (by random): " + resultRandom.getTotalWeight());
	}
	
}
