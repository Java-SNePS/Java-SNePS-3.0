package tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import sneps.exceptions.CannotBuildNodeException;
import sneps.exceptions.CannotInsertJustificationSupportException;
import sneps.exceptions.ContextNameDoesntExistException;
import sneps.exceptions.ContradictionFoundException;
import sneps.exceptions.CustomException;
import sneps.exceptions.DuplicateContextNameException;
import sneps.exceptions.DuplicatePropositionException;
import sneps.exceptions.EquivalentNodeException;
import sneps.exceptions.IllegalIdentifierException;
import sneps.exceptions.NodeNotFoundInNetworkException;
import sneps.exceptions.NodeNotFoundInPropSetException;
import sneps.exceptions.NotAPropositionNodeException;
import sneps.network.Network;
import sneps.network.Node;
import sneps.network.PropositionNode;
import sneps.network.classes.Relation;
import sneps.network.classes.RelationsRestrictedCaseFrame;
import sneps.network.classes.Semantic;
import sneps.network.classes.Wire;
import sneps.network.classes.setClasses.PropositionSet;
import sneps.snebr.Context;
import sneps.snebr.Controller;

public class GradedPropsTest {
	private static final String testContextName = "Test context";

	@BeforeClass
	public static void setUp() throws IllegalIdentifierException, DuplicateContextNameException, NotAPropositionNodeException, NodeNotFoundInNetworkException {
		Controller.clearSNeBR();
		Network.clearNetwork();
	}

	@Before
	public void beforeEach() throws DuplicateContextNameException, ContradictionFoundException, NodeNotFoundInNetworkException, NotAPropositionNodeException, IllegalIdentifierException {
		Network.defineDefaults();
		for (int i = 0; i < 8889; i++)
			Network.buildBaseNode("n" + i, Semantic.proposition);
		((PropositionNode) Network.getNodeById(0)).setHyp(true);
		((PropositionNode) Network.getNodeById(1)).setHyp(true);
		((PropositionNode) Network.getNodeById(2)).setHyp(true);
		((PropositionNode) Network.getNodeById(3)).setHyp(true);
		((PropositionNode) Network.getNodeById(4)).setHyp(true);
		((PropositionNode) Network.getNodeById(5)).setHyp(true);
		Controller.createContext(testContextName);
	}

	@After
	public void afterEach() {
		Controller.clearSNeBR();
		Network.clearNetwork();
	}


	@AfterClass
	public static void tearDown() {
		Network.clearNetwork();
	}

	@Test
	public void addTelescopedPropToContext() throws NotAPropositionNodeException,
	NodeNotFoundInNetworkException, DuplicatePropositionException, 
	ContradictionFoundException, NodeNotFoundInPropSetException, ContextNameDoesntExistException {
		Context cxt = Controller.getContextByName(testContextName);
		ArrayList<PropositionSet> t1 = cxt.getTelescopedSet();
		int length = 0;
		if (t1.size() > 0)
			length = PropositionSet.getPropsSafely(t1.get(0)).length;
		Context c = Controller.addTelescopedPropToContext(testContextName, 6, 1); // level 1
		Controller.addTelescopedPropToContext(testContextName, 5, 1); // level 1
		Context c2 = Controller.addTelescopedPropToContext(testContextName, 4, 2); // level 2


		assertEquals(c, Controller.getContextByName(testContextName));
		int[] props = PropositionSet.getPropsSafely(c2.getTelescopedSet().get(0));

		System.out.println("addTelescopedPropToContext Test: ");
		System.out.println("Telescoped prop set in context: " + cxt.getTelescopedSet());
		System.out.println("----------------------------------------------------------");

		assertEquals(props.length, length + 2);

		boolean found = false;
		for (int i = 0; i < props.length; i++) {
			if (found && props[i] == 6)
				fail("multiple insertion!");
			else if (props[i] == 6)
				found = true;
		}

		if (!found)
			fail("not inserted!");

	}

