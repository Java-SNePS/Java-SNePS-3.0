import java.util.ArrayList;
import java.util.LinkedList;

import junit.framework.TestCase;

import org.junit.Test;
import org.junit.After;
import org.junit.Before;

import sneps.exceptions.CannotBuildNodeException;
import sneps.exceptions.DuplicateContextNameException;
import sneps.exceptions.DuplicatePropositionException;
import sneps.exceptions.EquivalentNodeException;
import sneps.exceptions.IllegalIdentifierException;
import sneps.exceptions.NodeNotFoundInNetworkException;
import sneps.exceptions.NotAPropositionNodeException;
import sneps.network.Network;
import sneps.network.Node;
import sneps.network.PropositionNode;
import sneps.network.VariableNode;
import sneps.network.cables.DownCable;
import sneps.network.cables.DownCableSet;
import sneps.network.classes.CaseFrame;
import sneps.network.classes.Relation;
import sneps.network.classes.Semantic;
import sneps.network.classes.Wire;
import sneps.network.classes.term.Base;
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
import sneps.snip.classes.PTree;
import sneps.snip.classes.RuisHandler;
import sneps.snip.classes.RuleUseInfo;
import sneps.snip.matching.Binding;
import sneps.snip.matching.LinearSubstitutions;
import sneps.snip.rules.AndEntailment;


public class AndEntailmentTests extends TestCase {
	private static Context context;
	private static String contextName = "TempContext";
	private static AndEntailment and;
	private static Node fido, var1, var2, dog, barks;
	private static Node man, woman, married, husband, john, mary, steve, sue;
	private static Node prop1, prop2, prop3, prop4;
	private static Report report, report1, report2, report3, report4, report5;

