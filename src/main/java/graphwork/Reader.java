package graphwork;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Reader {

	public static Graph createGraph(String path) throws IOException {
		FileReader fileReader = new FileReader(path);

		try (BufferedReader br = new BufferedReader(fileReader)) {
			// Read first line
			String line = br.readLine();

			// Edges & vertices
			String[] firstLine = line.split(" ");
			int numVertices = Integer.parseInt(firstLine[0]);
			int numEdges = Integer.parseInt(firstLine[1]);
			
			// Create graph
			Graph newGraph = new Graph();
			
			line = br.readLine();

			// Add all edges & vertices to the graph
			int i = 0;
			List<Edge> arrayEdges = new ArrayList<>();
			while (line != null) {
				String[] auxLine = line.split(" ");
				
				newGraph.addEdge(Integer.parseInt(auxLine[0]), Integer.parseInt(auxLine[1]), Float.parseFloat(auxLine[2]));

				line = br.readLine();
				i++;
			}
			
			return newGraph;
		}
	}

}
