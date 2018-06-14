package sneps.snepslog.cup;

import java_cup.Main;

public class MainCup {

	public static void main(String[] args) throws Exception  { 
		String[] argv = new String[3];
		argv[0] = "-destdir";
		argv[1] = "src/sneps/snepslog";
		argv[2] = "src/sneps/snepslog/parser.cup";
		
		Main.main(argv);
    }
	
}
