import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Stack;
import java.util.Vector;

import net.sf.javaml.clustering.evaluation.AICScore;
import net.sf.javaml.clustering.evaluation.ClusterEvaluation;
import net.sf.javaml.distance.CosineSimilarity;
import net.sf.javaml.distance.DistanceMeasure;
import utilities.Utilities;
import net.sf.javaml.core.DenseInstance;
import net.sf.javaml.core.Instance;

public class MatchBetweenTwoClusterSets {

	private HashMap<Integer,Vector<String>> hashMap1;
	private HashMap<Integer,Vector<String>> hashMap2;
	HashMap<Integer,Double[]> hashMapDouble1;
	HashMap<Integer,Double[]> hashMapDouble2;
	private HashMap<String,Integer> hashMapLabelCluster1;
	private HashMap<String,Integer> hashMapLabelCluster2;
	private String[] spaceTerm; 
	
	public MatchBetweenTwoClusterSets(){};
	
	public MatchBetweenTwoClusterSets(HashMap<Integer,Vector<String>> inputhashMap1,HashMap<Integer,Vector<String>> inputhashMap2) {
		hashMap1=inputhashMap1;
		hashMap2=inputhashMap2;
	}
	
	public MatchBetweenTwoClusterSets(HashMap<Integer,Vector<String>> inputhashMap1,HashMap<String,Integer> inputHashMapLabelCluster1,HashMap<Integer,Vector<String>> inputhashMap2,HashMap<String,Integer> inputHashMapLabelCluster2) {
		hashMap1=inputhashMap1;
		hashMap2=inputhashMap2;
		hashMapLabelCluster1=inputHashMapLabelCluster1;
		hashMapLabelCluster2=inputHashMapLabelCluster2;
	}
	
	// TODO Not proper way of init 
	public void MatchBetweenTwoClusterSets2(HashMap<Integer,Double[]> inputhashMap1,
											HashMap<String,Integer> inputHashMapLabelCluster1,
											HashMap<Integer,Double[]> inputhashMap2,
											HashMap<String,Integer> inputHashMapLabelCluster2) {
		hashMapDouble1=inputhashMap1;
		hashMapDouble2=inputhashMap2;
		hashMapLabelCluster1=inputHashMapLabelCluster1;
		hashMapLabelCluster2=inputHashMapLabelCluster2;
	}
	
	
	public void createTheSpaceTermVector(){
		Iterator<Vector<String>> iter =hashMap1.values().iterator();
		Vector<String> tempv;
		HashSet<String> uniqueTerms=new HashSet<String>();
		while(iter.hasNext()){
			tempv=iter.next();
			uniqueTerms.addAll(tempv);
		}

		iter =hashMap2.values().iterator();
		while(iter.hasNext()){
			tempv=iter.next();
			uniqueTerms.addAll(tempv);
		}
		
		spaceTerm = new String[uniqueTerms.size()];
		uniqueTerms.toArray(spaceTerm);
	}
	
	public void transformMapClustersToLabels(){
		
		HashMap<Integer,double[]> hashMapVector1=transformClusterStringToDoubleArray(hashMap1);
		HashMap<Integer,double[]> hashMapVector2=transformClusterStringToDoubleArray(hashMap2);
		// Choose the big array as first
		
		if(hashMapVector1.size()>=hashMapVector2.size()){
			double[][] arraySim = calculateSimilarityArray(hashMapVector1,hashMapVector2);
			
			HashMap<Integer,Integer> matchArray=wrapperMaxMatcher(arraySim, hashMapVector1.size(), hashMapVector2.size());
			transformAnMapLabelToCluster(matchArray, hashMapLabelCluster1);
			
		}
		else{
			double[][] arraySim = calculateSimilarityArray(hashMapVector2,hashMapVector1);
			HashMap<Integer,Integer> matchArray=wrapperMaxMatcher(arraySim, hashMapVector2.size(), hashMapVector1.size());
			transformAnMapLabelToCluster(matchArray, hashMapLabelCluster2);
		}
				
	}

