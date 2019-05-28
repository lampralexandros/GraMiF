
import java.io.File;

import at.unisalzburg.dbresearch.apted.costmodel.StringUnitCostModel;
import at.unisalzburg.dbresearch.apted.distance.APTED;
import at.unisalzburg.dbresearch.apted.node.Node;
import at.unisalzburg.dbresearch.apted.node.StringNodeData;
import at.unisalzburg.dbresearch.apted.parser.BracketStringInputParser;
import dataProcess.DotFileProcessTree;


public class TestLodingResultsFromGspan {

	public static void main(String[] args) {
		File folder=new File("results_from_gspan");
		DotFileProcessTree gspanResults=new DotFileProcessTree(folder);
		gspanResults.printTheDotFiles();
		gspanResults.dotProcessCreateTreesFromGspan();
		
		String temp1=Utilities.exportALabelTreeIntoBracketForm(gspanResults.getTreeList().get(0));
		String temp2=Utilities.exportALabelTreeIntoBracketForm(gspanResults.getTreeList().get(1));
		System.out.println(temp1+" "+temp2);
		
		// Parse the input and transform to Node objects storing node information in MyNodeData.
		BracketStringInputParser parser = new BracketStringInputParser();
		Node<StringNodeData> t1 = parser.fromString(temp1);
		Node<StringNodeData> t2 = parser.fromString(temp2);
		// Initialise APTED.
		APTED<StringUnitCostModel, StringNodeData> apted = new APTED<>(new StringUnitCostModel());
		// Execute APTED.
		float result = apted.computeEditDistance(t1, t2);
		System.out.println("Results="+result);
		
	}

}
