import java.io.File;
import java.util.ArrayList;
import java.util.Vector;

public class Main3 {


	public static void main(String[] args) {
		File folder=new File("domain");
		DotFileProcessTree GameDevDomain=new DotFileProcessTree(folder);
		GameDevDomain.printTheDotFiles();
		GameDevDomain.dotProcess_CreateTrees();
		
		ArrayList<Node<String>> treeList=GameDevDomain.getTreeList();
		
		//Method name process
		TreeProcess Process1=new TreeProcess(treeList);
		Process1.extractMethodLabel();
		//nullify class labels
		Process1.extractClassLabel(null);
		
		ArrayList<String> domainLabel=new ArrayList<String>();
		Process1.getTreeList().forEach(node->node.getDataTraverser(domainLabel));
		
		
		ClusteringProcess Clusterer=new ClusteringProcess(domainLabel);
		Clusterer.semanticAnalysis();
		Clusterer.removeZeroFeature();
		Clusterer.doDefaultClusteringDense(5,20,200);
		//Clusterer.doQuickClusteringDense();
		Clusterer.saveIntoFileClusters("methodClusters.txt");
		
		//Class name process
		DotFileProcessTree GameDevDomain2=new DotFileProcessTree(folder);
		GameDevDomain2.dotProcess_CreateTrees();
		
		TreeProcess Process2=new TreeProcess(GameDevDomain2.getTreeList());
		Process2.extractClassLabel();
		//nullify method labels
		Process2.extractMethodLabel(null);
		ArrayList<String> classLabel=new ArrayList<String>();
		Process2.getTreeList().forEach(node->node.getDataTraverser(classLabel));
		
		ClusteringProcess Clusterer2=new ClusteringProcess(classLabel);
		Clusterer2.semanticAnalysis();
		Clusterer2.removeZeroFeature();
//		Clusterer2.doQuickClusteringDense();
		Clusterer2.doDefaultClusteringDense(2,20,200);
		Clusterer2.saveIntoFileClusters("classClusters.txt");
		
		
		OutlierProcess outlierProcess1=new OutlierProcess(Clusterer.getClusterFeatures());
		outlierProcess1.defualtIQR2();
		
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