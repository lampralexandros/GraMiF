import java.io.File;

public class TestLodingResultsFromGspan {

	public static void main(String[] args) {
		File folder=new File("results_from_gspan");
		DotFileProcessTree GameDevDomain=new DotFileProcessTree(folder);
		GameDevDomain.printTheDotFiles();
		GameDevDomain.dotProcessCreateTreesFromGspan();
	}

}
