import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

import org.jfree.data.xy.XYSeries;

import dataProcess.DotFileProcessTree;
import dataProcess.TreeProcess;
import nodes.Node;
import plots.PlotBarChart;
import plots.PlotDepthPerNumberOfClusters;
import utilities.Utilities;

public class CreateGraphStatistics {

	enum TestingParam{
		DATASETPATH("-datasetPath"),
		RESULTSPATH("-resultPath"),
		GSPANRESULTSFOLDER("-gspanResultFolder"),
		KMEANSCE("-kmeansCE"),
		GSPANSUPPORT("-gspanSupport");
		// internal state
		private String argCode;
		
	    // constructor
	    private TestingParam(final String code) {
	        this.argCode = code;
	    }
	    // get
	    public String getArgCode() {
	        return argCode;
	    }
	}
	
	
	public static void main(String[] args) throws IOException {
	
		// Importing testing arguments
		HashMap<String,String> testCaseArgs=Utilities.readTestingLog(args);
		// testing of imported arguments based on enumerated types
    	for(TestingParam parameter :TestingParam.values()){
			if(!testCaseArgs.containsKey(parameter.getArgCode())){
				throw new IllegalArgumentException("This testing parameter is not initialized in the testing Log file: "+parameter.getArgCode());
			}else{
				System.out.println(" Parameter "+parameter.getArgCode()+" initialized: "+ testCaseArgs.get(parameter.getArgCode()));
			}
		}
    	
    	
    	PlotBarChart tempBarChart = new PlotBarChart( testCaseArgs.get( TestingParam.RESULTSPATH.getArgCode() ) + "image"+ testCaseArgs.get( TestingParam.KMEANSCE.getArgCode() ) + ".jpeg" , 6 );
    	PlotDepthPerNumberOfClusters tempDepthBarChart=new PlotDepthPerNumberOfClusters( testCaseArgs.get( TestingParam.RESULTSPATH.getArgCode() ) + "imageDepth" + testCaseArgs.get( TestingParam.KMEANSCE.getArgCode() ) + ".jpeg");
    	TreeProcess tempProcess=new TreeProcess();
		
		File folder=new File(testCaseArgs.get(TestingParam.GSPANRESULTSFOLDER.getArgCode()));
		List<File> dotFileNames= Arrays.asList(folder.listFiles());
		
		
		for(File file:dotFileNames){
			
			Scanner tempScan = new Scanner(file);
			String clusterNum = "Not recognized in file";
			
			
			while ( tempScan.hasNextLine() ){
				clusterNum = tempScan.nextLine();
				if( clusterNum.contains( "-clusterNum " ) )
				break;
			}
			tempScan.close();
			
			
			
			DotFileProcessTree GameDevDomain=new DotFileProcessTree(file.getAbsolutePath());
			GameDevDomain.printTheDotFiles();
			GameDevDomain.dotProcessCreateTreesFromGspan();
			Iterator<Node<String>> tempIter = GameDevDomain.getTreeList().iterator();
			while(tempIter.hasNext()){
				if(tempIter.next().isLeaf())
					tempIter.remove();
			}
			
			
			double[] depthAsPercent = new double[GameDevDomain.getTreeList().size()]; 
			int[] depthRawCount= new int[GameDevDomain.getTreeList().size()];
			
			int[] rawcountofTrees= new int[GameDevDomain.getTreeList().size()];
			
			for(int i = 0 ; i < GameDevDomain.getTreeList().size() ; i++){
				depthAsPercent[i] = (double) GameDevDomain.getTreeList().get(i).getMaxDepth(0, 0)/((double) GameDevDomain.getTreeList().get(i).getMaxBreadth(0));
				depthRawCount[i] = GameDevDomain.getTreeList().get(i).getMaxDepth(0, 0);
				rawcountofTrees[i] = GameDevDomain.getTreeList().get(i).getMaxDepth(1, 0);
			}
			tempBarChart.addToDataSet(depthAsPercent, clusterNum );
			tempDepthBarChart.addToDataSetRenewingCategories2( depthRawCount, clusterNum );
			
			tempProcess.addTrees(GameDevDomain.getTreeList());
		
		}
		
		tempBarChart.SaveBarChartAsJpeg( "Clusters evaluated with: " + testCaseArgs.get( TestingParam.KMEANSCE.getArgCode() ) ,"Depth / Number Of Nodes", "Raw Count of Trees");
		tempDepthBarChart.SaveBarChartAsJpeg( "Clusters evaluated with: " + testCaseArgs.get( TestingParam.KMEANSCE.getArgCode() ) ,
												"Maximum Depth with Gspan Support =" + testCaseArgs.get(TestingParam.GSPANSUPPORT.argCode) + "%" , 
												"Raw Count of Trees");
	}

}
