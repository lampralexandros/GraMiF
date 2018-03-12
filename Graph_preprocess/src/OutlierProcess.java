import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Vector;
import java.util.stream.DoubleStream;

import feature.Feature;
import net.sf.javaml.core.DenseInstance;
import net.sf.javaml.core.Instance;
import net.sf.javaml.distance.CosineSimilarity;
import net.sf.javaml.distance.DistanceMeasure;

/**
 * This class represent a procedure to do outlier analysis
 * @author Alexandros Lampridis
 *
 */
public class OutlierProcess {

	private ArrayList<Vector<Feature>> clusterFeatures;
	private HashMap<String,Integer> outlierMap;
	private int numClusters;
	//constructor
	/**
	 * @author Alexandros Lampridis
	 * @param inputClusterFeature
	 * An ArrayList which contains the clusters of features
	 */
	public OutlierProcess(ArrayList<Vector<Feature>> inputClusterFeature){
		clusterFeatures=new ArrayList<Vector<Feature>>();
		clusterFeatures=inputClusterFeature;
		outlierMap=new HashMap<String,Integer>();
		numClusters=clusterFeatures.size();
	}
	
	//Set Get
	/**	  
	 * @author Alexandros Lampridis
	 */
	public ArrayList<Vector<Feature>> getClusterFeature(){
		return clusterFeatures;
	}
	
	/**	  
	 * @author Alexandros Lampridis
	 */
	public HashMap<String,Integer> getOutlierMap(){
		return outlierMap;
	}
	// Basic functions
	/**
	 * This function performs a basic IQR method to determine outliers in a cluster.
	 * The metric used is the similarity of feature vector.
	 * @author Alexandros Lampridis
	 */
	public void defualtIQR(){
		double lowerTuckey;
		double[] averageSimilarity;
		double[][] similarity;
		int numOutlier=numClusters;
		// Creating the Distance Measure as Cosine Similarity 
		DistanceMeasure dm=new CosineSimilarity();
		Instance TempInstance1;//=new DenseInstance(UniqueFeatures.get(0).getLabelFeature());
		Instance TempInstance2;//=new DenseInstance(UniqueFeatures.get(t).getLabelFeature());
		for(Vector<Feature> vec : clusterFeatures){
				similarity=new double[vec.size()][vec.size()];
				averageSimilarity=new double[vec.size()];
			for(int i=0;i<vec.size();i++){
				TempInstance1=new DenseInstance(vec.get(i).getLabelFeature());
				for(int j=0;j<vec.size();j++){
					TempInstance2=new DenseInstance(vec.get(j).getLabelFeature());
					similarity[i][j]=dm.measure(TempInstance1, TempInstance2);	
				}
				// Calculating the average similarity
				for(int j=0;j<vec.size();j++){
					averageSimilarity[i]+=similarity[i][j];
				}
				// subtract self similarity and calculate the average
				averageSimilarity[i]=(averageSimilarity[i]-similarity[i][i])/(vec.size()-1);	
			}
			if(averageSimilarity.length>4)
				lowerTuckey=lowerTukeysFence(averageSimilarity.clone(),1);
				else
				lowerTuckey=0;
			for(int i=0;i<vec.size();i++){
				if(averageSimilarity[i]<lowerTuckey){
					numOutlier=numOutlier+1;
					outlierMap.put(vec.get(i).getLabel(), numOutlier);
					vec.remove(i);
				}
			}
			
			
		}
		
	}
	/**
	 * This function performs a basic IQR method to determine outliers in a cluster.
	 * The metric used is sum of feature vector.
	 * @author Alexandros Lampridis
	 */
	public void defualtIQR2(){
		double lowerTuckey;
		double[] sumVector;
		
		int numOutlier=numClusters;
		// Creating a vector to store all the 

		for(Vector<Feature> vec : clusterFeatures){

			sumVector=new double[vec.size()];
			for(int i=0;i<vec.size();i++){
				sumVector[i]=  Arrays.stream(vec.get(i).getLabelFeature()).sum(); 
				
				// subtract self similarity and calculate the average
				sumVector[i]=	sumVector[i]-1;
			}
			if(sumVector.length>4)
				lowerTuckey=lowerTukeysFence(sumVector.clone(),1);
				else
				lowerTuckey=0;
			for(int i=0;i<vec.size();i++){
				if(sumVector[i]<lowerTuckey){
					numOutlier=numOutlier+1;
					outlierMap.put(vec.get(i).getLabel(), numOutlier);
					vec.remove(i);
				}
			}
			
			
		}
		
	}
	private double lowerTukeysFence(double[] inputArray,double k){
		double Q1,Qm,Q3,IQR;
		Arrays.sort(inputArray);
		Qm=median(inputArray);
		if( inputArray.length%2==0){
			//lower half array
			double[] temp=new double[inputArray.length/2];
			for(int i=0;i<inputArray.length/2;i++)
				temp[i]=inputArray[i];
			Q1=median(temp);
			//upper half array
			for(int i=inputArray.length/2;i<inputArray.length-1;i++)
				temp[i-inputArray.length/2]=inputArray[i];
			Q3=median(temp);
		}else{
			//lower half array
			double[] temp=new double[(int)(inputArray.length/2)];
			for(int i=0;i<(int)(inputArray.length/2)-1;i++)
				temp[i]=inputArray[i];
			Q1=median(temp);
			//upper half array
			for(int i=(int)(inputArray.length/2)+1;i<inputArray.length-1;i++)
				temp[i-((int)(inputArray.length/2)+1)]=inputArray[i];
			Q3=median(temp);
		}

		return Q1-k*(Q3-Q1);
	}
	/**
	 * 
	 */
	private double median(double[] inputArray){
		
		if(inputArray.length % 2==0){
			//the Array has even size 
			return (inputArray[inputArray.length/2-1]+inputArray[inputArray.length/2])/2;
			
		}
		else{
			//the Array has even odd 
			return inputArray[ (int)(inputArray.length/2)];
		}
	}
	
}
