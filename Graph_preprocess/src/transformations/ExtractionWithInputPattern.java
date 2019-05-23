package transformations;

import java.util.Scanner;
import java.util.regex.Pattern;

public class ExtractionWithInputPattern extends GeneralTransform<String> {

	private Pattern questionPattern;
	
	public ExtractionWithInputPattern(Pattern inputPattern){
		questionPattern=inputPattern;
	}
	
	
	@Override
	public String transform(String input) {
		String questionString = null;
		Scanner scanLine = new Scanner(input);
		if(input!=null){
			questionString=scanLine.findInLine(questionPattern);
		}
		scanLine.close();
		return questionString;
	}
}


