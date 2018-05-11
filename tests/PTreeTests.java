import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.After;
import org.junit.Test;

import sneps.network.Network;
import sneps.network.Node;
import sneps.network.VariableNode;
import sneps.network.classes.Semantic;
import sneps.setClasses.FlagNodeSet;
import sneps.setClasses.NodeSet;
import sneps.setClasses.RuleUseInfoSet;
import sneps.snebr.Support;
import sneps.snip.classes.FlagNode;
import sneps.snip.classes.PTree;
import sneps.snip.classes.PTree.PSubTree;
import sneps.snip.classes.RuisHandler;
import sneps.snip.classes.RuleUseInfo;
import sneps.snip.matching.LinearSubstitutions;
import junit.framework.TestCase;

public class PTreeTests extends TestCase {
	private NodeSet ants;
	private RuleUseInfo rui;
	private PTree tree;

	@Before
	protected void setUpBeforeClass() throws Exception {
		super.setUp();
		
		VariableNode n1 = (VariableNode) Network.buildVariableNode(new Semantic("member"));
		Node n2 = Network.buildBaseNode("X", new Semantic("class"));
		ants.addNode(n1);	ants.addNode(n2);
		
		LinearSubstitutions sub = new LinearSubstitutions();
		FlagNodeSet fns = new FlagNodeSet();
		Set<Support> support = new HashSet<Support>();
		support.add(new Support(0));
		FlagNode fn = new FlagNode(n1, support, 1);
		fns.putIn(fn);
		fn = new FlagNode(n2, support, 1);
		fns.putIn(fn);
		rui = new RuleUseInfo(sub, 1, 0, fns);

		tree = new PTree("default");
	}

	@Test
	public void testInsertRUI() {
		tree.insertRUI(rui);
		RuleUseInfoSet RuiSet = new RuleUseInfoSet();
		for(PSubTree subTree : tree.getSubTrees()){
			assertNotNull("PTree: PSubTree RootRUIS cannot be null", subTree.getRootRUIS());
			RuiSet.addAll(subTree.getRootRUIS());
		}
		assertTrue("PTree: PSubTree getRootRUI doesn't contain created RUI", RuiSet.contains(rui));
	}

	@Test
	public void testPTree() {
		Class<PTree> aClass = PTree.class;
		boolean thrown = false;
		try {
			aClass.getConstructor(new Class[] {
					String.class });
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

			f.setAccessible(true);
			f.set(e, new NodeSet());

			assertNotNull(
					"The constructor of PTree class should initialize inherited variables correctly by calling super.",
					f);
		} catch(Exception x){
			assertNull(x.getMessage(), x);
		}
	}

	@Test
	public void testBuildTree() {
		tree.buildTree(ants);
		assertNotNull("PTree: Built Sub Trees is null", tree.getSubTrees());
		assertNotNull("PTree: Built Sub Trees Map is null", tree.getSubTreesMap());
		assertEquals("PTree: Built Sub Trees is empty", false, tree.getSubTrees().isEmpty());
		assertEquals("PTree: Built Sub Trees Map is empty", false, tree.getSubTreesMap().isEmpty());
	}

	@After
	public void tearDownAfterClass(){
		Network.clearNetwork();
	}
}
