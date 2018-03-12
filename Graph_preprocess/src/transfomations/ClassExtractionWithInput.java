package transfomations;

import java.util.Scanner;

/**
 * This class is an implementation of General transform.
 * The basic function is to provide a way to use a string name as wild card
 * @author Alexandros Lampridis
 *
 */


public class ClassExtractionWithInput extends GeneralTransform<String>{
	String wildCard;
	public ClassExtractionWithInput(String inputName){
		wildCard=inputName;
	}
	
	
	
	@Override
	public String transform(String input) {
		Scanner scanLine=new Scanner(input);
		if(input!=null){
			if(input.contains("<init>")){
				scanLine.close();
				return wildCard;
			}else{
				scanLine.close();
				return input;
			}
		}else{
			scanLine.close();
			return wildCard;
		}	
	}
	
}
