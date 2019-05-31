
import java.util.ArrayList;

import at.unisalzburg.dbresearch.apted.costmodel.StringUnitCostModel;
import at.unisalzburg.dbresearch.apted.distance.APTED;
import at.unisalzburg.dbresearch.apted.node.Node;
import at.unisalzburg.dbresearch.apted.node.StringNodeData;
import at.unisalzburg.dbresearch.apted.parser.BracketStringInputParser;
import dataProcess.DotFileProcessTree;

public class Main6 {

	public Main6() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {
		
		// Loading the testing results
		DotFileProcessTree testingResults=new DotFileProcessTree("results_from_gspan/testingResults.dot");
		testingResults.printTheDotFiles();
		testingResults.dotProcessCreateTreesFromGspan();
		ArrayList<String> treeTestingBracketForm=Utilities.exportLabelTreesToBracketForm(testingResults.getTreeList());
		
		// Loading the training results
		DotFileProcessTree trainingResults=new DotFileProcessTree("results_from_gspan/trainingResults.dot");
		trainingResults.printTheDotFiles();
		trainingResults.dotProcessCreateTreesFromGspan();
		ArrayList<String> treeTrainingBracketForm=Utilities.exportLabelTreesToBracketForm(trainingResults.getTreeList());
		
		float resultsDistance[][]= new float[treeTestingBracketForm.size()][treeTrainingBracketForm.size()];
		
		BracketStringInputParser parser = new BracketStringInputParser();
		APTED<StringUnitCostModel, StringNodeData> apted = new APTED<>(new StringUnitCostModel());
		
		for(int i = 0 ; i < treeTestingBracketForm.size() ; i++){
			Node<StringNodeData> testingTreeAPTED = parser.fromString(treeTestingBracketForm.get(i));
			
			for(int j =0 ; j < treeTrainingBracketForm.size() ; j++){
				Node<StringNodeData> trainingTreeAPTED = parser.fromString(treeTrainingBracketForm.get(j));
				resultsDistance[i][j]=apted.computeEditDistance(testingTreeAPTED, trainingTreeAPTED);
				
			}
		}
		
		Utilities.print2dArray(resultsDistance, treeTestingBracketForm.size(), treeTrainingBracketForm.size());
		
		
	}
}
