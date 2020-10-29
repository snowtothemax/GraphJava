import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * Filename: PackageManager.java Project: p4 Authors: Max Johnson
 * 
 * PackageManager is used to process json package dependency files and provide
 * function that make that information available to other users.
 * 
 * Each package that depends upon other packages has its own entry in the json
 * file.
 * 
 * Package dependencies are important when building software, as you must
 * install packages in an order such that each package is installed after all of
 * the packages that it depends on have been installed.
 * 
 * For example: package A depends upon package B, then package B must be
 * installed before package A.
 * 
 * This program will read package information and provide information about the
 * packages that must be installed before any given package can be installed.
 * all of the packages in
 * 
 * You may add a main method, but we will test all methods with our own Test
 * classes.
 */

public class PackageManager {

	private Graph graph;

	/*
	 * Package Manager default no-argument constructor.
	 */
	public PackageManager() {
		graph = new Graph();
	}

	/**
	 * Takes in a file path for a json file and builds the package dependency graph
	 * from it.
	 * 
	 * @param jsonFilepath the name of json data file with package dependency
	 *                     information
	 * @throws FileNotFoundException if file path is incorrect
	 * @throws IOException           if the give file cannot be read
	 * @throws ParseException        if the given json cannot be parsed
	 */
	public void constructGraph(String jsonFilePath) throws FileNotFoundException, IOException, ParseException {

		// Parses the JSON File from the JSON file
		Object obj = new JSONParser().parse(new FileReader(jsonFilePath));

		// the object obtained from the file
		JSONObject jo = (JSONObject) obj;

		// Obtains the JSONArray from the jo object with the name "packages"
		JSONArray ja = (JSONArray) jo.get("packages");

		// Loops through each package
		for (int i = 0; i < ja.size(); i++) {

			// Gets each object from each package
			JSONObject otherJo = (JSONObject) ja.get(i);

			// Goes through the dependencies of otherJo and adds them to an ArrayList of
			// String type
			JSONArray currentJA = (JSONArray) otherJo.get("dependencies");
			ArrayList<String> dependencies = new ArrayList<String>();

			// adds each independent dependency to the list
			for (int j = 0; j < currentJA.size(); j++) {
				dependencies.add((String) currentJA.get(j));
			}

			// accesses the current Package name as a String
			String currentPkg = (String) otherJo.get("name");

			// adds the currentPackage to the graph
			graph.addVertex(currentPkg);

			// Links the package to each of its dependencies by adding an edge to the graph
			for (int k = 0; k < dependencies.size(); k++) {
				graph.addEdge(currentPkg, dependencies.get(k));
			}
		}
	}

	/**
	 * Helper method to get all packages in the graph.
	 * 
	 * @return Set<String> of all the packages
	 */
	public Set<String> getAllPackages() {
		return graph.getAllVertices();
	}

	/**
	 * Given a package name, returns a list of packages in a valid installation
	 * order.
	 * 
	 * Valid installation order means that each package is listed before any
	 * packages that depend upon that package.
	 * 
	 * @return List<String>, order in which the packages have to be installed
	 * 
	 * @throws CycleException           if you encounter a cycle in the graph while
	 *                                  finding the installation order for a
	 *                                  particular package. Tip: Cycles in some
	 *                                  other part of the graph that do not affect
	 *                                  the installation order for the specified
	 *                                  package, should not throw this exception.
	 * 
	 * @throws PackageNotFoundException if the package passed does not exist in the
	 *                                  dependency graph.
	 */
	public List<String> getInstallationOrder(String pkg) throws CycleException, PackageNotFoundException {

		// use the topological order algorithm in order to find all the dependencies of
		// a certain
		// package in the graph
		int num = graph.order();

		// creates a stack to store the installation order and a boolean array that
		// assigns each vertex to being unvisited
		Stack<String> st = new Stack<String>();
		// HashMap<String, Boolean> map = new HashMap<String, Boolean>();

		// creates a set of Strings for every vertex in the graph in order to add them
		// to the stack
		Set<String> vertices = graph.getAllVertices();

		// boolean to detect if the package exists or not
		boolean valid = false;

		// adds all vertices to a map to access the visited value
		for (String x : vertices) {
			// map.put(x, false);

			// will become true when pkg is accessed
			if (x.equals(pkg)) {
				valid = true;
			}
		}

		// if the boolean is false it throws an exception
		if (valid == false) {
			throw new PackageNotFoundException();
		}

		// pushes the current package to the stack and creates a list of visited
		// vertices that is empty
		st.push(pkg);
		ArrayList<String> visitedVertices = new ArrayList<String>();

		// loop to go through the list of vertices and determine the topological order
		// of the graph
		while (!st.isEmpty()) {
			// pop the vertex at the top of the stack
			String c = st.pop();

			// accesses the list of all the neighbors of the current vertex c
			ArrayList<String> neighbors = (ArrayList<String>) graph.getAdjacentVerticesOf(c);
			if (!visitedVertices.contains(c)) {
				
				visitedVertices.add(c);
				
				// checks for cycle expections within the graph
				for (String x : neighbors) {
					
					for (int i = 0; i < graph.getAdjacentVerticesOf(x).size(); i++) {
						
						// checks if the graph is cycling between vertices
						if (c.equals(graph.getAdjacentVerticesOf(x).get(i))) {
							throw new CycleException();
						}
					}

					// pushes the currently iterated vertex to the stack
					st.push(x);
				}
			}

		} // end of the while loop

		// reverses the visitedVertices List
		ArrayList<String> reversedList = new ArrayList<String>();
		for (int i = visitedVertices.size() - 1; i >= 0; i--) {
			reversedList.add(visitedVertices.get(i));
		}

		return reversedList;
	}

