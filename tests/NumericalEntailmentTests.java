import java.util.ArrayList;
import java.util.LinkedList;

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
import sneps.network.classes.Relation;
import sneps.network.classes.Semantic;
import sneps.network.classes.Wire;
import sneps.network.classes.term.Open;
import sneps.network.classes.term.Variable;
import sneps.setClasses.FlagNodeSet;
import sneps.setClasses.NodeSet;
import sneps.setClasses.PropositionSet;
import sneps.snebr.Context;
import sneps.snebr.Controller;
import sneps.snip.Report;
import sneps.snip.classes.FlagNode;
import sneps.snip.classes.RuisHandler;
import sneps.snip.classes.RuleUseInfo;
import sneps.snip.classes.SIndex;
import sneps.snip.matching.Binding;
import sneps.snip.matching.LinearSubstitutions;
import sneps.snip.rules.NumericalEntailment;
import junit.framework.TestCase;

public class NumericalEntailmentTests extends TestCase {
	private static Context context;
	private static String contextName = "TempContext";
	private static NumericalEntailment numerical;
	private static Node fido, var, dog, barks;
	private static Node prop1, prop2, prop3, prop4;
	private static RuleUseInfo rui;
	private static Report report;

 	public void setUp() {
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
		Relation antsRel = Network.defineRelation("iant", "&ant");
		Relation consRel = Network.defineRelation("iconsq", "&consq");
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

//-------------------------------------- fido, dog, barks, X ---------------------------------------------//

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

//------------------------------------- prop1, prop2, prop3, prop4 ----------------------------------------------//

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
			//------------------------------------------------------------//
		}

//------------------------------------- Numerical Supports ----------------------------------------------//

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

//------------------------------------- AND ----------------------------------------------//

		numerical = new NumericalEntailment(new Open("Open", dcs));
		numerical.setI(1);

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
	}

	@Test
	public void testApplyRuleHandler() {
		numerical.setKnownInstances(numerical.getNewInstances());
		numerical.getNewInstances().clear();

		numerical.applyRuleHandler(report, fido);
		if(numerical.getAntSize() <= 1)
			assertNotNull("NumericalEntailment: ApplyRuleHandler doesn't broadcast reports",
					numerical.getNewInstances());
		else
			assertTrue("NumericalEntailment: ApplyRuleHandler broacdcasts final report without waiting for enough positive antecedents reports",
					numerical.getNewInstances().isEmpty());


		numerical.setKnownInstances(numerical.getNewInstances());
		numerical.getNewInstances().clear();
		LinearSubstitutions sub = new LinearSubstitutions();
		FlagNodeSet fns = new FlagNodeSet();
		PropositionSet support = new PropositionSet();

		try {
			support.add(dog.getId());
		} catch (DuplicatePropositionException | NotAPropositionNodeException
				| NodeNotFoundInNetworkException e) {}

		FlagNode fn = new FlagNode(dog, support, 1);
		fns.insert(fn);
		report = new Report(sub, support, true, contextName);

		numerical.applyRuleHandler(report, dog);
		if(numerical.getAntSize() >= numerical.getI())
			assertTrue(
					"NumericalEntailment: ApplyRuleHandler doesn't broadcast reports",
					numerical.getNewInstances().isEmpty());
	}

	@Test
	public void testGetDownAntNodeSet() {
		NodeSet downAntNodeSet = numerical.getDownAntNodeSet();
		assertNotNull("NumericalEntailment: getDownAntNodeSet retuns null", 
				downAntNodeSet);
		assertFalse("NumericalEntailment: getDownAntNodeSet retuns an empty NodeSet", 
				downAntNodeSet.isEmpty());
	}

	@Test
	public void testCreateRuisHandler() {
		numerical.createRuisHandler(contextName);
		RuisHandler handler = numerical.getContextRuiHandler(contextName);
		assertNotNull("NumericalEntailment: CreateRuisHandler creats a null RuisHandler", 
				handler);
		assertTrue("NumericalEntailment: CreateRuisHandler doesn't create an SIndex as a Handler", 
				handler instanceof SIndex);
	}

	@Test
	public void testAddNotSentRui() {
		numerical.addNotSentRui(rui, contextName, dog);
		assertNotNull("NumericalEntailment: addNotSentRui doesn't add a RuiHandler in contextRuisHandlers", 
				numerical.getContextRuiHandler(contextName));

		NodeSet positives = numerical.getContextRuiHandler(contextName).getPositiveNodes();
		assertTrue("NumericalEntailment: addNotSentRui doesn't add signature to positiveNodes set", 
				positives.contains(dog));
		assertTrue("NumericalEntailment: addNotSentRui adds wrong signatures to positiveNodes set", 
				!positives.contains(fido));

		assertTrue("AndEntailment: addNotSentRui doesn't add an SIndex in contextRuisHandlers", 
				numerical.getContextRuiHandler(contextName)instanceof SIndex);

	}

	public void tearDown(){
		Network.clearNetwork();
		numerical.clear();
		fido = null;
		var = null;
		dog = null;
		rui = null;
		report = null;
	}
}
