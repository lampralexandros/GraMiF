package plots;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

public class PlotDepthPerNumberOfClusters {
	
	String fileName;
	ArrayList<String> categoriesList;
	final DefaultCategoryDataset dataset;
	
	/**
	 * Simple constructor
	 * @param inputFileName
	 */
	public PlotDepthPerNumberOfClusters(String inputFileName) {
		categoriesList = new ArrayList<String>();
		fileName = inputFileName;
		dataset = new DefaultCategoryDataset();
	}
	
	public void addToDataSetRenewingCategories( int[] values , String ValueName){
		
		int max=0;
		HashMap<Integer,Integer> rawCount = new HashMap<Integer,Integer>();
		
		for( int i = 0 ; i < values.length ; i++ ){
			max = ( max < values[i] ) ? values[i] : max ;
			rawCount.put( values[i] , rawCount.containsKey(values[i]) ? rawCount.get(values[i])+1 : 1 );
		}
		
		if( categoriesList.isEmpty() ){
			for( int i = 1 ; i <= max ; i++ )
				categoriesList.add( "Number = " + i);
		}else{
			if( categoriesList.size() < max){
				categoriesList.clear();
				for( int i = 1 ; i <= max ; i++ )
					categoriesList.add( "Number = " + i);	
			}
		}
		
		for( Integer i : rawCount.keySet())
			dataset.addValue(rawCount.get(i), ValueName , categoriesList.get(i-1));
		
		
			
	}
	
	
public void addToDataSetRenewingCategories2( int[] values , String ValueName){
		
		String clustNum= ValueName;
		
		int max=0;
		HashMap<Integer,Integer> rawCount = new HashMap<Integer,Integer>();
		
		for( int i = 0 ; i < values.length ; i++ ){
			max = ( max < values[i] ) ? values[i] : max ;
			rawCount.put( values[i] , rawCount.containsKey(values[i]) ? rawCount.get(values[i])+1 : 1 );
		}
		
		if( categoriesList.isEmpty() ){
			for( int i = 1 ; i <= max ; i++ )
				categoriesList.add( "Number = " + i);
		}else{
			if( categoriesList.size() < max){
				categoriesList.clear();
				for( int i = 1 ; i <= max ; i++ )
					categoriesList.add( "Number = " + i);	
			}
		}
		
		for( Integer i : rawCount.keySet())
			dataset.addValue(rawCount.get(i), "Depth = " + i ,  clustNum);
		
		
			
	}
	
	
	
	public void SaveBarChartAsJpeg( String plotTitle , String categoriestTitle , String yAxis ) throws IOException{
		 JFreeChart barChart = ChartFactory.createBarChart(
				 plotTitle, 
				 categoriestTitle, yAxis , 
		         dataset,PlotOrientation.VERTICAL, 
		         true, true, false);
		         
		      int width = 2200;    /* Width of the image */
		      int height = 900;   /* Height of the image */ 
		      File BarChart = new File( fileName ); 
		      ChartUtilities.saveChartAsJPEG( BarChart , barChart , width , height );
	}
	

}
