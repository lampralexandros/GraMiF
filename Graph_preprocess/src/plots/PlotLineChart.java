package plots;

import java.util.HashMap;

import org.jfree.data.xy.XYSeries;

/**
 * This class represent all the basic utilities to construct a Line plot
 * @author Alex
 *
 */
public class PlotLineChart {

	/**
	 * Void constructor
	 * @author Alexandros Lampridis
	 */
	public PlotLineChart() {}
	
	/**
	 * This method calculates the 
	 * @author Alexandros Lampridis
	 * @param measurments A matrix with double values.  
	 * @param lineName	The name of XYSeries
	 * @return
	 */
	public static XYSeries countFrequency( double[] measurments , String lineName){
		
		XYSeries tempLine = new XYSeries( lineName );
		HashMap< Double , Integer > tempHashMap = new HashMap< Double , Integer >();
		
		for( int i = 0 ; i < measurments.length ; i ++ ){
			
			if( tempHashMap.containsKey( measurments[i] ) ){
				tempHashMap.put( measurments[i] , tempHashMap.get( measurments[i] ) +1 );
			} else {
				tempHashMap.put( measurments[i] , 1 );
			}
		}
		
		for(Double key : tempHashMap.keySet() ){
			tempLine.add(  key.doubleValue() , tempHashMap.get( key ).doubleValue());
		}
		
		return tempLine;
		
	}
	

}
