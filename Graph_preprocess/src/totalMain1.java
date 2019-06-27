import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import at.unisalzburg.dbresearch.apted.parser.BracketStringInputParser;

import at.unisalzburg.dbresearch.apted.costmodel.StringUnitCostModel;
import at.unisalzburg.dbresearch.apted.distance.APTED;
import at.unisalzburg.dbresearch.apted.node.StringNodeData;
import clusteringRefinement.AnalysisTFIDF;
import dataProcess.DotFileProcessTree;
import dataProcess.TreeProcess;
import nodes.Node;
import utilities.Utilities;

public class totalMain1 {



		enum TestingParam{
			DATASETPATH("-datasetPath"),
			RESULTSPATH("-resultPath"),
			KMEANSCE("-kmeansCE"),
			KMEANWEIGTH("-kmeansWeightTFIDF"),
			KMEANSDM("-kmeansDM"),
			KMEANSCLUSTERSIZEMIN("-kmeansClusterSizeMin"),
			KMEANSCLUSTERSIZEMAX("-kmeansClusterSizeMax"),
			KMEANSCLUSTERSIZESTEP("-kmeansClusterSizeStep"),
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
			Utilities.outputToFile2(testCaseArgs.get(TestingParam.RESULTSPATH.getArgCode())+ "trainingProcessArrayListOfTrees", trainingProcess.getTreeList());
			
			

				
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
			
			
//			
//			System.out.println("training Cluster methods");
//			
//			trainingMethodClusterer.doClusteringKeepingResultsWithInputArgs(testCaseArgs);
//			trainingMethodClusterer.saveIntoFileClusters(testCaseArgs.get(TestingParam.RESULTSPATH.getArgCode())+ "trainingMethodClusters.txt");
//			
//			
//			
//			AnalysisTFIDF analizerTrainingMethod=new AnalysisTFIDF(trainingMethodClusterer.getClusterFeatures(),tempClusterer.getWordModel());
//			analizerTrainingMethod.perfomTFIDF();
//			
//			
//			
			
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
			
//			System.out.println("testing Cluster methods");
//			testingMethodClusterer.doClusteringKeepingResultsWithInputArgs(testCaseArgs);
//
//			testingMethodClusterer.saveIntoFileClusters(testCaseArgs.get(TestingParam.RESULTSPATH.getArgCode())+ "testingMethodClusters.txt");
//			
//		
//			
//			//perform TFIDF on clusters of Method
//			System.out.println("TFIDF Cluster method");
//			AnalysisTFIDF analizerTestingMethod=new AnalysisTFIDF(testingMethodClusterer.getClusterFeatures(),tempClusterer.getWordModel());
//			analizerTestingMethod.perfomTFIDF();

					
			
			
//			Utilities.outputToFile2(testCaseArgs.get(TestingParam.RESULTSPATH.getArgCode())+ "hashClusterTrainingMethodToCommonTerm", analizerTrainingMethod.getMapClustersCommonTerms());
//			Utilities.outputToFile2(testCaseArgs.get(TestingParam.RESULTSPATH.getArgCode())+ "hashClusterTestingMethodToCommonTerm", analizerTestingMethod.getMapClustersCommonTerms());
//			
//			Utilities.outputToFile3(testCaseArgs.get(TestingParam.RESULTSPATH.getArgCode())+ "hashTrainingClusterMethodToTFIDFTerm", analizerTrainingMethod.getMapClustersTFIDFTerms());
//			Utilities.outputToFile3(testCaseArgs.get(TestingParam.RESULTSPATH.getArgCode())+ "hashTestingClusterMethodToTFIDFTerm", analizerTestingMethod.getMapClustersTFIDFTerms());
//			
//			Utilities.outputToFile(testCaseArgs.get(TestingParam.RESULTSPATH.getArgCode())+ "trainingHashMethodMapLabelCluster", trainingMethodClusterer.getLabelClusterMap());
//			Utilities.outputToFile(testCaseArgs.get(TestingParam.RESULTSPATH.getArgCode())+ "testingHashMethodMapLabelCluster", testingMethodClusterer.getLabelClusterMap());
//			
			
			
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
			
			
//			System.out.println("training Cluster class");
//			trainingClassClusterer.doClusteringKeepingResultsWithInputArgs(testCaseArgs);
//
//			trainingClassClusterer.saveIntoFileClusters(testCaseArgs.get(TestingParam.RESULTSPATH.getArgCode())+ "trainingClassClusters.txt");
//			
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
			
//			System.out.println("testing Cluster class");
//			testingClassClusterer.doClusteringKeepingResultsWithInputArgs(testCaseArgs);
//			
//			testingClassClusterer.saveIntoFileClusters(testCaseArgs.get(TestingParam.RESULTSPATH.getArgCode())+ "testingClassClusters.txt");		
			
