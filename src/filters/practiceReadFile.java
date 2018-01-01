package filters;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class practiceReadFile {

	public void getRules() throws FileNotFoundException {
		Scanner s = new Scanner(new File("rulesFile.txt"));
		System.out.println(s.next());
		s.close();
	}
	
	public static void main(String[] args) throws FileNotFoundException {
			(new practiceReadFile()).getRules();
		}
}
