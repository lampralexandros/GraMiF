package nodes;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import transformations.GeneralTransform;


// A classic node representing a tree

public class Node<T> implements Serializable {

	protected List<Node<T>> children=new ArrayList<Node<T>>();
	protected Node<T> parent = null;
	protected T data=null;
	
	public Node(){
		
	}
	public Node(T inputData, Node<T> inputParent){
		parent=inputParent;
		data=inputData;
	}
	
	// Set Get functions
	public void setData(T inputData){
		data=inputData;
	}
	
	
	public T getData(){
		return data;
	}
	
	public void setParent(Node<T> inputParent){
		parent=inputParent;
	}
	
	public Node<T> getParent(){
		return parent;
	}
	
	public void setChildren(List<Node<T>> childrenInput){
		children=childrenInput;
	}
	
	public List<Node<T>> getChildren(){
		return children;
	}
	
// Basic Utilities functions
	
	// add a lower node
	public void addChildren(Node<T> nodeChild){
		children.add(nodeChild);
	}
	
	//Root
	public boolean isRoot(){
		return parent==null;
	}
	//Leaf
	public boolean isLeaf(){
		return children.isEmpty();
	}
		
	
	// This is NOT a serializer, this function returns all the data of a tree
	/**
	 *This function (right-most) traverse the tree and adds the node data (not-null data) into the input arrayList. 
	 * @author Alexandros Lampridis
	 * @param dataList
	 */
	public void getDataTraverser(ArrayList<T> dataList){
		if(data != null)
		dataList.add(data);
		if(!children.isEmpty()){
			for(Node<T> child:children)
				child.getDataTraverser(dataList);
		}	
	}
	
	/**
	 * Basic (right most traverser) traverse the tree and returning node starting from the leaf of a branch and recursively returns the rest.
	 * @author Alexandros Lampridis
	 * @return Node<T>
	 */
	public Node<T> getNodeTraverser(){
		for(Node<T> child:children)
			if(!children.isEmpty()){
				child.getNodeTraverser();
			}
		return this;
		
	}
	
	/**
	 * Check in the HashMap for the String key and sets a string (from Integer value) as the data 
	 * @author Alexandros Lampridis
	 * @param HashMap<String,Integer> hashMap
	 */
	public void nodeDataStringTransformHashMap(HashMap<String,Integer> hashMap){
		
		if(hashMap.containsKey(this.getData())){
			
			this.setData((T) hashMap.get(this.getData()).toString());
		}
		
		if(!children.isEmpty()){
			for(Node<T> child:children)
				child.nodeDataStringTransformHashMap(hashMap);
		}
		
	}
	
	/**
	 * Check in the HashMap for the String key and sets a string as the data 
	 * @author Alexandros Lampridis
	 * @param HashMap<String,String> hashMap
	 */
	public void nodeDataStringTransformHashMap1(HashMap<String,String> hashMap){
		
		if(hashMap.containsKey(this.getData())){
			
			this.setData((T) hashMap.get(this.getData()));
		}
		
		if(!children.isEmpty()){
			for(Node<T> child:children)
				child.nodeDataStringTransformHashMap1(hashMap);
		}
		
	}

	//Serializer wrapper 
	public void serializer(ArrayList<Vector<T>> dataSequence){
		Vector<T> sequence=new Vector<T>();
		this.traverserForSerializer(sequence, dataSequence);
			
	}
	// A traverser to complement the serializer wrapper
	protected void traverserForSerializer(Vector<T> sequence,ArrayList<Vector<T>> dataSequence){	
		sequence.add(data);
		if(!children.isEmpty()){
			for(Node<T> child:children){
				Vector<T> tempvec=new Vector<T>();
				tempvec.addAll(sequence);
				child.traverserForSerializer(tempvec, dataSequence);
			}
			
		}
		else
			dataSequence.add(sequence);
		
	}
	
	// traverse and effect transformation
	public void transform(GeneralTransform<T> transformer){
		data=transformer.transform(data);
		if(!children.isEmpty()){
			for(Node<T> child:children){
			child.transform(transformer);
			}
		}
	}
	
	public int getMaxBreadth(int max){
		int tempMax;
		for( Node<T> child : children){
			tempMax=child.getMaxBreadth(max);
			max = (tempMax >= max) ? tempMax : max ;
		}
		max = (children.size() >= max) ? children.size() : max ;
		return max;
	}
	
	public int getMaxDepth(int level, int max){
		
		for( Node<T> child : children){
			max = child.getMaxDepth(level+1, max);
		}
		if(children.isEmpty())
			max= (level >= max) ? level : max ;
		return max;
	}
	
	public int getTotalNodes(int counter){
		
		for( Node<T> child : children){
			counter = child.getTotalNodes(counter);
		}
		return counter+1;
	}
	
	
}
