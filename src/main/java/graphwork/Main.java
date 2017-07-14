package graphwork;

import graphwork.finder.Finder;
import graphwork.utils.CSV;
import graphwork.utils.Reader;
import graphwork.graph.Graph;
import graphwork.finder.FinderDestructive;
import graphwork.finder.FinderDestructiveImproved;
import graphwork.finder.FinderConstructive;
import graphwork.finder.FinderConstructiveImproved;
import graphwork.finder.FinderIteratedGreedyCustom;
import graphwork.finder.FinderIteratedGreedyImproved;
import graphwork.finder.IteratedGreedy;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main {

	public static final String RESULTS_CONSTRUCTIVE_FILEPATH = "./csv/results_constructive.csv";
	public static final String RESULTS_IG_FILEPATH = "./csv/results_ig.csv";
	public static final String RESULTS_IG_PARAMS_FILEPATH = "./csv/results_igm2_params.csv";
	public static final String RESULTS_FINAL_IG_FILEPATH = "./csv/results_igm2_final.csv";
	
	public static final float PORCENTAGE_TEST = 0.4f;
	
	public static void main(String[] args) {
		//executePhaseConstructive();
		
		//executePhaseIGX();
		
		//executePhaseIGM2();
		
		executePhaseFinalIG();
	}
	
	/**
	 * Execute constructive finders
	 */
	private static void executePhaseConstructive() {
		System.out.println("PHASE 1 Constructive");
		System.out.println("Calculating tree cover of " + PORCENTAGE_TEST * 100 + "% of  graphs...");
		
		// Create results file
		CSV.createResultsFile(RESULTS_CONSTRUCTIVE_FILEPATH, CSV.CONSTRUCTIVE_FILE_HEADER);
		
		// Set of small graphs+
		final File folder = new File("./dtp_small");
		int index = (int) Math.ceil(folder.listFiles().length * PORCENTAGE_TEST);
		for (final File fileEntry : folder.listFiles()) {
			// Exclude bad files
			if (!fileEntry.isDirectory()) {
				if (index <= 0) {
					break;
				}
				
				calculateConstructive(fileEntry);
				
				index--;
			}
		}
		
		// Set of large graphs
		final File folderLarge = new File("./dtp_large");
		int index2 = (int) Math.ceil(folder.listFiles().length * PORCENTAGE_TEST);
		for (final File fileEntry : folderLarge.listFiles()) {
			// Exclude bad files
			if (!fileEntry.isDirectory()
				&& !fileEntry.getName().equals("dtp_300_1000_0.txt")
				&& !fileEntry.getName().equals("dtp_300_1000_1.txt")
				&& !fileEntry.getName().equals("dtp_300_1000_2.txt")) {
				if (index2 <= 0) {
					break;
				}
				
				calculateConstructive(fileEntry);
				
				index2--;
			}
		}
	}
	
	private static void executePhaseIGX() {
		System.out.println("PHASE 1 Iterated Greedy");
		System.out.println("Calculating tree cover of " + PORCENTAGE_TEST * 100 + "% of graphs...");
		
		// Create results file
		CSV.createResultsFile(RESULTS_IG_FILEPATH, CSV.IG_FILE_HEADER);
		
		// Set of small graphs
		final File folder = new File("./dtp_small");
		int index = (int) Math.ceil(folder.listFiles().length * PORCENTAGE_TEST);
		for (final File fileEntry : folder.listFiles()) {
			// Exclude bad files
			if (!fileEntry.isDirectory()) {
				if (index <= 0) {
					break;
				}
				
				calculateIGX(fileEntry);
				
				index--;
			}
		}
		
		// Set of large graphs
		final File folderLarge = new File("./dtp_large");
		int index2 = (int) Math.ceil(folder.listFiles().length * PORCENTAGE_TEST);
		for (final File fileEntry : folderLarge.listFiles()) {
			// Exclude bad files
			if (!fileEntry.isDirectory()
				&& !fileEntry.getName().equals("dtp_300_1000_0.txt")
				&& !fileEntry.getName().equals("dtp_300_1000_1.txt")
				&& !fileEntry.getName().equals("dtp_300_1000_2.txt")) {
				if (index2 <= 0) {
					break;
				}
				
				calculateIGX(fileEntry);
				
				index2--;
			}
		}
	}
	
	private static void executePhaseIGM2() {
		System.out.println("PHASE 2");
		System.out.println("Calculating tree cover of all graphs...");
		
		// Create results file
		CSV.createResultsFile(RESULTS_IG_PARAMS_FILEPATH, CSV.IG_PARAMS_FILE_HEADER);
		
		for (int numOfTries = 100; numOfTries <= 500; numOfTries = numOfTries + 100) {
			
			List<ResultIG> listResult = new ArrayList<>();
			
			for (int i = 1; i <= 9; i++) {
				float destroyPrecentage = (float) i / 10;
				
				float weight = 0;
				long time = 0;
				int totalNum = 0;
				ResultIG resultIG = null;
				
				// Set of small graphs
				final File folder = new File("./dtp_small");
				int index = (int) Math.ceil(folder.listFiles().length * PORCENTAGE_TEST);
				for (final File fileEntry : folder.listFiles()) {
					// Exclude bad files
					if (!fileEntry.isDirectory()) {
						if (index <= 0) {
							break;
						}
						
						resultIG = calculateIGM2(fileEntry, destroyPrecentage, numOfTries);
						weight += resultIG.weight;
						time += resultIG.time;
						totalNum++;
						
						index--;
					}
				}

				// Set of large graphs
				final File folderLarge = new File("./dtp_large");
				int index2 = (int) Math.ceil(folder.listFiles().length * PORCENTAGE_TEST);
				for (final File fileEntry : folderLarge.listFiles()) {
					// Exclude bad files
					if (!fileEntry.isDirectory()
						&& !fileEntry.getName().equals("dtp_300_1000_0.txt")
						&& !fileEntry.getName().equals("dtp_300_1000_1.txt")
						&& !fileEntry.getName().equals("dtp_300_1000_2.txt")) {
						if (index2 <= 0) {
							break;
						}
						
						resultIG = calculateIGM2(fileEntry, destroyPrecentage, numOfTries);
						weight += resultIG.weight;
						time += resultIG.time;
						totalNum++;
						
						index2--;
					}
				}
				
				ResultIG result = new ResultIG();
				result.weight = weight / totalNum;
				result.time = time / totalNum;
				result.tries = numOfTries;
				result.percentage = destroyPrecentage;
				listResult.add(result);
			}

			// Append line to CSV
			CSV.appendRowIGStats(
				RESULTS_IG_PARAMS_FILEPATH,
				listResult
			);
		}
	}
	
	private static void executePhaseFinalIG() {
		System.out.println("PHASE 2");
		System.out.println("Calculating tree cover of all graphs...");
		
		// Create results file
		CSV.createResultsFile(RESULTS_FINAL_IG_FILEPATH, CSV.FINAL_IG_FILE_HEADER);
		
		// Set of small graphs
		final File folder = new File("./dtp_small");
		for (final File fileEntry : folder.listFiles()) {
			// Exclude bad files
			if (!fileEntry.isDirectory()) {
				calculateFinalIG(fileEntry);
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
				calculateFinalIG(fileEntry);
			}
		}
	}
	
	/**
	 * Constructive Stats
	 * @param file 
	 */
	private static void calculateConstructive(File file) {
		System.out.println("Calculating graph: " + file.getName());
		
		Graph graph;
		try {
			graph = Reader.createGraph(file.getAbsolutePath());
		} catch (IOException ignored) {
			System.out.println("Bad graph file");
			return;
		}
		
		System.out.println("\tOriginal: " + graph.getTotalWeight());
		
		// Constructive (C1)
		Result constructiveResult = callFinder(new FinderConstructive(graph));
		System.out.println("\tConstructive New: " + constructiveResult.weight);
		
		// Constructive Improved (C2)
		Result constructiveImprovedResult = callFinder(new FinderConstructiveImproved(graph));
		System.out.println("\tConstructive Mejorado New: " + constructiveImprovedResult.weight);
		
		// Destructive (D1)
		Result destructiveResult = callFinder(new FinderDestructive(graph));
		System.out.println("\tDestructivo New: " + destructiveResult.weight);
		
		// Destructive Improved (D2)
		Result destructiveImprovedResult = callFinder(new FinderDestructiveImproved(graph));
		System.out.println("\tDestructivo Mejorado New: " + destructiveImprovedResult.weight);
		
		List<Result> listResult = new ArrayList<>();
		listResult.add(constructiveResult);
		listResult.add(constructiveImprovedResult);
		listResult.add(destructiveResult);
		listResult.add(destructiveImprovedResult);
		
		// Save to CSV
		CSV.appendRowStats(
			RESULTS_CONSTRUCTIVE_FILEPATH,
			file.getName(), graph.getTotalWeight(),
			listResult
		);
	}
	
	/**
	 * IG
	 * @param file 
	 */
	private static void calculateIGX(File file) {
		System.out.println("Calculating graph: " + file.getName());
		
		Graph graph;
		try {
			graph = Reader.createGraph(file.getAbsolutePath());
		} catch (IOException ignored) {
			System.out.println("Bad graph file");
			return;
		}
		
		float percentage = 0.4f;
		int tries = 300;
				
		System.out.println("\tOriginal: " + graph.getTotalWeight());
		
		// Iterated Greedy Custom - destructive, remove random, add greedy
		Result iteratedGreedyCustomOneResult = callFinder(new FinderIteratedGreedyCustom(
				graph,
				FinderIteratedGreedyCustom.IGType.DESTRUCTIVE_REMOVE_RANDOM_ADD_GREEDY
		), percentage, tries);
		System.out.println("\tIterated Greedy 1: " + iteratedGreedyCustomOneResult.weight);
		
		// Iterated Greedy Custom - destructive, add random, remove greedy
		Result iteratedGreedyCustomTwoResult = callFinder(new FinderIteratedGreedyCustom(
				graph,
				FinderIteratedGreedyCustom.IGType.DESTRUCTIVE_ADD_RANDOM_REMOVE_GREEDY
		), percentage, tries);
		System.out.println("\tIterated Greedy 2: " + iteratedGreedyCustomTwoResult.weight);
		
		// Iterated Greedy Improved - with "destructive for constuctive phase"
		Result iteratedGreedyImprovedOneResult = callFinder(new FinderIteratedGreedyImproved(
				graph,
				FinderIteratedGreedyImproved.IGType.DESTRUCTIVE_REMOVE_RANDOM_ADD_GREEDY
		), percentage, tries);
		System.out.println("\tIterated Greedy Mejorado 1: " + iteratedGreedyImprovedOneResult.weight);
		
		// Iterated Greedy Improved - with "destructive for constuctive phase"
		Result iteratedGreedyImprovedTwoResult = callFinder(new FinderIteratedGreedyImproved(
				graph,
				FinderIteratedGreedyImproved.IGType.DESTRUCTIVE_ADD_RANDOM_REMOVE_GREEDY
		), percentage, tries);
		System.out.println("\tIterated Greedy Mejorado 2: " + iteratedGreedyImprovedTwoResult.weight);
		
		List<Result> listResult = new ArrayList<>();
		listResult.add(iteratedGreedyCustomOneResult);
		listResult.add(iteratedGreedyCustomTwoResult);
		listResult.add(iteratedGreedyImprovedOneResult);
		listResult.add(iteratedGreedyImprovedTwoResult);
		
		// Save to CSV
		CSV.appendRowStats(
			RESULTS_IG_FILEPATH,
			file.getName(), graph.getTotalWeight(),
			listResult
		);
	}
	
	/**
	 * IGM2
	 * @param file 
	 */
	private static ResultIG calculateIGM2(File file, float destroyPercentage, int numOfTries) {
		System.out.println("Calculating graph: " + file.getName());
		
		Graph graph;
		try {
			graph = Reader.createGraph(file.getAbsolutePath());
		} catch (IOException ignored) {
			System.out.println("Bad graph file");
			return null;
		}
		
		ResultIG resultIG = callFinder(new FinderIteratedGreedyImproved(
				graph,
				FinderIteratedGreedyImproved.IGType.DESTRUCTIVE_ADD_RANDOM_REMOVE_GREEDY
		), destroyPercentage, numOfTries);
		
		return resultIG;
	}
	
	/**
	 * Final IG
	 * @param file 
	 */
	private static void calculateFinalIG(File file) {
		System.out.println("Calculating graph: " + file.getName());
		
		Graph graph;
		try {
			graph = Reader.createGraph(file.getAbsolutePath());
		} catch (IOException ignored) {
			System.out.println("Bad graph file");
			return;
		}
		
		ResultIG result = callFinder(new FinderIteratedGreedyImproved(
				graph,
				FinderIteratedGreedyImproved.IGType.DESTRUCTIVE_ADD_RANDOM_REMOVE_GREEDY
		), 0.4f, 500);

		List<Result> listResult = new ArrayList<>();
		listResult.add(result);
		
		// Save to CSV
		CSV.appendRowStats(
			RESULTS_FINAL_IG_FILEPATH,
			file.getName(), graph.getTotalWeight(),
			listResult
		);
	}
	
	/**
	 * Function to run a finder
	 * @param finder
	 * @return Map - results with weight and time
	 */
	private static Result callFinder(Finder finder) {
		long startTimeAlg = System.currentTimeMillis();
		Graph resultTest = finder.getMinimumTreeCover();
		long totalTimeAlg = System.currentTimeMillis() - startTimeAlg;
		float weighAlg = resultTest.getTotalWeightOfMST();
		
		Result result = new Result(weighAlg, totalTimeAlg);
		
		return result;
	}

	/**
	 * Function to run a finder
	 * @param finder
	 * @return Map - results with weight and time
	 */
	private static ResultIG callFinder(IteratedGreedy finder, float randomPercentage, int numOfTries) {
		//long startTimeAlg = System.currentTimeMillis();
		long startTimeAlg = (long) (System.nanoTime() / 1e6);
		ResultIG result = null;
		try {
			Graph resultTest = finder.getMinimumTreeCover(randomPercentage, numOfTries);
			//long totalTimeAlg = System.currentTimeMillis() - startTimeAlg;
			long totalTimeAlg = (long) (((long) System.nanoTime() / 1e6) - startTimeAlg);
			float weighAlg = resultTest.getTotalWeightOfMST();
			result = new ResultIG(weighAlg, totalTimeAlg, randomPercentage, numOfTries);
		} catch (Exception ex) {
		}
		
		return result;
	}
	
	/**
	 * Get the best settings for an Iterated Greedy
	 * @param finder
	 * @return [weight, time, destroyPercentage, numOfTries]
	 */
	private static List<ResultIG> runIteratedGreedy(IteratedGreedy finder) {
		List<ResultIG> listResultIG = new ArrayList<>();
		
		for (float percentage = 0.1f; percentage <= 0.9f; percentage = percentage + 0.1f) {
			for (int tries = 100; tries <= 500; tries = tries + 100) {
				Graph resultIteratedGreedyImproved;
				try {
					long startTimeAlg = System.currentTimeMillis();
					resultIteratedGreedyImproved = finder.getMinimumTreeCover(
						percentage,
						tries
					);
					long totalTimeAlg = System.currentTimeMillis() - startTimeAlg;
					float weightAlg = resultIteratedGreedyImproved.getTotalWeight();

					ResultIG resultIG = new ResultIG();
					resultIG.weight = weightAlg;
					resultIG.time = totalTimeAlg;
					resultIG.percentage = percentage;
					resultIG.tries = tries;

					listResultIG.add(resultIG);
				} catch (Exception ignored) {
				}
			}
		}
		
		return listResultIG;
	}
	
}
