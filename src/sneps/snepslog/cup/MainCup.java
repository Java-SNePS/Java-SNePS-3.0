package sneps.snepslog.cup;

import java_cup.Main;

public class MainCup {

	public static void main(String[] args) throws Exception  { 
		String[] argv = new String[5];
		argv[0] = "-destdir";
		argv[1] = "src/sneps/snepslog";
		argv[2] = "-expect";
		argv[3] = "5";
		argv[4] = "src/sneps/snepslog/parser.cup";
		
		Main.main(argv);
    }
	
}
