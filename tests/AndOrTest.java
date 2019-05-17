import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.LinkedList;

import org.junit.BeforeClass;
import org.junit.Test;

import sneps.exceptions.CannotBuildNodeException;
import sneps.exceptions.DuplicateContextNameException;
import sneps.exceptions.DuplicatePropositionException;
import sneps.exceptions.EquivalentNodeException;
import sneps.exceptions.IllegalIdentifierException;
import sneps.exceptions.NodeNotFoundInNetworkException;
import sneps.exceptions.NotAPropositionNodeException;
import sneps.network.Network;
import sneps.network.Node;
import sneps.network.VariableNode;
import sneps.network.cables.DownCable;
import sneps.network.cables.DownCableSet;
import sneps.network.classes.CaseFrame;
import sneps.network.classes.RCFP;
import sneps.network.classes.Relation;
import sneps.network.classes.RelationsRestrictedCaseFrame;
import sneps.network.classes.Semantic;
import sneps.network.classes.Wire;
import sneps.network.classes.term.Closed;
import sneps.network.classes.term.Molecular;
import sneps.network.classes.term.Open;
import sneps.network.classes.term.Variable;
import sneps.network.classes.setClasses.FlagNodeSet;
import sneps.network.classes.setClasses.NodeSet;
import sneps.network.classes.setClasses.PropositionSet;
import sneps.snebr.Context;
import sneps.snebr.Controller;
import sneps.snip.InferenceTypes;
import sneps.snip.Report;
import sneps.snip.classes.FlagNode;
import sneps.snip.classes.RuleUseInfo;
import sneps.snip.matching.Binding;
import sneps.snip.matching.LinearSubstitutions;
import sneps.snip.rules.AndOrEntailment;
import sneps.snip.rules.OrEntailment;

public class AndOrTest {
	
	private static Context context;
	private static String contextName = "TempContext";
	private static AndOrEntailment andor;
	private static Node fido, var, dog, barks, animal, veg, mineral;
	private static Node prop1, prop2, prop3, prop4, prop5, prop6, prop7;
	private static Report report;
	private static Report report1, report2, report3, report4, report5, report6, 
	report7, report8, report9, report10, report11, report12, report13, report14, 
	report15, report16, report17, report18, report19, report20, report21, report22, 
	report23, report24, report25, report26, report27, report28;

