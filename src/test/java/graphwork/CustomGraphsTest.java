package graphwork;

import static org.junit.Assert.assertTrue;
import org.junit.Test;

public class CustomGraphsTest {
	
	@Test
	public void myGraph1Test() {
		Graph graph = new Graph();
		
		graph.addEdge(0, 1, 1);
		graph.addEdge(0, 2, 2);
		graph.addEdge(1, 2, 4);
		graph.addEdge(1, 3, 4);
		graph.addEdge(2, 3, 5);
		graph.addEdge(3, 4, 5);
		
		Finder finder = new Finder(graph);
		Graph result = finder.getMinimumCoverTree();
		
		// Validate result
		assertTrue(result.isTree());
		assertTrue(result.isVertexCoverOf(graph));
		
		// Correct tree
		assertTrue(result.getTotalWeight() <= 12);
	}
	
}