	public PropositionNode[] setupGradingProp(int prop, String gradeS) throws NodeNotFoundInNetworkException,
	NotAPropositionNodeException, IllegalIdentifierException, 
	CannotBuildNodeException, EquivalentNodeException {

		PropositionNode[] result = new PropositionNode[2];

		PropositionNode gradedProp = (PropositionNode) Network.getNodeById(prop);

		Node grade = Network.buildBaseNode(gradeS, Semantic.individual);

		ArrayList<Wire> wires = new ArrayList<>();
		wires.add(new Wire(Relation.prop, gradedProp));
		wires.add(new Wire(Relation.grade, grade));

		PropositionNode gradingProp = (PropositionNode) Network.buildMolecularNode(wires, RelationsRestrictedCaseFrame.gradedProp);
		result[0] = gradedProp;
		result[1] = gradingProp;
		return result;
	} 

	//Adding two supports from two different parent nodes

	@Test
	public void addTelescopedSupport() throws NodeNotFoundInNetworkException, 
	NotAPropositionNodeException, IllegalIdentifierException, CannotBuildNodeException, 
	EquivalentNodeException, NodeNotFoundInPropSetException, DuplicatePropositionException, 
	CannotInsertJustificationSupportException, ContradictionFoundException, 
	ContextNameDoesntExistException  {

		Context cxt = Controller.getContextByName(testContextName);
		PropositionNode[] result = setupGradingProp(6, "2");
		PropositionNode gradingProp = result[1];
		PropositionNode gradedProp = result[0];

		int[] pqr = new int[3];
		pqr[0] = 1;
		pqr[1] = 2;
		pqr[2] = 3;

		int[] mn = new int[3];
		mn[0] = 5;
		mn[1] = 4;
		mn[2] = 0;


		PropositionSet s1 = new PropositionSet(pqr);
		PropositionSet s2 = new PropositionSet(mn);

		gradingProp.addJustificationBasedSupport(s1);

		gradingProp.addJustificationBasedSupport(s2);

		int size = gradedProp.getTelescopedSupport().size();

		gradedProp.addTelescopedSupport(testContextName, gradingProp);

		assertEquals(gradedProp.getTelescopedSupport().size(), size + 2);

		result = setupGradingProp(6, "4");
		gradingProp = result[1];
		gradedProp = result[0];


		int[] pqr2 = new int[2];
		pqr2[0] = 1;
		pqr2[1] = 2;

		int[] mn2 = new int[2];
		mn2[0] = 5;
		mn2[1] = 4;


		PropositionSet s12 = new PropositionSet(pqr2);
		PropositionSet s22 = new PropositionSet(mn2);


		gradingProp.addJustificationBasedSupport(s12);

		gradingProp.addJustificationBasedSupport(s22);

		gradedProp.addTelescopedSupport(testContextName, gradingProp);

		System.out.println("addTelescopedSupport Test: ");

		System.out.println("Telescoped prop set in context: " + cxt.getTelescopedSet());

		System.out.println("Support of grading prop: " + gradingProp);

		System.out.println("Graded prop Support: " + gradedProp);

		System.out.println("Grading chains of graded prop: " + gradedProp.getGrades());

		System.out.println("----------------------------------------------------------");

		if (gradedProp.getGrades().size()!=4)
			fail("not inserted!");
	}

	//Adding support to a node with telescoped parent node
	@Test
	public void addTelescopedSupport2() throws NodeNotFoundInNetworkException, 
	NotAPropositionNodeException, IllegalIdentifierException, CannotBuildNodeException, 
	EquivalentNodeException, NodeNotFoundInPropSetException, DuplicatePropositionException, 
	CannotInsertJustificationSupportException, ContradictionFoundException, 
	ContextNameDoesntExistException  {

		PropositionNode gradedChild = (PropositionNode) Network.getNodeById(20);

		Node grade = Network.buildBaseNode("5", Semantic.individual);

		ArrayList<Wire> wires = new ArrayList<>();
		wires.add(new Wire(Relation.prop, gradedChild));
		wires.add(new Wire(Relation.grade, grade));

		PropositionNode gradingChild = (PropositionNode) Network.buildMolecularNode(wires, RelationsRestrictedCaseFrame.gradedProp);


		Context cxt = Controller.getContextByName(testContextName);
		PropositionNode[] result = setupGradingProp(gradingChild.getId(), "2");
		PropositionNode gradingParent = result[1];
		PropositionNode gradedParent = result[0];

		int[] pqr = new int[3];
		pqr[0] = 1;
		pqr[1] = 2;
		pqr[2] = 3;

		int[] mn = new int[3];
		mn[0] = 5;
		mn[1] = 4;
		mn[2] = 0;


		PropositionSet s1 = new PropositionSet(pqr);
		PropositionSet s2 = new PropositionSet(mn);

		gradingParent.addJustificationBasedSupport(s1);

		gradingParent.addJustificationBasedSupport(s2);

		gradedParent.addTelescopedSupport(testContextName, gradingParent);

		gradedChild.addTelescopedSupport(testContextName, gradingChild);

		System.out.println("addTelescopedSupport2 Test: ");

		System.out.println("Telescoped prop set in context: " + cxt.getTelescopedSet());

		System.out.println("Support of grading parent prop: " + gradingParent);

		System.out.println("Graded parent prop Support: " + gradedParent);

		System.out.println("Grading chains of graded parent prop: " + gradedParent.getGrades());

		System.out.println("------------------");

		System.out.println("Support of grading child prop: " + gradingChild);

		System.out.println("Graded child prop Support: " + gradedChild);

		System.out.println("Grading chains of graded child prop: " + gradedChild.getGrades());

		System.out.println("----------------------------------------------------------");

		assertEquals(gradedParent, gradingChild);

	}

