


import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import org.jfree.data.xy.XYSeries;

import feature.Feature;

import net.sf.javaml.clustering.Clusterer;
import net.sf.javaml.clustering.IterativeKMeans;
import net.sf.javaml.clustering.KMeans;
import net.sf.javaml.clustering.KMedoids;
import net.sf.javaml.clustering.evaluation.AICScore;
import net.sf.javaml.clustering.evaluation.BICScore;
import net.sf.javaml.clustering.evaluation.ClusterEvaluation;
import net.sf.javaml.clustering.evaluation.HybridCentroidSimilarity;
import net.sf.javaml.clustering.evaluation.HybridPairwiseSimilarities;
import net.sf.javaml.clustering.evaluation.SumOfAveragePairwiseSimilarities;
import net.sf.javaml.clustering.evaluation.SumOfCentroidSimilarities;
import net.sf.javaml.clustering.evaluation.SumOfSquaredErrors;
import net.sf.javaml.clustering.evaluation.TraceScatterMatrix;
import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.DefaultDataset;
import net.sf.javaml.core.DenseInstance;
import net.sf.javaml.core.Instance;
import net.sf.javaml.distance.AngularDistance;
import net.sf.javaml.distance.ChebychevDistance;
import net.sf.javaml.distance.CosineDistance;
import net.sf.javaml.distance.CosineSimilarity;
import net.sf.javaml.distance.DistanceMeasure;
import net.sf.javaml.distance.EuclideanDistance;
import net.sf.javaml.distance.JaccardIndexDistance;
import net.sf.javaml.distance.JaccardIndexSimilarity;
import net.sf.javaml.distance.LinearKernel;
import net.sf.javaml.distance.MahalanobisDistance;
import net.sf.javaml.distance.ManhattanDistance;
import net.sf.javaml.distance.MaxProductSimilarity;
import net.sf.javaml.distance.MinkowskiDistance;
import net.sf.javaml.distance.NormDistance;
import net.sf.javaml.distance.PearsonCorrelationCoefficient;
import net.sf.javaml.distance.RBFKernel;
import net.sf.javaml.distance.RBFKernelDistance;
import net.sf.javaml.distance.SpearmanFootruleDistance;
import net.sf.javaml.distance.SpearmanRankCorrelation;
import net.sf.javaml.distance.dtw.DTWSimilarity;


public class ClusteringProcess {
	private ArrayList<Feature> featureList;
	private ArrayList<String> Labels;
	private HashMap<String,Integer> labelClusterMap;
	private ArrayList <Vector<Feature>> clusterFeatures;
	private int numClusters;
	private String[] wordModelWords;
	private int kmeansClusterSizeMin,kmeansClusterSizeMax,kmeansIterations;
	private DistanceMeasure kmeansDM;
	private ClusterEvaluation kmeansCE;
	private ArrayList<Dataset[]> clusterOfData;
	private ArrayList<Double> scoresOfCluster;
	private ArrayList<String> emptyFeatureVectorLabel;
	private int clusteringRepeatNumber;
	private double clusterEvaluation;
	
	//Constructor
	public ClusteringProcess(ArrayList<String> inputLabel){
		clusterFeatures=new ArrayList<Vector<Feature>>();
		labelClusterMap=new HashMap<String,Integer>();
		featureList=new ArrayList<Feature>();
		Labels=new ArrayList<String>();
		emptyFeatureVectorLabel=new ArrayList<String>();
		Labels=inputLabel;
	}
	// Set/Get
	/**
	 * Returns a hash map with key the label and value the cluster number
	 * @author Alexandros Lampridis 
	 * @return HashMap<String,Integer>
	 */
	public HashMap<String,Integer> getLabelClusterMap(){
		return labelClusterMap;
	}
	
	/**
	 * @author Alexandros Lampridis
	 * @return numClusters
	 */
	public int getNumClusters(){
		return numClusters;
	}
	
	/**
	 * @author Alexandros Lampridis
	 * @return featureList
	 */
	public ArrayList<Feature> getFeatureList(){
		return featureList;
	}
	
	/** Returns an array list of clusters containing features 
	 * @author Alexandros Lampridis
	 * @return ArrayList<Vector<Feature>>
	 */
	public ArrayList<Vector<Feature>> getClusterFeatures(){
		return clusterFeatures;
	}
	
	/**	Returns an array of Strings describing the WordModel
	 * @author Alexandros Lampridis
	 * @return String[]
	 */
	public String[] getWordModel(){
		return wordModelWords;
	}
	/**
	 * @author Alexandros Lampridis
	 * @return Double The evaluation of the clusters
	 */
	public Double getClusterEvaluation(){
		return clusterEvaluation;
	}
	
