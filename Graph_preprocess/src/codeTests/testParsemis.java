package codeTests;


public class testParsemis {


	public static void main(String[] args) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		String[] gspanArgs= new String[3];
		gspanArgs[0] = "--graphFile=C:\\SPB_Data\\git\\GraMiF\\Graph_preprocess\\resultsfolder2019_06_17_21_12\\testingConcat.dot";

		gspanArgs[1] = "--outputFile=C:\\SPB_Data\\git\\GraMiF\\Graph_preprocess\\resultsfolder2019_06_17_21_12\\testingResults.dot";
		gspanArgs[2] = "--minimumFrequency=2%";
	
		de.parsemis.Miner.run(gspanArgs);
	}

}
