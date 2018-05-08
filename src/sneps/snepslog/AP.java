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

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Set;

import java_cup.runtime.Symbol;
import sneps.exceptions.CannotBuildNodeException;
import sneps.exceptions.CannotFindCaseFrameException;
import sneps.exceptions.CaseFrameCannotBeRemovedException;
import sneps.exceptions.CaseFrameMissMatchException;
import sneps.exceptions.CaseFrameWithSetOfRelationsNotFoundException;
import sneps.exceptions.DuplicateContextNameException;
import sneps.exceptions.DuplicatePropositionException;
import sneps.exceptions.EquivalentNodeException;
import sneps.exceptions.IllegalAtomicSymbolException;
import sneps.exceptions.InvalidArgumentsException;
import sneps.exceptions.InvalidWffNameException;
import sneps.exceptions.ModeOneOnlyException;
import sneps.exceptions.ModeThreeOnlyException;
import sneps.exceptions.NodeCannotBeRemovedException;
import sneps.exceptions.NodeNotFoundInNetworkException;
import sneps.exceptions.NodeNotFoundInPropSetException;
import sneps.exceptions.NotAPropositionNodeException;
import sneps.exceptions.RelationDoesntExistException;
import sneps.exceptions.SemanticAlreadySetException;
import sneps.exceptions.SemanticNotFoundInNetworkException;
import sneps.network.Network;
import sneps.network.Node;
import sneps.network.PropositionNode;
import sneps.network.cables.DownCableSet;
import sneps.network.classes.CaseFrame;
import sneps.network.classes.Relation;
import sneps.network.classes.RelationsRestrictedCaseFrame;
import sneps.network.classes.Semantic;
import sneps.network.classes.SemanticHierarchy;
import sneps.network.classes.Wire;
import sneps.network.classes.setClasses.NodeSet;
import sneps.network.classes.setClasses.PropositionSet;
import sneps.network.classes.term.Closed;
import sneps.network.classes.term.Molecular;
import sneps.snebr.Controller;

@SuppressWarnings("deprecation")
public class AP {

	/**
	 * This is a hashtable to store the descriptions of cfs in mode 3.
	 */
	private static Hashtable<String, String> cfsDescriptions = new Hashtable<String, String>();

	/**
	 * This is a hashtable to store the descriptions of nodes in mode 3.
	 */
	private static Hashtable<Node, String> nodesDescriptions = new Hashtable<Node, String>();

	/**
	 * This is a hashtable to store the case frames used in mode 3 where the key is
	 * the name used in creating the case frame.
	 */
	private static Hashtable<String, CaseFrame> modeThreeCaseFrames = new Hashtable<String, CaseFrame>();

	/**
	 * an integer which holds the number of the snepslog mode currently in use. It
	 * is initially set to 1.
	 */
	private static int snepslogMode = 1;

	/**
	 * a String which holds the name of the printing mode currently in use. It is
	 * initially set to normal.
	 */
	private static String printingMode = "normal";

	protected static Hashtable<String, String> getCfsDescriptions() {
		return cfsDescriptions;
	}

	protected static Hashtable<Node, String> getNodesDescriptions() {
		return nodesDescriptions;
	}

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
	 * This method is used to create a customized case frame for mode 1.
	 *
	 * @param noOfArguments
	 *            the number of argument relations.
	 *
	 * @return the case frame after being created.
	 */
	protected static CaseFrame createModeOneCaseFrame(int noOfArguments) {
		LinkedList<Relation> rels = new LinkedList<Relation>();
		Relation r = new Relation("r", "Proposition");
		rels.add(r);
		for (int i = 0; i < noOfArguments; i++) {
			rels.add(new Relation("a" + (i + 1), "Proposition"));
		}
		CaseFrame cf = Network.defineCaseFrame("Proposition", rels);
		return cf;
	}

	/**
	 * This method is used to create a customized case frame for mode 2.
	 * 
	 * @param p
	 *            the name of the p relation.
	 *
	 * @param noOfArguments
	 *            the number of argument relations.
	 *
	 * @return the case frame after being created.
	 */
	protected static CaseFrame createModeTwoCaseFrame(String p, int noOfArguments) {
		LinkedList<Relation> rels = new LinkedList<Relation>();
		Relation r = new Relation("| rel " + p + "|", "Proposition");
		rels.add(r);
		for (int i = 0; i < noOfArguments; i++) {
			rels.add(new Relation("|rel-arg#" + p + (i + 1) + "|", "Proposition"));
		}
		CaseFrame cf = Network.defineCaseFrame("Proposition", rels);
		return cf;
	}

