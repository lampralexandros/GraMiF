package matcherBetweenSets;

import java.util.ArrayList;

import be.abeel.util.Triplet;
import net.sf.javaml.distance.DistanceMeasure;

public class MatchingProcess {

	DistanceMeasure dMeasure;
	double[][] distanceArray;
	
	/**
	 * Null constructor
	 * @author Alexandros Lampridis
	 */
	public MatchingProcess(){
		
	}
	
	// Set Get methods
	
	public void setDistanceMatrix( double[][] inputDistanceArray ){
		distanceArray = inputDistanceArray;
	}
	
	// Main methods
	/**
	 * Simple max matcher.
	 * @return Arraylist with Triplets of indices from the Distance Array and its as a pair and its score
	 */
	public ArrayList< Triplet< Integer , Integer , Double > > matchBetweenTrees(){
		
		double max ;
		double[][] tempArray = new double[distanceArray.length][distanceArray[0].length];
		Integer index ;
		ArrayList< Triplet< Integer , Integer , Double > > tempList = new ArrayList< Triplet< Integer , Integer , Double > >() ;
		
		copyDoubleArray( tempArray , distanceArray );
		
		for( int i = 0 ; i < tempArray.length ; i++ ){
			
			max = 0 ;
			index = null ;
			
			for( int j = 0 ; j < tempArray[0].length ; j++ ){
				
				if( tempArray[i][j] > max ){
					index = Integer.valueOf( j ) ;
					max = tempArray[i][j] ;
				}
			}
				
			if( index != null ){
			
				tempList.add( new Triplet<Integer, Integer, Double>( Integer.valueOf(i) , index , Double.valueOf( max ) ) );
				nullifyColumn( tempArray , index.intValue() ) ; 
			}
		}
		
		
		return tempList;
		
	}
	
	
	/**
	 * Simple method to nullify a column of an array.
	 * @param array
	 * @param columnIndex
	 */
	private void nullifyColumn( double[][] array , int columnIndex){
		
		for( int i = 0 ; i < array.length ; i++){
			array[i][columnIndex] = 0 ;
		}
	}
	/**
	 * Simple method to copy an array with two dimensions.
	 * @param copiedArray
	 * @param array
	 */
	private void copyDoubleArray( double[][] copiedArray , double[][] array ){
		
		for( int i = 0 ; i < array.length ; i++ ){
			
			for( int j = 0 ; j < array[0].length ; j++ ){
				
				copiedArray[i][j] = array[i][j] ;
			}
		}
	}
	
}


