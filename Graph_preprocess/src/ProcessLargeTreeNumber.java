import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import dataProcess.DotFileProcessTree;
import dataProcess.TreeProcess;

public class ProcessLargeTreeNumber {

	
	public static void main(String[] args) throws FileNotFoundException, IOException, ClassNotFoundException {
		
		TreeProcess tempProcess=new TreeProcess();
		
		File folder=new File("domainPacMan1");
		List<File> dotFileNames= Arrays.asList(folder.listFiles());
		for(File file:dotFileNames){
		DotFileProcessTree GameDevDomain=new DotFileProcessTree(file.getAbsolutePath());
		GameDevDomain.printTheDotFiles();
		GameDevDomain.dotProcess_CreateTrees(false);
		tempProcess.addTrees(GameDevDomain.getTreeList());}
		

		
		// First the whole world model is created
		DotFileProcessTree GameDevDomain=new DotFileProcessTree(folder);
		GameDevDomain.printTheDotFiles();
		GameDevDomain.dotProcess_CreateTrees(false);
		TreeProcess generalProcess=new TreeProcess(GameDevDomain.getTreeList());
		generalProcess.extractMethodLabel();
		generalProcess.extractClassLabel();
		ArrayList<String> domainLabel=new ArrayList<String>();
		generalProcess.getTreeList().forEach(node -> node.getDataTraverser(domainLabel));
		ClusteringProcess tempClusterer=new ClusteringProcess(domainLabel);
		tempClusterer.semanticAnalysis();
		tempClusterer.removeZeroFeature();
		System.out.println("The world model has been created");

		System.out.println("Tree Number:"+tempProcess.getTreeList().size());
		
		//creating a random set of trees
		ArrayList<Integer> setRandom = new ArrayList<Integer>();
		for(int i = 0 ; i < tempProcess.getTreeList().size() ; i++){
			setRandom.add(i);
		}
		Collections.shuffle(setRandom);
		
		TreeProcess trainingProcess=new TreeProcess();
		TreeProcess testingProcess=new TreeProcess();
		System.out.println(" 75% ="+0.75*tempProcess.getTreeList().size()+" 25% ="+0.25*tempProcess.getTreeList().size());
		
		for(int i = 0 ; i < 0.75*tempProcess.getTreeList().size(); i++){
			//System.out.print(" "+setRandom.get(i)+" ,");
			trainingProcess.addTrees(tempProcess.getTreeList().get(setRandom.get(i)));
		}
		for(int i = (int) (0.75*tempProcess.getTreeList().size()) ; i < tempProcess.getTreeList().size() ; i++){
			testingProcess.addTrees(tempProcess.getTreeList().get(setRandom.get(i)));
		}
		
		//Saving trees
		Utilities.outputToFile2("testingProcessArrayListOfTrees", testingProcess.getTreeList());
		Utilities.outputToFile2("trainingProcessArrayListOfTrees", testingProcess.getTreeList());
		
		
		
		// method Labels for testing and training are processed
		TreeProcess trainingProcessMethod=new TreeProcess();
		TreeProcess testingProcessMethod=new TreeProcess();
		trainingProcessMethod.addTrees(Utilities.inputToMemory4("trainingProcessArrayListOfTrees"));
		testingProcessMethod.addTrees(Utilities.inputToMemory4("testingProcessArrayListOfTrees"));
		trainingProcessMethod.extractMethodLabel();
		trainingProcessMethod.extractClassLabel(null);
		testingProcessMethod.extractMethodLabel();
		testingProcessMethod.extractClassLabel(null);
		
		// The method training set is clustered
		domainLabel.clear();
		trainingProcessMethod.getTreeList().forEach(node->node.getDataTraverser(domainLabel));
		
		ClusteringProcess trainingMethodClusterer=new ClusteringProcess(domainLabel);
		trainingMethodClusterer.semanticAnalysis();
		trainingMethodClusterer.removeZeroFeature();
		System.out.println("training Cluster methods");
		trainingMethodClusterer.doDefaultClusteringDense(5,15,500);
		trainingMethodClusterer.saveIntoFileClusters("trainingMethodClusters.txt");
		AnalysisTFIDF analizerTrainingMethod=new AnalysisTFIDF(trainingMethodClusterer.getClusterFeatures(),tempClusterer.getWordModel());
		analizerTrainingMethod.perfomTFIDF();
		// The method test set is clustered
		domainLabel.clear();
		testingProcessMethod.getTreeList().forEach(node->node.getDataTraverser(domainLabel));
		
		ClusteringProcess testingMethodClusterer=new ClusteringProcess(domainLabel);
		testingMethodClusterer.semanticAnalysis();
		testingMethodClusterer.removeZeroFeature();
		System.out.println("testing Cluster methods");
		testingMethodClusterer.doDefaultClusteringDense(5,15,500);
		testingMethodClusterer.saveIntoFileClusters("testingMethodClusters.txt");
		
		//perform TFIDF on clusters of Method
		System.out.println("TFIDF Cluster method");
		AnalysisTFIDF analizerTestingMethod=new AnalysisTFIDF(testingMethodClusterer.getClusterFeatures(),tempClusterer.getWordModel());
		analizerTestingMethod.perfomTFIDF();

				
		
		
		Utilities.outputToFile2("hashClusterTrainingMethodToCommonTerm", analizerTrainingMethod.getMapClustersCommonTerms());
		Utilities.outputToFile2("hashClusterTestingMethodToCommonTerm", analizerTestingMethod.getMapClustersCommonTerms());
		Utilities.outputToFile("trainingHashMethodMapLabelCluster", trainingMethodClusterer.getLabelClusterMap());
		Utilities.outputToFile("testingHashMethodMapLabelCluster", testingMethodClusterer.getLabelClusterMap());
		
		
		
		// class Labels for testing and training are processed
		TreeProcess trainingProcessClass=new TreeProcess();
		TreeProcess testingProcessClass=new TreeProcess();
		trainingProcessClass.addTrees(trainingProcess.getTreeList());
		testingProcessClass.addTrees(testingProcess.getTreeList());
		trainingProcessClass.extractClassLabel();
		trainingProcessClass.extractMethodLabel(null);
		testingProcessClass.extractClassLabel();
		testingProcessClass.extractMethodLabel(null);
		
		// The class training set is clustered
		domainLabel.clear();
		trainingProcessClass.getTreeList().forEach(node->node.getDataTraverser(domainLabel));
		
		ClusteringProcess trainingClassClusterer=new ClusteringProcess(domainLabel);
		trainingClassClusterer.semanticAnalysis();
		trainingClassClusterer.removeZeroFeature();
		System.out.println("training Cluster class");
		trainingClassClusterer.doDefaultClusteringDense(5,15,500);
		trainingClassClusterer.saveIntoFileClusters("trainingClassClusters.txt");
		
		// The method test set is clustered
		domainLabel.clear();
		testingProcessClass.getTreeList().forEach(node->node.getDataTraverser(domainLabel));
		
		ClusteringProcess testingClassClusterer=new ClusteringProcess(domainLabel);
		testingClassClusterer.semanticAnalysis();
		testingClassClusterer.removeZeroFeature();
		System.out.println("testing Cluster class");
		testingClassClusterer.doDefaultClusteringDense(5,15,500);
		testingClassClusterer.saveIntoFileClusters("testingClassClusters.txt");		
		
		//perform TFIDF on clusters of classes
		System.out.println("TFIDF Cluster class");
		AnalysisTFIDF analizerTestingClass=new AnalysisTFIDF(testingClassClusterer.getClusterFeatures(),tempClusterer.getWordModel());
		analizerTestingClass.perfomTFIDF();
		AnalysisTFIDF analizerTrainingClass=new AnalysisTFIDF(trainingClassClusterer.getClusterFeatures(),tempClusterer.getWordModel());
		analizerTrainingClass.perfomTFIDF();
		
		
		
		//Saving clusters
		System.out.println("Saving to disk");
		Utilities.outputToFile2("hashTrainingClusterClassToCommonTerm", analizerTrainingClass.getMapClustersCommonTerms());
		Utilities.outputToFile2("hashTestingClusterClassToCommonTerm", analizerTestingClass.getMapClustersCommonTerms());
				  
		Utilities.outputToFile("trainingHashClassMapLabelCluster", trainingClassClusterer.getLabelClusterMap());
		Utilities.outputToFile("testingHashClassMapLabelCluster", testingClassClusterer.getLabelClusterMap());
		
		
		//Saving trees
		Utilities.outputToFile2("testingArrayListOfTrees", testingProcess.getTreeList());
		Utilities.outputToFile2("testingArrayListOfTrees", trainingProcess.getTreeList());
	}

}
