package tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import sneps.exceptions.CaseFrameWithSetOfRelationsNotFoundException;
import sneps.exceptions.NodeNotFoundInNetworkException;
import sneps.exceptions.NotAPropositionNodeException;
import sneps.exceptions.RelationDoesntExistException;
import sneps.exceptions.SemanticNotFoundInNetworkException;
import sneps.network.Network;
import sneps.network.Node;
import sneps.network.classes.CaseFrame;
import sneps.network.classes.Relation;
import sneps.network.classes.SemanticHierarchy;
import sneps.network.classes.setClasses.PropositionSet;
import sneps.network.classes.term.Molecular;
import sneps.network.paths.ComposePath;
import sneps.network.paths.FUnitPath;
import sneps.network.paths.Path;
import sneps.snebr.Controller;
import sneps.snepslog.AP;

public class SnepslogTest {

	@Before
	public void before() {
		Network.defineDefaults();
		AP.executeSnepslogCommand("set-mode-1");
		AP.executeSnepslogCommand("normal");
	}

	@After
	public void after() {
		AP.executeSnepslogCommand("set-mode-1");
		AP.executeSnepslogCommand("normal");
	}

	@Test
	public void testWffDot() throws NotAPropositionNodeException, NodeNotFoundInNetworkException {
		AP.executeSnepslogCommand("dog(Fido).");
		PropositionSet ps = Controller.getCurrentContext().allAsserted();
		boolean success = false;
		for (int i = 0; i < PropositionSet.getPropsSafely(ps).length; i++) {
			Node n = Network.getNodeById(PropositionSet.getPropsSafely(ps)[i]);
			if (n.getTerm() instanceof Molecular) {
				Molecular m = (Molecular) n.getTerm();
				LinkedList<Relation> rels = m.getDownCableSet().getCaseFrame().getRelations();
				if (rels.size() == 2 && rels.get(0).getName().equals("r") && rels.get(1).getName().equals("a1")) {
					Node dog = Network.getNode("dog");
					Node Fido = Network.getNode("Fido");
					if (m.getDownCableSet().getDownCable("r").getNodeSet().contains(dog)
							&& m.getDownCableSet().getDownCable("a1").getNodeSet().contains(Fido)) {
						success = true;
					}
				}
			}
		}
		if (!success) {
			fail("dog(Fido) assertion failed!");
		}
	}

	@Test
	public void testAddToContext() throws NotAPropositionNodeException, NodeNotFoundInNetworkException {
		AP.executeSnepslogCommand("add-to-context default {dog(Fido), animal(Fido)}");
		PropositionSet ps = Controller.getCurrentContext().allAsserted();
		boolean success1 = false;
		boolean success2 = false;
		for (int i = 0; i < PropositionSet.getPropsSafely(ps).length; i++) {
			Node n = Network.getNodeById(PropositionSet.getPropsSafely(ps)[i]);
			if (n.getTerm() instanceof Molecular) {
				Molecular m = (Molecular) n.getTerm();
				LinkedList<Relation> rels = m.getDownCableSet().getCaseFrame().getRelations();
				if (rels.size() == 2 && rels.get(0).getName().equals("r") && rels.get(1).getName().equals("a1")) {
					Node dog = Network.getNode("dog");
					Node animal = Network.getNode("animal");
					Node Fido = Network.getNode("Fido");
					if (m.getDownCableSet().getDownCable("r").getNodeSet().contains(dog)
							&& m.getDownCableSet().getDownCable("a1").getNodeSet().contains(Fido)) {
						success1 = true;
					}
					if (m.getDownCableSet().getDownCable("r").getNodeSet().contains(animal)
							&& m.getDownCableSet().getDownCable("a1").getNodeSet().contains(Fido)) {
						success2 = true;
					}
				}
			}
		}
		if (!(success1 && success2)) {
			fail("failure while adding dog(Fido) and animal(Fido) to the context named \"default\"!}");
		}
	}

