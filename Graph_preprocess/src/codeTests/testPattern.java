package codeTests;

import java.util.Scanner;
import java.util.regex.Pattern;

public class testPattern {

	public testPattern() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {
		String methodName;
		Scanner scanLine = new Scanner("<testsPacman.Pacman1.src.Enemy: void random_direction(testsPacman.Pacman1.src.PacmanGame$Things[][])>");
		methodName=scanLine.findInLine(Pattern.compile(" [[<]*[a-zA-Z]*[$]*[0-9]*[>]*[_]*]*[(]"));
		//not null
		if(methodName.contains("_")){
			char[] temp=methodName.toCharArray();
			temp[methodName.indexOf("_")+1]=Character.toUpperCase(temp[methodName.indexOf("_")+1]);
			methodName=new String(temp);
					}
		methodName=methodName.replaceAll("[(]*[$]*[0-9]*[_]*","").replaceAll(" ","");
	}

}
