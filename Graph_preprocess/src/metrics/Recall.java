package metrics;

import java.util.ArrayList;

import be.abeel.util.Pair;

public class Recall {

	public Recall() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * 
	 * In this implementation the relevant cases are the test case's results, thus they are represented as the first dimension <br>
	 * of the distance array
	 * @param distanceMatrix 
	 * @param similarityMatrix
	 * @return An arrayList with Pairs of ( recall score , similarity 
	 */
	public ArrayList< Pair < Double , Double > > calculateRecall( double[][] distanceArray , double[] similarityArray ){
		
		double counter = 0.0 ;
		boolean flag;
		ArrayList< Pair< Double, Double>> precisionArray = new ArrayList< Pair< Double , Double > >();
		Pair < Double , Double > precision;
		
		
		for( int k = 0 ; k < similarityArray.length ; k++ ){
			
			counter = 0 ;
			
			for( int i = 0 ; i < distanceArray.length ; i++ ){
				
				
				
				flag = false;
			
				for ( int j = 0 ; j < distanceArray[i].length ; j++ ){
					flag = flag || ( distanceArray[i][j] >= similarityArray[k] ) ? true : false ;
				}
				
				counter += flag ? 1.0 : 0.0 ;
			}
			
			precision = new Pair< Double , Double >( Double.valueOf( counter / distanceArray.length ) , Double.valueOf( similarityArray[k] ) );
			
			precisionArray.add( precision );
		}
		
		
		return precisionArray ;
		
	}

}
