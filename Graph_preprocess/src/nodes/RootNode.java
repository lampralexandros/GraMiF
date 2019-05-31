package nodes;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class RootNode<String> extends Node<String> implements Serializable {

	protected String graphName, graphStatistics;
	
	public RootNode(String inputData, String inputGraphName,String inputGraphStatistics){
		super(inputData,null);
		graphName=inputGraphName;
		graphStatistics=inputGraphStatistics;
	}
	
	// set get
	public void setGraphName(String inputGraphName ){
		graphName=inputGraphName;
	}
	
	public void setGraphStatistics(String inputGraphStatistics ){
		graphStatistics=inputGraphStatistics;
	}
	
	public String getGraphName(){
		return graphName;
	}
	
	public String getGraphStatistics(){
		return graphStatistics;
	}
}
