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
import sneps.network.classes.setClasses.NodeSet;
import sneps.network.classes.setClasses.PropositionSet;
import sneps.network.classes.term.Base;
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
		AP.executeSnepslogCommand("clearkb");
		AP.executeSnepslogCommand("set-mode-1");
		AP.executeSnepslogCommand("normal");
	}

	@After
	public void after() {
		AP.executeSnepslogCommand("clearkb");
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
		Hashtable<String, CaseFrame> modeThreeCaseFrames = (Hashtable<String, CaseFrame>) modeThreeCaseFramesGetter
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
		Hashtable<String, CaseFrame> modeThreeCaseFrames = (Hashtable<String, CaseFrame>) modeThreeCaseFramesGetter
				.invoke(null);
		assertTrue(modeThreeCaseFrames.size() == 0);
		Method cfsDescriptionsGetter = AP.class.getDeclaredMethod("getCfsDescriptions");
		cfsDescriptionsGetter.setAccessible(true);
		Hashtable<CaseFrame, String> cfsDescriptions = (Hashtable<CaseFrame, String>) cfsDescriptionsGetter
				.invoke(null);
		assertTrue(cfsDescriptions.size() == 0);
		Method nodesDescriptionsGetter = AP.class.getDeclaredMethod("getNodesDescriptions");
		nodesDescriptionsGetter.setAccessible(true);
		Hashtable<Node, String> nodesDescriptions = (Hashtable<Node, String>) nodesDescriptionsGetter.invoke(null);
		assertTrue(nodesDescriptions.size() == 0);
	}

	@Test
	public void testInfixedTerm() throws NodeNotFoundInNetworkException {
		AP.executeSnepslogCommand("dog(Fido) and animal(Fido).");
		Node n = Network.getNode("M3");
		Molecular m = (Molecular) n.getTerm();
		assertTrue(m.getDownCableSet().size() == 3);
		assertTrue(m.getDownCableSet().getDownCable("max").getNodeSet().size() == 1);
		assertTrue(m.getDownCableSet().getDownCable("min").getNodeSet().size() == 1);
		assertTrue(m.getDownCableSet().getDownCable("arg").getNodeSet().size() == 2);
		Node max = m.getDownCableSet().getDownCable("max").getNodeSet().getNode(0);
		assertTrue(max.getTerm() instanceof Base);
		assertTrue(max.getIdentifier().equals("2"));
		assertTrue(max.getSemantic().getSemanticType().equals("Infimum"));
		Node min = m.getDownCableSet().getDownCable("min").getNodeSet().getNode(0);
		assertTrue(min.getTerm() instanceof Base);
		assertTrue(min.getIdentifier().equals("2"));
		assertTrue(min.getSemantic().getSemanticType().equals("Infimum"));
		NodeSet args = m.getDownCableSet().getDownCable("arg").getNodeSet();
		boolean success1 = false;
		boolean success2 = false;
		for (int i = 0; i < args.size(); i++) {
			Node node = args.getNode(i);
			if (node.getTerm() instanceof Molecular) {
				Molecular molecular = (Molecular) node.getTerm();
				LinkedList<Relation> rels = molecular.getDownCableSet().getCaseFrame().getRelations();
				if (rels.size() == 2 && rels.get(0).getName().equals("r") && rels.get(1).getName().equals("a1")) {
					Node dog = Network.getNode("dog");
					Node animal = Network.getNode("animal");
					Node Fido = Network.getNode("Fido");
					if (molecular.getDownCableSet().getDownCable("r").getNodeSet().contains(dog)
							&& molecular.getDownCableSet().getDownCable("a1").getNodeSet().contains(Fido)) {
						success1 = true;
					}
					if (molecular.getDownCableSet().getDownCable("r").getNodeSet().contains(animal)
							&& molecular.getDownCableSet().getDownCable("a1").getNodeSet().contains(Fido)) {
						success2 = true;
					}
				}
			}
		}
		if (!(success1 && success2)) {
			fail("failure to build this infixedTerm!}");
		}
	}

	@Test
	public void testEntailment() throws NodeNotFoundInNetworkException {
		AP.executeSnepslogCommand("dog(Fido)=>animal(Fido).");
		Node n = Network.getNode("M3");
		Molecular m = (Molecular) n.getTerm();
		assertTrue(m.getDownCableSet().size() == 2);
		Node ant = m.getDownCableSet().getDownCable("ant").getNodeSet().getNode(0);
		Node cq = m.getDownCableSet().getDownCable("cq").getNodeSet().getNode(0);
		boolean success1 = false;
		boolean success2 = false;
		if (ant.getTerm() instanceof Molecular) {
			Molecular mant = (Molecular) ant.getTerm();
			LinkedList<Relation> relsant = mant.getDownCableSet().getCaseFrame().getRelations();
			if (relsant.size() == 2 && relsant.get(0).getName().equals("r") && relsant.get(1).getName().equals("a1")) {
				Node dog = Network.getNode("dog");
				Node Fido = Network.getNode("Fido");
				if (mant.getDownCableSet().getDownCable("r").getNodeSet().contains(dog)
						&& mant.getDownCableSet().getDownCable("a1").getNodeSet().contains(Fido)) {
					success1 = true;
				}
			}
		}
		if (cq.getTerm() instanceof Molecular) {
			Molecular mcq = (Molecular) cq.getTerm();
			LinkedList<Relation> relcq = mcq.getDownCableSet().getCaseFrame().getRelations();
			if (relcq.size() == 2 && relcq.get(0).getName().equals("r") && relcq.get(1).getName().equals("a1")) {
				Node animal = Network.getNode("animal");
				Node Fido = Network.getNode("Fido");
				if (mcq.getDownCableSet().getDownCable("r").getNodeSet().contains(animal)
						&& mcq.getDownCableSet().getDownCable("a1").getNodeSet().contains(Fido)) {
					success2 = true;
				}
			}
		}

		if (!(success1 && success2)) {
			fail("failure to build this entailment!}");
		}
	}

	@Test
	public void testNegatedTerm() throws NodeNotFoundInNetworkException {
		AP.executeSnepslogCommand("set-mode-2.");
		AP.executeSnepslogCommand("~dog(Fido).");
		Node n = Network.getNode("M2");
		Molecular m = (Molecular) n.getTerm();
		assertTrue(m.getDownCableSet().size() == 3);
		assertTrue(m.getDownCableSet().getDownCable("max").getNodeSet().size() == 1);
		assertTrue(m.getDownCableSet().getDownCable("min").getNodeSet().size() == 1);
		assertTrue(m.getDownCableSet().getDownCable("arg").getNodeSet().size() == 1);
		Node max = m.getDownCableSet().getDownCable("max").getNodeSet().getNode(0);
		assertTrue(max.getTerm() instanceof Base);
		assertTrue(max.getIdentifier().equals("0"));
		assertTrue(max.getSemantic().getSemanticType().equals("Infimum"));
		Node min = m.getDownCableSet().getDownCable("min").getNodeSet().getNode(0);
		assertTrue(min.getTerm() instanceof Base);
		assertTrue(min.getIdentifier().equals("0"));
		assertTrue(min.getSemantic().getSemanticType().equals("Infimum"));
		NodeSet args = m.getDownCableSet().getDownCable("arg").getNodeSet();
		boolean success = false;
		for (int i = 0; i < args.size(); i++) {
			Node node = args.getNode(i);
			if (node.getTerm() instanceof Molecular) {
				Molecular molecular = (Molecular) node.getTerm();
				LinkedList<Relation> rels = molecular.getDownCableSet().getCaseFrame().getRelations();
				if (rels.size() == 2 && rels.get(0).getName().equals("| rel dog|")
						&& rels.get(1).getName().equals("|rel-arg#dog1|")) {
					Node dog = Network.getNode("dog");
					Node Fido = Network.getNode("Fido");
					if (molecular.getDownCableSet().getDownCable("| rel dog|").getNodeSet().contains(dog)
							&& molecular.getDownCableSet().getDownCable("|rel-arg#dog1|").getNodeSet().contains(Fido)) {
						success = true;
					}
				}
			}
		}
		if (!success) {
			fail("failure to build this negatedTerm!}");
		}
	}

	@Test
	public void testAndorTerm() throws NodeNotFoundInNetworkException {
		AP.executeSnepslogCommand("andor(1,2){dog(Fido), animal(Fido)}.");
		Node n = Network.getNode("M3");
		Molecular m = (Molecular) n.getTerm();
		assertTrue(m.getDownCableSet().size() == 3);
		assertTrue(m.getDownCableSet().getDownCable("max").getNodeSet().size() == 1);
		assertTrue(m.getDownCableSet().getDownCable("min").getNodeSet().size() == 1);
		assertTrue(m.getDownCableSet().getDownCable("arg").getNodeSet().size() == 2);
		Node max = m.getDownCableSet().getDownCable("max").getNodeSet().getNode(0);
		assertTrue(max.getTerm() instanceof Base);
		assertTrue(max.getIdentifier().equals("2"));
		assertTrue(max.getSemantic().getSemanticType().equals("Infimum"));
		Node min = m.getDownCableSet().getDownCable("min").getNodeSet().getNode(0);
		assertTrue(min.getTerm() instanceof Base);
		assertTrue(min.getIdentifier().equals("1"));
		assertTrue(min.getSemantic().getSemanticType().equals("Infimum"));
		NodeSet args = m.getDownCableSet().getDownCable("arg").getNodeSet();
		boolean success1 = false;
		boolean success2 = false;
		for (int i = 0; i < args.size(); i++) {
			Node node = args.getNode(i);
			if (node.getTerm() instanceof Molecular) {
				Molecular molecular = (Molecular) node.getTerm();
				LinkedList<Relation> rels = molecular.getDownCableSet().getCaseFrame().getRelations();
				if (rels.size() == 2 && rels.get(0).getName().equals("r") && rels.get(1).getName().equals("a1")) {
					Node dog = Network.getNode("dog");
					Node animal = Network.getNode("animal");
					Node Fido = Network.getNode("Fido");
					if (molecular.getDownCableSet().getDownCable("r").getNodeSet().contains(dog)
							&& molecular.getDownCableSet().getDownCable("a1").getNodeSet().contains(Fido)) {
						success1 = true;
					}
					if (molecular.getDownCableSet().getDownCable("r").getNodeSet().contains(animal)
							&& molecular.getDownCableSet().getDownCable("a1").getNodeSet().contains(Fido)) {
						success2 = true;
					}
				}
			}
		}
		if (!(success1 && success2)) {
			fail("failure to build this andorTerm!}");
		}
	}

	@Test
	public void testSetTerm() throws NodeNotFoundInNetworkException {
		AP.executeSnepslogCommand("and{dog(Fido), animal(Fido)}.");
		Node n = Network.getNode("M3");
		Molecular m = (Molecular) n.getTerm();
		assertTrue(m.getDownCableSet().size() == 3);
		assertTrue(m.getDownCableSet().getDownCable("max").getNodeSet().size() == 1);
		assertTrue(m.getDownCableSet().getDownCable("min").getNodeSet().size() == 1);
		assertTrue(m.getDownCableSet().getDownCable("arg").getNodeSet().size() == 2);
		Node max = m.getDownCableSet().getDownCable("max").getNodeSet().getNode(0);
		assertTrue(max.getTerm() instanceof Base);
		assertTrue(max.getIdentifier().equals("2"));
		assertTrue(max.getSemantic().getSemanticType().equals("Infimum"));
		Node min = m.getDownCableSet().getDownCable("min").getNodeSet().getNode(0);
		assertTrue(min.getTerm() instanceof Base);
		assertTrue(min.getIdentifier().equals("2"));
		assertTrue(min.getSemantic().getSemanticType().equals("Infimum"));
		NodeSet args = m.getDownCableSet().getDownCable("arg").getNodeSet();
		boolean success1 = false;
		boolean success2 = false;
		for (int i = 0; i < args.size(); i++) {
			Node node = args.getNode(i);
			if (node.getTerm() instanceof Molecular) {
				Molecular molecular = (Molecular) node.getTerm();
				LinkedList<Relation> rels = molecular.getDownCableSet().getCaseFrame().getRelations();
				if (rels.size() == 2 && rels.get(0).getName().equals("r") && rels.get(1).getName().equals("a1")) {
					Node dog = Network.getNode("dog");
					Node animal = Network.getNode("animal");
					Node Fido = Network.getNode("Fido");
					if (molecular.getDownCableSet().getDownCable("r").getNodeSet().contains(dog)
							&& molecular.getDownCableSet().getDownCable("a1").getNodeSet().contains(Fido)) {
						success1 = true;
					}
					if (molecular.getDownCableSet().getDownCable("r").getNodeSet().contains(animal)
							&& molecular.getDownCableSet().getDownCable("a1").getNodeSet().contains(Fido)) {
						success2 = true;
					}
				}
			}
		}
		if (!(success1 && success2)) {
			fail("failure to build this setTerm!}");
		}
	}

	@Test
	public void testThreshTerm() throws NodeNotFoundInNetworkException {
		AP.executeSnepslogCommand("thresh(1,2){dog(Fido), animal(Fido)}.");
		Node n = Network.getNode("M3");
		Molecular m = (Molecular) n.getTerm();
		assertTrue(m.getDownCableSet().size() == 3);
		assertTrue(m.getDownCableSet().getDownCable("threshmax").getNodeSet().size() == 1);
		assertTrue(m.getDownCableSet().getDownCable("thresh").getNodeSet().size() == 1);
		assertTrue(m.getDownCableSet().getDownCable("arg").getNodeSet().size() == 2);
		Node max = m.getDownCableSet().getDownCable("threshmax").getNodeSet().getNode(0);
		assertTrue(max.getTerm() instanceof Base);
		assertTrue(max.getIdentifier().equals("2"));
		assertTrue(max.getSemantic().getSemanticType().equals("Infimum"));
		Node min = m.getDownCableSet().getDownCable("thresh").getNodeSet().getNode(0);
		assertTrue(min.getTerm() instanceof Base);
		assertTrue(min.getIdentifier().equals("1"));
		assertTrue(min.getSemantic().getSemanticType().equals("Infimum"));
		NodeSet args = m.getDownCableSet().getDownCable("arg").getNodeSet();
		boolean success1 = false;
		boolean success2 = false;
		for (int i = 0; i < args.size(); i++) {
			Node node = args.getNode(i);
			if (node.getTerm() instanceof Molecular) {
				Molecular molecular = (Molecular) node.getTerm();
				LinkedList<Relation> rels = molecular.getDownCableSet().getCaseFrame().getRelations();
				if (rels.size() == 2 && rels.get(0).getName().equals("r") && rels.get(1).getName().equals("a1")) {
					Node dog = Network.getNode("dog");
					Node animal = Network.getNode("animal");
					Node Fido = Network.getNode("Fido");
					if (molecular.getDownCableSet().getDownCable("r").getNodeSet().contains(dog)
							&& molecular.getDownCableSet().getDownCable("a1").getNodeSet().contains(Fido)) {
						success1 = true;
					}
					if (molecular.getDownCableSet().getDownCable("r").getNodeSet().contains(animal)
							&& molecular.getDownCableSet().getDownCable("a1").getNodeSet().contains(Fido)) {
						success2 = true;
					}
				}
			}
		}
		if (!(success1 && success2)) {
			fail("failure to build this threshTerm!}");
		}
	}

	@Test
	public void testSNeRETerm() throws NodeNotFoundInNetworkException {
		AP.executeSnepslogCommand("set-mode-3.");
		AP.executeSnepslogCommand("define-semantic Entity.");
		AP.executeSnepslogCommand("define-semantic Action.");
		AP.executeSnepslogCommand("define-relation state Proposition.");
		AP.executeSnepslogCommand("define-relation agent Proposition.");
		AP.executeSnepslogCommand("define-frame here Proposition (state agent).");
		AP.executeSnepslogCommand("define-frame say Act (action obj).");
		AP.executeSnepslogCommand("whendo(here(John), say(Hi:Entity)).");
		Node n = Network.getNode("M3");
		Molecular m = (Molecular) n.getTerm();
		assertTrue(m.getDownCableSet().size() == 2);
		assertTrue(m.getDownCableSet().getDownCable("when").getNodeSet().size() == 1);
		assertTrue(m.getDownCableSet().getDownCable("do").getNodeSet().size() == 1);
		Node when = m.getDownCableSet().getDownCable("when").getNodeSet().getNode(0);
		Node doo = m.getDownCableSet().getDownCable("do").getNodeSet().getNode(0);
		boolean success1 = false;
		boolean success2 = false;
		if (when.getTerm() instanceof Molecular) {
			Molecular mwhen = (Molecular) when.getTerm();
			LinkedList<Relation> relswhen = mwhen.getDownCableSet().getCaseFrame().getRelations();
			if (relswhen.size() == 2 && relswhen.get(0).getName().equals("state") && relswhen.get(1).getName().equals("agent")) {
				Node here = Network.getNode("here");
				Node John = Network.getNode("John");
				if (mwhen.getDownCableSet().getDownCable("state").getNodeSet().contains(here)
						&& mwhen.getDownCableSet().getDownCable("agent").getNodeSet().contains(John)) {
					success1 = true;
				}
			}
		}
		if (doo.getTerm() instanceof Molecular) {
			Molecular mdo = (Molecular) doo.getTerm();
			LinkedList<Relation> relsdo = mdo.getDownCableSet().getCaseFrame().getRelations();
			if (relsdo.size() == 2 && relsdo.get(0).getName().equals("action") && relsdo.get(1).getName().equals("obj")) {
				Node say = Network.getNode("say");
				Node Hi = Network.getNode("Hi");
				if (mdo.getDownCableSet().getDownCable("action").getNodeSet().contains(say)
						&& mdo.getDownCableSet().getDownCable("obj").getNodeSet().contains(Hi)) {
					success2 = true;
				}
			}
		}

		if (!(success1 && success2)) {
			fail("failure to build this SNeRETerm!}");
		}
	}
	
}
