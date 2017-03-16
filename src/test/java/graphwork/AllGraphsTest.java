package graphwork;

import static graphwork.Reader.createGraph;
import java.io.File;
import java.io.IOException;
import org.junit.Test;
import static org.junit.Assert.*;

public class AllGraphsTest {
	
	@Test
	public void smallGraphTest() {
		final File folder = new File("./dtp_small");
		for (final File fileEntry : folder.listFiles()) {
			if (!fileEntry.isDirectory()) {
				Graph graph = readGraphFile(fileEntry);
				
				oneGraphConstructiveTest(graph);
				oneGraphDestructiveTest(graph);
				oneGraphDestructiveImprovedTest(graph);
			}
		}
	}
	
	@Test
	public void largeGraphTest() {
		final File folder = new File("./dtp_large");
		for (final File fileEntry : folder.listFiles()) {
			// Exclude bad files
			if (!fileEntry.isDirectory()
				&& !fileEntry.getName().equals("dtp_300_1000_0.txt")
				&& !fileEntry.getName().equals("dtp_300_1000_1.txt")
				&& !fileEntry.getName().equals("dtp_300_1000_2.txt")) {
				Graph graph = readGraphFile(fileEntry);
				
				oneGraphConstructiveTest(graph);
				oneGraphDestructiveTest(graph);
				oneGraphDestructiveImprovedTest(graph);
			}
		}
	}
	
	private Graph readGraphFile(File file) {
		Graph graph = null;
		try {
			System.out.println("Testing graph: " + file.getName());
			graph = createGraph(file.getAbsolutePath());
		} catch (IOException ex) {
			fail("Bad file");
		}
		
		assertTrue(graph != null);
		
		return graph;
	}
	
	private void oneGraphConstructiveTest(Graph graph) {
		Finder finder = new FinderConstructive(graph);
		Graph result = finder.getMinimumCoverTree();
		
		// Validate result
		assertTrue(result.isTree());
		assertTrue(result.isVertexCoverOf(graph));
		
		System.out.println("Weight of the tree cover: " + result.getTotalWeight());
	}
	
	private void oneGraphDestructiveTest(Graph graph) {
		Finder finder = new FinderDestructive(graph);
		Graph result = finder.getMinimumCoverTree();
		
		// Validate result
		assertTrue(result.isTree());
		assertTrue(result.isVertexCoverOf(graph));
		
		System.out.println("Weight of the tree cover: " + result.getTotalWeight());
	}
	
	private void oneGraphDestructiveImprovedTest(Graph graph) {
		Finder finder = new FinderDestructiveImproved(graph);
		Graph result = finder.getMinimumCoverTree();
		
		// Validate result
		assertTrue(result.isTree());
		assertTrue(result.isVertexCoverOf(graph));
		
		System.out.println("Weight of the tree cover: " + result.getTotalWeight());
	}
}