	/**
	 * Given two packages - one to be installed and the other installed, return a
	 * List of the packages that need to be newly installed.
	 * 
	 * For example, refer to shared_dependecies.json - toInstall("A","B") If package
	 * A needs to be installed and packageB is already installed, return the list
	 * ["A", "C"] since D will have been installed when B was previously installed.
	 * 
	 * @return List<String>, packages that need to be newly installed.
	 * 
	 * @throws CycleException           if you encounter a cycle in the graph while
	 *                                  finding the dependencies of the given
	 *                                  packages. If there is a cycle in some other
	 *                                  part of the graph that doesn't affect the
	 *                                  parsing of these dependencies, cycle
	 *                                  exception should not be thrown.
	 * 
	 * @throws PackageNotFoundException if any of the packages passed do not exist
	 *                                  in the dependency graph.
	 */
	public List<String> toInstall(String newPkg, String installedPkg) throws CycleException, PackageNotFoundException {

		// Get installation order for both installed and new package
		List<String> orderInstalled = getInstallationOrder(installedPkg);
		List<String> orderNew = getInstallationOrder(newPkg);

		// Remove any already installed dependencies from the order needed for the new
		// package
		for (String x : orderInstalled) {
			if (orderNew.contains(x)) {
				orderNew.remove(x);
			}
		}

		// returns the new order
		return orderNew;
	}

	/**
	 * Return a valid global installation order of all the packages in the
	 * dependency graph.
	 * 
	 * assumes: no package has been installed and you are required to install all
	 * the packages
	 * 
	 * returns a valid installation order that will not violate any dependencies
	 * 
	 * @return List<String>, order in which all the packages have to be installed
	 * @throws CycleException if you encounter a cycle in the graph
	 */
	public List<String> getInstallationOrderForAllPackages() throws CycleException, PackageNotFoundException {
		// creates a list of all the vertices in the graph
		List<String> allVertices = new ArrayList<String>(graph.getAllVertices());
		// creates a hashSet of all the vertices that have incoming dependencies
		Set<String> noIncoming = new HashSet<String>(allVertices);

		// loops through all of the vertices and checks if the vertex has any incoming
		// dependencies by comparing the neighbors of the current vertex being iterated
		// to vertices in the noIncoming hashset. It is removed from the hashSet if it
		// is in the neighbors list.
		for (String x : allVertices) {
			// list of neighbors
			List<String> neighbors = graph.getAdjacentVerticesOf(x);
			// checks if each string in neighbors is in the noIncoming hashSet and removes
			// it if so.
			for (String neighbor : neighbors) {
				if (noIncoming.contains(neighbor))
					noIncoming.remove(neighbor);
			}
		}

		// creates a list to be returned for the complete installation order
		List<String> installOrder = new ArrayList<String>();

		// loops through each vertex in the noIncoming hashSet and adds each of the
		// current vertices installation orders to the installation order array.
		for (String x : noIncoming) {
			// adds each of the interated strings into the install order List
			installOrder.addAll(getInstallationOrder(x));
		}

		// after all of the installation orders are added, a private helper is called to
		// remove duplicates
		return removeDup(installOrder);
	}

	/**
	 * Find and return the name of the package with the maximum number of
	 * dependencies.
	 * 
	 * Tip: it's not just the number of dependencies given in the json file. The
	 * number of dependencies includes the dependencies of its dependencies. But, if
	 * a package is listed in multiple places, it is only counted once.
	 * 
	 * Example: if A depends on B and C, and B depends on C, and C depends on D.
	 * Then, A has 3 dependencies - B,C and D.
	 * 
	 * @return String, name of the package with most dependencies.
	 * @throws CycleException           if you encounter a cycle in the graph
	 * @throws PackageNotFoundException
	 */
	public String getPackageWithMaxDependencies() throws CycleException, PackageNotFoundException {
		// creates an int to obtain the max installation order from each vertex.
		int maxOrder = 0;
		// the String to represent the package with the most dependencies
		String maxPkg = "";
		// creates a hashset of all the vertices
		Set<String> vertices = new HashSet<String>(graph.getAllVertices());

		// iterates through all the vertices in the graph and checks the size of each of
		// the vertices installation orders. if it's size is larger than the int
		// maxOrder, maxOrder is set to that size.
		for (String x : vertices) {

			// list to hold the installation order of the given String
			List<String> installationOrder = getInstallationOrder(x);

			// compares the size of installationOrder to maxPkg and sets maxPkg equal to the
			// current iterated vertice
			if (installationOrder.size() > maxOrder) {
				maxOrder = installationOrder.size();
				maxPkg = x;
			}
		}

		// returns the package with the maximum order
		return maxPkg;

	}

	public static void main(String[] args) {
		System.out.println("PackageManager.main()");
	}

	// helper methods
	/**
	 * Helper method to remove any duplicates from an input list of String type
	 * 
	 * @param list - the list to be inputed to remove any duplicates from.
	 * @return List<String> - the list that contains no duplicates from the input
	 *         list.
	 */
	private List<String> removeDup(List<String> list) {
		// a list that is empty with no duplicates
		List<String> noDup = new ArrayList<String>();

		// iterates through the input list and if the current iterated string isnt in
		// the noDup list, it is added to the noDup list
		for (String x : list) {
			if (!noDup.contains(x)) {
				noDup.add(x);
			}
		}

		// returns the list with no duplicates
		return noDup;
	}

}
