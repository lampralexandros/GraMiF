package clusteringRefinement;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;
import feature.Feature;

/**
 * This class includes the framework to do tf-idf analysis
 * @author Alexandros Lampridis
 *
 */
public class AnalysisTFIDF {
	
	private ArrayList <Vector<Feature>> bagOfClusterFeatures;
	String[] innerWordModel;
	private int wordModelLength;
	private HashMap<Integer,Vector<String>> mapClustersCommonTerms;
	private HashMap<Integer,Double[]> mapClustersToTFIDFTerms;
	
	private ArrayList <Feature> bagOfFeature;
	
	/**
	 * @author Alexandros Lampridis
	 * @param ArrayList<Vector<Feature>> inputBagOfFeatures
	 * @param String[] inputWordModel
	 */
	public AnalysisTFIDF(ArrayList <Vector<Feature>> inputBagOfFeatures,String[] inputWordModel){
		mapClustersCommonTerms=new HashMap<Integer,Vector<String>>();
		mapClustersToTFIDFTerms=new HashMap<Integer,Double[]>();
		bagOfClusterFeatures=(ArrayList<Vector<Feature>>) inputBagOfFeatures;
		wordModelLength=inputBagOfFeatures.get(0).get(0).getLabelFeature().length;
		innerWordModel=inputWordModel;
		
	};
	
	/**
	 * @author Alexandros Lampridis
	 * @param inputBagOfFeatures
	 * @param inputWordModel
	 */
	public AnalysisTFIDF(String[] inputWordModel,ArrayList <Feature> inputBagOfFeature){
		mapClustersCommonTerms=new HashMap<Integer,Vector<String>>();
		mapClustersToTFIDFTerms=new HashMap<Integer,Double[]>();
		bagOfFeature= inputBagOfFeature;
		wordModelLength=inputBagOfFeature.get(0).getLabelFeature().length;
		innerWordModel=inputWordModel;
		
	};
	
	public HashMap<Integer,Vector<String>> getMapClustersCommonTerms(){
		return mapClustersCommonTerms;
	}
	
	public HashMap<Integer,Double[]> getMapClustersTFIDFTerms(){
		return mapClustersToTFIDFTerms;
	}
	
	//Main methods
	/**
	 * 
	 * @return
	 */
	public void perfomTFIDF(){
		int clusterCounter=1;
		
		// Revert tfidf weighting
		for(Vector<Feature> temp : bagOfClusterFeatures){
			for(Feature tempFeature : temp){
				for( int i=0 ; i < tempFeature.getLabelFeature().length ; i++){ 
						if( tempFeature.getLabelFeature()[i] > 0 )
							tempFeature.getLabelFeature()[i]=1;
				}
			}
		}
		
		double[] idf=idf();
		double[] tf;
		double[] tfidf;
		
		for(Vector<Feature> temp : bagOfClusterFeatures){
			
			tf=tfOfVectorNormalized(temp);
			
			tfidf=multiplyElementWise( tf,idf);
			
			// normalizing tfidf AFTER clearing NaN
			double max=0;
			for( int i=0 ; i < wordModelLength ; i++){
				max = ( max > tfidf[i] ) ? max : tfidf[i];
			}
			
			tfidf=multConstantByElementWise(1/max,tfidf);
			
			Double[] tfidfTemp = new Double[tfidf.length]; 
			for( int i =0 ; i < tfidf.length ; i++){
				tfidfTemp[i]= Double.valueOf(tfidf[i]);
			}
			
			mapClustersToTFIDFTerms.put(clusterCounter,  tfidfTemp);
			
			double medium=0;
			double counter=0;
			
			for(int i=0;i<wordModelLength;i++){
				// Nullifying NaN
				if(Double.isNaN(tfidf[i]))
					tfidf[i]=0;
				//Finding medium an weighted medium
				if(tfidf[i]>0){
				medium=medium+tfidf[i];
				counter=counter+1;
				}
			}
			medium=medium/counter;
			
			

			double answer[]= new double[tfidf.length];
			for(int i=0;i<wordModelLength;i++){
				// if over average keep it else nullify
				if(medium + medium/1.5 <= tfidf[i]){
					answer[i]=1;}
				else{
					answer[i]=0;
				}		
			}
			mapClustersCommonTerms.put(clusterCounter, returnTermsAsVector(answer));
			clusterCounter=clusterCounter+1;
		}
	}
	
	
	/**
	 * Perform tfidf on a single Array of features and returns it.
	 * @author Alexandros Lampridis
	 * @return
	 */
	public void perfomTFIDFonOneArray(){
		double[] idf=new double[wordModelLength];
		double[] tf=new double[wordModelLength];
		
		// Calculating D
		for(int i=0 ; i < wordModelLength ; i++){
			for(Feature tempf : bagOfFeature){
				idf[i]=idf[i]+tempf.getLabelFeature()[i];
			}
		}
		idf=divideConstantByElementWiseIgnoringZero(bagOfFeature.size(),idf);
		
		// Calculating idf
		idf=log10ElementWiseIgnoringZero(idf);
		
		// Calculating tf and passing tfidf to vector of the feature
		for(int j=0 ; j < bagOfFeature.size() ; j++){
			double tempSum=0;
			tf=bagOfFeature.get(j).getLabelFeature();
			for(int i=0 ; i < wordModelLength ; i++)
				tempSum=tempSum+tf[i];
			tf=multConstantByElementWise(1/tempSum,tf);
			for(int i=0 ; i < wordModelLength ; i++)
				tf[i]=tf[i]*idf[i];
			bagOfFeature.get(j).setLabelFeature(tf);
		}
		
		
	}
	