			//perform TFIDF on clusters of classes
//			System.out.println("TFIDF Cluster class");
//			AnalysisTFIDF analizerTestingClass=new AnalysisTFIDF(testingClassClusterer.getClusterFeatures(),tempClusterer.getWordModel());
//			analizerTestingClass.perfomTFIDF();
//			AnalysisTFIDF analizerTrainingClass=new AnalysisTFIDF(trainingClassClusterer.getClusterFeatures(),tempClusterer.getWordModel());
//			analizerTrainingClass.perfomTFIDF();
			

			//Saving clusters
//			System.out.println("Saving to disk");
//			Utilities.outputToFile2(testCaseArgs.get(TestingParam.RESULTSPATH.getArgCode())+ "hashTrainingClusterClassToCommonTerm", analizerTrainingClass.getMapClustersCommonTerms());
//			Utilities.outputToFile2(testCaseArgs.get(TestingParam.RESULTSPATH.getArgCode())+ "hashTestingClusterClassToCommonTerm", analizerTestingClass.getMapClustersCommonTerms());
//			
//			Utilities.outputToFile3(testCaseArgs.get(TestingParam.RESULTSPATH.getArgCode())+ "hashTrainingClusterClassToTFIDFTerm", analizerTrainingClass.getMapClustersTFIDFTerms());
//			Utilities.outputToFile3(testCaseArgs.get(TestingParam.RESULTSPATH.getArgCode())+ "hashTestingClusterClassToTFIDFTerm", analizerTestingClass.getMapClustersTFIDFTerms());
//			
//			Utilities.outputToFile(testCaseArgs.get(TestingParam.RESULTSPATH.getArgCode())+ "trainingHashClassMapLabelCluster", trainingClassClusterer.getLabelClusterMap());
//			Utilities.outputToFile(testCaseArgs.get(TestingParam.RESULTSPATH.getArgCode())+ "testingHashClassMapLabelCluster", testingClassClusterer.getLabelClusterMap());

	   		
			
			HashMap<String,Integer> wholeLabelClusterTesting;
			HashMap<String,Integer> wholeLabelClusterTraining;
			HashMap<Integer, Double[]> h1;
			HashMap<Integer, Double[]> h2;
			HashMap<String,String> transformMap;
			boolean flag=true;
			double areaMaxUnderCurve=0;
			
			testCaseArgs.put("-gspanResultFolder", testCaseArgs.get(TestingParam.RESULTSPATH.getArgCode())+"GSPAN");
			new File(testCaseArgs.get("-gspanResultFolder")).mkdirs();
			testCaseArgs.put("-gspanResultFolder", testCaseArgs.get(TestingParam.RESULTSPATH.getArgCode())+"GSPAN/");
			
			String[] gspanArgs= new String[3];
			
