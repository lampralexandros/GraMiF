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

import org.jfree.data.xy.XYSeries;

import at.unisalzburg.dbresearch.apted.parser.BracketStringInputParser;
import be.abeel.util.Pair;
import be.abeel.util.Triplet;
import at.unisalzburg.dbresearch.apted.costmodel.StringUnitCostModel;
import at.unisalzburg.dbresearch.apted.distance.APTED;
import at.unisalzburg.dbresearch.apted.node.StringNodeData;
import clusteringRefinement.AnalysisTFIDF;
import dataProcess.DotFileProcessTree;
import dataProcess.TreeProcess;
import matcherBetweenSets.MatchingProcess;
import metrics.AreaUnderCurver;
import metrics.Precision;
import metrics.Recall;
import metrics.TreeEditDistanceMetrics;
import nodes.Node;
import plots.PlotLineChart;
import utilities.Utilities;

public class TotalMain3 {



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
			GSPANSUPPORT("-gspanSupport"),
			TRAININGSETNUMBERPERCENT("-trainingSetNumberPercent"),
			TESTINGSETNUMBERPERCENT("-testingSetNumberPercent"),
			VALIDATIONSETNUMBERPERCENT("-validationSetNumberPercent");
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
		
		public static void main(String[] args) throws Exception {
			
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
			TreeProcess validationProcess=new TreeProcess();
			
			double testingNumberSet = Double.parseDouble( testCaseArgs.get( TestingParam.TESTINGSETNUMBERPERCENT.argCode ) ) ;
			double trainingNumberSet = Double.parseDouble( testCaseArgs.get( TestingParam.TRAININGSETNUMBERPERCENT.argCode ) ) ;
			double validationNumberSet = Double.parseDouble( testCaseArgs.get( TestingParam.VALIDATIONSETNUMBERPERCENT.argCode ) ) ;
			
			System.out.println( " Training Set number of trees, Percent: " + String.valueOf(trainingNumberSet) + " Size: " + trainingNumberSet * tempProcess.getTreeList().size() +
								" Testing Set number of trees, Percent: " + String.valueOf(testingNumberSet) + " Size: " + testingNumberSet * tempProcess.getTreeList().size() +
								" Validation Set number of trees, Percent: " + String.valueOf(validationNumberSet) + " Size: " + validationNumberSet * tempProcess.getTreeList().size() 
								);
			
			for(int i = 0 ; i < trainingNumberSet * tempProcess.getTreeList().size() ; i++){
				//System.out.print(" "+setRandom.get(i)+" ,");
				trainingProcess.addTrees(tempProcess.getTreeList().get(setRandom.get(i)));
			}
			
			
			for(int i = (int) ( ( trainingNumberSet ) * tempProcess.getTreeList().size() ) ; i < (int) ( ( testingNumberSet + trainingNumberSet ) * tempProcess.getTreeList().size() ) ; i++){
				testingProcess.addTrees(tempProcess.getTreeList().get(setRandom.get(i)));
			}
			
			for(int i = (int) ( ( trainingNumberSet + testingNumberSet ) * tempProcess.getTreeList().size() ) ; i < tempProcess.getTreeList().size() ; i++){
				validationProcess.addTrees(tempProcess.getTreeList().get(setRandom.get(i)));
			}
			
			//Saving trees
			Utilities.outputToFile2(testCaseArgs.get(TestingParam.RESULTSPATH.getArgCode())+ "testingProcessArrayListOfTrees", testingProcess.getTreeList());
			Utilities.outputToFile2(testCaseArgs.get(TestingParam.RESULTSPATH.getArgCode())+ "trainingProcessArrayListOfTrees", trainingProcess.getTreeList());
			Utilities.outputToFile2(testCaseArgs.get(TestingParam.RESULTSPATH.getArgCode())+ "validationProcessArrayListOfTrees", validationProcess.getTreeList());
			
			

				
			// method Labels for testing and training are processed
			TreeProcess trainingProcessMethod=new TreeProcess();
			TreeProcess testingProcessMethod=new TreeProcess();
			TreeProcess validationProcessMethod=new TreeProcess();
			trainingProcessMethod.addTrees(Utilities.inputToMemory4(testCaseArgs.get(TestingParam.RESULTSPATH.getArgCode())+ "trainingProcessArrayListOfTrees"));
			testingProcessMethod.addTrees(Utilities.inputToMemory4(testCaseArgs.get(TestingParam.RESULTSPATH.getArgCode())+ "testingProcessArrayListOfTrees"));
			validationProcessMethod.addTrees(Utilities.inputToMemory4(testCaseArgs.get(TestingParam.RESULTSPATH.getArgCode())+ "validationProcessArrayListOfTrees"));
			trainingProcessMethod.extractMethodLabel();
			trainingProcessMethod.extractClassLabel(null);
			testingProcessMethod.extractMethodLabel();
			testingProcessMethod.extractClassLabel(null);
			validationProcessMethod.extractMethodLabel();
			validationProcessMethod.extractClassLabel(null);
			
			
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
			
			// The method test set is clustered
			domainLabel.clear();
			validationProcessMethod.getTreeList().forEach(node->node.getDataTraverser(domainLabel));
			
			ClusteringProcess validationMethodClusterer=new ClusteringProcess(domainLabel);
			validationMethodClusterer.semanticAnalysis();
			validationMethodClusterer.removeZeroFeature();
			
			if( testCaseArgs.get(TestingParam.KMEANWEIGTH.getArgCode()).equals("1") ) {
				System.out.println("Performing TFIDF on validation method vectors");
				AnalysisTFIDF simpleAnalyzer3=new AnalysisTFIDF(tempClusterer.getWordModel(),validationMethodClusterer.getFeatureList());
				simpleAnalyzer3.perfomTFIDFonOneArray();}
						
			
			
			// class Labels for testing and training are processed
			TreeProcess trainingProcessClass = new TreeProcess();
			TreeProcess testingProcessClass = new TreeProcess();
			TreeProcess validationProcessClass = new TreeProcess();
//			trainingProcessClass.addTrees( trainingProcess.getTreeList() );
//			testingProcessClass.addTrees( testingProcess.getTreeList() );
			
			trainingProcessClass.addTrees(Utilities.inputToMemory4(testCaseArgs.get(TestingParam.RESULTSPATH.getArgCode())+ "trainingProcessArrayListOfTrees"));
			testingProcessClass.addTrees(Utilities.inputToMemory4(testCaseArgs.get(TestingParam.RESULTSPATH.getArgCode())+ "testingProcessArrayListOfTrees"));
			validationProcessClass.addTrees(Utilities.inputToMemory4(testCaseArgs.get(TestingParam.RESULTSPATH.getArgCode())+ "validationProcessArrayListOfTrees"));
			trainingProcessClass.extractClassLabel();
			trainingProcessClass.extractMethodLabel( null );
			testingProcessClass.extractClassLabel();
			testingProcessClass.extractMethodLabel( null );
			validationProcessClass.extractClassLabel();
			validationProcessClass.extractMethodLabel( null );
			
			// The class training set is clustered
			domainLabel.clear();
			trainingProcessClass.getTreeList().forEach(node->node.getDataTraverser(domainLabel));
			
			ClusteringProcess trainingClassClusterer=new ClusteringProcess(domainLabel);
			
			trainingClassClusterer.semanticAnalysis();
			trainingClassClusterer.removeZeroFeature();


			if( testCaseArgs.get(TestingParam.KMEANWEIGTH.getArgCode()).equals("1") ) {
				System.out.println("Performing TFIDF on training class vectors");
				AnalysisTFIDF simpleAnalyzer4=new AnalysisTFIDF(tempClusterer.getWordModel(),trainingClassClusterer.getFeatureList());
				simpleAnalyzer4.perfomTFIDFonOneArray();}

			// The class test set is clustered
			domainLabel.clear();
			testingProcessClass.getTreeList().forEach(node->node.getDataTraverser(domainLabel));
			
			ClusteringProcess testingClassClusterer=new ClusteringProcess(domainLabel);
			testingClassClusterer.semanticAnalysis();
			testingClassClusterer.removeZeroFeature();
			
			if( testCaseArgs.get(TestingParam.KMEANWEIGTH.getArgCode()).equals("1") ) {
				System.out.println("Performing TFIDF on testing class vectors");
				AnalysisTFIDF simpleAnalyzer5=new AnalysisTFIDF(tempClusterer.getWordModel(),testingClassClusterer.getFeatureList());
				simpleAnalyzer5.perfomTFIDFonOneArray();}	
			
			// The class validation set is clustered
			domainLabel.clear();
			validationProcessClass.getTreeList().forEach(node->node.getDataTraverser(domainLabel));
			
			ClusteringProcess validationClassClusterer=new ClusteringProcess(domainLabel);
			validationClassClusterer.semanticAnalysis();
			validationClassClusterer.removeZeroFeature();
			
			if( testCaseArgs.get(TestingParam.KMEANWEIGTH.getArgCode()).equals("1") ) {
				System.out.println("Performing TFIDF on validation class vectors");
				AnalysisTFIDF simpleAnalyzer6=new AnalysisTFIDF(tempClusterer.getWordModel(),validationClassClusterer.getFeatureList());
				simpleAnalyzer6.perfomTFIDFonOneArray();}	
			
			
			HashMap<String,Integer> wholeLabelClusterTesting;
			HashMap<String,Integer> wholeLabelClusterTraining;
			HashMap<Integer, Double[]> h1;
			HashMap<Integer, Double[]> h2;
			HashMap<String,String> transformMap;
			boolean flag=true;
			
			testCaseArgs.put("-gspanResultFolder", testCaseArgs.get(TestingParam.RESULTSPATH.getArgCode())+"GSPAN");
			new File(testCaseArgs.get("-gspanResultFolder")).mkdirs();
			testCaseArgs.put("-gspanResultFolder", testCaseArgs.get(TestingParam.RESULTSPATH.getArgCode())+"GSPAN/");
			
			String[] gspanArgs= new String[3];
			
			MatchBetweenTwoClusterSets matcher=new MatchBetweenTwoClusterSets();
			
			ArrayList< XYSeries > listOfLines = new ArrayList< XYSeries >(); 
			final XYSeries lineOfTestingClassClusterEval = new XYSeries( " testing Class " );
			final XYSeries lineOfTrainingClassClusterEval = new XYSeries( " training Class " );
			final XYSeries lineOfTestingMethodClusterEval = new XYSeries( " testing Method " );
			final XYSeries lineOfTrainingMethodClusterEval = new XYSeries( " training Method " );
			final XYSeries lineOfMaxSimilarityMatchesMethod = new XYSeries ( " methods match " );
			final XYSeries lineOfMaxSimilarityMatchesClasses = new XYSeries ( " classes match " );
			final XYSeries lineOfAverageMaxSimilarityMatches = new XYSeries ( " classes and method similarity " );
			final XYSeries lineOfPrecisionRecall = new XYSeries ( " Precision Recall ");
			double tempSimilarity;
			TreeEditDistanceMetrics analyserTreeEditDist = new TreeEditDistanceMetrics();
			MatchingProcess treeMatcher = new MatchingProcess() ;
			Precision analyserPrecision = new Precision();
			Recall analyserRecall = new Recall();
			ArrayList<XYSeries> tempList = new ArrayList<XYSeries>();
			ArrayList< Pair< Double , Double > > tempListRecall;
			ArrayList< Pair< Double , Double > > tempListPrecision;
			ArrayList<Pair<Integer, Double>> tempAUCList = new ArrayList<Pair<Integer, Double>>();
			
			
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
					
			// Creating lines of cluster evaluation per number of cluster centers
			lineOfTestingClassClusterEval.add( clusterCenters , testingClassClusterer.getClusterEvaluation() );
			lineOfTrainingClassClusterEval.add( clusterCenters , trainingClassClusterer.getClusterEvaluation() );
			lineOfTestingMethodClusterEval.add( clusterCenters , testingMethodClusterer.getClusterEvaluation() );
			lineOfTrainingMethodClusterEval.add( clusterCenters , trainingMethodClusterer.getClusterEvaluation() );
				
			System.out.println("TFIDF Clusters ");
			AnalysisTFIDF analizerTestingMethod=new AnalysisTFIDF(testingMethodClusterer.getClusterFeatures(),tempClusterer.getWordModel());
			analizerTestingMethod.perfomTFIDF();
			AnalysisTFIDF analizerTestingClass=new AnalysisTFIDF(testingClassClusterer.getClusterFeatures(),tempClusterer.getWordModel());
			analizerTestingClass.perfomTFIDF();
			AnalysisTFIDF analizerTrainingMethod=new AnalysisTFIDF(trainingMethodClusterer.getClusterFeatures(),tempClusterer.getWordModel());
			analizerTrainingMethod.perfomTFIDF();
			AnalysisTFIDF analizerTrainingClass=new AnalysisTFIDF(trainingClassClusterer.getClusterFeatures(),tempClusterer.getWordModel());
			analizerTrainingClass.perfomTFIDF();


			// Creating files for gspan
			
			// Testing concat
			wholeLabelClusterTesting=Utilities.mergeHashMapsIntoOne(testingMethodClusterer.getLabelClusterMap(), testingClassClusterer.getLabelClusterMap());
						
			TreeProcess testingProcess2=new TreeProcess(Utilities.inputToMemory4(testCaseArgs.get(TestingParam.RESULTSPATH.getArgCode())+ "testingProcessArrayListOfTrees"));
			testingProcess2.extractMethodLabel();
			testingProcess2.extractClassLabel();
			
			
			Utilities.printDotFileAsInputToGspan2(testCaseArgs.get(TestingParam.RESULTSPATH.getArgCode())+"testingConcat.dot",testingProcess2.getTreeList(),wholeLabelClusterTesting);
			
			String gspanTestingResultsName = "testingResults"+ clusterCenters +".dot";
			
			// Training concat
			wholeLabelClusterTraining=Utilities.mergeHashMapsIntoOne(trainingMethodClusterer.getLabelClusterMap(), trainingClassClusterer.getLabelClusterMap());
			
			TreeProcess trainingProcess2=new TreeProcess(Utilities.inputToMemory4(testCaseArgs.get(TestingParam.RESULTSPATH.getArgCode())+ "trainingProcessArrayListOfTrees"));
			trainingProcess2.extractMethodLabel();
			trainingProcess2.extractClassLabel();
		
			Utilities.printDotFileAsInputToGspan2(testCaseArgs.get(TestingParam.RESULTSPATH.getArgCode())+"trainingConcat.dot",trainingProcess2.getTreeList(),wholeLabelClusterTraining);
			
			String gspanTrainingResultsName = "trainingResults"+ clusterCenters +".dot";
			

			gspanArgs[0] = "--graphFile=C:\\SPB_Data\\git\\GraMiF\\Graph_preprocess\\" + testCaseArgs.get(TestingParam.RESULTSPATH.getArgCode()).replace("/", "") + "\\testingConcat.dot" ;
			gspanArgs[1] = "--outputFile=C:\\SPB_Data\\git\\GraMiF\\Graph_preprocess\\" + testCaseArgs.get(TestingParam.RESULTSPATH.getArgCode()).replace("/", "") + "\\GSPAN\\"+gspanTestingResultsName;
			gspanArgs[2] = "--minimumFrequency=" + testCaseArgs.get( "-gspanSupport" ) + "%" ;
			System.out.println("Running Parsemis on testing");
			de.parsemis.Miner.run(gspanArgs);
			
			FileWriter fileWriter = new FileWriter(testCaseArgs.get( TestingParam.GSPANRESULTFOLDER.getArgCode()).concat( gspanTestingResultsName ) , true );
			PrintWriter printWriter = new PrintWriter( fileWriter );
			printWriter.println( "-clusterNum ".concat( testCaseArgs.get( "-kmeansClusterSizeMax" ) ) );
			printWriter.println( "-gspanSupport ".concat( testCaseArgs.get( "-gspanSupport" ) ) );
			printWriter.close();
			fileWriter.close();
			
			gspanArgs[0] = "--graphFile=C:\\SPB_Data\\git\\GraMiF\\Graph_preprocess\\" + testCaseArgs.get(TestingParam.RESULTSPATH.getArgCode()).replace("/", "") + "\\trainingConcat.dot" ;
			gspanArgs[1] = "--outputFile=C:\\SPB_Data\\git\\GraMiF\\Graph_preprocess\\" + testCaseArgs.get(TestingParam.RESULTSPATH.getArgCode()).replace("/", "") + "\\GSPAN\\"+gspanTrainingResultsName;
			gspanArgs[2] = "--minimumFrequency=" + testCaseArgs.get( "-gspanSupport" ) + "%" ;
			System.out.println("Running Parsemis on training");
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
			HashMap<Integer,Integer> methodMatcher = matcher.transformMapClustersToLabels3(flag);
			tempSimilarity = matcher.getSumMaxAverage();
			lineOfMaxSimilarityMatchesMethod.add( clusterCenters , matcher.getSumMaxAverage() );
			
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
					testingMethodClusterer.getLabelClusterMap(),
					h1,
					trainingMethodClusterer.getLabelClusterMap());
			HashMap<Integer,Integer> classMatcher = matcher.transformMapClustersToLabels3(flag);
			tempSimilarity += matcher.getSumMaxAverage();
			lineOfMaxSimilarityMatchesClasses.add( clusterCenters , matcher.getSumMaxAverage() );
			lineOfAverageMaxSimilarityMatches.add( clusterCenters , tempSimilarity / 2.0);
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
			
