package metrics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import be.abeel.util.Pair;
import be.abeel.util.Triplet;

public class Precision {
	
	HashMap< Double , Integer > frequencyMap ;
	Comparator< Pair < Double , Integer > > SortByDouble ;
	ArrayList < Pair < Double , Integer > > concentratingArray ;
	int totalPositives , priorPositives , actualPositives , predictedPositives ;
	
	
	
	/**
	 * Void constructor. Initializing a HashMap and a comparator
	 */
	public Precision() {
		frequencyMap = new HashMap < Double , Integer >() ;
		concentratingArray = new ArrayList < Pair < Double , Integer > >() ;
		SortByDouble = new Comparator< Pair< Double , Integer > >(){
			@Override public int compare( Pair< Double , Integer > p1 , Pair< Double , Integer > p2 ){
				return Double.compare( p1.x() , p2.x() );} 
			};
	}
	
	// Get Set
	
	public void setActualPositives( int inputNumber ){
		actualPositives = inputNumber ; 
	}
	
	public void setPredictedPositives( int inputNumber ){
		predictedPositives = inputNumber ;
	}
	// Main methods
	
	public void importListOfTriplets( ArrayList < Triplet < Integer , Integer , Double > > inputListOfTriplets ){
		
		frequencyMap.clear();
		
		for( Triplet< Integer , Integer , Double > tempResult : inputListOfTriplets ){
			
			if( frequencyMap.containsKey( tempResult.z() ) ){
				frequencyMap.put( tempResult.z() , frequencyMap.get( tempResult.z() ) + 1 ) ; 
			}else{
				frequencyMap.put( tempResult.z() , 1 ) ;
			}
		}
		
	}
	
	public ArrayList< Pair < Double , Double > > calculatePrecisionPerSimilarity() {
		
		// Initializing accumulators 
		totalPositives = 0 ;
		priorPositives = 0 ;
		concentratingArray.clear() ;
		ArrayList< Pair < Double , Double > > tempResults = new ArrayList< Pair< Double , Double > >() ;
		
		for ( Double tempKey : frequencyMap.keySet() ){
			priorPositives = frequencyMap.get( tempKey ) ;
			concentratingArray.add( new Pair< Double , Integer >( tempKey , priorPositives ) ) ;
			totalPositives += priorPositives ;
		}
		
		Collections.sort( concentratingArray , SortByDouble ) ;
		
		priorPositives = actualPositives - totalPositives ;
		for( Pair< Double ,Integer > tempPair : concentratingArray ){
			tempResults.add( new Pair< Double , Double >( tempPair.x() , Double.valueOf( 1 - ( (double) priorPositives ) / ( (double) actualPositives ) ) ) );
			priorPositives += tempPair.y().intValue() ;
		}
		
		return tempResults;
	}
	
	public ArrayList< Pair < Double , Double > > calculateRecallPerSimilarity() {
		
		// Initializing accumulators 
		totalPositives = 0 ;
		priorPositives = 0 ;
		concentratingArray.clear() ;
		ArrayList< Pair < Double , Double > > tempResults = new ArrayList< Pair< Double , Double > >() ;
		
		for ( Double tempKey : frequencyMap.keySet() ){
			priorPositives = frequencyMap.get( tempKey ) ;
			concentratingArray.add( new Pair< Double , Integer >( tempKey , priorPositives ) ) ;
			totalPositives += priorPositives ;
		}
		
		Collections.sort( concentratingArray , SortByDouble ) ;
		
		priorPositives = actualPositives - totalPositives ;
		for( Pair< Double ,Integer > tempPair : concentratingArray ){
			tempResults.add( new Pair< Double , Double >( tempPair.x() , Double.valueOf( 1 - ( (double) priorPositives ) / ( (double) predictedPositives ) ) ) );
			priorPositives += tempPair.y().intValue() ;
		}
		
		return tempResults;
	}
	/**
	 * 
	 * @return ArrayList with pairs of Precision - Recall for each similarity measure.
	 */
	public ArrayList< Pair < Double , Double > > calculatePrecisionRecallCurve() {
		
		// Initializing accumulators 
		totalPositives = 0 ;
		priorPositives = 0 ;
		concentratingArray.clear() ;
		ArrayList< Pair < Double , Double > > tempResults = new ArrayList< Pair< Double , Double > >() ;
		
		for ( Double tempKey : frequencyMap.keySet() ){
			priorPositives = frequencyMap.get( tempKey ) ;
			concentratingArray.add( new Pair< Double , Integer >( tempKey , priorPositives ) ) ;
			totalPositives += priorPositives ;
		}
		
		Collections.sort( concentratingArray , SortByDouble ) ;
		
		priorPositives = actualPositives - totalPositives ;
		for( Pair< Double ,Integer > tempPair : concentratingArray ){
			tempResults.add( new Pair< Double , Double >( Double.valueOf( 1 - ( (double) priorPositives ) / ( (double) actualPositives ) ) ,
					Double.valueOf( 1 - ( (double) priorPositives ) / ( (double) predictedPositives ) ) ) );
			priorPositives += tempPair.y().intValue() ;
		}
		
		return tempResults;
	}
	
	/**
	 * 
	 * In this implementation the retrieved cases are the training case's results, thus they are represented as the second dimension <br>
	 * of the distance array
	 * @param distanceMatrix 
	 * @param similarityMatrix
	 * @return
	 */
	public ArrayList< Pair < Double , Double > > calculatePrecision( double[][] distanceArray , double[] similarityArray ){
		
		double counter = 0.0 ;
		boolean flag;
		ArrayList< Pair< Double, Double>> precisionArray = new ArrayList< Pair< Double , Double > >();
		Pair < Double , Double > precision;
		
		
		for( int k = 0 ; k < similarityArray.length ; k++ ){
			
			counter = 0 ;
			
			for( int j = 0 ; j < distanceArray[0].length ; j++ ){
				
				
				flag = false;
			
				for ( int i = 0 ; i < distanceArray.length ; i++ ){
					flag = flag || ( distanceArray[i][j] >= similarityArray[k] ) ? true : false ;
				}
				
				counter += flag ? 1.0 : 0.0 ;
			}
			
			precision = new Pair< Double , Double >( Double.valueOf( counter / distanceArray[0].length ) , Double.valueOf( similarityArray[k] ) );
			
			precisionArray.add( precision );
		}
		
		
		return precisionArray ;
		
	}
	
	
	

}
