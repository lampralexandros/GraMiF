import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import dataProcess.DotFileProcessTree;
import dataProcess.TreeProcess;

public class ProcessLargeTreeNumber {

	
	public static void main(String[] args) {
		
		TreeProcess tempProcess=new TreeProcess();
		
		File folder=new File("domainPacMan1");
		List<File> dotFileNames= Arrays.asList(folder.listFiles());
		for(File file:dotFileNames){
		DotFileProcessTree GameDevDomain=new DotFileProcessTree(file.getAbsolutePath());
		GameDevDomain.printTheDotFiles();
		GameDevDomain.dotProcess_CreateTrees(true);
		tempProcess.addTrees(GameDevDomain.getTreeList());}
		
		// First the whole world model is created
		System.out.println("wrong1");
		tempProcess.extractMethodLabel();
		System.out.println("wrong2");
		tempProcess.extractClassLabel();
		System.out.println("wrong3");
		ArrayList<String> domainLabel=new ArrayList<String>();
		
		tempProcess.getTreeList().forEach(node -> node.getDataTraverser(domainLabel));
		ClusteringProcess tempClusterer=new ClusteringProcess(domainLabel);
		tempClusterer.semanticAnalysis();
		tempClusterer.removeZeroFeature();
		System.out.println("finish");
		   
	}

}