	/**
	 * This method is used to create a case frame for mode 3 and stores it in a
	 * hashtable using the name as key.
	 * 
	 * @param semanticType
	 *            this specifies the semantic type of the case frame.
	 * @param name
	 *            this acts as an identifier for the case frame.
	 * @param relations
	 *            this String contains the relations that is used to create a case
	 *            frame.
	 * @return the case frame after being created.
	 * @throws RelationDoesntExistException
	 *             if a relation was not defined in the Network.
	 */
	protected static CaseFrame createModeThreeCaseFrame(String semanticType, String name, ArrayList<String> relations,
			String description) throws RelationDoesntExistException {
		// check if already exists
		if (modeThreeCaseFrames.containsKey(name)) {
			return modeThreeCaseFrames.get(name);
		}
		LinkedList<Relation> rels = new LinkedList<Relation>();
		if (!relations.get(0).equals("nil")) {
			rels.add(Network.getRelation(relations.get(0)));
		}
		for (int i = 1; i < relations.size(); i++) {
			rels.add(Network.getRelation(relations.get(i)));
		}
		CaseFrame cf = Network.defineCaseFrame(semanticType, rels);
		if (!relations.get(0).equals("nil")) {
			modeThreeCaseFrames.put(name, cf);
		} else {
			modeThreeCaseFrames.put(name + "$", cf);
		}
		if (description != null) {
			cfsDescriptions.put(name, description);
		}
		return cf;
	}

	/**
	 * This method is used to construct the nodes representing an infixedTerm in the
	 * network.
	 * 
	 * @param type
	 *            a String specifying the type of the infixed term. It should have
	 *            one of the following values: and, or, or equality.
	 * @param arg1
	 *            the first argument node.
	 * @param arg2
	 *            the second argument node.
	 * @return a molecular node representing the infixed term.
	 * @throws NodeNotFoundInNetworkException
	 * @throws NotAPropositionNodeException
	 * @throws CaseFrameMissMatchException
	 * @throws EquivalentNodeException
	 * @throws CannotBuildNodeException
	 */
	protected static Node buildInfixedTerm(String type, Node arg1, Node arg2)
			throws NotAPropositionNodeException, NodeNotFoundInNetworkException, CannotBuildNodeException,
			EquivalentNodeException, CaseFrameMissMatchException {
		Network.defineDefaults();
		RelationsRestrictedCaseFrame caseFrame = null;
		ArrayList<Wire> wires = new ArrayList<Wire>();
		switch (type) {
		case "and":
			caseFrame = (RelationsRestrictedCaseFrame) RelationsRestrictedCaseFrame.andOrRule;
			wires.add(new Wire(Relation.max, Network.buildBaseNode("2", Semantic.infimum)));
			wires.add(new Wire(Relation.min, Network.buildBaseNode("2", Semantic.infimum)));
			break;
		case "or":
			caseFrame = (RelationsRestrictedCaseFrame) RelationsRestrictedCaseFrame.andOrRule;
			wires.add(new Wire(Relation.max, Network.buildBaseNode("2", Semantic.infimum)));
			wires.add(new Wire(Relation.min, Network.buildBaseNode("1", Semantic.infimum)));
			break;
		case "equality":
			caseFrame = (RelationsRestrictedCaseFrame) RelationsRestrictedCaseFrame.threshRule;
			wires.add(new Wire(Relation.threshMax, Network.buildBaseNode("1", Semantic.infimum)));
			wires.add(new Wire(Relation.thresh, Network.buildBaseNode("1", Semantic.infimum)));
			break;
		}
		wires.add(new Wire(Relation.arg, arg1));
		wires.add(new Wire(Relation.arg, arg2));
		Node infixedTermNode = Network.buildMolecularNode(wires, caseFrame);
		return infixedTermNode;
	}

	/**
	 * This method is used to construct the nodes representing entailments in the
	 * network.
	 * 
	 * @param entailmentType
	 *            a String specifying the type of the entailment. It should have one
	 *            of the following values: AndEntailment, OrEntailment,
	 *            NumericalEntailment or Implication.
	 * @param antecedents
	 *            an ArrayList of the nodes representing the antecedents.
	 * @param consequents
	 *            an ArrayList of the nodes representing the consequents.
	 * @param optionalI
	 *            a String which contains the value of "i" in case of a numerical
	 *            entailment.
	 * @return a molecular node representing the entailment
	 * @throws CaseFrameMissMatchException
	 * @throws EquivalentNodeException
	 * @throws CannotBuildNodeException
	 * @throws NodeNotFoundInNetworkException
	 * @throws NotAPropositionNodeException
	 */
	protected static Node buildEntailment(String entailmentType, ArrayList<Node> antecedents,
			ArrayList<Node> consequents, String optionalI) throws CannotBuildNodeException, EquivalentNodeException,
			CaseFrameMissMatchException, NotAPropositionNodeException, NodeNotFoundInNetworkException {
		Network.defineDefaults();
		RelationsRestrictedCaseFrame caseFrame = null;
		ArrayList<Wire> wires = new ArrayList<Wire>();
		switch (entailmentType) {
		case "AndEntailment":
			caseFrame = (RelationsRestrictedCaseFrame) RelationsRestrictedCaseFrame.andRule;
			for (int i = 0; i < antecedents.size(); i++) {
				wires.add(new Wire(Relation.andAnt, antecedents.get(i)));
			}
			for (int j = 0; j < consequents.size(); j++) {
				wires.add(new Wire(Relation.cq, consequents.get(j)));
			}
			break;
		case "OrEntailment":
			caseFrame = (RelationsRestrictedCaseFrame) RelationsRestrictedCaseFrame.orRule;
			for (int i = 0; i < antecedents.size(); i++) {
				wires.add(new Wire(Relation.ant, antecedents.get(i)));
			}
			for (int j = 0; j < consequents.size(); j++) {
				wires.add(new Wire(Relation.cq, consequents.get(j)));
			}
			break;
		case "NumericalEntailment":
			caseFrame = (RelationsRestrictedCaseFrame) RelationsRestrictedCaseFrame.numericalRule;
			for (int i = 0; i < antecedents.size(); i++) {
				wires.add(new Wire(Relation.andAnt, antecedents.get(i)));
			}
			for (int j = 0; j < consequents.size(); j++) {
				wires.add(new Wire(Relation.cq, consequents.get(j)));
			}
			wires.add(new Wire(Relation.i, Network.buildBaseNode(optionalI, Semantic.infimum)));
			break;
		case "Implication":
			caseFrame = (RelationsRestrictedCaseFrame) RelationsRestrictedCaseFrame.orRule;
			for (int i = 0; i < antecedents.size(); i++) {
				wires.add(new Wire(Relation.ant, antecedents.get(i)));
			}
			for (int j = 0; j < consequents.size(); j++) {
				wires.add(new Wire(Relation.cq, consequents.get(j)));
			}
			break;
		}
		Node entailmentNode = Network.buildMolecularNode(wires, caseFrame);
		return entailmentNode;
	}

