import java.util.ArrayList;

import java.util.regex.Pattern;

import nodes.Node;
import transformations.ClassExtraction;
import transformations.ClassExtractionWithInput;
import transformations.MethodExtraction;
import transformations.MethodExtractionWithInput;
/**
 * There is a bug when you use extractClassLabel() before extractMethodLabel()
 * @author Alex Lampridis
 *
 */
public class TreeProcess {
	
	
		
	ArrayList<Node<String>> pTreeList;
	
	public TreeProcess(ArrayList<Node<String>> treeList){
		pTreeList=new ArrayList<Node<String>>();
		treeList.forEach(node->pTreeList.add(node));	
	}
	
	// Set/Get
	public void setTreeList(ArrayList<Node<String>> inputTreeList){
		pTreeList=inputTreeList;
	}
	
	public ArrayList<Node<String>> getTreeList(){
		return pTreeList;
	}
	
	
	public void extractMethodLabel(){
		Pattern methodName=Pattern.compile(" [<]*[a-zA-Z]*[$]*[0-9]*[>]*[(]");
		MethodExtraction transformer=new MethodExtraction(methodName);
		for(Node<String> tNode : pTreeList){
			tNode.transform(transformer);
		}
	}
	
	public void extractMethodLabel(String input){
		Pattern methodName=Pattern.compile(" [<]*[a-zA-Z]*[$]*[0-9]*[>]*[(]");
		MethodExtractionWithInput transformer=new MethodExtractionWithInput(methodName,input);
		for(Node<String> tNode : pTreeList){
			tNode.transform(transformer);
		}
	}
	
	public void extractClassLabel(){
		Pattern className=Pattern.compile("[.]*[a-zA-Z]*[$]*[0-9]*[:]");
		ClassExtraction transformer=new ClassExtraction(className);
		for(Node<String> tNode : pTreeList){
			tNode.transform(transformer);
		}
	}
	
	public void extractClassLabel(String input){
		ClassExtractionWithInput transformer=new ClassExtractionWithInput(input);
		for(Node<String> tNode : pTreeList){
			tNode.transform(transformer);
		}
	}
	

	
}
