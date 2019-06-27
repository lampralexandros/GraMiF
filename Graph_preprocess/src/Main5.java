import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Vector;

import dataProcess.TreeProcess;
import utilities.Utilities;

public class Main5 {

	public static void main(String[] args) throws FileNotFoundException, ClassNotFoundException, IOException {


		
		
		HashMap<String,Integer> mapTrainingMethodLabelCluster = Utilities.inputToMemory("trainingHashMethodMapLabelCluster");
		HashMap<String,Integer> mapTestingMethodLabelCluster = Utilities.inputToMemory("testingHashMethodMapLabelCluster");
		HashMap<Integer,Vector<String>> mapClusterTestingMethodToVector = Utilities.inputToMemory3("hashClusterTestingMethodToCommonTerm");
		HashMap<Integer,Vector<String>> mapClusterTrainingMethodToVector = Utilities.inputToMemory3("hashClusterTrainingMethodToCommonTerm");
		
		for(int i : new int[]{2,4}){
			Vector<String> TempVec=mapClusterTestingMethodToVector.get(i);
			System.out.println("");
			System.out.print("Key="+i+" {");
			for(String tempS : TempVec){
				System.out.print(tempS+" ");
			}
			System.out.print(" }");
			System.out.println("");
		}
		
		
		MatchBetweenTwoClusterSets matcher=new MatchBetweenTwoClusterSets(mapClusterTestingMethodToVector,mapTestingMethodLabelCluster,mapClusterTrainingMethodToVector, mapTrainingMethodLabelCluster);
		matcher.createTheSpaceTermVector();
		matcher.transformMapClustersToLabels();

		
		HashMap<String,Integer> mapTrainingClassLabelCluster = Utilities.inputToMemory("trainingHashClassMapLabelCluster");
		HashMap<String,Integer> mapTestingClassLabelCluster = Utilities.inputToMemory("testingHashClassMapLabelCluster");		
		HashMap<Integer,Vector<String>> mapClusterTestingClassToVector = Utilities.inputToMemory3("hashTestingClusterClassToCommonTerm");
		HashMap<Integer,Vector<String>> mapClusterTrainingClassToVector = Utilities.inputToMemory3("hashTrainingClusterClassToCommonTerm");
		
		matcher=new MatchBetweenTwoClusterSets(mapClusterTestingClassToVector,mapTestingClassLabelCluster,mapClusterTrainingClassToVector,mapTrainingClassLabelCluster);
		matcher.createTheSpaceTermVector();
		matcher.transformMapClustersToLabels();
		
		
		HashMap<String,Integer> wholeLabelCluster=new HashMap<String,Integer>();
		wholeLabelCluster=Utilities.mergeHashMapsIntoOne(mapTestingMethodLabelCluster, mapTestingClassLabelCluster);
		
		TreeProcess testingProcess=new TreeProcess(Utilities.inputToMemory4("testingProcessArrayListOfTrees"));
		HashMap<String,Integer> emptyMapLabelCluster=new HashMap<String,Integer>();
		
		testingProcess.extractMethodLabel();
		testingProcess.extractClassLabel();
		
		Utilities.printDotFileAsInputToGspan("testingCleanConcatWithLabels.dot",testingProcess.getTreeList(),emptyMapLabelCluster); 
		
		Utilities.printDotFileAsInputToGspan2("testingConcat.dot",testingProcess.getTreeList(),wholeLabelCluster);
		
		
		
		wholeLabelCluster.clear();
		wholeLabelCluster=Utilities.mergeHashMapsIntoOne(mapTrainingMethodLabelCluster, mapTrainingClassLabelCluster);
		TreeProcess trainingProcess=new TreeProcess(Utilities.inputToMemory4("testingProcessArrayListOfTrees"));
		trainingProcess.extractMethodLabel();
		trainingProcess.extractClassLabel();
	
		Utilities.printDotFileAsInputToGspan2("trainingConcat.dot",trainingProcess.getTreeList(),wholeLabelCluster);
		
		
	}

}
