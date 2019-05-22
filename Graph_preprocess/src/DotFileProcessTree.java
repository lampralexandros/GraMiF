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


// This class is basic class to extract tree structures from the input graphs

public class DotFileProcessTree {
	
	// List of the dot file names to be processed
	private List<File> dotFileNames;
	// ArrayList of Tree structures with raw labels
	private ArrayList<Node<String>> treeList;
	
	
	//Constructor input a folder with dot files
	public DotFileProcessTree(File domainFolder){
		//initialize dotFileNames
		dotFileNames= Arrays.asList(domainFolder.listFiles());
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
	public void  dotProcess_CreateTrees(){
		HashMap<String,Vector<String>> connection=new HashMap<String,Vector<String>>();
		List<String> treeRoots=new ArrayList<String>();	
		// Accessing each dot file and creating a connection between a parent node and children nodes 
		for( File fTemp:dotFileNames){
			try{
				Scanner scannerByLine = new Scanner(fTemp);
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
		int counter;
		for(String start:startingSet){
				counter=0;
				for(String end:temp){
				if(start.compareTo(end)==0){
					counter=counter+1;
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
		
		List<String> treeRoots=new ArrayList<String>();	
		HashMap<String,String> nodeToLabelMap=new HashMap<String,String>();
		Pattern nodeId=Pattern.compile("Node_[0-9]*");
		Pattern nodeLabel=Pattern.compile("label=\"[0-9]*\"");
		String tempNodeId;
		String tempNodeLabel;
		
		for( File fTemp:dotFileNames){
			try{
				Scanner scannerByLine = new Scanner(fTemp);
				while(scannerByLine.hasNextLine()){
						String tempLine=scannerByLine.nextLine();
						// New graph
						if(tempLine.contains("{")){
							while(!tempLine.contains("}")){
								// Not a connection 
								if(!tempLine.contains("->")){
									tempNodeId=scannerByLine.findInLine(nodeId);
									tempNodeLabel=scannerByLine.findInLine(nodeLabel);
									
									tempLine=scannerByLine.nextLine();
								}
							}
						}
					}
				scannerByLine.close();

									
			}
			catch (FileNotFoundException e) {
				e.printStackTrace();
			}				
		}
		

		
	
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