	/**
	 * The flag indicates which array is bigger so it can pass it outside of the method 
	 * @param flag
	 * @return
	 */
public HashMap<Integer,Integer> transformMapClustersToLabels2(boolean flag){
		

		// Choose the big array as first
		HashMap<Integer,Integer> matchArray;
		if(hashMapDouble1.size()>=hashMapDouble2.size()){
			double[][] arraySim = calculateSimilarityArray2(hashMapDouble1,hashMapDouble2);
			flag=true;
			matchArray=wrapperMaxMatcher(arraySim, hashMapDouble1.size(), hashMapDouble2.size());
		}
		else{
			double[][] arraySim = calculateSimilarityArray2(hashMapDouble2,hashMapDouble1);
			matchArray=wrapperMaxMatcher(arraySim, hashMapDouble2.size(), hashMapDouble1.size());
			flag=false;
		}
		return matchArray;
				
	}
	
	private HashMap<Integer,double[]> transformClusterStringToDoubleArray(HashMap<Integer,Vector<String>> hashMapClusterVector){
		HashMap<Integer,double[]> hashMapVector=new HashMap<Integer,double[]>();
		Vector<String> tempv;
		double [] temp;
		
		for(int key : hashMapClusterVector.keySet() ){
			
			temp=new double[spaceTerm.length];
			for(int i=0 ; i < spaceTerm.length ; i++){
				temp[i]=0;
				for(String tempString : hashMapClusterVector.get(key)){
					if(tempString.equals(spaceTerm[i]))
						temp[i]=1;
				}
			}
			hashMapVector.put(Integer.valueOf(key),temp);
		}
		return hashMapVector;
	}
	
	private double[][] calculateSimilarityArray(HashMap<Integer,double[]> BiggerHashMap, HashMap<Integer,double[]> SmallerHashMap){
		if(BiggerHashMap.size() < SmallerHashMap.size())
			throw new IllegalArgumentException("Bigger Array has smaller Size than Smaller array");
		
		double array[][]=new double[BiggerHashMap.size()][SmallerHashMap.size()];
		DistanceMeasure dm=new CosineSimilarity();
		
		for(int i : BiggerHashMap.keySet()){
			Instance tempInstanceLine=new DenseInstance(BiggerHashMap.get(i));
			for(int j : SmallerHashMap.keySet()){
				Instance tempInstanceRow=new DenseInstance(SmallerHashMap.get(j));
				array[i-1][j-1]=dm.measure(tempInstanceLine, tempInstanceRow);
			}
		}
		
		Utilities.print2dArray(array, BiggerHashMap.keySet().size(), SmallerHashMap.keySet().size());
		

		return array;
	}
	
	private double[][] calculateSimilarityArray2(HashMap<Integer,Double[]> BiggerHashMap, HashMap<Integer,Double[]> SmallerHashMap){
		if(BiggerHashMap.size() < SmallerHashMap.size())
			throw new IllegalArgumentException("Bigger Array has smaller Size than Smaller array");
		
		double array[][]=new double[BiggerHashMap.size()][SmallerHashMap.size()];
		DistanceMeasure dm=new CosineSimilarity();
		
	
		
		
		double[] tempArray1 = new double[BiggerHashMap.values().iterator().next().length];
		double[] tempArray2 = new double[SmallerHashMap.values().iterator().next().length];
		
		Integer[] indexBig=BiggerHashMap.keySet().toArray(new Integer[1]);
		Integer[] indexSmall=BiggerHashMap.keySet().toArray(new Integer[1]);
		
		for(int i=0 ; i < indexBig.length ; i++){
			unwrapDouble( BiggerHashMap.get(indexBig[i]) , tempArray1 );
			Instance tempInstanceLine=new DenseInstance( tempArray1 );
			for(int j=0 ; j < indexSmall.length ; j++ ){
				unwrapDouble( SmallerHashMap.get(indexSmall[j]) , tempArray2 );
				Instance tempInstanceRow=new DenseInstance( tempArray2 );
				array[i][j]=dm.measure( tempInstanceLine , tempInstanceRow );
			}
		}
		
//		Utilities.print2dArray(array, BiggerHashMap.keySet().size(), SmallerHashMap.keySet().size());
		

		return array;
	}
	
	private void unwrapDouble(Double[] arrayToUnwrap, double[] arrayToSet){
		for( int i=0 ; i < arrayToUnwrap.length ; i ++ )
			arrayToSet[i]= arrayToUnwrap[i];
	}
	
