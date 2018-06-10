import java.util.LinkedList;

import junit.framework.TestCase;

import org.junit.Test;

import sneps.exceptions.DuplicateContextNameException;
import sneps.exceptions.DuplicatePropositionException;
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
import sneps.network.classes.term.Base;
import sneps.network.classes.term.Open;
import sneps.setClasses.FlagNodeSet;
import sneps.setClasses.NodeSet;
import sneps.setClasses.PropositionSet;
import sneps.snebr.Context;
import sneps.snebr.Controller;
import sneps.snip.Report;
import sneps.snip.classes.FlagNode;
import sneps.snip.classes.PTree;
import sneps.snip.classes.RuisHandler;
import sneps.snip.classes.RuleUseInfo;
import sneps.snip.matching.LinearSubstitutions;
import sneps.snip.rules.AndEntailment;


public class AndEntailmentTests extends TestCase{
	private static Context context;
	private static String contextName = "TempContext";
	private static AndEntailment and;
	private static Node fido;
	private static Node var;
	private static Node dog;
	private static RuleUseInfo rui;
	private static Report report;

 	public void setUp(){
 		try {
			context = Controller.createContext(contextName);
		} catch (DuplicateContextNameException e1) {
			assertNotNull(e1.getMessage(), e1);
		}
 		try {
			var = Network.buildVariableNode("X");
			fido = Network.buildBaseNode("Fido", new Semantic("Member"));
			dog = Network.buildBaseNode("Dog", new Semantic("Class"));
		} catch (IllegalIdentifierException | NotAPropositionNodeException 
				| NodeNotFoundInNetworkException e) {
			assertNotNull(e.getMessage(), e);
			var = new VariableNode();
			fido = new Node(new Base("Fido"));
			dog = new Node(new Base("Dog"));
		}

		LinearSubstitutions sub = new LinearSubstitutions();
		FlagNodeSet fns = new FlagNodeSet();
		PropositionSet support = new PropositionSet();

		try {
			support.add(dog.getId());
		} catch (DuplicatePropositionException | NotAPropositionNodeException
				| NodeNotFoundInNetworkException e) {
			assertNotNull(e.getMessage(), e);
		}
		FlagNode fn = new FlagNode(dog, support, 1);
		fns.insert(fn);

		support.clearSet();
		try {
			support.add(fido.getId());
		} catch (DuplicatePropositionException | NotAPropositionNodeException
				| NodeNotFoundInNetworkException e) {
			assertNotNull(e.getMessage(), e);
		}
		fn = new FlagNode(fido, support, 1);
		fns.insert(fn);

		rui = new RuleUseInfo(sub, 1, 0, fns);

		NodeSet c1 = new NodeSet();
		Relation rel = new Relation("&ant", "type");
		c1.addNode(dog);
		LinkedList<DownCable> dc = new LinkedList<DownCable>();
		LinkedList<Relation> rels = new LinkedList<Relation>();
		rels.add(rel);
		dc.add(new DownCable(rel, c1));

		c1 = new NodeSet();
		rel = new Relation("&ant", "type");
		c1.addNode(fido);
		dc = new LinkedList<DownCable>();
		rels = new LinkedList<Relation>();
		rels.add(rel);
		dc.add(new DownCable(rel, c1));

		c1 = new NodeSet();
		rel = new Relation("&ant", "type");
		c1.addNode(var);
		dc = new LinkedList<DownCable>();
		rels = new LinkedList<Relation>();
		rels.add(rel);

		dc.add(new DownCable(rel, c1));
		DownCableSet dcs = new DownCableSet(dc, new CaseFrame("string", rels));
		try {
			support.add(dog.getId());
			support.add(fido.getId());
			support.add(var.getId());
		} catch (DuplicatePropositionException | NotAPropositionNodeException
				| NodeNotFoundInNetworkException e) {
			assertNotNull(e.getMessage(), e);
		}
		report = new Report(sub, support, true, contextName);

		and = new AndEntailment(new Open("Open", dcs));
		and.addAntecedent(var);
		and.addAntecedent(dog);
		and.addAntecedent(fido);
	}


	@Test
	public void testCreateRuisHandler() {
		RuisHandler createdHandler = and.createRuisHandler(contextName);
		RuisHandler retrievedHandler = and.getContextRuiHandler(contextName);
		assertNotNull(
				"AndEntailment: CreateRuisHandler creates a null RuisHandler",
				createdHandler);
		assertTrue(
				"AndEntailment: CreateRuisHandler doesn't creates a PTree as a Handler",
				createdHandler instanceof PTree);
		assertEquals("AndEntailment: GetRuisHandler retrieves a different RuisHandler from CreateRuisHandler  a RuisHandler", 
				createdHandler, retrievedHandler);
	}

	@Test
	public void testApplyRuleHandler() {
		and.setKnownInstances(and.getNewInstances());
		and.getNewInstances().clear();

		and.applyRuleHandler(report, fido);
		if(and.getAntSize() <= 1)
			assertNotNull("AndEntailment: ApplyRuleHandler doesn't broadcast report",
					and.getNewInstances());
		else
			assertTrue("AndEntailment: ApplyRuleHandler broacdcasts final report without waiting for enough positive antecedents reports",
					and.getNewInstances().isEmpty());


		and.setKnownInstances(and.getNewInstances());
		and.getNewInstances().clear();
		LinearSubstitutions sub = new LinearSubstitutions();
		FlagNodeSet fns = new FlagNodeSet();
		PropositionSet support = new PropositionSet();

		try {
			support.add(dog.getId());
		} catch (DuplicatePropositionException | NotAPropositionNodeException
				| NodeNotFoundInNetworkException e) {}

		FlagNode fn = new FlagNode(dog, support, 1);
		fns.insert(fn);
		report = new Report(sub, support, false, contextName);

		and.applyRuleHandler(report, dog);
		if(and.getAntSize() >= 1)
			assertTrue(
					"AndEntailment: ApplyRuleHandler broadcasts negative report",
					and.getNewInstances().isEmpty());
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
	public void testAddNotSentRui() {
		and.addNotSentRui(rui, contextName, dog);
		assertNotNull("AndEntailment: addNotSentRui doesn't add a RuiHandler in contextRuisHandlers", 
				and.getContextRuiHandler(contextName));

		NodeSet positives = and.getContextRuiHandler(contextName).getPositiveNodes();
		assertTrue("AndEntailment: addNotSentRui doesn't add signature to positiveNodes set", 
				positives.contains(dog));
		assertFalse("AndEntailment: addNotSentRui adds wrong signatures to positiveNodes set", 
				positives.contains(fido));

		assertTrue("AndEntailment: addNotSentRui doesn't add a PTree in contextRuisHandlers", 
				and.getContextRuiHandler(contextName)instanceof PTree);

	}

	//@AfterClass
	public void tearDown(){
		Network.clearNetwork();
		and.clear();
		fido = null;
		var = null;
		dog = null;
		rui = null;
		report = null;
	}
}
