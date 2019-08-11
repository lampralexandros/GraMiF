package plots;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

public class PlotBarChart {
	
	String fileName;
	ArrayList<String> categoriesList;
	final DefaultCategoryDataset dataset;
	
	/**
	 * Simple constructor
	 * @param inputFileName
	 */
	public PlotBarChart(String inputFileName) {
		categoriesList = new ArrayList<String>();
		fileName = inputFileName;
		dataset = new DefaultCategoryDataset();
	}
	
	public PlotBarChart(String inputFileName , int numberOfCategories) {
		categoriesList = new ArrayList<String>();
		fileName = inputFileName;
		dataset = new DefaultCategoryDataset();
		setRangesOfOneToCategories( numberOfCategories);
	}
	
	public void setRangesOfOneToCategories(int numberOfCategories){
		categoriesList.clear();
		for(double i=0 ; i < numberOfCategories-1 ; i++){
			
			categoriesList.add( "[ "+ String.format("%2.2f",(i/numberOfCategories)) + " | " + String.format("%2.2f",((i+1)/numberOfCategories))  + " )" );

			//categoriesList.add( "[ "+ String.valueOf((i/numberOfCategories))  + " | " + String.valueOf(((i+1)/numberOfCategories))  + " )" );
		}
		categoriesList.add( "[ "+ String.format("%2.2f",1.0-1/((double) numberOfCategories)) + " | " + " 1]" );
	}
	
	public void addToDataSet( double[] values , String ValueName){
		
		int valueCount[] = new int[categoriesList.size()];
		double categoryStep = 1.0/categoriesList.size();
		for(int i=0 ; i < values.length ; i++){
			
			for( int j=0 ; j < valueCount.length-1 ; j++ ){
				
				valueCount[j]+= ( j*categoryStep <= values[i] && values[i]< (j+1)*categoryStep) ? 1 : 0 ;
				
			}
			
			valueCount[valueCount.length-1]+= ( (valueCount.length-1)*categoryStep <= values[i] && values[i]<= 1) ? 1 : 0 ;
		}
		
		for(int i=0 ; i < categoriesList.size() ; i++){
			dataset.addValue(valueCount[i], ValueName , categoriesList.get(i));
		}
		
			
	}
	
	public void SaveBarChartAsJpeg( String plotTitle , String categoriestTitle , String yAxis ) throws IOException{
		 JFreeChart barChart = ChartFactory.createBarChart(
				 plotTitle, 
				 categoriestTitle, yAxis , 
		         dataset,PlotOrientation.VERTICAL, 
		         true, true, false);
		         
		      int width = 640;    /* Width of the image */
		      int height = 480;   /* Height of the image */ 
		      File BarChart = new File( fileName ); 
		      ChartUtilities.saveChartAsJPEG( BarChart , barChart , width , height );
	}
	

}
