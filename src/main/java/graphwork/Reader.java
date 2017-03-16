package graphwork;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Reader {

	public static Graph createGraph(String path) throws IOException {
		FileReader fileReader = new FileReader(path);
		BufferedReader br = new BufferedReader(fileReader);
		
		// Ignore first line (Edges & vertices)
		br.readLine();

		// Create graph
		Graph newGraph = new Graph();

		// Add all edges & vertices to the graph
		String line = br.readLine();
		while (line != null) {
			String[] auxLine = line.split(" ");

			// TODO: change weight
			//newGraph.addEdge(Integer.parseInt(auxLine[0]), Integer.parseInt(auxLine[1]), Float.parseFloat(auxLine[2]));
			newGraph.addEdge(Integer.parseInt(auxLine[0]), Integer.parseInt(auxLine[1]), 1f);

			line = br.readLine();
		}

		return newGraph;
	}

}