	@Before
	public void setUp() {
 		try {
			context = Controller.createContext(contextName);
		} catch (DuplicateContextNameException e1) {
			assertNotNull(e1.getMessage(), e1);
		}

		LinearSubstitutions sub = new LinearSubstitutions();
		PropositionSet support = new PropositionSet();
 		ArrayList<Wire> wires = new ArrayList<Wire>();
 		LinkedList<DownCable> dc = new LinkedList<DownCable>();
		LinkedList<Relation> rels = new LinkedList<Relation>();
		NodeSet nodeSet = new NodeSet();
		NodeSet nodeSett = new NodeSet();
		Relation memberRel = Network.defineRelation("Member", "NodeSet");
		Relation classRel = Network.defineRelation("Class", "NodeSet");
		Relation doesRel = Network.defineRelation("Does", "NodeSet");
		Relation antsRel = Network.defineRelation("&ant", "&ant");
		Relation consRel = Network.defineRelation("&consq", "&consq");
		rels.add(memberRel);	rels.add(classRel);
		CaseFrame caseFrameMC = Network.defineCaseFrame("MemberClass", rels);
		rels.clear();		rels.add(classRel);		rels.add(doesRel);
		rels.clear();		rels.add(antsRel);		rels.add(consRel);
 		CaseFrame caseFrameAC = Network.defineCaseFrame("AntsCons", rels);
		Wire wire1 = null, wire2 = null, wire3 = null, wire4 = null, wire5 = null;
		Wire wire6 = null, wire7 = null, wire8 = null, wire9 = null, wire10 = null;
		rels.clear();

//----------------------- SETTING UP NODES --------------------------------//

		try {
			var1 = Network.buildVariableNode("X");
			var2 = Network.buildVariableNode("Y");
			
			man = Network.buildBaseNode("Man", new Semantic("Class"));
			woman = Network.buildBaseNode("Woman", new Semantic("Class"));
			married = Network.buildBaseNode("Married", new Semantic("Class"));
			husband = Network.buildBaseNode("Husband", new Semantic("Class"));
			john = Network.buildBaseNode("John", new Semantic("Class"));
			mary = Network.buildBaseNode("Mary", new Semantic("Class"));
			steve = Network.buildBaseNode("Steve", new Semantic("Class"));
			sue = Network.buildBaseNode("Sue", new Semantic("Class"));
			wire5 = new Wire(memberRel, var1);
			wire6 = new Wire(memberRel, var2);
			wire7 = new Wire(classRel, man);
			wire8 = new Wire(classRel, woman);
			wire9 = new Wire(classRel, married);
			wire10 = new Wire(classRel, husband);
		} catch (IllegalIdentifierException | NotAPropositionNodeException 
				| NodeNotFoundInNetworkException e1) {
			assertNotNull(e1.getMessage(), e1);
			var1 = new VariableNode(new Variable("X"));
			var2 = new VariableNode(new Variable("Y"));
			man = new Node(new Base("Man"));
			woman = new Node(new Base("Woman"));
			married = new Node(new Base("Married"));
			husband = new Node(new Base("Husband"));
			john = new Node(new Base("John"));
			mary = new Node(new Base("Mary"));
			steve = new Node(new Base("Steve"));
			sue = new Node(new Base("Sue"));
		}

//---------------------- PROPOSITION NODES SETUP -------------------------------//

		try {
			wires.clear();	wires.add(wire5);	wires.add(wire7);
			prop1 = Network.buildMolecularNode(wires, caseFrameMC);

			wires.clear();	wires.add(wire6);	wires.add(wire8);
			prop2 = Network.buildMolecularNode(wires, caseFrameMC);

			wires.clear();	wires.add(wire5);	wires.add(wire6);	wires.add(wire9);
			prop3 = Network.buildMolecularNode(wires, caseFrameMC);
			
			wires.clear();	wires.add(wire5);	wires.add(wire6);	wires.add(wire10);
			prop4 = Network.buildMolecularNode(wires, caseFrameMC);
		} catch (CannotBuildNodeException | EquivalentNodeException
				| NotAPropositionNodeException | NodeNotFoundInNetworkException e1) {
			assertNotNull(e1.getMessage(), e1);
			LinkedList<DownCable> dcList = new LinkedList<DownCable>();
			NodeSet nodeSet1 = new NodeSet();
			NodeSet nodeSet2 = new NodeSet();
			NodeSet nodeSet3 = new NodeSet();
			NodeSet nodeSet4 = new NodeSet();
			NodeSet nodeSet5 = new NodeSet();
			NodeSet nodeSet6 = new NodeSet();
			NodeSet nodeSet7 = new NodeSet();
			NodeSet nodeSet8 = new NodeSet();
			DownCable dc1;	DownCableSet dcs;

			nodeSet1.addNode(var1);
			dc1 = new DownCable(memberRel, nodeSet1);
			dcList.add(dc1);
			nodeSet2.addNode(man);
			dc1 = new DownCable(classRel, nodeSet2);
			dcList.add(dc1);
			dcs = new DownCableSet(dcList, caseFrameMC); 
			prop1 = new PropositionNode(new Open("Prop1", dcs));
			//((Open) (prop1.getTerm())).getFreeVariables().addVarNode((VariableNode) var1);
			dcList.clear();
			//------------------------------------------------------------//
			nodeSet3.addNode(var2);
			dc1 = new DownCable(memberRel, nodeSet3);
			dcList.add(dc1);
			nodeSet4.addNode(woman);
			dc1 = new DownCable(classRel, nodeSet4);
			dcList.add(dc1);
			dcs = new DownCableSet(dcList, caseFrameMC); 
			prop2 = new PropositionNode(new Open("Prop2", dcs));
			//((Open) (prop2.getTerm())).getFreeVariables().addVarNode((VariableNode) var2);
			dcList.clear();
			//------------------------------------------------------------//
			nodeSet5.addNode(var1);		nodeSet5.addNode(var2);
			dc1 = new DownCable(memberRel, nodeSet5);
			dcList.add(dc1);
			nodeSet6.addNode(married);
			dc1 = new DownCable(classRel, nodeSet6);
			dcList.add(dc1);
			dcs = new DownCableSet(dcList, caseFrameMC);
			prop3 = new PropositionNode(new Open("Prop3", dcs));
			//((Open) (prop3.getTerm())).getFreeVariables().addVarNode((VariableNode) var1);
			//((Open) (prop3.getTerm())).getFreeVariables().addVarNode((VariableNode) var2);
			dcList.clear();
			//------------------------------------------------------------//
			nodeSet7.addNode(var1);		nodeSet7.addNode(var2);
			dc1 = new DownCable(memberRel, nodeSet7);
			dcList.add(dc1);
			nodeSet8.addNode(husband);
			dc1 = new DownCable(classRel, nodeSet8);
			dcList.add(dc1);
			dcs = new DownCableSet(dcList, caseFrameMC);
			prop4 = new PropositionNode(new Open("Prop4", dcs));
			//((Open) (prop4.getTerm())).getFreeVariables().addVarNode((VariableNode) var1);
			//((Open) (prop4.getTerm())).getFreeVariables().addVarNode((VariableNode) var2);
			dcList.clear();
			//------------------------------------------------------------//
		}

//----------------------- AND SETUP -----------------------------------//
		nodeSet.addNode(prop1);
		nodeSet.addNode(prop2);
		nodeSet.addNode(prop3);
		dc.add(new DownCable(antsRel, nodeSet));
		
		nodeSett.addNode(prop4);
		dc.add(new DownCable(consRel, nodeSett));

		DownCableSet dcs = new DownCableSet(dc, caseFrameAC);

//------------------------ AND ---------------------------//

		and = new AndEntailment(new Open("Open", dcs));

		sub.putIn(new Binding((VariableNode) var1, john));
		support = new PropositionSet();
		try {
			support.add(prop1.getId());
		} catch (DuplicatePropositionException | NotAPropositionNodeException
				| NodeNotFoundInNetworkException e) {
			assertNotNull(e.getMessage(), e);
		}
		report = new Report(sub, support, true, InferenceTypes.BACKWARD);
		
		LinearSubstitutions sub1 = new LinearSubstitutions();
		FlagNodeSet fns1 = new FlagNodeSet();
		PropositionSet support1 = new PropositionSet();
		FlagNode fn1;
		try {
			support1.add(prop2.getId());
		} catch (DuplicatePropositionException | NotAPropositionNodeException
				| NodeNotFoundInNetworkException e) {
			assertNotNull(e.getMessage(), e);
		}
		fn1 = new FlagNode(prop2, support1, 1);
		fns1.insert(fn1);
		sub1.putIn(new Binding((VariableNode) var2, mary));
		report1 = new Report(sub1, support1, true, InferenceTypes.BACKWARD);
		
		LinearSubstitutions sub2 = new LinearSubstitutions();
		FlagNodeSet fns2 = new FlagNodeSet();
		PropositionSet support2 = new PropositionSet();
		FlagNode fn2;
		try {
			support2.add(prop1.getId());
		} catch (DuplicatePropositionException | NotAPropositionNodeException
				| NodeNotFoundInNetworkException e) {
			assertNotNull(e.getMessage(), e);
		}
		fn2 = new FlagNode(prop1, support2, 1);
		fns2.insert(fn2);
		sub2.putIn(new Binding((VariableNode) var1, steve));
		report2 = new Report(sub2, support2, true, InferenceTypes.BACKWARD);
		
		LinearSubstitutions sub3 = new LinearSubstitutions();
		FlagNodeSet fns3 = new FlagNodeSet();
		PropositionSet support3 = new PropositionSet();
		FlagNode fn3;
		try {
			support3.add(prop2.getId());
		} catch (DuplicatePropositionException | NotAPropositionNodeException
				| NodeNotFoundInNetworkException e) {
			assertNotNull(e.getMessage(), e);
		}
		fn3 = new FlagNode(prop2, support3, 1);
		fns3.insert(fn3);
		sub3.putIn(new Binding((VariableNode) var2, sue));
		report3 = new Report(sub3, support3, true, InferenceTypes.BACKWARD);
		
		LinearSubstitutions sub4 = new LinearSubstitutions();
		FlagNodeSet fns4 = new FlagNodeSet();
		PropositionSet support4 = new PropositionSet();
		FlagNode fn4;
		try {
			support4.add(prop3.getId());
		} catch (DuplicatePropositionException | NotAPropositionNodeException
				| NodeNotFoundInNetworkException e) {
			assertNotNull(e.getMessage(), e);
		}
		fn4 = new FlagNode(prop3, support4, 1);
		fns4.insert(fn4);
		sub4.putIn(new Binding((VariableNode) var1, john));
		sub4.putIn(new Binding((VariableNode) var2, mary));
		report4 = new Report(sub4, support4, true, InferenceTypes.BACKWARD);
		
		LinearSubstitutions sub5 = new LinearSubstitutions();
		FlagNodeSet fns5 = new FlagNodeSet();
		PropositionSet support5 = new PropositionSet();
		FlagNode fn5;
		try {
			support5.add(prop3.getId());
		} catch (DuplicatePropositionException | NotAPropositionNodeException
				| NodeNotFoundInNetworkException e) {
			assertNotNull(e.getMessage(), e);
		}
		fn5 = new FlagNode(prop3, support5, 1);
		fns5.insert(fn5);
		sub5.putIn(new Binding((VariableNode) var1, steve));
		sub5.putIn(new Binding((VariableNode) var2, sue));
		report5 = new Report(sub5, support5, true, InferenceTypes.BACKWARD);
	}
	