	public Context buildTestNetwork() throws NotAPropositionNodeException, 
	NodeNotFoundInNetworkException, IllegalIdentifierException, 
	CannotBuildNodeException, EquivalentNodeException, NodeNotFoundInPropSetException, 
	DuplicatePropositionException, CannotInsertJustificationSupportException, ContradictionFoundException, 
	ContextNameDoesntExistException {
		PropositionNode gradedChild = (PropositionNode) Network.getNodeById(20);

		Node grade = Network.buildBaseNode("5", Semantic.individual);

		ArrayList<Wire> wires = new ArrayList<>();
		wires.add(new Wire(Relation.prop, gradedChild));
		wires.add(new Wire(Relation.grade, grade));

		PropositionNode gradingChild = (PropositionNode) Network.buildMolecularNode(wires, RelationsRestrictedCaseFrame.gradedProp);

		PropositionNode[] result = setupGradingProp(gradingChild.getId(), "2");
		PropositionNode gradingParent = result[1];
		PropositionNode gradedParent = result[0];

		int[] pqr = new int[3];
		pqr[0] = 1;
		pqr[1] = 2;
		pqr[2] = 3;

		int[] mn = new int[3];
		mn[0] = 5;
		mn[1] = 4;
		mn[2] = 0;


		PropositionSet s1 = new PropositionSet(pqr);
		PropositionSet s2 = new PropositionSet(mn);

		gradingParent.addJustificationBasedSupport(s1);

		gradingParent.addJustificationBasedSupport(s2);

		gradedParent.addTelescopedSupport(testContextName, gradingParent);

		return gradedChild.addTelescopedSupport(testContextName, gradingChild);
	}

	// Tests if supported at a given level
	@Test
	public void isSupported() throws NotAPropositionNodeException, 
	NodeNotFoundInNetworkException, ContextNameDoesntExistException, 
	CustomException, NodeNotFoundInPropSetException, ContradictionFoundException, 
	DuplicatePropositionException, CannotInsertJustificationSupportException,
	IllegalIdentifierException, CannotBuildNodeException, EquivalentNodeException, DuplicateContextNameException {

		Context cxt = buildTestNetwork();
		Controller.createContext("Context", cxt.getHypothesisSet(), cxt.getTelescopedSet());
		Controller.setCurrentContext("Context");
		PropositionSet p = new PropositionSet(new int[]{0, 4, 5});
		Controller.addPropsToCurrentContext(p);
		System.out.println("isSupported Test: ");
		System.out.println("Hypothesis Set: " + Controller.getCurrentContext().getHypothesisSet());
		System.out.println("Telescoped prop set in context: " + Controller.getCurrentContext().getTelescopedSet());
		System.out.println("----------------------------------------------------------");

		assertTrue(Controller.getCurrentContext().isSupported((PropositionNode) Network.getNodeById(8890), 1));
		assertTrue(Controller.getCurrentContext().isSupported((PropositionNode) Network.getNodeById(20), 2));
		assertTrue(Controller.getCurrentContext().isSupported((PropositionNode) Network.getNodeById(8890), 2));
		assertFalse(Controller.getCurrentContext().isAsserted((PropositionNode) Network.getNodeById(20), 1));

	}

