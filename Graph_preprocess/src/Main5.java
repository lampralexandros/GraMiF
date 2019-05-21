import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;

import bin.TreeProcess;

public class Main5 {

	public static void main(String[] args) throws FileNotFoundException, ClassNotFoundException, IOException {
		File folder=new File("domain");
		DotFileProcessTree GameDevDomain=new DotFileProcessTree(folder);
		GameDevDomain.printTheDotFiles();
		GameDevDomain.dotProcess_CreateTrees();
		// First the whole world model is created
		TreeProcess tempProcess=new TreeProcess(GameDevDomain.getTreeList());
		tempProcess.extractMethodLabel();
		tempProcess.extractClassLabel();
		
		HashMap<String,Integer> mapClassLabelCluster = Utilities.inputToMemory("hashClassMapLabelCluster");
		HashMap<String,Integer> mapMethodLabelCluster = Utilities.inputToMemory("hashMethodMapLabelCluster");
				
		Integer max=0;
		for(Integer i : mapClassLabelCluster.values()){
			if( i.intValue() > max.intValue() ){
				max=i;
			}
		}
		
		for( String temp : mapMethodLabelCluster.keySet()){
			mapMethodLabelCluster.put(temp, Integer.sum(mapMethodLabelCluster.get(temp).intValue(), max.intValue()));
		}
		

		
	}

}
