package graphwork;

import graphwork.finder.Finder;
import graphwork.utils.CSV;
import graphwork.utils.Reader;
import graphwork.graph.Graph;
import graphwork.finder.FinderIteratedGreedy;
import graphwork.finder.FinderDestructive;
import graphwork.finder.FinderDestructiveImproved;
import graphwork.finder.FinderConstructive;
import graphwork.finder.FinderConstructiveImproved;
import graphwork.finder.FinderIteratedGreedyCustom;
import graphwork.finder.FinderIteratedGreedyCustom.IGType;
import graphwork.finder.FinderIteratedGreedyImproved;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Main {

	public static final String RESULTS_CSV_FILEPATH = "./results.csv";
	
	public class Result {
		public float weight;
		public long time;
		
		public Result(float weight, long time) {
			this.weight = weight;
			this.time = time;
		}
	}
	
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
		Result constructiveResult = callFinder(new FinderConstructive(graph));
		System.out.println("\tConstructive Original: " + graph.getTotalWeight());
		System.out.println("\tConstructive New: " + constructiveResult.weight);
		
		// Most connected to unknwon nodes - Constructive Improved
		Result constructiveImprovedResult = callFinder(new FinderConstructiveImproved(graph));
		System.out.println("\tConstructive Mejorado Original: " + graph.getTotalWeight());
		System.out.println("\tConstructive Mejorado New: " + constructiveImprovedResult.weight);
		
		// Least connected - Destructive
		Result destructiveResult = callFinder(new FinderDestructive(graph));
		System.out.println("\tDestructivo Original: " + graph.getTotalWeight());
		System.out.println("\tDestructivo New: " + destructiveResult.weight);
		
		// Least connected - Destructive Improved
		Result destructiveImprovedResult = callFinder(new FinderDestructiveImproved(graph));
		System.out.println("\tDestructivo Mejorado Original: " + graph.getTotalWeight());
		System.out.println("\tDestructivo Mejorado New: " + destructiveImprovedResult.weight);
		
		// Iterated Greedy - with "destructive for constuctive phase"
		Result iteratedGreedyResult = callFinder(new FinderIteratedGreedy(graph));
		System.out.println("\tIterated Greedy Original: " + graph.getTotalWeight());
		System.out.println("\tIterated Greedy New: " + iteratedGreedyResult.weight);
		
		if (destructiveImprovedResult.weight > iteratedGreedyResult.weight) {
			System.out.println("-------------------");
			System.out.println("\tMEJORA ENCONTRADA EN ITERATED GREEDY");
			System.out.println("-------------------");
		}
		
		// Iterated Greedy Improved - with "destructive for constuctive phase"
		Result iteratedGreedyImprovedResult = callFinder(new FinderIteratedGreedyImproved(graph));
		System.out.println("\tIterated Greedy Mejorado Original: " + graph.getTotalWeight());
		System.out.println("\tIterated Greedy Mejorado New: " + iteratedGreedyImprovedResult.weight);
		
		if (destructiveImprovedResult.weight > iteratedGreedyImprovedResult.weight) {
			System.out.println("-------------------");
			System.out.println("\tMEJORA ENCONTRADA EN ITERATED GREEDY IMPROVED");
			System.out.println("-------------------");
		}
		
		// Iterated Greedy Improved Custom - constructive, remove random, add greedy
		Result iteratedGreedyCustomOneResult = callFinder(new FinderIteratedGreedyCustom(
				graph,
				IGType.CONSTRUCTIVE_REMOVE_RANDOM_ADD_GREEDY
		));
		System.out.println("\tIterated Greedy Custom 1 Original: " + graph.getTotalWeight());
		System.out.println("\tIterated Greedy Custom 1 New: " + iteratedGreedyCustomOneResult.weight);
		
		// Iterated Greedy Improved Custom - constructive, add random, remove greedy
		Result iteratedGreedyCustomTwoResult = callFinder(new FinderIteratedGreedyCustom(
				graph,
				IGType.CONSTRUCTIVE_ADD_RANDOM_REMOVE_GREEDY
		));
		System.out.println("\tIterated Greedy Custom 2 Original: " + graph.getTotalWeight());
		System.out.println("\tIterated Greedy Custom 2 New: " + iteratedGreedyCustomTwoResult.weight);
		
		// Iterated Greedy Improved Custom - destructive, remove random, add greedy
		Result iteratedGreedyCustomThreeResult = callFinder(new FinderIteratedGreedyCustom(
				graph,
				IGType.DESTRUCTIVE_REMOVE_RANDOM_ADD_GREEDY
		));
		System.out.println("\tIterated Greedy Custom 3 Original: " + graph.getTotalWeight());
		System.out.println("\tIterated Greedy Custom 3 New: " + iteratedGreedyCustomThreeResult.weight);
		
		// Iterated Greedy Improved Custom - destructive, add random, remove greedy
		Result iteratedGreedyCustomFourResult = callFinder(new FinderIteratedGreedyCustom(
				graph,
				IGType.DESTRUCTIVE_ADD_RANDOM_REMOVE_GREEDY
		));
		System.out.println("\tIterated Greedy Custom 4 Original: " + graph.getTotalWeight());
		System.out.println("\tIterated Greedy Custom 4 New: " + iteratedGreedyCustomFourResult.weight);
		
		// FinderTest
		/*Map<Integer, Float> testMap = callFinder(new FinderTest(graph));
		System.out.println("\tTest Original: " + graph.getTotalWeight());
		System.out.println("\tTest New: " + testMap.get(0));*/
		
		// Save to CSV
		CSV.appendResult(
			RESULTS_CSV_FILEPATH,
			file.getName(), graph.getTotalWeight(),
			constructiveResult.weight, constructiveResult.time,
			constructiveImprovedResult.weight, constructiveImprovedResult.time,
			destructiveResult.weight, destructiveResult.time,
			destructiveImprovedResult.weight, destructiveImprovedResult.time,
			iteratedGreedyResult.weight, iteratedGreedyResult.time,
			iteratedGreedyImprovedResult.weight, iteratedGreedyImprovedResult.time,
			
			iteratedGreedyCustomOneResult.weight, iteratedGreedyCustomOneResult.time,
			iteratedGreedyCustomTwoResult.weight, iteratedGreedyCustomTwoResult.time,
			iteratedGreedyCustomThreeResult.weight, iteratedGreedyCustomThreeResult.time,
			iteratedGreedyCustomFourResult.weight, iteratedGreedyCustomFourResult.time
		);
	}
	
	/**
	 * Function to run a finder
	 * @param finder
	 * @return Map - results with weight and time
	 */
	private static Result callFinder(Finder finder) {
		long startTimeAlg = System.currentTimeMillis();
		Graph resultTest = finder.getMinimumCoverTree();
		long totalTimeAlg = System.currentTimeMillis() - startTimeAlg;
		float weighAlg = resultTest.getTotalWeight();
		
		Main main = new Main();
		Result result = main.new Result(weighAlg, totalTimeAlg);
		
		return result;
	}
	
	/**
	 * Get the best settings for an Iterated Greedy
	 * @param finder
	 * @return [weight, time, destroyPercentage, numOfTries]
	 */
	private Map<Integer, Float> runIteratedGreedy(Finder finder) {
		Map<Integer, Float> resultMap = new HashMap<>();
		
		Float bestWeight = null;
		for (float percentage = 0.1f; percentage <= 0.9f; percentage = percentage + 0.1f) {
			for (float tries = 100; tries <= 500; tries = tries + 100) {
				long startTimeAlg = System.currentTimeMillis();
				Graph resultIteratedGreedyImproved = finder.getMinimumCoverTree();
				long totalTimeAlg = System.currentTimeMillis() - startTimeAlg;
				float weighAlg = resultIteratedGreedyImproved.getTotalWeight();
				
				if (bestWeight == null || weighAlg < bestWeight) {
					bestWeight = weighAlg;
					
					resultMap.put(0, weighAlg);
					resultMap.put(1, Float.parseFloat(totalTimeAlg + ""));
					resultMap.put(2, percentage);
					resultMap.put(3, tries);
				}
			}
		}
		
		return resultMap;
	}
	
}
