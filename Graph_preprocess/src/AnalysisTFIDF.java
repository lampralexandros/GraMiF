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

	/**
	 * @author Alexandros Lampridis
	 * @param ArrayList<Vector<Feature>> inputBagOfFeatures
	 * @param String[] inputWordModel
	 */
	public AnalysisTFIDF(ArrayList <Vector<Feature>> inputBagOfFeatures,String[] inputWordModel){
		mapClustersCommonTerms=new HashMap<Integer,Vector<String>>();
		bagOfClusterFeatures=(ArrayList<Vector<Feature>>) inputBagOfFeatures;
		wordModelLength=inputBagOfFeatures.get(0).get(0).getLabelFeature().length;
		innerWordModel=inputWordModel;
		
	};
	
	public HashMap<Integer,Vector<String>> getMapClustersCommonTerms(){
		return mapClustersCommonTerms;
	}
	
	//Main methods
	/**
	 * 
	 * @return
	 */
	public void perfomTFIDF(){
		String temps = null;
		int clusterCounter=1;
		double[] idf=idf();
		double[] tfidf;
		
		for(Vector<Feature> temp : bagOfClusterFeatures){
			tfidf=multiplyElementWise( tfOfVector(temp),idf);
			
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
				if(medium + medium/1.5<= tfidf[i]){
					answer[i]=1;}
				else{
					answer[i]=0;
				}		
			}
			clusterCounter=clusterCounter+1;
			mapClustersCommonTerms.put(clusterCounter, returnTermsAsVector(answer));
			
			
			
		}
		
		
		
		
		
		
	}
	
	
	
	
	//Supplement Functions
	
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
		idfArray=setD();
		idfArray=log10ElementWise(divideConstantByElementWise(bagOfClusterFeatures.size(),idfArray));
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
	
}