			MatchBetweenTwoClusterSets matcher=new MatchBetweenTwoClusterSets();
		for( int clusterCenters = Integer.valueOf( testCaseArgs.get(TestingParam.KMEANSCLUSTERSIZEMIN.getArgCode()) ) ;
					   clusterCenters <= (int) Integer.valueOf( testCaseArgs.get(TestingParam.KMEANSCLUSTERSIZEMAX.getArgCode()) ) ;
					   clusterCenters += Integer.valueOf( testCaseArgs.get(TestingParam.KMEANSCLUSTERSIZESTEP.getArgCode()) ) )
		{
			
			System.out.println("training Cluster methods");
			
			trainingMethodClusterer.doClusteringWithInputArgsExceptKmeansCenters( testCaseArgs , clusterCenters );
			
			System.out.println("testing Cluster methods");
			testingMethodClusterer.doClusteringWithInputArgsExceptKmeansCenters( testCaseArgs , clusterCenters );
		
			System.out.println("training Cluster class");
			trainingClassClusterer.doClusteringWithInputArgsExceptKmeansCenters( testCaseArgs , clusterCenters );

			System.out.println("testing Cluster class");
			testingClassClusterer.doClusteringWithInputArgsExceptKmeansCenters( testCaseArgs , clusterCenters );
			
			System.out.println("TFIDF Cluster ");
			AnalysisTFIDF analizerTestingMethod=new AnalysisTFIDF(testingMethodClusterer.getClusterFeatures(),tempClusterer.getWordModel());
			analizerTestingMethod.perfomTFIDF();
			AnalysisTFIDF analizerTestingClass=new AnalysisTFIDF(testingClassClusterer.getClusterFeatures(),tempClusterer.getWordModel());
			analizerTestingClass.perfomTFIDF();
			AnalysisTFIDF analizerTrainingMethod=new AnalysisTFIDF(trainingMethodClusterer.getClusterFeatures(),tempClusterer.getWordModel());
			analizerTrainingMethod.perfomTFIDF();
			AnalysisTFIDF analizerTrainingClass=new AnalysisTFIDF(trainingClassClusterer.getClusterFeatures(),tempClusterer.getWordModel());
			analizerTrainingClass.perfomTFIDF();
	
			// Creating files for gspan
			
			wholeLabelClusterTesting=Utilities.mergeHashMapsIntoOne(testingMethodClusterer.getLabelClusterMap(), testingClassClusterer.getLabelClusterMap());
						
			TreeProcess testingProcess2=new TreeProcess(Utilities.inputToMemory4(testCaseArgs.get(TestingParam.RESULTSPATH.getArgCode())+ "testingProcessArrayListOfTrees"));
//			HashMap<String,Integer> emptyMapLabelCluster=new HashMap<String,Integer>();
			testingProcess2.extractMethodLabel();
			testingProcess2.extractClassLabel();
			
			
			Utilities.printDotFileAsInputToGspan2(testCaseArgs.get(TestingParam.RESULTSPATH.getArgCode())+"testingConcat.dot",testingProcess2.getTreeList(),wholeLabelClusterTesting);
			
			String gspanTestingResultsName = "testingResults"+ clusterCenters +".dot";
			
			//wholeLabelCluster.clear();
			
			wholeLabelClusterTraining=Utilities.mergeHashMapsIntoOne(trainingMethodClusterer.getLabelClusterMap(), trainingClassClusterer.getLabelClusterMap());
			
			TreeProcess trainingProcess2=new TreeProcess(Utilities.inputToMemory4(testCaseArgs.get(TestingParam.RESULTSPATH.getArgCode())+"testingProcessArrayListOfTrees"));
			trainingProcess2.extractMethodLabel();
			trainingProcess2.extractClassLabel();
		
			Utilities.printDotFileAsInputToGspan2(testCaseArgs.get(TestingParam.RESULTSPATH.getArgCode())+"trainingConcat.dot",trainingProcess2.getTreeList(),wholeLabelClusterTraining);
			
			String gspanTrainingResultsName = "trainingResults"+ clusterCenters +".dot";
			

			gspanArgs[0] = "--graphFile=C:\\SPB_Data\\git\\GraMiF\\Graph_preprocess\\" + testCaseArgs.get(TestingParam.RESULTSPATH.getArgCode()).replace("/", "") + "\\testingConcat.dot" ;
			gspanArgs[1] = "--outputFile=C:\\SPB_Data\\git\\GraMiF\\Graph_preprocess\\" + testCaseArgs.get(TestingParam.RESULTSPATH.getArgCode()).replace("/", "") + "\\GSPAN\\"+gspanTestingResultsName;
			gspanArgs[2] = "--minimumFrequency=" + testCaseArgs.get( "-gspanSupport" ) + "%" ;
			System.out.println("Running Parsemis on training");
			de.parsemis.Miner.run(gspanArgs);
			
			FileWriter fileWriter = new FileWriter(testCaseArgs.get( TestingParam.GSPANRESULTFOLDER.getArgCode()).concat( gspanTestingResultsName ) , true );
			PrintWriter printWriter = new PrintWriter( fileWriter );
			printWriter.println( "-clusterNum ".concat( testCaseArgs.get( "-kmeansClusterSizeMax" ) ) );
			printWriter.println( "-gspanSupport ".concat( testCaseArgs.get( "-gspanSupport" ) ) );
			System.out.println("Running Parsemis on testing");
			printWriter.close();
			fileWriter.close();
			
			gspanArgs[0] = "--graphFile=C:\\SPB_Data\\git\\GraMiF\\Graph_preprocess\\" + testCaseArgs.get(TestingParam.RESULTSPATH.getArgCode()).replace("/", "") + "\\testingConcat.dot" ;
			gspanArgs[1] = "--outputFile=C:\\SPB_Data\\git\\GraMiF\\Graph_preprocess\\" + testCaseArgs.get(TestingParam.RESULTSPATH.getArgCode()).replace("/", "") + "\\GSPAN\\"+gspanTrainingResultsName;
			gspanArgs[2] = "--minimumFrequency=" + testCaseArgs.get( "-gspanSupport" ) + "%" ;
			de.parsemis.Miner.run(gspanArgs);
			
			FileWriter fileWriter2 = new FileWriter(testCaseArgs.get( TestingParam.GSPANRESULTFOLDER.getArgCode()).concat( gspanTrainingResultsName ) , true );
			PrintWriter printWriter2 = new PrintWriter( fileWriter );
			printWriter2.println( "-clusterNum ".concat( testCaseArgs.get( "-kmeansClusterSizeMax" ) ) );
			printWriter2.println( "-gspanSupport ".concat( testCaseArgs.get( "-gspanSupport" ) ) );
			printWriter2.close();
			fileWriter2.close();
			
			
			// Loading the testing results
			DotFileProcessTree testingResults=new DotFileProcessTree( testCaseArgs.get( TestingParam.GSPANRESULTFOLDER.getArgCode()).concat( gspanTestingResultsName ) );
			testingResults.printTheDotFiles();
			testingResults.dotProcessCreateTreesFromGspan();
			
			
			// Loading the training results
			DotFileProcessTree trainingResults=new DotFileProcessTree( testCaseArgs.get( TestingParam.GSPANRESULTFOLDER.getArgCode()).concat( gspanTrainingResultsName ) );
			trainingResults.printTheDotFiles();
			trainingResults.dotProcessCreateTreesFromGspan();
			
			
			

			matcher.MatchBetweenTwoClusterSets2(analizerTestingMethod.getMapClustersTFIDFTerms(),
												testingMethodClusterer.getLabelClusterMap(),
												analizerTrainingMethod.getMapClustersTFIDFTerms(),
												trainingMethodClusterer.getLabelClusterMap());
			HashMap<Integer,Integer> methodMatcher = matcher.transformMapClustersToLabels2(flag);
			
			
			transformMap = Utilities.tranformHashMap(methodMatcher);
			
			if(flag){
				//changing happens in testing
				for(Node<String> root : testingResults.getTreeList()){
					root.nodeDataStringTransformHashMap1(transformMap);
					
				}
			}else{
				// changing happens in training
				for(Node<String> root : trainingResults.getTreeList()){
					root.nodeDataStringTransformHashMap1(transformMap);
					
				}
			}
			
			// upscaling hashMaps
			h1 = Utilities.upscaleHashMaps(analizerTrainingMethod.getMapClustersTFIDFTerms(),analizerTrainingClass.getMapClustersTFIDFTerms());
			h2 = Utilities.upscaleHashMaps(analizerTestingMethod.getMapClustersTFIDFTerms(),analizerTestingClass.getMapClustersTFIDFTerms());
			
			matcher.MatchBetweenTwoClusterSets2(h2,
					testingClassClusterer.getLabelClusterMap(),
					h1,
					trainingClassClusterer.getLabelClusterMap());
			
			HashMap<Integer,Integer> classMatcher = matcher.transformMapClustersToLabels2(flag);
			transformMap = Utilities.tranformHashMap(classMatcher);
			
			if(flag){
				//changing happens in testing
				for(Node<String> root : testingResults.getTreeList()){
					root.nodeDataStringTransformHashMap1(transformMap);
					
				}
			}else{
				// changing happens in training
				for(Node<String> root : trainingResults.getTreeList()){
					root.nodeDataStringTransformHashMap1(transformMap);

				}
			}
			
			//removing leaves from the trees
			// TODO Get the iterator outside of the for loop
			Iterator<Node<String>> iter1 = testingResults.getTreeList().iterator();
			while(iter1.hasNext()){
				if(iter1.next().isLeaf() )
					iter1.remove();
			}
		
			// TODO Get the iterator outside of the for loop
			Iterator<Node<String>> iter2 = trainingResults.getTreeList().iterator();
			while(iter2.hasNext()){
				if(iter2.next().isLeaf() )
					iter2.remove();
			}
				
			ArrayList<String> treeTestingBracketForm=Utilities.exportLabelTreesToBracketForm(testingResults.getTreeList());
			ArrayList<String> treeTrainingBracketForm=Utilities.exportLabelTreesToBracketForm(trainingResults.getTreeList());
			float resultsDistance[][]= new float[treeTestingBracketForm.size()][treeTrainingBracketForm.size()];
			
			BracketStringInputParser parser = new BracketStringInputParser();
			APTED<StringUnitCostModel, StringNodeData> apted = new APTED<>(new StringUnitCostModel());
			

			//TODO change the sequence of the loop
			for(int i = 0 ; i < treeTestingBracketForm.size() ; i++){
				at.unisalzburg.dbresearch.apted.node.Node<StringNodeData> testingTreeAPTED = parser.fromString(treeTestingBracketForm.get(i));
				
				for(int j =0 ; j < treeTrainingBracketForm.size() ; j++){
					at.unisalzburg.dbresearch.apted.node.Node<StringNodeData> trainingTreeAPTED = parser.fromString(treeTrainingBracketForm.get(j));
					resultsDistance[i][j]=apted.computeEditDistance(testingTreeAPTED, trainingTreeAPTED) / ( ( trainingTreeAPTED.getNodeCount() >= testingTreeAPTED.getNodeCount() ) ? trainingTreeAPTED.getNodeCount() : testingTreeAPTED.getNodeCount() );
				
				}
			}
			
			double[] minDistance = new double[treeTrainingBracketForm.size()];
			double[] TPR = new double[treeTrainingBracketForm.size()];
			double[] FPR = new double[treeTrainingBracketForm.size()];
		
			
			for(int j =0 ; j < treeTrainingBracketForm.size() ; j++){
				at.unisalzburg.dbresearch.apted.node.Node<StringNodeData> trainingTreeAPTED = parser.fromString(treeTrainingBracketForm.get(j));
				minDistance[j]=1;
				for(int i = 0 ; i < treeTestingBracketForm.size() ; i++)
					minDistance[j]= ( resultsDistance[i][j] < minDistance[j] ) ? resultsDistance[i][j] : minDistance[j];
				TPR[j]=1-minDistance[j]; // maximum similarity = 100%
				FPR[j]=minDistance[j]; 
				
			}
			 System.out.println(" Number of kmeans :" + clusterCenters + " AUC : " +  Utilities.createAUC( TPR , FPR , true ) );
			
			
	

			
		}
			
			
			
			
			
			
				
			
		
			

				
			
			
	    	// Printing inside the results folder the test case args
	    	Utilities.printTheTestCaseArgs(testCaseArgs,TestingParam.RESULTSPATH.argCode);
		}

	}

