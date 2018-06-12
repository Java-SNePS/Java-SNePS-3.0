import java.util.HashSet;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import sneps.exceptions.DuplicateContextNameException;
import sneps.exceptions.DuplicatePropositionException;
import sneps.exceptions.IllegalIdentifierException;
import sneps.exceptions.NodeNotFoundInNetworkException;
import sneps.exceptions.NotAPropositionNodeException;
import sneps.network.Network;
import sneps.network.Node;
import sneps.network.VariableNode;
import sneps.network.classes.Semantic;
import sneps.network.classes.term.Base;
import sneps.setClasses.FlagNodeSet;
import sneps.setClasses.NodeSet;
import sneps.setClasses.PropositionSet;
import sneps.setClasses.RuleUseInfoSet;
import sneps.snebr.Context;
import sneps.snebr.Controller;
import sneps.snip.classes.FlagNode;
import sneps.snip.classes.PTree;
import sneps.snip.classes.PTree.PSubTree;
import sneps.snip.classes.RuleUseInfo;
import sneps.snip.matching.Binding;
import sneps.snip.matching.LinearSubstitutions;
import junit.framework.TestCase;

public class PTreeTests extends TestCase {
	private static Context context;
	private static String contextName = "TempContext";
	private static NodeSet ants;
	private static PTree tree;
	private static RuleUseInfo rui;

	@Before
	public void setUp(){
		try {
			Controller.createContext("default");
		} catch (DuplicateContextNameException e1) {
			assertNotNull(e1.getMessage(), e1);
		}

		ants = new NodeSet();
		try {
			context = Controller.createContext(contextName);
		} catch (DuplicateContextNameException e1) {
			assertNotNull(e1.getMessage(), e1);
		}
		VariableNode var;
		Node fido,dog;
 		try {
			var = Network.buildVariableNode("X");
			fido = Network.buildBaseNode("Fido", new Semantic("Member"));
			dog = Network.buildBaseNode("Dog", new Semantic("Class"));
			ants.addNode(var);		ants.addNode(fido);		ants.addNode(dog);
		} catch (IllegalIdentifierException | NotAPropositionNodeException 
				| NodeNotFoundInNetworkException e) {
			assertNotNull(e.getMessage(), e);
			var = new VariableNode();
			fido = new Node(new Base("Fido"));
			dog = new Node(new Base("Dog"));
			ants.addNode(fido);		ants.addNode(var);		ants.addNode(dog);
		}
		
		LinearSubstitutions sub = new LinearSubstitutions();
		FlagNodeSet fns = new FlagNodeSet();
		PropositionSet supports = new PropositionSet();
		FlagNode fn;

		try {
			supports.add(var.getId());
		} catch (DuplicatePropositionException | NotAPropositionNodeException
				| NodeNotFoundInNetworkException e) {
			assertNotNull(e.getMessage(), e);
		}
		fn = new FlagNode(var, supports, 1);
		fns.insert(fn);

		supports.clearSet();
		try {
			supports.add(dog.getId());
		} catch (DuplicatePropositionException | NotAPropositionNodeException
				| NodeNotFoundInNetworkException e) {
			assertNotNull(e.getMessage(), e);
		}
		fn = new FlagNode(dog, supports, 1);
		fns.insert(fn);
		sub.insert(new Binding(var, dog));
		
		supports.clearSet();
		try {
			supports.add(fido.getId());
		} catch (DuplicatePropositionException | NotAPropositionNodeException
				| NodeNotFoundInNetworkException e) {
			assertNotNull(e.getMessage(), e);
		}
		fn = new FlagNode(fido, supports, 1);
		fns.insert(fn);

		rui = new RuleUseInfo(sub, 1, 0, fns);
		tree = new PTree();
	}

	@Test
	public void testInsertRUI() {
		tree.buildTree(ants);
		RuleUseInfoSet ruiSet = new RuleUseInfoSet();

		tree.insertRUI(rui);
		HashSet<PSubTree> set = tree.getSubTrees();
		assertNotNull(
				"PTree: PSubTrees cannot be null",
				set);

		for(PSubTree subTree : set)
			ruiSet.addAll(subTree.getRootRUIS());

		assertFalse(
				"PTree: PSubTree getRootRUIS cannot return empty",
				ruiSet.isEmpty());
		assertTrue(
				"PTree: PSubTree getRootRUI doesn't contain created RUI",
				ruiSet.contains(rui));
	}

	@Test
	public void testBuildTree() {
		tree.buildTree(ants);
		assertNotNull("PTree: Built Sub Trees is null",
				tree.getSubTrees());
		assertNotNull("PTree: Built Sub Trees Map is null",
				tree.getSubTreesMap());
/*		assertEquals("PTree: Built Sub Trees is empty",
				false, tree.getSubTrees().isEmpty());
		assertEquals("PTree: Built Sub Trees Map is empty",
				false, tree.getSubTreesMap().isEmpty());*/
	}

	@After
	public void tearDown(){
		Network.clearNetwork();
		ants.clear();
		tree = null;
		rui = null;
	}

}