	private HashMap<Integer,Integer> wrapperMaxMatcher(double[][] array, int bigSize , int smallSize){
		
		HashMap<Integer,Integer> matchMap= new HashMap<Integer,Integer>();
		double[][] tempArray=copyDoubleArray(array,bigSize,smallSize);
		matchMap=SimpleMaxMatcher(tempArray,bigSize ,smallSize);
		while(matchMap.size()<bigSize){
			tempArray=copyDoubleArray(array,bigSize,smallSize);
			for(int i : matchMap.keySet()){
				tempArray=nulifylineOfArray(tempArray,i-1,smallSize);
			}
			
			HashMap<Integer,Integer> tempMap= SimpleMaxMatcher(tempArray,bigSize ,smallSize);
			for(int i :tempMap.keySet()){
				matchMap.put(i, tempMap.get(i));
			}
			
		}
		
		
		return matchMap;
	}
	
	private HashMap<Integer,Integer> SimpleMaxMatcher(double[][] array, int bigSize , int smallSize){
		HashMap<Integer,Integer> matchMap= new HashMap<Integer,Integer>();
		
		for(int i = 0 ; i < bigSize ; i ++){
			
			
			double max=0;
			int indexLine=-1;
			int indexRow=-1;
			for(int j = 0 ; j < smallSize ; j++){
				if( max <array[i][j] ){
					indexLine=i+1;
					indexRow=j+1;
					max=array[i][j];
				}	
			}
			if( !(indexLine==-1)){
				matchMap.put(indexLine, indexRow);
				for(int j = 0 ; j < bigSize ; j++){
					array[j][indexRow-1]=0;
				}
			}
		}
		
		
		return matchMap;
	}
	
	
	
	
	
	
	
	
	/**
	 * Discrete optimization solver not optimized faulty
	 * @param array
	 * @param bigSize
	 * @param smallSize
	 */
	private void discreteOptimizationProblem(double[][] array, int bigSize, int smallSize){
		// Modeling the problem as tasks (smaller hashmap) and workers (bigger hashmap)
		// conditions a task should have a minimum of one worker, a worker can have one task
		// objective function maximize the similarity 
		
		// creating pool of workers tasks
		Stack<Integer[]> indexes= new Stack<Integer[]>();
		Integer[] tempIndex = new Integer[2];
		
		//Creating Space 
		for(int i=0 ; i < bigSize ; i++){
			for( int j=0 ; j < smallSize ; j++){
				tempIndex[0]=i;
				tempIndex[1]=j;
				indexes.add(tempIndex);
			}
		}
		
		HashMap<Vector<Integer[]>,Double> mapOfAnswer = new HashMap<Vector<Integer[]>,Double>();
		recursion(indexes, 0, new Vector<Integer[]>(),0, array, mapOfAnswer);
		
	}
	
	private HashMap<Vector<Integer[]>,Double> recursion(Stack<Integer[]> space, int flag, Vector<Integer[]> answer, double match, double[][] arraySim, HashMap<Vector<Integer[]>,Double> mapOfAnswer){
		
		if(!space.isEmpty()){
			Integer[] tempIndex=new Integer[3];
			Integer[] tempIndex2=space.pop();
			tempIndex[0]=tempIndex2[0];
			tempIndex[1]=tempIndex2[1];
			tempIndex[2]=flag;
			match=match+tempIndex[2]*arraySim[tempIndex[0]][tempIndex[1]];
			answer.add(tempIndex);
			recursion(space , 0 , answer,match,arraySim,mapOfAnswer);
			recursion(space , 1 , answer,match,arraySim,mapOfAnswer);
		}else{
			mapOfAnswer.put(answer, match);
			return mapOfAnswer;
		}
		return mapOfAnswer;
		
	}
	
	private double[][] copyDoubleArray(double[][] array,int line,int row){
		double[][] temp=new double[line][row];
		
		for(int i=0 ; i < line ; i++){
			for( int j=0 ; j < row ; j++){
				temp[i][j]=array[i][j];
			}
		}
		return temp;
	}
	private double[][] nulifylineOfArray(double[][] array,int line,int row){
		for( int j=0 ; j < row ; j++){
			array[line][j]=0;
		}
		return array;
	}
	
	private void transformAnMapLabelToCluster(HashMap<Integer,Integer> transformationMap, HashMap<String,Integer> mapToBeTransformed){
		
		for(String temps:mapToBeTransformed.keySet()){
			mapToBeTransformed.put(temps, transformationMap.get( mapToBeTransformed.get(temps)));
		}
	}

}
