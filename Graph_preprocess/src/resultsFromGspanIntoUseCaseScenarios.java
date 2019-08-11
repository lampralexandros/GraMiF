import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import utilities.Utilities;

public class resultsFromGspanIntoUseCaseScenarios {

	enum TestingParam{
		DATASETPATH("-datasetPath"),
		RESULTSPATH("-resultPath"),
		KMEANSCE("-kmeansCE"),
		KMEANWEIGTH("-kmeansWeightTFIDF"),
		KMEANSDM("-kmeansDM"),
		GSPANRESULTFOLDER("-gspanResultFolder"),
		GSPANSUPPORT("-gspanSupport");
		// internal state
		private String argCode;
		
	    // constructor
	    private TestingParam(final String code) {
	        this.argCode = code;
	    }
	    // get
	    public String getArgCode() {
	        return argCode;
	    }
	}
	
	public static void main(String[] args) throws ClassNotFoundException, IOException {
		
		// Importing testing arguments
		HashMap<String,String> testCaseArgs=Utilities.readTestingLog(args);
		// testing of imported arguments based on enumerated types
    	for(TestingParam parameter :TestingParam.values()){
			if(!testCaseArgs.containsKey(parameter.getArgCode())){
				throw new IllegalArgumentException("This testing parameter is not initialized in the testing Log file: "+parameter.getArgCode());
			}else{
				System.out.println(" Parameter "+ parameter.getArgCode() +" initialized: "+ testCaseArgs.get(parameter.getArgCode()));
			}
		}	
		
    	//Importing results from Clustering - TFIDF analysis 
    	String prefixPath=testCaseArgs.get(TestingParam.RESULTSPATH.getArgCode());
    	HashMap<String,Integer> mapTrainingMethodLabelCluster = Utilities.inputToMemory( prefixPath + "trainingHashMethodMapLabelCluster");
		HashMap<String,Integer> mapTestingMethodLabelCluster = Utilities.inputToMemory( prefixPath + "testingHashMethodMapLabelCluster");
		HashMap<Integer,Vector<String>> mapClusterTestingMethodToVector = Utilities.inputToMemory3( prefixPath + "hashClusterTestingMethodToCommonTerm");
		HashMap<Integer,Vector<String>> mapClusterTrainingMethodToVector = Utilities.inputToMemory3( prefixPath + "hashClusterTrainingMethodToCommonTerm");
		
		
		
		FileWriter testingVectors = new FileWriter( "testingCommonTerms.txt");
		PrintWriter printwrit= new PrintWriter( testingVectors);
		
		for( Integer i : mapClusterTestingMethodToVector.keySet() ){
			String temps = new String();
			mapClusterTestingMethodToVector.get(i).forEach( tempS -> temps.concat(" " + tempS) );
			printwrit.println("Label= " + i + temps );
		}
		
    	
		MatchBetweenTwoClusterSets matcher=new MatchBetweenTwoClusterSets(mapClusterTestingMethodToVector,mapTestingMethodLabelCluster,mapClusterTrainingMethodToVector, mapTrainingMethodLabelCluster);
		matcher.createTheSpaceTermVector();
		matcher.transformMapClustersToLabels();

		
		HashMap<String,Integer> mapTrainingClassLabelCluster = Utilities.inputToMemory( prefixPath + "trainingHashClassMapLabelCluster");
		HashMap<String,Integer> mapTestingClassLabelCluster = Utilities.inputToMemory( prefixPath + "testingHashClassMapLabelCluster");		
		HashMap<Integer,Vector<String>> mapClusterTestingClassToVector = Utilities.inputToMemory3( prefixPath + "hashTestingClusterClassToCommonTerm");
		HashMap<Integer,Vector<String>> mapClusterTrainingClassToVector = Utilities.inputToMemory3( prefixPath + "hashTrainingClusterClassToCommonTerm");
		
		matcher=new MatchBetweenTwoClusterSets(mapClusterTestingClassToVector,mapTestingClassLabelCluster,mapClusterTrainingClassToVector,mapTrainingClassLabelCluster);
		matcher.createTheSpaceTermVector();
		matcher.transformMapClustersToLabels();
    	
    	
    	
    	
    	File folder=new File(testCaseArgs.get(TestingParam.GSPANRESULTFOLDER.getArgCode()));
		List<File> dotFileNames= Arrays.asList(folder.listFiles());
		
		dotFileNames.forEach( tempf -> System.out.println(tempf.getName()));
		
		
	}

}