	@Test
	public void testSetModeOne() throws NoSuchMethodException, SecurityException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {
		AP.executeSnepslogCommand("set-mode-1");
		Method snepslogModeGetter = AP.class.getDeclaredMethod("getSnepslogMode");
		snepslogModeGetter.setAccessible(true);
		int mode = (int) snepslogModeGetter.invoke(null);
		assertEquals(1, mode);
	}

	@Test
	public void testSetModeTwo() throws NoSuchMethodException, SecurityException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {
		AP.executeSnepslogCommand("set-mode-2");
		Method snepslogModeGetter = AP.class.getDeclaredMethod("getSnepslogMode");
		snepslogModeGetter.setAccessible(true);
		int mode = (int) snepslogModeGetter.invoke(null);
		assertEquals(2, mode);
	}

	@Test
	public void testSetModeThree() throws NoSuchMethodException, SecurityException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {
		AP.executeSnepslogCommand("set-mode-3");
		Method snepslogModeGetter = AP.class.getDeclaredMethod("getSnepslogMode");
		snepslogModeGetter.setAccessible(true);
		int mode = (int) snepslogModeGetter.invoke(null);
		assertEquals(3, mode);
	}

	@Test
	public void testExpert() throws NoSuchMethodException, SecurityException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {
		AP.executeSnepslogCommand("expert");
		Method printingModeGetter = AP.class.getDeclaredMethod("getPrintingMode");
		printingModeGetter.setAccessible(true);
		String mode = (String) printingModeGetter.invoke(null);
		assertEquals("expert", mode);
	}

	@Test
	public void testNormal() throws NoSuchMethodException, SecurityException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {
		AP.executeSnepslogCommand("normal");
		Method printingModeGetter = AP.class.getDeclaredMethod("getPrintingMode");
		printingModeGetter.setAccessible(true);
		String mode = (String) printingModeGetter.invoke(null);
		assertEquals("normal", mode);
	}

	@Test
	public void testUnlabeled() throws NoSuchMethodException, SecurityException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {
		AP.executeSnepslogCommand("unlabeled");
		Method printingModeGetter = AP.class.getDeclaredMethod("getPrintingMode");
		printingModeGetter.setAccessible(true);
		String mode = (String) printingModeGetter.invoke(null);
		assertEquals("unlabeled", mode);
	}

	@Test
	public void testDefineSemantic() throws SemanticNotFoundInNetworkException {
		AP.executeSnepslogCommand("set-mode-3.");
		AP.executeSnepslogCommand("define-semantic Action.");
		assertTrue(SemanticHierarchy.getSemantics().containsKey("Action"));
		assertTrue(SemanticHierarchy.getSemantic("Action").getSemanticType().equals("Action"));
	}

	@Test
	public void testDefineRelation() throws RelationDoesntExistException {
		AP.executeSnepslogCommand("set-mode-3.");
		AP.executeSnepslogCommand("define-relation motherof Proposition.");
		assertTrue(Network.getRelations().containsKey("motherof"));
		Relation relation = Network.getRelation("motherof");
		assertTrue(relation.getName().equals("motherof"));
		assertTrue(relation.getType().equals("Proposition"));
	}

