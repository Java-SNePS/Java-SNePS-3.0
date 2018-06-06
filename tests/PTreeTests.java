import java.lang.reflect.Field;

import org.junit.Before;
import org.junit.After;
import org.junit.Test;

import sneps.network.Network;
import sneps.network.Node;
import sneps.network.VariableNode;
import sneps.network.classes.Semantic;
import sneps.network.classes.term.Variable;
import sneps.setClasses.FlagNodeSet;
import sneps.setClasses.NodeSet;
import sneps.setClasses.PropositionSet;
import sneps.setClasses.RuleUseInfoSet;
import sneps.snip.classes.FlagNode;
import sneps.snip.classes.PTree;
import sneps.snip.classes.PTree.PSubTree;
import sneps.snip.classes.PTree.PTreeNode;
import sneps.snip.classes.RuisHandler;
import sneps.snip.classes.RuleUseInfo;
import sneps.snip.matching.LinearSubstitutions;
import junit.framework.TestCase;

public class PTreeTests extends TestCase {
	private static NodeSet ants;
	private static PTree tree;
	private static RuleUseInfo rui;

	@Before
	protected void setUpBeforeClass() throws Exception {
		super.setUp();

		ants = new NodeSet();
		VariableNode var = new VariableNode(new Variable("X"));
		Node fido = Network.buildBaseNode("Fido", new Semantic("Member"));
		Node dog = Network.buildBaseNode("Dog", new Semantic("Class"));
		ants.addNode(fido);		ants.addNode(dog);
		
		LinearSubstitutions sub = new LinearSubstitutions();
		FlagNodeSet fns = new FlagNodeSet();
		PropositionSet supports = new PropositionSet();
		FlagNode fn;

		supports.add(var.getId());
		fn = new FlagNode(var, supports, 1);
		fns.insert(fn);

		supports.clearSet();
		supports.add(dog.getId());
		fn = new FlagNode(dog, supports, 1);
		fns.insert(fn);

		rui = new RuleUseInfo(sub, 1, 0, fns);
		tree = new PTree("default");
	}

	@Test
	public void testInsertRUI() {
		tree.buildTree(ants);
		RuleUseInfoSet ruiSet = new RuleUseInfoSet();

		tree.insertRUI(rui);
		for(PSubTree subTree : tree.getSubTrees()){
			assertNotNull(
					"PTree: PSubTree RootRUIS cannot be null",
					subTree.getRootRUIS());
			ruiSet.addAll(subTree.getRootRUIS());
		}
		assertTrue(
				"PTree: PSubTree getRootRUI doesn't contain created RUI",
				ruiSet.contains(rui));
	}

	@Test
	public void testPTree() {
		Class<PTree> aClass = PTree.class;
		boolean thrown = false;
		try {
			aClass.getConstructor(new Class[] {String.class} );
		} catch (Exception e) {
			thrown = true;
		}
		assertFalse(
				"Missing constructor with String parameter in PTree class.",
				thrown);

		assertEquals(
				"PTree class should extend RuisHandler class",
				RuisHandler.class,
				PTree.class.getSuperclass());

		PTree e = tree;
		Field f;
		try {
			f = e.getClass().getDeclaredField("positiveNodes");

			f.setAccessible(true);	f.set(e, new NodeSet());

			assertNotNull(
					"The constructor of PTree class should initialize inherited variables correctly by calling super.",
					f);
		} catch(Exception x) {
			assertNull(x.getMessage(), x);
		}
	}

	@Test
	public void testBuildTree() {
		tree.buildTree(ants);
		assertNotNull("PTree: Built Sub Trees is null",
				tree.getSubTrees());
		assertNotNull("PTree: Built Sub Trees Map is null",
				tree.getSubTreesMap());
		assertEquals("PTree: Built Sub Trees is empty",
				false, tree.getSubTrees().isEmpty());
		assertEquals("PTree: Built Sub Trees Map is empty",
				false, tree.getSubTreesMap().isEmpty());
	}

	@After
	public void tearDownAfterClass(){
		Network.clearNetwork();
		ants.clear();
		tree = null;
		rui = null;
	}

	//PSubTree
	@Test
	public void testPSubTreeInsert(){
		tree.buildTree(ants);
		for(PSubTree subTree : tree.getSubTrees()){
			subTree.insert(rui);
			assertNotNull(
					"PSubTree: PSubTree RootRUIS cannot be null",
					subTree.getRootRUIS());
		}
	}
	@Test
	public void testPSubTreeGetLeafPattern(){
		tree.buildTree(ants);
		tree.insertRUI(rui);
		for(PSubTree subTree : tree.getSubTrees()){
			assertNotNull(
					"PSubTree: PSubTree RootRUIS cannot be null",
					subTree.getRootRUIS());
			PTreeNode node = subTree.getLeafPattern(1, subTree.getRoot());
			assertNotNull(
					"PSubTree: PSubTree GetLeafPattern cannot return null",
					node);
		}
	}

	
	//PTreeNode
	@Test
	public void testPTreeNodeInsertRUI(){
		tree.buildTree(ants);
	}
	
	@Test
	public void testPTreeNodeInsertIntoTree(){
		tree.buildTree(ants);
	}

}
