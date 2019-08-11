package plots;

import java.util.HashMap;

public class PlotUtilities {

	
	
	
	public static double calculateTrapezoidArea( double[] point1 , double[] point2){
		
		if( point1.length != 2 || point2.length != 2 )
			throw new IllegalArgumentException(" Points size must have length of two ");

		if( point1[0] >= point2[0] )
			throw new IllegalArgumentException(" The first point x should be smaller than the second point ");
		
		double maxY = ( point1[1] >= point2[1]) ? point1[1] : point2[1];
		double minY = ( point1[1] < point2[1]) ? point1[1] : point2[1];
		double area = minY * ( point2[0] - point1[0] );
		area += ( maxY - minY ) * ( point2[0] - point1[0]) / 2.00 ; 
		
		return area;
	}
	
	public static double calculateTrapezoidArea( double x1 , Double y1 , double x2 , Double y2 ){
		double[] point1 = new double[2];
		double[] point2 = new double[2];
		point1[0]=x1;
		point1[1]=y1.doubleValue();	
		point2[0]=x2;
		point2[1]=y2.doubleValue();
		
		if( point1.length != 2 || point2.length != 2 )
			throw new IllegalArgumentException(" Points size must have length of two ");

		if( point1[0] >= point2[0] )
			throw new IllegalArgumentException(" The first point x should be smaller than the second point ");
		
		double maxY = ( point1[1] >= point2[1]) ? point1[1] : point2[1];
		double minY = ( point1[1] < point2[1]) ? point1[1] : point2[1];
		double area = minY * ( point2[0] - point1[0] );
		area += ( maxY - minY ) * ( point2[0] - point1[0]) / 2.00 ; 
		
		return area;
	}
	
	/**
	 * This method inserts the values as keys in the hashMap. If the key does not exist it does a linear interval.
	 * @param tempHash1
	 * @param values a sorted double array to be inserted as keys.
	 */
	
	public static void calculateLineIntervalOnHashMaps( HashMap< Double , Double > tempHash1 , double[] values ){
		
		int existingValueIndex;
		double gradient , intercept;
		
		for( int i = 0 ; i < values.length - 1 ; i++){
			//Checking the next value
			if( ! tempHash1.containsKey( values [ i + 1 ] ) ){
				// Did not found the following value So incrementing until it does.
				existingValueIndex = i + 2;
				while ( !tempHash1.containsKey( values[ existingValueIndex ] ) && existingValueIndex < values.length  ){
					existingValueIndex += 1;
				} 
				gradient = ( tempHash1.get( values[ existingValueIndex ] ) - tempHash1.get( values[ i ] ) ) / ( values[ existingValueIndex ] - values[ i ] ) ;
				intercept = tempHash1.get( values[ i ] );
				
				for( int j = i + 1 ; j < existingValueIndex ; j ++){
					tempHash1.put( values[ j ] , gradient*( values[ j ] - values [ i ]) + intercept );
				}
				i = existingValueIndex-1;
			}
		}
		
	}
	
	
}
