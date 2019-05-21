package Tests;
import java.io.File;
import java.util.ArrayList;

import bin.DotFileProcessTree;
import bin.TreeProcess;

public class TestingTreeprocess {

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
		tempProcess.getTreeList().forEach(node->node.getDataTraverser(domainLabel));
		for(String S : domainLabel){
			System.out.print(S+" ");
			}
	}

}
