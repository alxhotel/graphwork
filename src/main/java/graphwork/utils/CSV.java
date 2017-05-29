package graphwork.utils;

import graphwork.finder.FinderIteratedGreedy;
import graphwork.finder.FinderIteratedGreedyCustom;
import graphwork.finder.FinderIteratedGreedyImproved;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class CSV {
	
	private static final String COMMA_DELIMITER = ",";
    private static final String NEW_LINE_SEPARATOR = "\n";

	private static final String FILE_HEADER = ""
			+ "Graph Name,"
			+ "Weigh of Original Graph,"
			+ "Weigh of Most Connected,Time of Most Connected (ms),"
			+ "Weigh of Most Connected Improved,Time of Most Connected Improved (ms),"
			+ "Weigh of Least Connected,Time of Least Connected (ms),"
			+ "Weigh of Least Connected Improved,Time of Least Connected Improved (ms),"
			+ "Weigh of Iterated Greedy #" + FinderIteratedGreedy.DEFAULT_NUM_TRIES + ",Time of Iterated Greedy #" + FinderIteratedGreedy.DEFAULT_NUM_TRIES + " (ms),"
			+ "Weigh of Iterated Greedy Improved #" + FinderIteratedGreedyImproved.DEFAULT_NUM_TRIES + ",Time of Iterated Greedy Improved #" + FinderIteratedGreedyImproved.DEFAULT_NUM_TRIES + " (ms),"
			
			+ "Weigh of Iterated Greedy Custom 1 #" + FinderIteratedGreedyCustom.DEFAULT_NUM_TRIES + ",Time of Iterated Greedy Custom 1 #" + FinderIteratedGreedyCustom.DEFAULT_NUM_TRIES + " (ms),"
			+ "Weigh of Iterated Greedy Custom 2 #" + FinderIteratedGreedyCustom.DEFAULT_NUM_TRIES + ",Time of Iterated Greedy Custom 2 #" + FinderIteratedGreedyCustom.DEFAULT_NUM_TRIES + " (ms),"
			+ "Weigh of Iterated Greedy Custom 3 #" + FinderIteratedGreedyCustom.DEFAULT_NUM_TRIES + ",Time of Iterated Greedy Custom 3 #" + FinderIteratedGreedyCustom.DEFAULT_NUM_TRIES + " (ms),"
			+ "Weigh of Iterated Greedy Custom 4 #" + FinderIteratedGreedyCustom.DEFAULT_NUM_TRIES + ",Time of Iterated Greedy Custom 4 #" + FinderIteratedGreedyCustom.DEFAULT_NUM_TRIES + " (ms)";
	
	public static void createResultsFile(String filePath) {
		PrintWriter writer;
		FileWriter fileWriter = null;
		try {
			// Reset file
			writer = new PrintWriter(filePath);
			writer.print("");
			
			// Add header
			fileWriter = new FileWriter(filePath);
			fileWriter.append(FILE_HEADER);
			
			// New line
			fileWriter.append(NEW_LINE_SEPARATOR);
			
			System.out.println("CSV file created");
		} catch (FileNotFoundException ex) {
			// Do nothing
		} catch (IOException ex) {
			// Do nothing
		} finally {
            try {
				if (fileWriter != null) {
					fileWriter.flush();
					fileWriter.close();
				}
            } catch (IOException e) {
                System.out.println("Error while flushing and closing");
            }
        }
	}
	
	public static void appendResult(String filePath,
			String name, Float weightOriginal,
			Float weightMostConnected, Long timeMostConnected,
			Float weightMostConnectedImproved, Long timeMostConnectedImproved,
			Float weightLeastConnected, Long timeLeastConnected,
			Float weightLeastConnectedImproved, Long timeLeastConnectedImproved,
			Float weightIteratedGreedy, Long timeIteratedGreedy,
			Float weightIteratedGreedyImproved, Long timeIteratedGreedyImproved,
			Float weightIteratedGreedyCustomOne, Long timeIteratedGreedyCustomOne,
			Float weightIteratedGreedyCustomTwo, Long timeIteratedGreedyCustomTwo,
			Float weightIteratedGreedyCustomThree, Long timeIteratedGreedyCustomThree,
			Float weightIteratedGreedyCustomFour, Long timeIteratedGreedyCustomFour) {
		
		FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(filePath, true);
			
			// Graph name identifier
			fileWriter.append(name);
			fileWriter.append(COMMA_DELIMITER);
			fileWriter.append(weightOriginal.toString());
			fileWriter.append(COMMA_DELIMITER);
			
			// Most connected
			fileWriter.append(weightMostConnected.toString());
			fileWriter.append(COMMA_DELIMITER);
			fileWriter.append(timeMostConnected.toString());
			fileWriter.append(COMMA_DELIMITER);
			
			// Most connected Improved
			fileWriter.append(weightMostConnectedImproved.toString());
			fileWriter.append(COMMA_DELIMITER);
			fileWriter.append(timeMostConnectedImproved.toString());
			fileWriter.append(COMMA_DELIMITER);

			// Least connected
			fileWriter.append(weightLeastConnected.toString());
			fileWriter.append(COMMA_DELIMITER);
			fileWriter.append(timeLeastConnected.toString());
			fileWriter.append(COMMA_DELIMITER);
			
			// Least connected improved
			fileWriter.append(weightLeastConnectedImproved.toString());
			fileWriter.append(COMMA_DELIMITER);
			fileWriter.append(timeLeastConnectedImproved.toString());
			fileWriter.append(COMMA_DELIMITER);
			
			// Iterated greedy
			fileWriter.append(weightIteratedGreedy.toString());
			fileWriter.append(COMMA_DELIMITER);
			fileWriter.append(timeIteratedGreedy.toString());
			fileWriter.append(COMMA_DELIMITER);
			
			// Iterated greedy improved
			fileWriter.append(weightIteratedGreedyImproved.toString());
			fileWriter.append(COMMA_DELIMITER);
			fileWriter.append(timeIteratedGreedyImproved.toString());
			fileWriter.append(COMMA_DELIMITER);
			
			// Iterated greedy custom 1
			fileWriter.append(weightIteratedGreedyCustomOne.toString());
			fileWriter.append(COMMA_DELIMITER);
			fileWriter.append(timeIteratedGreedyCustomOne.toString());
			fileWriter.append(COMMA_DELIMITER);
			
			// Iterated greedy custom 2
			fileWriter.append(weightIteratedGreedyCustomTwo.toString());
			fileWriter.append(COMMA_DELIMITER);
			fileWriter.append(timeIteratedGreedyCustomTwo.toString());
			fileWriter.append(COMMA_DELIMITER);
			
			// Iterated greedy custom 3
			fileWriter.append(weightIteratedGreedyCustomThree.toString());
			fileWriter.append(COMMA_DELIMITER);
			fileWriter.append(timeIteratedGreedyCustomThree.toString());
			fileWriter.append(COMMA_DELIMITER);
			
			// Iterated greedy custom 4
			fileWriter.append(weightIteratedGreedyCustomFour.toString());
			fileWriter.append(COMMA_DELIMITER);
			fileWriter.append(timeIteratedGreedyCustomFour.toString());
			
			// New line
			fileWriter.append(NEW_LINE_SEPARATOR);
			
            System.out.println("Graph appended to CSV file");
        } catch (IOException ignored) {
            System.out.println("Error while creating CSV file");
        } finally {
            try {
				if (fileWriter != null) {
					fileWriter.flush();
					fileWriter.close();
				}
            } catch (IOException ignored) {
                System.out.println("Error while flushing and closing");
            }
        }
	}
	
}