	@Test
	public void testGetDownAntNodeSet() {
		NodeSet downAntNodeSet = and.getDownAntNodeSet();
		assertNotNull("AndEntailment: getDownAntNodeSet retuns null", 
				downAntNodeSet);
		assertFalse("AndEntailment: getDownAntNodeSet retuns an empty NodeSet", 
				downAntNodeSet.isEmpty());
	}
	
	@Test
	public void testAntsandConsq() {
		and.processNodes(and.getAntecedents());
		assertEquals(1, and.getConsequents().size());
		assertEquals(3, and.getAntecedents().size());
		assertEquals(3, and.getAntsWithVars().size());
		assertEquals(0, and.getAntsWithoutVars().size());
	}

	@Test
	public void testApplyRuleHandler() {
		// Man(john)
		and.applyRuleHandler(report, prop1);
		assertNotNull(and.getRuisHandler());
		assertTrue("AndEntailment: RuisHandler added is not a PTree", 
				and.getRuisHandler() instanceof PTree);
		assertEquals(0, and.getReplies().size());
		
		// Woman(mary)
		and.applyRuleHandler(report1, prop2);
		assertEquals(0, and.getReplies().size());
		
		// Man(steve)
		and.applyRuleHandler(report2, prop1);
		assertEquals(0, and.getReplies().size());
		
		// Married(john, mary)
		and.applyRuleHandler(report4, prop3);
		assertEquals(1, and.getReplies().size());
		
		// Married(steve, sue)
		and.applyRuleHandler(report5, prop3);
		assertEquals(1, and.getReplies().size());
		
		// Woman(sue)
		and.applyRuleHandler(report3, prop2);
		assertEquals(2, and.getReplies().size());
		
		for(Report r : and.getReplies())
			System.out.println(r);
	}

	@After
	public void tearDown(){
		Network.clearNetwork();
		and.clear();
	}
}
