package Tests;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.HashMap;

public class TestImportHashMapLabelCluster {

	public static void main(String[] args) throws FileNotFoundException, IOException, ClassNotFoundException {

	ObjectInputStream ois = new ObjectInputStream(new FileInputStream("hashClassMapLabelCluster"));
	HashMap<String,Integer> labelClusterMap=(HashMap<String,Integer>) ois.readObject();
	ois.close();
	}
	
}
