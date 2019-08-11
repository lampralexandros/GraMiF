package metrics;

import java.util.ArrayList;

import be.abeel.util.Pair;

public class AreaUnderCurver {

	public AreaUnderCurver() {
		// TODO Auto-generated constructor stub
	}
	
	
	public static double calculateAUC(ArrayList<Pair<Double, Double>> xAxis, ArrayList<Pair<Double, Double>> yAxis) throws Exception{
		
		if (xAxis.size() != yAxis.size()) 
			throw new Exception("Arrays' sizes differ");
		
		double[][] arrayX = new double[2][xAxis.size()];
		double[][] arrayY = new double[2][yAxis.size()];
		
		for (int i = 0; i < xAxis.size(); i++) {
			arrayX[0][i] = xAxis.get(i).x();
			arrayX[1][i] = xAxis.get(i).y();
			arrayY[0][i] = yAxis.get(i).x();
			arrayY[1][i] = yAxis.get(i).y();
		}
		
		return calculateAUC(arrayX, arrayY);
	}
	
	public static double calculateAUC(double[][] xAxis, double[][] yAxis) throws Exception{
		
		double auc = 0;
		
		if (xAxis.length != 2  && yAxis.length != 2)
			throw new IllegalArgumentException(" Different array dimensions");
		
		if (xAxis[1][0] == 1.0 && yAxis[1][0] == 1.0){
			for (int i = 0; i < (xAxis[0].length - 1); i++) {
				auc += calculateTrapezoidArea(xAxis[0][i], yAxis[0][i], xAxis[0][i+1], yAxis[0][i+1]);	
			} 
		} else if (xAxis[1][0] == 0.0 && yAxis[1][0] == 0.0 ) {
			for ( int i = (xAxis[0].length -1); i > 1; i--) {
				auc += calculateTrapezoidArea(xAxis[0][i], yAxis[0][i], xAxis[0][i-1], yAxis[0][i-1]);	
			} 			
		} else {
			throw new Exception(" Not fully implemented ");
		}
		
		return auc;
	}
	
	private static double calculateTrapezoidArea(double x1, double y1, double x2, double y2){

		double maxY = (y1 >= y2) ? y1 : y2;
		double minY = ( y1 < y2) ? y1 : y2;
		return minY * (x2 - x1) + (maxY - minY) * (x2 - x1) / 2.00 ;
	}
	
	
}
