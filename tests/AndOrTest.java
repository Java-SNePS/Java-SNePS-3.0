import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.LinkedList;

import org.junit.BeforeClass;
import org.junit.Test;

import sneps.exceptions.CannotBuildNodeException;
import sneps.exceptions.DuplicateContextNameException;
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
import sneps.network.classes.setClasses.NodeSet;
import sneps.network.classes.setClasses.PropositionSet;
import sneps.snebr.Context;
import sneps.snebr.Controller;
import sneps.snip.InferenceTypes;
import sneps.snip.Report;
import sneps.snip.classes.SIndex;
import sneps.snip.matching.Binding;
import sneps.snip.matching.LinearSubstitutions;
import sneps.snip.rules.AndOrEntailment;

public class AndOrTest {
	
	private static Context context;
	private static String contextName = "TempContext";
	private static AndOrEntailment andor;
	private static Node john, fido, var, animal, veg, mineral, human;
	private static Node prop5, prop6, prop7, prop8;
	private static Report report, report1, report2, report3;
	
	@BeforeClass
 	public static void setUp() throws Exception {
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
		Relation memberRel = Network.defineRelation("Member", "NodeSet");
		Relation classRel = Network.defineRelation("Class", "NodeSet");
		Relation argsRel = Network.defineRelation("arg", "arg");
		rels.add(memberRel);	rels.add(classRel);
		CaseFrame caseFrameMC = Network.defineCaseFrame("MemberClass", rels);
 		rels.clear();		rels.add(argsRel);
 		CaseFrame caseFrameArgs = Network.defineCaseFrame("Args", rels);
		Wire wire4 = null, wire5 = null, wire6 = null, wire7 = null, wire8 = null;
		rels.clear();
		
		try {
			var = Network.buildVariableNode("X");
			fido = Network.buildBaseNode("Fido", new Semantic("Base"));
			john = Network.buildBaseNode("John", new Semantic("Base"));
			animal = Network.buildBaseNode("Animal", new Semantic("Base"));
			veg = Network.buildBaseNode("Vegetable", new Semantic("Base"));
			mineral = Network.buildBaseNode("Mineral", new Semantic("Base"));
			human = Network.buildBaseNode("Human", new Semantic("Base"));
			wire4 = new Wire(memberRel, var);
			wire5 = new Wire(classRel, animal);
			wire6 = new Wire(classRel, veg);
			wire7 = new Wire(classRel, mineral);
			wire8 = new Wire(classRel, human);
		} catch (IllegalIdentifierException | NotAPropositionNodeException 
				| NodeNotFoundInNetworkException e1) {
			assertNotNull(e1.getMessage(), e1);
			var = new VariableNode(new Variable("X"));
		}
		
		try {
			wires.clear();	wires.add(wire4);	wires.add(wire5);
			prop5 = Network.buildMolecularNode(wires, caseFrameArgs);
			
			wires.clear();	wires.add(wire4);	wires.add(wire6);
			prop6 = Network.buildMolecularNode(wires, caseFrameArgs);
			
			wires.clear();	wires.add(wire4);	wires.add(wire7);
			prop7 = Network.buildMolecularNode(wires, caseFrameArgs);
			
			wires.clear();	wires.add(wire4);	wires.add(wire8);
			prop8 = Network.buildMolecularNode(wires, caseFrameArgs);
		} catch (CannotBuildNodeException | EquivalentNodeException
				| NotAPropositionNodeException | NodeNotFoundInNetworkException e1) {
			assertNotNull(e1.getMessage(), e1);
		}
		
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

		nodeSet1.addNode(var);
		dc1 = new DownCable(memberRel, nodeSet1);
		dcList.add(dc1);
		nodeSet2.addNode(animal);
		dc1 = new DownCable(classRel, nodeSet2);
		dcList.add(dc1);
		dcs = new DownCableSet(dcList, caseFrameMC); 
		prop5 = new Node(new Open("Prop5", dcs));
		//((Open) (prop5.getTerm())).getFreeVariables().addVarNode((VariableNode) var);
		dcList.clear();
		//------------------------------------------------------------//
		nodeSet3.addNode(var);
		dc1 = new DownCable(memberRel, nodeSet3);
		dcList.add(dc1);
		nodeSet4.addNode(veg);
		dc1 = new DownCable(classRel, nodeSet4);
		dcList.add(dc1);
		dcs = new DownCableSet(dcList, caseFrameMC); 
		prop6 = new Node(new Open("Prop6", dcs));
		//((Open) (prop6.getTerm())).getFreeVariables().addVarNode((VariableNode) var);
		dcList.clear();
		//------------------------------------------------------------//
		nodeSet5.addNode(var);
		dc1 = new DownCable(memberRel, nodeSet5);
		dcList.add(dc1);
	    nodeSet6.addNode(mineral);
		dc1 = new DownCable(classRel, nodeSet6);
		dcList.add(dc1);
		dcs = new DownCableSet(dcList, caseFrameMC); 
		prop7 = new Node(new Open("Prop7", dcs));
		//((Open) (prop7.getTerm())).getFreeVariables().addVarNode((VariableNode) var);
		dcList.clear();
		//------------------------------------------------------------//
		nodeSet7.addNode(var);
		dc1 = new DownCable(memberRel, nodeSet7);
		dcList.add(dc1);
		nodeSet8.addNode(human);
		dc1 = new DownCable(classRel, nodeSet8);
		dcList.add(dc1);
		dcs = new DownCableSet(dcList, caseFrameMC); 
		prop8 = new Node(new Open("Prop8", dcs));
		//((Open) (prop8.getTerm())).getFreeVariables().addVarNode((VariableNode) var);
		dcList.clear();
//---------------------------------------------------------------------------------//
		
		nodeSet.addNode(prop5);
		nodeSet.addNode(prop6);
		nodeSet.addNode(prop7);
		nodeSet.addNode(prop8);
		dc.add(new DownCable(argsRel, nodeSet));

		DownCableSet dcss = new DownCableSet(dc, caseFrameArgs);

//---------------------------- ANDOR -----------------------------------//
		
		andor = new AndOrEntailment(new Open("Open", dcss));
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
	public void testProcessNodes() {
 		assertEquals(0, andor.getConsequents().size());
		assertEquals(4, andor.getAntecedents().size());
		assertEquals(4, andor.getAntsWithVars().size());
		assertEquals(0, andor.getAntsWithoutVars().size());
 	}
	
	/**
	 * First inference rule allowed by andor (j args are true, then the other n-j 
	 * have to be false).
	 */
	@Test
	public void test() {
		andor.applyRuleHandler(report, prop5);
		assertNotNull("Null RuisHandler", andor.getRuisHandler());
		assertTrue("addRuiHandler doesn't create an SIndex as a RuisHandler", 
				andor.getRuisHandler() instanceof SIndex);
		assertEquals(SIndex.SINGLETON, ((SIndex) (andor.getRuisHandler())).getRuiHandlerType());
		assertEquals(0, andor.getReplies().size());
		
		andor.applyRuleHandler(report1, prop6);
		assertEquals(1, andor.getReplies().size());
		assertEquals(false, andor.getReplies().get(0).getSign());
		assertEquals(2, andor.getConsequents().size());
		assertEquals(prop7, andor.getConsequents().getNode(0));
		assertEquals(prop8, andor.getConsequents().getNode(1));
	}
	
	/**
	 * Second inference rule  allowed by andor (n-i args are false, then the other i 
	 * have to be true).
	 */
	@Test
	public void test2() {
		andor.clear();
		LinearSubstitutions sub = new LinearSubstitutions();
		LinearSubstitutions sub1 = new LinearSubstitutions();
		sub.putIn(new Binding((VariableNode) var, fido));
		sub1.putIn(new Binding((VariableNode) var, john));
		
		PropositionSet support = new PropositionSet();
		/*try {
		support.add(prop5.getId());
		} catch (DuplicatePropositionException | NotAPropositionNodeException 
				| NodeNotFoundInNetworkException e) {
		e.printStackTrace();
		}*/
		report = new Report(sub, support, false, InferenceTypes.FORWARD);
		
		support = new PropositionSet();
		/*try {
		support.add(prop7.getId());
		} catch (DuplicatePropositionException | NotAPropositionNodeException 
				| NodeNotFoundInNetworkException e) {
		e.printStackTrace();
		}*/
		report1 = new Report(sub, support, false, InferenceTypes.BACKWARD);
		
		support = new PropositionSet();
		/*try {
		support.add(prop8.getId());
		} catch (DuplicatePropositionException | NotAPropositionNodeException 
				| NodeNotFoundInNetworkException e) {
		e.printStackTrace();
		}*/
		report3 = new Report(sub1, support, false, InferenceTypes.BACKWARD);
		report2 = new Report(sub, support, false, InferenceTypes.BACKWARD);
		
		andor.applyRuleHandler(report, prop5);
		assertEquals(0, andor.getReplies().size());
		
		andor.applyRuleHandler(report1, prop7);
		assertEquals(0, andor.getReplies().size());
		
		andor.applyRuleHandler(report3, prop8);
		assertEquals(0, andor.getReplies().size());
		
		andor.applyRuleHandler(report2, prop8);
		assertEquals(1, andor.getReplies().size());
		assertEquals(true, andor.getReplies().get(0).getSign());
		assertEquals(1, andor.getConsequents().size());
		assertEquals(prop6, andor.getConsequents().getNode(0));
	}
	
	/**
	 * Min and Max are both equal to 1.
	 */
	@Test
	public void test3() {
		andor.clear();
		andor.setMax(1);
		andor.setMin(1);
		LinearSubstitutions sub = new LinearSubstitutions();
		PropositionSet support = new PropositionSet();
		/*try {
		support.add(prop5.getId());
		} catch (DuplicatePropositionException | NotAPropositionNodeException 
				| NodeNotFoundInNetworkException e) {
		e.printStackTrace();
		}*/
		sub.putIn(new Binding((VariableNode) var, fido));
		report = new Report(sub, support, true, InferenceTypes.BACKWARD);
		
		andor.applyRuleHandler(report, prop5);
		assertEquals(1, andor.getReplies().size());
		assertEquals(false, andor.getReplies().get(0).getSign());
		assertEquals(3, andor.getConsequents().size());
	}
	
	/**
	 * Min and Max are both equal to 0.
	 */
	@Test
	public void test4() {
		andor.clear();
		andor.setMax(0);
		andor.setMin(0);
		LinearSubstitutions sub = new LinearSubstitutions();
		PropositionSet support = new PropositionSet();
		/*try {
		support.add(prop5.getId());
		} catch (DuplicatePropositionException | NotAPropositionNodeException 
				| NodeNotFoundInNetworkException e) {
		e.printStackTrace();
		}*/
		sub.putIn(new Binding((VariableNode) var, fido));
		report = new Report(sub, support, true, InferenceTypes.BACKWARD);
		
		andor.applyRuleHandler(report, prop5);
		assertEquals(0, andor.getReplies().size());
	}
	
	public void tearDown() {
		Network.clearNetwork();
		andor.clear();
	}
}