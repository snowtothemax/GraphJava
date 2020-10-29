import java.io.FileNotFoundException;
import java.io.IOException;

import org.json.simple.parser.ParseException;


public class TestClass {

	public static void main(String[] args) {
		PackageManager mngr = new PackageManager();
		try {
			mngr.constructGraph("valid.json");
			System.out.println(mngr.getInstallationOrder("A"));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CycleException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (PackageNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(mngr.getAllPackages());
		
	}

}