	public PropositionNode setupContradiction(int id, int gradeI) throws NodeNotFoundInNetworkException, NotAPropositionNodeException,
	IllegalIdentifierException, CannotBuildNodeException, EquivalentNodeException, 
	NodeNotFoundInPropSetException, DuplicatePropositionException, 
	CannotInsertJustificationSupportException, ContradictionFoundException, 
	ContextNameDoesntExistException {

		PropositionNode negatedProp = (PropositionNode) Network.getNodeById(id);

		Node zero = Network.buildBaseNode("0", Semantic.infimum);

		ArrayList<Wire> wires = new ArrayList<>();
		wires.add(new Wire(Relation.arg, negatedProp));
		wires.add(new Wire(Relation.max, zero));
		wires.add(new Wire(Relation.min, zero));

		PropositionNode negatingNode = (PropositionNode) Network.buildMolecularNode(wires, RelationsRestrictedCaseFrame.andOrRule);

		if(gradeI!=0) {
			PropositionNode graded = (PropositionNode) Network.getNodeById(negatingNode.getId());

			Node grade = Network.buildBaseNode(""+gradeI, Semantic.individual);

			wires = new ArrayList<>();
			wires.add(new Wire(Relation.prop, graded));
			wires.add(new Wire(Relation.grade, grade));

			PropositionNode grading = (PropositionNode) Network.buildMolecularNode(wires, RelationsRestrictedCaseFrame.gradedProp);

			int[] pqr = new int[3];
			pqr[0] = 1;
			pqr[1] = 2;
			pqr[2] = 3;

			int[] mn = new int[3];
			mn[0] = 5;
			mn[1] = 4;
			mn[2] = 0;


			PropositionSet s1 = new PropositionSet(pqr);
			PropositionSet s2 = new PropositionSet(mn);

			grading.addJustificationBasedSupport(s1);

			grading.addJustificationBasedSupport(s2);

			graded.addTelescopedSupport("Context", grading);

		}

		return negatingNode;
	}

	//Adding a hyp conflicting with a telescoped prop
	@Test
	public void addConflictingHypToContext() throws ContradictionFoundException, 
	ContextNameDoesntExistException, NotAPropositionNodeException, NodeNotFoundInNetworkException,
	DuplicateContextNameException, IllegalIdentifierException, CannotBuildNodeException, 
	EquivalentNodeException, NodeNotFoundInPropSetException, DuplicatePropositionException, 
	CannotInsertJustificationSupportException {
		Context cxt = buildTestNetwork();
		Controller.createContext("Context", cxt.getHypothesisSet(), cxt.getTelescopedSet());
		Controller.setCurrentContext("Context");
		PropositionSet p = new PropositionSet(new int[]{0, 4, 5});
		Controller.addPropsToCurrentContext(p);
		assertTrue(Controller.getCurrentContext().isSupported((PropositionNode) Network.getNodeById(20), 2));
		System.out.println("Adding a hyp conflicting with a telescoped prop: ");
		System.out.println("Telscoped set before conflict: " + Controller.getCurrentContext().getTelescopedSet());
		System.out.println("Hypothesis set before conflict:" + Controller.getCurrentContext().getHypothesisSet());
		Controller.addPropToCurrentContext(setupContradiction(20, 0).getId());
		System.out.println("Telscoped set after conflict:" + Controller.getCurrentContext().getTelescopedSet());
		System.out.println("Hypothesis set after conflict:" + Controller.getCurrentContext().getHypothesisSet());	
		System.out.println("----------------------------------------------------------");
		assertFalse(Controller.getCurrentContext().isSupported((PropositionNode) Network.getNodeById(20), 2));
	}

