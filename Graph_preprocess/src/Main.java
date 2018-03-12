import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

public class Main {

	public static void main(String[] args) {
		
		// Start by concentrate the dot files in the domain directory
		File folder=new File("domain");
		DotFileProcess GameDevDomain=new DotFileProcess(folder);
	//	GameDevDomain.printTheLabelsMap();
//		try {
//			GameDevDomain.concentrateGraphs();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		//GameDevDomain.printTheLabels();
		GameDevDomain.doSemanticAnalysis();
		System.out.println("LabelsFeatureSize="+String.valueOf(GameDevDomain.getLabelsFeature().size())+" UniqueFeatures="+String.valueOf(GameDevDomain.getUniqueFeatures().size()));
//		GameDevDomain.TestDouble();
		System.out.println("Starting clustering");
		GameDevDomain.Clustering2();
		GameDevDomain.printTheClusterLabelMap();
		try {
			GameDevDomain.concentrateGraphs();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		//		GameDevDomain.printTheDotFiles();
//		List<File> dotNames= Arrays.asList(folder.listFiles());
//		Iterator iterFiles=dotNames.iterator();
//		while(iterFiles.hasNext()){
//			System.out.println(iterFiles.next().toString());
//		}		
//		try {
//			Scanner scannerByLine = new Scanner(new File(iterFiles.next().toString()));
//			
//			Pattern methodName=Pattern.compile(" [a-zA-Z]*[(]");
//			while(scannerByLine.hasNextLine()){
//				Scanner scanLine = new Scanner(scannerByLine.nextLine());
//				String s=scanLine.findInLine(methodName);
//				if(s != null){
//					s=s.replaceAll("[(]","");
//					System.out.println(s);					
//				}
//			}
//			
//			
//			
//			
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		}
//		
		
		

	}

	
	
	
	
	
}