	//Supplement Functions
	/**
	 * Method to calculate normalized tf of vector and normalize by dividing with max.
	 * @param vec
	 * @return
	 */
	private double[] tfOfVectorNormalized(Vector<Feature> vec){
		double[] tf=new double[wordModelLength];
		double sumOfTerms=0;
		double max=0;
		
		for(Feature tempf: vec)
			tf=addElementWise(tf,tempf.getLabelFeature());
				
		for(double d :tf)
			sumOfTerms=d+sumOfTerms;
		
		tf=multConstantByElementWise(1/sumOfTerms,tf);
		
		for(double d :tf)
			max = (d >= max) ? d : max;
		
		tf=multConstantByElementWise(1/max,tf);
		
		
		return tf;
	}
	
	
	
	
	//Supplement Functions
	/**
	 * Method to calculate tf of vector POSSIBLY faulty on normalization
	 * @param vec
	 * @return
	 */
	private double[] tfOfVector(Vector<Feature> vec){
		double[] tf=new double[wordModelLength];
		double sumOfTerms=0;
		for(int i=0;i<wordModelLength;i++){
			tf[i]=0;
		}
		for(int i=0;i<wordModelLength;i++){
			tf[i]=0;
		}	
		for(Feature tempf: vec){
			tf=addElementWise(tf,tempf.getLabelFeature());
			
		}
			
		for(double d :tf){
			sumOfTerms=d+sumOfTerms;
		}
		
		tf=multConstantByElementWise(1/sumOfTerms,tf);
		return tf;
	}
	
	
	
	/**
	 * method to calculate idf by the type idf(word[],D)=log(N/D[word1, word2,...,wordn]
 	 * @author Alexandros Lampridis
 	 * @return double[] idf of all words
 	 * 
	 */
	private double[] idf(){
		double[] idfArray=new double[wordModelLength];
		idfArray=setD2();
		
		return idfArray;
	}
	
	
	
	
	/**
	 * method to calculate the number of occurrences of a word in all sets.
	 * @author Alexandros Lampridis
	 * @return D[length of wordModel]
	 * 
	 */
	private double[] setD(){
		double[] D=new double[wordModelLength];
		double[] tempSum=new double[wordModelLength];
		for(int i=0;i<wordModelLength;i++){
			D[i]=0;
			tempSum[i]=0;
		}
		for(Vector<Feature> vec : bagOfClusterFeatures ){

			for(int i=0;i<wordModelLength;i++){
				tempSum[i]=0;
			}
			
			for(Feature tempf: vec){
				tempSum=addElementWise(tempSum,tempf.getLabelFeature());
			}
			tempSum=ifBigerThan0then1(tempSum);
			D=addElementWise(D,tempSum);
		}
		return D;
	}
	
