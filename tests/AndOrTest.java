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
import sneps.network.classes.term.Molecular;
import sneps.network.classes.term.Open;
import sneps.network.classes.term.Variable;
import sneps.setClasses.FlagNodeSet;
import sneps.setClasses.NodeSet;
import sneps.setClasses.PropositionSet;
import sneps.snebr.Context;
import sneps.snebr.Controller;
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
	private static Node fido, var, dog, barks;
	private static Node prop1, prop2, prop3, prop4;
	private static RuleUseInfo rui;
	private static Report report;
	private static Report report1;
	private static Report report2;
	private static Report report3;
	private static Report report4;
	private static Report report5;
	private static Report report6;
	private static Report report7;
	private static Report report8;
	private static Report report9;
	private static Report report10;
	private static Report report11;
	private static Report report12;
	private static Report report13;
	private static Report report14;
	private static Report report15;
	private static Report report16;
	private static Report report17;
	private static Report report18;
	private static Report report19;
	private static Report report20;
	private static Report report21;
	private static Report report22;
	private static Report report23;
	private static Report report24;
	private static Report report25;
	private static Report report26;
	private static Report report27;
	private static Report report28;

	@BeforeClass
 	public static void setUpBeforeClass() throws Exception {
		try {
			context = Controller.createContext(contextName);
		} catch (DuplicateContextNameException e1) {
			assertNotNull(e1.getMessage(), e1);
		}
		
		
		/**
		 * Create substitutions,
		 * FlagNodeSet,
		 * FlagNode,
		 * PropositionSet,
		 * wires,
		 * relations,
		 * caseFrames
		 */
		
		
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
		rels.add(memberRel);	rels.add(classRel);
		CaseFrame caseFrameMC = Network.defineCaseFrame("MemberClass", rels);
		rels.clear();		rels.add(classRel);		rels.add(doesRel);
		CaseFrame caseFrameCD = Network.defineCaseFrame("ClassDoes", rels);
		rels.clear();		rels.add(memberRel);		rels.add(doesRel);
 		CaseFrame caseFrameMD = Network.defineCaseFrame("MemberDoes", rels);
		rels.clear();		rels.add(antsRel);		rels.add(consRel);
 		CaseFrame caseFrameAC = Network.defineCaseFrame("AntsCons", rels);
		Wire wire1 = null, wire2 = null, wire3 = null, wire4 = null;
		rels.clear();
		
		
		
		
		/**
		 * Building propositions & base nodes,
		 * adding wires
		 */
		
		try {
			var = Network.buildVariableNode("X");
			fido = Network.buildBaseNode("Fido", new Semantic("Base"));
			dog = Network.buildBaseNode("Dog", new Semantic("Proposition"));//MolecularNode(wires, caseFrame);
			barks = Network.buildBaseNode("Barks", new Semantic("Proposition"));//MolecularNode(wires, caseFrame);
			wire1 = new Wire(memberRel, fido);
			wire2 = new Wire(classRel, dog);
			wire3 = new Wire(doesRel, barks);
			wire4 = new Wire(memberRel, var);
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
		} catch (CannotBuildNodeException | EquivalentNodeException
				| NotAPropositionNodeException | NodeNotFoundInNetworkException e1) {
			assertNotNull(e1.getMessage(), e1);
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
			prop1 = new Node(new Open("Prop1", dcs));
			dcList.clear();
			//------------------------------------------------------------//
			nodeSet1.clear();		nodeSet1.addNode(dog);
			dc1 = new DownCable(classRel, nodeSet1);
			dcList.add(dc1);
			nodeSet1.clear();		nodeSet1.addNode(barks);
			dc1 = new DownCable(doesRel, nodeSet1);
			dcList.add(dc1);
			dcs = new DownCableSet(dcList, caseFrameCD); 
			prop2 = new Node(new Open("Prop2", dcs));
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
			dcList.clear();
			//------------------------------------------------------------//
			nodeSet1.clear();		nodeSet1.addNode(fido);
			dc1 = new DownCable(memberRel, nodeSet1);
			dcList.add(dc1);
			nodeSet1.clear();		nodeSet1.addNode(barks);
			dc1 = new DownCable(doesRel, nodeSet1);
			dcList.add(dc1);
			dcs = new DownCableSet(dcList, caseFrameMD); 
			prop4 = new Node(new Open("Prop4", dcs));
			dcList.clear();
		}
		
		
		
		
		try {
			support.add(prop1.getId());
		} catch (DuplicatePropositionException | NotAPropositionNodeException
				| NodeNotFoundInNetworkException e) {
			assertNotNull(e.getMessage(), e);
		}
		fn = new FlagNode(prop1, support, 1);
		fns.insert(fn);

		support.clearSet();
		try {
			support.add(prop2.getId());
		} catch (DuplicatePropositionException | NotAPropositionNodeException
				| NodeNotFoundInNetworkException e) {
			assertNotNull(e.getMessage(), e);
		}
		fn = new FlagNode(prop2, support, 1);
		fns.insert(fn);

		support.clearSet();
		try {
			support.add(prop3.getId());
		} catch (DuplicatePropositionException | NotAPropositionNodeException
				| NodeNotFoundInNetworkException e) {
			assertNotNull(e.getMessage(), e);
		}
		fn = new FlagNode(prop3, support, 1);
		fns.insert(fn);

		nodeSet.addNode(prop1);
		dc.add(new DownCable(antsRel, nodeSet));

		nodeSet.clear();
		nodeSet.addNode(prop2);
		dc.add(new DownCable(antsRel, nodeSet));

		nodeSet.clear();
		nodeSet.addNode(prop3);
		dc.add(new DownCable(antsRel, nodeSet));

		nodeSet.clear();
		nodeSet.addNode(prop4);
		dc.add(new DownCable(consRel, nodeSet));

		DownCableSet dcs = new DownCableSet(dc, caseFrameAC);
		
		/**
		 * AndOr-Entailment
		 */
		
		
		andor = new AndOrEntailment(new Open("Open", dcs));

		try {
			support.add(dog.getId());
			support.add(fido.getId());
			support.add(var.getId());
		} catch (DuplicatePropositionException | NotAPropositionNodeException
				| NodeNotFoundInNetworkException e) {
			assertNotNull(e.getMessage(), e);
		}

		sub.insert(new Binding((VariableNode) var,fido));
		rui = new RuleUseInfo(sub, 1, 0, fns);
		report = new Report(sub, support, true, contextName);
		report1 = new Report(sub, support, true, contextName);
		report2 = new Report(sub, support, true, contextName);
		report3 = new Report(sub, support, true, contextName);
		report4 = new Report(sub, support, false, contextName);
		report5 = new Report(sub, support, false, contextName);
		report6 = new Report(sub, support, false, contextName);
		report7 = new Report(sub, support, false, contextName);
		report8 = new Report(sub, support, false, contextName);
		report9 = new Report(sub, support, false, contextName);
	
		
		report10 = new Report(sub, support, true, contextName);
		report11 = new Report(sub, support, true, contextName);
		report12 = new Report(sub, support, true, contextName);
		report13 = new Report(sub, support, true, contextName);
		report14 = new Report(sub, support, true, contextName);
		report15 = new Report(sub, support, true, contextName);
		report16 = new Report(sub, support, true, contextName);
		report17 = new Report(sub, support, true, contextName);
		report18 = new Report(sub, support, false, contextName);
		report19 = new Report(sub, support, false, contextName);
		
		
		report20 = new Report(sub, support, true, contextName);
		report21 = new Report(sub, support, false, contextName);
		report22 = new Report(sub, support, false, contextName);
		report23 = new Report(sub, support, false, contextName);
		report24 = new Report(sub, support, false, contextName);
		report25 = new Report(sub, support, false, contextName);
		report26 = new Report(sub, support, false, contextName);
		report27 = new Report(sub, support, false, contextName);
		report28 = new Report(sub, support, false, contextName);
	
		andor.setAndOrArgs(10);
		andor.setAndOrMax(5);
		andor.setAndOrMin(3);
	}
	
	
	@Test
	public void test() {
		andor.setAndOrArgs(10);
		andor.setAndOrMax(5);
		andor.setAndOrMin(3);
		andor.applyRuleHandler(report, fido);
		andor.applyRuleHandler(report1, fido);
		andor.applyRuleHandler(report2, fido);
		andor.applyRuleHandler(report3, fido);
		andor.applyRuleHandler(report4, fido);
		andor.applyRuleHandler(report5, fido);
		andor.applyRuleHandler(report6, fido);
		andor.applyRuleHandler(report7, fido);
		andor.applyRuleHandler(report8, fido);
		andor.applyRuleHandler(report9, fido);
		//assertEquals(4, andor.getPos());
		//assertEquals(6, andor.getNeg());
		assertEquals(3,andor.getAndOrMin());
		assertEquals(5,andor.getAndOrMax());
		assertEquals(10,andor.getAndOrArgs());
		assertEquals(true, andor.isSign());
		andor.clrAll();
	}
	
	@Test
	public void testTwo() {
		andor.setAndOrArgs(10);
		andor.setAndOrMax(5);
		andor.setAndOrMin(3);
		andor.applyRuleHandler(report10, fido);
		andor.applyRuleHandler(report11, fido);
		andor.applyRuleHandler(report12, fido);
		andor.applyRuleHandler(report13, fido);
		andor.applyRuleHandler(report14, fido);
		andor.applyRuleHandler(report15, fido);
		andor.applyRuleHandler(report16, fido);
		andor.applyRuleHandler(report17, fido);
		andor.applyRuleHandler(report18, fido);
		andor.applyRuleHandler(report19, fido);
		//assertEquals(8, andor.getPos());
		//assertEquals(2, andor.getNeg());
		assertEquals(3,andor.getAndOrMin());
		assertEquals(5,andor.getAndOrMax());
		assertEquals(10,andor.getAndOrArgs());
		assertEquals(false, andor.isSign());
		andor.clrAll();
	}
	
	@Test
	public void testThree() {
		andor.setAndOrArgs(10);
		andor.setAndOrMax(5);
		andor.setAndOrMin(3);
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
		assertEquals(3,andor.getAndOrMin());
		assertEquals(5,andor.getAndOrMax());
		assertEquals(10,andor.getAndOrArgs());
		assertEquals(false, andor.isSign());
		andor.clrAll();
	}
	
	
	
}