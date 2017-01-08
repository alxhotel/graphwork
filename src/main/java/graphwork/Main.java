package graphwork;

import java.io.File;

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
		} catch (Exception ex) {
			System.out.println("Bad graph file");
			return;
		}
		
		// Execute algorithm
		Finder finder = new Finder(graph);
		
		// Most connected
		long startTimeAlg = System.currentTimeMillis();
		Graph resultMostConnected = finder.getMinimumCoverTree(Finder.TYPE_MOST_CONNECTED);
		long totalTimeAlg = System.currentTimeMillis() - startTimeAlg;
		float weighAlg = resultMostConnected.getTotalWeight();
		
		// Random
		long startTimeRandom = System.currentTimeMillis();
		Graph resultRandom = finder.getMinimumCoverTree(Finder.TYPE_RANDOM);
		long totalTimeRandom = System.currentTimeMillis() - startTimeRandom;
		float weighRandom = resultRandom.getTotalWeight();
		
		// Save to CSV
		CSV.saveResults(RESULTS_CSV_FILEPATH, file.getName(),
			weighAlg, totalTimeAlg,
			weighRandom, totalTimeRandom);
	}
	
}
