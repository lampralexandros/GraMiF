package Tests;

import java.util.Scanner;
import java.util.regex.Pattern;

public class ScannerTest {

	public ScannerTest() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {
		String input=null;
		String className;
		Pattern ClassName=Pattern.compile("[.]*[a-zA-Z]*[$]*[0-9]*[:]");
		
		if(input!=null){
			Scanner scanLine=new Scanner(input);
			if(input.contains("<init>")){
				//className=scanLine.findInLine(ClassName).replaceAll(".","").replaceAll(":","").replaceAll(" ","");
				className=scanLine.findInLine(ClassName).replaceAll("[:]*[.]*[$]*[0-9]*","");
				//className=className.substring(1,className.length());
				scanLine.close();
			
			}else{
				scanLine.close();
				
			}
		}else{
			
		}
	}

}
