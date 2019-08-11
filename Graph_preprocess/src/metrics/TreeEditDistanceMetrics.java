package metrics;

import java.util.ArrayList;

import at.unisalzburg.dbresearch.apted.costmodel.StringUnitCostModel;
import at.unisalzburg.dbresearch.apted.distance.APTED;
import at.unisalzburg.dbresearch.apted.node.StringNodeData;
import at.unisalzburg.dbresearch.apted.parser.BracketStringInputParser;

public class TreeEditDistanceMetrics {
	
	ArrayList< String > actualTrees ;
	ArrayList< String > predictedTrees ;
	double[][] resultsDistance ;

	
	/**
	 * Void constructor
	 * @author Alexandros Lampridis
	 */
	public TreeEditDistanceMetrics() {}
	
	
	// Set Get methods
	/**
	 *  Set Actual trees and predicted tree in bracket notation
	 * @param inputActualTrees
	 * @param inputPredictedTrees
	 */
	public void setActualTrees( ArrayList< String > inputActualTrees , ArrayList< String > inputPredictedTrees ){
		actualTrees = inputActualTrees ;
		predictedTrees = inputPredictedTrees ; 
	}
	
	
	public double[][] getResultsDistance(){
		return resultsDistance;
	}
	
	
	// main methods
	/**
	 * Creates the Distance Array of a set between the actual tree set and predicted set.
	 * Bounded to zero. Max similarity 1.0 minimum 0.0
	 * @author Alexandros Lampridis
	 */
	public void createDistanceArrayPercentage() {
		
		resultsDistance = new double[actualTrees.size()][predictedTrees.size()];
		BracketStringInputParser parser = new BracketStringInputParser();
		APTED< StringUnitCostModel, StringNodeData > apted = new APTED<>( new StringUnitCostModel() );
		
		for(int i = 0 ; i < actualTrees.size() ; i++){
			at.unisalzburg.dbresearch.apted.node.Node< StringNodeData > actualTreeAPTED = parser.fromString( actualTrees.get(i) );
			
			for(int j =0 ; j < predictedTrees.size() ; j++){
				
				at.unisalzburg.dbresearch.apted.node.Node< StringNodeData > predictedTreeAPTED = parser.fromString( predictedTrees.get(j) );
				resultsDistance[i][j] = 1- apted.computeEditDistance( actualTreeAPTED , predictedTreeAPTED ) / (  ( predictedTreeAPTED.getNodeCount() >= actualTreeAPTED.getNodeCount() ) ? (double) predictedTreeAPTED.getNodeCount() : (double) actualTreeAPTED.getNodeCount() );
				resultsDistance[i][j] = ( resultsDistance[i][j] < 0.0 ) ? 0.0 : resultsDistance[i][j] ;
			}
		}
	}
}
