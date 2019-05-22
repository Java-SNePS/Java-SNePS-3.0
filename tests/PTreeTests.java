import java.util.ArrayList;

import java.util.LinkedList;
import java.util.Queue;

import org.junit.After;
import org.junit.Before;
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
import sneps.network.classes.setClasses.RuleUseInfoSet;
import sneps.snebr.Controller;
import sneps.snip.InferenceTypes;
import sneps.snip.classes.FlagNode;
import sneps.snip.classes.PTree;
import sneps.snip.classes.PTree.PSubTree;
import sneps.snip.classes.PTree.PTreeNode;
import sneps.snip.classes.RuleUseInfo;
import sneps.snip.matching.Binding;
import sneps.snip.matching.LinearSubstitutions;
import junit.framework.TestCase;

public class PTreeTests extends TestCase {
	private static NodeSet ants;
	Node prop1, prop2, prop3;
	private static PTree tree;
	private static RuleUseInfo rui, rui1, rui2;

	@Before
	public void setUp() {
		try {
			Controller.createContext("default");
		} catch (DuplicateContextNameException e1) {
			assertNotNull(e1.getMessage(), e1);
		}

		ants = new NodeSet();
		VariableNode var1, var2;
		Node man, woman, married;
		Node john, mary;
		ArrayList<Wire> wires = new ArrayList<Wire>();
		LinkedList<Relation> rels = new LinkedList<Relation>();
		Relation memberRel = Network.defineRelation("Member", "NodeSet");
		Relation classRel = Network.defineRelation("Class", "NodeSet");
		rels.add(memberRel);	rels.add(classRel);
		CaseFrame caseFrameMC = Network.defineCaseFrame("MemberClass", rels);
		Wire wire1 = null, wire2 = null, wire3 = null, wire4 = null, wire5 = null;
		rels.clear();
		
 		try {
			var1 = Network.buildVariableNode("X");
			var2 = Network.buildVariableNode("Y");
			
			man = Network.buildBaseNode("Man", new Semantic("Class"));
			woman = Network.buildBaseNode("Woman", new Semantic("Class"));
			married = Network.buildBaseNode("Married", new Semantic("Class"));
			john = Network.buildBaseNode("John", new Semantic("Class"));
			mary = Network.buildBaseNode("Mary", new Semantic("Class"));
			
			wire1 = new Wire(memberRel, var1);
			wire2 = new Wire(memberRel, var2);
			wire3 = new Wire(classRel, man);
			wire4 = new Wire(classRel, woman);
			wire5 = new Wire(classRel, married);
		} catch (IllegalIdentifierException | NotAPropositionNodeException 
				| NodeNotFoundInNetworkException e) {
			assertNotNull(e.getMessage(), e);
			var1 = new VariableNode(new Variable("X"));
			var2 = new VariableNode(new Variable("Y"));
			man = new Node(new Base("Man"));
			woman = new Node(new Base("Woman"));
			married = new Node(new Base("Married"));
			john = new Node(new Base("John"));
			mary = new Node(new Base("Mary"));
		}
 		
 		try {
			wires.clear();	wires.add(wire1);	wires.add(wire3);
			prop1 = Network.buildMolecularNode(wires, caseFrameMC);

			wires.clear();	wires.add(wire2);	wires.add(wire4);
			prop2 = Network.buildMolecularNode(wires, caseFrameMC);

			wires.clear();	wires.add(wire1);	wires.add(wire2);	wires.add(wire5);
			prop3 = Network.buildMolecularNode(wires, caseFrameMC);
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

		ants.addNode(prop1);
		ants.addNode(prop2);
		ants.addNode(prop3);
		
		LinearSubstitutions sub = new LinearSubstitutions();
		FlagNodeSet fns = new FlagNodeSet();
		PropositionSet supports = new PropositionSet();
		FlagNode fn;
		try {
			supports.add(prop1.getId());
		} catch (DuplicatePropositionException | NotAPropositionNodeException
				| NodeNotFoundInNetworkException e) {
			assertNotNull(e.getMessage(), e);
		}
		fn = new FlagNode(prop1, supports, 1);
		fns.insert(fn);
		sub.putIn(new Binding(var1, john));
		rui = new RuleUseInfo(sub, 1, 0, fns, InferenceTypes.BACKWARD);
        
		LinearSubstitutions sub1 = new LinearSubstitutions();
		FlagNodeSet fns1 = new FlagNodeSet();
		PropositionSet supports1 = new PropositionSet();
		FlagNode fn1;
		try {
			supports.add(prop2.getId());
		} catch (DuplicatePropositionException | NotAPropositionNodeException
				| NodeNotFoundInNetworkException e) {
			assertNotNull(e.getMessage(), e);
		}
		fn1 = new FlagNode(prop2, supports1, 1);
		fns1.insert(fn1);
		sub1.putIn(new Binding(var2, mary));
		rui1 = new RuleUseInfo(sub1, 1, 0, fns1, InferenceTypes.BACKWARD);
		
		LinearSubstitutions sub2 = new LinearSubstitutions();
		FlagNodeSet fns2 = new FlagNodeSet();
		PropositionSet supports2 = new PropositionSet();
		FlagNode fn2;
		try {
			supports.add(prop3.getId());
		} catch (DuplicatePropositionException | NotAPropositionNodeException
				| NodeNotFoundInNetworkException e) {
			assertNotNull(e.getMessage(), e);
		}
		fn2 = new FlagNode(prop3, supports2, 1);
		fns2.insert(fn2);
		sub2.putIn(new Binding(var1, john));
		sub2.putIn(new Binding(var2, mary));
		rui2 = new RuleUseInfo(sub2, 1, 0, fns2, InferenceTypes.BACKWARD);

		tree = new PTree();
	}
	
	@Test
	public void testBuildTree() {
		// Filling Pattern Variables and Variable Patterns
		tree.fillPVandVP(ants);
		assertEquals(3, tree.getPatternVariables().size());
		assertEquals(2, tree.getVariablePatterns().size());
		
		// Computing Pattern sequence
		Queue<PTreeNode> res = tree.getPatternSequence();
		assertEquals(3, res.size());
		
		// Constructing the tree
		tree.constructBottomUp(res);
		assertNotNull("PTree: Built Sub Trees is null",
				tree.getSubTrees());
		assertNotNull("PTree: Built Sub Trees Map is null",
				tree.getSubTreesMap());
		
		assertFalse("PTree: Built Sub Trees is empty", 
				tree.getSubTrees().isEmpty());
		assertEquals(1, tree.getSubTrees().size());
		
		assertFalse("PTree: Built Sub Trees Map is empty",
				tree.getSubTreesMap().isEmpty());
		assertEquals(3, tree.getSubTreesMap().size());
		
		PTreeNode root = tree.getSubTrees().iterator().next().getRoot();
		
		// Root node represents the conjunction of Man(x), Woman(x), Married(x, y)
		assertEquals(3, root.getPats().size());
		assertEquals(2, root.getVars().size());
		
		// Conjunction of Man(x) and Married(x, y)
		assertEquals(2, root.getLeftChild().getPats().size());
		assertEquals(2, root.getLeftChild().getVars().size());
		
		// Man(x) is a leaf node
		assertEquals(1, root.getLeftChild().getLeftChild().getPats().size());
		assertEquals(1, root.getLeftChild().getLeftChild().getVars().size());
		assertEquals(null, root.getLeftChild().getLeftChild().getLeftChild());
		
		// Married(x, y) is a leaf node
		assertEquals(1, root.getLeftChild().getRightChild().getPats().size());
		assertEquals(2, root.getLeftChild().getRightChild().getVars().size());
		assertEquals(null, root.getLeftChild().getRightChild().getLeftChild());
		
		// Woman(x) is a leaf node
		assertEquals(1, root.getRightChild().getPats().size());
		assertEquals(1, root.getRightChild().getVars().size());
	}

	@Test
	public void testInsertRUI() {
		tree.buildTree(ants);
		RuleUseInfoSet ruiSet = new RuleUseInfoSet();

		tree.insertRUI(rui);
		tree.insertRUI(rui1);
		PTreeNode root = tree.getSubTrees().iterator().next().getRoot();
		
		// Since rui is received from the antecedent Man(x), then it should be 
		// inserted in the Man(x) leaf node
		assertTrue("Rui is not inserted in the correct PTree node", 
				root.getLeftChild().getLeftChild().getRUIS().contains(rui));
				
		// Since rui1 is received from the antecedent Woman(x), then it should be 
		// inserted in the Woman(x) leaf node
		assertTrue("Rui is not inserted in the correct PTree node", 
				root.getRightChild().getRUIS().contains(rui1));
		
		ruiSet = root.getRUIS();
		assertTrue(ruiSet.isEmpty());
		
		tree.insertRUI(rui2);
		ruiSet = root.getRUIS();
		assertEquals(1, ruiSet.size());
		// Value of pcount of rui stored at the root should be equal to the num of 
		// antecedents
		assertEquals(3, ruiSet.iterator().next().getPosCount());
	}
	
	/**
	 * PSubTree Tests.
	 */
	
	@Test
	public void testPSubTreeGetLeafPattern() {
		tree.buildTree(ants);
		tree.insertRUI(rui);
		PSubTree subTree = tree.getSubTrees().iterator().next();
		assertNotNull("PSubTree: PSubTree GetLeafPattern cannot return null", 
				subTree.getLeafPattern(prop1.getId(), subTree.getRoot()));
		assertFalse("PSubTree: Leaf pattern node returned from GetLeafPattern should "
				+ "contain the rui inserted", 
				subTree.getLeafPattern(prop1.getId(), subTree.getRoot()).getRUIS().isEmpty());
	}

	@After
	public void tearDown() {
		Network.clearNetwork();
		ants.clear();
		tree = null;
		rui = null;
	}

}
