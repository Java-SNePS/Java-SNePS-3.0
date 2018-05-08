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
import sneps.snebr.Context;
import sneps.snebr.Support;
import sneps.snip.classes.FlagNode;
import sneps.snip.classes.PTree;
import sneps.snip.classes.PTree.PSubTree;
import sneps.snip.classes.RuleUseInfo;
import sneps.snip.classes.RuleUseInfoSet;
import sneps.snip.matching.LinearSubstitutions;
import junit.framework.TestCase;

public class PTreeTests extends TestCase {
	private NodeSet ants;
	private RuleUseInfo rui;
	private PTree tree;
	private Context context;

	@Before
	protected void setUpBeforeClass() throws Exception {
		super.setUp();
		
		VariableNode n1 = (VariableNode) Network.buildVariableNode(new Semantic("member"));
		Node n2 = Network.buildBaseNode("X", new Semantic("class"));
		ants.addNode(n1);	ants.addNode(n2);
		
		LinearSubstitutions sub = new LinearSubstitutions();
		FlagNodeSet fns = new FlagNodeSet();
		Set<Support> support = new HashSet<Support>();
		support.add(new Support());
		FlagNode fn = new FlagNode(n1, support, 1);
		fns.putIn(fn);
		fn = new FlagNode(n2, support, 1);
		fns.putIn(fn);
		rui = new RuleUseInfo(sub, 1, 0, fns);
		
		context = new Context();
		
		tree = new PTree((String) context.getName());
	}

	@Test
	public void testInsertRUI() {
		tree.insertRUI(rui);
		for(PSubTree subTree : tree.getSubTrees()){
			assertNotNull("PTree.PSubTree RootRUIS cannot be null", subTree.getRootRUIS());
			assertEquals("Created RUI is not the same as PTree.PSubTree getRootRUI", rui, subTree.getRootRUIS());
		}
	}

	@Test
	public void testPTree() {
		
	}

	@Test
	public void testBuildTree() {
		tree.buildTree(ants);
		assertNotNull("PTree buiding: Sub Trees is null", tree.getSubTrees());
		assertNotNull("PTree buiding: Sub Trees Map is null", tree.getSubTreesMap());
		assertEquals("PTree building: Sub Trees is empty", false, tree.getSubTrees().isEmpty());
		assertEquals("PTree building: Sub Trees Map is empty", false, tree.getSubTreesMap().isEmpty());	
	}

	@After
	public void tearDownAfterClass(){
		Network.clearNetwork();
	}
}
