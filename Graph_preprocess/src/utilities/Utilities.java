package utilities;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.util.Vector;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import dataProcess.DotFileProcessTree;
import feature.Feature;
import nodes.Node;
import plots.PlotUtilities;

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
	 * This function saves a hashmap into a file with the given name
	 * @author Alexandros Lampridis
	 * @param String fileName
	 * @param HashMap hashMap to be saved
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static void outputToFile3(String fileName, HashMap <Integer, Double[]> hashMapToBeSaved) throws FileNotFoundException, IOException{
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
	 * This function returns a hashmap with keys Integers to values Vectors of Strings from a file with the given name
	 * @author Alexandros Lampridis
	 * @param String fileName
	 * @return 
	 * @return ArrayList of cluster with labels
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public static HashMap<Integer,Double[]> inputToMemory5(String fileName) throws FileNotFoundException, IOException, ClassNotFoundException{
		ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fileName));
		HashMap<Integer,Double[]> clusterFeatures=(HashMap<Integer,Double[]>) ois.readObject();
		ois.close();
		return clusterFeatures;
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
	
	
	/**
	 * This function upscales the keys of the second HashMap and returns it 
	 * incrementing for example:
	 * Clusters 1-9 in hashMap1 Cluster 1-7 in hashMap2
	 * => hashMap1's Clusters 1-9 hashMaps2's Clusters 10-16
	 * Does not support duplicate values. In such case, hashMap2's key value would replace hasMap1's key value
	 * @author Alexandros Lampridis
	 * @param hashMap1
	 * @param hashMap2
	 * @return
	 */
	public static HashMap<Integer,Double[]> upscaleHashMaps(HashMap<Integer,Double[]> hashMap1, HashMap<Integer,Double[]> hashMap2){
		
		HashMap<Integer,Double[]> hashMapAns =new HashMap<Integer,Double[]>();
		Integer max=0;
		
		for( Integer temp : hashMap1.keySet()){
			 
			max = ( max <= temp ) ? temp : max ;
			
		}
		
		
		for( Integer temp : hashMap2.keySet()){
			
			hashMapAns.put(temp+max, hashMap2.get(temp));
		}
		return hashMapAns;
		
	}
	
	/**
	 *This function inserts "label=" key " and changes a hashmap from integer integer to string string
	 * @author Alexandros Lampridis
	 * @param hashMap1
	 * @return
	 */
	public static HashMap<String,String>  tranformHashMap(HashMap<Integer,Integer> hashMap1){
		
		HashMap<String,String> tempHash=new HashMap<String,String>();
		
		for( Integer key : hashMap1.keySet())
			tempHash.put("label=\""+ key + "\"", "label=\""+ hashMap1.get(key) + "\"");
		return tempHash;
		
	}
	
	
	public static ArrayList<String> exportLabelTreesToBracketForm(ArrayList<Node<String>> listOfTrees){
		ArrayList<String> treeOfBracketForm = new ArrayList<String>();
		for(Node<String> rootNode : listOfTrees){
			treeOfBracketForm.add(exportALabelTreeIntoBracketForm(rootNode));
		}
		return treeOfBracketForm;
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
	
	public static void print2dArray(float[][] array,int rows,int cols){
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
	
	
	public static void printDotFileAsInputToGspan2(String fileName, ArrayList<Node<String>> treeList ,HashMap<String,Integer> wholeLabelCluster ) throws IOException{
		
	for( Node<String> rootNode : treeList){
		rootNode.nodeDataStringTransformHashMap(wholeLabelCluster);
	}
	
	FileWriter fileWriter=new FileWriter(fileName);
	PrintWriter printWriter = new PrintWriter(fileWriter);
	int counter=0;
	for( Node<String> rootNode : treeList){
		printWriter.println("digraph Tree"+counter+" {");

		DotFileProcessTree.createDotFilesLikeTrees2(rootNode, 0, printWriter);
		printWriter.println("}");
		counter=counter+1;
	}
	printWriter.close();
	fileWriter.close();
	}
	
	/**
	 * This function reads a testing log file path and loads in a hashMap
	 * the parameters of the test
	 * @throws FileNotFoundException 
	 */
	public static HashMap<String,String> readTestingLog(String[] args) throws FileNotFoundException{
		if( args.length<2){
			throw new IllegalArgumentException("args length < 2");
		}
		
		HashMap<String,String> inputArgs=new HashMap<String,String>();
		HashMap<String,String> testArgs=new HashMap<String,String>();
		for(int i = 0 ; i < args.length-1 ; i++){
			if(args[i].contains("-")){
				if(i+1 > args.length)
					throw new IllegalArgumentException("argument="+args[i]+" has no value");
				if(args[i+1].contains("-"))
					throw new IllegalArgumentException("argument="+args[i]+" value another argument="+args[i+1]);
				if(inputArgs.containsKey(args[i]))
					throw new IllegalArgumentException("argument="+args[i]+" has duplicate value");
				inputArgs.put(args[i], args[i+1]);
				i=i+1;
			}
		}
		
		if(!inputArgs.containsKey("-path"))
			throw new IllegalArgumentException("-path does not exists");
		Scanner tempScanner=new Scanner(new File((String) inputArgs.get("-path") ));

		
		String[] tempStrArg=new String[2];
		while(tempScanner.hasNextLine()){
			tempStrArg[0]=tempScanner.nextLine();
			if(!tempStrArg[0].contains("%") & (tempStrArg[0].length()!=0)){
				tempStrArg=tempStrArg[0].split(" ");
				testArgs.put(tempStrArg[0], tempStrArg[1]);
				}
		}
		tempScanner.close();
		return testArgs;
	}
	/**
	 * Simple method to create the results folder with date and time.
	 * @author Alexandros Lampridis
	 * @param testCaseArgs
	 * @param resultsPath
	 */
	public static void createTheReasultFolder(HashMap<String,String> testCaseArgs, String resultsPath ) {
		
		SimpleDateFormat format = new SimpleDateFormat("yyyy_MM_dd_HH_mm");
		testCaseArgs.put(resultsPath,  testCaseArgs.get(resultsPath)+format.format(new Date())) ;
		new File(testCaseArgs.get(resultsPath)).mkdirs();
		// add path / to fileString
		testCaseArgs.put(resultsPath, testCaseArgs.get(resultsPath).concat("/"));
    	
		
	}
	/**
	 * Simple method to save the case's test arguments.
	 * @param testCaseArgs
	 * @param resultsPath
	 * @throws IOException
	 */
	public static void printTheTestCaseArgs(HashMap<String,String> testCaseArgs, String resultsPath) throws IOException{
		
		FileWriter fileWriter=new FileWriter(new File(testCaseArgs.get(resultsPath)+"testCaseArgs.txt"));
		PrintWriter printWriter = new PrintWriter(fileWriter);
		
		for(String temp : testCaseArgs.keySet()){
			printWriter.print(temp+" ");
			printWriter.println(testCaseArgs.get(temp));
		}
		
		fileWriter.close();
		printWriter.close();
	}
	
	
	public static void plot2dLineGraph(String fileName, String plotTitle, ArrayList<XYSeries> linesToPlot ) throws IOException{
		
	      final XYSeriesCollection dataset = new XYSeriesCollection( );
	      
	      for( XYSeries tempSeries : linesToPlot )
	    	  dataset.addSeries(tempSeries);

	      JFreeChart xylineChart = ChartFactory.createXYLineChart(
	    	  plotTitle, 
	         "Cluster Number",
	         "Score", 
	         dataset,
	         PlotOrientation.VERTICAL, 
	         true, true, false);
	      
	      int width = 640;   /* Width of the image */
	      int height = 480;  /* Height of the image */ 
	      File XYChart = new File( fileName ); 
	      ChartUtilities.saveChartAsJPEG( XYChart, xylineChart, width, height);
	}
	
	/**
	 * Scatter plot faulty!
	 * @param fileName
	 * @param plotTitle
	 * @param linesToPlot
	 * @throws IOException
	 */
	public static void plotScatterPlot(String fileName, String plotTitle, ArrayList<XYSeries> linesToPlot ) throws IOException{
		
	      final XYSeriesCollection dataset = new XYSeriesCollection( );
	      
	      for( XYSeries tempSeries : linesToPlot )
	    	  dataset.addSeries(tempSeries);

	      JFreeChart xylineChart = ChartFactory.createScatterPlot(
	    	  plotTitle, 
	         "Breadth",
	         "Depth", 
	         dataset,
	         PlotOrientation.VERTICAL, 
	         true, true, false);
	      
	      int width = 640;   /* Width of the image */
	      int height = 480;  /* Height of the image */ 
	      File BarChart = new File( fileName ); 
	      ChartUtilities.saveChartAsJPEG( BarChart, xylineChart, width, height);
	}
	
	public static double createAUC( double[] TPR, double[] FPR , boolean flagPercentage){
		
		double[] values;
		int tempIndex;
		
		// round up to two decimals
		for( int i=0 ; i < TPR.length ; i++ ){
			TPR[i] = Math.round( TPR[i]*100.00 ) / 100.00 ; 
			FPR[i] = Math.round( FPR[i]*100.00 ) / 100.00 ; 
		}
		
		HashMap<Double,Double> tempHas1=new HashMap<Double,Double>();
		HashMap<Double,Double> tempHas2=new HashMap<Double,Double>();
		for( int i = 0 ; i < TPR.length ; i++){
			if( tempHas1.containsKey(TPR[i]) ){
				tempHas1.put( TPR[i]  ,  tempHas1.get( TPR[i] ) +1);
			}else{
				tempHas1.put( TPR[i]  , +1.0 );
			}
			
			if( tempHas2.containsKey(FPR[i]) ){
				tempHas2.put( FPR[i]  ,  tempHas2.get( FPR[i] ) +1);
			}else{
				tempHas2.put( FPR[i]  , +1.0 );
			}
		}
		
		// inserting starting value 0 and final value 1 if they do not exist
		tempHas1.put( 0.0 , tempHas1.containsKey(0.0) ? tempHas1.get(0.0) :	0.0 );
		tempHas1.put( 1.0 , tempHas1.containsKey(1.0) ? tempHas1.get(1.0) :	0.0 ); 
		
		tempHas2.put( 0.0 , tempHas2.containsKey(0.0) ? tempHas2.get(0.0) :	0.0 );
		tempHas2.put( 1.0 , tempHas2.containsKey(1.0) ? tempHas2.get(1.0) :	0.0 ); 
		
		HashSet<Double> tempHs = new HashSet<Double>();
		tempHs.addAll( tempHas1.keySet() );
		tempHs.addAll( tempHas2.keySet() );
		
		values = new double[ tempHs.size() ];
		tempIndex = 0;
		for(Double temp : tempHs.toArray( new Double[1] ) ){
			values[tempIndex] = temp.doubleValue();
			tempIndex++;
		}
		
		Arrays.sort(values);
	
		PlotUtilities.calculateLineIntervalOnHashMaps( tempHas1 , values );
		PlotUtilities.calculateLineIntervalOnHashMaps( tempHas2 , values );
		
		double areaAUC = 0 ;
		double areaTPR = 0 ;
		double areaFPR = 0 ;
		double sumAreaTPR= 0 ;
		for( int i = 0 ; i < values.length -1 ; i++ ){
			areaTPR = PlotUtilities.calculateTrapezoidArea(  values[ i ] ,  tempHas1.get( values[ i ] ) , values[ i+1 ] , tempHas1.get( values[ i+1 ]) );
			areaFPR = PlotUtilities.calculateTrapezoidArea(  values[ i ] ,  tempHas2.get( values[ i ] ) , values[ i+1 ] , tempHas2.get( values[ i+1 ]) );
			sumAreaTPR += areaFPR ;
			areaAUC += ( areaTPR - areaFPR <= 0.0 ) ? 0.0 : areaTPR - areaFPR; 
		}
		
		if( flagPercentage ){
			areaAUC = areaAUC / sumAreaTPR ;
		}
		
		return areaAUC;
		
	}
	
	
	
	
}
