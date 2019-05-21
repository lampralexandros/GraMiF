import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.Vector;


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
	
	// Creating tree structures from the graphs
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
	
	
	
	
// Variant Utilities functions 
	// printing the path of the dot files
	public void printTheDotFiles(){
		System.out.println("The file to be processed");
		for(File fileTemp : dotFileNames){
			System.out.println(fileTemp.toString());
		}	
	}	
}