	//Adding two conflicting graded propositions with same grade
	@Test
	public void addConflictingHypToContext2() throws ContradictionFoundException, 
	ContextNameDoesntExistException, NotAPropositionNodeException, NodeNotFoundInNetworkException,
	DuplicateContextNameException, IllegalIdentifierException, CannotBuildNodeException, 
	EquivalentNodeException, NodeNotFoundInPropSetException, DuplicatePropositionException, 
	CannotInsertJustificationSupportException {
		Context cxt = buildTestNetwork();
		Controller.createContext("Context", cxt.getHypothesisSet(), cxt.getTelescopedSet());
		Controller.setCurrentContext("Context");
		PropositionSet p = new PropositionSet(new int[]{0, 4, 5});
		Controller.addPropsToCurrentContext(p);
		System.out.println("Adding two conflicting graded propositions with same grade: ");
		assertTrue(Controller.getCurrentContext().isSupported((PropositionNode) Network.getNodeById(20), 2));
		System.out.println("Telscoped set before conflict: " + Controller.getCurrentContext().getTelescopedSet());
		System.out.println("Hypothesis set before conflict:" + Controller.getCurrentContext().getHypothesisSet());
		PropositionNode negated = setupContradiction(20,5);
		System.out.println("Telscoped set after conflict:" + Controller.getCurrentContext().getTelescopedSet());
		System.out.println("Hypothesis set after conflict:" + Controller.getCurrentContext().getHypothesisSet());	
		System.out.println("----------------------------------------------------------");
		assertFalse(Controller.getCurrentContext().isSupported((PropositionNode) Network.getNodeById(20), 2));
		assertFalse(Controller.getCurrentContext().isSupported((PropositionNode) Network.getNodeById(negated.getId()), 1));
	}

	//Adding two conflicting graded propositions with larger grade
	@Test
	public void addConflictingHypToContext3() throws ContradictionFoundException, 
	ContextNameDoesntExistException, NotAPropositionNodeException, NodeNotFoundInNetworkException,
	DuplicateContextNameException, IllegalIdentifierException, CannotBuildNodeException, 
	EquivalentNodeException, NodeNotFoundInPropSetException, DuplicatePropositionException, 
	CannotInsertJustificationSupportException {
		Context cxt = buildTestNetwork();
		Controller.createContext("Context", cxt.getHypothesisSet(), cxt.getTelescopedSet());
		Controller.setCurrentContext("Context");
		PropositionSet p = new PropositionSet(new int[]{0, 4, 5});
		Controller.addPropsToCurrentContext(p);
		System.out.println("Adding two conflicting graded propositions with larger grade: ");
		assertTrue(Controller.getCurrentContext().isSupported((PropositionNode) Network.getNodeById(20), 2));
		System.out.println("Telscoped set before conflict: " + Controller.getCurrentContext().getTelescopedSet());
		System.out.println("Hypothesis set before conflict:" + Controller.getCurrentContext().getHypothesisSet());
		PropositionNode negated = setupContradiction(20,8);
		System.out.println("Telscoped set after conflict:" + Controller.getCurrentContext().getTelescopedSet());
		System.out.println("Hypothesis set after conflict:" + Controller.getCurrentContext().getHypothesisSet());	
		System.out.println("----------------------------------------------------------");
		assertFalse(Controller.getCurrentContext().isSupported((PropositionNode) Network.getNodeById(20), 2));
		assertTrue(Controller.getCurrentContext().isSupported((PropositionNode) Network.getNodeById(negated.getId()), 1));
	}

	//Adding two conflicting graded propositions with smaller grade
	@Test
	public void addConflictingHypToContext4() throws ContradictionFoundException, 
	ContextNameDoesntExistException, NotAPropositionNodeException, NodeNotFoundInNetworkException,
	DuplicateContextNameException, IllegalIdentifierException, CannotBuildNodeException, 
	EquivalentNodeException, NodeNotFoundInPropSetException, DuplicatePropositionException, 
	CannotInsertJustificationSupportException {
		Context cxt = buildTestNetwork();
		Controller.createContext("Context", cxt.getHypothesisSet(), cxt.getTelescopedSet());
		Controller.setCurrentContext("Context");
		PropositionSet p = new PropositionSet(new int[]{0, 4, 5});
		Controller.addPropsToCurrentContext(p);
		System.out.println("Adding two conflicting graded propositions with smaller grade: ");
		assertTrue(Controller.getCurrentContext().isSupported((PropositionNode) Network.getNodeById(20), 2));
		System.out.println("Telscoped set before conflict: " + Controller.getCurrentContext().getTelescopedSet());
		System.out.println("Hypothesis set before conflict:" + Controller.getCurrentContext().getHypothesisSet());
		setupContradiction(20,1);
		System.out.println("Telscoped set after conflict:" + Controller.getCurrentContext().getTelescopedSet());
		System.out.println("Hypothesis set after conflict:" + Controller.getCurrentContext().getHypothesisSet());	
		System.out.println("----------------------------------------------------------");
		assertTrue(Controller.getCurrentContext().isSupported((PropositionNode) Network.getNodeById(20), 2));
	}
}