	/**
	 * This method is used to construct the nodes representing a negatedTerm in the
	 * network.
	 * 
	 * @param node
	 *            a node to be negated.
	 * @return a molecular node representing a negatedTerm.
	 * @throws NodeNotFoundInNetworkException
	 * @throws NotAPropositionNodeException
	 * @throws CaseFrameMissMatchException
	 * @throws EquivalentNodeException
	 * @throws CannotBuildNodeException
	 */
	protected static Node buildNegatedTerm(Node node)
			throws NotAPropositionNodeException, NodeNotFoundInNetworkException, CannotBuildNodeException,
			EquivalentNodeException, CaseFrameMissMatchException {
		Network.defineDefaults();
		ArrayList<Wire> wires = new ArrayList<Wire>();
		wires.add(new Wire(Relation.arg, node));
		wires.add(new Wire(Relation.max, Network.buildBaseNode("0", Semantic.infimum)));
		wires.add(new Wire(Relation.min, Network.buildBaseNode("0", Semantic.infimum)));
		RelationsRestrictedCaseFrame caseFrame = (RelationsRestrictedCaseFrame) RelationsRestrictedCaseFrame.andOrRule;
		Node negatedNode = Network.buildMolecularNode(wires, caseFrame);
		return negatedNode;
	}

	/**
	 * This method is used to construct the nodes representing an andTerm in the
	 * network.
	 * 
	 * @param i
	 *            the andor min.
	 * @param j
	 *            the andor max.
	 * @param arguments
	 *            an ArrayList of the nodes representing the arguments.
	 * @return a molecular node representing an andorTerm.
	 * @throws NodeNotFoundInNetworkException
	 * @throws NotAPropositionNodeException
	 * @throws CaseFrameMissMatchException
	 * @throws EquivalentNodeException
	 * @throws CannotBuildNodeException
	 */
	protected static Node buildAndorTerm(String i, String j, ArrayList<Node> arguments)
			throws NotAPropositionNodeException, NodeNotFoundInNetworkException, CannotBuildNodeException,
			EquivalentNodeException, CaseFrameMissMatchException {
		Network.defineDefaults();
		ArrayList<Wire> wires = new ArrayList<Wire>();
		for (int a = 0; a < arguments.size(); a++) {
			wires.add(new Wire(Relation.arg, arguments.get(a)));
		}
		wires.add(new Wire(Relation.max, Network.buildBaseNode(j, Semantic.infimum)));
		wires.add(new Wire(Relation.min, Network.buildBaseNode(i, Semantic.infimum)));
		RelationsRestrictedCaseFrame caseFrame = (RelationsRestrictedCaseFrame) RelationsRestrictedCaseFrame.andOrRule;
		Node andorNode = Network.buildMolecularNode(wires, caseFrame);
		return andorNode;
	}

