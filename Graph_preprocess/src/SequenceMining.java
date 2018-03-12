import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import ca.pfv.spmf.algorithms.sequentialpatterns.prefixSpan_AGP.items.SequenceDatabase;
import ca.pfv.spmf.algorithms.sequentialpatterns.prefixSpan_AGP.items.creators.AbstractionCreator;
import ca.pfv.spmf.algorithms.sequentialpatterns.prefixSpan_AGP.items.creators.AbstractionCreator_Qualitative;
import ca.pfv.spmf.algorithms.sequentialpatterns.prefixspan.AlgoPrefixSpan;
import ca.pfv.spmf.algorithms.sequentialpatterns.prefixspan.SequentialPattern;
import ca.pfv.spmf.algorithms.sequentialpatterns.prefixspan.SequentialPatterns;
import ca.pfv.spmf.test.MainTestPrefixSpan_saveToMemory;


/**
 * This Class represent a wrapper for various sequence mining algorithms
 * @author Alex Lampridis
 *
 */
public class SequenceMining {

	private ArrayList<String[]> inputSequences;
	private HashMap<String,Integer> outlierMap;
	private int outlierCounter;
	
//	/**
//	 * Constructor
//	 * @param Input sequences 
//	 */
//	public SequenceMining(ArrayList<Vector<String>> labelSequence, HashMap<String,Integer> labelClusters,int numClusters){
//		InputSequences=new ArrayList<int[]>();
//		int[] numSeq;
//		
//		int outlierCounter=numClusters;
//		int index;
//		for(Vector<String> Vec :labelSequence){
//			numSeq=new int[Vec.size()];
//			index=0;
//			for(String label : Vec){
//				if(labelClusters.containsKey(label)){
//					numSeq[index]=labelClusters.get(label).intValue();
//				}
//				// outlier case
//				else{
//					numSeq[index]=outlierCounter;
//					outlierCounter=outlierCounter+1;
//				}
//				index=index+1;
//			}
//			InputSequences.add(numSeq);
//		}
//		
//	}
	
	/**
	 * Constructor with the number of the clusters to calculate the outliers
	 */
	public SequenceMining(int numOutlier){
		inputSequences= new ArrayList<String[]>();
		outlierMap= new HashMap<String,Integer>();
		
		
		outlierCounter=numOutlier;
	}
	
	/**
	 * A function to add a sequence in the sequence list
	 */
	public void addSequence(ArrayList<Vector<String>> labelSequence, HashMap<String,Integer> labelClusters){
		
		ArrayList<Vector<String>> tempSeq=new ArrayList<Vector<String>>(); 
		HashMap<String,Integer> UniqueNum=new HashMap<String,Integer>();
		Vector<String> tempVec;
		int max=0;
		for(Vector<String> Vec :labelSequence){
			if(Vec.size()>=max)
				max=Vec.size();
		}
		
		
		for(int i=0;i<max;i++){
			for(Vector<String> Vec :labelSequence){
				if(Vec.size()>i){
					if(labelClusters.containsKey(Vec.get(i))){
						// checking for duplicate item
						if(!UniqueNum.containsKey(String.valueOf(labelClusters.get(Vec.get(i)).intValue()))){
							UniqueNum.put(String.valueOf(labelClusters.get(Vec.get(i)).intValue()), 1);
						}
					}else{
						if(outlierMap.containsKey(Vec.get(i))){
							// checking for duplicate item
							if(!UniqueNum.containsKey(String.valueOf(outlierMap.get(Vec.get(i)).intValue()))){
								UniqueNum.put(String.valueOf(outlierMap.get(Vec.get(i)).intValue()), 1);
							}
						}else{
							outlierCounter=outlierCounter+1;
							outlierMap.put(Vec.get(i),outlierCounter );
							// checking for duplicate item
							if(!UniqueNum.containsKey(String.valueOf(outlierMap.get(Vec.get(i)).intValue()))){
								UniqueNum.put(String.valueOf(outlierMap.get(Vec.get(i)).intValue()), 1);
							}
						}
					}
				}
				
			}
			// creating a vector with the unique numbers
			tempVec=new Vector<String>();
			for(String tempString : UniqueNum.keySet()){
				tempVec.add(tempString);
			}
			tempSeq.add(tempVec);
			
		}
		
		// preparing the sequence to add it into numSeq
		// with the form of input in SPMF
		
		int charNum=0;
		for(Vector<String> Vec :tempSeq)
			charNum=charNum+Vec.size();
		
		String[] inputSeq=new String[charNum+tempSeq.size()+1];
		int index=0;
		for(Vector<String> Vec :tempSeq){
			for(String tempString:Vec){
				inputSeq[index]=tempString;
				index=index+1;
			}
			inputSeq[index]="-1";
			index=index+1;
		}
		inputSeq[index]="-2";
		inputSequences.add(inputSeq);
	}
	
	/**
	 * This is a wrapper for the Prefix Span algorithm. It creates a Sequence database incorporating the sequences 
	 * from the sequences List "inputSequences" 
	 * @author Alexandros Lampridis
	 * @param support 
	 * the minimum support of the sequence occurrence 
	 * @param verbose
	 * True to print the data base and the results statistics
	 * @throws IOException 
	 */
	public void RunPrefixSpan(double support,boolean verbose) {
		// write the input file
		FileWriter fileWriter;
		try {
			fileWriter = new FileWriter("sequenceList.txt");
			PrintWriter printWriter = new PrintWriter(fileWriter);
			for(String[] tempString:inputSequences){
				for(String tS: tempString){
					printWriter.print(tS+" ");
				}
				printWriter.println("");
			}
			printWriter.close();
			fileWriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// 
		try {
			//String inputFile = fileToPath("sequenceList.txt");
			// Create an instance of the algorithm 
			AlgoPrefixSpan algo = new AlgoPrefixSpan(); 
			algo.setMaximumPatternLength(400);
			
	        // if you set the following parameter to true, the sequence ids of the sequences where
	        // each pattern appears will be shown in the result
	        algo.setShowSequenceIdentifiers(true);
			
			// execute the algorithm with minsup = 50 %
			SequentialPatterns patterns = algo.runAlgorithm("sequenceList.txt", 0.4, null);    
			System.out.println(" == PATTERNS FOUND ==");
			for(List<SequentialPattern> level : patterns.levels) {
				for(SequentialPattern pattern : level){
					System.out.println(pattern + " support : " + pattern.getAbsoluteSupport());
				}
			}
			
			// print statistics
			algo.printStatistics();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	
	
}
