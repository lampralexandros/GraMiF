import java.util.ArrayList;
import java.util.Vector;
import feature.Feature;

/**
 * This class includes the framework to do tf-idf analysis
 * @author Alexandros Lampridis
 *
 */
public class AnalysisTFIDF {
	
	private ArrayList <Vector<Feature>> bagOfClusterFeatures;
	private String[] wordModel;

	//constructor
	@SuppressWarnings("unchecked")
	/**
	 * @author Alexandros Lampridis
	 * @param ArrayList<Vector<Feature>> inputBagOfFeatures
	 * @param String[] inputWordModel
	 */
	public AnalysisTFIDF(ArrayList <Vector<Feature>> inputBagOfFeatures, String[] inputWordModel){
		bagOfClusterFeatures= new ArrayList<Vector<Feature>>();
		bagOfClusterFeatures=(ArrayList<Vector<Feature>>) inputBagOfFeatures.clone();
		wordModel=inputWordModel.clone();
	};
	
	//Main methods
	/**
	 * 
	 * @return
	 */
	public void perfomTFIDF(){
		double[] D=setD();
		
	}
	
	
	
	
	//Supplement Functions
	
	private double[] tfOfVector(Vector<Feature> vec){
		double[] tf=new double[wordModel.length];
		double sumOfTerms=0;
		for(int i=0;i<wordModel.length;i++){
			tf[i]=0;
		}
		for(int i=0;i<wordModel.length;i++){
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
		double[] idfArray=new double[wordModel.length];
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
		double[] D=new double[wordModel.length];
		double[] tempSum=new double[wordModel.length];
		for(int i=0;i<wordModel.length;i++){
			D[i]=0;
			tempSum[i]=0;
		}
		for(Vector<Feature> vec : bagOfClusterFeatures ){

			for(int i=0;i<wordModel.length;i++){
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
}
