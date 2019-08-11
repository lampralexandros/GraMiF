package resultsCreation;

import java.util.ArrayList;
import java.util.HashMap;

import nodes.Node;

/**
 * This class creates the end result of the analysis. Which is a tree representing a conception of the use case scenarios.
 * @author Alexandros Lampridis
 *
 */
public class UseCaseTreeProcess {
	
	private ArrayList< Node< String > > treeList;
	private HashMap< String , Double[] > mapMethodClusterTFIDF;
	private HashMap< String , Double[] > mapClassClusterTFIDF;
	private String[] worldVector;
	
	public UseCaseTreeProcess( HashMap< String , Double[] > inputMapMethodClusterTFIDF,
							   HashMap< String , Double[] > inputMapClassClusterTFIDF,
							   ArrayList< Node< String > > inputTreeList, 
							   String[] inputWorldVector ) { 
		
		mapMethodClusterTFIDF = inputMapMethodClusterTFIDF;
		mapClassClusterTFIDF = inputMapClassClusterTFIDF;
		treeList = inputTreeList;
		worldVector = inputWorldVector;
	}
	
	

}
