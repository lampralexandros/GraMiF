import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

import dataProcess.DotFileProcessTree;
import feature.Feature;
import nodes.Node;

public class Utilities {
	
	/**
	 * This function saves a hashmap into a file with the given name
	 * @author Alexandros Lampridis
	 * @param String fileName
	 * @param HashMap hashMap to be saved
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static void outputToFile(String fileName, HashMap <String,Integer> hashMapToBeSaved) throws FileNotFoundException, IOException{
		ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fileName));
		oos.writeObject(hashMapToBeSaved);
		oos.close();
	}
	
	/**
	 * This function saves a hashmap into a file with the given name
	 * @author Alexandros Lampridis
	 * @param String fileName
	 * @param HashMap hashMap to be saved
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static void outputToFile2(String fileName, HashMap <Integer,Vector<String>> hashMapToBeSaved) throws FileNotFoundException, IOException{
		ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fileName));
		oos.writeObject(hashMapToBeSaved);
		oos.close();
	}
	
	/**
	 * This function saves a ArrayCluster  into a file with the given name
	 * @author Alexandros Lampridis
	 * @param String fileName
	 * @param ArrayList clusterFeatures to be saved
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static void outputToFile(String fileName, ArrayList <Vector<Feature>> clusterFeaturesToBeSaved) throws FileNotFoundException, IOException{
		ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fileName));
		oos.writeObject(clusterFeaturesToBeSaved);
		oos.close();
	}
	
	/**
	 * This function saves a Array list of Trees into a file with the given name
	 * @author Alexandros Lampridis
	 * @param String fileName
	 * @param ArrayList Nodes of Strings to be saved
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static void outputToFile2(String fileName, ArrayList <Node<String>> TreesToBeSaved) throws FileNotFoundException, IOException{
		ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fileName));
		oos.writeObject(TreesToBeSaved);
		oos.close();
	}
	
	/**
	 * This function returns a hashmap from a file with the given name
	 * @author Alexandros Lampridis
	 * @param String fileName
	 * @return HashMap
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
	 * This function returns a hashmap from a file with the given name
	 * @author Alexandros Lampridis
	 * @param String fileName
	 * @return 
	 * @return ArrayList of cluster with labels
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public static ArrayList <Vector<Feature>> inputToMemory2(String fileName) throws FileNotFoundException, IOException, ClassNotFoundException{
		ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fileName));
		ArrayList <Vector<Feature>> clusterFeatures=(ArrayList <Vector<Feature>>) ois.readObject();
		ois.close();
		return clusterFeatures;
	}
	
	/**
	 * This function returns a hashmap with keys Integers to values Vectors of Strings from a file with the given name
	 * @author Alexandros Lampridis
	 * @param String fileName
	 * @return 
	 * @return ArrayList of cluster with labels
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public static HashMap<Integer,Vector<String>> inputToMemory3(String fileName) throws FileNotFoundException, IOException, ClassNotFoundException{
		ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fileName));
		HashMap<Integer,Vector<String>> clusterFeatures=(HashMap<Integer,Vector<String>>) ois.readObject();
		ois.close();
		return clusterFeatures;
	}
	
	/**
	 * This function returns an array list with nodes of Strings, representing trees, from a file with the given name.
	 * @author Alexandros Lampridis
	 * @param String fileName
	 * @return 
	 * @return ArrayList of cluster with labels
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public static ArrayList<Node<String>> inputToMemory4(String fileName) throws FileNotFoundException, IOException, ClassNotFoundException{
		ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fileName));
		ArrayList<Node<String>> treeList=(ArrayList<Node<String>>) ois.readObject();
		ois.close();
		return treeList;
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
	
	
	public static String exportALabelTreeIntoBracketForm(Node<String> rootNode){
		String output="";
		return traverseTreeToBracketForm(output,rootNode);

	}

	private static String traverseTreeToBracketForm(String output, Node<String> rootNode){
		output=output.concat("{");
		output=output.concat(rootNode.getData());
		if(!rootNode.getChildren().isEmpty()){
			
			for(Node<String> node : rootNode.getChildren()){
				output=traverseTreeToBracketForm(output,node);
			}
		}
		return output.concat("}");
		
	}
	
	public static void print2dArray(double[][] array,int rows,int cols){
		for(int i=0 ; i < rows ; i++){
			System.out.print("[ ");
			for(int j=0 ; j < cols ; j++){
				System.out.print(array[i][j]+" ");
			}
			System.out.println("]");
		}
		
	}
	
	public static void printDotFileAsInputToGspan(String fileName, ArrayList<Node<String>> treeList ,HashMap<String,Integer> wholeLabelCluster ) throws IOException{
	
	for( Node<String> rootNode : treeList){
		rootNode.nodeDataStringTransformHashMap(wholeLabelCluster);
	}
	
	FileWriter fileWriter=new FileWriter(fileName);
	PrintWriter printWriter = new PrintWriter(fileWriter);
	int counter=0;
	for( Node<String> rootNode : treeList){
		printWriter.println("digraph Tree"+counter+" {");
		DotFileProcessTree.createDotFilesLikeTrees(rootNode, 0, printWriter);
		printWriter.println("}");
		counter=counter+1;
	}
	printWriter.close();
	fileWriter.close();
	}
	
	
}
