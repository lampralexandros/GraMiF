import java.io.File;
import java.util.ArrayList;


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
		System.out.println("Cluster methods");
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
		System.out.println("Cluster class");
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
