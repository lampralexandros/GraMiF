package dataProcess;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.Set;
import java.util.Vector;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


import feature.Feature;
import net.sf.javaml.clustering.Clusterer;
import net.sf.javaml.clustering.IterativeKMeans;
import net.sf.javaml.clustering.KMeans;
import net.sf.javaml.clustering.evaluation.AICScore;
import net.sf.javaml.clustering.evaluation.ClusterEvaluation;

import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.DefaultDataset;
import net.sf.javaml.core.DenseInstance;
import net.sf.javaml.core.Instance;

import net.sf.javaml.distance.CosineSimilarity;
import net.sf.javaml.distance.DistanceMeasure;


public class DotFileProcess {
	
	private List<File> dotFileNames;
	private ArrayList<String> UniqueLabels;
	private HashMap<String,Integer> LabelsMap;
	private ArrayList<double[]> LabelsFeature=new ArrayList<double[]>();
	private ArrayList<Feature> UniqueFeatures=new ArrayList<Feature>();
	private ArrayList<FeatureManyLabel> DuplicateFeatures=new ArrayList<FeatureManyLabel>();
	// A Map to do the transform from graph native label to cluster label
	private HashMap<String,String> LabelClusterMap=new HashMap<String,String>();
	

	//Class to represent a data point as a feature the names of the methods as a ArrayList of strings and theirs Feature vector from the Wordnet
	public class FeatureManyLabel {
		private String Label;
		private ArrayList<String> MulLabels;
		private double[] LabelFeature;
		//Constructor
		FeatureManyLabel(double[] DoubleArray){
			MulLabels=new ArrayList<String>();
			LabelFeature=new double[DoubleArray.length];
			LabelFeature=DoubleArray;
		}
		FeatureManyLabel(){
			MulLabels=new ArrayList<String>();
		}
		//Get-Set methods
		public String getMainLabel(){return Label;};
		public void setMainLabel(String label){Label=label;};
		public void addLabels(String label){MulLabels.add(label);};
		public double[] getLabelFeature(){return LabelFeature;};
		public ArrayList<String> getMulLabels(){return MulLabels;};
	}

	//Constructor
	DotFileProcess(File domainFolder){
		dotFileNames= Arrays.asList(domainFolder.listFiles());
		System.out.println("These are the files to be processed :");
		printTheDotFiles();
		dotProcess();
		dotProcess_CreateTrees();
	}
	// Doesn't work
	DotFileProcess(String dotFolderPath){
		dotFileNames.add(new File(dotFolderPath));
	}
	// Variant Utilities functions 
	// printing the path file
	public void printTheDotFiles(){
		Iterator<File> iterFiles=dotFileNames.iterator();
		while(iterFiles.hasNext()){
			System.out.println(iterFiles.next().toString());
		}
	}
	//printing the Labels from the ArrayList
	public void printTheLabels(){
		Iterator<String> iter=UniqueLabels.iterator();
		while(iter.hasNext()){
			System.out.println(iter.next().toString());
		}
	}
	//printing the Labels from the HasMap 
	public void printTheLabelsMap(){
		Iterator<String> iter=LabelsMap.keySet().iterator();
		while(iter.hasNext()){
			System.out.println(iter.next().toString());
		}
	}
	//printing the Map Label->Cluster
	public void printTheClusterLabelMap(){
		Iterator<Entry<String, String>> iter= LabelClusterMap.entrySet().iterator();
		Entry<String,String> Temp;
		while(iter.hasNext()){
			Temp=iter.next();
			System.out.println("Label="+Temp.getKey()+" Cluster="+Temp.getValue());
		}
	}

	// Set Get 
	public ArrayList<String> getUniqueLabels(){
		return UniqueLabels;
	}
	
	public  ArrayList<double[]> getLabelsFeature(){
		return LabelsFeature;
	}
	
	public ArrayList<Feature> getUniqueFeatures(){
		return UniqueFeatures;
	}
	
