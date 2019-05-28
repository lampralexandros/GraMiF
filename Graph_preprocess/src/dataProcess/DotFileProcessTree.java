package dataProcess;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.Vector;
import java.util.regex.Pattern;

import nodes.Node;
import nodes.RootNode;
import transformations.ExtractionWithInputPattern;


// This class is basic class to extract tree structures from the input graphs

public class DotFileProcessTree {
	
	// List of the dot file names to be processed
	private List<File> dotFileNames=new ArrayList<File>();
	// ArrayList of Tree structures with raw labels
	private ArrayList<Node<String>> treeList;
	
	
	//Constructor input a folder with dot files
	public DotFileProcessTree(File domainFolder){
		//initialize dotFileNames
		dotFileNames= Arrays.asList(domainFolder.listFiles());
		treeList=new ArrayList<Node<String>>();
	}
	
	//Constructor input a folder name with dot file
	public DotFileProcessTree(String fileName){
		//initialize dotFileNames
		File file=new File(fileName);
		dotFileNames.add(file);
		treeList=new ArrayList<Node<String>>();
	}
	
	
	// Set/Get
	public void setTreeList(ArrayList<Node<String>> inputTreeList){
		treeList=inputTreeList;
	}
	
	public ArrayList<Node<String>> getTreeList(){
		return treeList;
	}
	
	
// Basic Utilities functions
	/**
	 * Process to create Trees from Soot file.
	 *@author Alexandros Lampridis
	 */
	public void  dotProcess_CreateTrees(boolean debugFlag){
		final HashMap<String,Vector<String>> connection=new HashMap<String,Vector<String>>();
		List<String> treeRoots=new ArrayList<String>();	
		int counter=0;
		// Accessing each dot file and creating a connection between a parent node and children nodes 
		for( File fTemp:dotFileNames){
			try{
				Scanner scannerByLine = new Scanner(fTemp);
				if(debugFlag==true){
					counter=counter+1;
					System.out.println("Processing Tree num: "+counter);
				}
				while(scannerByLine.hasNextLine()){
					String tempLine=scannerByLine.nextLine();
						if(tempLine.contains("->")){
							Scanner scanLine = new Scanner(tempLine);
							scanLine.useDelimiter("->");
							scanLine.findInLine("\"");
							String tempLabel=scanLine.next().replace("\"","");
							Vector<String> tempVec=new Vector<String>();
								if(!connection.containsKey(tempLabel)){
									tempVec.add(scanLine.next().replace(";","").replace("\"",""));
									connection.put(tempLabel, tempVec);
								}else{
									tempVec=connection.get(tempLabel);
									tempVec.add(scanLine.next().replace(";","").replace("\"",""));
									connection.put(tempLabel, tempVec);
								}
							scanLine.close();
						}
					}
				scannerByLine.close();

									
			}
			catch (FileNotFoundException e) {
				e.printStackTrace();
			}				
		}
		
		// Finding the root nodes
		Set<String> startingSet=connection.keySet();
		ArrayList<String> temp =new ArrayList<String>();
		connection.values().forEach(VecStr->VecStr.forEach(StrTemp->temp.add(StrTemp)));
		int flagCounter;
		for(String start:startingSet){
				counter=0;
				for(String end:temp){
				if(start.compareTo(end)==0){
					flagCounter=counter+1;
					break;
				}
			}
				if(counter==0){
				//System.out.println("Start="+start);
				treeRoots.add(start);
			}
		}
		
		
		
		createTrees(treeRoots,connection);
			connection.clear();
			treeRoots.clear();
		}	
	
