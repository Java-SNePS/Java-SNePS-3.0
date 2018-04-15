/**
 * @className AP.java
 * 
 * @ClassDescription This is the class that acts as an interface to the snepslog 
 *  parser. It contains some static fields and some helper methods used to make 
 *  changes in the backend.
 * 
 * @author Mostafa El-assar
 * @version 3.00 1/4/2018
 */
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

	/**
	 * an integer which holds the number of the snepslog mode currently in use. It 
	 * is initially set to 1.
	 */
	private static int snepslogMode = 1;
	
	/**
	 * a String which holds the name of the printing mode currently in use. It 
	 * is initially set to normal.
	 */
	private static String printingMode = "normal";

	/**
	 * @return an int representing the number of the snepslog mode currently in use.
	 */
	protected static int getSnepslogMode() {
		return snepslogMode;
	}

	/**
	 * @param snepslogMode
	 *            the number of the snepslog mode to be used.
	 */
	protected static void setSnepslogMode(int snepslogMode) {
		AP.snepslogMode = snepslogMode;
	}

	/**
	 * @return a String representing the name of the printing mode currently in use.
	 */
	protected static String getPrintingMode() {
		return printingMode;
	}

	/**
	 * @param printingMode
	 *            the name of the printing mode to be used.
	 */
	protected static void setPrintingMode(String printingMode) {
		AP.printingMode = printingMode;
	}

	/**
	 * This method is used to create a customized case frame for mode 1 
	 *  with semantic type "Individual".
	 *
	 * @param noOfArguments
	 *            the number of argument relations.
	 *
	 * @throws CustomException
	 *             if the case frame is already created.
	 */
	protected static CaseFrame createModeOneIndividualCaseFrame(int noOfArguments) throws CustomException {
		LinkedList<Relation> rels = new LinkedList<Relation>();
		Relation r = new Relation("r", "Individual");
		rels.add(r);
		for (int i = 0; i < noOfArguments; i++) {
			rels.add(new Relation("a" + (i + 1), "Individual"));
		}
		CaseFrame cf = Network.defineCaseFrame("Individual", rels);
		return cf;
	}

	/**
	 * This method is used to create a customized case frame for mode 1 
	 *  with semantic type "Proposition".
	 *
	 * @param noOfArguments
	 *            the number of argument relations.
	 *
	 * @throws CustomException
	 *             if the case frame is already created.
	 */
	protected static CaseFrame createModeOnePropositionCaseFrame(int noOfArguments) throws CustomException {
		LinkedList<Relation> rels = new LinkedList<Relation>();
		Relation r = new Relation("rp", "Individual");
		rels.add(r);
		for (int i = 0; i < noOfArguments; i++) {
			rels.add(new Relation("ap" + (i + 1), "Individual"));
		}
		CaseFrame cf = Network.defineCaseFrame("Proposition", rels);
		return cf;
	}

	
	/**
	 * This method is used to create a customized case frame for mode 2 
	 *  with semantic type "Individual".
	 * @param p
	 *            the name of the p relation.
	 *
	 * @param noOfArguments
	 *            the number of argument relations.
	 *
	 * @throws CustomException
	 *             if the case frame is already created.
	 */
	protected static CaseFrame createModeTwoIndividualCaseFrame(String p, int noOfArguments) throws CustomException {
		LinkedList<Relation> rels = new LinkedList<Relation>();
		Relation r = new Relation("| rel " + p + "|", "Individual");
		rels.add(r);
		for (int i = 0; i < noOfArguments; i++) {
			rels.add(new Relation("|rel-arg#" + p + (i + 1) + "|", "Individual"));
		}
		CaseFrame cf = Network.defineCaseFrame("Individual", rels);
		return cf;
	}

	/**
	 * This method is used to create a customized case frame for mode 2 
	 *  with semantic type "Proposition".
	 * @param p
	 *            the name of the p relation.
	 *
	 * @param noOfArguments
	 *            the number of argument relations.
	 *
	 * @throws CustomException
	 *             if the case frame is already created.
	 */
	protected static CaseFrame createModeTwoPropositionCaseFrame(String p, int noOfArguments) throws CustomException {
		LinkedList<Relation> rels = new LinkedList<Relation>();
		Relation r = new Relation("| rel " + p + "|", "Individual");
		rels.add(r);
		for (int i = 0; i < noOfArguments; i++) {
			rels.add(new Relation("|rel-arg#" + p + (i + 1) + "|", "Individual"));
		}
		CaseFrame cf = Network.defineCaseFrame("Proposition", rels);
		return cf;
	}
	
	/**
	 * This method is used to clear the knowledge base entirely.
	 */
	protected static void clearKnowledgeBase() {
		// TODO Finish building clearKnowledgeBase()
	}
	
	/**
	 * This method is used to execute a snepslog command.
	 * 
	 * @param command
	 *            a String holding the command that is to be executed.
	 * 
	 * @return a String representing the output of that command.
	 * 
	 * @throws Exception
	 *             if the command is syntactically incorrect.
	 */
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