			analyserTreeEditDist.setActualTrees(treeTestingBracketForm, treeTrainingBracketForm) ;
			analyserTreeEditDist.createDistanceArrayPercentage() ;
			
			double[] similarityMeasure = { 0.0 , 0.1 , 0.2 , 0.3 , 0.4 , 0.5 , 0.6 , 0.7 , 0.8 , 0.9 , 1.0 };
			
			tempListPrecision = analyserPrecision.calculatePrecision( analyserTreeEditDist.getResultsDistance() , similarityMeasure );
			tempListRecall = analyserRecall.calculateRecall( analyserTreeEditDist.getResultsDistance() , similarityMeasure) ;
			
			for( int i = 0 ; i < similarityMeasure.length ; i++ ){
				lineOfPrecisionRecall.add( tempListRecall.get(i).x() , tempListPrecision.get(i).x() );
			}	
			
			
			tempAUCList.add(new Pair<Integer, Double>(Integer.valueOf(clusterCenters), Double.valueOf(AreaUnderCurver.calculateAUC(tempListRecall, tempListPrecision))) );
			
			tempList.clear();
			tempList.add(lineOfPrecisionRecall);
			Utilities.plot2dLineGraph(testCaseArgs.get(TestingParam.RESULTSPATH.getArgCode())+"PrecisionRecal" + clusterCenters + ".jpeg",
					" Precision Recal Curve ",
					tempList);
			lineOfPrecisionRecall.clear(); 
		}
		
		XYSeries AUCLine = new XYSeries("AUC");
		for (Pair<Integer, Double> tempPair : tempAUCList){
			AUCLine.add(tempPair.x(), tempPair.y());
		}
		
		tempList.clear();
		tempList.add(AUCLine);
		Utilities.plot2dLineGraph(testCaseArgs.get(TestingParam.RESULTSPATH.getArgCode())+" AUC of Recal and Precision"+".jpeg"
				, "AUC", tempList);
	
		}

	}

