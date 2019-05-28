import java.io.File;
import java.util.ArrayList;
import java.util.Vector;

import dataProcess.DotFileProcessTree;
import dataProcess.TreeProcess;
import nodes.Node;

public class Main3 {


	public static void main(String[] args) {
		File folder=new File("domain");
		DotFileProcessTree GameDevDomain=new DotFileProcessTree(folder);
		GameDevDomain.printTheDotFiles();
		GameDevDomain.dotProcess_CreateTrees(false);
		
		ArrayList<Node<String>> treeList=GameDevDomain.getTreeList();
		
		//Methods names process
		TreeProcess Process1=new TreeProcess(treeList);
		Process1.extractMethodLabel();
		//nullify classes labels
		Process1.extractClassLabel(null);
		
		ArrayList<String> domainLabel=new ArrayList<String>();
		Process1.getTreeList().forEach(node->node.getDataTraverser(domainLabel));
		
		
		ClusteringProcess Clusterer=new ClusteringProcess(domainLabel);
		Clusterer.semanticAnalysis();
		Clusterer.removeZeroFeature();
		System.out.println("Method word vector is:"+Clusterer.getWordModel().length);   
		//Clusterer.doDefaultClusteringDense(5,8,10);
		//Clusterer.doQuickClusteringDense();
		//Clusterer.saveIntoFileClusters("methodClusters.txt");
		
		//Classes names process
		DotFileProcessTree GameDevDomain2=new DotFileProcessTree(folder);
		GameDevDomain2.dotProcess_CreateTrees(false);
		
		TreeProcess Process2=new TreeProcess(GameDevDomain2.getTreeList());
		Process2.extractClassLabel();
		//nullify methods labels
		Process2.extractMethodLabel(null);
		ArrayList<String> classLabel=new ArrayList<String>();
		Process2.getTreeList().forEach(node->node.getDataTraverser(classLabel));
		
		
		ClusteringProcess Clusterer2=new ClusteringProcess(classLabel);
		Clusterer2.semanticAnalysis();
		Clusterer2.removeZeroFeature();
		System.out.println("Class word vector is:"+Clusterer2.getWordModel().length);
		//Clusterer2.doQuickClusteringDense();
		//Clusterer2.doDefaultClusteringDense(3,20,10);
		Clusterer2.saveIntoFileClusters("classClusters.txt");
		
		
		OutlierProcess outlierProcess1=new OutlierProcess(Clusterer.getClusterFeatures());
		outlierProcess1.defualtIQR2();
		
		//Process1.getTreeList().forEach(tree->tree.serializer(sequencesList));
		
		SequenceMining miner=new SequenceMining(Clusterer.getNumClusters());
		
		//AnalysisTFIDF analyzer=new AnalysisTFIDF(Clusterer.getClusterFeatures(),Clusterer.getWordModel());
		//analyzer.perfomTFIDF();
		
		ArrayList<Vector<String>> sequencesList=new ArrayList<Vector<String>>();
		for(Node<String> tempTree :Process1.getTreeList()){
			tempTree.serializer(sequencesList);
			miner.addSequence(sequencesList,Clusterer.getLabelClusterMap());
			sequencesList.clear();
		}
		
		miner.RunPrefixSpan(0.5, false);
		//Testing tree
		
		
//		ArrayList<String> domainLabel=new ArrayList<String>();
//		treeList.forEach(tree->tree.getDataTraverser(domainLabel));

		
		
		System.out.println("End");
		
		//Test to print the sequences
//		ArrayList<Vector<String>> sequencesList=new ArrayList<Vector<String>>();
//		Process1.getTreeList().forEach(tree->tree.serializer(sequencesList));
//		sequencesList.forEach(vec->{System.out.println("end of sequence");vec.forEach(str->System.out.print(str+"  "));});
		
		
		
	}

}
