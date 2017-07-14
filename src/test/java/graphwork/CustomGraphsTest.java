package graphwork;

import graphwork.graph.Graph;
import graphwork.finder.FinderConstructive;
import graphwork.finder.Finder;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

public class CustomGraphsTest {
	
	@Test
	public void myGraphConstructiveTest() {
		Graph graph = new Graph();
		
		graph.addEdge(0, 1, 1);
		graph.addEdge(0, 2, 2);
		graph.addEdge(1, 2, 4);
		graph.addEdge(1, 3, 4);
		graph.addEdge(2, 3, 5);
		graph.addEdge(3, 4, 5);
		
		Finder finder = new FinderConstructive(graph);
		Graph result = finder.getMinimumTreeCover();
		
		// Validate result
		assertTrue(result.isTree());
		assertTrue(result.isVertexCoverOf(graph));
		
		// Correct tree
		assertTrue(result.getTotalWeight() <= 12);
	}
	
}