	// Basic Functions 
	
	// Function to do semantic analysis. This function creates a map of labels to features 
	public void semanticAnalysis(){
		// Hashmap to identify the unique labels
		HashMap<String,Integer> uniqueLabels=new HashMap<String,Integer>();
		
		//Passing for the first time to calculate all the dimensions
		for(String label:Labels){
			//System.out.println("Label="+label);
			WordModel.commonWordNet.getSentenceFeatureVector(label);
			if(uniqueLabels.containsKey(label))
				uniqueLabels.put(label, uniqueLabels.get(label)+1);
			else
				uniqueLabels.put(label, 1);
		}
		//Creating the list of feature
		for(String label:uniqueLabels.keySet()){
			Feature tempFeature=new Feature(label,WordModel.commonWordNet.getSentenceFeatureVector(label));
			featureList.add(tempFeature);
		}
		//Saving the word model
		double[] tempvector=new double[WordModel.commonWordNet.getCurrentFeatureVectorLength()]; 
		for(int i=0;i<tempvector.length;i++)
			tempvector[i]=1;
 		wordModelWords=WordModel.commonWordNet.convertVectorToFeatureSentence(tempvector).split(" ");
	}
	
	/**
	 * Removes Features from the featureList, that had a feature vector of zeros. 
	 * */
	public void removeZeroFeature(){
		double flag;
		Iterator<Feature> iter= featureList.iterator();
		while(iter.hasNext()){
			flag=0;
			Feature tempFeature = iter.next();
			for(double vec :tempFeature.getLabelFeature()){
				flag=flag+vec;
			}
			if(flag==0){
				emptyFeatureVectorLabel.add(tempFeature.getLabel());
				iter.remove();
			}	
		}
	}
	
	
	/**
	 * A clusterer Iterative Knn with input args as parameters
	 * @author Alexandros Lampridis
	 * @param testingArgs
	 */
	
	public void iterativeClusteringWithInputArgs(HashMap<String,String> testingArgs){
		
		
		inputTestingArguments(testingArgs);
		
		//creating the data set
		Dataset data=new DefaultDataset();
		for(Feature tempFeature:featureList){
			Instance tempInstance=new DenseInstance(tempFeature.getLabelFeature());
			data.add(tempInstance);
		}

		Clusterer km=new IterativeKMeans(kmeansClusterSizeMin,kmeansClusterSizeMax,kmeansIterations,kmeansDM,kmeansCE);
		
		Dataset[] clusters = km.cluster(data);
		
		numClusters=clusters.length;
		Double[] a = new Double[data.get(0).values().size()];
		int counter=0;
		for(Dataset cluster:clusters){
			counter=counter+1;
			Vector<Feature> tempVec=new Vector<Feature>();
			for(Instance inst:cluster){
				for(Feature tempFeature :featureList){
					inst.values().toArray(a);
					if(compare2doubleArr(a,tempFeature.getLabelFeature())){
						labelClusterMap.put(tempFeature.getLabel(),counter);
						tempVec.add(tempFeature);
					} 
				}
			}
			clusterFeatures.add(tempVec);
		}
		
	}
	
	
	
	/**
	 * A simple quick clusterer Iterative Knn with AIC metric
	 * @author Alexandros Lampridis
	 * @param kMin
	 * The minimum number of clusters
	 * @param kMax
	 * The maximum number of clusters
	 * @param numIter
	 * The number of iteration's
	 */
	
	public void doDefaultClusteringDense(int kMin,int kMax,int numIter){
		//creating the data set
		Dataset data=new DefaultDataset();
		for(Feature tempFeature:featureList){
			Instance tempInstance=new DenseInstance(tempFeature.getLabelFeature());
			data.add(tempInstance);
		}
		//default settings for the clusterer
		DistanceMeasure dm=new CosineSimilarity();
		ClusterEvaluation ce=new AICScore();
		Clusterer km=new IterativeKMeans(kMin,kMax,numIter,dm,ce);
		
		Dataset[] clusters = km.cluster(data);
		
		numClusters=clusters.length;
		Double[] a = new Double[data.get(0).values().size()];
		int counter=0;
		for(Dataset cluster:clusters){
			counter=counter+1;
			Vector<Feature> tempVec=new Vector<Feature>();
			for(Instance inst:cluster){
				for(Feature tempFeature :featureList){
					inst.values().toArray(a);
					if(compare2doubleArr(a,tempFeature.getLabelFeature())){
						labelClusterMap.put(tempFeature.getLabel(),counter);
						tempVec.add(tempFeature);
					} 
				}
			}
			clusterFeatures.add(tempVec);
		}
		
	}
	
