import java.util.LinkedList;

import org.junit.Test;

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
import sneps.snip.classes.RuisHandler;
import sneps.snip.classes.RuleUseInfo;
import sneps.snip.classes.SIndex;
import sneps.snip.matching.LinearSubstitutions;
import sneps.snip.rules.NumericalEntailment;
import junit.framework.TestCase;

public class NumericalEntailmentTests extends TestCase {
	private static Context context;
	private static String contextName = "TempContext";
	private static NumericalEntailment numerical;
	private static Node var;
	private static Node fido;
	private static Node dog;
	private static RuleUseInfo rui;
	private static Report report;

 	public void setUp() {
		Controller.createContext();
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
		Relation rel = new Relation("iant", "type");
		c1.addNode(dog);
		LinkedList<DownCable> dc = new LinkedList<DownCable>();
		LinkedList<Relation> rels = new LinkedList<Relation>();
		rels.add(rel);
		dc.add(new DownCable(rel, c1));

		c1 = new NodeSet();
		rel = new Relation("iant", "type");
		c1.addNode(fido);
		dc = new LinkedList<DownCable>();
		rels = new LinkedList<Relation>();
		rels.add(rel);
		dc.add(new DownCable(rel, c1));

		c1 = new NodeSet();
		rel = new Relation("iant", "type");
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

		numerical = new NumericalEntailment(new Open("Wat", dcs));
		numerical.setI(2);
		numerical.addAntecedent(var);
		numerical.addAntecedent(dog);
		numerical.addAntecedent(fido);
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
