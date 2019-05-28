import java.io.File;
import java.util.ArrayList;
import java.util.Vector;

import dataProcess.DotFileProcessTree;
import dataProcess.TreeProcess;
import nodes.Node;


public class Main2 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		File folder=new File("domain");
		DotFileProcessTree GameDevDomain=new DotFileProcessTree(folder);
		GameDevDomain.printTheDotFiles();
		GameDevDomain.dotProcess_CreateTrees(false);
		
		ArrayList<Node<String>> treeList=GameDevDomain.getTreeList();
		TreeProcess Process1=new TreeProcess(treeList);
		
		Process1.extractMethodLabel();
		Process1.extractClassLabel();
		
		ArrayList<String> domainLabel=new ArrayList<String>();
		Process1.getTreeList().forEach(node->node.getDataTraverser(domainLabel));
		
		
		ClusteringProcess Clusterer=new ClusteringProcess(domainLabel);
		Clusterer.semanticAnalysis();
		Clusterer.removeZeroFeature();
		//Clusterer.doDefaultClusteringDense();
		Clusterer.doQuickClusteringDense();
		Clusterer.saveIntoFileClusters("cluster.txt");
		
		OutlierProcess outlierProcess1=new OutlierProcess(Clusterer.getClusterFeatures());
		outlierProcess1.defualtIQR();
		//Process1.getTreeList().forEach(tree->tree.serializer(sequencesList));
		
		SequenceMining miner=new SequenceMining(Clusterer.getNumClusters());
		
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