	@BeforeClass
 	public static void setUp() throws Exception {
		try {
			context = Controller.createContext(contextName);
		} catch (DuplicateContextNameException e1) {
			assertNotNull(e1.getMessage(), e1);
		}
		
		LinearSubstitutions sub = new LinearSubstitutions();
		FlagNodeSet fns = new FlagNodeSet();
		FlagNode fn;
		PropositionSet support = new PropositionSet();
 		ArrayList<Wire> wires = new ArrayList<Wire>();
 		LinkedList<DownCable> dc = new LinkedList<DownCable>();
		LinkedList<Relation> rels = new LinkedList<Relation>();
		NodeSet nodeSet = new NodeSet();
		Relation memberRel = Network.defineRelation("Member", "NodeSet");
		Relation classRel = Network.defineRelation("Class", "NodeSet");
		Relation doesRel = Network.defineRelation("Does", "NodeSet");
		Relation antsRel = Network.defineRelation("Xant", "Xant");
		Relation consRel = Network.defineRelation("Xconsq", "Xconsq");
		Relation argsRel = Network.defineRelation("arg", "arg");
		rels.add(memberRel);	rels.add(classRel);
		CaseFrame caseFrameMC = Network.defineCaseFrame("MemberClass", rels);
		rels.clear();		rels.add(classRel);		rels.add(doesRel);
		CaseFrame caseFrameCD = Network.defineCaseFrame("ClassDoes", rels);
		rels.clear();		rels.add(memberRel);		rels.add(doesRel);
 		CaseFrame caseFrameMD = Network.defineCaseFrame("MemberDoes", rels);
		rels.clear();		rels.add(antsRel);		rels.add(consRel);
 		CaseFrame caseFrameAC = Network.defineCaseFrame("AntsCons", rels);
 		rels.clear();		rels.add(argsRel);
 		CaseFrame caseFrameArgs = Network.defineCaseFrame("Args", rels);
		Wire wire1 = null, wire2 = null, wire3 = null, wire4 = null;
		Wire wire5 = null, wire6 = null, wire7 = null;
		rels.clear();
		
		
		try {
			var = Network.buildVariableNode("X");
			fido = Network.buildBaseNode("Fido", new Semantic("Base"));
			dog = Network.buildBaseNode("Dog", new Semantic("Base"));
			barks = Network.buildBaseNode("Barks", new Semantic("Base"));
			wire1 = new Wire(memberRel, fido);
			wire2 = new Wire(classRel, dog);
			wire3 = new Wire(doesRel, barks);
			wire4 = new Wire(memberRel, var);
			
			animal = Network.buildBaseNode("Animal", new Semantic("Base"));
			veg = Network.buildBaseNode("Vegetable", new Semantic("Base"));
			mineral = Network.buildBaseNode("Mineral", new Semantic("Base"));
			wire5 = new Wire(classRel, animal);
			wire6 = new Wire(classRel, veg);
			wire7 = new Wire(classRel, mineral);
		} catch (IllegalIdentifierException | NotAPropositionNodeException 
				| NodeNotFoundInNetworkException e1) {
			assertNotNull(e1.getMessage(), e1);
			var = new VariableNode(new Variable("X"));
		}
		
		
		try {
			wires.clear();	wires.add(wire1);	wires.add(wire2);
			prop1 = Network.buildMolecularNode(wires, caseFrameMC);

			wires.clear();	wires.add(wire2);	wires.add(wire3);
			prop2 = Network.buildMolecularNode(wires, caseFrameCD);

			wires.clear();	wires.add(wire4);	wires.add(wire2);
			prop3 = Network.buildMolecularNode(wires, caseFrameMC);

			wires.clear();	wires.add(wire1);	wires.add(wire3);
			prop4 = Network.buildMolecularNode(wires, caseFrameMD);
			
			wires.clear();	wires.add(wire4);	wires.add(wire5);
			prop5 = Network.buildMolecularNode(wires, caseFrameArgs);
			
			wires.clear();	wires.add(wire4);	wires.add(wire6);
			prop6 = Network.buildMolecularNode(wires, caseFrameArgs);
			
			wires.clear();	wires.add(wire4);	wires.add(wire7);
			prop7 = Network.buildMolecularNode(wires, caseFrameArgs);
		} catch (CannotBuildNodeException | EquivalentNodeException
				| NotAPropositionNodeException | NodeNotFoundInNetworkException e1) {
			assertNotNull(e1.getMessage(), e1);
		}
		
		LinkedList<DownCable> dcList = new LinkedList<DownCable>();
		NodeSet nodeSet1 = new NodeSet();
		DownCable dc1;	DownCableSet dcs;

		nodeSet1.addNode(fido);
		dc1 = new DownCable(memberRel, nodeSet1);
		dcList.add(dc1);
		nodeSet1.clear();		nodeSet1.addNode(dog);
		dc1 = new DownCable(classRel, nodeSet1);
		dcList.add(dc1);
		dcs = new DownCableSet(dcList, caseFrameMC); 
		prop1 = new Node(new Closed("Prop1", dcs));
		dcList.clear();
		//------------------------------------------------------------//
		nodeSet1.clear();		nodeSet1.addNode(dog);
		dc1 = new DownCable(classRel, nodeSet1);
		dcList.add(dc1);
		nodeSet1.clear();		nodeSet1.addNode(barks);
		dc1 = new DownCable(doesRel, nodeSet1);
		dcList.add(dc1);
		dcs = new DownCableSet(dcList, caseFrameCD); 
		prop2 = new Node(new Closed("Prop2", dcs));
		dcList.clear();
		//------------------------------------------------------------//
		nodeSet1.clear();		nodeSet1.addNode(var);
		dc1 = new DownCable(memberRel, nodeSet1);
		dcList.add(dc1);
		nodeSet1.clear();		nodeSet1.addNode(dog);
		dc1 = new DownCable(classRel, nodeSet1);
		dcList.add(dc1);
		dcs = new DownCableSet(dcList, caseFrameMC); 
		prop3 = new Node(new Open("Prop3", dcs));
		((Open) (prop3.getTerm())).getFreeVariables().addVarNode((VariableNode) var);
		dcList.clear();
		//------------------------------------------------------------//
		nodeSet1.clear();		nodeSet1.addNode(fido);
		dc1 = new DownCable(memberRel, nodeSet1);
		dcList.add(dc1);
		nodeSet1.clear();		nodeSet1.addNode(barks);
		dc1 = new DownCable(doesRel, nodeSet1);
		dcList.add(dc1);
		dcs = new DownCableSet(dcList, caseFrameMD); 
		prop4 = new Node(new Closed("Prop4", dcs));
		dcList.clear();
		//------------------------------------------------------------//
		nodeSet1.clear();		nodeSet1.addNode(var);
		dc1 = new DownCable(memberRel, nodeSet1);
		dcList.add(dc1);
		nodeSet1.clear();		nodeSet1.addNode(animal);
		dc1 = new DownCable(classRel, nodeSet1);
		dcList.add(dc1);
		dcs = new DownCableSet(dcList, caseFrameMC); 
		prop5 = new Node(new Open("Prop5", dcs));
		((Open) (prop5.getTerm())).getFreeVariables().addVarNode((VariableNode) var);
		dcList.clear();
		//------------------------------------------------------------//
		nodeSet1.clear();		nodeSet1.addNode(var);
		dc1 = new DownCable(memberRel, nodeSet1);
		dcList.add(dc1);
		nodeSet1.clear();		nodeSet1.addNode(veg);
		dc1 = new DownCable(classRel, nodeSet1);
		dcList.add(dc1);
		dcs = new DownCableSet(dcList, caseFrameMC); 
		prop6 = new Node(new Open("Prop6", dcs));
		((Open) (prop6.getTerm())).getFreeVariables().addVarNode((VariableNode) var);
		dcList.clear();
		//------------------------------------------------------------//
		nodeSet1.clear();		nodeSet1.addNode(var);
		dc1 = new DownCable(memberRel, nodeSet1);
		dcList.add(dc1);
	    nodeSet1.clear();		nodeSet1.addNode(mineral);
		dc1 = new DownCable(classRel, nodeSet1);
		dcList.add(dc1);
		dcs = new DownCableSet(dcList, caseFrameMC); 
		prop7 = new Node(new Open("Prop7", dcs));
		((Open) (prop7.getTerm())).getFreeVariables().addVarNode((VariableNode) var);
		dcList.clear();
				
//---------------------------------------------------------------------------------//

		/*nodeSet.addNode(prop1);
		dc.add(new DownCable(argsRel, nodeSet));

		nodeSet.clear();
		nodeSet.addNode(prop2);
		dc.add(new DownCable(argsRel, nodeSet));

		nodeSet.clear();
		nodeSet.addNode(prop3);
		dc.add(new DownCable(argsRel, nodeSet));

		nodeSet.clear();
		nodeSet.addNode(prop4);
		dc.add(new DownCable(argsRel, nodeSet));*/
		
		nodeSet.addNode(prop5);
		nodeSet.addNode(prop6);
		nodeSet.addNode(prop7);
		dc.add(new DownCable(argsRel, nodeSet));

		DownCableSet dcss = new DownCableSet(dc, caseFrameArgs);
		
		NodeSet a = new NodeSet();
		a.addNode(prop5);
		a.addNode(prop6);
		a.addNode(prop7);

//---------------------------- ANDOR -----------------------------------//
		
		andor = new AndOrEntailment(new Open("Open", dcss));
		andor.setAntecedents(a);
		andor.setMax(2);
		andor.setMin(1);
		
		sub = new LinearSubstitutions();
		support = new PropositionSet();
		/*try {
		support.add(prop5.getId());
		} catch (DuplicatePropositionException | NotAPropositionNodeException 
				| NodeNotFoundInNetworkException e) {
		e.printStackTrace();
		}*/
		sub.putIn(new Binding((VariableNode) var, fido));
		report = new Report(sub, support, true, InferenceTypes.BACKWARD);
		
		support = new PropositionSet();
		/*try {
		support.add(prop6.getId());
		} catch (DuplicatePropositionException | NotAPropositionNodeException 
				| NodeNotFoundInNetworkException e) {
		e.printStackTrace();
		}*/
		report1 = new Report(sub, support, true, InferenceTypes.BACKWARD);
	}
	
	@Test
	public void test() {
		andor.applyRuleHandler(report, prop5);
		assertEquals(0, andor.getReplies().size());
		
		andor.applyRuleHandler(report1, prop6);
		assertEquals(1, andor.getReplies().size());
	}
	
	@Test
	public void test2() {
		
	}
	
	/*@Test
	public void test3() {
		andor.applyRuleHandler(report20, fido);
		andor.applyRuleHandler(report21, fido);
		andor.applyRuleHandler(report22, fido);
		andor.applyRuleHandler(report23, fido);
		andor.applyRuleHandler(report24, fido);
		andor.applyRuleHandler(report25, fido);
		andor.applyRuleHandler(report26, fido);
		andor.applyRuleHandler(report27, fido);
		andor.applyRuleHandler(report28, fido);
		//assertEquals(1, andor.getPos());
		//assertEquals(8, andor.getNeg());
	}*/
	
	public void tearDown() {
		Network.clearNetwork();
		andor.clear();
	}
}