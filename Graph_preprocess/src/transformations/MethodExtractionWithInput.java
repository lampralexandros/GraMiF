package transformations;

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
		
		
		
		if(input!=null){
			Scanner scanLine = new Scanner(input);
			methodName=scanLine.findInLine(MethodName);
			scanLine.close();
			// not needed due to replacement with repstring
			//if(methodName !=null)
			//	methodName.replaceAll("[(]*[$]*[0-9]*","").replaceAll(" ","");
				
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
			//scanLine.close();
			return input;
		}
	}

}
