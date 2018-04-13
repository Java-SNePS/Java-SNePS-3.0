package sneps.snepslog;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;

import java_cup.runtime.Symbol;
import sneps.exceptions.CustomException;
import sneps.network.Network;
import sneps.network.classes.CaseFrame;
import sneps.network.classes.Relation;

@SuppressWarnings("deprecation")
public class AP {

	private static int snepslogMode = 1;
	private static String printingMode = "normal";

	protected static int getSnepslogMode() {
		return snepslogMode;
	}

	protected static void setSnepslogMode(int snepslogMode) {
		AP.snepslogMode = snepslogMode;
	}

	protected static String getPrintingMode() {
		return printingMode;
	}

	protected static void setPrintingMode(String printingMode) {
		AP.printingMode = printingMode;
	}

	protected static CaseFrame createModeOneCaseFrame(int noOfArguments) throws CustomException {
		LinkedList<Relation> rels = new LinkedList<Relation>();
		Relation r = new Relation("r", "Entity");
		rels.add(r);
		for (int i = 0; i < noOfArguments; i++) {
			rels.add(new Relation("a" + (i + 1), "Entity"));
		}
		CaseFrame cf = Network.defineCaseFrame("Entity", rels);
		return cf;
	}

	protected static CaseFrame createModeTwoCaseFrame(String p, int noOfArguments) throws CustomException {
		LinkedList<Relation> rels = new LinkedList<Relation>();
		Relation r = new Relation("| rel " + p + "|", "Entity");
		rels.add(r);
		for (int i = 0; i < noOfArguments; i++) {
			rels.add(new Relation("|rel-arg#" + p + (i + 1) + "|", "Entity"));
		}
		CaseFrame cf = Network.defineCaseFrame("Entity", rels);
		return cf;
	}

	public static String executeSnepslogCommand(String command) throws Exception {
		InputStream is = new ByteArrayInputStream(command.getBytes(StandardCharsets.UTF_8));
		DataInputStream dis = new DataInputStream(is);
		parser parser = new parser(new Lexer(dis));
		Symbol res = parser.parse();
		String output = (String) res.value;
		is.close();
		dis.close();
		return output;
	}

}
