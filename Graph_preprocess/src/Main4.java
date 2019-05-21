import java.io.File;
import java.util.ArrayList;
import java.util.Vector;
import java.util.concurrent.TimeUnit;

public class Main4 {

	public static void main(String[] args) {
		File folder=new File("domain");
		DotFileProcessTree GameDevDomain=new DotFileProcessTree(folder);
		GameDevDomain.printTheDotFiles();
		GameDevDomain.dotProcess_CreateTrees();
		
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
		GameDevDomain.dotProcess_CreateTrees();
		TreeProcess methodProcess=new TreeProcess(GameDevDomain.getTreeList());
		methodProcess.extractMethodLabel();
		methodProcess.extractClassLabel(null);
		domainLabel.clear();
		methodProcess.getTreeList().forEach(node->node.getDataTraverser(domainLabel));
		ClusteringProcess methodClusterer=new ClusteringProcess(domainLabel);
		methodClusterer.semanticAnalysis();
		methodClusterer.removeZeroFeature();
		methodClusterer.doDefaultClusteringDense(8,20,100);
		methodClusterer.saveIntoFileClusters("methodClusters.txt");
		
		
		// Then the class labels are clustered 
		GameDevDomain.getTreeList().clear();
		GameDevDomain.dotProcess_CreateTrees();
		TreeProcess classProcess=new TreeProcess(GameDevDomain.getTreeList());
		classProcess.extractClassLabel();
		classProcess.extractMethodLabel(null);
		domainLabel.clear();
		classProcess.getTreeList().forEach(node->node.getDataTraverser(domainLabel));
		ClusteringProcess classClusterer=new ClusteringProcess(domainLabel);
		classClusterer.semanticAnalysis();
		classClusterer.removeZeroFeature();
		classClusterer.doDefaultClusteringDense(8,20,100);
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
		
		
		//Sequence mining
		GameDevDomain.getTreeList().clear();
		GameDevDomain.dotProcess_CreateTrees();
		SequenceMining miner=new SequenceMining(methodClusterer.getNumClusters());
		ArrayList<Vector<String>> sequencesList=new ArrayList<Vector<String>>();
		for(Node<String> tempTree :GameDevDomain.getTreeList()){
			tempTree.serializer(sequencesList);
			miner.addSequence(sequencesList,methodClusterer.getLabelClusterMap());
			sequencesList.clear();
		}
		miner.RunPrefixSpan(0.5, false);
	}

}
