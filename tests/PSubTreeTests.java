import static org.junit.Assert.*;

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
import sneps.network.classes.setClasses.FlagNodeSet;
import sneps.network.classes.setClasses.NodeSet;
import sneps.network.classes.setClasses.PropositionSet;
import sneps.network.classes.setClasses.RuleUseInfoSet;
import sneps.snebr.Context;
import sneps.snebr.Controller;
import sneps.snip.classes.FlagNode;
import sneps.snip.classes.PTree;
import sneps.snip.classes.RuleUseInfo;
import sneps.snip.classes.PTree.PSubTree;
import sneps.snip.classes.PTree.PTreeNode;
import sneps.snip.matching.Binding;
import sneps.snip.matching.LinearSubstitutions;


public class PSubTreeTests {
	private static Context context;
	private static String contextName = "TempContext";
	private static NodeSet ants;
	private static PTree tree;
	private static RuleUseInfo rui;

	@Before
	public void setUp() {
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
			ants.addNode(var);		ants.addNode(fido);		ants.addNode(dog);
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

		rui = new RuleUseInfo(sub, 1, 0, fns);
		tree = new PTree();
	}

	@After
	public void tearDown(){
		Network.clearNetwork();
		ants.clear();
		tree = null;
		rui = null;
	}

	@Test
	public void testPSubTreeInsert(){
		tree.buildTree(ants);
		tree.insertRUI(rui);
		RuleUseInfoSet ruiSet = new RuleUseInfoSet();
		for(PSubTree subTree : tree.getSubTrees()){
			subTree.insert(rui);
			ruiSet.addAll(subTree.getRootRUIS());
		}
		assertFalse(
				"PSubTree: PSubTree RootRUIS cannot be empty",
				ruiSet.isEmpty());
	}
	@Test
	public void testPSubTreeGetLeafPattern(){
		tree.buildTree(ants);
		tree.insertRUI(rui);
		for(PSubTree subTree : tree.getSubTrees()){
			PTreeNode node = subTree.getLeafPattern(1, subTree.getRoot());
			assertNotNull(
					"PSubTree: PSubTree GetLeafPattern cannot return null",
					node);
		}
	}
}