	/**
	 * A simple quick clusterer Knn for testing purpose 
	 * @author Alexandros Lampridis
	 * 
	 */
	public void doQuickClusteringDense(){
		//creating the data set
		Dataset data=new DefaultDataset();
		for(Feature tempFeature:featureList){
			Instance tempInstance=new DenseInstance(tempFeature.getLabelFeature());
			data.add(tempInstance);
		}
		//default settings for the clusterer
		DistanceMeasure dm=new CosineSimilarity();
		Clusterer km=new KMeans(10,200,dm);
		Dataset[] clusters = km.cluster(data);
		numClusters=clusters.length;
		Double[] a = new Double[data.get(0).values().size()];
		int counter=0;
		for(Dataset cluster:clusters){
			counter=counter+1;
			Vector<Feature> tempVec=new Vector<Feature>();
			for(Instance inst:cluster){
				for(Feature tempFeature :featureList){
					inst.values().toArray(a);
					if(compare2doubleArr(a,tempFeature.getLabelFeature())){
						labelClusterMap.put(tempFeature.getLabel(),counter);
						tempVec.add(tempFeature);
					} 
				}
			}
			clusterFeatures.add(tempVec);
		}
		
	}
	
	
	public void doClusteringKeepingResultsWithInputArgs(HashMap<String,String> testCaseArgs){
		
		// initialize ArrayList of clusters
		clusterOfData =new ArrayList<Dataset[]>();
		scoresOfCluster = new ArrayList<Double>();
		// input the TestCase parameters
		inputTestingArguments(testCaseArgs);
		
		//creating the data set
		Dataset data=new DefaultDataset();
		for(Feature tempFeature:featureList){
			Instance tempInstance=new DenseInstance(tempFeature.getLabelFeature());
			data.add(tempInstance);
		}
		
		Clusterer kmeans,kmeans2;
		Dataset[] dataResults=null;
		for(int k = kmeansClusterSizeMin ; k <= kmeansClusterSizeMax ; k++ ){
			System.out.println("Clustering with: "+ k +" Centers");
		   
			try {
		    	kmeans=new KMeans(k,kmeansIterations,kmeansDM);
		    	dataResults=kmeans.cluster(data);
		      } catch (Exception e) {
		        System.out.println("Something went wrong. But moving forward");
		      }
			
			
			
			kmeans2=new KMedoids(k,kmeansIterations,kmeansDM);
			for( int numberOfRepeats = 0 ; numberOfRepeats < clusteringRepeatNumber ; numberOfRepeats++ ){
				
				  try {
					    Dataset[] tempData=kmeans2.cluster(data);
					    System.out.println("Repetition number:" + (numberOfRepeats+1) +" Temp score: "+ kmeansCE.score(tempData) +" Score: " + kmeansCE.score(dataResults) );
						dataResults = ( kmeansCE.compareScore( kmeansCE.score(tempData) ,  kmeansCE.score(dataResults) )  ) ? dataResults : tempData ;
				      } catch (Exception e) {
				        System.out.println("Something went wrong. But moving forward");
				      }
				
				
				 
				
			}
			
			
			clusterOfData.add(dataResults);
		}
		
		for(Dataset[] tempDataset :clusterOfData){
			scoresOfCluster.add(kmeansCE.score(tempDataset));
		}
		
		Dataset[] clusters= clusterOfData.get(getTheBestSocre());
		
		numClusters=clusters.length;
		Double[] a = new Double[data.get(0).values().size()];
		int counter=0;
		for(Dataset cluster:clusters){
			counter=counter+1;
			Vector<Feature> tempVec=new Vector<Feature>();
			for(Instance inst:cluster){
				for(Feature tempFeature :featureList){
					inst.values().toArray(a);
					if(compare2doubleArr(a,tempFeature.getLabelFeature())){
						labelClusterMap.put(tempFeature.getLabel(),counter);
						tempVec.add(tempFeature);
					} 
				}
			}
			clusterFeatures.add(tempVec);
		}
		
		
	}

	
	public void doClusteringWithInputArgsExceptKmeansCenters(HashMap<String,String> testCaseArgs, int numClusters){
		
		// initialize ArrayList of clusters
		clusterFeatures.clear();
		labelClusterMap.clear();
		// input the TestCase parameters
		// TODO change the input method 
		inputTestingArguments(testCaseArgs);
		
		//creating the data set
		Dataset data=new DefaultDataset();
		for(Feature tempFeature:featureList){
			Instance tempInstance=new DenseInstance(tempFeature.getLabelFeature());
			data.add(tempInstance);
		}
		
		Clusterer kmeans,kmeans2;
		Dataset[] dataResults=null;

			System.out.println("Clustering with: "+ numClusters +" Centers");
		   
			try {
				
		    	kmeans = new KMeans( numClusters , kmeansIterations , kmeansDM );
		    	dataResults = kmeans.cluster( data );
		    	System.out.println( "Repetition number: 0 Score: " + kmeansCE.score( dataResults ) );
		    	
		      } catch (Exception e) {
		        System.out.println("Something went wrong. But moving forward");
		      }
			
			
			
			kmeans2=new KMedoids( numClusters , kmeansIterations , kmeansDM );
			for( int numberOfRepeats = 0 ; numberOfRepeats < clusteringRepeatNumber ; numberOfRepeats++ ){
				
				  try {
					    Dataset[] tempData=kmeans2.cluster(data);
					    System.out.println("Repetition number:" + (numberOfRepeats+1) +" Temp score: "+ kmeansCE.score(tempData) +" Score: " + kmeansCE.score(dataResults) );
						dataResults = ( kmeansCE.compareScore( kmeansCE.score(tempData) ,  kmeansCE.score(dataResults) )  ) ? dataResults : tempData ;
				      } catch (Exception e) {
				        System.out.println("Something went wrong. But moving forward with kmeans");
				        kmeans = new KMeans( numClusters , kmeansIterations , kmeansDM );
				        Dataset[] tempData=kmeans.cluster(data);
					    System.out.println("Repetition number:" + (numberOfRepeats+1) +" Temp score: "+ kmeansCE.score(tempData) +" Score: " + kmeansCE.score(dataResults) );
						dataResults = ( kmeansCE.compareScore( kmeansCE.score(tempData) ,  kmeansCE.score(dataResults) )  ) ? dataResults : tempData ;
				      }
			}
		
			
		Dataset[] clusters= dataResults;
		//keeping final score
		clusterEvaluation = kmeansCE.score(clusters);
		
		numClusters=clusters.length;
		Double[] a = new Double[data.get(0).values().size()];
		int counter=0;
		for(Dataset cluster:clusters){
			counter=counter+1;
			Vector<Feature> tempVec=new Vector<Feature>();
			for(Instance inst:cluster){
				for(Feature tempFeature :featureList){
					inst.values().toArray(a);
					if(compare2doubleArr(a,tempFeature.getLabelFeature())){
						labelClusterMap.put(tempFeature.getLabel(),counter);
						tempVec.add(tempFeature);
					} 
				}
			}
			clusterFeatures.add(tempVec);
		}
		
		
	}
	
	
	/**
	 * Compare the elements of two double arrays to check if they are same.
	 * @param a1 double[]
	 * @param a2 double[]
	 * @return True if a1[].=a2[] False if a1[n].!a2[n] 
	 */
	public boolean compare2doubleArr(double[] a1, double[] a2){
		boolean flag;
		//This if it is if the vector dimension differs
		if(a1.length!=a2.length){System.exit(1);;}
		flag=true;
		for(int i=0;i<a1.length;i++){if(a1[i]!=a2[i]){flag=false;break;}}
			return flag;
		}	
	/**
	 * Compare the elements of one Double array and one double array to check if they are same.
	 * @param a1 Double[]
	 * @param a2 double[]
	 * @return True if a1[].=a2[] False if a1[n].!a2[n] 
	 */
	public boolean compare2doubleArr(Double[] a1, double[] a2){
		boolean flag;
		//This if it is if the vector dimension differs
		if(a1.length!=a2.length){System.exit(1);;}
		flag=true;
		for(int i=0;i<a1.length;i++){if(a1[i].doubleValue()!=a2[i]){flag=false;break;}}
			return flag;
		}	
	