	@Test
	public void testDefineFrame()
			throws RelationDoesntExistException, CaseFrameWithSetOfRelationsNotFoundException, NoSuchMethodException,
			SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		AP.executeSnepslogCommand("set-mode-3.");
		AP.executeSnepslogCommand("define-relation motherof Proposition.");
		AP.executeSnepslogCommand("define-frame mother Proposition (nil motherof).");
		Method modeThreeCaseFramesGetter = AP.class.getDeclaredMethod("getModeThreeCaseFrames");
		modeThreeCaseFramesGetter.setAccessible(true);
		Hashtable<String, CaseFrame> modeThreeCaseFrames = (Hashtable<String, CaseFrame>) snepslogModeGetter
				.invoke(null);
		assertTrue(modeThreeCaseFrames.containsKey("mother$"));
		CaseFrame caseFrame = modeThreeCaseFrames.get("mother$");
		assertTrue(caseFrame.getSemanticClass().equals("Proposition"));
		LinkedList<Relation> relations = caseFrame.getRelations();
		assertTrue(relations.size() == 1);
		assertTrue(relations.contains(Network.getRelation("motherof")));
		assertTrue(Network.getCaseFrames().containsKey(caseFrame.getId()));
	}

	@Test
	public void testDefinePath() throws RelationDoesntExistException {
		AP.executeSnepslogCommand("set-mode-3.");
		AP.executeSnepslogCommand("define-relation rel Proposition.");
		AP.executeSnepslogCommand("define-relation class Proposition.");
		AP.executeSnepslogCommand("define-relation member Proposition.");
		AP.executeSnepslogCommand("define-path rel compose(class, member).");
		Path path = Network.getRelation("rel").getPath();
		assertTrue(path instanceof ComposePath);
		ComposePath cpath = (ComposePath) path;
		LinkedList<Path> paths = cpath.getPaths();
		assertTrue(paths.size() == 2);
		boolean c1 = false;
		boolean c2 = false;
		for (int i = 0; i < paths.size(); i++) {
			if (paths.get(i) instanceof FUnitPath) {
				FUnitPath fupath = (FUnitPath) paths.get(i);
				if (fupath.getRelation().equals(Network.getRelation("class"))) {
					c1 = true;
				}
				if (fupath.getRelation().equals(Network.getRelation("member"))) {
					c2 = true;
				}
			}
		}
		if (!(c1 && c2)) {
			fail("The proces of defining this path failed.");
		}
	}

	@Test
	public void testUndefinePath() throws RelationDoesntExistException {
		AP.executeSnepslogCommand("set-mode-3.");
		AP.executeSnepslogCommand("define-relation rel Proposition.");
		AP.executeSnepslogCommand("define-relation class Proposition.");
		AP.executeSnepslogCommand("define-relation member Proposition.");
		AP.executeSnepslogCommand("define-path rel compose(class, member).");
		AP.executeSnepslogCommand("undefine-path rel.");
		assertNull(Network.getRelation("rel").getPath());
	}

	@Test
	public void testSetContext() {
		assertTrue(!Controller.getAllNamesOfContexts().contains("mythology"));
		AP.executeSnepslogCommand("set-context mythology");
		assertTrue(Controller.getAllNamesOfContexts().contains("mythology"));
	}

	@Test
	public void testSetDefaultContext() {
		AP.executeSnepslogCommand("set-context mythology");
		assertTrue(Controller.getContextByName("default").equals(Controller.getCurrentContext()));
		AP.executeSnepslogCommand("set-default-context mythology");
		assertTrue(Controller.getContextByName("mythology").equals(Controller.getCurrentContext()));
	}

	@Test
	public void testClearKB() throws NoSuchMethodException, SecurityException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {
		AP.executeSnepslogCommand("clearkb.");
		Method modeThreeCaseFramesGetter = AP.class.getDeclaredMethod("getModeThreeCaseFrames");
		modeThreeCaseFramesGetter.setAccessible(true);
		Hashtable<String, CaseFrame> modeThreeCaseFrames = (Hashtable<String, CaseFrame>) snepslogModeGetter
				.invoke(null);
		assertTrue(modeThreeCaseFrames.size() == 0);
		Method cfsDescriptionsGetter = AP.class.getDeclaredMethod("getCfsDescriptions");
		cfsDescriptionsGetter.setAccessible(true);
		Hashtable<CaseFrame, String> cfsDescriptions = (Hashtable<CaseFrame, String>) cfsDescriptionsGetter
				.invoke(null);
		assertTrue(cfsDescriptions.size() == 0);
		Method nodesDescriptionsGetter = AP.class.getDeclaredMethod("getNodesDescriptions");
		nodesDescriptionsGetter.setAccessible(true);
		Hashtable<Node, String> nodesDescriptions = (Hashtable<Node, String>) snepslogModeGetter.invoke(null);
		assertTrue(nodesDescriptions.size() == 0);
	}

}
