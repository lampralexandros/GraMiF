import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;

public class Utilities {
	
	/**
	 * This function saves a hashmap into a file with the given name
	 * @author Alexandros Lampridis
	 * @param String fileName
	 * @param HashMap <String,Integer> hashMapToBeSaved
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static void outputToFile(String fileName, HashMap <String,Integer> hashMapToBeSaved) throws FileNotFoundException, IOException{
		ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fileName));
		oos.writeObject(hashMapToBeSaved);
		oos.close();
	}
	
	/**
	 * This function returns a hashmap from a file with the given name
	 * @author Alexandros Lampridis
	 * @param String fileName
	 * @return HashMap<String,Integer>
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public static HashMap<String,Integer> inputToMemory(String fileName) throws FileNotFoundException, IOException, ClassNotFoundException{
		ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fileName));
		HashMap<String,Integer> labelClusterMap=(HashMap<String,Integer>) ois.readObject();
		ois.close();
		return labelClusterMap;
	}
	
	/**
	 * This function merge two hashMaps into one keeping the cluster number unique (<Label as key, Cluster number as value>) 
	 * incrementing for example:
	 * Clusters 1-9 in hashMap1 Cluster 1-7 in hashMap2
	 * => hashMap1's Clusters 1-9 hashMaps2's Clusters 10-16
	 * Does not support duplicate values. In such case, hashMap2's key value would replace hasMap1's key value
	 * @author Alexandros Lampridis
	 * @param hashMap1
	 * @param hashMap2
	 * @return
	 */
	public static HashMap<String,Integer> mergeHashMapsIntoOne(HashMap<String,Integer> hashMap1, HashMap<String,Integer> hashMap2){
		HashMap<String,Integer> hashMapNew= new HashMap<String,Integer>();
		
		Integer max=0;
		
		for( String temp : hashMap1.keySet()){
			if( hashMap1.get(temp).intValue() > max.intValue() ){
				max=hashMap1.get(temp);
			}
			hashMapNew.put(temp, hashMap1.get(temp));
		}
		
		
		for( String temp : hashMap2.keySet()){
			hashMapNew.put(temp, Integer.sum(hashMap2.get(temp).intValue(), max.intValue()));
		}
		
		return hashMapNew;
	}

	
	
}