	// Process the dot file to extract the method name and adding it to the hash map and the ArrayList
	private void  dotProcess(){
		int n=0;
		Pattern methodName=Pattern.compile(" [a-zA-Z]*[(]");
		UniqueLabels=new ArrayList<String>();
		LabelsMap=new HashMap<String,Integer>();
		Iterator<File> iterFiles=dotFileNames.iterator();
		
		while(iterFiles.hasNext()){
			try {
				Scanner scannerByLine = new Scanner(new File(iterFiles.next().toString()));
				while(scannerByLine.hasNextLine()){
					String temp=scannerByLine.nextLine();
					Scanner scanLine = new Scanner(temp);
					String s=scanLine.findInLine(methodName);
					if(s != null ){
						s=s.replaceAll("[(]","");
							if(!LabelsMap.containsKey(s)){								
								LabelsMap.put(s, n);
								n=n+1;
								UniqueLabels.add(s);
								//System.out.println(s);
							}
											
						}
					scanLine.close();
					}
				scannerByLine.close();
			} 
			catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	
	// creating a tree structure from the graphs
	private void  dotProcess_CreateTrees(){
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
						System.out.println("Start="+start);
						treeRoots.add(start);
					}
				}
				
				
			}
			catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			
		}
		
		
		
		
		
		connection.clear();
	}
	
	
	
	
	
	
	
	
	
	
	
	//
	public void concentrateGraphs() throws IOException{
		
		String lineTemp;
		HashMap<String,Integer> DotNodes=new HashMap<String,Integer>();
		
		FileWriter fileWriter=new FileWriter("concat.dot");
		PrintWriter printWriter = new PrintWriter(fileWriter);
		Iterator<File> iterFiles=dotFileNames.iterator();
		while(iterFiles.hasNext()){
			Scanner scannerByLine = new Scanner(new File(iterFiles.next().toString()));
		
			// printing the graph name
			printWriter.println(scannerByLine.nextLine());
				// creating the new nodes
				while(scannerByLine.hasNextLine()){
					lineTemp=scannerByLine.nextLine();
					//Scanner scanLine = new Scanner(scannerByLine.nextLine());
					Scanner scanLine = new Scanner(lineTemp);
					if(scanLine.findInLine("->")!=null){
					    //System.out.println(lineTemp);
					    dotNodeConnectionCreation(printWriter,lineTemp,DotNodes );
					}
					scanLine.close();
					
				}
			
			
			
			printWriter.println("}");

			scannerByLine.close();
			// clearing HashMap for the next DotFile
			DotNodes.clear();
		}
		printWriter.close();
		fileWriter.close();
	}
	
	// Dot functions NOT ready
	private void dotNodeConnectionCreation(PrintWriter printWriter, String lineTemp,HashMap<String,Integer> DotNodes ){
		Scanner scanLine = new Scanner(lineTemp);
		Pattern methodName=Pattern.compile(" [<]*[a-zA-Z]*[$]*[0-9]*[>]*[(]");
		String methodNameCaller=scanLine.findInLine(methodName).replaceAll("[(]","");
		String methodNameCalle=scanLine.findInLine(methodName).replaceAll("[(]","");
		
		//replace <init> with class name
		scanLine.close();
		scanLine = new Scanner(lineTemp);
		Pattern className=Pattern.compile("[.]*[a-zA-Z]*[:]");
		if(methodNameCaller.contains("<init>")){
			methodNameCaller=scanLine.findInLine(className);
		}
		if(methodNameCalle.contains("<init>")){
			methodNameCalle=scanLine.findInLine("->");
			methodNameCalle=scanLine.findInLine(className);
		}
		scanLine.close();
		
		if(LabelClusterMap.get(methodNameCaller)!=null){
		methodNameCaller=LabelClusterMap.get(methodNameCaller);}
		
	
		if(LabelClusterMap.get(methodNameCalle)!=null){
		methodNameCalle=LabelClusterMap.get(methodNameCalle);}
		
		if(!DotNodes.containsKey("\""+methodNameCaller+"\""+"->"+"\""+methodNameCalle+"\"")){
		printWriter.println("\""+methodNameCaller+"\"");
		printWriter.println("\""+methodNameCalle+"\"");
		printWriter.println("\""+methodNameCaller+"\""+"->"+"\""+methodNameCalle+"\"");
		DotNodes.put("\""+methodNameCaller+"\""+"->"+"\""+methodNameCalle+"\"", 1);
		}
//
//		if(!DotNodes.containsKey(methodNameCaller)){
//			DotNodes.put(methodNameCaller, 1);
//			if(LabelClusterMap.get(methodNameCaller)!=null){
//				printWriter.println("\""+LabelClusterMap.get(methodNameCaller)+"\"");
//				methodNameCaller=LabelClusterMap.get(methodNameCaller);}
//			else
//				printWriter.println("\""+methodNameCaller+"\"");
//		}else{
//			DotNodes.put(methodNameCaller, DotNodes.get(methodNameCaller)+1);
//		}
//
//		if(!DotNodes.containsKey(methodNameCalle)){
//			DotNodes.put(methodNameCalle, 1);
//			if(LabelClusterMap.get(methodNameCalle)!=null){
//				printWriter.println("\""+LabelClusterMap.get(methodNameCalle)+"\"");
//				methodNameCalle=LabelClusterMap.get(methodNameCalle);}
//			else
//				printWriter.println("\""+methodNameCalle+"\"");
//		}else{
//			DotNodes.put(methodNameCalle, DotNodes.get(methodNameCalle)+1);
//		}
//		
//		printWriter.println("\""+methodNameCaller+"\""+"->"+"\""+methodNameCalle+"\"");

//		System.out.println(test);
//		test=test.concat(" ->"+scanLine.findInLine(methodName)).replaceAll("[(]","");
//		System.out.println(test);

	}
	
	// Semantic analysis using WordNet creating the ArrayList feature vector  
	public void doSemanticAnalysis(){
		Iterator<String> iter=UniqueLabels.iterator();
		String Temp;
		// Passing first time to calculate all the dimensions 
		while(iter.hasNext()){
			Temp=iter.next();
//			WordModel.commonWordNet.getSentenceFeatureVector(Temp);
			}
		// Passing Second time to create the Features 
		iter=UniqueLabels.iterator();
		Feature tempFeature;
		ArrayList<Feature> AllFeature=new ArrayList<Feature>();
		while(iter.hasNext()){
			Temp=iter.next();
	//		LabelsFeature.add(WordModel.commonWordNet.getSentenceFeatureVector(Temp));
	//		tempFeature=new Feature(Temp,WordModel.commonWordNet.getSentenceFeatureVector(Temp));
	//		AllFeature.add(tempFeature);
		}
		// Removing Duplicate Feature and populating the UniqueFeatures and the DuplicateFeatures based on feature Vector
		removeDuplicateFeature(AllFeature);		
		// Creating Labels for DuplicateFeature and adding to the UniqueFeature
		conDuplicateFeature();
		
	}
	
	// Clustering
	public void Clustering(){
		Dataset data=new DefaultDataset();
		
		Iterator<double[]> iter=LabelsFeature.iterator();
		while(iter.hasNext()){
			Instance TempInstance=new DenseInstance(iter.next());
			data.add(TempInstance);
		}
		System.out.println("Data Size="+String.valueOf(data.size()));
		//Define the cosine Similarity as a Distance Measure
		DistanceMeasure dm=new CosineSimilarity();
		
		Clusterer km = new KMeans(5,100, dm);
		Dataset[] clusters = km.cluster(data);
		
		Iterator<Instance> iterInst=clusters[0].iterator();
		Iterator<String> iterLabels=UniqueLabels.iterator();
		ArrayList<String> ClusterLabels=new ArrayList<String>();
		String temp;
		
		//Collection<Double> CollectTemp;
		
		double[] tempfeature;
		while(iterInst.hasNext()){
			//System.out.println("This ="+Arrays.toString(iterInst.next().values().toArray())); 
			iter=LabelsFeature.iterator();
			iterLabels=UniqueLabels.iterator();
			tempfeature = iterInst.next().values().stream().collect(Collectors.toList()).stream().mapToDouble(Double::doubleValue).toArray();
			while(iter.hasNext()){
				temp=iterLabels.next();
				if(compare2doubleArr(tempfeature,iter.next())){
					ClusterLabels.add(temp);
					break;
				}
				
			}
			
			
			
			
			//CollectTemp=iterInst.next().values();
			// tempfeature = iterInst .next().values().stream().collect(Collectors.toList()).stream().mapToDouble(Double::doubleValue).toArray(); 
//			System.out.println("This 1="+Arrays.toString(CollectTemp.toArray())); 
//			tempfeature=CollectTemp.stream().collect(Collectors.toList()).stream().mapToDouble(Double::doubleValue).toArray(); 
//			System.out.println("This 2="+Arrays.toString(tempfeature)); 
//			while(iter.hasNext()){
//				 temp=iterLabels.next();
//				 if(){
//					ClusterLabels.add(temp);
//					
//				 }
//			 }
		}
		
		Iterator<String> iterLabels2=ClusterLabels.iterator();
		while(iterLabels2.hasNext()){
			System.out.println(iterLabels2.next());
			
		}
		
		
		
//		try {
//			FileHandler.exportDataset(clusters[2],new File("output2.txt"));
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}
	
	public void Clustering2(){
		Dataset data=new DefaultDataset();
		Iterator<Feature> iter=UniqueFeatures.iterator();
		//Creating the data set
		while(iter.hasNext()){
			Instance TempInstance=new DenseInstance(iter.next().getLabelFeature());
			data.add(TempInstance);
		}
		System.out.println("Data Set Size="+String.valueOf(data.size()));
		//Define the cosine Similarity as a Distance Measure
		DistanceMeasure dm=new CosineSimilarity();
		//Using the basic Kmeans		
		//Clusterer km = new KMeans(5,100, dm);
		ClusterEvaluation ce=new AICScore();
		Clusterer km=new IterativeKMeans(10,20,500,dm,ce);
		
		
		Dataset[] clusters = km.cluster(data);
		
		
		
		FileWriter fileWriter;
		try {
			fileWriter = new FileWriter("clusters.txt");
			PrintWriter printWriter = new PrintWriter(fileWriter);

		
		//Collecting and transforming a double[] feature vector into a Label and adding cluster items to a file
		double[] Tempfvector;
		Feature TempFeature;
		int i=0;
		for(Dataset clust : clusters){
			Iterator<Instance> iterInst=clust.iterator();
			String ClusterName="Cluster";
			ClusterName=ClusterName.concat(String.valueOf(i++));
			printWriter.println("Cluster="+ClusterName+"{");
			while(iterInst.hasNext()){
			iter=UniqueFeatures.iterator();
			Tempfvector = iterInst.next().values().stream().collect(Collectors.toList()).stream().mapToDouble(Double::doubleValue).toArray();
				while(iter.hasNext()){
				TempFeature=iter.next();	
					if(compare2doubleArr(Tempfvector,TempFeature.getLabelFeature())){
						LabelClusterMap.put(TempFeature.getLabel(),ClusterName );
						//System.out.println(TempFeature.getLabel()+" Cluster="+ClusterName);
						printWriter.println(TempFeature.getLabel());
						break;
					}
				}
			}
			printWriter.println("}");
			printWriter.flush();
		}
		printWriter.close();
		fileWriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		// Adding to LabelClusteMAp the duplicate labels 
		for(FeatureManyLabel dupFeatu:DuplicateFeatures){
			if(LabelClusterMap.containsKey(dupFeatu.getMainLabel())){
				String ClusterName=LabelClusterMap.get(dupFeatu.getMainLabel());
				for(String label : dupFeatu.getMulLabels()){
					LabelClusterMap.put(label,ClusterName );
				}
			}
		}
		//Remove main
		if(LabelClusterMap.remove(" main")!=null)
			System.out.println("Main removed");
		
		System.out.println("EndingClustering");
	}
	
	// a test for the distance measure
	public void testingTheDm(){
		//create a new dataset
		Dataset data=new DefaultDataset();
		Iterator<Feature> iter=UniqueFeatures.listIterator();
		//adding the FeatureVector as an Instace in the data base
		while(iter.hasNext()){
			Instance TempInstance=new DenseInstance(iter.next().getLabelFeature());
			data.add(TempInstance);
		}
		// Creating the Distance Measure as Cosine Similarity 
		DistanceMeasure dm=new CosineSimilarity();
		int t=8;
		double[] test1=UniqueFeatures.get(0).getLabelFeature();
		double[] test2=UniqueFeatures.get(t).getLabelFeature();
		System.out.println("Label1="+UniqueFeatures.get(0).getLabel()+" Label2="+UniqueFeatures.get(t).getLabel());
		System.out.println("Manios cosine sim="+String.valueOf(similarity(test1,test2)));
		Instance TempInstance1=new DenseInstance(UniqueFeatures.get(0).getLabelFeature());
		Instance TempInstance2=new DenseInstance(UniqueFeatures.get(t).getLabelFeature());
		System.out.println("dm="+String.valueOf(dm.measure(TempInstance1, TempInstance2)));
		
	}
	
	//Compare two double arrays to check if they are same. Returns true if they are and false if they are not
	public boolean compare2doubleArr(double[] a1, double[] a2){
		boolean flag;
		if(a1.length!=a2.length){System.exit(1);;}
		flag=true;
		for(int i=0;i<a1.length;i++){if(a1[i]!=a2[i]){flag=false;break;}}
		return flag;
	}
	
	//Test to double vectors
	public void TestDouble(){
		Iterator<double[]> iter1=LabelsFeature.iterator();
		Iterator<double[]> iter2;
		Iterator<String> iter3;
		ArrayList<String> TempList = new ArrayList<String>();
		String str;
		int counter;
		double[] fv;
		while(iter1.hasNext()){
			counter=0;
			fv=iter1.next();
			iter2=LabelsFeature.iterator();
			iter3=UniqueLabels.iterator();
			while(iter2.hasNext()){
				str=iter3.next();
				if(compare2doubleArr(fv,iter2.next())){
					counter=counter+1;
					TempList.add(str);
				}
				
			}
			
			if(counter>1){
				System.out.println("Count="+String.valueOf(counter));
				System.out.println("Duplicate=");
				Iterator<String> iter4=TempList.iterator();
				while(iter4.hasNext()){
					System.out.print(iter4.next()+" ");
				}
				System.out.println();
			}
			
			TempList.clear();
		}
	}
	
	//Removes duplicate features based on feature vector
	private void removeDuplicateFeature(ArrayList<Feature> AllFeature){
		Iterator<Feature> iter1;
		ArrayList<Feature> DuplicateFeature=new ArrayList<Feature>();		
		Feature Temp;
		Feature Temp2;
			
		while(AllFeature.size()>0){
			iter1=AllFeature.listIterator();
			Temp=iter1.next();
			iter1.remove();
			while(iter1.hasNext()){
				Temp2=iter1.next();
				if(compare2doubleArr(Temp.getLabelFeature(),Temp2.getLabelFeature())){
					DuplicateFeature.add(Temp2);
					iter1.remove();
				}
			}	
			if (DuplicateFeature.size()>0){
				DuplicateFeature.add(Temp);
				FeatureManyLabel Temp3=new FeatureManyLabel(Temp.getLabelFeature());
				iter1=DuplicateFeature.iterator();
				while(iter1.hasNext()){
					Temp3.addLabels(iter1.next().getLabel());
				}
				DuplicateFeatures.add(Temp3);
			}else{
				UniqueFeatures.add(Temp);
			}
			DuplicateFeature.clear();		
		}
		
		
		
	}

	//Creating a unique Label and adding the the Duplicate Feature as a Unique Feature
	private void conDuplicateFeature(){
		Iterator<FeatureManyLabel> iter1= DuplicateFeatures.iterator();
		int counter=0;
		String DupLabel=" Duplicate";
		FeatureManyLabel ManyTemp;
		while(iter1.hasNext()){
			DupLabel=DupLabel.concat(String.valueOf(counter));
			ManyTemp=iter1.next();
			ManyTemp.setMainLabel(DupLabel);
			Feature Temp=new Feature(ManyTemp.getMainLabel(),ManyTemp.getLabelFeature());
			UniqueFeatures.add(Temp);
			counter=counter+1;
		}
		
	}
	
	//Testing of similarity
    public static double similarity(double[] v1, double[] v2) {
    	return dot(v1,v2)/Math.sqrt(dot(v1,v1)*dot(v2,v2));//it's fine to divide, since larger feature sets associate to more cognitive relations
    }
    
    public static double dot(double[] v1, double[] v2) {
    	double ret = 0;
    	for(int i=0;i<v1.length && i<v2.length;i++)
    		ret += v1[i]*v2[i];
    	return ret;
    }
	
	
}
