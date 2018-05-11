import static org.junit.Assert.*;

import java.lang.reflect.Field;
import java.util.LinkedList;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import sneps.network.Network;
import sneps.network.Node;
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
import sneps.setClasses.NodeSet;
import sneps.snebr.Context;
import sneps.snebr.Controller;
import sneps.snip.classes.PTree;
import sneps.snip.classes.RuisHandler;
import sneps.snip.rules.AndEntailment;


public class AndEntailmentTests {
	private static AndEntailment and;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		Node var = new VariableNode(new Variable("X"));
		Node fido = Network.buildBaseNode("Fido", new Semantic("Member"));
		Node dog = Network.buildBaseNode("Dog", new Semantic("Class"));

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

		and = new AndEntailment(new Open("Wat", dcs));
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		Network.clearNetwork();
		and = null;
	}

	@Test
	public void testApplyRuleHandler() {
		fail("Not yet implemented");
	}

	@Test
	public void testApplyRuleOnRui() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetDownAntNodeSet() {
		NodeSet downAntNodeSet = and.getDownAntNodeSet();
		assertNotNull("AndEntailment: getDownAntNodeSet retuns null", downAntNodeSet);
		assertTrue("AndEntailment: getDownAntNodeSet retuns an empty NodeSet", !downAntNodeSet.isEmpty());
	}

	@Test
	public void testCreateRuisHandler() {
		Context contxt = (Context) Controller.getContextByName("default");
		and.createRuisHandler("default");
		RuisHandler handler = and.getContextRuiHandler(contxt);
		assertNotNull("AndEntailment: CreateRuisHandler creats a null RuisHandler", handler);
		assertTrue("AndEntailment: CreateRuisHandler doesn't creat a PTree as a Handler", handler instanceof PTree);
	}

	@Test
	public void testAndEntailmentTerm() {
		Class<AndEntailment> aClass = AndEntailment.class;
		boolean thrown = false;
		try {
			aClass.getConstructor(new Class[] {
					Term.class });
		} catch (Exception e) {
			thrown = true;
		}
		assertFalse(
				"Missing constructor with Term parameter in AndEntailment class.",
				thrown);

		assertEquals(
				"AndEntailment class should extend RuleNode",
				RuleNode.class,
				AndEntailment.class.getSuperclass());

		AndEntailment e = and;
		Field f;
		try {
			f = e.getClass().getDeclaredField("contextRuisSet");

			f.setAccessible(true);
			f.set(e, new ContextRuisSet());

			assertNotNull(
					"The constructor of AndEntailment class should initialize inherited variables correctly by calling super.",
					f);
		} catch(Exception x){
			assertNull(x.getMessage(), x);
		}
	}

	@Test
	public void testAndEntailmentSemanticTerm() {
		Class<AndEntailment> aClass = AndEntailment.class;
		boolean thrown = false;
		try {
			aClass.getConstructor(new Class[] {
					Semantic.class, Term.class });
		} catch (Exception e) {
			thrown = true;
		}
		assertFalse(
				"Missing constructor with Semantic and Term parameters in AndEntailment class.",
				thrown);

		assertEquals(
				"AndEntailment class should extend RuleNode",
				RuleNode.class,
				AndEntailment.class.getSuperclass());

		AndEntailment e = and;
		Field f;
		try {
			f = e.getClass().getDeclaredField("contextRuisSet");

			f.setAccessible(true);
			f.set(e, new ContextRuisSet());

			assertNotNull(
					"The constructor of AndEntailment class should initialize inherited variables correctly by calling super.",
					f);
		} catch(Exception x){
			assertNull(x.getMessage(), x);
		}
	}

	@Test
	public void testAddNotSentRui() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetConsequents() {
		fail("Not yet implemented");
	}

	@Test
	public void testSetConsequents() {
		fail("Not yet implemented");
	}

}
