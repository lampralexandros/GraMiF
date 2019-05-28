import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

import dataProcess.DotFileProcessTree;
import dataProcess.TreeProcess;
import feature.Feature;
import nodes.Node;

public class Main5 {

	public static void main(String[] args) throws FileNotFoundException, ClassNotFoundException, IOException {
		File folder=new File("domain");
		DotFileProcessTree GameDevDomain=new DotFileProcessTree(folder);
		GameDevDomain.printTheDotFiles();
		GameDevDomain.dotProcess_CreateTrees(false);
		// First the whole world model is created
		TreeProcess tempProcess=new TreeProcess(GameDevDomain.getTreeList());
		tempProcess.extractMethodLabel();
		tempProcess.extractClassLabel();
		
		HashMap<String,Integer> mapClassLabelCluster = Utilities.inputToMemory("hashClassMapLabelCluster1");
		HashMap<String,Integer> mapMethodLabelCluster = Utilities.inputToMemory("hashMethodMapLabelCluster1");
				
		HashMap<String,Integer> wholeLabelCluster=new HashMap<String,Integer>();
		
		
		
		wholeLabelCluster=Utilities.mergeHashMapsIntoOne(mapMethodLabelCluster, mapClassLabelCluster);
		
		ArrayList <Vector<Feature>> clusterFeatures= Utilities.inputToMemory2("MethodClusterFeatures");
		
		
		
		
		
		
		// traversing the tree with a hash Map
		for( Node<String> rootNode : tempProcess.getTreeList()){
			rootNode.nodeDataStringTransformHashMap(wholeLabelCluster);
		}
		
		FileWriter fileWriter=new FileWriter("concat.dot");
		PrintWriter printWriter = new PrintWriter(fileWriter);
		int counter=0;
		for( Node<String> rootNode : tempProcess.getTreeList()){
			printWriter.println("digraph Tree"+counter+" {");
			DotFileProcessTree.createDotFilesLikeTrees(rootNode, 0, printWriter);
			printWriter.println("}");
			counter=counter+1;
		}
		printWriter.close();
		fileWriter.close();
		
		
	}

}
