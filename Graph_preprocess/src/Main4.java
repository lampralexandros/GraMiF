import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import dataProcess.DotFileProcessTree;
import dataProcess.TreeProcess;


public class Main4 {

	public static void main(String[] args) throws FileNotFoundException, IOException {
		File folder=new File("domain");
		DotFileProcessTree GameDevDomain=new DotFileProcessTree(folder);
		GameDevDomain.printTheDotFiles();
		GameDevDomain.dotProcess_CreateTrees(false);
		
		// First the whole world model is created
		TreeProcess tempProcess=new TreeProcess(GameDevDomain.getTreeList());
		tempProcess.extractMethodLabel();
		tempProcess.extractClassLabel();
		ArrayList<String> domainLabel=new ArrayList<String>();
		tempProcess.getTreeList().forEach(node -> node.getDataTraverser(domainLabel));
		ClusteringProcess tempClusterer=new ClusteringProcess(domainLabel);
		tempClusterer.semanticAnalysis();
		tempClusterer.removeZeroFeature();
		   
		
		// Then the methods labels are clustered 
		GameDevDomain.getTreeList().clear();
		GameDevDomain.dotProcess_CreateTrees(false);
		TreeProcess methodProcess=new TreeProcess(GameDevDomain.getTreeList());
		methodProcess.extractMethodLabel();
		methodProcess.extractClassLabel(null);
		domainLabel.clear();
		methodProcess.getTreeList().forEach(node->node.getDataTraverser(domainLabel));
		ClusteringProcess methodClusterer=new ClusteringProcess(domainLabel);
		methodClusterer.semanticAnalysis();
		methodClusterer.removeZeroFeature();
		System.out.println("Cluster methods");
		methodClusterer.doDefaultClusteringDense(5,15,500);
		methodClusterer.saveIntoFileClusters("methodClusters.txt");
		
		
		// Then the class labels are clustered 
		GameDevDomain.getTreeList().clear();
		GameDevDomain.dotProcess_CreateTrees(false);
		TreeProcess classProcess=new TreeProcess(GameDevDomain.getTreeList());
		classProcess.extractClassLabel();
		classProcess.extractMethodLabel(null);
		domainLabel.clear();
		classProcess.getTreeList().forEach(node->node.getDataTraverser(domainLabel));
		ClusteringProcess classClusterer=new ClusteringProcess(domainLabel);
		classClusterer.semanticAnalysis();
		classClusterer.removeZeroFeature();
		System.out.println("Cluster class");
		classClusterer.doDefaultClusteringDense(5,15,500);
		classClusterer.saveIntoFileClusters("classClusters.txt");
		
		// Outliers process
		OutlierProcess outlierProcess1=new OutlierProcess(classClusterer.getClusterFeatures());
		outlierProcess1.defualtIQR();
		// print outliers
		outlierProcess1.printOutliers();
		outlierProcess1.saveIntoFileClusters("cleanClassClusters.txt");
		classClusterer.saveIntoFileClusters("classClusters2.txt");
		
		OutlierProcess outlierProcess2=new OutlierProcess(methodClusterer.getClusterFeatures());
		outlierProcess2.defualtIQR();
		// print outliers
		outlierProcess2.printOutliers();
		outlierProcess2.saveIntoFileClusters("cleanMethodClusters.txt");
		methodClusterer.saveIntoFileClusters("methodClusters2.txt");
		
		//peform TFIDF
		AnalysisTFIDF analizerClass=new AnalysisTFIDF(outlierProcess1.getClusterFeature(),tempClusterer.getWordModel());
		analizerClass.perfomTFIDF();
		AnalysisTFIDF analizerMethod=new AnalysisTFIDF(outlierProcess2.getClusterFeature(),tempClusterer.getWordModel());
		analizerMethod.perfomTFIDF();
		
		Utilities.outputToFile2("hashClusterClassToCommonTerm", analizerClass.getMapClustersCommonTerms());
		Utilities.outputToFile2("hashClusterMethodToCommonTerm", analizerMethod.getMapClustersCommonTerms());
	  
		Utilities.outputToFile("hashClassMapLabelCluster1", classClusterer.getLabelClusterMap());
		Utilities.outputToFile("ClassClusterFeatures",methodClusterer.getClusterFeatures());
		Utilities.outputToFile("hashMethodMapLabelCluster1", methodClusterer.getLabelClusterMap());
		Utilities.outputToFile("MethodClusterFeatures",methodClusterer.getClusterFeatures());
//		ObjectOutputStream oos,oos1;
//		try {
//			oos = new ObjectOutputStream(new FileOutputStream("hashClassMapLabelCluster"));
//			try {
//				oos.writeObject(classClusterer.getLabelClusterMap());
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//			oos.close();
//		} catch (FileNotFoundException e1) {
//			e1.printStackTrace();
//		} catch (IOException e1) {
//			e1.printStackTrace();
//		}
//
//		try {
//			oos1 = new ObjectOutputStream(new FileOutputStream("hashMethodMapLabelCluster"));
//			try {
//				oos1.writeObject(methodClusterer.getLabelClusterMap());
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//			oos1.close();
//		} catch (FileNotFoundException e1) {
//			e1.printStackTrace();
//		} catch (IOException e1) {
//			e1.printStackTrace();
//		}

	}

}
