package transfomations;

import java.util.Scanner;
import java.util.regex.Pattern;

public class ClassExtraction extends GeneralTransform<String> {

	Pattern ClassName;
	
	public ClassExtraction(Pattern inputClassName){
		ClassName=inputClassName;
	}
	
	
	
	@Override
	public String transform(String input) {
		String className;
		Scanner scanLine=new Scanner(input);
		
		if(input!=null){
			if(input.contains("<init>")){
				//className=scanLine.findInLine(ClassName).replaceAll(".","").replaceAll(":","").replaceAll(" ","");
				className=scanLine.findInLine(ClassName).replaceAll("[:]*[.]*[$]*[0-9]*","");
				//className=className.substring(1,className.length());
				scanLine.close();
				return className;
			}else{
				scanLine.close();
				return input;
			}
		}else{
			scanLine.close();
			return input;
		}
		
	}
	

}
