package transformations;

import java.util.Scanner;
import java.util.regex.Pattern;

public class MethodExtraction extends GeneralTransform<String>{
	Pattern MethodName;
	
	
	public MethodExtraction(Pattern inputMethodName){
		MethodName=inputMethodName;
	}
	


	@Override
	public
	String transform(String input) {
		String methodName;
		Scanner scanLine = new Scanner(input);
		
		
		if(input!=null){
			methodName=scanLine.findInLine(MethodName).replaceAll("[(]*[$]*[0-9]*","").replaceAll(" ","");
			if(methodName.contains("<init>")|| methodName==null){
				scanLine.close();
				return input;}
			else{
				scanLine.close();
				return methodName;}
		}else{
			scanLine.close();
			return input;
		}
	}

}
