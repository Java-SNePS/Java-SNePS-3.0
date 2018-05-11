import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import sneps.network.Network;
import sneps.network.Node;
import sneps.network.PropositionNode;
import sneps.network.RuleNode;
import sneps.network.VariableNode;
import sneps.network.cables.DownCable;
import sneps.network.cables.DownCableSet;
import sneps.network.classes.CaseFrame;
import sneps.network.classes.Relation;
import sneps.network.classes.Semantic;
import sneps.network.classes.term.Open;
import sneps.network.classes.term.Term;
import sneps.network.classes.term.Variable;
import sneps.setClasses.ContextRuisSet;
import sneps.setClasses.FlagNodeSet;
import sneps.setClasses.NodeSet;
import sneps.snebr.Context;
import sneps.snebr.Controller;
import sneps.snebr.Support;
import sneps.snip.Report;
import sneps.snip.classes.FlagNode;
import sneps.snip.classes.RuisHandler;
import sneps.snip.classes.RuleUseInfo;
import sneps.snip.classes.SIndex;
import sneps.snip.matching.LinearSubstitutions;
import sneps.snip.rules.NumericalEntailment;
import junit.framework.TestCase;


public class NumericalEntailmentTests extends TestCase {

	private static NumericalEntailment numerical;
	private static Node fido;
	private static Node var;
	private static Node dog;
	private static RuleUseInfo rui;
	private static Report report;

	@BeforeClass
 	public static void setUpBeforeClass() throws Exception {
		var = new VariableNode(new Variable("X"));
		fido = Network.buildBaseNode("Fido", new Semantic("Member"));
		dog = Network.buildBaseNode("Dog", new Semantic("Class"));
		
		LinearSubstitutions sub = new LinearSubstitutions();
		FlagNodeSet fns = new FlagNodeSet();
		Set<Support> support = new HashSet<Support>();
		support.add(new Support(0));
		FlagNode fn = new FlagNode(dog, support, 1);
		fns.putIn(fn);
		fn = new FlagNode(fido, support, 1);
		fns.putIn(fn);
		rui = new RuleUseInfo(sub, 1, 0, fns);

		NodeSet c1 = new NodeSet();
		Relation rel = new Relation("Class", "type");
		c1.addNode(dog);
		LinkedList<DownCable> dc = new LinkedList<DownCable>();
		LinkedList<Relation> rels = new LinkedList<Relation>();
		rels.add(rel);
		dc.add(new DownCable(rel, c1));

		c1 = new NodeSet();
		rel = new Relation("Member", "type");
		c1.addNode(fido);
		dc = new LinkedList<DownCable>();
		rels = new LinkedList<Relation>();
		rels.add(rel);
		dc.add(new DownCable(rel, c1));

		c1 = new NodeSet();
		rel = new Relation("Var", "type");
		c1.addNode(var);
		dc = new LinkedList<DownCable>();
		rels = new LinkedList<Relation>();
		rels.add(rel);

		dc.add(new DownCable(rel, c1));
		DownCableSet dcs = new DownCableSet(dc, new CaseFrame("string", rels));

		numerical = new NumericalEntailment(new Open("Wat", dcs));
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		Network.clearNetwork();
		numerical = null;
	}

	@Test
	public void testApplyRuleHandler() {
		numerical.setKnownInstances(numerical.getNewInstances());
		numerical.getNewInstances().clear();

		numerical.applyRuleHandler(report, dog);
		if(numerical.getAntSize() <= numerical.getI())
			assertNotNull("NumericalEntailment: ApplyRuleHandler doesn't broadcast report",
					((PropositionNode)numerical).getNewInstances());
	}

	@Test
	public void testGetDownAntNodeSet() {
		NodeSet downAntNodeSet = numerical.getDownAntNodeSet();
		assertNotNull("NumericalEntailment: getDownAntNodeSet retuns null", downAntNodeSet);
		assertTrue("NumericalEntailment: getDownAntNodeSet retuns an empty NodeSet", !downAntNodeSet.isEmpty());
	}

	@Test
	public void testCreateRuisHandler() {
		Context contxt = (Context) Controller.getContextByName("default");
		numerical.createRuisHandler("default");
		RuisHandler handler = numerical.getContextRuiHandler(contxt);
		assertNotNull("NumericalEntailment: CreateRuisHandler creats a null RuisHandler", handler);
		assertTrue("NumericalEntailment: CreateRuisHandler doesn't create an SIndex as a Handler", handler instanceof SIndex);
	}

	@Test
	public void testAndEntailmentTerm() {
		Class<NumericalEntailment> aClass = NumericalEntailment.class;
		boolean thrown = false;
		try {
			aClass.getConstructor(new Class[] {
					Term.class });
		} catch (Exception e) {
			thrown = true;
		}
		assertFalse(
				"Missing constructor with Term parameter in NumericalEntailment class.",
				thrown);

		assertEquals(
				"NumericalEntailment class should extend RuleNode",
				RuleNode.class,
				NumericalEntailment.class.getSuperclass());

		NumericalEntailment e = numerical;
		Field f;
		try {
			f = e.getClass().getDeclaredField("contextRuisSet");

			f.setAccessible(true);
			f.set(e, new ContextRuisSet());

			assertNotNull(
					"The constructor of NumericalEntailment class should initialize inherited variables correctly by calling super.",
					f);
		} catch(Exception x){
			assertNull(x.getMessage(), x);
		}
	}

	@Test
	public void testAndEntailmentSemanticTerm() {
		Class<NumericalEntailment> aClass = NumericalEntailment.class;
		boolean thrown = false;
		try {
			aClass.getConstructor(new Class[] {
					Semantic.class, Term.class });
		} catch (Exception e) {
			thrown = true;
		}
		assertFalse(
				"Missing constructor with Semantic numerical Term parameters in NumericalEntailment class.",
				thrown);

		assertEquals(
				"NumericalEntailment class should extend RuleNode",
				RuleNode.class,
				NumericalEntailment.class.getSuperclass());

		NumericalEntailment e = numerical;
		Field f;
		try {
			f = e.getClass().getDeclaredField("contextRuisSet");

			f.setAccessible(true);
			f.set(e, new ContextRuisSet());

			assertNotNull(
					"The constructor of NumericalEntailment class should initialize inherited variables correctly by calling super.",
					f);
		} catch(Exception x){
			assertNull(x.getMessage(), x);
		}
	}

	@Test
	public void testAddNotSentRui() {
		numerical.addNotSentRui(rui, "default", dog);
		Context contxt = (Context) Controller.getContextByName("default");
		assertNotNull("NumericalEntailment: addNotSentRui doesn't add a RuiHandler in contextRuisHandlers", 
				numerical.getContextRuiHandler(contxt));
		assertTrue("NumericalEntailment: addNotSentRui doesn't add signature to positiveNodes set", 
				numerical.getContextRuiHandler(contxt).getPositiveNodes().contains(dog));
		assertTrue("NumericalEntailment: addNotSentRui doesn't add a SIndex in contextRuisHandlers", 
				numerical.getContextRuiHandler(contxt)instanceof SIndex);
	}

}
