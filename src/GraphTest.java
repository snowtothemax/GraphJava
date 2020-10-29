import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Random;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;



/**
 * Test Graph class that adds and removes vertices to the graph.
 * 
 * Written By Max Johnson
 */
public class GraphTest {

	// The graph to be tested
	static Graph graph;

	// the file to read from
	static String readFrom;

	/** initializes the file to read from */
	@BeforeAll
	public static void beforeClass() throws Exception {
		readFrom = "valid.json";
	}

	/** Initializes the graph before each method */
	@BeforeEach
	public void setUp() throws Exception {
		graph = new Graph();
	}

	/** Not much to do, just make sure that variables are reset */
	@AfterEach
	public void tearDown() throws Exception {
		graph = null;
	}

	/**
	 * Tests adding a vertex to the graph and checks if the graph contains the
	 * vertices
	 */
	@Test
	public void test00_testAddVertex() {

		// adds all of these strings to the graph
		graph.addVertex("A");
		graph.addVertex("B");
		graph.addVertex("C");

		// checks if the graph order is correct or not and fails the test if not
		if (graph.order() != 3) {
			fail("ERROR: The graph didnt add the vertices correctly");
		}
	}

	/**
	 * Tests if the addEdge() method works correctly
	 */
	@Test
	public void test01_testAddEdge() {
		// adds all of these strings to the graph
		graph.addVertex("A");
		graph.addVertex("B");
		graph.addVertex("C");

		// adds the edge between A and B, from A to B
		graph.addEdge("A", "B");

		// checks if the adjacent vertices of A equals B
		if (!graph.getAdjacentVerticesOf("A").get(0).equals("B")) {
			fail("ERROR: The edge was not added between the vertices.");
		}
	}

	/**
	 * Tests the removeVertex() method of the Graph ADT
	 */
	@Test
	public void test02_testRemoveVertex() {
		// adds all of these strings to the graph
		graph.addVertex("A");
		graph.addVertex("B");
		graph.addVertex("C");

		// removes vertex B from the graph
		graph.removeVertex("B");

		// checks if the order was decremented
		if (graph.order() != 2) {
			fail("ERROR: the method does not work correctly");
		}
	}

	/** Tests if the removeEdge works correctly **/
	@Test
	public void test03_testRemoveVertex() {
		// adds all of these strings to the graph
		graph.addVertex("A");
		graph.addVertex("B");
		graph.addVertex("C");

		// adds the edge between A and B, from A to B
		graph.addEdge("A", "B");

		// removes the edge from A and B
		graph.removeEdge("A", "B");

		// checks if the adjacent vertices of A equals B
		if (!graph.getAdjacentVerticesOf("A").get(0).equals("B")) {
			fail("ERROR: The edge was not added between the vertices.");
		}
	}

	/** Tests if the getAllVertices function works correctly **/
	@Test
	public void test04_testGetAllVertices() {
		// adds all of these strings to the graph
		graph.addVertex("A");
		graph.addVertex("B");
		graph.addVertex("C");

		// adds the edge between A and B, from A to B
		graph.addEdge("A", "B");
		
		ArrayList<String> list = (ArrayList<String>) graph.getAllVertices();
		
		//fails if the list is empty
		if(list.isEmpty()) {
			fail("ERROR: The listfor all the vertices was empty");
		}
	}
}