	/**
	 * @author Alexandros Lampridis
	 */
	public void printClusters(){
		int counter=1;
		for(Vector<Feature> tempVec:clusterFeatures){
			System.out.println("Cluster num="+String.valueOf(counter));
			for(Feature tempFeat:tempVec){
				System.out.print(tempFeat.getLabel()+" ");
			}
		}
	}
	
	/**
	 * This function prints the cluster name and label content of the cluster in the file "cluster.txt"
	 * @author Alexandros Lampridis
	 * @param fileName
	 * The name of the file to save the clusters
	 * 
	 */
	public void saveIntoFileClusters(String fileName){
		int counter=1;
		try {
			FileWriter fileWriter=new FileWriter(fileName);
			PrintWriter printWriter = new PrintWriter(fileWriter);
			for(Vector<Feature> tempVec:clusterFeatures){
				printWriter.println("Cluster num="+String.valueOf(counter));
				for(Feature tempFeat:tempVec){
					printWriter.print(tempFeat.getLabel()+" ");
				}
				counter=counter+1;
				printWriter.println("");
			}
			printWriter.close();
			fileWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * @author Alexandros Lampridis
	 * @return maxIndex which is the best Clustering set based on the Clustering Evaluation
	 */
	private int getTheBestSocre(){
		int maxIndex=0;
		for( int i=1 ; i < scoresOfCluster.size() ; i++){
				boolean flag2 = kmeansCE.compareScore( scoresOfCluster.get(maxIndex).doubleValue(), scoresOfCluster.get(i).doubleValue());
			if(flag2)
				maxIndex=i;
		}
		return maxIndex;
	}
	
	
	
	private void inputTestingArguments(HashMap<String,String> testingArgs){
		// initialize 
		kmeansClusterSizeMin= Integer.parseInt(testingArgs.get("-kmeansClusterSizeMin"));
		kmeansClusterSizeMax= Integer.parseInt(testingArgs.get("-kmeansClusterSizeMax"));
		kmeansIterations = Integer.parseInt(testingArgs.get("-kmeansIterations"));
		clusteringRepeatNumber = Integer.parseInt(testingArgs.get("-kmeansRepetitions"));
		kmeansDM=findDM(testingArgs.get("-kmeansDM"));
		kmeansCE=findCE(testingArgs.get("-kmeansCE"));
	}
	
	private DistanceMeasure findDM(String dmName){
		switch(dmName){

		case "AngularDistance":
			return new AngularDistance();
		case "ChebychevDistance":
			return new ChebychevDistance();
		case "CosineDistance":
			return new CosineDistance();
		case "CosineSimilarity":
			return new CosineSimilarity();
		case "DTWSimilarity":
			return new DTWSimilarity();
		case "EuclideanDistance":
			return new EuclideanDistance();
		case "JaccardIndexDistance":
			return new JaccardIndexDistance();
		case "JaccardIndexSimilarity":
			return new JaccardIndexSimilarity();
		case "LinearKernel":
			return new LinearKernel();
		case "MahalanobisDistance":
			return new MahalanobisDistance();
		case "ManhattanDistance":
			return new ManhattanDistance();
		case "MaxProductSimilarity":
			return new MaxProductSimilarity();
		case "MinkowskiDistance":
			return new MinkowskiDistance();
		case "NormDistance":
			return new NormDistance();
		case "PearsonCorrelationCoefficient":
			return new PearsonCorrelationCoefficient();
		case "RBFKernel":
			return new RBFKernel();
		case "RBFKernelDistance":
			return new RBFKernelDistance();
		case "SpearmanFootruleDistance": 
			return new SpearmanFootruleDistance();
		case "SpearmanRankCorrelation":
			return new SpearmanRankCorrelation();
		}
		return null;
		
	}
	private ClusterEvaluation findCE(String ceName){
		switch(ceName){

		case "AICScore":
			return new AICScore();
		case "BICScore":
			return new BICScore();

		case "HybridCentroidSimilarity":
			return new HybridCentroidSimilarity();
		case "HybridPairwiseSimilarities":
			return new HybridPairwiseSimilarities();


		case "SumOfAveragePairwiseSimilarities":
			return new SumOfAveragePairwiseSimilarities();
		case "SumOfCentroidSimilarities":
			return new SumOfCentroidSimilarities();
		case "SumOfSquaredErrors":
			return new SumOfSquaredErrors();

		case "TraceScatterMatrix":
			return new TraceScatterMatrix();

		}
		return null;
		
	}
	

	
	public XYSeries getXYPlotClustEvalDoubleXAxis(String lineName){
		
		final XYSeries tempSeries = new XYSeries( lineName );
	    
		for( int i = 0 ; i < clusterOfData.size() ; i++ ){
			tempSeries.add( clusterOfData.get(i).length  , scoresOfCluster.get(i));
		}
		
		return tempSeries;
	}
	
	
}
