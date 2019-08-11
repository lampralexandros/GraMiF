import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.jfree.data.xy.XYSeries;

import clusteringRefinement.AnalysisTFIDF;
import dataProcess.DotFileProcessTree;
import dataProcess.TreeProcess;
import utilities.Utilities;

public class testCaseResultingInGspan {

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
	
	public static void main(String[] args) throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException {
		
		// Importing testing arguments
		HashMap<String,String> testCaseArgs=Utilities.readTestingLog(args);
		// testing of imported arguments based on enumerated types
    	for(TestingParam parameter :TestingParam.values()){
			if(!testCaseArgs.containsKey(parameter.getArgCode())){
				throw new IllegalArgumentException("This testing parameter is not initialized in the testing Log file: "+parameter.getArgCode());
			}else{
				System.out.println(" Parameter "+parameter.getArgCode()+" initialized: "+ testCaseArgs.get(parameter.getArgCode()));
			}
		}
    	
		//Creating a folder with date and time to save the results
    	Utilities.createTheReasultFolder(testCaseArgs, TestingParam.RESULTSPATH.argCode);
    	

    	
    	

    	
    	TreeProcess tempProcess=new TreeProcess();
		
		File folder=new File(testCaseArgs.get(TestingParam.DATASETPATH.getArgCode()));
		List<File> dotFileNames= Arrays.asList(folder.listFiles());
		for(File file:dotFileNames){
		DotFileProcessTree GameDevDomain=new DotFileProcessTree(file.getAbsolutePath());
		GameDevDomain.printTheDotFiles();
		GameDevDomain.dotProcess_CreateTrees(false);
		tempProcess.addTrees(GameDevDomain.getTreeList());}
		

		
		// First the whole world model is created
		DotFileProcessTree GameDevDomain=new DotFileProcessTree(folder);
		GameDevDomain.printTheDotFiles();
		GameDevDomain.dotProcess_CreateTrees(false);
		TreeProcess generalProcess=new TreeProcess(GameDevDomain.getTreeList());
		generalProcess.extractMethodLabel();
		generalProcess.extractClassLabel();
		ArrayList<String> domainLabel=new ArrayList<String>();
		generalProcess.getTreeList().forEach(node -> node.getDataTraverser(domainLabel));
		ClusteringProcess tempClusterer=new ClusteringProcess(domainLabel);
		tempClusterer.semanticAnalysis();
		tempClusterer.removeZeroFeature();
		System.out.println("The world model has been created");

		System.out.println("Tree Number:"+tempProcess.getTreeList().size());
		
		//creating a random set of trees
		ArrayList<Integer> setRandom = new ArrayList<Integer>();
		for(int i = 0 ; i < tempProcess.getTreeList().size() ; i++){
			setRandom.add(i);
		}
		Collections.shuffle(setRandom);
		
		TreeProcess trainingProcess=new TreeProcess();
		TreeProcess testingProcess=new TreeProcess();
		System.out.println(" 75% ="+0.75*tempProcess.getTreeList().size()+" 25% ="+0.25*tempProcess.getTreeList().size());
		
		for(int i = 0 ; i < 0.75*tempProcess.getTreeList().size(); i++){
			//System.out.print(" "+setRandom.get(i)+" ,");
			trainingProcess.addTrees(tempProcess.getTreeList().get(setRandom.get(i)));
		}
		for(int i = (int) (0.75*tempProcess.getTreeList().size()) ; i < tempProcess.getTreeList().size() ; i++){
			testingProcess.addTrees(tempProcess.getTreeList().get(setRandom.get(i)));
		}
		

		//Saving trees
		Utilities.outputToFile2(testCaseArgs.get(TestingParam.RESULTSPATH.getArgCode())+ "testingProcessArrayListOfTrees", testingProcess.getTreeList());
		Utilities.outputToFile2(testCaseArgs.get(TestingParam.RESULTSPATH.getArgCode())+ "trainingProcessArrayListOfTrees", testingProcess.getTreeList());
		
		
		
		// method Labels for testing and training are processed
		TreeProcess trainingProcessMethod=new TreeProcess();
		TreeProcess testingProcessMethod=new TreeProcess();
		trainingProcessMethod.addTrees(Utilities.inputToMemory4(testCaseArgs.get(TestingParam.RESULTSPATH.getArgCode())+ "trainingProcessArrayListOfTrees"));
		testingProcessMethod.addTrees(Utilities.inputToMemory4(testCaseArgs.get(TestingParam.RESULTSPATH.getArgCode())+ "testingProcessArrayListOfTrees"));
		trainingProcessMethod.extractMethodLabel();
		trainingProcessMethod.extractClassLabel(null);
		testingProcessMethod.extractMethodLabel();
		testingProcessMethod.extractClassLabel(null);
		
		
		
		
		
		// The method training set is clustered
		domainLabel.clear();
		trainingProcessMethod.getTreeList().forEach(node->node.getDataTraverser(domainLabel));
		
		
		
		ClusteringProcess trainingMethodClusterer=new ClusteringProcess(domainLabel);
		trainingMethodClusterer.semanticAnalysis();
		trainingMethodClusterer.removeZeroFeature();
		
		
		if( testCaseArgs.get(TestingParam.KMEANWEIGTH.getArgCode()).equals("1") ) {
			System.out.println("Performing TFIDF on training method vectors");
			AnalysisTFIDF simpleAnalyzer1=new AnalysisTFIDF(tempClusterer.getWordModel(),trainingMethodClusterer.getFeatureList());
			simpleAnalyzer1.perfomTFIDFonOneArray();}
		
		
		
		System.out.println("training Cluster methods");
		
		trainingMethodClusterer.doClusteringKeepingResultsWithInputArgs(testCaseArgs);
		trainingMethodClusterer.saveIntoFileClusters(testCaseArgs.get(TestingParam.RESULTSPATH.getArgCode())+ "trainingMethodClusters.txt");
		
		
		
		AnalysisTFIDF analizerTrainingMethod=new AnalysisTFIDF(trainingMethodClusterer.getClusterFeatures(),tempClusterer.getWordModel());
		analizerTrainingMethod.perfomTFIDF();
		
		
		
		
		// The method test set is clustered
		domainLabel.clear();
		testingProcessMethod.getTreeList().forEach(node->node.getDataTraverser(domainLabel));
		
		ClusteringProcess testingMethodClusterer=new ClusteringProcess(domainLabel);
		testingMethodClusterer.semanticAnalysis();
		testingMethodClusterer.removeZeroFeature();
		
		if( testCaseArgs.get(TestingParam.KMEANWEIGTH.getArgCode()).equals("1") ) {
			System.out.println("Performing TFIDF on testing method vectors");
			AnalysisTFIDF simpleAnalyzer2=new AnalysisTFIDF(tempClusterer.getWordModel(),testingMethodClusterer.getFeatureList());
			simpleAnalyzer2.perfomTFIDFonOneArray();}
		
		System.out.println("testing Cluster methods");
		testingMethodClusterer.doClusteringKeepingResultsWithInputArgs(testCaseArgs);

		testingMethodClusterer.saveIntoFileClusters(testCaseArgs.get(TestingParam.RESULTSPATH.getArgCode())+ "testingMethodClusters.txt");
		
	
		
		//perform TFIDF on clusters of Method
		System.out.println("TFIDF Cluster method");
		AnalysisTFIDF analizerTestingMethod=new AnalysisTFIDF(testingMethodClusterer.getClusterFeatures(),tempClusterer.getWordModel());
		analizerTestingMethod.perfomTFIDF();

				
		
		
		Utilities.outputToFile2(testCaseArgs.get(TestingParam.RESULTSPATH.getArgCode())+ "hashClusterTrainingMethodToCommonTerm", analizerTrainingMethod.getMapClustersCommonTerms());
		Utilities.outputToFile2(testCaseArgs.get(TestingParam.RESULTSPATH.getArgCode())+ "hashClusterTestingMethodToCommonTerm", analizerTestingMethod.getMapClustersCommonTerms());
		
		Utilities.outputToFile3(testCaseArgs.get(TestingParam.RESULTSPATH.getArgCode())+ "hashTrainingClusterMethodToTFIDFTerm", analizerTrainingMethod.getMapClustersTFIDFTerms());
		Utilities.outputToFile3(testCaseArgs.get(TestingParam.RESULTSPATH.getArgCode())+ "hashTestingClusterMethodToTFIDFTerm", analizerTestingMethod.getMapClustersTFIDFTerms());
		
		Utilities.outputToFile(testCaseArgs.get(TestingParam.RESULTSPATH.getArgCode())+ "trainingHashMethodMapLabelCluster", trainingMethodClusterer.getLabelClusterMap());
		Utilities.outputToFile(testCaseArgs.get(TestingParam.RESULTSPATH.getArgCode())+ "testingHashMethodMapLabelCluster", testingMethodClusterer.getLabelClusterMap());
		
		
		
		// class Labels for testing and training are processed
		TreeProcess trainingProcessClass=new TreeProcess();
		TreeProcess testingProcessClass=new TreeProcess();
		trainingProcessClass.addTrees(trainingProcess.getTreeList());
		testingProcessClass.addTrees(testingProcess.getTreeList());
		trainingProcessClass.extractClassLabel();
		trainingProcessClass.extractMethodLabel(null);
		testingProcessClass.extractClassLabel();
		testingProcessClass.extractMethodLabel(null);
		
		// The class training set is clustered
		domainLabel.clear();
		trainingProcessClass.getTreeList().forEach(node->node.getDataTraverser(domainLabel));
		
		ClusteringProcess trainingClassClusterer=new ClusteringProcess(domainLabel);
		
		trainingClassClusterer.semanticAnalysis();
		trainingClassClusterer.removeZeroFeature();


		if( testCaseArgs.get(TestingParam.KMEANWEIGTH.getArgCode()).equals("1") ) {
			System.out.println("Performing TFIDF on training class vectors");
			AnalysisTFIDF simpleAnalyzer3=new AnalysisTFIDF(tempClusterer.getWordModel(),trainingClassClusterer.getFeatureList());
			simpleAnalyzer3.perfomTFIDFonOneArray();}
		
		
		System.out.println("training Cluster class");
		trainingClassClusterer.doClusteringKeepingResultsWithInputArgs(testCaseArgs);

		trainingClassClusterer.saveIntoFileClusters(testCaseArgs.get(TestingParam.RESULTSPATH.getArgCode())+ "trainingClassClusters.txt");
		
		// The class test set is clustered
		domainLabel.clear();
		testingProcessClass.getTreeList().forEach(node->node.getDataTraverser(domainLabel));
		
		ClusteringProcess testingClassClusterer=new ClusteringProcess(domainLabel);
		testingClassClusterer.semanticAnalysis();
		testingClassClusterer.removeZeroFeature();
		
		if( testCaseArgs.get(TestingParam.KMEANWEIGTH.getArgCode()).equals("1") ) {
			System.out.println("Performing TFIDF on testing class vectors");
			AnalysisTFIDF simpleAnalyzer3=new AnalysisTFIDF(tempClusterer.getWordModel(),testingClassClusterer.getFeatureList());
			simpleAnalyzer3.perfomTFIDFonOneArray();}
		
		System.out.println("testing Cluster class");
		testingClassClusterer.doClusteringKeepingResultsWithInputArgs(testCaseArgs);
		
		testingClassClusterer.saveIntoFileClusters(testCaseArgs.get(TestingParam.RESULTSPATH.getArgCode())+ "testingClassClusters.txt");		
		
		//perform TFIDF on clusters of classes
		System.out.println("TFIDF Cluster class");
		AnalysisTFIDF analizerTestingClass=new AnalysisTFIDF(testingClassClusterer.getClusterFeatures(),tempClusterer.getWordModel());
		analizerTestingClass.perfomTFIDF();
		AnalysisTFIDF analizerTrainingClass=new AnalysisTFIDF(trainingClassClusterer.getClusterFeatures(),tempClusterer.getWordModel());
		analizerTrainingClass.perfomTFIDF();
		
		ArrayList<XYSeries> tempList=new ArrayList<XYSeries>();
		tempList.add(trainingMethodClusterer.getXYPlotClustEvalDoubleXAxis("training methods"));
		tempList.add(testingMethodClusterer.getXYPlotClustEvalDoubleXAxis("testing methods"));
		tempList.add(trainingClassClusterer.getXYPlotClustEvalDoubleXAxis("training class"));
		tempList.add(testingClassClusterer.getXYPlotClustEvalDoubleXAxis("testing class"));
		Utilities.plot2dLineGraph(testCaseArgs.get(TestingParam.RESULTSPATH.getArgCode())+"clusterEval.jpeg",
				"Cluster evaluation with CE: " + testCaseArgs.get( TestingParam.KMEANSCE.getArgCode() ) + 
				" DM: " + testCaseArgs.get( TestingParam.KMEANSDM.getArgCode() ) 
				,tempList);
		
		//Saving clusters
		System.out.println("Saving to disk");
		Utilities.outputToFile2(testCaseArgs.get(TestingParam.RESULTSPATH.getArgCode())+ "hashTrainingClusterClassToCommonTerm", analizerTrainingClass.getMapClustersCommonTerms());
		Utilities.outputToFile2(testCaseArgs.get(TestingParam.RESULTSPATH.getArgCode())+ "hashTestingClusterClassToCommonTerm", analizerTestingClass.getMapClustersCommonTerms());
		
		Utilities.outputToFile3(testCaseArgs.get(TestingParam.RESULTSPATH.getArgCode())+ "hashTrainingClusterClassToTFIDFTerm", analizerTrainingClass.getMapClustersTFIDFTerms());
		Utilities.outputToFile3(testCaseArgs.get(TestingParam.RESULTSPATH.getArgCode())+ "hashTestingClusterClassToTFIDFTerm", analizerTestingClass.getMapClustersTFIDFTerms());
		
		Utilities.outputToFile(testCaseArgs.get(TestingParam.RESULTSPATH.getArgCode())+ "trainingHashClassMapLabelCluster", trainingClassClusterer.getLabelClusterMap());
		Utilities.outputToFile(testCaseArgs.get(TestingParam.RESULTSPATH.getArgCode())+ "testingHashClassMapLabelCluster", testingClassClusterer.getLabelClusterMap());

   		
		// Creating files for gspan
		
		HashMap<String,Integer> wholeLabelCluster=new HashMap<String,Integer>();
		wholeLabelCluster=Utilities.mergeHashMapsIntoOne(testingMethodClusterer.getLabelClusterMap(), testingClassClusterer.getLabelClusterMap());
		
		TreeProcess testingProcess2=new TreeProcess(Utilities.inputToMemory4(testCaseArgs.get(TestingParam.RESULTSPATH.getArgCode())+ "testingProcessArrayListOfTrees"));
		HashMap<String,Integer> emptyMapLabelCluster=new HashMap<String,Integer>();
	
		testingProcess2.extractMethodLabel();
		testingProcess2.extractClassLabel();
		
		
		Utilities.printDotFileAsInputToGspan2(testCaseArgs.get(TestingParam.RESULTSPATH.getArgCode())+"testingConcat.dot",testingProcess2.getTreeList(),wholeLabelCluster);
		
		String gspanTestingResultsName = "testingResults"+ wholeLabelCluster.size()  +".dot";
		
		wholeLabelCluster.clear();
		wholeLabelCluster=Utilities.mergeHashMapsIntoOne(trainingMethodClusterer.getLabelClusterMap(), trainingClassClusterer.getLabelClusterMap());
		TreeProcess trainingProcess2=new TreeProcess(Utilities.inputToMemory4(testCaseArgs.get(TestingParam.RESULTSPATH.getArgCode())+"testingProcessArrayListOfTrees"));
		trainingProcess2.extractMethodLabel();
		trainingProcess2.extractClassLabel();
	
		Utilities.printDotFileAsInputToGspan2(testCaseArgs.get(TestingParam.RESULTSPATH.getArgCode())+"trainingConcat.dot",trainingProcess2.getTreeList(),wholeLabelCluster);
		
		String gspanTrainingResultsName = "trainingResults"+ wholeLabelCluster.size()  +".dot";
		
		testCaseArgs.put("-gspanResultFolder", testCaseArgs.get(TestingParam.RESULTSPATH.getArgCode())+"GSPAN");
		new File(testCaseArgs.get("-gspanResultFolder")).mkdirs();
		testCaseArgs.put("-gspanResultFolder", testCaseArgs.get(TestingParam.RESULTSPATH.getArgCode())+"GSPAN/");
		
		
	
		
		String[] gspanArgs= new String[3];
		gspanArgs[0] = "--graphFile=C:\\SPB_Data\\git\\GraMiF\\Graph_preprocess\\" + testCaseArgs.get(TestingParam.RESULTSPATH.getArgCode()).replace("/", "") + "\\testingConcat.dot" ;
		gspanArgs[1] = "--outputFile=C:\\SPB_Data\\git\\GraMiF\\Graph_preprocess\\" + testCaseArgs.get(TestingParam.RESULTSPATH.getArgCode()).replace("/", "") + "\\GSPAN\\"+gspanTestingResultsName;
		gspanArgs[2] = "--minimumFrequency=" + testCaseArgs.get( "-gspanSupport" ) + "%" ;
	
		de.parsemis.Miner.run(gspanArgs);
			
		FileWriter fileWriter = new FileWriter(testCaseArgs.get( TestingParam.GSPANRESULTFOLDER.getArgCode()).concat( gspanTestingResultsName ) , true );
		PrintWriter printWriter = new PrintWriter( fileWriter );
		printWriter.println( "-clusterNum ".concat( testCaseArgs.get( "-kmeansClusterSizeMax" ) ) );
		printWriter.println( "-gspanSupport ".concat( testCaseArgs.get( "-gspanSupport" ) ) );
		printWriter.close();
		fileWriter.close();
		
    	// Printing inside the results folder the test case args
    	Utilities.printTheTestCaseArgs(testCaseArgs,TestingParam.RESULTSPATH.argCode);
	}

}