	/**
	 * 
	 * @author Alexandros Lampridis
	 */
	public void  dotProcessCreateTreesFromGspan(){
		
		HashMap<String,String> nodeToLabelMap=new HashMap<String,String>();
		HashMap<String,Vector<String>> connectionNodesMaps=new HashMap<String,Vector<String>>();
		String tempLine;
		Scanner scannerByLine;
		
		
		for( File fTemp : dotFileNames){
			try{
				scannerByLine = new Scanner(fTemp);
				while(scannerByLine.hasNextLine()){
						tempLine=scannerByLine.nextLine();
						// New graph
						if(tempLine.contains("{")){
							nodeToLabelMap.clear();
							connectionNodesMaps.clear();
							RootNode<String> root=new RootNode<String>(null,tempLine,null);
							while(!tempLine.contains("}")){
								processOneDotGraph(tempLine,nodeToLabelMap,connectionNodesMaps);
								tempLine=scannerByLine.nextLine();
							}
							root.setGraphStatistics(tempLine);
							root.setData(findRoot(connectionNodesMaps,nodeToLabelMap));
							populateTree(root,connectionNodesMaps);
							root.nodeDataStringTransformHashMap1(nodeToLabelMap);
							ExtractionWithInputPattern patternLabelsFromGSPAN=new ExtractionWithInputPattern(Pattern.compile("[0-9][0-9]*"));
							root.transform(patternLabelsFromGSPAN);
							treeList.add(root);
							

						}
					}
				scannerByLine.close();

									
			}
			catch (FileNotFoundException e) {
				e.printStackTrace();
			}				
		}

		}
	/**
	 * @author Alexandros Lampridis
	 */
	private void processOneDotGraph(String graphLine, HashMap<String,String> nodeToLabelMap, HashMap<String,Vector<String>> connectionNodesMaps){
		Pattern nodeId=Pattern.compile("Node_[0-9]*");
		Pattern nodeLabel=Pattern.compile("label=\"[0-9]*\"");
		String tempNodeId;
		String tempNodeId1;
		String tempNodeLabel;
		Scanner scanLine;
		
		scanLine =new Scanner(graphLine);
		if(!graphLine.contains("{")){
			// Not a connection 
			if(!graphLine.contains("->")){
				tempNodeId=scanLine.findInLine(nodeId);
				tempNodeLabel=scanLine.findInLine(nodeLabel);
				if( !(tempNodeId==null) & ! (tempNodeLabel==null) ) {
					nodeToLabelMap.put(tempNodeId, tempNodeLabel);
				}
				
			}
			
			if(graphLine.contains("->")){
				tempNodeId=scanLine.findInLine(nodeId);
				tempNodeId1=scanLine.findInLine(nodeId);
				Vector<String> tempVec= connectionNodesMaps.get(tempNodeId);
				if(tempVec==null){
					tempVec=new Vector<String>();
					tempVec.addElement(tempNodeId1);
					connectionNodesMaps.put(tempNodeId, tempVec);
				}else{
					tempVec.add(tempNodeId1);
				}
			}

		}
		scanLine.close();
	}
	
	private String findRoot(HashMap<String,Vector<String>> connectionNodesMaps, HashMap<String, String> nodeToLabelMap){
		String rootLabel = null;
		HashMap<String,Integer> tempHash=new HashMap<String,Integer>();
		connectionNodesMaps.values().forEach(vec -> vec.forEach(str->tempHash.put(str, 1)));
		
		for(String temp:connectionNodesMaps.keySet()){
			if(!tempHash.containsKey(temp)){
				rootLabel=temp;
			}
		}
		// One node only
		if(rootLabel==null)
			rootLabel=nodeToLabelMap.keySet().iterator().next();
		return rootLabel;
		
	}
	
	// A wrapper function to recursively create the tree
	private void createTrees(List<String> treeRoots, HashMap<String,Vector<String>> connection){
		for(String RootLabel: treeRoots){
			Node<String> rootNode=new Node<String>(RootLabel,null);
			populateTree(rootNode,connection);
			treeList.add(rootNode);
		}
	}
	
	private void populateTree(Node<String> Node,HashMap<String,Vector<String>> connection){
		
		if(connection.containsKey(Node.getData())){
			for(String childLabel:connection.get(Node.getData())){
				Node<String> childNode=new Node<String>(childLabel,Node);
				populateTree(childNode,connection);
				Node.addChildren(childNode);
			}
		}
		
	}
	
	
	
	
// Various Utilities functions 
	// printing the path of the dot files
	public void printTheDotFiles(){
		System.out.println("The file to be processed");
		for(File fileTemp : dotFileNames){
			System.out.println(fileTemp.toString());
		}	
	}
	
	/**
	 * Function to recreate a Tree in a dot file
	 * @author Alexandros Lampridis
	 * @param node Node String the starting node of a Tree
	 * @param counter int should start with zero  
	 * @param printWriter must be opened at the wrapper
	 */
	static public int createDotFilesLikeTrees(Node<String> node, int counter,PrintWriter printWriter){
		String idNode="node"+counter;
		if(!node.getChildren().isEmpty())
			for(Node<String> child : node.getChildren()){
				printWriter.println(idNode+" -> "+"node"+String.valueOf(counter+1));
				
				counter=createDotFilesLikeTrees(child,counter+1,printWriter);
			}
		printWriter.println(idNode+" [label="+node.getData()+"]");
		return counter;
	}
	
}
