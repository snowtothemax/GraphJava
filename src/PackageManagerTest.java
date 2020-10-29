

public class PackageManagerTest {
  enum Meal = {
      BREAKFAST, LUNCH, DINNER
  }
  PackageManager packageManager;
  
  /**
   * @throws java.lang.Exception
   */
  @BeforeEach
  void setUp() throws Exception {
    // The setup must initialize this class's instances 
    // and the super class instances.
    // Because of the inheritance between the interfaces and classes,
    // we can do this by calling createInstance() and casting to the desired type
    // and assigning that same object reference to the super-class fields.
    packageManager = new PackageManager();
  }

  /**
   * @throws java.lang.Exception
   */
  @AfterEach
  void tearDown() throws Exception {
    packageManager = null;
  }
  
  /**
   * Test that the package manager gets the correct installation path for a given
   * package in an acyclic graph with sample json
   */
  @Test
  void testPManager_01_get_path_p1_notCyclic() {
    //Construct the graph from sample json
    try {
      packageManager.constructGraph("src/example.json");
    } catch (IOException | ParseException e) {
      fail("Exception caught parsing json or loading file");
    }
    
    try {
      //Create installation order and an ArrayList that should mirror that order
      List<String> insOrder = packageManager.getInstallationOrder("p1");
      List<String> expectedOrder = new ArrayList<String>();
      expectedOrder.add("p5");
      expectedOrder.add("p2");
      expectedOrder.add("p6");
      expectedOrder.add("p7");
      expectedOrder.add("p3");
      expectedOrder.add("p4");
      expectedOrder.add("p1");
      //Compare
      if(insOrder.equals(expectedOrder) == false) {
        System.out.println(insOrder.toString());
        fail("Installation order is not as expected");
      }
      //Unexpected exception
    } catch (CycleException | PackageNotFoundException e) {
      fail("Exception thrown when it shouldn't have been");
      e.printStackTrace();
    }
  }
  
  /**
   * Test that the package manager gets the throws an exception trying to get the installation
   * path for a package that is cyclic on that path with sample json
   */
  @Test
  void testPManager_02_get_path_p1_cyclic() {
    //Construct the graph from sample json
    try {
      packageManager.constructGraph("src/exampleCyclic.json");
    } catch (IOException | ParseException e) {
      fail("Exception caught parsing json or loading file");
    }
    
    try {
      //Get installation order for a package that is cyclic
      List<String> insOrder = packageManager.getInstallationOrder("p1");
      fail("Exception not thrown when it should have been");
      
      //Excpected exception
    } catch (CycleException | PackageNotFoundException e) {
      //Good
    }
  }
  
  /**
   * Test that the package manager can correctly get the installation path for a package
   * when one of its dependencies (and that package's dependencies) have already been
   * installed with sample json
   */
  @Test
  void testPManager_03_get_path_p1_p3_alreadyInstalled() {
    //Construct the graph from sample json
    try {
      packageManager.constructGraph("src/example.json");
    } catch (IOException | ParseException e) {
      fail("Exception caught parsing json or loading file");
    }
    
    try {
      //Create installation order and an ArrayList that should mirror that order
      List<String> insOrder = packageManager.toInstall("p1", "p3");
      List<String> expectedOrder = new ArrayList<String>();
      expectedOrder.add("p5");
      expectedOrder.add("p2");
      expectedOrder.add("p4");
      expectedOrder.add("p1");
       
      //Compare
      if(insOrder.equals(expectedOrder) == false) {
        System.out.println(insOrder.toString());
        fail("Installation order is not as expected");
      }
      //Unexpected exception
    } catch (CycleException | PackageNotFoundException e) {
      fail("Exception thrown when it shouldn't have been");
      e.printStackTrace();
    }
  }
  
  /**
   * Test that the package manager gets the correct package that has the highest amounts
   * of dependencies with sample json
   */
  @Test
  void testPManager_04_get_package_with_max() {
    //Construct the graph from sample json
    try {
      packageManager.constructGraph("src/example.json");
    } catch (IOException | ParseException e) {
      fail("Exception caught parsing json or loading file");
    }
    //Ensure that the package found has the most dependencies
    try {
      if(packageManager.getPackageWithMaxDependencies().equals("p1") == false)
        fail("Package with max dependencies should be p1");
      //Unexpected exception
    } catch (CycleException | PackageNotFoundException e) {
      fail("Exception thrown when it shouldn't have been");
      e.printStackTrace();
    }
  }
  
  /**
   * Test that the package manager gets the correct installation path for all packages
   * in an acyclic graph with sample json
   */
  @Test
  void testPManager_05_get_whole_path_notCyclic() {
    //Construct the graph from sample json
    try {
      packageManager.constructGraph("src/example.json");
    } catch (IOException | ParseException e) {
      fail("Exception caught parsing json or loading file");
    }
    
    try {
    //Create installation order and an ArrayList that should mirror that order
      List<String> insOrder = packageManager.getInstallationOrderForAllPackages();
      List<String> expectedOrder = new ArrayList<String>();
      expectedOrder.add("p5");
      expectedOrder.add("p2");
      expectedOrder.add("p6");
      expectedOrder.add("p7");
      expectedOrder.add("p3");
      expectedOrder.add("p4");
      expectedOrder.add("p1");
      //Compare
      if(insOrder.equals(expectedOrder) == false) {
        System.out.println(insOrder.toString());
        fail("Installation order is not as expected");
      }
      //Unexpected exception
    } catch (CycleException | PackageNotFoundException e) {
      fail("Exception thrown when it shouldn't have been");
      e.printStackTrace();
    }
  }
  
  /**
   * Test that the package manager throws a CycleException trying to get the entire
   * installation path in a cyclic graph
   */
  @Test
  void testPManager_02_get_whole_path_cyclic() {
    //Construct the graph from sample json
    try {
      packageManager.constructGraph("src/exampleCyclic.json");
    } catch (IOException | ParseException e) {
      fail("Exception caught parsing json or loading file");
    }
    
    try {
      //Get installation order for a graph that is cyclic
      List<String> insOrder = packageManager.getInstallationOrderForAllPackages();
      //Exception not thrown
      fail("Exception not thrown when it should have been");
      
      //Expected excetion
    } catch (CycleException | PackageNotFoundException e) {
      //Good
    }
  }
  
  


}
