package graphwork.utils;

import graphwork.Result;
import graphwork.ResultIG;
import graphwork.finder.FinderIteratedGreedyCustom;
import graphwork.finder.FinderIteratedGreedyImproved;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public class CSV {
	
	private static final String COMMA_DELIMITER = ",";
    private static final String NEW_LINE_SEPARATOR = "\n";

	public static final String CONSTRUCTIVE_FILE_HEADER = ""
			+ "Graph Name,"
			+ "Weigh of Original Graph,"
			+ "Weigh C1,Time C1 (ms),"
			+ "Weigh C2,Time C2 (ms),"
			+ "Weigh D1,Time D1 (ms),"
			+ "Weigh D2,Time D2 (ms)";
	
	public static final String IG_FILE_HEADER = ""
			+ "Graph Name,"
			+ "Weigh of Original Graph,"
			+ "Weigh IG1,Time IG1 (ms),"
			+ "Weigh IG2,Time IG2 (ms),"
			+ "Weigh IGM1,Time IGM1 (ms),"
			+ "Weigh IGM2,Time IGM2 (ms)";
	
	public static final String IG_PARAMS_FILE_HEADER = ""
			+ "iterations,"
			+ "weight1,time1,"
			+ "weight2,time2,"
			+ "weight3,time3,"
			+ "weight4,time4,"
			+ "weight5,time5,"
			+ "weight6,time6,"
			+ "weight7,time7,"
			+ "weight8,time8,"
			+ "weight9,time9";
	
	public static final String FINAL_IG_FILE_HEADER = ""
			+ "instance,originalweight,"
			+ "weightigm2,timeigm2";
	
	public static void createResultsFile(String filePath, String fileHeader) {
		PrintWriter writer;
		FileWriter fileWriter = null;
		try {
			// Reset file
			writer = new PrintWriter(filePath);
			writer.print("");
			
			// Add header
			fileWriter = new FileWriter(filePath);
			fileWriter.append(fileHeader);
			
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

	public static void appendRowStats(
			String filePath, String name, Float totalWeight, List<Result> listResult) {
		
		FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(filePath, true);
			
			// Graph name identifier
			fileWriter.append(name);
			fileWriter.append(COMMA_DELIMITER);
			fileWriter.append(totalWeight.toString());
			fileWriter.append(COMMA_DELIMITER);
			
			for (Result aux : listResult) {
				fileWriter.append(aux.weight.toString());
				fileWriter.append(COMMA_DELIMITER);
				fileWriter.append(aux.time.toString());
				if (listResult.indexOf(aux) < listResult.size() - 1) {
					fileWriter.append(COMMA_DELIMITER);
				}
			}
			
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
	
	public static void appendRowIGStats(
			String filePath, List<ResultIG> listResult) {
		
		FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(filePath, true);
			
			// Num of tries
			fileWriter.append(listResult.get(0).tries.toString());
			fileWriter.append(COMMA_DELIMITER);
			
			for (Result aux : listResult) {
				fileWriter.append(aux.weight.toString());
				fileWriter.append(COMMA_DELIMITER);
				fileWriter.append(aux.time.toString());
				if (listResult.indexOf(aux) < listResult.size() - 1) {
					fileWriter.append(COMMA_DELIMITER);
				}
			}
			
			// New line
			fileWriter.append(NEW_LINE_SEPARATOR);
			
            System.out.println("Line of iterations appended to CSV file");
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
