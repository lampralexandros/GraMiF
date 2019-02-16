import java.util.ArrayList;
import java.util.Vector;

import feature.Feature;

/**
 * This class includes the framework to do tf-idf analysis
 * @author Alexandros Lampridis
 *
 */
public class AnalysisTFIDF {
	
	private ArrayList <Vector<Feature>> bagOfFeatures;
	private String[] wordModel;

	//constructor
	@SuppressWarnings("unchecked")
	/**
	 * @author Alexandros Lampridis
	 * @param ArrayList<Vector<Feature>> inputBagOfFeatures
	 * @param String[] inputWordModel
	 */
	public AnalysisTFIDF(ArrayList <Vector<Feature>> inputBagOfFeatures, String[] inputWordModel){
		bagOfFeatures= new ArrayList<Vector<Feature>>();
		bagOfFeatures=(ArrayList<Vector<Feature>>) inputBagOfFeatures.clone();
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
	
	private double[] setD(){
		double[] D=new double[wordModel.length];
		double[] tempSum=new double[wordModel.length];
		for(int i=0;i<wordModel.length;i++){
			D[i]=0;
			tempSum[i]=0;
		}
		for(Vector<Feature> vec : bagOfFeatures ){

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
	
	
}