	/**
	 * This method is used to construct the nodes representing setTerms in the
	 * network.
	 * 
	 * @param type
	 *            a String specifying the type of the setTerm. It should have one of
	 *            the following values: and, or, nand, nor, xor or iff.
	 * @param arguments
	 *            an ArrayList of the nodes representing the arguments.
	 * @return a molecular node representing a setTerm
	 * @throws NodeNotFoundInNetworkException
	 * @throws NotAPropositionNodeException
	 * @throws CaseFrameMissMatchException
	 * @throws EquivalentNodeException
	 * @throws CannotBuildNodeException
	 */
	protected static Node buildSetTerm(String type, ArrayList<Node> arguments)
			throws NotAPropositionNodeException, NodeNotFoundInNetworkException, CannotBuildNodeException,
			EquivalentNodeException, CaseFrameMissMatchException {
		Network.defineDefaults();
		RelationsRestrictedCaseFrame caseFrame = null;
		ArrayList<Wire> wires = new ArrayList<Wire>();
		for (int i = 0; i < arguments.size(); i++) {
			wires.add(new Wire(Relation.arg, arguments.get(i)));
		}
		switch (type) {
		case "and":
			caseFrame = (RelationsRestrictedCaseFrame) RelationsRestrictedCaseFrame.andOrRule;
			wires.add(new Wire(Relation.max, Network.buildBaseNode(arguments.size() + "", Semantic.infimum)));
			wires.add(new Wire(Relation.min, Network.buildBaseNode(arguments.size() + "", Semantic.infimum)));
			break;
		case "or":
			caseFrame = (RelationsRestrictedCaseFrame) RelationsRestrictedCaseFrame.andOrRule;
			wires.add(new Wire(Relation.max, Network.buildBaseNode(arguments.size() + "", Semantic.infimum)));
			wires.add(new Wire(Relation.min, Network.buildBaseNode("1", Semantic.infimum)));
			break;
		case "nand":
			caseFrame = (RelationsRestrictedCaseFrame) RelationsRestrictedCaseFrame.andOrRule;
			wires.add(new Wire(Relation.max, Network.buildBaseNode(arguments.size() - 1 + "", Semantic.infimum)));
			wires.add(new Wire(Relation.min, Network.buildBaseNode("0", Semantic.infimum)));
			break;
		case "nor":
			caseFrame = (RelationsRestrictedCaseFrame) RelationsRestrictedCaseFrame.andOrRule;
			wires.add(new Wire(Relation.max, Network.buildBaseNode("0", Semantic.infimum)));
			wires.add(new Wire(Relation.min, Network.buildBaseNode("0", Semantic.infimum)));
			break;
		case "xor":
			caseFrame = (RelationsRestrictedCaseFrame) RelationsRestrictedCaseFrame.andOrRule;
			wires.add(new Wire(Relation.max, Network.buildBaseNode("1", Semantic.infimum)));
			wires.add(new Wire(Relation.min, Network.buildBaseNode("1", Semantic.infimum)));
			break;
		case "iff":
			caseFrame = (RelationsRestrictedCaseFrame) RelationsRestrictedCaseFrame.threshRule;
			wires.add(new Wire(Relation.threshMax, Network.buildBaseNode(arguments.size() - 1 + "", Semantic.infimum)));
			wires.add(new Wire(Relation.thresh, Network.buildBaseNode("1", Semantic.infimum)));
			break;
		}
		Node setTermNode = Network.buildMolecularNode(wires, caseFrame);
		return setTermNode;
	}

	/**
	 * This method is used to construct the nodes representing a threshTerm in the
	 * network.
	 * 
	 * @param thresh
	 *            the thresh min.
	 * @param threshmax
	 *            the thresh max.
	 * @param arguments
	 *            an ArrayList of the nodes representing the arguments.
	 * @return a molecular node representing a threshTerm.
	 * @throws CaseFrameMissMatchException
	 * @throws EquivalentNodeException
	 * @throws CannotBuildNodeException
	 * @throws NodeNotFoundInNetworkException
	 * @throws NotAPropositionNodeException
	 */
	protected static Node buildThreshTerm(String thresh, String threshmax, ArrayList<Node> arguments)
			throws CannotBuildNodeException, EquivalentNodeException, CaseFrameMissMatchException,
			NotAPropositionNodeException, NodeNotFoundInNetworkException {
		Network.defineDefaults();
		ArrayList<Wire> wires = new ArrayList<Wire>();
		for (int a = 0; a < arguments.size(); a++) {
			wires.add(new Wire(Relation.arg, arguments.get(a)));
		}
		wires.add(new Wire(Relation.max, Network.buildBaseNode(threshmax, Semantic.infimum)));
		wires.add(new Wire(Relation.min, Network.buildBaseNode(thresh, Semantic.infimum)));
		RelationsRestrictedCaseFrame caseFrame = (RelationsRestrictedCaseFrame) RelationsRestrictedCaseFrame.threshRule;
		Node threshNode = Network.buildMolecularNode(wires, caseFrame);
		return threshNode;
	}