	/**
	 * method to properly calculate D.
	 * @author Alexandros Lampridis
	 * @return D[length of wordModel]
	 * 
	 */
	private double[] setD2(){
		
		double[] tempSum= new double[wordModelLength];
		double[] D= new double[wordModelLength];
		
		for( Vector<Feature> tempVec : bagOfClusterFeatures ){
				
			for(Feature tempFeature : tempVec)	
				tempSum=addElementWise(tempSum,tempFeature.getLabelFeature());
			
			for( int i=0 ; i < wordModelLength ; i++){
				D[i]= D[i] + ( (tempSum[i] > 0) ? 1.0 : 0.0 ) ;
				tempSum[i]=0;
			}
	
		}
		
		D=divideConstantByElementWiseIgnoringZero(bagOfClusterFeatures.size(), D);
		D=log10ElementWiseIgnoringZero(D);
		return D;
	}
	

	private double[] addElementWise(double[] array1, double[] array2) {
		if(array1.length!=array2.length)
			throw new Error("array1.length!=array2.length");
		for(int i=0;i<array1.length;i++)
			array1[i]=array1[i]+array2[i];
		return array1;
	}
	
	private double[] multiplyElementWise(double[] array1, double[] array2) {
		if(array1.length!=array2.length)
			throw new Error("array1.length!=array2.length");
		for(int i=0;i<array1.length;i++)
			array1[i]=array1[i]*array2[i];
		return array1;
	}
	
	private double[] ifBigerThan0then1(double[] array1){
		for(int i=0;i<array1.length;i++){
			if(array1[i]>1){
				array1[i]=1;
			}
		}
		return array1;
	}
	
	private double[] divideConstantByElementWise(double constant,double[] array1){
		for(int i=0;i<array1.length;i++){
				array1[i]=constant/array1[i];
		}
		return array1;
	}
	
	private double[] divideConstantByElementWiseIgnoringZero(double constant,double[] array1){
		for(int i=0;i<array1.length;i++){
			if(array1[i] != 0)
				array1[i]=constant/array1[i];
		}
		return array1;
	}
	
	private double[] multConstantByElementWise(double constant,double[] array1){
		for(int i=0;i<array1.length;i++){
				array1[i]=constant*array1[i];
		}
		return array1;
	}
	
	private double[] log10ElementWise(double[] array1){
		for(int i=0;i<array1.length;i++){
				array1[i]=java.lang.Math.log10(array1[i]);
		}
		return array1;
	}
	
	private double[] log10ElementWiseIgnoringZero(double[] array1){
		for(int i=0;i<array1.length;i++){
			if(array1[i] != 0)
				array1[i]=java.lang.Math.log10(array1[i]);
		}
		return array1;
	}
	
	
	private String returnTerms(double[] array){
		String tempS="";
		for(int i=0 ; i< array.length ; i++){
			if(array[i]==1.00){
				tempS=tempS.concat(" "+innerWordModel[i]);
			}
		}
		return tempS;
	}
	
	private String returnTerms(double[] array,double[] tfidf){
		String tempS="";
		for(int i=0 ; i< array.length ; i++){
			if(array[i]==1.00){
				tempS=tempS.concat(innerWordModel[i]+" "+tfidf[i]+",");
			}
		}
		return tempS;
	}
	private Vector<String> returnTermsAsVector(double[] array){
		Vector<String> tempVec = new Vector<String>();
		for(int i=0 ; i< array.length ; i++){
			if(array[i]==1.00){
				tempVec.add(innerWordModel[i]);
			}
		}
		return tempVec;
	}
	
	public void printTheCommonTerms(String fileName) throws IOException{
		FileWriter fileWriter=new FileWriter(fileName);
		PrintWriter printWriter = new PrintWriter(fileWriter);
		for(Integer key : mapClustersCommonTerms.keySet()){
			Vector<String> tempV= mapClustersCommonTerms.get(key);
			printWriter.println("Key="+key+" {");
			if( tempV == null){
				printWriter.println("[]");
			}
			else{
				for(String tempS : tempV){
				printWriter.print(tempS+", ");	
				}
			}
			printWriter.println(" }");
			
		}
		
		printWriter.close();
		fileWriter.close();
	}
	
}
