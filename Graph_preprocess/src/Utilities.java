import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;

public class Utilities {
	public static void outputToFile(String fileName, HashMap <String,Integer> hashMapToBeSaved) throws FileNotFoundException, IOException{
		ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fileName));
		oos.writeObject(hashMapToBeSaved);
		oos.close();
	}
	
	public static HashMap<String,Integer> inputToMemory(String fileName) throws FileNotFoundException, IOException, ClassNotFoundException{
		ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fileName));
		HashMap<String,Integer> labelClusterMap=(HashMap<String,Integer>) ois.readObject();
		ois.close();
		return labelClusterMap;
	}
}