	/**
	 * This method is used to construct the nodes representing a SNeRE TERM in the
	 * network.
	 * 
	 * @param type
	 *            a String specifying the type of the SNeRE term. It should have one
	 *            of the following values: ifdo, whendo, wheneverdo, ActPlan,
	 *            Effect, GoalPlan or Precondition.
	 * @param arg1
	 *            the first argument node.
	 * @param arg2
	 *            the second argument node.
	 * @return a molecular node representing the SNeRE term.
	 * @throws CaseFrameMissMatchException
	 * @throws EquivalentNodeException
	 * @throws CannotBuildNodeException
	 * @throws NodeNotFoundInNetworkException
	 * @throws NotAPropositionNodeException
	 */
	protected static Node buildSNeRETerm(String type, Node arg1, Node arg2)
			throws CannotBuildNodeException, EquivalentNodeException, CaseFrameMissMatchException,
			NotAPropositionNodeException, NodeNotFoundInNetworkException {
		Network.defineDefaults();
		RelationsRestrictedCaseFrame caseFrame = null;
		ArrayList<Wire> wires = new ArrayList<Wire>();
		switch (type) {
		case "ifdo":
			caseFrame = (RelationsRestrictedCaseFrame) RelationsRestrictedCaseFrame.doIf;
			wires.add(new Wire(Relation.iff, arg1));
			wires.add(new Wire(Relation.doo, arg2));
			break;
		case "whendo":
			caseFrame = (RelationsRestrictedCaseFrame) RelationsRestrictedCaseFrame.whenDo;
			wires.add(new Wire(Relation.when, arg1));
			wires.add(new Wire(Relation.doo, arg2));
			break;
		case "wheneverdo":
			caseFrame = (RelationsRestrictedCaseFrame) RelationsRestrictedCaseFrame.wheneverDo;
			wires.add(new Wire(Relation.whenever, arg1));
			wires.add(new Wire(Relation.doo, arg2));
			break;
		case "ActPlan":
			caseFrame = (RelationsRestrictedCaseFrame) RelationsRestrictedCaseFrame.planAct;
			wires.add(new Wire(Relation.act, arg1));
			wires.add(new Wire(Relation.plan, arg2));
			break;
		case "Effect":
			caseFrame = (RelationsRestrictedCaseFrame) RelationsRestrictedCaseFrame.actEffect;
			wires.add(new Wire(Relation.act, arg1));
			wires.add(new Wire(Relation.effect, arg2));
			break;
		case "GoalPlan":
			caseFrame = (RelationsRestrictedCaseFrame) RelationsRestrictedCaseFrame.planGoal;
			wires.add(new Wire(Relation.goal, arg1));
			wires.add(new Wire(Relation.plan, arg2));
			break;
		case "Precondition":
			caseFrame = (RelationsRestrictedCaseFrame) RelationsRestrictedCaseFrame.preconditionAct;
			wires.add(new Wire(Relation.act, arg1));
			wires.add(new Wire(Relation.precondition, arg2));
			break;
		}
		Node snereNode = Network.buildMolecularNode(wires, caseFrame);
		return snereNode;
	}

