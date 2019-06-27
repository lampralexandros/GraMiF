
import java.io.File;
import java.util.ArrayList;

import at.unisalzburg.dbresearch.apted.costmodel.StringUnitCostModel;
import at.unisalzburg.dbresearch.apted.distance.APTED;
import at.unisalzburg.dbresearch.apted.node.Node;
import at.unisalzburg.dbresearch.apted.node.StringNodeData;
import at.unisalzburg.dbresearch.apted.parser.BracketStringInputParser;
import dataProcess.DotFileProcessTree;
import utilities.Utilities;


public class TestLoadingResultsFromGspan {

	public static void main(String[] args) {
		File folder=new File("results_from_gspan");
		DotFileProcessTree gspanResults=new DotFileProcessTree(folder);
		gspanResults.printTheDotFiles();
		gspanResults.dotProcessCreateTreesFromGspan();
		
//		String temp1=Utilities.exportALabelTreeIntoBracketForm(gspanResults.getTreeList().get(0));
//		String temp2=Utilities.exportALabelTreeIntoBracketForm(gspanResults.getTreeList().get(1));
//		System.out.println(temp1+" "+temp2);
		ArrayList<String> treeBracketForm=Utilities.exportLabelTreesToBracketForm(gspanResults.getTreeList());
		
		// Parse the input and transform to Node objects storing node information in MyNodeData.
		BracketStringInputParser parser = new BracketStringInputParser();
		Node<StringNodeData> t1 = parser.fromString(treeBracketForm.get(0));
		Node<StringNodeData> t2 = parser.fromString(treeBracketForm.get(1));
		// Initialise APTED.
		APTED<StringUnitCostModel, StringNodeData> apted = new APTED<>(new StringUnitCostModel());
		// Execute APTED.
		float result = apted.computeEditDistance(t1, t2);
		System.out.println("Results="+result);
		
	}

}
