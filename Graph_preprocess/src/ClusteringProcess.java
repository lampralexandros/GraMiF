/**
 * 
 * @author Alexandros Lampridis 
 */

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import feature.Feature;
import net.sf.javaml.clustering.Clusterer;
import net.sf.javaml.clustering.IterativeKMeans;
import net.sf.javaml.clustering.KMeans;
import net.sf.javaml.clustering.evaluation.AICScore;
import net.sf.javaml.clustering.evaluation.ClusterEvaluation;
import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.DefaultDataset;
import net.sf.javaml.core.DenseInstance;
import net.sf.javaml.core.Instance;
import net.sf.javaml.distance.CosineSimilarity;
import net.sf.javaml.distance.DistanceMeasure;

public class ClusteringProcess {
	private ArrayList<Feature> featureList;
	private ArrayList<String> Labels;
	private HashMap<String,Integer> labelClusterMap;
	private ArrayList <Vector<Feature>> clusterFeatures  ;
	private int numClusters;
	private String[] wordModelWords;
	
	//Constructor
	public ClusteringProcess(ArrayList<String> inputLabel){
		clusterFeatures=new ArrayList<Vector<Feature>>();
		labelClusterMap=new HashMap<String,Integer>();
		featureList=new ArrayList<Feature>();
		Labels=new ArrayList<String>();
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
			for(double vec :iter.next().getLabelFeature()){
				flag=flag+vec;
			}
			if(flag==0){
				iter.remove();
			}	
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
	
	
	
	
	
}
