package graphwork;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class Main {

	public static final String RESULTS_CSV_FILEPATH = "./results.csv";
	
	public static void main(String[] args) {
		System.out.println("Calculating tree cover of all graphs...");
		
		// Create results file
		CSV.createResultsFile(RESULTS_CSV_FILEPATH);
		
		// Set of small graphs
		final File folder = new File("./dtp_small");
		for (final File fileEntry : folder.listFiles()) {
			// Exclude bad files
			if (!fileEntry.isDirectory()) {
				calculateGraphResult(fileEntry);
			}
		}
		
		// Set of large graphs
		final File folderLarge = new File("./dtp_large");
		for (final File fileEntry : folderLarge.listFiles()) {
			// Exclude bad files
			if (!fileEntry.isDirectory()
				&& !fileEntry.getName().equals("dtp_300_1000_0.txt")
				&& !fileEntry.getName().equals("dtp_300_1000_1.txt")
				&& !fileEntry.getName().equals("dtp_300_1000_2.txt")) {
				
				calculateGraphResult(fileEntry);
				
			}
		}
	}
	
	private static void calculateGraphResult(File file) {
		System.out.println("Calculating graph: " + file.getName());
		
		Graph graph;
		try {
			graph = Reader.createGraph(file.getAbsolutePath());
		} catch (IOException ignored) {
			System.out.println("Bad graph file");
			return;
		}
		
		// Most connected - Constructive
		FinderConstructive finderConstructive = new FinderConstructive(graph);
		long startTimeAlgConstructive = System.currentTimeMillis();
		Graph resultMostConnected = finderConstructive.getMinimumCoverTree();
		long totalTimeAlgConstructive = System.currentTimeMillis() - startTimeAlgConstructive;
		float weighAlgConstructive = resultMostConnected.getTotalWeight();
		
		System.out.println("\tConstructivo Original: " + graph.getTotalWeight());
		System.out.println("\tConstructivo New: " + weighAlgConstructive);
		
		// Least connected - Destructive
		FinderDestructive finderDestructive = new FinderDestructive(graph);
		long startTimeAlgDestructive = System.currentTimeMillis();
		Graph resultLeastConnectedDestructive = finderDestructive.getMinimumCoverTree();
		long totalTimeAlgDestructive = System.currentTimeMillis() - startTimeAlgDestructive;
		float weighAlgDestructive = resultLeastConnectedDestructive.getTotalWeight();
		
		System.out.println("\tDestructivo Original: " + graph.getTotalWeight());
		System.out.println("\tDestructivo New: " + weighAlgDestructive);
		
		// Least connected - Destructive Improved
		FinderDestructiveImproved finderDestructiveImproved = new FinderDestructiveImproved(graph);
		long startTimeAlgDestructiveImproved = System.currentTimeMillis();
		Graph resultLeastConnectedDestructiveImproved = finderDestructiveImproved.getMinimumCoverTree();
		long totalTimeAlgDestructiveImproved = System.currentTimeMillis() - startTimeAlgDestructiveImproved;
		float weighAlgDestructiveImproved = resultLeastConnectedDestructiveImproved.getTotalWeight();
		
		System.out.println("\tDestructivo Mejorado Original: " + graph.getTotalWeight());
		System.out.println("\tDestructivo Mejorado New: " + weighAlgDestructiveImproved);
		
		//System.out.println(resultLeastConnectedDestructiveImproved.getAllVertices().toString());
		
		// Iterated Greedy - with "least connected for random destruction"
		FinderIteratedGreedy finderIteratedGreedy = new FinderIteratedGreedy(graph);
		long startTimeAlgIteratedGreedy = System.currentTimeMillis();
		Graph resultIteratedGreedy = finderIteratedGreedy.getMinimumCoverTree();
		long totalTimeAlgIteratedGreedy = System.currentTimeMillis() - startTimeAlgIteratedGreedy;
		float weighAlgIteratedGreedy = resultIteratedGreedy.getTotalWeight();
		
		System.out.println("\tIterated Greedy Original: " + graph.getTotalWeight());
		System.out.println("\tIterated Greedy New: " + weighAlgIteratedGreedy);
		
		//System.out.println(resultIteratedGreedy.getAllVertices().toString());
		
		//System.out.println(resultIteratedGreedy.getAllVertices());
		
		// Random
		/*FinderRandom finderRandom = new FinderRandom(graph);
		long startTimeRandom = System.currentTimeMillis();
		Graph resultRandom = finder.getMinimumCoverTree(Finder.TYPE_RANDOM);
		long totalTimeRandom = System.currentTimeMillis() - startTimeRandom;
		float weighRandom = resultRandom.getTotalWeight();*/
		
		// Save to CSV
		CSV.appendResult(RESULTS_CSV_FILEPATH,
			file.getName(), graph.getTotalWeight(),
			weighAlgConstructive, totalTimeAlgConstructive,
			weighAlgDestructive, totalTimeAlgDestructive,
			weighAlgDestructiveImproved, totalTimeAlgDestructiveImproved,
			weighAlgIteratedGreedy, totalTimeAlgIteratedGreedy);
	}
	
}
