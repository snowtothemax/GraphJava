import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * Filename:   PackageManager.java
 * Project:    p4
 * Authors:    Aidan Kaiser
 * 
 * PackageManager is used to process json package dependency files
 * and provide function that make that information available to other users.
 * 
 * Each package that depends upon other packages has its own
 * entry in the json file.  
 * 
 * Package dependencies are important when building software, 
 * as you must install packages in an order such that each package 
 * is installed after all of the packages that it depends on 
 * have been installed.
 * 
 * For example: package A depends upon package B,
 * then package B must be installed before package A.
 * 
 * This program will read package information and 
 * provide information about the packages that must be 
 * installed before any given package can be installed.
 * all of the packages in
 * 
 * You may add a main method, but we will test all methods with
 * our own Test classes.
 */

public class PM {
    
    private Graph graph;
    
    /**
     * Package Manager default no-argument constructor.
     */
    public PM() {
        graph = new Graph();
    }
    
    /**
     * Takes in a file path for a json file and builds the
     * package dependency graph from it. 
     * 
     * @param jsonFilepath the name of json data file with package dependency information
     * @throws FileNotFoundException if file path is incorrect
     * @throws IOException if the give file cannot be read
     * @throws ParseException if the given json cannot be parsed 
     */
    public void constructGraph(String jsonFilepath) throws FileNotFoundException, 
      IOException, ParseException {
      //Parse json object from file
      Object obj = new JSONParser().parse(new FileReader(jsonFilepath)); 
      JSONObject jo = (JSONObject) obj;
      //Get the json array packages from the object, which contains the packages
      JSONArray ja = (JSONArray) jo.get("packages");
      //Loop through all packages
      for(int i=0; i<ja.size(); i++) {
        //Get each individual object in the package
        JSONObject currentJO = (JSONObject) ja.get(i);
        //Loop through the dependencies json array and store all dependences for each package
        JSONArray currentJA = (JSONArray) currentJO.get("dependencies");
        ArrayList<String> dependencies = new ArrayList<String>();
        for(int idx2=0; idx2<currentJA.size(); idx2++) {
          dependencies.add((String) currentJA.get(idx2));
        }
        //Get the name of the package that has the dependencies and add it as a vertex
        String currentPackage = (String) currentJO.get("name");
        graph.addVertex(currentPackage);
        //Link package to all of its verticies
        for(int idx2=0; idx2<dependencies.size(); idx2++) {
          graph.addEdge(currentPackage, dependencies.get(idx2));
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
     * Given a package name, returns a list of packages in a
     * valid installation order.  
     * 
     * Valid installation order means that each package is listed 
     * before any packages that depend upon that package.
     * 
     * @return List<String>, order in which the packages have to be installed
     * 
     * @throws CycleException if you encounter a cycle in the graph while finding
     * the installation order for a particular package. Tip: Cycles in some other
     * part of the graph that do not affect the installation order for the 
     * specified package, should not throw this exception.
     * 
     * @throws PackageNotFoundException if the package passed does not exist in the 
     * dependency graph.
     */
    public List<String> getInstallationOrder(String pkg) throws CycleException, 
      PackageNotFoundException {
      //Keep track of which verticies have been visited so they are not added twice
      List<String> visitedVerticies = new ArrayList<String>();
      //Stack for DFS
      Stack<String> stack = new Stack<String>();
      stack.push(pkg);
      List<String> allVerticies = new ArrayList<String>(graph.getAllVertices());
      boolean valid = false;
      //Check if pkg exists in all verticies. If it doesn't throw exception
      for(String v: allVerticies) {
        if(v.equals(pkg)) {
          valid = true;
        }
      }
      if(valid == false)
        throw new PackageNotFoundException();
      //While there are more dependencies in the stack to deal with
      while (stack.isEmpty() == false) {
          //Take the top of the stack, which is the next dependency
          String vertex = stack.pop();
          //If the vertex wasn't tracked, add it to visited and take all of its dependencies
          if (visitedVerticies.contains(vertex) == false) {
              visitedVerticies.add(vertex);
              for (String v : graph.getAdjacentVerticesOf(vertex)) {
                  for(int i=0; i<graph.getAdjacentVerticesOf(v).size(); i++) {
                    //If the graph is cycling back and forth between dependencies, throw
                    //CycleException
                    if(vertex.equals(graph.getAdjacentVerticesOf(v).get(i))) {
                      throw new CycleException();
                    }
                  }
                  //Push the current vertex's dependencies to the stack
                  stack.push(v);
              }
          }
      }
      //Return the reversal of the arrayList because DFS will return reverse of installationOrder
      return reverseArrayList(visitedVerticies);
    }
    
    /**
     * Private helper method to get all packages in the graph.
     * 
     * @param listToReverse is the ArrayList that we need to reverse
     * @return ArrayList<String> of the parameter in reverse order
     */
    private ArrayList<String> reverseArrayList(List<String> listToReverse) 
    { 
        //Initialize new list, loop through listToReverse backwards and add in that order
        ArrayList<String> reversedList = new ArrayList<String>(); 
        for (int i = listToReverse.size() - 1; i >= 0; i--) { 
          reversedList.add(listToReverse.get(i)); 
        } 
        return reversedList; 
    } 
    

    
    /**
     * Given two packages - one to be installed and the other installed, 
     * return a List of the packages that need to be newly installed. 
     * 
     * For example, refer to shared_dependecies.json - toInstall("A","B") 
     * If package A needs to be installed and packageB is already installed, 
     * return the list ["A", "C"] since D will have been installed when 
     * B was previously installed.
     * 
     * @return List<String>, packages that need to be newly installed.
     * 
     * @throws CycleException if you encounter a cycle in the graph while finding
     * the dependencies of the given packages. If there is a cycle in some other
     * part of the graph that doesn't affect the parsing of these dependencies, 
     * cycle exception should not be thrown.
     * 
     * @throws PackageNotFoundException if any of the packages passed 
     * do not exist in the dependency graph.
     */
    public List<String> toInstall(String newPkg, String installedPkg) throws CycleException, PackageNotFoundException {
        //Get installation order for both installed and new package
        List<String> orderForInstalled = getInstallationOrder(installedPkg);
        List<String> orderNeededForNew = getInstallationOrder(newPkg);
        
        //Remove any already installed dependencies from the order needed for the new package
        for(String v: orderForInstalled) {
          if(orderNeededForNew.contains(v)) {
            orderNeededForNew.remove(v);
          }
        }
        
        return orderNeededForNew;
    }
    
    /**
     * Return a valid global installation order of all the packages in the 
     * dependency graph.
     * 
     * assumes: no package has been installed and you are required to install 
     * all the packages
     * 
     * returns a valid installation order that will not violate any dependencies
     * 
     * @return List<String>, order in which all the packages have to be installed
     * @throws CycleException if you encounter a cycle in the graph
     * @throws PackageNotFoundException 
     */
    public List<String> getInstallationOrderForAllPackages() throws CycleException, PackageNotFoundException {
        ArrayList<String> allVerticies = new ArrayList<String>(graph.getAllVertices());
        Set<String> noIncoming = new HashSet<String>(allVerticies);
        //Loop through every vertex in the graph and remove all that have any incoming edges
        //In order to find the roots of the graph
        for(String v: allVerticies) {
          List<String> adjacent = graph.getAdjacentVerticesOf(v);
          for(String adj: adjacent) {
            if(noIncoming.contains(adj))
              noIncoming.remove(adj);
          }
        }
        ArrayList<String> fullPath = new ArrayList<String>();
        //Add the complete installation order of all of the roots to the full installation path
        for(String v: noIncoming) {
          fullPath.addAll(getInstallationOrder(v));
        }
        
        //If there are too many edges in the directed graph relative to the order
        //Throw CycleException because the graph cycles
        if(graph.size() > graph.order() - 1)
          throw new CycleException();
        
        //Remove any duplicates in the installation path so dependencies won't be installed twice
        return removeDuplicates(fullPath);
    }
    

    /**
     * Private helper method to remove all duplicates in a single ArrayList
     * 
     * @param list is the ArrayList that we need to remove duplicates from
     * @return ArrayList<String> of the parameter with no duplicates
     */
    private <String> ArrayList<String> removeDuplicates(ArrayList<String> list) 
    { 
        ArrayList<String> listNoDuplicates = new ArrayList<String>(); 
        //Loop through the parameter list and every time a new element comes up
        //Add it to the list with no duplicates
        for(String v : list) { 
            if(listNoDuplicates.contains(v) == false) { 
              listNoDuplicates.add(v); 
            } 
        } 
        return listNoDuplicates; 
    } 
    
    /**
     * Find and return the name of the package with the maximum number of dependencies.
     * 
     * Tip: it's not just the number of dependencies given in the json file.  
     * The number of dependencies includes the dependencies of its dependencies.  
     * But, if a package is listed in multiple places, it is only counted once.
     * 
     * Example: if A depends on B and C, and B depends on C, and C depends on D.  
     * Then,  A has 3 dependencies - B,C and D.
     * 
     * @return String, name of the package with most dependencies.
     * @throws CycleException if you encounter a cycle in the graph
     * @throws PackageNotFoundException 
     */
    public String getPackageWithMaxDependencies() throws CycleException, PackageNotFoundException {
      ArrayList<String> allVerticies = new ArrayList<String>(graph.getAllVertices());
      Set<String> noIncoming = new HashSet<String>(allVerticies);
      //Loop through every vertex in the graph and remove all that have any incoming edges
      //In order to find the roots of the graph (which will have the most dependencies)
      for(String v: allVerticies) {
        List<String> adjacent = graph.getAdjacentVerticesOf(v);
        for(String adj: adjacent) {
          if(noIncoming.contains(adj))
            noIncoming.remove(adj);
        }
      }
      //Variables to track the the name of the package with most dependencies and its
      //Dependency number
      String maxPackage = "";
      int currentMax = -1;
      
      //Using the roots, see how big the installation orders are and get the package with the
      //biggest
      for(String v: noIncoming) {
        if(getInstallationOrder(v).size() >= currentMax) {
          currentMax = getInstallationOrder(v).size();
          maxPackage = v;
        }
      }
      return maxPackage;
    }
    
}
