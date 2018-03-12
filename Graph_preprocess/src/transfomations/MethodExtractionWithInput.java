package transfomations;

import java.util.Scanner;
import java.util.regex.Pattern;

public class MethodExtractionWithInput extends GeneralTransform<String>{
	Pattern MethodName;
	String repString;
	
	
	public MethodExtractionWithInput(Pattern inputMethodName,String inputString){
		MethodName=inputMethodName;
		repString=inputString;
	}
	


	@Override
	public
	String transform(String input) {
		String methodName;
		Scanner scanLine = new Scanner(input);
		
		
		if(input!=null){
			methodName=scanLine.findInLine(MethodName);
			if(methodName !=null)
				methodName.replaceAll("[(]*[$]*[0-9]*","").replaceAll(" ","");
				scanLine.close();
			//if(methodName.contains("<init>") || methodName==null)
			if(methodName!=null){
				if(methodName.contains("<init>") )
					return input;
				else
					return repString;}
			else{
				return input;
			}
		}else{
			scanLine.close();
			return input;
		}
	}

}