	/**
	 * This method is used to clear the knowledge base entirely.
	 */
	protected static void clearKnowledgeBase() {
		Controller.clearSNeBR();
		Network.clearNetwork();
		SemanticHierarchy.getSemantics().clear();
		cfsDescriptions.clear();
		nodesDescriptions.clear();
		modeThreeCaseFrames.clear();
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
	public static String executeSnepslogCommand(String command) {
		try {
			InputStream is = new ByteArrayInputStream(command.getBytes(StandardCharsets.UTF_8));
			DataInputStream dis = new DataInputStream(is);
			parser parser = new parser(new Lexer(dis));
			parser.command = command;
			Symbol res;
			res = parser.parse();
			String output = (String) res.value;
			is.close();
			dis.close();
			return output;
		} catch (CannotBuildNodeException e) {
			return e.getMessage();
		} catch (CannotFindCaseFrameException e) {
			return e.getMessage();
		} catch (CaseFrameCannotBeRemovedException e) {
			return e.getMessage();
		} catch (CaseFrameMissMatchException e) {
			return e.getMessage();
		} catch (CaseFrameWithSetOfRelationsNotFoundException e) {
			return e.getMessage();
		} catch (DuplicateContextNameException e) {
			return e.getMessage();
		} catch (DuplicatePropositionException e) {
			return e.getMessage();
		} catch (EquivalentNodeException e) {
			return e.getMessage();
		} catch (IllegalAtomicSymbolException e) {
			return e.getMessage();
		} catch (InvalidArgumentsException e) {
			return e.getMessage();
		} catch (InvalidWffNameException e) {
			return e.getMessage();
		} catch (ModeOneOnlyException e) {
			return e.getMessage();
		} catch (ModeThreeOnlyException e) {
			return e.getMessage();
		} catch (NodeCannotBeRemovedException e) {
			return e.getMessage();
		} catch (NodeNotFoundInNetworkException e) {
			return e.getMessage();
		} catch (NodeNotFoundInPropSetException e) {
			return e.getMessage();
		} catch (NotAPropositionNodeException e) {
			return e.getMessage();
		} catch (RelationDoesntExistException e) {
			return e.getMessage();
		} catch (SemanticAlreadySetException e) {
			return e.getMessage();
		} catch (SemanticNotFoundInNetworkException e) {
			return e.getMessage();
		} catch (Exception e) {
			return e.getMessage();
		}
	}

	public static Hashtable<String, CaseFrame> getModeThreeCaseFrames() {
		return modeThreeCaseFrames;
	}

	/**
	 * This method is used to construct the nodes representing withsome and withall
	 * terms in the network.
	 * 
	 * @param type
	 *            a String specifying the type of the term. It should have one of
	 *            the following values: withsome or withall.
	 * @param vars
	 *            an ArrayList of the nodes representing the vars.
	 * @param suchthat
	 *            an ArrayList of the nodes representing the suchthat.
	 * @param doo
	 *            an ArrayList of the nodes representing the doo.
	 * @param elsee
	 *            an ArrayList of the nodes representing the elsee.
	 * @return a molecular node representing the entailment
	 * @throws CaseFrameMissMatchException
	 * @throws EquivalentNodeException
	 * @throws CannotBuildNodeException
	 * @throws NodeNotFoundInNetworkException
	 * @throws NotAPropositionNodeException
	 */
	protected static Node buildWithsomeAllTerm(String type, ArrayList<Node> vars, ArrayList<Node> suchthat,
			ArrayList<Node> doo, ArrayList<Node> elsee)
			throws NotAPropositionNodeException, NodeNotFoundInNetworkException, CannotBuildNodeException,
			EquivalentNodeException, CaseFrameMissMatchException {
		Network.defineDefaults();
		RelationsRestrictedCaseFrame caseFrame = null;
		ArrayList<Wire> wires = new ArrayList<Wire>();
		switch (type) {
		case "withsome":
			wires.add(new Wire(Relation.action, Network.buildBaseNode("withsome", Semantic.act)));
			for (int i = 0; i < vars.size(); i++) {
				wires.add(new Wire(Relation.vars, vars.get(i)));
			}
			for (int j = 0; j < suchthat.size(); j++) {
				wires.add(new Wire(Relation.suchthat, suchthat.get(j)));
			}
			for (int k = 0; k < doo.size(); k++) {
				wires.add(new Wire(Relation.doo, doo.get(k)));
			}
			if (elsee != null) {
				for (int x = 0; x < elsee.size(); x++) {
					wires.add(new Wire(Relation.elsee, elsee.get(x)));
				}
				caseFrame = (RelationsRestrictedCaseFrame) RelationsRestrictedCaseFrame.withSome;
			} else {
				caseFrame = (RelationsRestrictedCaseFrame) RelationsRestrictedCaseFrame.withSomeNoElse;
			}
			break;
		case "withall":
			wires.add(new Wire(Relation.action, Network.buildBaseNode("withall", Semantic.act)));
			for (int i = 0; i < vars.size(); i++) {
				wires.add(new Wire(Relation.vars, vars.get(i)));
			}
			for (int j = 0; j < suchthat.size(); j++) {
				wires.add(new Wire(Relation.suchthat, suchthat.get(j)));
			}
			for (int k = 0; k < doo.size(); k++) {
				wires.add(new Wire(Relation.doo, doo.get(k)));
			}
			if (elsee != null) {
				for (int x = 0; x < elsee.size(); x++) {
					wires.add(new Wire(Relation.elsee, elsee.get(x)));
				}
				caseFrame = (RelationsRestrictedCaseFrame) RelationsRestrictedCaseFrame.withAll;
			} else {
				caseFrame = (RelationsRestrictedCaseFrame) RelationsRestrictedCaseFrame.withAllNoElse;
			}
			break;
		}
		Node node = Network.buildMolecularNode(wires, caseFrame);
		return node;
	}

	/**
	 * This method is used to construct the nodes representing an allTerm in the
	 * network.
	 * 
	 * @param vars
	 *            an ArrayList of the nodes representing the vars.
	 * @param wff
	 *            a node representing the scope of the quantifier.
	 * @return a molecular node representing the allTerm
	 * @throws EquivalentNodeException
	 * @throws CannotBuildNodeException
	 * @throws NodeCannotBeRemovedException
	 * @throws RelationDoesntExistException
	 * @throws NodeNotFoundInNetworkException
	 * @throws NotAPropositionNodeException
	 */
	protected static Node buildAllTerm(ArrayList<Node> vars, Node wff)
			throws CannotBuildNodeException, EquivalentNodeException, NodeCannotBeRemovedException,
			RelationDoesntExistException, NotAPropositionNodeException, NodeNotFoundInNetworkException {
		LinkedList<Relation> relations = new LinkedList<Relation>();
		Relation forAll = Network.defineRelation("forall", "Infimum");
		relations.add(forAll);
		Molecular molecular = (Molecular) wff.getTerm();
		for (int i = 0; i < molecular.getDownCableSet().getCaseFrame().getRelations().size(); i++) {
			relations.add(molecular.getDownCableSet().getCaseFrame().getRelations().get(i));
		}
		CaseFrame caseFrame = Network.defineCaseFrame(molecular.getDownCableSet().getCaseFrame().getSemanticClass(),
				relations);
		ArrayList<Wire> wires = new ArrayList<Wire>();
		for (int j = 0; j < vars.size(); j++) {
			wires.add(new Wire(forAll, vars.get(j)));
		}
		DownCableSet downCableSet = molecular.getDownCableSet();
		Set<String> keys = downCableSet.getDownCables().keySet();
		for (String key : keys) {
			NodeSet ns = downCableSet.getDownCables().get(key).getNodeSet();
			for (int k = 0; k < ns.size(); k++) {
				wires.add(new Wire(Network.getRelation(key), ns.getNode(k)));
			}
		}
		Node node = Network.buildMolecularNode(wires, caseFrame);
		Molecular m = (Molecular) node.getTerm();
		ArrayList<String> varNames = new ArrayList<String>();
		for (int i = 0; i < vars.size(); i++) {
			varNames.add(vars.get(i).getIdentifier());
		}
		replaceWithVars(varNames, m.getDownCableSet());
		Network.removeNode(wff);
		return node;
	}

	private static void replaceWithVars(ArrayList<String> varNames, DownCableSet downCableSet) {
		Set<String> keys = downCableSet.getDownCables().keySet();
		for (String key : keys) {
			NodeSet ns = downCableSet.getDownCables().get(key).getNodeSet();
			for (int i = ns.size() - 1; i >= 0; i--) {
				Node node = ns.getNode(i);
				if (node.getTerm() instanceof Molecular) {
					Molecular m = (Molecular) node.getTerm();
					replaceWithVars(varNames, m.getDownCableSet());
				} else {
					String identifier = node.getIdentifier();
					ns.removeNode(node);
					ns.addNode(Network.buildVariableNode(identifier));
				}
			}
		}
	}

	/**
	 * This method converts a group of Nodes into a String representation according
	 * to the printing mode in use.
	 * 
	 * @param wffs
	 *            an ArrayList of some nodes.
	 * @return a String holding the representation.
	 * @throws NotAPropositionNodeException
	 * @throws NodeNotFoundInNetworkException
	 */
	public static String displayWffs(ArrayList<Node> wffs)
			throws NotAPropositionNodeException, NodeNotFoundInNetworkException {
		String output = "";
		switch (AP.getPrintingMode()) {
		case "normal":
			for (int i = 0; i < wffs.size(); i++) {
				if (wffs.get(i) instanceof PropositionNode && wffs.get(i).getTerm() instanceof Closed) {
					PropositionNode pNode = (PropositionNode) wffs.get(i);
					String temp = "";
					if (Controller.getCurrentContext().isAsserted(pNode)) {
						temp += "!";
					}
					output += "WFF" + wffs.get(i).getIdentifier().substring(1) + temp + ": " + wffs.get(i).toString()
							+ '\n';
				}
			}
			break;
		case "expert":
			for (int i = 0; i < wffs.size(); i++) {
				if (wffs.get(i) instanceof PropositionNode && wffs.get(i).getTerm() instanceof Closed) {
					PropositionNode pNode = (PropositionNode) wffs.get(i);
					String temp = "";
					if (Controller.getCurrentContext().isAsserted(pNode)) {
						temp += "!";
					}
					output += "WFF" + wffs.get(i).getIdentifier().substring(1) + temp + ": " + wffs.get(i).toString()
							+ '\n';
					output += pNode.getBasicSupport().toString() + '\n';
				}
			}
			break;
		case "unlabeled":
			for (int i = 0; i < wffs.size(); i++) {
				if (wffs.get(i) instanceof PropositionNode && wffs.get(i).getTerm() instanceof Closed) {
					PropositionNode pNode = (PropositionNode) wffs.get(i);
					String temp = "";
					if (Controller.getCurrentContext().isAsserted(pNode)) {
						temp += "!";
					}
					output += wffs.get(i).toString() + '\n';
				}
			}
			break;
		}
		return output;
	}

	/**
	 * A method to convert an ArrayList of Nodes to a PropositionSet.
	 */
	protected static PropositionSet arrayListToPropositionSet(ArrayList<Node> nodes)
			throws NotAPropositionNodeException, NodeNotFoundInNetworkException {
		int[] props = new int[nodes.size()];
		for (int i = 0; i < nodes.size(); i++) {
			props[i] = nodes.get(i).getId();
		}
		return new PropositionSet(props);
	}

	/**
	 * A method to convert a PropositionSet to an ArrayList of Nodes.
	 */
	protected static ArrayList<Node> propositionSetToArrayList(PropositionSet set)
			throws NotAPropositionNodeException, NodeNotFoundInNetworkException {
		ArrayList<Node> nodes = new ArrayList<Node>();
		int[] props = PropositionSet.getPropsSafely(set);
		for (int i = 0; i < props.length; i++) {
			nodes.add(Network.getNodeById(props[i]));
		}
		return nodes;
	}

	/**
	 * A method returning the closed nodes from an ArrayList of Nodes.
	 */
	protected static ArrayList<Node> getClosed(ArrayList<Node> nodes) {
		ArrayList<Node> closed = new ArrayList<>();
		for (int i = 0; i < nodes.size(); i++) {
			if (nodes.get(i).getTerm() instanceof Closed) {
				closed.add(nodes.get(i));
			}
		}
		return closed;
	}

	/**
	 * A method returning all the closed nodes from the Network.
	 */
	protected static ArrayList<Node> getAllClosedNodesFromTheNetwork() {
		ArrayList<Node> closed = new ArrayList<>();
		Set<String> keys = Network.getNodes().keySet();
		for (String key : keys) {
			if (Network.getNodes().get(key).getTerm() instanceof Closed) {
				closed.add(Network.getNodes().get(key));
			}
		}
		return closed;
	}

	/**
	 * A method returning the asserted nodes dominating the nodes in the given
	 * ArrayList.
	 * 
	 * @throws NodeNotFoundInNetworkException
	 * @throws NotAPropositionNodeException
	 */
	protected static ArrayList<Node> beliefsAbout(ArrayList<Node> nodes)
			throws NotAPropositionNodeException, NodeNotFoundInNetworkException {
		ArrayList<Node> dominatingTerms = new ArrayList<>();
		for (int i = 0; i < nodes.size(); i++) {
			NodeSet parents = nodes.get(i).getParentNodes();
			for (int j = 0; j < parents.size(); j++) {
				dominatingTerms.add(parents.getNode(j));
			}
		}
		ArrayList<Node> beliefs = new ArrayList<>();
		for (int k = 0; k < dominatingTerms.size(); k++) {
			if (Controller.getCurrentContext().isAsserted((PropositionNode) dominatingTerms.get(k))) {
				beliefs.add(dominatingTerms.get(k));
			}
		}
		return beliefs;
	}

	/**
	 * A method returning the description of some given nodes.
	 */
	protected static String describeTerms(ArrayList<Node> nodes) {
		String result = "";
		if (AP.getSnepslogMode() != 3) {
			return result;
		}
		for (int i = 0; i < nodes.size(); i++) {
			if (nodesDescriptions.get(nodes.get(i)) != null) {
				result += nodesDescriptions.get(nodes.get(i)) + '\n';
			}
		}
		return result;
	}

	/**
	 * A method that loads some commands from a file and execute them.
	 */
	protected static String loadFile(String path) {
		ArrayList<String> commands = new ArrayList<String>();
		try {
			BufferedReader br = new BufferedReader(new FileReader(path));
			String line = null;
			while ((line = br.readLine()) != null) {
				commands.add(line);
			}
			br.close();
		} catch (IOException e) {
			return "Error reading the file at: " + path;
		}
		String output = "";
		for (int i = 0; i < commands.size(); i++) {
			try {
				output += executeSnepslogCommand(commands.get(i)) + '\n';
			} catch (Exception e) {
				return "Error executing the command: " + commands.get(i);
			}
		}
		return output;
	}

	/**
	 * Docs goes here
	 */
	protected static Semantic defineSemantic(String identifier, String parent, ArrayList<String> children) {
		// TODO A wrapper for defineSemantic
		return SemanticHierarchy.createSemanticType(identifier);
	}

	/**
	 * Docs goes here
	 */
	protected static ArrayList<Node> match(Node node) {
		// TODO A wrapper for match
		ArrayList<Node> output = new ArrayList<Node>();
		output.add(node);
		return output;
	}

	/**
	 * Docs goes here
	 */
	protected static void activateTracing(String type) {
		// TODO A wrapper for setting tacing variables to true
		switch (type) {
		case "inference":
			break;
		case "acting":
			break;
		case "parsing":
			break;
		}
	}

	/**
	 * Docs goes here
	 */
	protected static void deactivateTracing(String type) {
		// TODO A wrapper for setting tracing variables to false
		switch (type) {
		case "inference":
			break;
		case "acting":
			break;
		case "parsing":
			break;
		}
	}

	/**
	 * Docs goes here
	 */
	protected static ArrayList<Node> deduce(Node node, String type, int i, int j) {
		// TODO A wrapper for deduce
		ArrayList<Node> output = new ArrayList<Node>();
		switch (type) {
		case "ask":
			break;
		case "askifnot":
			break;
		case "askwh":
			break;
		case "askwhnot":
			break;
		case "?":
			// Handle the optional i and j parameters if there
			break;
		}
		return output;
	}

	/**
	 * Docs goes here
	 */
	protected static void clearInfer() {
		// TODO A wrapper for the method clearing the acg
	}

	/**
	 * Docs goes here
	 */
	protected static String getBrMode() {
		// TODO A wrapper for the method returning the current belief revision mode.
		return "";
	}

	/**
	 * Docs goes here
	 */
	protected static void setBrMode(String mode) {
		// TODO A wrapper for the method setting the belief revision mode.
	}

	/**
	 * Docs goes here
	 */
	protected static void perform(Node node) {
		// TODO A wrapper for perform.
	}

	/**
	 * Docs goes here
	 */
	protected static void removeFromContext(String context, PropositionSet hyps) {
		// TODO A wrapper for the method removing hyps from context.
	}

	/**
	 * Docs goes here
	 */
	protected static ArrayList<Node> forwardInference(Node node, String type) {
		// TODO A wrapper for forward inference
		ArrayList<Node> output = new ArrayList<Node>();
		switch (type) {
		case "activate":
			break;
		case "activate!":
			break;
		case "!":
			break;
		}
		return output;
	}

}
