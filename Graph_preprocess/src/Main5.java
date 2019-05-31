import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import java.util.HashMap;
import java.util.Vector;

import dataProcess.DotFileProcessTree;
import dataProcess.TreeProcess;
import nodes.Node;

public class Main5 {

	public static void main(String[] args) throws FileNotFoundException, ClassNotFoundException, IOException {
		File folder=new File("domain");
		DotFileProcessTree GameDevDomain=new DotFileProcessTree(folder);
		GameDevDomain.printTheDotFiles();
		GameDevDomain.dotProcess_CreateTrees(false);
		// First the whole world model is created
//		TreeProcess tempProcess=new TreeProcess(GameDevDomain.getTreeList());
		//TreeProcess tempProcess=new TreeProcess(Utilities.inputToMemory4("ArrayListOfTrees"));
//		tempProcess.extractMethodLabel();
//		tempProcess.extractClassLabel();
		
		


		
		
		HashMap<String,Integer> mapTrainingMethodLabelCluster = Utilities.inputToMemory("trainingHashMethodMapLabelCluster");
		HashMap<String,Integer> mapTestingMethodLabelCluster = Utilities.inputToMemory("testingHashMethodMapLabelCluster");
		HashMap<Integer,Vector<String>> mapClusterTestingMethodToVector = Utilities.inputToMemory3("hashClusterTestingMethodToCommonTerm");
		HashMap<Integer,Vector<String>> mapClusterTrainingMethodToVector = Utilities.inputToMemory3("hashClusterTrainingMethodToCommonTerm");
		
		MatchBetweenTwoClusterSets matcher=new MatchBetweenTwoClusterSets(mapClusterTestingMethodToVector,mapTestingMethodLabelCluster,mapClusterTrainingMethodToVector, mapTrainingMethodLabelCluster);
		matcher.createTheSpaceTermVector();
		matcher.transformMapClustersToLabels();;
		// import a clean tree
		
		HashMap<String,Integer> mapTrainingClassLabelCluster = Utilities.inputToMemory("trainingHashClassMapLabelCluster");
		HashMap<String,Integer> mapTestingClassLabelCluster = Utilities.inputToMemory("testingHashClassMapLabelCluster");		
		HashMap<Integer,Vector<String>> mapClusterTestingClassToVector = Utilities.inputToMemory3("hashTestingClusterClassToCommonTerm");
		HashMap<Integer,Vector<String>> mapClusterTrainingClassToVector = Utilities.inputToMemory3("hashTrainingClusterClassToCommonTerm");
		
		matcher=new MatchBetweenTwoClusterSets(mapClusterTestingClassToVector,mapTestingClassLabelCluster,mapClusterTrainingClassToVector,mapTrainingClassLabelCluster);
		matcher.createTheSpaceTermVector();
		matcher.transformMapClustersToLabels();;
		
		
		HashMap<String,Integer> wholeLabelCluster=new HashMap<String,Integer>();
		wholeLabelCluster=Utilities.mergeHashMapsIntoOne(mapTestingMethodLabelCluster, mapTestingClassLabelCluster);
		
		TreeProcess testingProcess=new TreeProcess(Utilities.inputToMemory4("testingProcessArrayListOfTrees"));
		testingProcess.extractMethodLabel();
		testingProcess.extractClassLabel();
		Utilities.printDotFileAsInputToGspan("testingConcat.dot",testingProcess.getTreeList(),wholeLabelCluster);
		
		wholeLabelCluster.clear();
		wholeLabelCluster=Utilities.mergeHashMapsIntoOne(mapTrainingMethodLabelCluster, mapTrainingClassLabelCluster);
		TreeProcess trainingProcess=new TreeProcess(Utilities.inputToMemory4("testingProcessArrayListOfTrees"));
		trainingProcess.extractMethodLabel();
		trainingProcess.extractClassLabel();
		Utilities.printDotFileAsInputToGspan("trainingConcat.dot",trainingProcess.getTreeList(),wholeLabelCluster);
		
		
		// traversing the tree with a hash Map
//		for( Node<String> rootNode : tempProcess.getTreeList()){
//			rootNode.nodeDataStringTransformHashMap(wholeLabelCluster);
//		}
//		
//		FileWriter fileWriter=new FileWriter("concat.dot");
//		PrintWriter printWriter = new PrintWriter(fileWriter);
//		int counter=0;
//		for( Node<String> rootNode : tempProcess.getTreeList()){
//			printWriter.println("digraph Tree"+counter+" {");
//			DotFileProcessTree.createDotFilesLikeTrees(rootNode, 0, printWriter);
//			printWriter.println("}");
//			counter=counter+1;
//		}
//		printWriter.close();
//		fileWriter.close();
		
		
	}

}
